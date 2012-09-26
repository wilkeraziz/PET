#!/usr/bin/perl

use strict;

use Getopt::Long qw/GetOptions/; 
use XML::TreeBuilder;
use HTML::TreeBuilder;

binmode(STDIN, ":utf8");
binmode(STDOUT, ":utf8");
binmode(STDERR, ":utf8");

my $Me = "readper.pl";
my ($JOB, $TASK, $S, $MT, $PE, $HT, $ANNOTATION, $IDEAL, $PREFERABLE, $MAX, $INDICATOR, $REVISION, $ID, $PRODUCER, $STATUS, $TYPE) = qw/job task S MT PE HT annotation ideal preferable max indicator r id producer status type/;
my $ASSESSMENT = "assessment";
my ($help, $per, $tmp, $history);

my ($annotator, $sys, $ep, $flagid);

my $skipChanges = {ht => 1, pe => 3, fix => 3};
my $trackChanges;
my $ter = "/home/tools/ter/tercom-0.7.25/tercom.7.25.jar";
my $encoding = 'utf-8';
$tmp = 'tmp';
my $html;
my @additional;
my @assessments;

$help=1 unless
&GetOptions(
	'per=s' => \$per,
	'tmp=s' => \$tmp,
	'ter=s' => \$ter,
	'annotator=s' => \$annotator,
	'indicator=s' => \@additional,
	'assessment=s' => \@assessments,
	'flagid' => \$flagid,
	'html' => \$html,
	'track-changes' => \$trackChanges,
	'help' => \$help,
);

if ($help or not ($per and $annotator)){
	print STDERR "$Me
	--per <file>         * A PER file
	--annotator <str>    * Annnotator who performed the PEJ
	--tmp <dir>          A temp directory (default: ./tmp)
	--ter <path>         Path to TER jar (default: /home/tools/ter/tercom-0.7.25/tercom.7.25.jar)
	                     Computes HTER for each segment if the tool is available where indicated
	--help               Displays these instructions

	--indicator <id>     Parses additional indicators
	--assessment <id>    Parses assessments
	--html               Parses HTML from text fields

	--track-changes      Gets a history of changes
	--flagid             Outputs the id of the unit in output files

	Example:
		./readper.pl --per your_output.per --annotator your_name
	Output files:
		.source - source sentences in order
		.mt - machine translations (a blank line represents a HT task)
		.pe - human editings (either PE or HT)
		.summary - a table that you can open in a spreadsheet application (first line is a header)

	\n";
	exit 1;
}

# preparing some global values
my $useTER = 0;
if (-f $ter){
	print STDERR "TER version: $ter\n";
	$useTER = 1;
}

my $pername = $per;
$pername =~ s/.+\///;
mkdir $tmp;

# parsing per
my $reader = XML::TreeBuilder->new();
$reader->parse_file($per);

# get job id or set it to PER file name
my ($xmlJob) = $reader->find($JOB);
my $jobid = $xmlJob->attr($ID);
if ($jobid =~ m/^\s*$/){
	$jobid = $per;
	$jobid =~ s/^.+\///;
}
# no spaces
$jobid =~ s/\s+/_/g;

my @xmlTasks = $reader->find($TASK);
open (my $OS, ">:utf8", "$per.source") or die "Could not write src file\n";
open (my $OM, ">:utf8", "$per.mt") or die "Could not write mt file\n";
open (my $OP, ">:utf8", "$per.pe") or die "Could not write pe file\n";
open (my $OSUM, ">:utf8", "$per.summary") or die "Could not write summary file\n";
open (my $OHIS, ">:utf8", "$per.history") or die "Could not write history file\n";
open (my $OOPS, ">:utf8", "$per.operations") or die "Could not write history (operations) file\n";

print $OOPS join("\t", ("#i", "#type", "#elapsed", "#words", "#len", "#in", "#out")), "\n\n";

my $info = {};
my $avghter = {};

# if you want to include information in the summary or change its order this is the place
my @header = qw/id type status producer revisions editing_time assessing_time src_tokens mt_tokens pe_tokens src_len mt_len pe_len keystrokes printable_keystrokes unchanged lc_unchanged secondsPerWord oer-word oer-len oer-time irate drate/;


push(@header, 'hter') if $useTER;
my @outputs;
print $OSUM "#job #annotator #" . join(" #", @header, @additional, @assessments) . "\n";


