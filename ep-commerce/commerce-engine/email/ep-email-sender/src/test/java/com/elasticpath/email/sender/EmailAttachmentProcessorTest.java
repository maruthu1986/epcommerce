/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.sender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.util.ByteArrayDataSource;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;

import org.apache.camel.Exchange;
import org.apache.camel.ExpectedBodyTypeException;
import org.apache.camel.Message;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.email.EmailAttachmentDto;
import com.elasticpath.email.EmailDto;

/**
 * Test class for {@link EmailAttachmentProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmailAttachmentProcessorTest {

	@InjectMocks
	private EmailAttachmentProcessor processor;

	private final ArgumentCaptor<DataHandler> argumentCaptor = ArgumentCaptor.forClass(DataHandler.class);

	@Test(expected = ExpectedBodyTypeException.class)
	public void verifyExceptionThrownWhenExchangeDoesNotContainAnEmailDto() throws Exception {
		final Exchange exchange = createExchangeWithBody(new Object());

		processor.process(exchange);
	}

	@Test
	public void verifyAttachmentLoadedFromFilename() throws Exception {
		final String attachmentName = "attachment1.txt";
		final String attachmentUrl = getUrlForResourceName(attachmentName);

		final EmailDto emailDto = createEmailDtoWithAttachments(attachmentUrl);

		final Exchange exchange = createExchangeWithBody(emailDto);

		processor.process(exchange);

		verify(exchange.getIn()).addAttachment(eq(attachmentName), argumentCaptor.capture());

		assertDataSourceMappedToUrl(argumentCaptor.getValue().getDataSource(), attachmentUrl);
	}

	@Test
	public void verifyMultipleAttachmentsCanHaveTheSameFilenameDontEvenWorryAboutItItsCool() throws Exception {
		final String attachmentName = "attachment1.txt";
		final String attachmentUrl = getUrlForResourceName(attachmentName);

		final EmailDto emailDto = createEmailDtoWithAttachments(attachmentUrl, attachmentUrl);

		final Exchange exchange = createExchangeWithBody(emailDto);

		processor.process(exchange);

		verify(exchange.getIn(), times(2)).addAttachment(eq(attachmentName), argumentCaptor.capture());

		argumentCaptor.getAllValues()
				.stream()
				.map(DataHandler::getDataSource)
				.forEach(dataSource -> assertDataSourceMappedToUrl(dataSource, attachmentUrl));
	}

	@Test
	public void verifyCanAttachWebResource() throws Exception {
		final String attachmentUrl = "http://example.org/dontworry/thistest/doesnot/actually/reachout/remotely.txt";
		final String attachmentName = "remotely.txt";

		final EmailDto emailDto = createEmailDtoWithAttachments(attachmentUrl);

		final Exchange exchange = createExchangeWithBody(emailDto);

		processor.process(exchange);

		verify(exchange.getIn()).addAttachment(eq(attachmentName), argumentCaptor.capture());

		assertDataSourceMappedToUrl(argumentCaptor.getValue().getDataSource(), attachmentUrl);
	}

	@Test
	public void verifyCanAttachByteArray() throws Exception {
		final String attachmentName = "attachment.bin";
		final String attachmentContents = "This is my attachment.  There are many like it, but this one is mine.";
		final String mimeType = "text/plain";

		final EmailDto emailDto = createEmailDtoWithAttachment(attachmentName, attachmentContents.getBytes(), mimeType);

		final Exchange exchange = createExchangeWithBody(emailDto);

		processor.process(exchange);

		verify(exchange.getIn()).addAttachment(eq(attachmentName), argumentCaptor.capture());

		assertThat(argumentCaptor.getValue().getDataSource())
				.isInstanceOf(ByteArrayDataSource.class);
	}

	@Test
	public void verifyMultipleAttachmentsAsByteArraysCanAlsoHaveTheSameFilenameWhyNot() throws Exception {
		final String attachmentName = "attachment.bin";
		final String mimeType = "text/plain";
		final String attachmentContents1 = "Attachment contents #1";
		final String attachmentContents2 = "Attachment contents #2";

		final Collection<EmailAttachmentDto> attachments = ImmutableList.of(
				new EmailAttachmentDto(attachmentName, attachmentContents1.getBytes(), mimeType),
				new EmailAttachmentDto(attachmentName, attachmentContents2.getBytes(), mimeType)
		);

		final EmailDto emailDto = EmailDto.builder()
				.withAttachments(attachments)
				.build();

		final Exchange exchange = createExchangeWithBody(emailDto);

		processor.process(exchange);

		verify(exchange.getIn(), times(2)).addAttachment(eq(attachmentName), argumentCaptor.capture());

		argumentCaptor.getAllValues()
				.stream()
				.map(DataHandler::getDataSource)
				.forEach(dataSource -> assertThat(dataSource).isInstanceOf(ByteArrayDataSource.class));
	}

	private String getUrlForResourceName(final String attachmentName) {
		final URL resource = getClass().getClassLoader().getResource(attachmentName);
		assertThat(resource).isNotNull();

		return resource.toString();
	}

	private EmailDto createEmailDtoWithAttachments(final String... attachmentUrls) {
		EmailDto.Builder<?> builder = EmailDto.builder();

		for (final String attachmentUrl : attachmentUrls) {
			builder = builder.addAttachmentUrl(attachmentUrl);
		}

		return builder.build();
	}

	private EmailDto createEmailDtoWithAttachment(final String filename, final byte[] attachmentData, final String contentType) {
		return EmailDto.builder()
				.addAttachment(filename, attachmentData, contentType)
				.build();
	}

	private Exchange createExchangeWithBody(final Object body) {
		final Message message = mock(Message.class);
		final Exchange exchange = mock(Exchange.class);

		when(exchange.getIn()).thenReturn(message);
		when(message.getBody()).thenReturn(body);

		return exchange;
	}

	private void assertDataSourceMappedToUrl(final DataSource dataSource, final String url) {
		assertThat(dataSource)
				.isInstanceOf(URLDataSource.class);

		assertThat(((URLDataSource) dataSource).getURL())
				.hasToString(url);
	}

}