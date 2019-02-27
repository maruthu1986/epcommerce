#!/usr/bin/perl

my $nums = $ARGV[0];
my @fileList = glob("ThreadDump_*.log");
my %files;
my @allSummary;
my %allFiles;
my $outfile = "ThreadSummary.csv";
my $temporaryCounter = 0;
my $allQueues = 1;
my %allThreadsCache; # 
my $doStackTraceInspection = 1;

my %GLOBAL_LockLines;
my %GLOBAL_SettingCallers;

open(OUT,">$outfile") || die "Error: Couldn't create file $outfile: $!\n";

foreach $file (@fileList) {
	$file =~ /ThreadDump\_(\d+)\./;
	my $fileid = $1;
	if (not $fileid =~ /\d/) {
		$files{$file} = $file;
	} else {
		$files{$fileid} = $file;
	}
	
}		


foreach $file (sort {$a <=> $b} keys %files) {
	my $filename = $files{$file};
	my @file;
	#print "Importing information from $filename\n";
	$temporaryCounter +=1;
     
     # First read in the contents of the file and break it into separate pieces
     my ($threads,$time) = extractThreadStacksFromFile($filename);
     if (not ref($threads) eq 'ARRAY') {
          print "Error importing $filename: $threads\n";
          next;
     } elsif (scalar(@{$threads}) < 1) {
     	  print "Error: No threads found in $filename.\n";
     	  next;
     }
     # Now convert these into thread objects
     my $threadCount = -1;
     foreach $thread (@{$threads}) {
          $threadCount += 1;
          my $newThread = buildThreadObject($thread);
         
          if (not ref($newThread) eq 'HASH') {
               print "Error: Couldn't construct object for thread $threadCount: $newThread\n";
               $file[$threadCount] = '';
          } else {
               $file[$threadCount] = $newThread;
          }
     }

     if ($doStackTraceInspection) {
        $allThreadsCache{$time} = \@file;
     }
     my $summary = createThreadDumpSummary(\@file);
     if (not ref($summary) eq 'HASH') {
          print "Error: $summary\n";
     } else {
          $$summary{'Filename'} = $filename;
          $$summary{'Time'} = $time;
          push @allSummary, $summary;
     }
     
#     foreach $element (sort keys %{$summary}) {
#          print "$element\t\t=> $$summary{$element}\n";
#     }

     # If you want to keep the file object around in a big list
     #$files{$file} = \@file;
     
     

}

# Make sure that I know about every column that I might get
my %allSummaryColumns;
foreach $fileSummary (@allSummary) {
     foreach $column (keys %{$fileSummary}) {
          if ($column ne 'Time' && $column ne 'Filename') {
               $allSummaryColumns{$column} += 1;
          }
     }
}
# Print out the csv header
my $header = "Time,Filename,";
foreach $column (sort keys %allSummaryColumns) {
     $header .= "$column,";
}
chop $header;
print OUT "$header\n";

# Now print out a row for each dump
foreach $fileSummary (@allSummary) {
     my $row = $$fileSummary{'Time'} . "," . $$fileSummary{'Filename'} . ",";
     
     foreach $column (sort keys %allSummaryColumns) {
          if (!$$fileSummary{$column} ) {
               $row .= "0,";
          } else {
               $row .= $$fileSummary{$column} . ",";
          }
     }
     chop $row;
     print OUT "$row\n";
}

close(OUT);

if ($doStackTraceInspection) {
    #print "Now doing inspection of busy threads\n";
    inspectBusyStacks(\%allThreadsCache);
}

print "\nTop Methods Waiting on Locks:\n";
foreach my $l (sort {$GLOBAL_LockLines{$b} <=> $GLOBAL_LockLines{$a}} keys %GLOBAL_LockLines) {
	print "$GLOBAL_LockLines{$l} -> $l\n";
}
print "\n";

print "\nTop Settings Callers:\n";
foreach my $l (sort {$GLOBAL_SettingCallers{$b} <=> $GLOBAL_SettingCallers{$a}} keys %GLOBAL_SettingCallers) {
	print "$GLOBAL_SettingCallers{$l} -> $l\n";
}
print "\n";