foreach my $xmlTask (@xmlTasks){
	my $id = $xmlTask->attr($ID);
	my $status = $xmlTask->attr($STATUS);
	my $type = $xmlTask->attr($TYPE);
	my ($src) = $xmlTask->find($S);
	my ($mt) = $xmlTask->find($MT);
	my $producer = $mt->attr($PRODUCER) if $mt;
	my $srctext = sprintf("%s", $src->content_list);
	$srctext = parseHTML($srctext) if $html;
	my $mttext = sprintf("%s", $mt->content_list) if $mt;
	$mttext = parseHTML($mttext) if $html;
	my $revision = getCurrentRevision($type, $xmlTask, $mttext);
	my $petext = $revision->{$PE};
	my ($hter, $badchunks, $chunks) = getHTER($id, $petext, $mttext) if $type eq 'pe';
	#detectShifts($revision->{changes});
	my ($oerWord, $oerLen, $oerTime, $timeByTypeOp) = observedNumberOfEdits($revision->{changes});
	
	my $pelen = getLength($petext);
	my $petokens = getTokens($petext);
	my $output = {
		'id' => $id,
		'type' => $type,
		'status' => $status,
		'src' => $srctext,
		'mt' => $mttext,
		'pe' => $petext,
		'producer' => $producer,
		'hter' => ($type eq 'pe')?sprintf("%.6f", $hter):'-',
		'src_len' => getLength($srctext),
		'mt_len' => getLength($mttext),
		'pe_len' => getLength($petext),
		'src_tokens' => getTokens($srctext),
		'mt_tokens' => getTokens($mttext),
		'pe_tokens' => getTokens($petext),
		'revisions' => $revision->{revisions},
		'keystrokes' => $revision->{"$INDICATOR:keystrokes"},
		'printable_keystrokes' => ($revision->{"$INDICATOR:white-keystyped"} + $revision->{"$INDICATOR:nonwhite-keystyped"}),
		'editing_time' => $revision->{"$INDICATOR:editing"},
		'assessing_time' => $revision->{"$INDICATOR:assessing"},
		'unchanged' => ($revision->{"$INDICATOR:unchanged"} == 1)?'true':'false',
		'lc_unchanged' => (lc($mttext) eq lc($petext))?'true':'false',
		'data_object' => $revision, # this object contains the changes and stuff like that (check getCurrentRevision)
		'secondsPerWord' => ($petokens)?sprintf("%.6f", $revision->{"$INDICATOR:editing"}/$petokens):"",
		'oer-word' => ($petokens)?sprintf("%6f", $oerWord/$petokens):"",
		'oer-len' => ($pelen)?sprintf("%6f", $oerLen/$pelen):"",
		'oer-time' => ($petokens)?sprintf("%6f", $oerTime/$petokens):"",
		'irate' => ($revision->{"$INDICATOR:editing"})?sprintf("%.6f", $timeByTypeOp->{insertion}/$revision->{"$INDICATOR:editing"}) : "",
		'drate' => ($revision->{"$INDICATOR:editing"})?sprintf("%.6f", $timeByTypeOp->{deletion}/$revision->{"$INDICATOR:editing"}) : "",
	};
	foreach my $indicator (@additional){
		$output->{$indicator} = $revision->{"$INDICATOR:$indicator"};
	}
	foreach my $assessment (@assessments){
		my $value = $revision->{$ASSESSMENT}->{$assessment};
		$output->{$assessment} = $value;
	}
	
	# one could store here the current output in order
	# push(@outputs, $output); 

	my $prefix = "";
	$prefix = "$id: " if $flagid;
	my $prefixmt = "";
	$prefixmt = "$id.$producer: " if $flagid;
	my $prefixpe = "";
	$prefixpe = "$id.$producer.$annotator: " if $flagid;
	printHistory("$id: ", $revision->{transformations}, $OHIS);
	printOperations("$jobid $annotator $id $type $status $producer", $revision->{changes}, $OOPS);
	print $OS "$prefix$srctext\n";
	print $OM "$prefixmt$mttext\n";
	$petext =~ s/\n/\t/g;
	print $OP "$prefixpe$petext\n";
	my @ordered_output;
	foreach my $key (@header,@additional,@assessments){
		my $value = $output->{$key};
		$value = '-' if $value =~ m/^\s*$/;
		push(@ordered_output, $value);
	}
	print $OSUM "$jobid $annotator " . join(' ', @ordered_output) . "\n";

	if ($useTER and $type eq 'pe'){
		$avghter->{chunks} += $chunks;
		$avghter->{bad} += $badchunks;
		$avghter->{samples}++;
	}
	$info->{acc_editing} += $revision->{"$INDICATOR:editing"};
	$info->{acc_assessing} += $revision->{"$INDICATOR:assessing"};
	$info->{tasks}++;
	$info->{done}++ if $status eq 'FINISHED';
	$info->{$type}++;
}
close $OS;
close $OM;
close $OP;
close $OSUM;

$avghter->{hter} = $avghter->{bad}/$avghter->{chunks} if $useTER;

