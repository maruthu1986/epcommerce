/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.util;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;

import com.google.common.collect.Sets;

/**
 * Utility method for examining {@link javax.mail.Message Email Message} contents.
 */
public final class EmailContentUtil {

	/**
	 * Private constructor.
	 */
	private EmailContentUtil() {
		// cannot instantiate utility class
	}

	/**
	 * <p>
	 * Finds and returns all {@link BodyPart}s of the provided {@link MimeMultipart}.
	 * </p>
	 *
	 * @param mimeMultipart the mime multipart message
	 * @return the matching {@link BodyPart}, or {@code null} if none exists
	 * @throws MessagingException in case of inconsistent {@code mimeMultipart} contents
	 * @throws IOException in case of errors while reading the input {@code mimeMultipart} contents
	 */
	public static Collection<BodyPart> findBodyParts(final MimeMultipart mimeMultipart)
			throws MessagingException, IOException {
		final Set<BodyPart> bodyParts = Sets.newHashSet();

		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			final BodyPart bodyPart = mimeMultipart.getBodyPart(i);

			final Object content = bodyPart.getContent();

			if (content.getClass().isAssignableFrom(MimeMultipart.class)) {
				bodyParts.addAll(findBodyParts((MimeMultipart) content));
			} else {
				bodyParts.add(bodyPart);
			}
		}

		return bodyParts;
	}

	/**
	 * <p>
	 * Finds and returns the {@link BodyPart} of the provided {@link MimeMultipart} that has a content type starting with {@code
	 * requiredContentType},
	 * or {@code null} if no such {@code BodyPart} can be found.
	 * </p>
	 * <p>
	 * A {@link BodyPart} is considered a match if its content type begins with {@code requiredContentType} in order to match cases where the
	 * character set is included, for example {@code "text/html; charset=UTF-8"}.
	 * </p>
	 *
	 * @param mimeMultipart the mime multipart message
	 * @param requiredContentType the content type to find
	 * @return the matching {@link BodyPart}, or {@code null} if none exists
	 * @throws MessagingException in case of inconsistent {@code mimeMultipart} contents
	 * @throws IOException in case of errors while reading the input {@code mimeMultipart} contents
	 */
	public static BodyPart findBodyPartByContentType(final MimeMultipart mimeMultipart, final String requiredContentType) throws MessagingException,
			IOException {
		return findBodyParts(mimeMultipart)
				.stream()
				.filter(bodyPart -> bodyPartContentTypeMatches(bodyPart, requiredContentType))
				.findFirst()
				.orElse(null);
	}

	/**
	 * <p>
	 * Finds and returns the contents of a {@link BodyPart} of the provided {@link MimeMultipart} that has a content type starting with
	 * {@code requiredContentType}, or {@code null} if no such {@code BodyPart} can be found.
	 * </p>
	 * <p>
	 * A {@link BodyPart} is considered a match if its content type begins with {@code requiredContentType} in order to match cases where the
	 * character set is included, for example {@code "text/html; charset=UTF-8"}.
	 * </p>
	 *
	 * @param mimeMultipart the mime multipart message
	 * @param requiredContentType the content type to find
	 * @return the String contents of the matching {@link BodyPart}, or {@code null} if none exists
	 * @throws MessagingException in case of inconsistent {@code mimeMultipart} contents
	 * @throws IOException in case of errors while reading the input {@code mimeMultipart} contents
	 */
	public static String findBodyPartContentsByContentType(final MimeMultipart mimeMultipart, final String requiredContentType) throws
			MessagingException, IOException {
		final BodyPart bodyPart = findBodyPartByContentType(mimeMultipart, requiredContentType);

		if (bodyPart == null) {
			return null;
		}

		return bodyPart.getContent().toString();
	}

	/**
	 * <p>
	 * Finds and returns the {@link BodyPart} of the provided {@link MimeMultipart} that has a disposition matching {@code requiredDisposition}, or
	 * {@code null} if no such {@code BodyPart} can be found.
	 * </p>
	 *
	 * @param mimeMultipart the mime multipart message
	 * @param requiredDisposition the disposition to find
	 * @return the matching {@link BodyPart}, or {@code null} if none exists
	 * @throws MessagingException in case of inconsistent {@code mimeMultipart} contents
	 * @throws IOException in case of errors while reading the input {@code mimeMultipart} contents
	 */
	public static Collection<BodyPart> findBodyPartsByDisposition(final MimeMultipart mimeMultipart, final String requiredDisposition) throws
			IOException,
			MessagingException {
		return findBodyParts(mimeMultipart)
				.stream()
				.filter(bodyPart -> bodyPartDispositionMatches(bodyPart, requiredDisposition))
				.collect(toSet());
	}

	private static boolean bodyPartContentTypeMatches(final Part bodyPart, final String requiredContentType) {
		try {
			return bodyPart.getContentType().startsWith(requiredContentType);
		} catch (final MessagingException e) {
			return false;
		}
	}

	private static boolean bodyPartDispositionMatches(final Part bodyPart, final String requiredDisposition) {
		try {
			return Objects.equals(bodyPart.getDisposition(), requiredDisposition);
		} catch (final MessagingException e) {
			return false;
		}
	}

}
