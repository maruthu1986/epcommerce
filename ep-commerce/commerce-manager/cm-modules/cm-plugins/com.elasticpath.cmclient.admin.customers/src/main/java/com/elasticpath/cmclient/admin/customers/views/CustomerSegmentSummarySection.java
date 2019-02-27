/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.admin.customers.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.springframework.util.StringUtils;

import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.domain.customer.CustomerGroup;

/**
 * Customer segment summary section.
 */
public class CustomerSegmentSummarySection extends AbstractCmClientEditorPageSectionPart {

	private final transient ControlModificationListener listener;

	private transient IEpLayoutComposite mainPane;

	private transient EpState authorization;

	private transient Text customerSegmentNameField;
	
	private transient Text customerSegmentDescriptionField;
	
	private transient Button customerSegmentEnabledCheckbox;

	/**
	 * Construct the CSR customer segments section.
	 * 
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CustomerSegmentSummarySection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.listener = editor;
	}
	
	/**
	 * Gets the section title.
	 * 
	 * @return string
	 */
	@Override
	protected String getSectionTitle() {
		return AdminCustomersMessages.get().CustomerSegmentEditor_SummaryPage_Details;
	}

	/**
	 * Creates the customer segment controls in the editor.
	 * 
	 * @param parentComposite the composite
	 * @param toolkit the form toolkit
	 */
	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		setAuthorization();

		mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		mainPane.setLayoutData(data);

		final IEpLayoutData labelData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = this.mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData checkBoxData = this.mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER);

		mainPane.addLabelBoldRequired(AdminCustomersMessages.get().CustomerSegmentName, EpState.EDITABLE, labelData);
		customerSegmentNameField = mainPane.addTextField(EpState.EDITABLE, fieldData);
		
		mainPane.addLabelBoldRequired(AdminCustomersMessages.get().CustomerSegmentDescription, EpState.EDITABLE, labelData);
		customerSegmentDescriptionField = mainPane.addTextField(EpState.EDITABLE, fieldData);

		mainPane.addLabelBold(AdminCustomersMessages.get().CustomerSegmentEnabledFlag, labelData);
		customerSegmentEnabledCheckbox = mainPane.addCheckBoxButton("", EpState.EDITABLE, checkBoxData); //$NON-NLS-1$
	}
	
	private void setAuthorization() {
		if (AdminCustomersPlugin.isSegmentsAuthorized()) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}		
	}

	@Override
	protected void populateControls() {
		CustomerGroup customerGroup = (CustomerGroup) getEditor().getModel();
		
		customerSegmentNameField.setEnabled(isEditable() && !StringUtils.hasText(customerGroup.getName()));
		customerSegmentNameField.setText(safeDisplayString(customerGroup.getName()));
		
		customerSegmentDescriptionField.setText(safeDisplayString(customerGroup.getDescription()));
		customerSegmentDescriptionField.setEnabled(isEditable());
		
		customerSegmentEnabledCheckbox.setSelection(customerGroup.isEnabled());
		customerSegmentEnabledCheckbox.setEnabled(isEditable());

		mainPane.setControlModificationListener(listener);
	}
	
	private boolean isEditable() {
		return authorization != null && authorization == EpState.EDITABLE;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		CustomerGroup customerGroup = (CustomerGroup) getEditor().getModel();
		
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		binder.bind(bindingContext, customerSegmentNameField, customerGroup, "name", //$NON-NLS-1$
				EpValidatorFactory.ATTRIBUTE_KEY, null, true);
		customerSegmentNameField.setEnabled(isEditable() && !StringUtils.hasText(customerGroup.getName()));
		
		binder.bind(bindingContext, customerSegmentDescriptionField, customerGroup, "description", //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, null, true);

		binder.bind(bindingContext, customerSegmentEnabledCheckbox, customerGroup, "enabled"); //$NON-NLS-1$
		
		bindingContext.updateTargets();
	}
	
	private String safeDisplayString(final String value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		return value;
	}

}