printf "Tasks: %d out of %d\n", $info->{done}, $info->{tasks};
printf "Total editing time: %.4f\nAverage editing time: %.4f\n", $info->{acc_editing}, $info->{acc_editing}/$info->{done};
printf "Total assessing time: %.4f\nAverage assessing time: %.4f\n", $info->{acc_assessing}, $info->{acc_assessing}/$info->{done};
printf "PE: " . $info->{pe} . "\nHT: " . $info->{ht} . "\n";
printf "Total HTER: %.4f (%.4f/%.4f)\n", $avghter->{hter}, $avghter->{bad}, $avghter->{chunks} if $useTER;

sub getLength
{
	my $str = shift;
	$str =~ s/^\s+//;
	$str =~ s/\s+$//;
	$str =~ s/\s+/ /;
	return length($str);
}

sub getTokens
{
	my $str = shift;
	$str =~ s/^\s+//;
	$str =~ s/\s+$//;
	return scalar(split(/\s+/, $str));
}

sub getCurrentRevision
{
	my ($unitType, $xml, $mttext) = @_;

	my $OUT_TAG = ($unitType eq 'pe')?$PE:$HT;

	my @annotations = $xml->find($ANNOTATION);
	my $n = scalar(@annotations);
	my $changes = [];
	my $transformations = [$mttext];
	my $features = {'revisions' => $n, 'changes' => $changes, 'transformations' => $transformations};
	foreach my $annotation (@annotations){
		my $r = $annotation->attr($REVISION);
		my ($pe) = $annotation->find($OUT_TAG);
		my @indicators = $annotation->find($INDICATOR);
		my $minchanges = $skipChanges->{$unitType};
		$minchanges = $skipChanges->{fix} if $r > 1;
		my $changesSkipped = 0;
		foreach my $indicator (@indicators){
			my $id = $indicator->attr($ID);
			my $type = $indicator->attr($TYPE);
			my $value = sprintf("%s", $indicator->content_list);
			$value = strToSeconds($value) if $type eq 'time';
			$value = toBoolean($value) if $type eq 'flag';
			if ($type eq 'time' or $type eq 'count'){
				$features->{"$INDICATOR:$id"} += $value;
			} elsif ($type eq 'change'){
				if ($trackChanges){
					if ($changesSkipped < $minchanges){
						$changesSkipped++;
					} else{
						my ($edit, $transformedmt) = parseChange($indicator, $transformations->[-1]);
						push(@$transformations, $transformedmt);
						push(@$changes, $edit) if defined $edit;
					}
				}
			} else{
				$features->{"$INDICATOR:$id"} = $value;
			}
		}
		my @assessments = $annotation->find($ASSESSMENT);
		foreach my $assessment (@assessments){
			my $id = $assessment->attr($ID);
			my ($score) = $assessment->find("score");
			my $value;
			if (defined $score){
				$value = sprintf("%s", $score->content_list);
				$value =~ s/\s+/_/g;
			}
			$features->{$ASSESSMENT} = {} unless $features->{$ASSESSMENT};
			$features->{$ASSESSMENT}->{$id} = $value;
		}
		if ($r == $n){
			$features->{"$PE"} = sprintf("%s", $pe->content_list) if $pe;
			$features->{"$PE"} = parseHTML($features->{"$PE"}) if $html;
		}
	}
	return $features;
}

sub parseChange
{
	my ($change, $mt) = @_;
	my $id = $change->attr('id');
	my $elapsed = $change->attr('elapsed');
	my $length = $change->attr('length');
	my $offset = $change->attr('offset');
	my $in = sprintf("%s", $change->content_list);
	my $out = "";
	if ($id eq 'deletion' or $id eq 'substitution'){
		$out = substr ($mt, $offset, $length, $in);
	} elsif ($id eq 'insertion'){
		$out = substr ($mt, $offset, 0, $in);
	}
	my @inTokens = split(/\s+/,trim($in));
	my @outTokens = split(/\s+/,trim($out));
	my $nWords = scalar(@inTokens) + scalar(@outTokens);
	my $lenEdit = length($in) + length($out);
	my $edit = undef;
	if (!($in =~ m/^\s*$/ and $out =~ m/^\s*$/)){
		$edit = {type => $id, elapsed => strToSeconds($elapsed), words => $nWords, len => $lenEdit, in => $in, out => $out};
	}
	return ($edit, $mt);
}

sub toBoolean
{
	my $str = shift;
	return ($str eq '1' or lc($str) eq 'true');
}

