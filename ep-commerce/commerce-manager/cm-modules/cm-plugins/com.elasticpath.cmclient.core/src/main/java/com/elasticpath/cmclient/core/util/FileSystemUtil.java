/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**
 *
 * The util class for accessing the FileSystem.
 *
 */
public final class FileSystemUtil {
	private static final Logger LOG = Logger.getLogger(FileSystemUtil.class);

	/**
	 * Private constructor.
	 */
	private FileSystemUtil() {

	}
	/**
	 * The Temp sub directory.
	 */
	private static final String CM_TEMP_SUBDIR =  File.separator +  "ep-cm-temp";

	/**
	 * Gets the CM temp directory path as a string. Creates it if necessary.
	 * @return the CM temp directory path.
	 */
	public static String getTempDirectory() {

		String tempDirPath = FileUtils.getTempDirectoryPath() + CM_TEMP_SUBDIR;
		Path path = Paths.get(tempDirPath);
		if (!path.toFile().exists()) {
			try {
				Files.createDirectories(path);
			} catch (IOException exception) {
				LOG.error("Could not create file", exception); //$NON-NLS-1$
			}
		}
		return  tempDirPath;
	}

	/**
	 * Gets the temp dir File.
	 * @return the temp dir File.
	 */
	public static File getTempDir() {
		return new File(getTempDirectory());
	}
}
