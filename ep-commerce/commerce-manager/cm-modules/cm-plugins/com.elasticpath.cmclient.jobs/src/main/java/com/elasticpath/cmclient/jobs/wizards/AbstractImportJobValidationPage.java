/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.jobs.JobsImageRegistry;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Abstract import validation page.
 */
public abstract class AbstractImportJobValidationPage extends WizardPage {

	private static final String ERROR_TABLE = "Error table"; //$NON-NLS-1$
	/**
	 * Import service.
	 */
	private final ImportService importService;

	private final ImportJobRequest request;

	/**
	 * Row number index.
	 */
	protected static final int ROW_NUMBER_INDEX = 0;

	/**
	 * Second column index.
	 */
	protected static final int SECOND_COLUMN_INDEX = 1;

	/**
	 * Third column index.
	 */
	protected static final int THIRD_COLUMN_INDEX = 2;

	/**
	 * Fourth column index.
	 */
	protected static final int FOURTH_COLUMN_INDEX = 3;

	/**
	 * Table width.
	 */
	protected static final int TABLE_GRID_LAYOUT_WIDTH = 500;

	/**
	 * Table height.
	 */
	protected static final int TABLE_GRID_LAYOUT_HEIGHT = 300;

	/**
	 * Initial column width.
	 */
	protected static final int INITIAL_TABLE_COLUMN_WIDTH = 100;

	private IEpTableViewer errorsTable;

	/**
	 * Constructor.
	 * 
	 * @param pageName the page name
	 * @param title the page title
	 * @param titleImage the image to display in the page
	 * @param request import job
	 * @param description the page description
	 */
	protected AbstractImportJobValidationPage(final String pageName, final String title, final String description, final ImageDescriptor titleImage,
			final ImportJobRequest request) {
		super(pageName, title, titleImage);
		this.request = request;
		setDescription(description);
		importService =  ServiceLocator.getService("importService"); //$NON-NLS-1$

	}

	/**
	 * Creates the controls.
	 * @param parent The parent to the new controls.
	 */
	public void createControl(final Composite parent) {
		IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, true);

		IEpLayoutComposite group = controlPane.addGroup(getSubTitle(), 1, true, controlPane.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.BEGINNING));

		IEpLayoutComposite comp = group.addGridLayoutComposite(2, false, group.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		comp.addImage(JobsImageRegistry.getImage(JobsImageRegistry.JOB_IMPORT_ERROR_SMALL), controlPane.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.BEGINNING));
		comp.addLabel(getErrorDescription(), controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true));

		errorsTable = group.addTableViewer(false,
			EpControlFactory.EpState.READ_ONLY, group.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, true, 2, 1),
			ERROR_TABLE);
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);

		data.heightHint = TABLE_GRID_LAYOUT_HEIGHT;
		data.widthHint = TABLE_GRID_LAYOUT_WIDTH;

		errorsTable.getSwtTable().setLayoutData(data);
		populateErrorsTable(errorsTable);
		this.setControl(controlPane.getSwtComposite());

	}

	/**
	 * Gets subtitle.
	 * 
	 * @return subtitle
	 */
	protected abstract String getSubTitle();

	/**
	 * Gets error description.
	 * 
	 * @return error description
	 */
	protected abstract String getErrorDescription();

	/**
	 * Performs validation.
	 * 
	 * @param csv source csv file
	 * @return list of faults
	 */
	//protected abstract List<RowValidationFault> doValidate(String csv);

	/**
	 * Populates errors table.
	 * 
	 * @param errorsTable errors table
	 */
	protected abstract void populateErrorsTable(IEpTableViewer errorsTable);

	/**
	 * Sets validation faults.
	 *
	 * @param faults faults
	 */
	public void setValidationFaults(final List<RowValidationFault> faults) {
		if (!faults.isEmpty()) {
			errorsTable.setInput(faults.toArray(new RowValidationFault[faults.size()]));
			for (TableColumn col : errorsTable.getSwtTable().getColumns()) {
				col.pack();
			}
		}
	}

	@Override
	public void setErrorMessage(final String newMessage) {
		// Do nothing
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	/**
	 * Content provider for the page.
	 */
	protected class ViewContentProvider implements IStructuredContentProvider {

		/**
		 * Constructor. Registers this content provider with listener services.
		 */
		public ViewContentProvider() {
			// none
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// nothing
		}

		@Override
		public void dispose() {
			// nothing
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof Object[]) {
				return (Object[]) inputElement;
			}
			return new Object[0];
		}
	}

	/**
	 * Gets import setup service.
	 * 
	 * @return import setup service
	 */
	public ImportService getImportService() {
		return importService;
	}

	/**
	 * Gets import job request.
	 * 
	 * @return import job request
	 */
	public ImportJobRequest getImportJobRequest() {
		return request;
	}

}
