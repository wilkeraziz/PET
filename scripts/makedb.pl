#! /usr/bin/perl

use strict;

use XML::Generator; 
use XML::TreeBuilder;
use Getopt::Long "GetOptions";

binmode (STDOUT, ":utf8");

my $Me = "makedb.pl";
my ($help, @tables, $encoding, $alias, $shorter, $th)=();

$encoding = 'utf-8';
$alias = 'phrase-table';
#DEFAULT

$help=1 unless
&GetOptions(
	'table=s' => \@tables,
	'alias=s' => \$alias,
	'th=f' => \$th,
	'shorter' => \$shorter,
	'help' => \$help,
);

$th = -1;

if ($help || !scalar(@tables) ){
	print "$Me <options>
	--table <path>        * A table formatted as follows (multiple allowed)
	                      source ||| target ||| score
    --th                  -1 by default
	--alias <name>        An alias
	--help                print these instructions

	IMPORTANT: use utf8 data
		use `file --mime-encoding <file>` to find out the encoding of your file
		and `iconv -f encoding -t utf-8 <input> > output`
		or `iconv -f encoding -t utf-8 -c <input> > output` to force conversions
		or modify this script in order to read a write using the expected encoding

	\n";
	exit(1);
}


my $data = {};
foreach my $table (@tables){
	loadTable($table, $data);
}

print makeDB($data) . "\n";


sub loadTable
{
	my ($table, $data) = @_;
	open (my $T, "<:utf8", $table) or die "Could not open $table\n$!\n";
	while (my $line = <$T>){
		chomp $line;
		next if $line =~ m/^\s*$/;
		my ($s, $t, $f) = split(/ [|]{3} /, $line);
		next unless $f > $th;
		next if ($shorter and length($t) >= length($s));
		$data->{$s} = {} unless $data->{$s};
		$data->{$s}->{$t} = $f;
	}
	close $T;
}

sub makeDB
{
	my $data = shift;
	my $gen = XML::Generator->new(':pretty', escape => 'always', encoding => $encoding);
	my $entries = [];
	foreach my $s (sort keys %$data){
		my $targets = $data->{$s};
		my $paraphrases = [];
		foreach my $t (sort keys %$targets){
			my $p = $targets->{$t};
			push(@$paraphrases, 
				$gen->paraphrase( {'score' => $p}, $t)
			);
		}
		push (@$entries,
			$gen->entry(
				$gen->phrase($s),
				@$paraphrases
			)
		);
	}
	return $gen->xml(
		$gen->db(
			{'alias' => $alias},
			@$entries
		)
	);
}

