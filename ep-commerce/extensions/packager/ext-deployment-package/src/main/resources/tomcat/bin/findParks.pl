#!/usr/bin/perl -w

use Digest::MD5  qw(md5 md5_hex md5_base64);

my $debug = 0;
#my $parkPattern = 'java.net.SocketInputStream.socketRead0';
my $parkPattern = shift;
my $filesPattern = '*ThreadDump*';
my $stackLevels = shift;

my %sigHash;
my $sig;
my $levelCount;
my $parkCount = 0;


die "Usage: perl $0 <levels>" if (not defined $stackLevels);

my @dumpfiles = glob($filesPattern);

foreach my $file (@dumpfiles)
{
	open(IN, "$file") or die "Could not open $file";
	
	while(my $line = <IN>)
	{
		if ($line =~ /$parkPattern/)
		{
			$parkCount++;

			# obviously we're in the thread dump here so let's find the next com.* entry
			$levelCount = 0;
			$sig = "";
			while($line = <IN>)
			{
				last if ($levelCount >= $stackLevels); # break if we've grabbed deep enough

				last if ( not ($line =~ /at\ / or $line =~ /locked/ or $line =~ /waiting to lock/) ); # if we've run out of thread dump, bail scene

				if ($line =~ /(com\.elasticpath.*)/ or $line =~ /(com\.lulu.*)/)
				{
					#chomp $line;
					$levelCount++;
					$sig .= $1 . "||";
					$debug and print $levelCount . ". " . $1 . "\n";
				}
			}
			# Before we continue on to the next one, hash and save this guy
			$sigHash{$sig} += 1;
		}
	}

	close(IN);
}


# Print summary
print "Break down for finding $parkCount instances of $parkPattern\n\n";
foreach my $stack (sort { $sigHash{$b} <=> $sigHash{$a} } keys %sigHash)
{
	#print $stack."\n";
	my @methods = split(/\|\|/, $stack);
	# hack for blank entries -- need to fix
	if (scalar(@methods) > 0)
	{
		print "Count: " . $sigHash{$stack} . "\n";
		print "  $_\n" foreach (@methods);
	}
}


exit 0;
