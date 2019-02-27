/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.importer.unpackager.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;
import com.elasticpath.importexport.importer.unpackager.Unpackager;

/**
 * NullUnpackager just opens entries from separate files without any unpacking.
 */
public class NullUnpackagerImpl implements Unpackager {
	
	private static final Logger LOG = Logger.getLogger(NullUnpackagerImpl.class);

	private Queue<String> queue;

	private File importDirectory;

	/**
	 *
	 * @throws ImportRuntimeException in case of:
	 * <li>There are no more entries</li>
	 * <li>File Not Found</li> 
	 */	
	@Override
	public InputStream nextEntry() {
		String fileName = queue.poll();
		
		if (fileName == null) {
			throw new ImportRuntimeException("IE-30203");
		}
		
		String pathname = importDirectory.getAbsolutePath() + File.separatorChar + fileName;
		
		LOG.info("Processing unpackager file: " + pathname);
		
		final File entryDescriptor = new File(pathname);
		InputStream entryStream = null;
		
		try {
			entryStream = new FileInputStream(entryDescriptor);
		} catch (FileNotFoundException exception) {
			throw new ImportRuntimeException("IE-30204", exception, fileName);
		}
		return entryStream;
	}

	@Override	
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public void initialize(final RetrievalMethod retrievalMethod) {
		importDirectory = retrievalMethod.retrieve();
		if (!importDirectory.isDirectory()) {
			throw new ImportRuntimeException("IE-30200", this.getClass().getName());
		}
		final File[] manifestDescriptor = importDirectory.listFiles(
				new FilenameFilter() {
					@Override
					public boolean accept(final File dir, final String name) {
						return name.equals(Manifest.MANIFEST_XML);
					}
				});
		if (manifestDescriptor == null || manifestDescriptor.length != 1) {
			throw new ImportRuntimeException("IE-30201");
		}
		InputStream manifestStream = null;
		try {
			manifestStream = new FileInputStream(manifestDescriptor[0]);
		} catch (FileNotFoundException exception) {
			throw new ImportRuntimeException("IE-30201", exception);
		}
		final Manifest manifest = new XMLUnmarshaller(Manifest.class).unmarshall(manifestStream);
		queue = new LinkedList<>(manifest.getResources());
		try {
			manifestStream.close();
		} catch (IOException exception) {
			throw new ImportRuntimeException("IE-30202", exception);
		}
	}
}