sub getHTER
{
	my ($id, $ref, $hyp) = @_;
	if ($useTER){
		open (my $TMPR, ">:utf8", "$tmp/$pername.r") or die "Cound not write temp ref file\n";
		open (my $TMPH, ">:utf8", "$tmp/$pername.h") or die "Cound not write temp hyp file\n";
		print $TMPR "$ref ($id)\n";
		print $TMPH "$hyp ($id)\n";
		close $TMPR;
		close $TMPH;
		my $cmd = "$ter -r $tmp/$pername.r -h $tmp/$pername.h | egrep 'TER'";
		open (my $TER, "$cmd |") or die "Could not compute HTER\n";
		my $hterline = <$TER>;#Total TER: 0.0 (0.0/1.0)
		close $TER;
		$hterline =~ m/Total TER: ([0-9\.]+) \(([0-9\.]+)\/([0-9\.]+)\)/;
		my ($hter, $badchunks, $chunks) = ($1, $2, $3);
		return ($hter, $badchunks, $chunks);
	} else{
		return (0,0,0);
	}
}

sub strToSeconds
{
	my $str = shift;
	my ($d, $h, $m, $s, $milli) = (0, 0,0,0,0);
	if ($str =~ m/([0-9]+)d/){
		$d = $1;
	}
	if ($str =~ m/([0-9]+)h/){
		$h = $1;
	}
	if ($str =~ m/([0-9]+)m/){
		$m = $1;
	}
	if ($str =~ m/([0-9]+)s/){
		$s = $1;
	}
	if ($str =~ m/,([0-9]+)$/){
		$milli = $1;
	}
	return $s+$m*60+$h*60*60+$d*24*60*60+$milli/1000;
}

sub trim
{
	my $str = shift;
	$str =~ s/^\s+//;
	$str =~ s/\s+$//;
	return $str;
}

sub observedNumberOfEdits
{
	my $edits = shift;
	my ($words, $len, $time) = (0,0, 0);
	my $byType = {};
	foreach my $edit (@$edits){
		$words += $edit->{words};
		$len += $edit->{len};
		$time += $edit->{elapsed};
		$byType->{$edit->{type}} += $edit->{elapsed};
	}
	return ($words, $len, $time, $byType);
}

sub detectShifts
{
	my $edits = shift;
	my ($deletion, $insertion) = (undef, undef);
	my $continue = 1;
	while ($continue) {
		foreach my $i (0 .. $#{$edits}-1){
			last if (defined $deletion and defined $insertion);
			my $first = $edits->[$i];
			next unless $first->{type} eq 'deletion';
			my $out = trim($first->{out});
			foreach my $j ($i + 1 .. $#{$edits}){
				my $second = $edits->[$j];
				next unless $second->{type} eq 'insertion';
				my $in = trim($second->{in});
				if ($out eq $in){
					($deletion, $insertion) = ($i, $j);
					last;
				}
			}
		}
		foreach my $i (0 .. $#{$edits}-1){
			last if (defined $deletion and defined $insertion);
			my $first = $edits->[$i];
			next unless $first->{type} eq 'insertion';
			my $out = trim($first->{in});
			foreach my $j ($i + 1 .. $#{$edits}){
				my $second = $edits->[$j];
				next unless $second->{type} eq 'deletion';
				my $in = trim($second->{out});
				if ($out eq $in){
					($insertion, $deletion) = ($i, $j);
					last;
				}
			}
			last if (defined $deletion and defined $insertion);
		}
		if (defined $deletion and defined $insertion){
			my $shift = {type => 'shift', 
				elapsed => $edits->[$deletion]->{elapsed} + $edits->[$insertion]->{elapsed}, 
				words => $edits->[$deletion]->{words}, 
				len => $edits->[$deletion]->{len},
				in => $edits->[$insertion]->{in},
				out => $edits->[$deletion]->{out}
			};
			my ($first, $second) = ($deletion, $insertion);
			($first, $second) = ($second, $first) if $first > $second;
			splice(@$edits, $second, 1);
			splice(@$edits, $first, 1, ($shift));
			($deletion, $insertion) = (undef, undef);
		} else{
			$continue = 0;
		}
	}
}

sub printHistory
{
	my ($prefix, $v, $O) = @_;
	print $O "$prefix$_\n" foreach (@$v);
}

sub printOperations
{
	my ($header, $edits, $O) = @_;
	my $i = 1;
	my $n = scalar(@$edits);
	print $O "$header $n\n";
	foreach my $edit (@$edits){
		my ($in, $out) = ($edit->{in}, $edit->{out});
		$in =~ s/\t/ /g;
		$out =~ s/\t/ /g;
		print $O join("\t", ($i, $edit->{type}, $edit->{elapsed}, $edit->{words}, $edit->{len}, $edit->{in}, $edit->{out})), "\n";
		$i++;
	}
	print $O "\n";
}


sub parseHTML
{
	my $data = shift;
	my $htmlReader = HTML::TreeBuilder->new();
	$htmlReader->parse($data);
	return $htmlReader->as_text;
}
