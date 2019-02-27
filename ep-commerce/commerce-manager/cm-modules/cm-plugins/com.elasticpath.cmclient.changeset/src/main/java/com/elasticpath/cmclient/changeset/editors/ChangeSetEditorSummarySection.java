/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors;

import java.util.Date;

import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.helpers.UserViewFormatter;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.cmclient.core.adapters.EmailHyperlinkAdapter;

/**
 * The summary section UI.
 */
public class ChangeSetEditorSummarySection extends AbstractPolicyAwareEditorPageSectionPart {

	private static final int COLUMNS = 4;

	private static final int TEXT_AREA_HEIGHT_HINT = 100;

	private static final int LAYOUT_COLUMNS = 3;

	private Text changeSetGuidText;
	private Text changeSetNameText;
	private Text changeSetDescriptionText;
	private IPolicyTargetLayoutComposite mainComposite;

	private Text createdByText;

	private Hyperlink emailHyperlink;

	private Text dateCreatedText;

	private Text stateText;

	private final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);

	/**
	 * Constructs a new section.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public ChangeSetEditorSummarySection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider provider = EpControlBindingProvider.getInstance();

		provider.bind(bindingContext, changeSetNameText,
				getModel(), "name", EpValidatorFactory.STRING_255_REQUIRED, null, true);  //$NON-NLS-1$
		provider.bind(bindingContext, changeSetDescriptionText,
				getModel(), "description", EpValidatorFactory.MAX_LENGTH_255, null, true); //$NON-NLS-1$

	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		mainComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory.createGridLayoutComposite(client, COLUMNS, false));
		mainComposite.getSwtComposite().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		PolicyActionContainer summaryControlsContainer = addPolicyActionContainer("summaryControls"); //$NON-NLS-1$
		PolicyActionContainer infoControlsContainer = addPolicyActionContainer("infoControls"); //$NON-NLS-1$
		PolicyActionContainer emailControlsContainer = addPolicyActionContainer("emailControls"); //$NON-NLS-1$

		final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, false, LAYOUT_COLUMNS, 1);

		mainComposite.addLabelBold(ChangeSetMessages.get().ChangeSetEditor_ChangeSet_Guid, labelData, summaryControlsContainer);
		changeSetGuidText = mainComposite.addTextField(fieldData, infoControlsContainer);

		mainComposite.addLabelBoldRequired(ChangeSetMessages.get().ChangeSetEditor_ChangeSet_Name, labelData, summaryControlsContainer);
		changeSetNameText = mainComposite.addTextField(fieldData, summaryControlsContainer);

		mainComposite.addLabelBold(ChangeSetMessages.get().ChangeSetEditor_ChangeSet_Description, labelData, summaryControlsContainer);
		changeSetDescriptionText = mainComposite.addTextArea(true, false,
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, LAYOUT_COLUMNS, 1), summaryControlsContainer);
		((GridData) changeSetDescriptionText.getLayoutData()).heightHint = TEXT_AREA_HEIGHT_HINT;

		mainComposite.addLabelBold(ChangeSetMessages.get().ChangeSetEditor_CreatedBy, labelData, infoControlsContainer);
		createdByText = mainComposite.addTextField(null, infoControlsContainer);

		final Label emailLabel = mainComposite.addLabel(StringUtils.EMPTY, null, emailControlsContainer);
		emailLabel.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EMAIL_SEND));

		emailHyperlink = mainComposite.addHyperLinkText(StringUtils.EMPTY, null, emailControlsContainer);

		mainComposite.addLabelBold(ChangeSetMessages.get().ChangeSetEditor_DateCreated, labelData, infoControlsContainer);
		dateCreatedText = mainComposite.addTextField(fieldData, infoControlsContainer);

		mainComposite.addLabelBold(ChangeSetMessages.get().ChangeSetEditor_ChangeSet_State, labelData, infoControlsContainer);
		stateText = mainComposite.addTextField(fieldData, infoControlsContainer);
		
		addCompositesToRefresh(mainComposite.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		ChangeSet changeSet = (ChangeSet) getModel();

		if (changeSet.getGuid() != null) {
			changeSetGuidText.setText(changeSet.getGuid());
		}
		if (changeSet.getName() != null) {
			changeSetNameText.setText(changeSet.getName());
		}
		if (changeSet.getDescription() != null) {
			changeSetDescriptionText.setText(changeSet.getDescription());
		}

		final CmUser cmUser = getCreatedBy((ChangeSet) getModel());

		createdByText.setText(getCreatedByName(cmUser));

		final String email = getCreatedByEmail(cmUser);
		emailHyperlink.setText(email);
		emailHyperlink.addHyperlinkListener(new EmailHyperlinkAdapter());

		final String dateAdded = getDateCreated((ChangeSet) getModel());
		dateCreatedText.setText(dateAdded);

		stateText.setText(ChangeSetMessages.get().getMessage(changeSet.getStateCode()));

		mainComposite.setControlModificationListener(getEditor());
	}

	/**
	 * Gets the user's email who created the change set.
	 *
	 * @param cmUser the CM user
	 * @return the user's name or NA
	 */
	protected String getCreatedByEmail(final CmUser cmUser) {
		if (cmUser != null) {
			return cmUser.getEmail();
		}
		return ChangeSetMessages.get().NotAvailable;
	}

	/**
	 * Gets the user who created the change set.
	 *
	 * @param cmUser the CM user
	 * @return the user's name or NA
	 */
	protected String getCreatedByName(final CmUser cmUser) {
		if (cmUser != null) {
			return UserViewFormatter.formatWithName(cmUser);
		}
		return ChangeSetMessages.get().NotAvailable;
	}
	/**
	 * Gets the cm user who created the change set.
	 *
	 * @param changeSet the metadata
	 * @return the CmUser instance
	 */
	protected CmUser getCreatedBy(final ChangeSet changeSet) {
		if (changeSet.getCreatedByUserGuid() == null) {
			return LoginManager.getCmUser();
		}

		CmUser createdByCmUser = cmUserService.findByGuid(changeSet.getCreatedByUserGuid());
		if (createdByCmUser == null) {
			return LoginManager.getCmUser();
		}
		return createdByCmUser;
	}

	/**
	 * Gets the date when the change set was created.
	 *
	 * @param changeSet the change set
	 * @return a Date
	 */
	protected String getDateCreated(final ChangeSet changeSet) {
		Date date = changeSet.getCreatedDate();
		if (date == null) {
			date = new Date();
		}
		return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
	}
}
