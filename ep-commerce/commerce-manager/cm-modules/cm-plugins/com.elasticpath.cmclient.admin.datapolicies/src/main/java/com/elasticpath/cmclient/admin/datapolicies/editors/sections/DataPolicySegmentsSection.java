/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors.sections;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.actions.AddSegmentAction;
import com.elasticpath.cmclient.admin.datapolicies.actions.RemoveSegmentAction;
import com.elasticpath.cmclient.admin.datapolicies.editors.DataPolicyEditor;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.domain.datapolicy.DataPolicy;

/**
 * UI representation of the Data Policy Segments section.
 */
public class DataPolicySegmentsSection extends AbstractCmClientEditorPageSectionPart {

	private static final int SEGMENT_COLUMN_WIDTH = 250;
	private static final String SEGMENT_TABLE_NAME = "Segments table";
	private static final int LAYOUT_COLUMN_NUMBER = 3;

	private IEpLayoutComposite mainPane;

	private IEpTableViewer segmentsTableViewer;

	private IEpTableColumn segmentTableColumn;

	private Text segmentNameTextField;

	private Button addSegmentButton;

	private Button removeSegmentButton;

	private Action addSegmentAction;

	private Action removeSegmentAction;

	/**
	 * Constructor.
	 *
	 * @param formPage the form page.
	 * @param editor   the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 */
	public DataPolicySegmentsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
	}

	@Override
	protected String getSectionTitle() {
		return AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_SegmentsSection;
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

	private void updateSegmentValue(final String oldValue, final String newValue) {
		Set<String> segments = getEditorModel().getSegments();
		segments.remove(oldValue);
		segments.add(newValue);
		segmentsTableViewer.getSwtTableViewer().refresh();
		getEditor().controlModified();
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		mainPane = CompositeFactory.createTableWrapLayoutComposite(client, LAYOUT_COLUMN_NUMBER, false);

		final IEpLayoutData tableLayoutData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, true);
		final IEpLayoutData labelData = mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		mainPane.addLabelBold(AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_Dialog_Title, labelData);
		segmentNameTextField = mainPane.addTextField(EpControlFactory.EpState.EDITABLE, fieldData);
		addSegmentButton = mainPane.addPushButton(AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_AddSegmentButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				EpControlFactory.EpState.EDITABLE, fieldData);

		mainPane.addLabelBold(AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_SegmentsSection, labelData);
		segmentsTableViewer = mainPane.addTableViewer(false, EpControlFactory.EpState.EDITABLE, tableLayoutData, SEGMENT_TABLE_NAME);

		segmentTableColumn = segmentsTableViewer.addTableColumn(AdminDataPoliciesMessages.get()
				.DataPolicyEditor_SegmentsPage_TableValueColumn, SEGMENT_COLUMN_WIDTH);

		segmentsTableViewer.getSwtTable().setHeaderVisible(false);

		removeSegmentButton = mainPane
				.addPushButton(AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_RemoveSegmentButton,
						CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
						EpControlFactory.EpState.EDITABLE, fieldData);

		addSegmentAction = new AddSegmentAction(segmentsTableViewer, segmentNameTextField, getEditor(), getEditorModel(),
				AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_AddSegmentButton, CoreImageRegistry.IMAGE_ADD);
		removeSegmentAction = new RemoveSegmentAction(segmentsTableViewer, getEditor(), getEditorModel(), this::getSelectedSegment,
				AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_RemoveSegmentButton);

		addListeners();

		segmentsTableViewer.setContentProvider(new SegmentsContentProvider());
		segmentsTableViewer.setLabelProvider(new SegmentsLabelProvider());
	}

	private void addListeners() {
		removeSegmentButton.setEnabled(false);
		addSegmentButton.setEnabled(false);

		segmentNameTextField.addModifyListener(modifyEvent -> {
			String selectionText = segmentNameTextField.getText();
			boolean enableAddButton = !StringUtils.isEmpty(selectionText);
			addSegmentButton.setEnabled(enableAddButton && isEditableMode());
		});

		segmentsTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
			Object firstSelection = strSelection.getFirstElement();
			removeSegmentButton.setEnabled(firstSelection != null && isEditableMode());
		});

		if (isEditableMode()) {
			segmentTableColumn.setEditingSupport(new SegmentsEditingSupport(segmentsTableViewer, this::updateSegmentValue));
		}

		addSegmentButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				addSegmentAction.run();
			}
		});

		removeSegmentButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				removeSegmentAction.run();
			}
		});

	}

	private String getSelectedSegment() {
		IStructuredSelection selection = (IStructuredSelection) segmentsTableViewer.getSwtTableViewer().getSelection();
		if (selection.isEmpty()) {
			return null;
		}
		return (String) selection.getFirstElement();
	}

	@Override
	protected void populateControls() {
		segmentsTableViewer.setInput(getEditorModel());
		segmentsTableViewer.getSwtTableViewer().refresh();

		segmentNameTextField.setEnabled(isEditableMode());

		if (isEditableMode()) {
			segmentTableColumn.setEditingSupport(new SegmentsEditingSupport(segmentsTableViewer, this::updateSegmentValue));
		}
	}

	@Override
	public void commit(final boolean onSave) {
		super.commit(onSave);
	}

	private DataPolicy getEditorModel() {
		return ((DataPolicy) getEditor().getModel());
	}

	/**
	 * Segments table content provider.
	 */
	private class SegmentsContentProvider implements IStructuredContentProvider {

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// do nothing
		}

		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			DataPolicy dataPolicy = (DataPolicy) inputElement;
			String[] segments = dataPolicy.getSegments().toArray(new String[0]);
			Arrays.sort(segments);
			return segments;
		}
	}

	/**
	 * Segments table label provider.
	 */
	private class SegmentsLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (isEditableMode()) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
			}
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			return (String) element;
		}
	}

	private boolean isEditableMode() {
			return ((DataPolicyEditor) getEditor()).isEditableMode();
	}
}
