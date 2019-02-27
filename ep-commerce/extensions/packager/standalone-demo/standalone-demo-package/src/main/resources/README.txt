Elastic Path Commerce Standalone Demo
=====================================
This is a stand-alone version of Elastic Path Commerce that runs on your local computer.

PREREQUISITES
-------------
* Java 8 installed as default JVM
* Operating systems
  - Windows
  - MacOS
* Memory
  - 8 GB minimum
  - 16 GB recommended

RUNNING THE DEMO
----------------
This package includes scripts for Mac and Windows systems to start and stop the demo:
- start.sh / start.bat
- stop.sh / stop.bat

After the applications have started there may be a short delay before products are visible as the search indexes are being built for the first time. 
Subsequent starts should not have a delay. 

Applications are started at the following URLs:
- Commerce Admin:      http://localhost:9081/admin
- Cortex Studio:       http://localhost:9080/studio
- Cortex:              http://localhost:9080/cortex
- Search Server:       http://localhost:38082/searchserver/status/info.html
- Integration Server:  http://localhost:38083/integration/status/info.html
- ActiveMQ Console     http://localhost:39081/jms

Default administrator password for Commerce Admin:
- User ID: admin
- Password: 111111

SETTING UP THE IMPORT EXPORT TOOL
---------------------------------
Before running importexport.sh / importexport.bat make sure the target and source destinations are configured appropriately in the following files:
- exportconfiguration.xml:     <target>path/to/target/export/destination</target>
- importconfiguration.xml:     <source>path/to/source/import/destination</source>

TROUBLESHOOTING
---------------
Logs can be found in the following locations: 
- Commerce Admin:      target/logs/ep-cm.log
- Cortex:              target/logs/ep-cortex.log
- Search Server:       target/logs/ep-searchserver.log
- Integration Server:  target/logs/ep-integration.log
- Import Export Tool:  target/logs/ep-importexport.log

ENABLING EMAIL
--------------
Email sending is disabled by default. To enable email, update the following settings and restart the demo:
* Set COMMERCE/SYSTEM/emailEnabled to "true"
* Set COMMERCE/SYSTEM/mailHost to a valid SMTP server
* Additional settings are available to configure the SMTP server connection if required:
    COMMERCE/SYSTEM/EMAIL/mailPort (default is 25)
    COMMERCE/SYSTEM/EMAIL/smtpScheme (default is smtp)
    COMMERCE/SYSTEM/EMAIL/emailAuthenticationUsername
    COMMERCE/SYSTEM/EMAIL/emailAuthenticationPassword

KNOWN ISSUES
------------

PB-170: Mac: Cortex doesn't start sometimes in Demo Package
-----------------------------------------------------------
Problem: Cortex server does not start on Mac with errors similar to the following logged:
Caused by: org.h2.jdbc.JdbcSQLException: IO Exception: "java.net.UnknownHostException: MacBook-Pro.local: MacBook-Pro.local: nodename nor servname provided, or not known" [90028-173]

This issue can occur if the connected network has recently changed.

Workaround: The H2 database is attempting to use an old hostname. Restarting the computer or changing the computer's name should resolve this problem. The computer's name can be updated by going to: System Preference -> Sharing

Alternatively, the computer's hostname can be mapped to loopback in the system's host file /etc/hosts. For example:
127.0.0.1       {computer's host name}

The hostname can be determined by running the 'hostname' command from the terminal.

