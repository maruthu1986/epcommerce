/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors.support;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.formatting.MetadataDateFormat;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.cmclient.core.adapters.EmailHyperlinkAdapter;

/**
 * A page section for displaying info on a change set.
 */
public class ChangeSetInfoEditorPageSummarySection extends AbstractCmClientEditorPageSectionPart {
	private static final Logger LOG = Logger.getLogger(ChangeSetInfoEditorPageSummarySection.class);
	
	private static final int LAYOUT_COLUMNS = 3;

	private Text changeSetNameText;
	private Text addedByText;
	private Text dateAddedText;
	private Text stateText;
	private Hyperlink emailHyperlink;

	private final ChangeSetService changeSetService;

	/**
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public ChangeSetInfoEditorPageSummarySection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
		
		changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		final EpState epState = EpState.READ_ONLY;
		final IEpLayoutComposite composite = CompositeFactory.createGridLayoutComposite(client, 4, false);
		if (isObjectPartOfChangeSet()) {
			IEpLayoutData labelData = composite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
			IEpLayoutData fieldData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, LAYOUT_COLUMNS, 1);
			
			composite.addLabelBold(ChangeSetMessages.get().ChangeSetInfoPage_ChangeSet, labelData);
			changeSetNameText = composite.addTextField(epState, fieldData);
			
			composite.addLabelBold(ChangeSetMessages.get().ChangeSetInfoPage_ChangeSet_State, labelData);
			stateText = composite.addTextField(EpState.READ_ONLY, fieldData);
			
			composite.addLabelBold(ChangeSetMessages.get().ChangeSetInfoPage_AddedBy, labelData);
			addedByText = composite.addTextField(epState, null);
			
			final Image emailImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EMAIL_SEND);
			final Label emailLabel = composite.addLabel(StringUtils.EMPTY, null);
			emailLabel.setImage(emailImage);

			emailHyperlink = composite.addHyperLinkText(StringUtils.EMPTY, EpState.EDITABLE, null);

			composite.addLabelBold(ChangeSetMessages.get().ChangeSetInfoPage_DateAdded, labelData);
			dateAddedText = composite.addTextField(epState, fieldData);
		} else {
			composite.addLabel(ChangeSetMessages.get().ChangeSetInfoPage_NoChangeSetMessage, null);
		}
	}

	/**
	 * Checks whether an object is part of a change set.
	 * 
	 * @return true if the underlying object is part of a change set
	 */
	private boolean isObjectPartOfChangeSet() {
		ChangeSetObjectStatus status = changeSetService.getStatus(getDependentObject());
		return status.isLocked();
	}

	@Override
	protected void populateControls() {
		if (isObjectPartOfChangeSet()) {
			ChangeSet changeSet = changeSetService.findChangeSet(getDependentObject());
			
			changeSetNameText.setText(changeSet.getName());
			Map<String, String> metadata = changeSetService.findChangeSetMemberMetadata(changeSet.getGuid(), getDependentObject());
			if (metadata == null) {
				return;
			}
			
			final CmUser cmUser = getAddedBy(metadata);

			addedByText.setText(getAddedByName(cmUser));
			
			final String email = getAddedByEmail(cmUser);
			emailHyperlink.setText(email);

			emailHyperlink.addHyperlinkListener(new EmailHyperlinkAdapter());

			final String dateAdded = getDateAdded(metadata);
			dateAddedText.setText(dateAdded);
			
			stateText.setText(ChangeSetMessages.get().getMessage(changeSet.getStateCode()));
		}
	}

	/**
	 * Gets the user's email who added the object.
	 * 
	 * @param cmUser the CM user
	 * @return the user's name or NA
	 */
	protected String getAddedByEmail(final CmUser cmUser) {
		if (cmUser != null) {
			return cmUser.getEmail();
		}
		return ChangeSetMessages.get().NotAvailable;
	}

	/**
	 * Gets the user who added the object.
	 * 
	 * @param cmUser the CM user
	 * @return the user's name or NA
	 */
	protected String getAddedByName(final CmUser cmUser) {
		if (cmUser != null) {
			return cmUser.getLastName() + ", " + cmUser.getFirstName(); //$NON-NLS-1$
		}
		return ChangeSetMessages.get().NotAvailable;
	}
	/**
	 * Gets the cm user who added the model object to a change set.
	 * 
	 * @param metadata the metadata
	 * @return the CmUser instance
	 */
	protected CmUser getAddedBy(final Map<String, String> metadata) {
		String cmUserGuid = metadata.get("addedByUserGuid"); //$NON-NLS-1$
		if (cmUserGuid != null) {
			CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
			return cmUserService.findByGuid(cmUserGuid);
		}
		return null;
	}

	/**
	 * Gets the date when the object was added to a change set.
	 * 
	 * @param metadata the metadata
	 * @return a Date
	 */
	protected String getDateAdded(final Map<String, String> metadata) {
		String dateAdded = metadata.get("dateAdded"); //$NON-NLS-1$
		if (dateAdded != null) {
			try {
				Date date = new MetadataDateFormat().parse(dateAdded);
				return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
			} catch (ParseException e) {
				LOG.error("Could not parse date " + dateAdded, e); //$NON-NLS-1$
			}
		}
		return ChangeSetMessages.get().NotAvailable;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// editing is not supported
	}

}
