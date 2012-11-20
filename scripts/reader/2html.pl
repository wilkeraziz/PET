#!/usr/bin/perl

use strict;

binmode STDIN, ':utf8';
binmode STDOUT, ':utf8';
binmode STDERR, ':utf8';

if (@ARGV != 2){
	print STDERR "$0 title dir\n";
	exit;
}

my $title = shift;
my $dir = shift;


print "
<html>
<head>
	<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/> 
	<title>$title</title>
</head>
<body>
<table border='1'>
";

my $header = <>;
chomp $header;
my @names = split(/\s+/, $header);
print "<tr>";
foreach my $field (@names){
	$field =~ s/#//g;
	print "<th>$field</th>";
}
print "</tr>";

while (chomp(my $line = <>)){
	next if $line =~ m/^\s*#/;
	my ($job, $who, $id, $type, $status, $producer, @others) = split(/\s+/, $line);
	print "<tr>
	<td><a name='$job.$id'>$job</a></td>
	<td><a href='$dir/$job.per.pe.html#$id.$producer.$who'>$who</a> (<a href='$dir/$job.per.operations.html#$id.$type.$producer.$who'>o</a>, <a href='$dir/$job.per.history.html#$id.$type.$producer.$who'>h</a>)</td>
	<td><a href='$dir/$job.per.source.html#$id'>$id</a></td>
	<td>$type</td>
	<td>$status</td>
	<td><a href='$dir/$job.per.mt.html#$id.$producer'>$producer</a></td>
	";
	print "\t<td>$_</td>\n" foreach (@others);
	print "</tr>";
}

print "</table>
</body>
</html>";


