/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.commons.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.marshalling.XMLMarshaller;
import com.elasticpath.importexport.common.types.JobType;

/**
 * Tool used to marshall {@link Dto}s to an xml file for testing purposes.
 */
public class TestDomainMarshaller {
	private static final String XML_ENCODING_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

	/**
	 * Marshall dto to xml output path.
	 *
	 * @param dto the {@link Dto} to marshall.
	 * @param jobType the job type
	 * @param xmlOutputPath the xml output path
	 *
	 * @throws Exception in case of error during marshalling
	 */
	public void marshall(final Dto dto, final JobType jobType, final String xmlOutputPath) throws Exception {
		XMLMarshaller xmlMarshaller = new XMLMarshaller(dto.getClass());
		File customersXmlFile = new File(xmlOutputPath);
		FileOutputStream outputStream = new FileOutputStream(customersXmlFile);

		PrintStream printStream = new PrintStream(outputStream);
		printStream.println(XML_ENCODING_HEADER);
		printStream.print("<" + jobType.getTagName() + ">");

		xmlMarshaller.marshal(dto, outputStream);
		printStream.print("</" + jobType.getTagName() + ">");
	}

	/**
	 * Marshals an object and outputs to a file.
	 *
	 * @param clazz the class of the object
	 * @param object the object to marshal
	 * @param outputFile the output file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void marshalObject(final Class<?> clazz, final Object object, final File outputFile) throws IOException {
		final FileOutputStream manifestFileOutputStream = new FileOutputStream(outputFile);
		final XMLMarshaller manifestXmlMarshaller = new XMLMarshaller(clazz);
		manifestXmlMarshaller.marshal(object, manifestFileOutputStream);
		manifestFileOutputStream.close();
	}
}
