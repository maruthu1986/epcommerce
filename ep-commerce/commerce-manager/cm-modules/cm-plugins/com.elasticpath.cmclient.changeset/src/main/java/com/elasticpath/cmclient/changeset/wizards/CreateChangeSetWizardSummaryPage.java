/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.wizards;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.changeset.ChangeSet;

/**
 * The summary page for the wizard.
 */
public class CreateChangeSetWizardSummaryPage extends AbstractEPWizardPage<ChangeSet> {

	private static final int TEXT_AREA_HEIGHT_HINT = 150;
	private Text changeSetNameText;
	private Text changeSetDescriptionText;

	/**
	 *
	 * @param pageName the page name
	 */
	protected CreateChangeSetWizardSummaryPage(final String pageName) {
		super(2, false, pageName, new DataBindingContext());
		setDescription(ChangeSetMessages.get().CreateChangeSetWizardSummaryPage_Desc);
		setTitle(ChangeSetMessages.get().CreateChangeSetWizardSummaryPage_Title);
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider provider = EpControlBindingProvider.getInstance();
		DataBindingContext dataBindingContext = getDataBindingContext();
		ChangeSet model = getModel();
		
		provider.bind(dataBindingContext, changeSetNameText, 
				model, "name", EpValidatorFactory.STRING_255_REQUIRED, null, true);  //$NON-NLS-1$
		provider.bind(dataBindingContext, changeSetDescriptionText, 
				model, "description", EpValidatorFactory.MAX_LENGTH_255, null, true); //$NON-NLS-1$
		
		EpWizardPageSupport.create(this, dataBindingContext);

	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, 
				true, false);

		pageComposite.addLabelBoldRequired(ChangeSetMessages.get().ChangeSetEditor_ChangeSet_Name, EpState.EDITABLE, labelData);
		changeSetNameText = pageComposite.addTextField(EpState.EDITABLE, fieldData);
		
		pageComposite.addLabelBold(ChangeSetMessages.get().ChangeSetEditor_ChangeSet_Description, labelData);
		changeSetDescriptionText = pageComposite.addTextArea(EpState.EDITABLE, fieldData);
		
		((GridData) changeSetDescriptionText.getLayoutData()).heightHint = TEXT_AREA_HEIGHT_HINT;
		
		// the control of the page must be set
		setControl(pageComposite.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		// nothing to populate

	}
}