###############################################
sub extractThreadStacksFromFile {
     my ($file) = (@_);
     
     if (not -e $file) {
          return "$file does not exist\n";
     }
     my @threads;
     my $inThreads = 0;
     my $threadCount = -1;
     my $time;
     
     local *TDUMP;
     open(TDUMP,$file) || return "Couldn't open $file: $!";

     while ($line = <TDUMP>) {
          chomp $line;

	#print "$line\n";

          if ($line =~ /\A\#/ || $line =~ /\ANOTE/) {
		print "skipping line: $line\n";
               next;

          #} elsif ($line =~ /Thread dump taken at: \[(.+)\]/) {
          } elsif (!$time) {
          		$line =~ /\A\[(.+)\]/;
          		$time = $1;
          }
          
          # Strip out the timestamp info
          #$line =~ s/\A\[(.+):\d\d\]\s*//;i
          $line =~ s/^\[(.+?)\]\s*//;
         
          if ($line =~/\A\"/) {
               #Then this is the start of a new thread
               $threadCount += 1;
               $inThreads = 1;
               my @newThreadArray;
               $threads[$threadCount] = \@newThreadArray;
               push @{$threads[$threadCount]}, $line;
          } elsif ($inThreads == 1 && $line =~ /\S/) {	
               push @{$threads[$threadCount]}, $line;
          } 
     }
     
     close(TDUMP);
     return (\@threads, $time);
}


