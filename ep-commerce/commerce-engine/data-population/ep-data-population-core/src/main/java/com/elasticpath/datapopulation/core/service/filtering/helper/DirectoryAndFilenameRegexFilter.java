/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering.helper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link java.io.FileFilter} and {@link java.io.FilenameFilter} implementation which matches files based on regular expressions for either the
 * directory name, filename, or both.
 */
public class DirectoryAndFilenameRegexFilter extends AbstractFileFilter {
	private Pattern directoryMatchingPattern;
	private Pattern filenameMatchingPattern;

	/**
	 * Default constructor with no directory or filename matching regular expressions set.
	 */
	public DirectoryAndFilenameRegexFilter() {
		this(null, null);
	}

	/**
	 * Constructor which takes in regular expression Strings for matching directory names and file names respectively.
	 *
	 * @param directoryMatchingRegex the regular expression for matching directory names; may be null and then all directories are matched.
	 * @param filenameMatchingRegex  the regular expression for matching file names; may be null and then all files are matched.
	 */
	public DirectoryAndFilenameRegexFilter(final String directoryMatchingRegex, final String filenameMatchingRegex) {
		this.directoryMatchingPattern = createPattern(directoryMatchingRegex);
		this.filenameMatchingPattern = createPattern(filenameMatchingRegex);
	}

	/**
	 * Accepts the given {@link File} if either:
	 * <ol>
	 * <li>It represents a directory and the directory name matches the directory name {@link Pattern} (see
	 * {@link #getDirectoryMatchingPattern()}) or no directory name {@link Pattern} is configured;</li>
	 * <li>It represents a file and the file name matches the file name {@link Pattern} (see
	 * {@link #getFilenameMatchingPattern()}) or no file name {@link Pattern} is configured.</li>
	 * </ol>
	 *
	 * @param file the file to validate.
	 * @return whether the file is validated, see the rules for validation above.
	 */
	@Override
	public boolean accept(final File file) {
		boolean result = false;

		if (file != null) {
			final File canonicalFile;

			try {
				canonicalFile = file.getCanonicalFile();
			} catch (final IOException e) {
				throw new IllegalArgumentException("Unable to resolve canonical file for file: " + file.getAbsolutePath(), e);
			}

			if (canonicalFile.isDirectory()) {
				result = acceptDirectory(canonicalFile);
			} else {
				result = acceptFile(canonicalFile);
			}
		}

		return result;
	}

	/**
	 * Assumes the {@link File} given is a directory, and gets its file name and passes it to {@link #accept(String, java.util.regex.Pattern)}
	 * passing
	 * in the {@link Pattern} obtained from {@link #getDirectoryMatchingPattern()} also, before returning its result.
	 *
	 * @param directory the directory to validate.
	 * @return true if the directory is valid (as per above), false otherwise.
	 */
	protected boolean acceptDirectory(final File directory) {
		return accept(directory.getName(), getDirectoryMatchingPattern());
	}

	/**
	 * Assumes the {@link File} given is a file, and gets its file name and passes it to {@link #accept(String, java.util.regex.Pattern)} passing
	 * in the {@link Pattern} obtained from {@link #getFilenameMatchingPattern()} also, before returning its result.
	 *
	 * @param file the file to validate.
	 * @return true if the file is valid (as per above), false otherwise.
	 */
	protected boolean acceptFile(final File file) {
		return accept(file.getName(), getFilenameMatchingPattern());
	}

	/**
	 * Accepts the filename if it matches the {@link Pattern} provided, or if no {@link Pattern} was provided.
	 *
	 * @param filename the filename to match.
	 * @param pattern  the pattern to match. Optional, if null, then all filenames are matched and return true.
	 * @return true if the {@link Pattern} matches the filename, or if no {@link Pattern} was provided; false otherwise.
	 */
	protected boolean accept(final String filename, final Pattern pattern) {
		boolean result = true;

		if (pattern != null) {
			result = pattern.matcher(filename).matches();
		}

		return result;
	}

	// Factory methods

	/**
	 * Factory method which creates a {@link Pattern} object for the given regular expression String if not null or blank; otherwise returns null.
	 *
	 * @param regex the regular expression String to create a {@link Pattern}; may be null.
	 * @return a {@link Pattern} object for the given regular expression String if not null or blank; otherwise returns null.
	 */
	private Pattern createPattern(final String regex) {
		Pattern pattern = null;

		if (StringUtils.isNotBlank(regex)) {
			pattern = Pattern.compile(regex);
		}

		return pattern;
	}

	// Getters and Setters

	/**
	 * Gets the {@link Pattern} for matching directory names; may be null if all directories are accepted.
	 *
	 * @return the {@link Pattern} for matching directory names; may be null if all directories are accepted.
	 */
	protected Pattern getDirectoryMatchingPattern() {
		return this.directoryMatchingPattern;
	}

	/**
	 * Sets the {@link Pattern} for matching directory names; may be null if all directories are accepted.
	 *
	 * @param directoryMatchingPattern the {@link Pattern} for matching directory names; may be null if all directories are accepted.
	 */
	public void setDirectoryMatchingPattern(final Pattern directoryMatchingPattern) {
		this.directoryMatchingPattern = directoryMatchingPattern;
	}

	/**
	 * Sets a {@link Pattern} corresponding to the regular expression passed in for matching directory names; may be null if all directories
	 * are accepted.
	 *
	 * @param directoryMatchingRegex the regular expression passed in for matching directory names; may be null if all directories
	 *                               are accepted.
	 */
	public void setDirectoryMatchingRegex(final String directoryMatchingRegex) {
		setDirectoryMatchingPattern(createPattern(directoryMatchingRegex));
	}

	/**
	 * Gets the {@link Pattern} for matching filenames; may be null if all files are accepted.
	 *
	 * @return the {@link Pattern} for matching filenames; may be null if all files are accepted.
	 */
	protected Pattern getFilenameMatchingPattern() {
		return this.filenameMatchingPattern;
	}

	/**
	 * Sets the {@link Pattern} for matching filenames; may be null if all files are accepted.
	 *
	 * @param filenameMatchingPattern the {@link Pattern} for matching directory names; may be null if all files are accepted.
	 */
	public void setFilenameMatchingPattern(final Pattern filenameMatchingPattern) {
		this.filenameMatchingPattern = filenameMatchingPattern;
	}

	/**
	 * Sets a {@link Pattern} corresponding to the regular expression passed in for matching filenames; may be null if all files
	 * are accepted.
	 *
	 * @param filenameMatchingRegex the regular expression passed in for matching directory names; may be null if all files
	 *                              are accepted.
	 */
	public void setFilenameMatchingRegex(final String filenameMatchingRegex) {
		setFilenameMatchingPattern(createPattern(filenameMatchingRegex));
	}
}
