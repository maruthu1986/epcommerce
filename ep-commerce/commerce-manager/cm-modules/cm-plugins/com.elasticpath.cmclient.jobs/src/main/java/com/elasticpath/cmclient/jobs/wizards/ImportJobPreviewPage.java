/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Preview page.
 */
public class ImportJobPreviewPage extends WizardPage {

	/**
	 * A page name.
	 */
	public static final String PAGE_NAME = "ImportJobPreviewPage"; //$NON-NLS-1$

	private static final String PREVIEW_TABLE = "Preview Table"; //$NON-NLS-1$

	private final ImportJobRequest request;

	private final ImportService importService;

	private IEpTableViewer previewTable;

	private static final int TABLE_GRID_LAYOUT_WIDTH = 500;

	private static final int TABLE_GRID_LAYOUT_HEIGHT = 300;

	private static final int INITIAL_TABLE_COLUMN_WIDTH = 100;
	
	/**
	 * Limit of lines quantity for preview.    
	 */
	private static final int MAX_PREVIEW_ROWS = 100;

	private IWizardPage nextPage;
	
	/**
	 * Constructor.
	 * 
	 * @param title the page title
	 * @param titleImage the image to display in the page
	 * @param description the page description
	 * @param request the model object
	 * @param nextPage nextPage
	 */
	public ImportJobPreviewPage(final String title, final String description, final ImageDescriptor titleImage,
			final ImportJobRequest request, final IWizardPage nextPage) {
		super(PAGE_NAME, title, titleImage);
		this.setDescription(description);
		this.request = request;
		this.importService =  ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
		this.nextPage = nextPage;
	}
	
	/**
	 * Get the limit or lines for preview.
	 * @return limit or lines for preview.
	 */
	public static int getPreviewRowsLimit() {
		return MAX_PREVIEW_ROWS;
	}

	/**
	 * Create the wizard's page composite.
	 * 
	 * @param parent the page's parent
	 */
	public void createControl(final Composite parent) {
		IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, true);

		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, false);
		previewTable = controlPane.addTableViewer(false, EpControlFactory.EpState.READ_ONLY, fieldData, PREVIEW_TABLE);

		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		data.heightHint = TABLE_GRID_LAYOUT_HEIGHT;
		data.widthHint = TABLE_GRID_LAYOUT_WIDTH;

		previewTable.getSwtTable().setLayoutData(data);

		this.updatePreviewDataInViewTable(request);
		
		this.setControl(controlPane.getSwtComposite());
	}

	private ImportJob createRestrictedCloneOfImportJobForPreview(final ImportJobRequest importJobRequest) {
		ImportJob clone = ServiceLocator.getService(ContextIdNames.IMPORT_JOB);
		clone.setCsvFileName(importJobRequest.getImportSource());
		clone.setCsvFileColDelimeter(importJobRequest.getImportJob().getCsvFileColDelimeter());
		clone.setCsvFileTextQualifier(importJobRequest.getImportJob().getCsvFileTextQualifier());
		clone.setMappings(new HashMap<String, Integer>(importJobRequest.getImportJob().getMappings()));
		return clone;
	}

	@Override
	public void setVisible(final boolean visible) {
		this.updatePreviewDataInViewTable(request);
		super.setVisible(visible);
	}

	private void updatePreviewDataInViewTable(final ImportJobRequest request) {
		
		if (request.getImportJob().getCsvFileName() == null 
				|| request.getImportJob().getCsvFileName().trim().length() == 0) {
			return;
		}
		previewTable.getSwtTable().setRedraw(false);
		
		if (previewTable.getSwtTable().getColumnCount() > 0) {
			for (TableColumn column : previewTable.getSwtTable().getColumns()) {
				if (column.isDisposed()) {
					continue;
				}
				column.dispose();
			}
		}
		
		ImportJob importJobClone = this.createRestrictedCloneOfImportJobForPreview(request);
		List<List<String>> previewData = importService.getPreviewData(importJobClone, getPreviewRowsLimit());
		
		if (!previewData.isEmpty()) {
			List<String> titles = previewData.get(0);
			for (String title : titles) {
				previewTable.addTableColumn(title, INITIAL_TABLE_COLUMN_WIDTH);
			}
			previewTable.setLabelProvider(new PreviewLabelProvider());
			previewTable.setContentProvider(new ViewContentProvider());
			List<List<String>> input = previewData.subList(1, previewData.size());
			Object[] res = input.toArray(new Object[input.size()]);
			previewTable.setInput(res);
			for (TableColumn col : previewTable.getSwtTable().getColumns()) {
				col.pack();
			}
		}
		
		previewTable.getSwtTable().setRedraw(true);
	}

	@Override
	public IWizardPage getNextPage() {
		return nextPage;
	}

	/**
	 * @param nextPage the nextPage to set
	 */
	public void setNextPage(final IWizardPage nextPage) {
		this.nextPage = nextPage;
	}


	/**
	 * Preview label provider for the page.
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
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final List<String> row = (List<String>) element;
			return row.get(columnIndex);
		}
	}

	/**
	 * Content provider for the page.
	 */
	class ViewContentProvider implements IStructuredContentProvider {

		/**
		 * Constructor. Registers this content provider with listener services.
		 */
		ViewContentProvider() {
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

}
