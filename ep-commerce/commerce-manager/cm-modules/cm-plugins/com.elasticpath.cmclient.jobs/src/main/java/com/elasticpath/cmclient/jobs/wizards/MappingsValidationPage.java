/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * Mappings validation page.
 */
public class MappingsValidationPage extends AbstractImportJobValidationPage {

	/**
	 * A page name.
	 */
	public static final String PAGE_NAME = "MappingsValidationPage"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param title the page title
	 * @param titleImage the image to display in the page
	 * @param request import job
	 * @param description the page description
	 */
	public MappingsValidationPage(final String title, final String description, final ImageDescriptor titleImage,
			final ImportJobRequest request) {
		super(PAGE_NAME, title, description, titleImage, request);
	}

	@Override
	protected String getSubTitle() {
		return JobsMessages.get().RunWizard_MappingsValidationPageSubTitile;
	}

	@Override
	protected String getErrorDescription() {
		return JobsMessages.get().RunWizard_MappingsValidationErrors;
	}

	@Override
	protected void populateErrorsTable(final IEpTableViewer errorTable) {
		errorTable.addTableColumn(JobsMessages.get().RunWizard_MappingsTableColumnTitle_Row, INITIAL_TABLE_COLUMN_WIDTH);
		errorTable.addTableColumn(JobsMessages.get().RunWizard_MappingsTableColumnTitle_Name, INITIAL_TABLE_COLUMN_WIDTH);
		errorTable.addTableColumn(JobsMessages.get().RunWizard_MappingsTableColumnTitle_Data, INITIAL_TABLE_COLUMN_WIDTH);
		errorTable.addTableColumn(JobsMessages.get().RunWizard_MappingsTableColumnTitle_Error, INITIAL_TABLE_COLUMN_WIDTH);
		errorTable.setLabelProvider(new PreviewLabelProvider());
		errorTable.setContentProvider(new ViewContentProvider());
	}

	/**
	 * Provides a preview of labels.
	 */
	protected class PreviewLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			final RowValidationFault fault = (RowValidationFault) element;
			switch (columnIndex) {

			case ROW_NUMBER_INDEX:
				return fault.getRowNumber();
			case SECOND_COLUMN_INDEX:
				return fault.getColumnName();
			case THIRD_COLUMN_INDEX:
				return fault.getData();
			case FOURTH_COLUMN_INDEX:
				return fault.getError();
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}
}
