Hot Deployment
==============

There are 2 possible ways to hot-deploy OSGi bundles:

1. Using Tomcat's hot-deploy mechanism
2. Using Maven Sling plugin

NOTE: Neither of these 2 methods will work 100% due to various issues with, OS (probably only Windows), Tomcat and/or Equinox (or certain Eclipse 
plugins).  
However, hot deployment may be still useful for majority of the bundles.

Tomcat hot deployment
=====================

The hot deployment works only with external Tomcat installation.  
All attempts to make it work with Tomcat, started with **Maven Tomcat** plugin (from ext-cm-webapp-runner) have failed.

All tests have been conducted with Tomcat 7.0.65 (part of Demo package).

In this case, an old version of a bundle is deleted and a new version is copied to
  **WEB-INF/eclipse/plugins** dir. 
  This means that modified bundle will remain under Tomcat and changes will be visible even after restarting Tomcat. 

The final step is touching **TOMCAT_HOME/conf/context.xml** that triggers hot deployment of **Web CM** 
  application.
  
The side effects of this method are:  
  1. long deployment time - about 25-30s (same as very first deployment when Tomcat starts)  
  2. Windows OS places a lock on **TOMCAT_HOME/work** which needs to be deleted before every deployment. 
  	 Hence, the hot deployment may or may not work correctly some times (e.g. changes not visible or login page not accessible).  
  	 Linux/Mac users may not experience this, so the hot deployment may work every time, until OutOfMemory error occurs. 
  	 
  	 
To hot deploy a bundle using this method it is required to specify **web.cm.deployment.dir** property in 
   	 **.M2/settings.xml** under active profile e.g. **ep-developer** like in the example below:
   	 
   	 ```
   	 <web.cm.deployment.dir>c:/Servers/EP-Commerce-Demo/webapps/apache-tomcat-7.0.65/webapps/cm</web.cm.deployment.dir>
   	 ```
   	 
   	 Essentially, the last part, **cm** is the web application folder under **TOMCAT_HOME/webapps** dir.
   	 
Change folder to plugin home e.g. COMMERCE_MANAGER_CLIENT_HOME/cm-plugins/com.elasticpath.cmclient.pricelistmanager and execute
  	 **mvn clean install -DskipAllTests -Phot-deploy-tomcat**.
  	 
Watch Tomcat's console or log file.
  	 
The deployment worked even for **cmclient.core** plugin


Hot deployment with Maven Sling plugin
=======================================

This type of hot-deployment is in-memory, which means that changes will disappear after restarting Tomcat.  
The new version of a bundle (with changes) is first uninstalled from Equinox and then installed.  
Default deployment settings for Sling plugin will start the bundle and refresh packages upon completion of the operation
( (un)install).

Nevertheless, it has been observed that that's not always the case - e.g. in case of Policy and ConditionBuilder plugins, 
the bundles are in **Installed** mode, instead **Active**. After pressing **Refresh Packages** button in Felix console, the bundles become active.

Another issues that may arise:

1. Bundles that have dependencies on other bundles e.g. *PriceListManager* bundle depends on *Policy* bundle, most probably will not work until 
dependency bundle is redeployed too. Interestingly, *PriceListManager* depends on *ConditionBuilder* too, but it's not required to redeploy 
*ConditionBuilder* plugin.

2. The Core plugin doesn't work after redeployment. The first reason is non-active Spring context (although it should be because new Context is 
created every time when bundle is started; and closed when bundle is stopped). This could be fixed in a proper place, but introducing a 
non-production code didn't make sense.  
	
	The second reason is inconsistent state in Eclipse's extension registry, used for obtaining permission extension points. When Core bundle is 
	stopped, an event is sent and extension points are removed from the registry. However, it should be expected that extension points will be 
	restored when bundle is started, but that never happens, thus leading to NullPointerException.
	
	Since there is no API to "refresh" or recreate the registry (neither restarting the registry bundle helped) it would be hard (if not impossible)
	to work around this problem.
	
	Thus, the Core plugin should **NOT** be hot-deployed with Sling.
	
To hot deploy a bundle using this method it is required to specify **sling.url** property in 
   	 **.M2/settings.xml** under active profile e.g. **ep-developer** like in the example below:
   	 
   	 ```
   	 <sling.url>http://localhost:8080/cm/system/console</sling.url>
   	 ```  
Change folder to plugin home e.g. COMMERCE_MANAGER_CLIENT_HOME/cm-plugins/com.elasticpath.cmclient.pricelistmanager and execute
  	 **mvn clean install -DskipAllTests -Phot-deploy-sling**.
  	 
Verify that changes are visible and if they aren't check Felix's console and bundle's status (it should be **Active**)