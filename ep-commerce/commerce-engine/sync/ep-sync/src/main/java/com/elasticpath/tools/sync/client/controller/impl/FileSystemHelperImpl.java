/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.client.controller.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.controller.FileSystemHelper;
import com.elasticpath.tools.sync.configuration.marshal.XMLMarshaller;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.impl.JobDescriptorImpl;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * Immutable implementation of {@link FileSystemHelper}.
 */
public class FileSystemHelperImpl implements FileSystemHelper {

	private static final Logger LOG = Logger.getLogger(FileSystemHelperImpl.class);

	private final SyncJobConfiguration syncJobConfiguration;

	private final int objectsLimitPerFile;

	private final boolean addTimestampToFolder;

	private String workingFolder;

	/**
	 * A marker object for an end-of-file.
	 * This is driven by the fact that {@link ObjectInputStream} does not have any function to check for EOF.
	 */
	private static final String EOF = "EOF";

	/**
	 * Constructor.
	 *
	 * @param syncJobConfiguration the configuration for this sync job
	 * @param objectsLimitPerFile the maximum number of objects per file
	 * @param addTimestampToFolder specifies whether or not to generate subdirectories with a timestamp
	 */
	public FileSystemHelperImpl(final SyncJobConfiguration syncJobConfiguration, final int objectsLimitPerFile, final boolean addTimestampToFolder) {
		this.syncJobConfiguration = syncJobConfiguration;
		this.objectsLimitPerFile = objectsLimitPerFile;
		this.addTimestampToFolder = addTimestampToFolder;
	}

	/**
	 * The path where the output files will be written.
	 */
	private String getRootPath() {
		final String rootPath = syncJobConfiguration.getRootPath();
		return FilenameUtils.concat(rootPath, syncJobConfiguration.getSubDir());
	}

	private String getWorkingFolderPath() {
		if (workingFolder == null) {
			final String rootPath = syncJobConfiguration.getRootPath();
			if (rootPath == null) {
				throw new IllegalArgumentException("Root Path should be valid");
			}
			final String adapterParameter = syncJobConfiguration.getAdapterParameter();
			if (addTimestampToFolder) {
				this.workingFolder = FilenameUtils.concat(rootPath, generateSubPath(syncJobConfiguration));
			} else {
				this.workingFolder = FilenameUtils.concat(rootPath, adapterParameter);
			}
			LOG.info("Using working folder path : " + this.workingFolder);
		}
		return this.workingFolder;
	}

	private static String generateSubPath(final SyncJobConfiguration syncJobConfiguration) {
		LOG.info("Generating sub directory name");
		return syncJobConfiguration.getAdapterParameter() + "_" + syncJobConfiguration.getExecutionId();
	}

	@Override
	public void readTransactionJobFromFile(final String fileName, final SerializableObjectListener objectListener) {
		try {
			File file = new File(getFilePath(getRootPath(), fileName, false));
			if (!file.exists()) {
				throw new FileNotFoundException("Cannot find file: " + file.getAbsolutePath());
			}
			for (int index = 1; file.exists(); index++) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Reading from file: " + file.getName());
				}
				try (final ObjectInput inputStream = new ObjectInputStream(
						new BufferedInputStream(
								new FileInputStream(file)))) {
					for (Object obj = inputStream.readObject();
						  !Objects.equals(EOF, obj);
						  obj = inputStream.readObject()) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Object read: " + obj);
						}

						objectListener.processObject((SerializableObject) obj);
					}
				}
				file = new File(getFilePath(getRootPath(), fileName, index, false));
			}
		} catch (final IOException e) {
			throw new SyncToolRuntimeException("Unable to read Job Unit", e);
		} catch (final ClassNotFoundException e) {
			throw new SyncToolRuntimeException("Unable to process Job Unit", e);
		}
	}

	@Override
	public void saveTransactionJobToFile(final Iterable<? extends SerializableObject> objectProvider, final String fileName) {
		try {
			final Iterator<? extends SerializableObject> iterator = objectProvider.iterator();
			File file = new File(getFilePath(getWorkingFolderPath(), fileName, false));
			for (int index = 1; iterator.hasNext(); index++) {
				try (final ObjectOutput outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
					for (int objectsRead = 0;
						 iterator.hasNext() && objectsRead <= objectsLimitPerFile;
						 objectsRead++) {
						final SerializableObject object = iterator.next();
						outputStream.writeObject(object);

						if (LOG.isDebugEnabled()) {
							LOG.debug("Object saved: " + object);
						}
					}
					outputStream.writeObject(EOF);
				}
				file = new File(getFilePath(getWorkingFolderPath(), fileName, index, false));
			}
		} catch (final Exception e) {
			throw new SyncToolRuntimeException("Unable to write Job Unit", e);
		}
	}

	@Override
	public void saveJobDescriptor(final JobDescriptor jobDescriptor, final String fileName) {
		try (OutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(getFilePath(getWorkingFolderPath(), fileName, true)))) {
				new XMLMarshaller(JobDescriptorImpl.class).marshal(jobDescriptor, outputStream);
		} catch (final IOException e) {
			throw new SyncToolRuntimeException("Unable to marshal job descriptor", e);
		}
	}

	/**
	 * Gets file path for fileName and creates directories if it needs.
	 *
	 * @param fileName should be just a name of the file without any path elements
	 * @param index the index of the object being written to a file
	 * @param createDirs if it is true root directories will be created (for write)
	 * @return concatenated file path
	 */
	private String getFilePath(final String rootPath, final String fileName, final int index, final boolean createDirs) {
		final String indexedFileName = FilenameUtils.getBaseName(fileName)
				+ index + '.' + FilenameUtils.getExtension(fileName);

		return getFilePath(rootPath, indexedFileName, createDirs);
	}

	/**
	 * Gets file path for fileName and creates directories if it needs.
	 *
	 * @param rootPath the root path to return the file path from.
	 * @param fileName should be just a name of the file without any path elements.
	 * @param createDirs if it is true root directories will be created (for write)
	 * @return concatenated file path
	 */
	private String getFilePath(final String rootPath, final String fileName, final boolean createDirs) {
		if (createDirs && !new File(rootPath).mkdirs()) {
			LOG.warn("Could not create directories : " + fileName);
		}
		return FilenameUtils.concat(rootPath, fileName);
	}

}