sub buildThreadObject {
     # For now, this subroutine will build a thread psuedo-object as a hash, 
     # when handed the text for a thread from a thread dump, as reference to an array of lines
     
     my ($threadText) = (@_);
     if (not (ref($threadText) eq 'ARRAY')) {
          return ("Could not build thread hash because an array reference was not given: $threadText");
     } elsif (scalar(@{$threadText}) < 1) {
          return ("Could not build thread hash because an array reference was not given");
     } elsif (not $$threadText[0] =~ /tid\=/) {
          return ("The array passed does not appear to contain the information expected");
     }
     
     
     # Create the object 
     my @locksHeld;
     my @locksWaiting;
     
     my %thisThread = (
          # General properties
          'threadId'                 => '',
          'threadName'               => '',
          'nId'                      => '',
          'currentState'             => '',
          'isDaemon'                 => -1,
          'priority'                 => -1,
          'endOfFirstLine'           => '',   
          'currentMethod'            => '',
          'isCurrentMethodNative'    =>  0,
          'initiatingMethod'         => '',
          'fullThreadStack'          => '',
          'locksHeld'                => \@locksHeld,
          'locksWaiting'             => \@locksWaiting,
          # Application specific properties 
          'isInJDBC'                 =>  0,
          'isInNetwork'              =>  0,
          'isBusy'                   => -1,
          'firstJDBCMethod'          => '',
          'lastJDBCMethod'           => '',
          'lastAppMethod'          => '',
          'firstAppMethod'         => '',
          'threadQueue'              => '',
          'appThreadType'          => '',
          'creatingDBConnection'     => 0,
          'inSettings'				=> 0,
          'inStoreResManager'		=> 0
     );   
     
     # Lists of things to ignore etc
     my @threadNameNotBusy = ('Signal Dispatcher',
                              'VM Thread',
                              'VM Periodic Task Thread',
                              'Suspend Checker Thread');
     my @currentMethodMeansNotBusy = ('java.lang.Thread.sleep',
                                      'java.lang.Object.wait',
                                      'java.net.PlainSocketImpl.socketAccept',
                                      'java.lang.UNIXProcess.waitForProcessExit',
                                      '.PosixSocketMuxer.',
                                      'weblogic.socket.NTSocketMuxer.getIoCompletionResult',
                                      'weblogic.platform.SunVM.threadDump');
     my @appClassesToIgnoreForFirstMethod = ('CacheControlFilter.java',
                                               'RequestCleanupFilter.java',
                                               'RequestProcessor.java',
                                               'UrlRewritingServlet.java',
                                               'com.elasticpath.persistence.impl.JpaPersistenceEngineImpl',
						'com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl'
                                               #,'com.elasticpath.commons.util.impl.StoreResourceManagerProxyImpl.getResource'
                                               );
     my @daoClassesToIgnore = ();
     
#     my %threadTypeMapping = (
#          'Signal Dispatcher'                     => 'JVM',
#          'VM Thread'                             => 'JVM',
#          'VM Periodic Task Thread'               => 'JVM',
#          'Suspend Checker Thread'                => 'JVM',
#          'main'                                  => 'JVM',
#          'GC Daemon'                             => 'JVM',
#          'process reaper'                        => 'JVM',
#          'Java2D Disposer'                       => 'JVM',
#          'Finalizer'                             => 'JVM',
#          'Reference Handler'                     => 'JVM',
#          'Low Memory Detector'					  => 'JVM',
#          'GC task thread'					      => 'JVM',
#          'DestroyJavaVM'					      => 'JVM',
#		  'CompilerThread1'					      => 'JVM',
#		  'CompilerThread0'					      => 'JVM',
#		  'AdapterThread'					      => 'JVM',
#          'ExecuteThread'                         => 'ExecuteThread',
#          'RMI Reaper'                            => 'WebLogicBackground',
#          'RMI TCP'                               => 'WebLogicBackground',
#          'weblogic.health.CoreHealthMonitor'     => 'WebLogicBackground',
#          'ListenThread.Default'                  => 'WebLogicBackground',
#          'SSLListenThread.Default'               => 'WebLogicBackground',
#          'weblogic.time.TimeEventGenerator'      => 'WebLogicBackground',
#          'weblogic.security.SpinnerRandomSource' => 'WebLogicBackground',
#          'Thread-'                               => 'UnNamedThread',
#          'ElementEventQueue'                     => 'JCS',
#          'CacheEventQueue'                       => 'JCS',
#          'VDE Transaction Processor Thread'      => 'LDAP',
#          'LDAP'                                  => 'LDAP',
#          'DefaultQuartzScheduler'				  => 'Quartz',
#          'http-'								  => 'Http',
#          'Listener'							  => 'Listener'
#     );

          
                              
     
     # Start looking at the thread
     my $firstLine = shift(@{$threadText});
#     $thisThread{'debug1'} = $firstLine;

	#print "first line: $firstLine\n";    
 
     if ($firstLine =~ /daemon /) {
          $thisThread{'isDaemon'} = 1;
          $firstLine =~ s/daemon //;
     } else {
          $thisThread{'isDaemon'} = 0;
     }
     
     #if (not $firstLine =~ /\[[^\]]+\]/) {
     #     # Add this in to make sure the regular expression below works
     #     $firstLine .= " [null]";
     #}

     #$firstLine =~ /\A\"([^\"]+)\"\s+prio\=(\S+)\stid\=(\S+)\snid\=(\S+)\s([^\[]+)\s\[([^\]]+)\]/;
     #print "first line: $firstLine\n";
     $firstLine =~ /\A\"(.+)\"\s+prio\=(\S+)\stid\=(\S+)\snid\=(\S+)\s([^\[]+)\s\[([^\]]+)\]/;
	
     # hitting:
     # "GC task thread#0 (ParallelGC)" prio=1 tid=0x000000001f502a20 nid=0x481f runnable
	# not hitting
	#  "http-80-204" daemon prio=1 tid=0x00002aaab0c010b0 nid=0x4e54 runnable [0x000000004f80f000..0x000000004f812b90] 
     $thisThread{'threadName'} = $1;

	#if (defined $thisThread{'threadName'}) { print "hit thread: " . $thisThread{'threadName'} . "\n"; }

     $thisThread{'priority'} = $2;
     $thisThread{'threadId'} = $3;
     $thisThread{'nId'} = $4;
     $thisThread{'currentState'} = $5;
     $thisThread{'endOfFirstLine'} = $6;
     
     
     if ($thisThread{'threadName'} =~ /queue/) {
          # Then I need to pick out the name of the queue to which this belongs
          #ExecuteThread: '2' for queue: 'weblogic.admin.RMI'
          $thisThread{'threadName'} =~ /queue\:\s+\'(.+)\'/;
          $thisThread{'threadQueue'} = $1;
     } else {
          $thisThread{'threadQueue'} = 'NONE';
     }
     
     
#     # Figure out the thread type
#     foreach $match (keys %threadTypeMapping) {
#          if ($thisThread{'threadName'} =~ /\A$match/) {
#          	   $thisThread{'appThreadType'} = $threadTypeMapping{$match};
#               last;
#          }
#     }
#     if ($thisThread{'appThreadType'} eq '') {
#     	$thisThread{'appThreadType'} = 'Unknown';
#     	print "$thisThread{'threadName'}\n";
#     }


     # Set the value for the current method and properties which rely on it
     $thisThread{'currentMethod'} = $$threadText[0];
     $thisThread{'currentMethod'} =~ s/\A\s+at //;
     # Is this thread "busy"?
     if (isInArray($thisThread{'currentMethod'},\@currentMethodMeansNotBusy)) {
          # Then this is a thread that is considered not busy by Our standards
          $thisThread{'isBusy'} = 0;
     } elsif (isInArray($thisThread{'threadName'},\@threadNameNotBusy)) {
          $thisThread{'isBusy'} = 0;
     } else {
          $thisThread{'isBusy'} = 1;
     }
     # Is this thread in a network call?
     if ($thisThread{'currentMethod'} =~ /java\.net\./) {
          # Then this is a thread that is in a network call
          $thisThread{'isInNetwork'} = 1;
     } 
     # Is this thread in a network native call?
     if ($thisThread{'currentMethod'} =~ /Native Method/) {
          # Then this is a thread that is in a native call
          $thisThread{'isCurrentMethodNative'} = 1;
     }
     
     # Keep track of the first method in the stack
     $thisThread{'initiatingMethod'} = $$threadText[-1];
     
     my $settingsCaller;
         
     #Now go through the whole thread stack and figure things out accordingly
     my $lastLine;
     foreach $line (@{$threadText}) {
          # Clean up the spacing at the beginning
          $line =~ s/\A\s+at //;
          #locked 	- locked <0x99442870> (a oracle.jdbc.driver.OraclePreparedStatement)
          if ($line =~ /\A\s+\-\s+locked\s+\<([^\>]+)\>/) {
               push @locksHeld, $1;
               $line =~s /\A(\s+)\-\s+locked//;
               next;
          }
          #- waiting to lock <0x71c1f0c8> (a java.util.WeakHashMap)
          # - waiting to lock <0x73aeeab0> (a com.elasticpath.commons.util.impl.StoreResourceManagerProxyImpl)
          if ($line =~ /waiting to lock\s+\<([^\>]+)\>\s*\(\S\s+(\S+)\)/) {
               push @locksWaiting, $1;
               my $locked = $2;
               $GLOBAL_LockLines{$lastLine}++;
               $line =~s /\A(\s+)\-\s+waiting to lock//;
               next;
          }
          
          if ($line =~ /\.jdbc\./i) {
               if ($thisThread{'lastJDBCMethod'} eq '') {
                    $thisThread{'lastJDBCMethod'} = $line;
               }
               $thisThread{'isInJDBC'} = 1;
               $thisThread{'firstJDBCMethod'} = $line;
          }
          # Update for specific app
          if ( ($line =~ /com\.elasticpath/ or $line =~ /com\.tibco/ or $line =~ /com\.symantec/ ) &&
              not (isInArray($line,\@appClassesToIgnoreForFirstMethod))) { 
               if ($thisThread{'lastAppMethod'} eq '') {
                    $thisThread{'lastAppMethod'} = $line;
                    #print "Method: $thisThread{'lastAppMethod'}\n";
               }
               $thisThread{'firstAppMethod'} = $line;
               
          }
          if ($line =~ /dao/i) {
               if (not (isInArray($line,\@daoClassesToIgnore))) { 
                    if ($thisThread{'lastDaoMethod'} eq '') {
                        $thisThread{'lastDaoMethod'} = $line;
                    }
                    $thisThread{'firstDaoMethod'} = $line;
               }               
               
          }
          if ($line =~ /oracle\.jdbc\.driver\.OracleConnection\.\<init\>/) {
               $thisThread{'creatingDBConnection'} = 1;
          }
          
          if ($line =~ /com\.elasticpath\.service\.settings\.impl/ || $line =~ /com\.elasticpath.+getSetting/) {
          		$thisThread{'inSettings'} = 1; 
				$thisThread{'isBusy'} = 1;
          }
          
          if ($line =~ /com\.elasticpath\.commons\.util\.impl\.StoreResourceManagerProxyImpl/) {
          		$thisThread{'inStoreResManager'} = 1;
          		$thisThread{'isBusy'} = 1;
          }
          
          if (	$thisThread{'inSettings'} == 1 
          		&& $settingsCaller eq ''
          		&& $line =~ /com\.elasticpath\./
          		&& $line !~ /com\.elasticpath\.service\.settings/) {
          			
          	$settingsCaller = $line;
          	chomp $settingsCaller;
          	$GLOBAL_SettingCallers{$settingsCaller}++;
          }
          			
          		
          
          $lastLine = $line;           
   
     }
          
     # Store what's left as the full thread stack
     $thisThread{'fullThreadStack'} = $threadText;
          
     return(\%thisThread);
     
}


