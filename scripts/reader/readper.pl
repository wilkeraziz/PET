#!/usr/bin/perl

use strict;

use Getopt::Long qw/GetOptions/; 
use XML::TreeBuilder;
use HTML::TreeBuilder;

binmode(STDIN, ":utf8");
binmode(STDOUT, ":utf8");
binmode(STDERR, ":utf8");

my $Me = "readper.pl";
my ($JOB, $UNIT, $TASK, $S, $MT, $PE, $HT, $ANNOTATION, $IDEAL, $PREFERABLE, $MAX, $INDICATOR, $REVISION, $ID, $PRODUCER, $STATUS, $TYPE) = qw/job unit task S MT PE HT annotation ideal preferable max indicator r id producer status type/;
my $ASSESSMENT = "assessment";
my ($help, $per, $tmp, $history);

my ($annotator, $sys, $ep, $flagid);

my $skipChanges = {ht => 1, pe => 3, fix => 3};
my $trackChanges;
my $ter = "/home/tools/ter/tercom-0.7.25/tercom.7.25.jar";
my $encoding = 'utf-8';
$tmp = 'tmp';
my $html = 'html';
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
	'html=s' => \$html,
	'track-changes' => \$trackChanges,
	'help' => \$help,
);

$flagid = 1;

if ($help or not ($per and $annotator)){
	print STDERR "$Me
	--per <file>         * A PER file
	--annotator <str>    * Annnotator who performed the PEJ
    --html      <dir>    Where the .html files will be placed (default: html)
	--tmp <dir>          A temp directory (default: ./tmp)
	--ter <path>         Path to TER jar (default: /home/tools/ter/tercom-0.7.25/tercom.7.25.jar)
	                     Computes HTER for each segment if the tool is available where indicated
	--help               Displays these instructions

	--indicator <id>     Parses additional indicators
	--assessment <id>    Parses assessments

	--track-changes      Gets a history of changes

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


mkdir $html;

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
	$jobid =~ s/\.per$//;
}
# no spaces
$jobid =~ s/\s+/_/g;

my @xmlTasks = $reader->find($UNIT);
@xmlTasks = $reader->find($TASK) unless @xmlTasks;

open (my $OS, ">:utf8", "$html/$per.source.html") or die "Could not write src file\n";
open (my $OM, ">:utf8", "$html/$per.mt.html") or die "Could not write mt file\n";
open (my $OP, ">:utf8", "$html/$per.pe.html") or die "Could not write pe file\n";
open (my $OSUM, ">:utf8", "$per.summary") or die "Could not write summary file\n";
open (my $OHIS, ">:utf8", "$html/$per.history.html") or die "Could not write history file\n";
open (my $OOPS, ">:utf8", "$html/$per.operations.html") or die "Could not write history (operations) file\n";

print $OS "
<html>
<head>
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/> 
	<title>$jobid - Source</title>
</head>
<body>
<table border='1'>
<tr><th>id</th><th>Sentence</th></tr>
";

print $OM "
<html>
<head>
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/> 
	<title>$jobid - MT</title>
</head>
<body>
<table border='1'>
<tr><th>id</th><th>Producer</th><th>Sentence</th></tr>
";

print $OP "
<html>
<head>
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/> 
	<title>$jobid - PE</title>
</head>
<body>
<table border='1'>
<tr><th>id</th><th>Producer</th><th>Annotator</ht><th>Sentence</th></tr>
";


print $OHIS "
<html>
<head>
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/> 
	<title>$jobid - History</title>
</head>
<body>
<table border='1'>
";

print $OOPS "
<html>
<head>
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/> 
	<title>$jobid - Operations</title>
</head>
<body>
<table border='1'>
";

#print $OOPS join("\t", ("#i", "#type", "#elapsed", "#words", "#len", "#in", "#out")), "\n\n";

my $info = {};
my $avghter = {};

