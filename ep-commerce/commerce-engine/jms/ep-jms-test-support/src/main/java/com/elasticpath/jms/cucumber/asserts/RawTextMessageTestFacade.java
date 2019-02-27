/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.jms.cucumber.asserts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

/**
 * Class for testing TextMessage values.
 */
@SuppressWarnings("PMD.UseUtilityClass")
public final class RawTextMessageTestFacade {

	private static final Logger LOGGER = Logger.getLogger(RawTextMessageTestFacade.class);

	/**
	 * Private constructor.
	 */
	private RawTextMessageTestFacade() {

	}

	/**
	 * Verifies that txtMessageList contains a TextMessage which contains the msgValueToVerify.
	 *
	 * @param txtMessageList   the list of message read
	 * @param msgValueToVerify the value to verify in the list of message
	 */
	public static void verifyMessageText(final List<TextMessage> txtMessageList, final String msgValueToVerify) {

		boolean valueExists = false;
		try {
			for (TextMessage txtMessage : txtMessageList) {
				if (txtMessage.getText().contains(msgValueToVerify)) {
					valueExists = true;
					break;
				}
			}
		} catch (JMSException ex) {
			LOGGER.error(ex.getMessage());
		}

		assertThat(valueExists)
				.as("Message value '" + msgValueToVerify + "' doesn't exist")
				.isTrue();

	}

}
