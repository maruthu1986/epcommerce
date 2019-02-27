#!/usr/bin/perl
#
# simple script to separate one big console dump into seperate individual thread dumps
# 
# this is specific to weblogic and is definitely specific to the sun jvm
#
# last updated: 2009-04-14
#
# author: alan schroder, elastic path
#
#
# [04/1/2009 16:12:07] "[ACTIVE] ExecuteThread: '0' for queue: 'weblogic.kernel.Default (self-tuning)'" daemon prio=1 tid=0x68540fe0 nid=0x696e runnable [0x68478000..0x6847e020]
# [04/1/2009 16:12:07] 	at java.lang.ClassLoader.defineClass1(Native Method)
# [04/1/2009 16:12:07] 	at java.lang.ClassLoader.defineClass(ClassLoader.java:620)
# [04/1/2009 16:12:07] 	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:124)


my $ignorePattern = "^\<";

my $filePattern = shift;

die "Usage: perl $0 <filePattern ie. console\*.log>\n" unless (defined $filePattern);

my @files = glob("$filePattern");

die "No files found" if (scalar(@files) < 1);

my $outFilePrefix = "ThreadDump_";
my $outFileCount = 1;

my $inDump = 0;
my @dump;
my $dumpCount = 0;

foreach my $file (@files)
{

	print "Seperating thread dumps found in $file ... \n";

	open(IN, $file) or die "Could not open $file";
	
	while(my $line = <IN>)
	{
		chomp $line; 
		
		if ($inDump and ($line =~ /Full thread dump/ or $line =~ /^\</))
		{
			# We're using this as the end of the last dump
			$inDump = 0;
			dumpDump(\@dump);
			@dump = (); # clear array
			
			print "Dumps found: " . ++$dumpCount . "\n";
			
		}
		if ($line =~ /Full thread dump/)
		{
			$inDump = 1;
		}
		elsif ($inDump) # just a normal thread dump line
		{
			my $moddedLine = '[TDNum' . $dumpCount . '] ' . $line;
			push @dump, $moddedLine;
		}
		
	}
		
	close IN;
	
	print "done.\n";
	
}

exit 0;



#####

sub dumpDump
{
	my $lines = shift;
	my $outFile = $outFilePrefix . $outFileCount . '.log';
	$outFileCount++; # for next time
	open(OUT, ">$outFile") or die "Could not open $outFile for writing.";
	foreach my $line (@{$lines})
	{
		print OUT $line."\n";
	}
	close OUT;
}



