/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.client.controller.impl;

import com.elasticpath.tools.sync.client.SyncJobConfiguration;
import com.elasticpath.tools.sync.client.controller.FileSystemHelper;
import com.elasticpath.tools.sync.client.controller.FileSystemHelperFactory;

/**
 * Implementation of {@link FileSystemHelperFactory}.
 */
public class FileSystemHelperFactoryImpl implements FileSystemHelperFactory {

	private int objectsLimitPerFile;
	private boolean addTimestampToFolder;

	@Override
	public FileSystemHelper createFileSystemHelper(final SyncJobConfiguration syncJobConfiguration) {
		return new FileSystemHelperImpl(syncJobConfiguration, getObjectsLimitPerFile(), isAddTimestampToFolder());
	}

	protected int getObjectsLimitPerFile() {
		return objectsLimitPerFile;
	}

	public void setObjectsLimitPerFile(final int objectsLimitPerFile) {
		this.objectsLimitPerFile = objectsLimitPerFile;
	}

	protected boolean isAddTimestampToFolder() {
		return addTimestampToFolder;
	}

	public void setAddTimestampToFolder(final boolean addTimestampToFolder) {
		this.addTimestampToFolder = addTimestampToFolder;
	}

}
