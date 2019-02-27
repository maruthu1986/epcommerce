/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.email.handler.cmuser.helper.impl;

import java.util.Locale;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.cmuser.helper.CmUserEmailPropertyHelper;

/**
 * Helper for processing email properties for CmUser e-mails.
 */
public class CmUserEmailPropertyHelperImpl extends AbstractEpDomainImpl implements CmUserEmailPropertyHelper {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private static final String CREATE_PWD_EMAIL_TXT_TEMPLATE = "cmCreatePassword.txt";

	private static final String RESET_PWD_EMAIL_TXT_TEMPLATE = "cmResetPassword.txt";

	private static final String CHANGE_PASSWORD_TEMPLATE = "cmChangePassword.txt";

	@Override
	public EmailProperties getCreateEmailProperties(final CmUser cmUser, final String newPassword, final Locale locale) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("cmUser", cmUser);
		emailProperties.getTemplateResources().put("newPassword", newPassword);
		emailProperties.setDefaultSubject("New Elastic Path Commerce Account");
		emailProperties.setLocaleDependentSubjectKey("cm.passwordCreate.emailSubject");
		emailProperties.setEmailLocale(locale);
		emailProperties.setTextTemplate(CREATE_PWD_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(cmUser.getEmail());
		emailProperties.setStoreCode(null);

		return emailProperties;
	}

	@Override
	public EmailProperties getResetEmailProperties(final CmUser cmUser, final String newPassword, final Locale locale) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("cmUser", cmUser);
		emailProperties.getTemplateResources().put("newPassword", newPassword);
		emailProperties.setDefaultSubject("Elastic Path Commerce Password Reset");
		emailProperties.setLocaleDependentSubjectKey("cm.passwordReset.emailSubject");
		emailProperties.setEmailLocale(locale);
		emailProperties.setTextTemplate(RESET_PWD_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(cmUser.getEmail());
		emailProperties.setStoreCode(null);

		return emailProperties;
	}

	@Override
	public EmailProperties getChangePasswordEmailProperties(final CmUser cmUser, final String newPassword, final Locale locale) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.setRecipientAddress(cmUser.getEmail());
		emailProperties.getTemplateResources().put("locale", locale);
		emailProperties.setStoreCode(null); // use a global template
		emailProperties.setTextTemplate(CHANGE_PASSWORD_TEMPLATE);
		emailProperties.getTemplateResources().put("cmUser", cmUser);
		emailProperties.getTemplateResources().put("newPassword", newPassword);

		emailProperties.setDefaultSubject("Elastic Path Commerce Password Changed");
		emailProperties.setEmailLocale(locale);
		emailProperties.setLocaleDependentSubjectKey("cm.passwordChange.emailSubject");

		return emailProperties;
	}

	/**
	 * @return
	 */
	private EmailProperties getEmailPropertiesBeanInstance() {
		return getBean(ContextIdNames.EMAIL_PROPERTIES);
	}

}
