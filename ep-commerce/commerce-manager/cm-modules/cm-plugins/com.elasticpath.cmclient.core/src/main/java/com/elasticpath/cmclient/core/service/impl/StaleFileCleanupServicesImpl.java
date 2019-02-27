/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.util.FileSystemUtil;

/**
 * Stale file cleanup service.
 */
public class StaleFileCleanupServicesImpl {

	private static final Logger LOG = Logger.getLogger(StaleFileCleanupServicesImpl.class);

	/**
	 * The Age of files (in hours) to remove.
	 */
	private int age;


	/**
	 * Gets the age threshold of files to delete, in Hours. Any file in the temp dir that is older than this will be deleted.
	 * @return the age.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Sets the threshold for the age of files to delete, in Hours. Any file in the temp dir that is older than this will be deleted.
	 * @param age the age in Hours.
	 */
	public void setAge(final int age) {
		this.age = age;
	}

	/**
	 * Executes the job.
	 * @throws IOException if there are issues accessing the temp files.
	 */
	public void executeMethod() throws IOException {
		LOG.debug("Executing job");

		File tempDir = FileSystemUtil.getTempDir();
		Instant threshold = Instant.now().minus(Duration.ofHours(this.getAge()));
		File[] files = tempDir.listFiles();
		if (files != null) {
			for (File file : files) {
				BasicFileAttributes attributes = Files.readAttributes(Paths.get(file.toURI()), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
				FileTime fileCreationTime = attributes.creationTime();

				Instant instant = fileCreationTime.toInstant();
				if (instant.isBefore(threshold)) {
					deleteFile(file);
				}

			}
		}

	}

	private void deleteFile(final File file) {
		LOG.debug("File age exceeds threshold, deleting" + file.getAbsolutePath());
		if (!file.delete()) {
			LOG.warn("Could not delete file " + file.getAbsolutePath());
		}
	}
}
