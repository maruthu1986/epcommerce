/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.client.controller;

import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * The Helper class which abstracts a way of saving and reading {@link com.elasticpath.tools.sync.job.TransactionJob}s and {@link JobDescriptor}s.
 * <p>It hides operations with directories. It generates subDirectory depends on parameter if it needs.</p>
 */
public interface FileSystemHelper {

	/**
	 * Reads TransactionJob from file.
	 *
	 * @param objectListener the object listener
	 * @param fileName the file name
	 */
	void readTransactionJobFromFile(String fileName, SerializableObjectListener objectListener);

	/**
	 * Saves TransactionJob to file.
	 *
	 * @param objectProvider the object provider
	 * @param fileName the file name to save to
	 */
	void saveTransactionJobToFile(Iterable<? extends SerializableObject> objectProvider, String fileName);

	/**
	 * Saves JobDescriptor.
	 *
	 * @param jobDescriptor the {@link JobDescriptor} instance
	 * @param fileName the file name.
	 */
	void saveJobDescriptor(JobDescriptor jobDescriptor, String fileName);

}
