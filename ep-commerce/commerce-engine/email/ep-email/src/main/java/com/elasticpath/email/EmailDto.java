/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * DTO class representing the body and metadata of an email message.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.ShortVariable", "PMD.TooManyMethods"})
public class EmailDto {

	private String contentType;
	private String from;
	private String subject;

	private List<String> to;
	private List<String> cc;
	private List<String> bcc;

	private List<String> replyTo;

	private String htmlBody;
	private String textBody;

	private Collection<String> attachmentUrls;
	private Collection<EmailAttachmentDto> attachments;

	/**
	 * Default constructor.
	 */
	public EmailDto() {
		// empty constructor.
	}

	/**
	 * Constructor.
	 *
	 * @param builder the builder that is used to populate this DTO instance
	 * @param <T> the Builder type
	 */
	public <T extends Builder<T>> EmailDto(final Builder<T> builder) {
		from = builder.from;
		to = builder.tos;
		cc = builder.ccs;
		bcc = builder.bccs;
		replyTo = builder.replyTos;
		contentType = builder.contentType;
		subject = builder.subject;
		htmlBody = builder.htmlBody;
		textBody = builder.textBody;
		attachmentUrls = builder.attachmentUrls;
		attachments = builder.attachments;
	}

	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setFrom(final String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	@JsonSetter
	public void setTo(final List<String> to) {
		this.to = to;
	}

	public void setTo(final String... to) {
		this.to = Lists.newArrayList(to);
	}

	public List<String> getTo() {
		return to;
	}

	@JsonSetter
	public void setCc(final List<String> cc) {
		this.cc = cc;
	}

	public void setCc(final String... cc) {
		this.cc = Lists.newArrayList(cc);
	}

	public List<String> getCc() {
		return cc;
	}

	@JsonSetter
	public void setBcc(final List<String> bcc) {
		this.bcc = bcc;
	}

	public void setBcc(final String... bcc) {
		this.bcc = Lists.newArrayList(bcc);
	}

	public List<String> getBcc() {
		return bcc;
	}

	@JsonSetter
	public void setReplyTo(final List<String> replyTo) {
		this.replyTo = replyTo;
	}

	public void setReplyTo(final String... replyTo) {
		this.replyTo = Lists.newArrayList(replyTo);
	}

	public List<String> getReplyTo() {
		return replyTo;
	}

	public void setHtmlBody(final String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setTextBody(final String textBody) {
		this.textBody = textBody;
	}

	public String getTextBody() {
		return textBody;
	}

	public Collection<String> getAttachmentUrls() {
		return attachmentUrls;
	}

	public void setAttachmentUrls(final Collection<String> attachmentUrls) {
		this.attachmentUrls = attachmentUrls;
	}

	public Collection<EmailAttachmentDto> getAttachments() {
		return attachments;
	}

	public void setAttachments(final Collection<EmailAttachmentDto> attachments) {
		this.attachments = attachments;
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

	/**
	 * Factory method to return a Builder capable of producing new EmailDto instances.
	 * 
	 * @return a Builder
	 */
	public static Builder<?> builder() {
		return new Builder<>();
	}

	/**
	 * Builder class that constructs {@link EmailDto} instances.
	 *
	 * @param <T> Builder type
	 */
	public static class Builder<T extends Builder<T>> {
		private final List<String> tos = new ArrayList<>();
		private final List<String> ccs = new ArrayList<>();
		private final List<String> bccs = new ArrayList<>();
		private final List<String> replyTos = new ArrayList<>();
		private final Collection<String> attachmentUrls = new ArrayList<>();
		private final Collection<EmailAttachmentDto> attachments = new ArrayList<>();

		private String contentType;
		private String from;
		private String subject;

		private String htmlBody;
		private String textBody;

		/**
		 * Returns this instance of the builder.
		 *
		 * @return this builder instance
		 */
		@SuppressWarnings("unchecked")
		protected T self() {
			return (T) this;
		}

		/**
		 * Specifies an EmailDto instance to use a prototype.
		 *
		 * @param dto the prototype EmailDto
		 * @return the builder
		 */
		public T fromPrototype(final EmailDto dto) {
			withFrom(dto.getFrom());
			withTo(dto.getTo());
			withCc(dto.getCc());
			withBcc(dto.getBcc());
			withReplyTo(dto.getReplyTo());
			withContentType(dto.getContentType());
			withSubject(dto.getSubject());
			withHtmlBody(dto.getHtmlBody());
			withTextBody(dto.getTextBody());
			withAttachmentUrls(dto.getAttachmentUrls());
			withAttachments(dto.getAttachments());

			return self();
		}

		/**
		 * Builds an {@link EmailDto} instance.
		 * 
		 * @return an {@link EmailDto} instance.
		 */
		public EmailDto build() {
			return new EmailDto(this);
		}

		/**
		 * Adds a BCC address.
		 * 
		 * @param bcc the address
		 * @return the builder
		 */
		public T addBcc(final String bcc) {
			this.bccs.add(bcc);
			return self();
		}

		/**
		 * Sets the BCC addresses.
		 * 
		 * @param bccs the addresses
		 * @return the builder
		 */
		public T withBcc(final String... bccs) {
			this.bccs.clear();
			this.bccs.addAll(Lists.newArrayList(bccs));
			return self();
		}

		/**
		 * Sets the BCC addresses.
		 * 
		 * @param bccs the addresses
		 * @return the builder
		 */
		public T withBcc(final List<String> bccs) {
			this.bccs.clear();
			this.bccs.addAll(bccs);
			return self();
		}

		/**
		 * Adds a CC address.
		 * 
		 * @param cc the address
		 * @return the builder
		 */
		public T addCc(final String cc) {
			this.ccs.add(cc);
			return self();
		}

		/**
		 * Sets the CC addresses.
		 *
		 * @param ccs the addresses
		 * @return the builder
		 */
		public T withCc(final String... ccs) {
			this.ccs.clear();
			this.ccs.addAll(Lists.newArrayList(ccs));
			return self();
		}

		/**
		 * Sets the addresses.
		 *
		 * @param ccs the addresses
		 * @return the builder
		 */
		public T withCc(final List<String> ccs) {
			this.ccs.clear();
			this.ccs.addAll(ccs);
			return self();
		}

		/**
		 * Adds a recipient address.
		 *
		 * @param to the address
		 * @return the builder
		 */
		public T addTo(final String to) {
			this.tos.add(to);
			return self();
		}

		/**
		 * Sets the recipient addresses.
		 * 
		 * @param tos the addresses
		 * @return the builder
		 */
		public T withTo(final String... tos) {
			this.tos.clear();
			this.tos.addAll(Lists.newArrayList(tos));
			return self();
		}

		/**
		 * Sets the recipient addresses.
		 * 
		 * @param tos the addresses
		 * @return the builder
		 */
		public T withTo(final List<String> tos) {
			this.tos.clear();
			this.tos.addAll(tos);
			return self();
		}

		/**
		 * Adds a Reply-To address.
		 * 
		 * @param replyTo the address
		 * @return the builder
		 */
		public T addReplyTo(final String replyTo) {
			this.replyTos.add(replyTo);
			return self();
		}

		/**
		 * Sets the Reply-To addresses.
		 * 
		 * @param replyTos the addresses
		 * @return the builder
		 */
		public T withReplyTo(final String... replyTos) {
			this.replyTos.clear();
			this.replyTos.addAll(Lists.newArrayList(replyTos));
			return self();
		}

		/**
		 * Sets the Reply-To addresses.
		 * 
		 * @param replyTos the addresses
		 * @return the builder
		 */
		public T withReplyTo(final List<String> replyTos) {
			this.replyTos.clear();
			this.replyTos.addAll(replyTos);
			return self();
		}

		/**
		 * Sets the Content-Type.
		 * 
		 * @param contentType the content type
		 * @return the builder
		 */
		public T withContentType(final String contentType) {
			this.contentType = contentType;
			return self();
		}

		/**
		 * Sets the from address.
		 * 
		 * @param from the from address
		 * @return the builder
		 */
		public T withFrom(final String from) {
			this.from = from;
			return self();
		}

		/**
		 * Sets the HTML body.
		 * 
		 * @param htmlBody the HTML body
		 * @return the builder
		 */
		public T withHtmlBody(final String htmlBody) {
			this.htmlBody = htmlBody;
			return self();
		}

		/**
		 * Sets the subject.
		 * 
		 * @param subject the subject
		 * @return the builder
		 */
		public T withSubject(final String subject) {
			this.subject = subject;
			return self();
		}

		/**
		 * Sets the text body.
		 * 
		 * @param textBody the text body
		 * @return the builder
		 */
		public T withTextBody(final String textBody) {
			this.textBody = textBody;
			return self();
		}

		/**
		 * Adds an attachment URL.
		 *
		 * @param attachmentUrl the attachment URL
		 * @return the builder
		 */
		public T addAttachmentUrl(final String attachmentUrl) {
			this.attachmentUrls.add(attachmentUrl);
			return self();
		}

		/**
		 * Adds attachment URLs.
		 *
		 * @param attachmentUrls the attachment URLs
		 * @return the builder
		 */
		public T withAttachmentUrls(final String... attachmentUrls) {
			this.attachmentUrls.clear();
			this.attachmentUrls.addAll(Lists.newArrayList(attachmentUrls));
			return self();
		}

		/**
		 * Sets the attachment URLs.
		 *
		 * @param attachmentUrls the attachment URLs
		 * @return the builder
		 */
		public T withAttachmentUrls(final Collection<String> attachmentUrls) {
			this.attachmentUrls.clear();
			this.attachmentUrls.addAll(attachmentUrls);
			return self();
		}

		/**
		 * Adds an attachment.
		 *
		 * @param filename the name of the file being attached
		 * @param data the data of the attachment
		 * @param contentType the content type of the attachment
		 * @return the builder
		 */
		public T addAttachment(final String filename, final byte[] data, final String contentType) {
			this.attachments.add(new EmailAttachmentDto(filename, data, contentType));
			return self();
		}

		/**
		 * Adds an attachment.
		 *
		 * @param emailAttachmentDto the attachment to add
		 * @return the builder
		 */
		public T addAttachment(final EmailAttachmentDto emailAttachmentDto) {
			this.attachments.add(emailAttachmentDto);
			return self();
		}

		/**
		 * Sets attachments.
		 *
		 * @param attachments the attachments
		 * @return the builder
		 */
		public T withAttachments(final Collection<EmailAttachmentDto> attachments) {
			this.attachments.clear();
			this.attachments.addAll(attachments);
			return self();
		}

		/**
		 * Sets attachments.
		 *
		 * @param attachments the attachments
		 * @return the builder
		 */
		public T withAttachments(final EmailAttachmentDto... attachments) {
			this.attachments.clear();
			this.attachments.addAll(Sets.newHashSet(attachments));
			return self();
		}
	}

}