sub isInArray {
     my ($value, $array) = (@_);
     if (!$value || ref($array) ne 'ARRAY') {
          return 0;
     }
     foreach $compare (@{$array}) {
          if ($value =~ /$compare/) {
               #Then there's a match so say yes and get out
               return 1;
          }
     }
     
     return 0;
}


sub createThreadDumpSummary {
     my ($file) = (@_);
     if (not ref($file) eq 'ARRAY') {
          return("An array reference is required to create a thread dump summary");
     }
     
     # Things that that will be included in the summary
     my %summary = (
          'All:Total'         => 0,
          'All:Locked'        => 0,
          'All:Busy'          => 0,
          'All:Runnable'      => 0,
          'Busy:InJDBC'       => 0,
          'Busy:InNetwork'    => 0,
          'Busy:InNative'     => 0,
          'Busy:InSettings'	  => 0,
          'Busy:InStoreResMan'=> 0,
     );

     my %queueInit;
     my @queueCols = ('TotalThreads','RunnableThreads','BusyThreads','InJDBC','InNetwork','InNative','InSettings','InStoreResMan');
     my @busyCols  = ();
     
     foreach $thread (@{$file}) {
          $summary{'All:Total'} += 1;
          if ($$thread{'currentState'} =~ /runnable/) {
               $summary{'All:Runnable'} += 1;
          }
          $summary{'All:Type:' . $$thread{'appThreadType'}} += 1;
          
          if (scalar(@{$$thread{'locksWaiting'}})) {
               $summary{'All:Locked'} += 1;            
          }
          
          if($$thread{'isBusy'}) {
               $summary{'All:Busy'} += 1;
               
               # Only count this stuff for threads I've marked as busy               
               if($$thread{'isInJDBC'}) {
                    $summary{'Busy:InJDBC'} += 1;
               }
               if($$thread{'isInNetwork'}) {
                    $summary{'Busy:InNetwork'} += 1;
               }
               if($$thread{'isCurrentMethodNative'}) {
                    $summary{'Busy:InNative'} += 1;
               }
               if($$thread{'creatingDBConnection'}) {
                    $summary{'Busy:CreateConn'} += 1;
               }
               if ($$thread{'inSettings'}) {
               		$summary{'Busy:InSettings'} += 1;
               }
               if ($$thread{'inStoreResManager'}) {
               		$summary{'Busy:InStoreResMan'} += 1;
               }
               

          }

#          if ( ($allQueues && $$thread{'threadQueue'} ne 'NONE') ||
#               (!$allQueues && ($$thread{'threadQueue'} =~ /default/i || $$thread{'threadQueue'} =~ /PrimaryExecute/i))) {
          if ( ($allQueues && $$thread{'threadQueue'} ne 'NONE') || 
                (!$allQueues && ($$thread{'threadQueue'} =~ /default/i || $$thread{'threadQueue'} =~ /PrimaryExecute/i)) ){                                      
               # Make sure that all possible columns are initilized
               if (!$queueInit{ $$thread{'threadQueue'} }) {
                    foreach (@queueCols) {
                         $summary{'Queue:' . $$thread{'threadQueue'} . ":" .  $_} = 0;
                    }
                    $queueInit{$$thread{'threadQueue'}} = 1;
               }
                         
               $summary{'Queue:' . $$thread{'threadQueue'} . ':TotalThreads'} += 1;
               if ($$thread{'currentState'} =~ /runnable/) {
                    $summary{'Queue:' . $$thread{'threadQueue'} . ':RunnableThreads'} += 1;
               }
               if ($$thread{'isBusy'}) {
                    $summary{'Queue:' . $$thread{'threadQueue'} . ':BusyThreads'} += 1;
               }
               
               if($$thread{'isInJDBC'}) {
                    $summary{'Queue:' . $$thread{'threadQueue'} . ':InJDBC'} += 1;
               }
               if($$thread{'isInNetwork'}) {
                    $summary{'Queue:' . $$thread{'threadQueue'} . ':InNetwork'} += 1;
               }
               if($$thread{'isCurrentMethodNative'}) {
                    $summary{'Queue:' . $$thread{'threadQueue'} . ':InNative'} += 1;
               }
          }
     }
     
     return \%summary;    
}


