/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors.sections;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.actions.CreateDataPointAction;
import com.elasticpath.cmclient.admin.datapolicies.editors.DataPolicyDataPointSelectionDualListBox;
import com.elasticpath.cmclient.admin.datapolicies.editors.DataPolicyEditor;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * UI representation of the Data Policy Data Point section.
 */
public class DataPolicyDataPointSection extends AbstractCmClientEditorPageSectionPart {

	private IEpLayoutComposite mainPane;

	private DataPolicyDataPointSelectionDualListBox dataPointSelectionBox;
	private Button createDataPointButton;
	private Action createDataPointAction;

	/**
	 * Constructor.
	 *
	 * @param formPage the form page.
	 * @param editor   the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 */
	public DataPolicyDataPointSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
	}

	@Override
	protected String getSectionTitle() {
		return AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Section;
	}

	@Override
	public void initialize(final IManagedForm form) {
		super.initialize(form);
		mainPane.setControlModificationListener(getEditor());
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		//nothing to bind
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, true);

		final IEpLayoutData sectionLayoutData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1);
		final Section section = createSection(toolkit, sectionLayoutData);
		final IEpLayoutComposite layoutPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);

		EpControlFactory.EpState state = isEditableMode() ? EpControlFactory.EpState.EDITABLE : EpControlFactory.EpState.DISABLED;
		dataPointSelectionBox = new DataPolicyDataPointSelectionDualListBox(layoutPane, getEditorModel(), state);
		dataPointSelectionBox.createControls();
		dataPointSelectionBox.registerChangeListener(this::markDirty);
		section.setClient(layoutPane.getSwtComposite());

		createDataPointButton = mainPane.addPushButton(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Button_CreateDataPoint,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				EpControlFactory.EpState.EDITABLE,
				mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, false, false, 1, 1));
		createDataPointButton.setVisible(isEditableMode());

		createDataPointAction = new CreateDataPointAction(getEditor(), dataPointSelectionBox.getAvailableTableViewer(),
				AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Button_CreateDataPoint_Tooltip,
				CoreImageRegistry.IMAGE_ADD);

		addListeners();
	}

	private void addListeners() {
		createDataPointButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				createDataPointAction.run();
			}
		});
	}

	private Section createSection(final FormToolkit toolkit, final IEpLayoutData layoutData) {
		final Section section = toolkit.createSection(mainPane.getSwtComposite(), ExpandableComposite.TITLE_BAR);
		section.setLayoutData(layoutData.getSwtLayoutData());
		return section;
	}

	@Override
	protected void populateControls() {
		//do nothing
	}

	@Override
	public void commit(final boolean onSave) {
		super.commit(onSave);
		getEditorModel().setDataPoints(dataPointSelectionBox.getAssigned());
	}

	private DataPolicy getEditorModel() {
		return ((DataPolicy) getEditor().getModel());
	}

	private boolean isEditableMode() {
		return ((DataPolicyEditor) getEditor()).isEditableMode();
	}
}
