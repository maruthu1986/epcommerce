/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.dialogs;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.helpers.UserViewFormatter;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * Change set dialog allows.
 */
public class ChangeSetDialog extends AbstractEpDialog {

	private static final String CHANGESET_TABLE = "ChangeSet Table"; //$NON-NLS-1$

	private Collection<ChangeSet> changeSets;
	
	private IEpTableViewer changeSetTableViewer;
	
	private final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);

	private ChangeSet selectedChangeSetForMove;	

	/**
	 * Constructs the dialog.
	 * 
	 * @param parentShell the parent Shell	
	 * @param changeSets the list of change sets
	 * A new price tier will be created automatically.
	 */
	public ChangeSetDialog(final Shell parentShell, final Collection<ChangeSet> changeSets) {
		
		super(parentShell, 1, false);
				
		new DataBindingContext();
		
		// Set our model
		this.changeSets = changeSets;
		
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);
		
		dialogComposite.addLabel(ChangeSetMessages.get().ChangeSetDialog_PleaseSelectObjectsToMove_Message, labelData);
		
		IEpLayoutData tableData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		
		changeSetTableViewer = dialogComposite.addTableViewer(false, EpState.EDITABLE, tableData, CHANGESET_TABLE);
		
		final int[] widths = new int[] { 130, 110, 120 };
		final String[] columnNames = new String[] {
				ChangeSetMessages.get().ChangeSetDialog_ChangeSet_NameColumn,
				ChangeSetMessages.get().ChangeSetDialog_ChangeSet_CreatedByColumn,
				ChangeSetMessages.get().ChangeSetDialog_ChangeSet_DateCreatedColumn
		};
		
		for (int columnIndex = 0; columnIndex < widths.length; columnIndex++) {
			changeSetTableViewer.addTableColumn(columnNames[columnIndex], widths[columnIndex]);
		}
		
		changeSetTableViewer.setLabelProvider(new ChangeSetLabelProvider());
		changeSetTableViewer.setContentProvider(new ChangeSetProvider());
		
		changeSetTableViewer.getSwtTableViewer().addSelectionChangedListener((ISelectionChangedListener) event ->
			getButton(IDialogConstants.OK_ID).setEnabled(true));
	}

	@Override
	protected String getPluginId() {
		return ChangeSetPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return changeSets;
	}

	@Override
	protected void populateControls() {
		
		// Set data for the controls
		changeSetTableViewer.setInput(changeSets);
		
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected void bindControls() {
		// Does nothing
	}
	
	@Override
	protected String getTitle() {		
		
		return ChangeSetMessages.get().ChangeSetDialog_MoveObjectsToAnotherChangeset_Title;
	}
	
	@Override
	protected String getWindowTitle() {		
		return ChangeSetMessages.get().ChangeSetDialog_MoveObjects_WindowTitle;
	}


	@Override
	protected Image getWindowImage() {
	
		return null;
	}
	
	@Override
	protected String getInitialMessage() {
		return null;
	}
	
	/**
	 * Set the change sets.
	 * @param changeSets the change sets to specify
	 */
	public void setChangeSets(final List<ChangeSet> changeSets) {
		this.changeSets = changeSets;
	}
	
	/**
	 * A label provider for the change set table.
	 */
	public class ChangeSetLabelProvider extends LabelProvider implements ITableLabelProvider {
		
		private static final int CHANGESET_NAME_COLUMN = 0;
		private static final int CREATED_BY_COLUMN = 1;
		private static final int CREATED_DATE_TIME_COLUMN = 2;

		/**
		 * Get column image.
		 * @param element the object element
		 * @param columnIndex the column index
		 * @return an image or null
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}
		
		/**
		 * Gets the text for the given column with index columnIndex.
		 * 
		 * @param element the object element
		 * @param columnIndex the column index
		 * @return the text to use
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			ChangeSet member = (ChangeSet) element;
			switch (columnIndex) {		
				case CHANGESET_NAME_COLUMN:
					return member.getName(); 
				case CREATED_BY_COLUMN:
					return getCreatedByName(getCreatedBy(member));				
				case CREATED_DATE_TIME_COLUMN:
					return DateTimeUtilFactory.getDateUtil().formatAsDateTime(member.getCreatedDate());
				default: 
					return ChangeSetMessages.EMPTY_STRING;
			}
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
		 * Gets the user who created the change set.
		 * 
		 * @param cmUser the CM user
		 * @return the user's name or NA
		 */
		protected String getCreatedByName(final CmUser cmUser) {
			if (cmUser != changeSetTableViewer.getSwtTableViewer().getSelection()) {
				return UserViewFormatter.formatWithName(cmUser);
			}
			return ChangeSetMessages.get().NotAvailable;
		}
		
	}
	
	@Override
	protected void okPressed() {
			
		ISelection selection = changeSetTableViewer.getSwtTableViewer().getSelection();
		
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			
		this.selectedChangeSetForMove = (ChangeSet) structuredSelection.getFirstElement();				 		
	
		super.okPressed();
	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {

		createEpOkButton(parent, "Move", null); //$NON-NLS-1$
		createEpCancelButton(parent);


	}
	
	/**
	 * Content provider for the table.
	 */
	public class ChangeSetProvider implements IStructuredContentProvider {
		
		/**
		 * Gets the elements of a change set.
		 * 
		 * @param element the element
		 * @return an array of {@link ChangeSet} instances
		 */
		@Override
		public Object[] getElements(final Object element) {
			
			Collection<ChangeSet> changeSets = (Collection<ChangeSet>) element;
			return changeSets.toArray();
		}
		
		/**
		 *
		 */
		@Override
		public void dispose() {
			// nothing to dispose
		}
		
		/**
		 *
		 * @param viewer the viewer
		 * @param oldInput the old input
		 * @param newInput the new input
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// not significant for this implementation 
			
		}
	}	

	/**
	 * Get selected change set for move.
	 * @return the selected change set to move an object to.
	 */
	public ChangeSet getSelectedChangeSetForMove() {
		return selectedChangeSetForMove;
	}
	
}