sub inspectBusyStacks {
    my ($allThreads) = @_;
    
    if (ref($allThreads) ne 'HASH') {
        print STDERR "Error: Thread list sent for inspection was not a hash: $allThreads\n";
    }
    
    my $totalLineCount = 0;
    my %allLines;
    
    my @tree;
    my %leafIndex;
    my %daoMethods;
    my %appLastMethods;
    
    #print "Sorting through all threads to find the info I need\n";
    foreach my $file (sort {$a <=> $b} keys %{$allThreads}) {
        #print "Looking at $file\n";
        foreach my $thread (@{$$allThreads{$file}}) {
            # Only deal with busy threads
            if (not $$thread{'isBusy'}) {
                #print "Skipping thread\n";
                next;
            }
            
            if ($$thread{'lastDaoMethod'}) {
                $daoMethods{$$thread{'lastDaoMethod'}} += 1;
            }
            
            if ($$thread{'lastAppMethod'}) {
            	$appLastMethods{$$thread{'lastAppMethod'}}++;
            }
            
#            # Pull out the full stack as an array reference, then reverse it      
#            my $stack = $$thread{'fullThreadStack'};
#            my @stack = reverse(@{$stack});
#            
#            my $lineNumber = 0;
#            my $fullLocator = '';
#            foreach my $line (@stack) {
#                # Skip lines for locks etc
#                if ($line =~ /\A\s*\(/ || $line =~ /\A\s*\</) {
#                    next;
#                }
#                # 
#                $allLines{$line} += 1;                
#                $totalLineCount += 1;
#                
#                
#                # Determine what the new full locator would be for this line
#                if ($fullLocator eq '') {
#                    $fullLocator .= $line;
#                } else {
#                    $fullLocator .= '###' . $line;
#                }
#                
#                
#                # Get the level hash from the main tree
#                # If there isn't one, then create one
#                my $level = $tree[$lineNumber];
#                if (ref($level) ne 'HASH') {
#                    my %level;
#                    $tree[$lineNumber] = \%level;
#                    $level = $tree[$lineNumber];
#                }
#                
#                $$level{$fullLocator} += 1;
#                if ($$level{$fullLocator} == 1) {
#                    # If it's new, add it to the index
#                    my $indexId = getLeafIndexNumber(\%leafIndex,$fullLocator);
#                }
#                    
#                $lineNumber +=1;
#            }
        }
    }
   
# Sort through and report on the most common dao calls found
print "\nTop Dao Methods:\n";
foreach my $daoMethod (sort {$daoMethods{$b} <=> $daoMethods{$a}} keys %daoMethods) {
    print "$daoMethods{$daoMethod} - $daoMethod\n";
}
print "\n";

print "\nTop App Methods:\n";

foreach my $appLastMethods (sort {$appLastMethods{$b} <=> $appLastMethods{$a}} keys %appLastMethods) {
    print "$appLastMethods{$appLastMethods} - $appLastMethods\n";
}
print "\n";  
    
#    print "Found a tree of " . scalar(@tree) . " depth.  Chewing down it now\n";
#    my $currentLevel = 0;
#    my $xmlOutput = '<?xml version="1.0" encoding="UTF-8"?>' . "\n::::0::::LEVEL0::::\n</xml>";
#    my $htmlOutput;
#    local *TPL;
#    open(TPL,"template.html") || print STDERR "Error: Couldn't open template.html: $!\n";
#    while(my $line = <TPL>) {
#        $htmlOutput .= $line;
#    }
#    close(TPL);
#    my $previous = $htmlOutput;
#    foreach $level (@tree) {
#        print "Working on level $currentLevel: $level\n";
#        # Build a list of parents, pointing to the children
#        my %parentThreads;
#        
#        foreach $leaf (keys %{$level}) {
#            #print "\tFound leaf $leaf\n";
#            my $lastCall;
#            my $parent;
#            
#            if (not $leaf =~ /\#\#\#/) {
#                # Then this must be the base of a thread
#                $lastCall = $leaf;
#                $parent = '0';
#            } else {
#                $leaf =~ /\#\#\#([^\#]+)\Z/;
#                $lastCall = $1;
#                if (!$lastCall) {
#                    # Then something's wrong
#                    print STDERR "Error: Couldn't find last call in this stack\n\t$leaf\n";
#                }
#                $parent = $leaf;
#                $parent =~ s/\#\#\#[^\#]+\Z//;
#                
#            }
#            
#            # Store on the parent thread
#            if (ref($parentThreads{$parent}) ne 'ARRAY') {
#                my @newArray = ();
#                $parentThreads{$parent} = \@newArray;             
#            }
#            push @{$parentThreads{$parent}}, $leaf;
#
#        }
#        
#        
#        foreach my $parent (keys %parentThreads) {
#            print "Parent:$parent\n";
#            # Print a page for each parent thread
#            #printPageForParent($parent,$parentThreads{$parent},\%leafIndex,$level);
#            
#            my $sub = "::::" . $parent . "::::LEVEL" . $currentLevel . "::::";
#            
#            my $addition;
#            my $haddition;
#            # Loop through each of the children
#            foreach my $child (@{$parentThreads{$parent}}) {             
#                my $childLast = getLastCall($child);
#                my $newSub = "::::" . $childLast . "::::LEVEL" . ($currentLevel + 1) . "::::";
#                print "\tChild: $childLast\n";
#                #print "NewSubIs: $newSub\n";
#                #$addition .= "\n" . '<THREAD METHOD="' . getLastCall($child) . '" LEVEL="' . $currentLevel . '" COUNT="' . $$level{$child} . '">' . "\n$newSub\n</THREAD>\n";
#                $haddition .= "\n<li><b>($$level{$child})</b>&nbsp;$childLast&nbsp;<b>$currentLevel</b>\n<ul>\n$newSub\n</ul>\n</li>\n";
#            }
#            
#            #print "Was:\n$xmlOutput: \n";
#            #print "Starting regexp\n";
#            $xmlOutput =~ s/::::$parent::::LEVEL$currentLevel::::/$addition/;
#            #print "$haddition\n";
#            #$htmlOutput =~ s/::::$parent::::LEVEL$currentLevel::::/$haddition/;
#            #$htmlOutput =~ s/$sub/$haddition/; 
#            my $evalLine = '$htmlOutput' . " =~ s/$sub/$haddition/";
#            print "$evalLine\n";
#            eval "$evalLine";
#            #print "Finished regexp\n";
#            #print "\n\nIs:\n$xmlOutput\n\n\n\n";
#            #print "\n";
#        }
#        print "HTML WAS:$previous\n\nHTML IS:\n$htmlOutput\n\n\n";
#        $previous = $htmlOutput;
#        
#        
#        $currentLevel += 1;   
#    }  
#    
#    
#    local *OUT;
#    local *IN;
#    my $tempfile = '000_tmp.xml';
#    my $realFile = "000.xml";
#    open(OUT,">$tempfile") || print STDERR "Error: Couldn't create $tempfile: $!\n";
#    print OUT $xmlOutput;
#    close(OUT);
#   
#    open(IN,$tempfile) || print STDERR "Error: Couldn't open $tempfile: $!\n";
#    open(OUT,">$realFile") || print STDERR "Error: Couldn't create $realFile: $!\n";
#    while (my $inLine = <IN>) {
#        if ($inLine =~ /::::/) {
#            next;
#        } else {
#            print OUT $inLine;
#        }
#    }
#    close(OUT);             
#    close(IN); 
#    unlink($tempfile);           
#    
#    local *OUT;
#    local *IN;
#    $tempfile = '000_tmp.html';
#    $realFile = "000.html";
#    open(OUT,">$tempfile") || print STDERR "Error: Couldn't create $tempfile: $!\n";
#    print OUT $htmlOutput;
#    close(OUT);
#   
#    open(IN,$tempfile) || print STDERR "Error: Couldn't open $tempfile: $!\n";
#    open(OUT,">$realFile") || print STDERR "Error: Couldn't create $realFile: $!\n";
#    while (my $inLine = <IN>) {
#        if ($inLine =~ /::::/) {
#            next;
#        } else {
#            print OUT $inLine;
#        }
#    }
#    close(OUT);             
#    close(IN); 
#    unlink($tempfile);           
}

