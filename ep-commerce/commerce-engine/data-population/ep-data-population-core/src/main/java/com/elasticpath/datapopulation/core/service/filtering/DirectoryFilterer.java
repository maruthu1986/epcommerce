/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;

import com.elasticpath.datapopulation.core.service.filtering.helper.DirectoryAndFilenameRegexFilter;

/**
 * A class which filters files contained in whole directories based on the given filter information.
 */
public class DirectoryFilterer {
	private final FileFilterer filterer;

	/**
	 * Constructor which takes the {@link FileFilterer} to use to filter the files inside the directories to filter.
	 *
	 * @param fileFilterer the {@link FileFilterer} to use to filter the files inside the directories to filter.
	 */
	public DirectoryFilterer(final FileFilterer fileFilterer) {
		this.filterer = fileFilterer;
	}

	/**
	 * Filters the files in the given input directory and writes the files to the given output directory. The input and output directories can be the
	 * same if the filtering should occur in-situ. If recurse is true, then all sub-directories are also filtered, otherwise only files in the input
	 * directory itself are filtered.
	 *
	 * @param inputDirectory   the input directory to filter.
	 * @param outputDirectory  the directory to write the output files to, under the same directory structure as the input directory; may in fact be
	 *                         the input directory if filtering should occur in-situ.
	 * @param recurse          true if files in the input directory's sub-directories should also be filtered.
	 * @param filterProperties one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public void filterDirectory(final File inputDirectory, final File outputDirectory, final boolean recurse, final Properties... filterProperties)
			throws IOException {
		filterDirectory(inputDirectory, outputDirectory, recurse, null, null, filterProperties);
	}

	/**
	 * Filters the files in the given input directory and writes the files to the given output directory. The input and output directories can be the
	 * same if the filtering should occur in-situ. The files to filter are determined by two regular expressions, one that matches the
	 * sub-directories
	 * to filter, and one that matches the files to filter. These are passed to {@link #createFileFilter(String, String)} which creates a
	 * {@link FileFilter} which is passed along with the other arguments to {@link #filterDirectory(java.io.File, java.io.File, java.io.FileFilter,
	 * boolean, java.util.Properties...)} for processing.
	 *
	 * @param inputDirectory         the input directory to filter.
	 * @param outputDirectory        the directory to write the output files to, under the same directory structure as the input directory; may in
	 *                               fact be
	 *                               the input directory if filtering should occur in-situ.
	 * @param recurse                true if files in the input directory's sub-directories should also be filtered.
	 * @param directoryMatchingRegex a regular expression to use to match which sub-directories should be filtered. Only relevant if recurse is true.
	 *                               May be null, in which case all sub-directories are filtered.
	 * @param filenameMatchingRegex  a regular expression to use to match which sub-directories should be filtered. May be null, in which case all
	 *                               files are filtered in the matched sub-directories.
	 * @param filterProperties       one or more {@link Properties} objects to filter the input file with. They are searched in order for each
	 *                               property
	 *                               placeholder, so the order they are passed to this method is significant if a property is defined in multiple
	 *                               filter
	 *                               {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public void filterDirectory(final File inputDirectory, final File outputDirectory, final boolean recurse, final String directoryMatchingRegex,
								final String filenameMatchingRegex, final Properties... filterProperties) throws IOException {
		final FileFilter fileFilter = createFileFilter(directoryMatchingRegex, filenameMatchingRegex);

		filterDirectory(inputDirectory, outputDirectory, fileFilter, recurse, filterProperties);
	}

	/**
	 * Filters the files in the given input directory and writes the files to the given output directory. The input and output directories can be the
	 * same if the filtering should occur in-situ. The files to filter are determined by the {@link FileFilter} passed in which is used to get the
	 * list of files/directories to process. Directories are only processed and recursed into if the recurse parameter is true.
	 *
	 * @param inputDirectory   the input directory to filter.
	 * @param outputDirectory  the directory to write the output files to, under the same directory structure as the input directory; may in fact be
	 *                         the input directory if filtering should occur in-situ.
	 * @param fileFilter       the {@link FileFilter} to use to get a list of files to process in the input directory, and any sub-directories it
	 *                         returns if
	 *                         recurse is true.
	 * @param recurse          true if files in the input directory's sub-directories should also be filtered.
	 * @param filterProperties one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public void filterDirectory(final File inputDirectory, final File outputDirectory, final FileFilter fileFilter, final boolean recurse,
								final Properties... filterProperties) throws IOException {
		final File[] subInputFiles = inputDirectory.listFiles(fileFilter);

		final FileFilterer fileFilterer = getFilterer();

		for (File subInputFile : subInputFiles) {
			final String subInputFilename = subInputFile.getName();
			final File subOutputFile = new File(outputDirectory, subInputFilename);

			// Use File.getCanonicalFile() to check if it is a file or directory to resolve symlinks on Unix-based systems correctly
			if (subInputFile.getCanonicalFile().isFile()) {
				fileFilterer.filter(subInputFile, subOutputFile, filterProperties);
			} else if (recurse) {
				// Is a directory so recurse as instructed
				filterDirectory(subInputFile, subOutputFile, fileFilter, recurse, filterProperties);
			}
		}
	}

	// Factory methods

	/**
	 * Creates a {@link FileFilter} that filters files and directories based on the regular expressions passed in. Either one can be null, and in
	 * that
	 * case all sub-directories or all files are filtered, respectively.
	 *
	 * @param directoryMatchingRegex a regular expression to use to match which sub-directories should be filtered. Only relevant if recurse is true.
	 *                               May be null, in which case all sub-directories are filtered.
	 * @param filenameMatchingRegex  a regular expression to use to match which sub-directories should be filtered. May be null, in which case all
	 *                               files are filtered in the matched sub-directories.
	 * @return a {@link FileFilter} instance as described above.
	 */
	protected FileFilter createFileFilter(final String directoryMatchingRegex, final String filenameMatchingRegex) {
		return new DirectoryAndFilenameRegexFilter(directoryMatchingRegex, filenameMatchingRegex);
	}

	// Getters

	protected FileFilterer getFilterer() {
		return this.filterer;
	}
}
