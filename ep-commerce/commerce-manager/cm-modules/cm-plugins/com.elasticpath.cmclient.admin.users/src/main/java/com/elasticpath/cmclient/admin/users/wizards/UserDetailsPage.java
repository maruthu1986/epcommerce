/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.users.wizards;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.admin.users.AdminUsersMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * The User Details wizard page.
 */
public class UserDetailsPage extends AbstractEPWizardPage<CmUser> {
	private static final Logger LOG = Logger.getLogger(UserDetailsPage.class);

	private final CmUser cmUser;

	private UserDetailsProfileSectionPart userProfileSectionPart;

	/**
	 * Constructor.
	 *
	 * @param pageName the page name
	 * @param title    the page title
	 * @param message  the message
	 * @param cmUser   the <code>CmUser</code> to show on this page. If null, a new one will be created.
	 */
	protected UserDetailsPage(final String pageName, final String title, final String message,
		final CmUser cmUser) {
		super(1, false, pageName, title, message, new DataBindingContext());
		this.cmUser = cmUser;
	}

	/**
	 * Return whether this is a CreateUser page or an EditUser page.
	 *
	 * @return true if a new user, false if an existing one
	 */
	protected boolean isNewUser() {
		return !this.cmUser.isPersisted();
	}

	/**
	 * Create the wizard's page composite.
	 *
	 * @param parent the page's parent
	 */
	public void createControl(final Composite parent) {
		IManagedForm managedForm = this.createManagedForm(parent);

		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		final int margin = 10;
		layout.leftMargin = margin;
		layout.rightMargin = margin;
		ScrolledForm scrolledForm = managedForm.getForm();
		scrolledForm.getBody().setLayout(layout);

		userProfileSectionPart = new UserDetailsProfileSectionPart(this, scrolledForm.getBody(), managedForm.getToolkit(), this
			.getDataBindingContext());
		managedForm.addPart(userProfileSectionPart);
		// managedForm.addPart(new UserDetailsTypeSectionPart(this, scrolledForm.getBody(), managedForm.getToolkit(), this
		// .getDataBindingContext()));
		/* MUST be called */

		EpWizardPageSupport.create(this, getDataBindingContext());
		this.setControl(scrolledForm);
	}

	private IManagedForm createManagedForm(final Composite parent) {
		FormToolkit toolkit = EpControlFactory.getInstance().createFormToolkit();
		ScrolledForm scrolledForm = toolkit.createScrolledForm(parent);
		IManagedForm managedForm = new ManagedForm(toolkit, scrolledForm);
		scrolledForm.getBody().setLayout(new FillLayout());
		return managedForm;
	}

	/**
	 * Gets the CM user.
	 *
	 * @return {@link CmUser}
	 */
	protected CmUser getCmUser() {
		return cmUser;
	}

	@Override
	protected void bindControls() {
		// Do nothing
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		// Do nothing
	}

	@Override
	protected void populateControls() {
		// Do nothing
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		return processUserParametersValidation();
	}

	/**
	 * Processes the validation of user parameters such as user name, and user email.
	 *
	 * @return true if user parameters are valid and false otherwise
	 */
	public boolean processUserParametersValidation() {
		if (userNameExists()) {
			setErrorMessage(AdminUsersMessages.get().UserNameExists);
			return false;
		}
		if (emailExists()) {
			setErrorMessage(AdminUsersMessages.get().EmailExists);
			return false;
		}
		return true;
	}

	private boolean userNameExists() {
		final CmUserService service = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);

		LOG.debug("Checking if UserName exists"); //$NON-NLS-1$
		if (service.userNameExists(this.cmUser)) {
			return true;
		}
		return false;
	}

	private boolean emailExists() {
		final CmUserService service = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);

		LOG.debug("Checking if Email exists"); //$NON-NLS-1$
		if (service.emailExists(this.cmUser)) {
			return true;
		}
		return false;
	}

}
