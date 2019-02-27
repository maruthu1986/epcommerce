/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * DTO class representing a file attachment that comprises part of an email message.
 */
public class EmailAttachmentDto {

	private String filename;
	private byte[] data;
	private String mimeType;

	/**
	 * Default constructor.
	 */
	public EmailAttachmentDto() {
		// default constructor required for JSON unmarshalling.
	}

	/**
	 * Constructor.
	 *
	 * @param filename the name of the file being attached
	 * @param data the data of the attachment
	 * @param mimeType the MIME type of the attachment
	 */
	public EmailAttachmentDto(final String filename, final byte[] data, final String mimeType) {
		this.filename = filename;
		this.data = data.clone();
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setData(final byte[] data) {
		this.data = data.clone();
	}

	public byte[] getData() {
		return data.clone();
	}

	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

}