# if you want to include information in the summary or change its order this is the place
my @header = qw/id type status producer revisions editing assessing src_tokens mt_tokens pe_tokens src_len mt_len pe_len letter-keys digit-keys white-keys symbol-keys navigation-keys erase-keys copy-keys cut-keys paste-keys do-keys keystrokes unchanged lc_unchanged secondsPerWord oer-word oer-len oer-time irate drate/;


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
        'letter-keys' => $revision->{"$INDICATOR:letter-keys"},
        'digit-keys' => $revision->{"$INDICATOR:digit-keys"},
        'white-keys' => $revision->{"$INDICATOR:white-keys"},
        'symbol-keys' => $revision->{"$INDICATOR:symbol-keys"},
        'navigation-keys' => $revision->{"$INDICATOR:navigation-keys"},
        'erase-keys' => $revision->{"$INDICATOR:erase-keys"},
        'copy-keys' => $revision->{"$INDICATOR:copy-keys"},
        'cut-keys' => $revision->{"$INDICATOR:cut-keys"},
        'paste-keys' => $revision->{"$INDICATOR:paste-keys"},
        'do-keys' => $revision->{"$INDICATOR:do-keys"},
		'keystrokes' => sumkeys($revision),
		'editing' => $revision->{"$INDICATOR:editing"},
		'assessing' => $revision->{"$INDICATOR:assessing"},
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
	my $editingTime = $revision->{"$INDICATOR:editing"};
	my $prefix = "";
	$prefix = "$id: " if $flagid;
	printHistory($jobid, $annotator, $id, $type, $status, $producer, $revision->{transformations}, $OHIS);
	printOperations($jobid, $annotator, $id, $type, $status, $producer, $revision->{changes}, $editingTime, $OOPS);
	print $OS "<tr><td><a name='$id'>$id</a></td><td>$srctext</td></tr>\n";
	print $OM "<tr><td><a name='$id.$producer'>$id</a></td><td>$producer</td><td>$mttext</td></tr>\n";
	$petext =~ s/\n/\t/g;
	print $OP "<tr><td><a name='$id.$producer.$annotator'>$id</a></td><td>$producer</td><td>$annotator</td><td>$petext</td></tr>\n";
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

print $OHIS "</table>
</body>
</html>";

print $OOPS "</table>
</body>
</html>";

print $OS "</table>
</body>
</html>";

print $OM "</table>
</body>
</html>";

print $OP "</table>
</body>
</html>";

close $OOPS;
close $OS;
close $OM;
close $OP;
close $OSUM;


