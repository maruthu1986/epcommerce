package com.elasticpath.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.log4j.Logger;

import com.elasticpath.selenium.framework.util.PropertyManager;

/**
 * SyncConfig class.
 */

public class SyncConfig {

	private final PropertyManager propertyManager = PropertyManager.getInstance();
	private final String resourseFolderPath;
	private int exitValue = 1;
	private FileWriter fileWriter;
	private PrintWriter printWriter;
	private final String syncCliPath;
	private static final String SYNC_FILE_NAME = "sync.sh";
	private URL loc;
	private static final Logger LOGGER = Logger.getLogger(SyncConfig.class);

	/**
	 * Constructor.
	 */
	public SyncConfig() {
		URL loc = getClass().getClassLoader().getResource(SYNC_FILE_NAME);
		resourseFolderPath = new File(loc.getFile()).getParent();
		syncCliPath = resourseFolderPath + "/" + propertyManager.getProperty("sync.cli");
	}

	/**
	 * Gets the process exit value.
	 *
	 * @return process exit value
	 */
	public int getExitValue() {
		return this.exitValue;
	}

	/**
	 * Runs the data sync command in sync.sh file.
	 *
	 * @param changeSetGuid the change set guid
	 * @return the process exist value
	 */

	public int runDataSync(final String changeSetGuid) {
		try {

			loc = getClass().getClassLoader().getResource("sync.sh");

			Runtime.getRuntime().exec("chmod 777 " + loc.getPath());

			Runtime.getRuntime().exec("chmod 777 " + syncCliPath + "/synctool.sh");
			String[] cmd = {loc.getPath(), resourseFolderPath + "/" + propertyManager.getProperty("sync.cli"), changeSetGuid};
			LOGGER.info("cmd ...... : " + resourseFolderPath + "/" + propertyManager.getProperty("sync.cli") + " changeSetGuid: " + changeSetGuid);
			Process process = Runtime.getRuntime().exec(cmd);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				LOGGER.info(line);
			}
			exitValue = process.waitFor();
			LOGGER.info("Process exitValue: " + exitValue);
		} catch (Exception e) {
			LOGGER.error(e);
		}

		return exitValue;
	}

	/**
	 * Creates source and target config.xml.
	 */
	public void writeToSyncConfig() {
		writeToSyncConfig("source");
		writeToSyncConfig("target");
	}

	private void writeToSyncConfig(final String fileType) {
		try {

			fileWriter = new FileWriter(syncCliPath + "/" + fileType + "config.xml");
			printWriter = new PrintWriter(fileWriter);

			printWriter.println("<?xml version='1.0' encoding='UTF-8'?>");
			printWriter.println("<connectionconfiguration type=\"local\">");
			printWriter.println("<url>" + propertyManager.getProperty(fileType + ".url") + "</url>");
			printWriter.println("<login>" + propertyManager.getProperty(fileType + ".login") + "</login>");
			printWriter.println("<pwd>" + propertyManager.getProperty(fileType + ".pwd") + "</pwd>");
			printWriter.println("<driver>" + propertyManager.getProperty(fileType + ".driver") + "</driver>");
			printWriter.println("</connectionconfiguration>");

			closeWriters(fileWriter, printWriter);

		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			closeWriters(fileWriter, printWriter);
		}
	}

	private void closeWriters(final FileWriter fileWriter, final PrintWriter printWriter) {
		if (fileWriter != null) {
			try {
				fileWriter.close();
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}

		if (printWriter != null) {
			printWriter.close();
		}
	}

}
