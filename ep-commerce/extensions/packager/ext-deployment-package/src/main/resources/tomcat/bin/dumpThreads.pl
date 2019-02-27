#!/usr/bin/perl

my $sleep = 120;	# This is how long we'll sleep between getting thread dumps

my $psLine = 'org.apache.catalina.startup.Bootstrap';	# This is the line we'll use in our ps grep to find the PID


# Read different time if entered
my $newSleep = shift;

$sleep = (defined $newSleep and $newSleep =~ /^\d+$/)? $newSleep : $sleep;

my %javaPids = %{getPids()};	# You can also put this inside the loop below if you're worried about the PID changing

#print "$_, " foreach (keys %javaPids);

while (1) {
	foreach my $pid (keys %javaPids) {
		`kill -3 $pid`;
	}
	sleep $sleep;
}

sub getPids {

	my %javaPids;
	local *CMD;
	open(CMD,"ps -ef |") || print STDERR "Error: Couldn't execte ps: $!\n";
	while (my $line = <CMD>) {
		if ($line =~ /$psLine/) {
			$line =~ /\A\s*\S+\s+(\d+)\s/;
			my $pid = $1;
			$javaPids{$pid} = 1;
		}
	}
	close(CMD);

	return \%javaPids;

}