$avghter->{hter} = $avghter->{bad}/$avghter->{chunks} if $useTER and $avghter->{chunks};

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
	my $transformations = [];
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
			} elsif ($type eq 'change' or $type eq 'wrap'){
				if ($trackChanges){
                    my $initialmt = (scalar(@$transformations))?$transformations->[-1]:'';
					my ($edit, $transformedmt) = parseChange($indicator, $id, $type, $initialmt);
					push(@$changes, $edit);
					push(@$transformations, $transformedmt);
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
	my ($indicator, $op, $type, $mt) = @_;
	my $edit = {};
    print "$op: ", $mt, "\n";
    if ($type eq 'change'){
        my $id = $indicator->attr('id');
        my $elapsed = $indicator->attr('elapsed');
        my $length = $indicator->attr('length');
        my $offset = $indicator->attr('offset');
        my $t0 = $indicator->attr('t0');
        my $text = sprintf("%s", $indicator->content_list);
        my $nWords = scalar(split(/\s+/, $text));
        $edit = {type => $id, elapsed => strToSeconds($elapsed), words => $nWords, len => $length, in => $text, out => '', off => $offset, 'shift' => 0};
        if ($id eq 'deletion'){
            $edit->{in} = '';
            $edit->{out} = $text;
        }
        if ($id eq 'deletion'){
            substr ($mt, $offset, $length, '');
        } elsif ($id eq 'insertion'){
            substr ($mt, $offset, 0, $edit->{in});
        } elsif ($id eq 'assignment'){
            $mt = $edit->{in};
        }
        print "\t$id: ", $mt, "\n";
        
    } elsif ($type eq 'wrap'){
        my ($first, $second) = $indicator->find('action');
        my $id1 = $first->attr('id');
        my $elapsed1 = $first->attr('elapsed');
        my $length1 = $first->attr('length');
        my $offset1 = $first->attr('offset');
        my $t01 = $first->attr('t0');
        my $text1 = sprintf("%s", $first->content_list);
        my $nWords1 = scalar(split(/\s+/, $text1));
        
        my $id2 = $second->attr('id');
        my $elapsed2 = $second->attr('elapsed');
        my $length2 = $second->attr('length');
        my $offset2 = $second->attr('offset');
        my $t02 = $second->attr('t0');
        my $text2 = sprintf("%s", $second->content_list);
        my $nWords2 = scalar(split(/\s+/, $text2));

        $edit = {type => $op, elapsed => strToSeconds($elapsed1) + strToSeconds($elapsed2), words => $nWords1 + $nWords2, len => $length1 + $length2, in => $text1, out => $text2, off => $offset1, 'shift' => $offset2 - $offset1};
        if ($id1 eq 'deletion' or $id2 eq 'insertion'){
            $edit->{in} = $text2;
            $edit->{out} = $text1;
        }
        if ($id1 eq 'insertion'){
            substr ($mt, $offset1, 0, $text1); # ins
            print "\t", $mt, "\n";
            substr ($mt, $offset2, $length2, ''); #del
            print "\t", $mt, "\n";
        } else{
            substr ($mt, $offset1, $length1, ''); #del
            print "\t", $mt, "\n";
            substr ($mt, $offset2, 0, $text2); # ins
            print "\t", $mt, "\n";
        }
    } else {
        die "Not sure what to do with indicator of type: $type\n";
    }
    print "final: $mt\n";
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
    my $first = 1;
	foreach my $edit (@$edits){
        if ($first){
            $first = 0;
            next;
        }
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
	my ($job, $annotator, $id, $type, $status, $producer, $history, $O) = @_;
	my $n = scalar(@$history);
	print $O getHistHeader("$id.$type.$producer.$annotator", "$job $annotator $id $type $producer ($status) - $n edits"), "\n";
	foreach my $entry (@$history){
		print $O "<tr><td>$entry</td></tr>\n";
	}
}

sub getColor
{
	my $ratio = shift;
	if ($ratio >=0 and $ratio < 10){
		return ("FFFFFF","black");
	} elsif ($ratio >= 10 and $ratio < 20){
		return ("00CCFF","black");
	} elsif ($ratio >= 20 and $ratio < 30){
		return ("0033FF","white");
	} elsif ($ratio >= 30 and $ratio < 40){
		return ("000099","white");
	} elsif ($ratio >= 40 and $ratio < 50){
		return ("9966FF","white");
	} elsif ($ratio >= 50 and $ratio < 60){
		return ("9900CC","white");
	} elsif ($ratio >= 60 and $ratio < 70){
		return ("990066","white");
	} elsif ($ratio >= 70 and $ratio < 80){
		return ("FF6600","white");
	} elsif ($ratio >= 80 and $ratio < 90){
		return ("FF3300","white");
	} elsif ($ratio >= 90 and $ratio < 100){
		return ("FF0000","white");
	} else {
		return ("000000","white");
	}
}

sub printOperations
{
	my ($job, $annotator, $id, $type, $status, $producer, $edits, $totalTime, $O) = @_;
	my $i = 1;
	my $n = scalar(@$edits);
	print $O getOpHeader("$id.$type.$producer.$annotator", "$job $annotator $id $type $producer ($status) - $n edits");
	foreach my $edit (@$edits){
		my ($in, $out) = ($edit->{in}, $edit->{out});
		$in =~ s/\t/ /g;
		$out =~ s/\t/ /g;
		print $O "<tr>";
		my $ratio = sprintf ("%.2f", $edit->{elapsed}*100.0/$totalTime);
		my ($back, $fore) = getColor($ratio);
		print $O "\t<td>$_</td>" foreach ($i, $edit->{type});
		print $O "\t<td bgcolor='#$back'><font color='$fore'>", $edit->{elapsed}, " (", $ratio, "%)</td>"; 
		print $O "\t<td>$_</td>\n" foreach ($edit->{words}, $edit->{len}, $edit->{off}, $edit->{'shift'}, $edit->{in}, $edit->{out});
		print $O "</tr>\n";
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

sub getOpHeader
{
	my ($anchor, $title) = @_;
	return "<tr><th colspan='9'><a name='$anchor'>$title</a></th></tr>
<tr>
<th>i</th>
<th>operation</th>
<th>time</th>
<th>#words</th>
<th>#chars</th>
<th>where</th>
<th>shift</th>
<th>in</th>
<th>out</th>
</tr>";
}

sub getHistHeader
{
	my ($anchor, $title) = @_;
	return "<tr><th><a name='$anchor'>$title</a></th></tr>";
}

sub sumkeys
{
    my $revision = shift;
    return $revision->{"$INDICATOR:letter-keys"} +
        $revision->{"$INDICATOR:digit-keys"}+
        $revision->{"$INDICATOR:white-keys"}+
        $revision->{"$INDICATOR:symbol-keys"}+
        $revision->{"$INDICATOR:navigation-keys"}+
        $revision->{"$INDICATOR:erase-keys"}+
        $revision->{"$INDICATOR:copy-keys"}+
        $revision->{"$INDICATOR:cut-keys"}+
        $revision->{"$INDICATOR:paste-keys"}+
        $revision->{"$INDICATOR:do-keys"};
}
