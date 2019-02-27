/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.handlers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rap.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.commons.util.AssetRepository;

/**
 * The extension of {@link DiskFileUploadReceiver} with a difference that files are uploaded to
 * <strong>import asset</strong> directory.
 */
public class CsvFileUploadReceiver extends DiskFileUploadReceiver {

	private static final Logger LOG = Logger.getLogger(CsvFileUploadReceiver.class);
	private final AssetRepository assetRepository = ServiceLocator.getService("assetRepository");

	private final Display display;

	/**
	 * Custom constructor.
	 *
	 * @param display the display.
	 */
	public CsvFileUploadReceiver(final Display display) {
		this.display = display;
	}

	@Override
	public void receive(final InputStream dataStream, final FileDetails details) throws IOException {
		super.receive(dataStream, details);

		//clean old files
		removeOldUploadedFiles();
	}

	@Override
	protected File createTargetFile(final FileDetails details) throws IOException {
		String fileName = "upload.tmp";
		if (details != null && details.getFileName() != null) {
			fileName = details.getFileName();
		}

		File result = new File(createTempAssetDirectory(), fileName);
		if (result.createNewFile()) {
			return result;
		}

		LOG.error("File [" + fileName + "] couldn't be created");
		return null;
	}

	private File createTempAssetDirectory() throws IOException {
		String assetImportPath = assetRepository.getImportAssetPath();

		try {
			return Files.createTempDirectory(Paths.get(assetImportPath), "fileupload_").toFile();
		} catch (NoSuchFileException nsfe) {
			display.syncExec(() -> showErrorMessageDialog("Unable to create temp directory: " + nsfe.getMessage()));
			LOG.error("Unable to create temp directory: " + nsfe.getMessage());

		} catch (Exception exc) {
			display.syncExec(() -> showErrorMessageDialog("Error creating temp directory: " + exc.getMessage()));

			LOG.error("Error creating temp directory", exc);
		}

		return null;
	}

	private void showErrorMessageDialog(final String message) {
		MessageDialog.openError(new Shell(display), JobsMessages.get().RunWizard_CsvImportDialogTitle, message);
	}

	/**
	 * Get the last uploaded file.
	 *
	 * @return the last uploaded file.
	 */
	public File getUploadedFile() {
		File[] targetFiles = getTargetFiles();
		//we care only about the last uploaded file - the rest will be deleted and nulled anyway
		return targetFiles[targetFiles.length - 1];
	}

	/**
	 * Get the relative file path, starting from file's parent folder.
	 *
	 * @return the relative file path, made of parent folder name and uploaded file name.
	 */
	public String getRelativePathToUploadedFile() {
		File uploadedFile = getUploadedFile();
		return uploadedFile.getParentFile().getName() + File.separator + uploadedFile.getName();
	}

	/*
		delete previous uploads, in case user mistakenly uploaded one or more wrong files
		the method doesn't remove array elements because doesn't have the access
	 */
	private void removeOldUploadedFiles() {
		File[] targetFiles = getTargetFiles();

		for (int i = 0; i < targetFiles.length - 1; i++) {
			FileUtils.deleteQuietly(targetFiles[i].getParentFile());
			targetFiles[i] = null;
		}
	}
}