sub printPageForParent {
    my ($parent,$childList,$ind,$level) = @_;
    
    local *OUT;
    my $parentIndex = getLeafIndexNumber($ind,$parent);
    if (!$parentIndex) {
        print STDERR "Error: Couldn't find index for $parent\n";
    }
    
   
    my $header = "<html>\n";
    my $footer = "</html>\n";
    my $output = $header;
    
    # Break out the components of the parent
    my @pieces = split /\#\#\#/,  $parent;
    foreach $pele (@pieces) {
        $output .= "$pele<br>\n";
    }
    
    foreach $child (@{$childList}) {
        my $childIndex = getLeafIndexNumber($ind,$child);
        if (!$childIndex) {
            print STDERR "Error: Couldn't find index for $child\n";
        }
        
        my $childCount = $$level{$child};
        my $lastCall = getLastCall($child);
#        my $lastCall;
#        if (not $child =~ /\#\#\#/) {
#            # Then this must be the base of a thread
#            $lastCall = $child;
#        } else {
#            $child =~ /\#\#\#([^\#]+)\Z/;
#            $lastCall = $1;
#            if (!$lastCall) {
#                # Then something's wrong
#                print STDERR "Error: Couldn't find last call in this stack\n\t$leaf\n";
#            }
#        }
 
        $output .= '&nbsp;&nbsp;&nbsp;&nbsp;' . $childCount . '&nbsp;&nbsp;<a href="' . $childIndex . '.html">' . $lastCall . '</a><br>' . "\n";
    }
    
    #print "Printing $parentIndex.html for $parent\n";
    open(OUT,">$parentIndex.html") || print STDERR "Error: Couldn't create file $parentIndex.html: $!\n";
    print OUT $output;
    close(OUT);     
    
    
        
}

sub getLastCall {
    my ($string) = @_;
    my $lastCall;
    if (not $string =~ /\#\#\#/) {
        # Then this must be the base of a thread
        $lastCall = $string;
    } else {
        $string =~ /\#\#\#([^\#]+)\Z/;
        $lastCall = $1;
        if (!$lastCall) {
            # Then something's wrong
            print STDERR "Error: Couldn't find last call in this stack\n\t$string\n";
        }
    }
    return ($lastCall);
    
        
}
sub getLeafIndexNumber {
    my ($ind,$leaf) = @_;
    
    my $return = $$ind{$leaf};
    if (!$return) {
        # Find the max index number -> Hacky way to do this
        my $max = 0;
        foreach $key (keys %{$ind}) {
            if ($$ind{$key} > $max) {
                $max = $$ind{$key};
            }
        }
        $$ind{$leaf} = ($max +1);
        $return = $$ind{$leaf};
    }
    
    return($return);
            
}
