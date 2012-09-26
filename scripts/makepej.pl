#! /usr/bin/perl

use strict;

use XML::Generator; 
use XML::TreeBuilder;
use Getopt::Long "GetOptions";

binmode (STDOUT, ":utf8");

my ($help, $jobid, @src, @tgt, @ref, $encoding)=();

$encoding = 'utf-8';
#DEFAULT
my ($ID, $SYS, $TYPE) = qw/id producer type/;

$help=1 unless
&GetOptions(
	'jobid=s' => \$jobid,
	'src=s' => \@src,
	'tgt=s' => \@tgt,
	'ref=s' => \@ref,
	'help' => \$help,
);

if ($help || !$jobid){
	print "$0 <options>
	--jobid <string>      * job identification (no spaces)
	--src <sys=file>      source file
	--tgt <sys=file>      target file (for post-editing tasks)
	--ref <sys=file>      reference file
	--help                print these instructions

	IMPORTANT: use utf8 data
		use `file --mime-encoding <file>` to find out the encoding of your file
		and `iconv -f encoding -t utf-8 <input> > output`
		or `iconv -f encoding -t utf-8 -c <input> > output` to force conversions
		or modify this script in order to read a write using the expected encoding

	EXAMPLES:
	a) HT task 
		a.1) with references
		$0 --jobid bigbang --src opus=examples/bigbang.en --ref opus=examples/bigbang.br > examples/bigbang.pej
		a.2) without references
		$0 --jobid bigbang --src opus=examples/bigbang.en > examples/bigbang.pej
	b) PE task

		$0 --jobid bigbang-pe --src en-opus=examples/bigbang.en --ref pt-opus=examples/bigbang.br --tgt baseline=examples/bigbang.baseline.br --tgt google=examples/bigbang.google.br --tgt bing=examples/bigbang.bing.br  > examples/bigbang-pe.pej

	\n";
	exit(1);
}

my $N = undef;

my @sources;
foreach my $s (@src){
	my ($producer, $file) = split(/=/, $s);
	my $sentences = readSentences($file);
	$N = scalar(@$sentences) unless defined $N;
	$N == scalar(@$sentences) or die "$file differs in the number of sentences: $N\n";
	push(@sources, [$producer, $sentences]);
}

my @targets;
foreach my $t (@tgt){
	my ($producer, $file) = split(/=/, $t);
	my $sentences = readSentences($file);
	$N == scalar(@$sentences) or die "$file differs in the number of sentences: $N\n";
	push(@targets, [$producer, $sentences]);
}

my @references;
foreach my $r (@ref){
	my ($producer, $file) = split(/=/, $r);
	my $sentences = readSentences($file);
	$N == scalar(@$sentences) or die "$file differs in the number of sentences: $N\n";
	push(@references, [$producer, $sentences]);
}

print makeTask() . "\n";

sub get
{
	my ($i, @options) = @_;
	my @return;
	foreach my $option (@options){
		my ($producer, $sentences) = @$option;
		push(@return, [$producer, $sentences->[$i]]);
	}
	return @return;
}


sub makeTask
{
	my $gen = XML::Generator->new(':pretty', escape => 'always', encoding => $encoding);
	my $jobs = [];
	my $type = (@tgt)? 'pe': 'ht';
	foreach my $i (0 .. $N-1){
		my @s = get($i, @sources);
		my @t = get($i, @targets);
		my @r = get($i, @references) if @ref;
		my @elements;
		push(@elements, {$ID => ($i+1), $TYPE => $type});
		push(@elements, 
			$gen->S( {$SYS => $_->[0]}, $_->[1] )
		) foreach (@s);
		if (@ref){
			push(@elements, 
				$gen->R( {$SYS => $_->[0]}, $_->[1] )
			) foreach (@r);
		}
		push(@elements, 
			$gen->MT( {$SYS => $_->[0]}, $_->[1] )
		) foreach (@t);
		my $task = $gen->task(@elements);
		push(@$jobs, $task);
	}
	return $gen->xml(
		$gen->job(
			{$ID => $jobid},
			@$jobs
		)
	);
}

sub readSentences
{
	my $file = shift;
	my $sentences = [];
	open (my $I, "<:utf8", $file) or die "Could not open file: $file\n";
	while (my $line = <$I>){
		chomp $line;
		push (@$sentences, $line);
	}
	return $sentences;
}

# returns a trimed version of a given string
sub trim
{
	my $string = shift;
	$string =~ s/^\s+//;
	$string =~ s/\s+$//;
	return $string;
}
