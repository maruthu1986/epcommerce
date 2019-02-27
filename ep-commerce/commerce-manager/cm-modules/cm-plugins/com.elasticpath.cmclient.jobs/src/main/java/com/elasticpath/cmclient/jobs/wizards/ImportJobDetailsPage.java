/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.jobs.JobsImageRegistry;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.JobsPlugin;
import com.elasticpath.cmclient.jobs.handlers.CsvFileUploadReceiver;
import com.elasticpath.cmclient.jobs.helpers.ImportJobDataValidator;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * Import job details.
 */
public class ImportJobDetailsPage extends WizardPage {

	/** A page name. */
	public static final String PAGE_NAME = "ImportJobDetailsPage"; //$NON-NLS-1$

	private Text browseTextField;

	private Spinner maxErrorSpinner;

	private Button previewDataButton;

	private final ImportJobRequest request;

	private final DataBindingContext dbc;

	private static final int NUM_COLUMNS = 3;

	private static final int MAX_SPINNER_VALUE = 1000;

	private static final int MAX_ALLOW_ERROR = 100;

	private boolean tooManyErrorsAllowed;

	private Label importFileValidationInfoLabel;

	private final int type;
	private final Display display;

	private static final Logger LOG = Logger.getLogger(ImportJobDetailsPage.class);

	private final ServerPushSession pushSession = new ServerPushSession();

	/**
	 * Constructor.
	 *
	 * @param title the page title
	 * @param titleImage the image to display in the page
	 * @param description the page description
	 * @param request the model object
	 * @param type jobs type
	 */
	protected ImportJobDetailsPage(final String title, final String description, final ImageDescriptor titleImage,
			final ImportJobRequest request, final int type) {
		super(PAGE_NAME, title, titleImage);
		setDescription(description);
		this.request = request;
		this.dbc = new DataBindingContext();
		this.type = type;
		this.display = Display.getDefault();
	}

	/**
	 * Get the DataBindingContext.
	 *
	 * @return the DataBindingContext.
	 */
	public DataBindingContext getBindingContext() {
		return this.dbc;
	}

	/**
	 * Create the wizard's page composite.
	 *
	 * @param parent the page's parent
	 */
	public void createControl(final Composite parent) {

		final IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(parent, NUM_COLUMNS, false);

		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, true, true, 2, 1);

		controlPane.addLabelBold(JobsMessages.get().JobDetailsPage_ImportName, labelData);
		controlPane.addLabel(request.getImportJob().getName(), fieldData);

		if (type == AbstractJobList.CATALOG_IMPORT_JOBS_TYPE) {
			controlPane.addLabelBold(JobsMessages.get().JobDetailsPage_Catalog, labelData);
			controlPane.addLabel(request.getImportJob().getCatalog().getName(), fieldData);
		} else if (type == AbstractJobList.CUSTOMER_IMPORT_JOBS_TYPE) {
			controlPane.addLabelBold(JobsMessages.get().JobDetailsPage_Store, labelData);
			controlPane.addLabel(request.getImportJob().getStore().getName(), fieldData);
		} else if (type == AbstractJobList.WAREHOUSE_IMPORT_JOBS_TYPE) {
			controlPane.addLabelBold(JobsMessages.get().JobDetailsPage_Warehouse, labelData);
			controlPane.addLabel(request.getImportJob().getWarehouse().getName(), fieldData);
		}

		controlPane.addLabelBold(JobsMessages.get().JobDetailsPage_DataType, labelData);
		controlPane.addLabel(request.getImportJob().getImportDataTypeName(), fieldData);

		controlPane.addLabelBold(JobsMessages.get().JobDetailsPage_ImportType, labelData);
		controlPane.addLabel(JobsMessages.get().getMessage(request.getImportJob().getImportType().getNameMessageKey()), fieldData);

		controlPane.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_ImportFile, EpControlFactory.EpState.EDITABLE, labelData);

		browseTextField = controlPane.addTextField(EpControlFactory.EpState.DISABLED, controlPane.createLayoutData(IEpLayoutData.FILL,
			IEpLayoutData.BEGINNING, true, true));
		final FileUpload fileUpload = new FileUpload(controlPane.getSwtComposite(), SWT.NONE);
		fileUpload.setFilterExtensions(new String[]{".csv"});

		final CsvFileUploadReceiver receiver = new CsvFileUploadReceiver(display);
		final FileUploadHandler uploadHandler = new FileUploadHandler(receiver);
		uploadHandler.addUploadListener(new FileUploadListener() {
			public void uploadProgress(final FileUploadEvent event) {
				//no-op
			}
			public void uploadFailed(final FileUploadEvent event) {
				//no-op
			}
			public void uploadFinished(final FileUploadEvent event) {
				File uploadedFile =  receiver.getUploadedFile();

				if (uploadedFile.exists()) {
					LOG.info("File uploaded to " + uploadedFile.getAbsolutePath());

					display.syncExec(() -> {
						browseTextField.setText(uploadedFile.getName());
						importFileValidationInfoLabel.setVisible(true);
						request.setImportSource(receiver.getRelativePathToUploadedFile());
					});
				}
			}
		});
		fileUpload.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				fileUpload.submit(uploadHandler.getUploadUrl());
			}
		});

		fileUpload.setImage(JobsImageRegistry.getImage(JobsImageRegistry.JOB_SEARCH_FILE));


		controlPane.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_MaximumErrors, EpControlFactory.EpState.EDITABLE, labelData);
		maxErrorSpinner = controlPane.addSpinnerField(EpControlFactory.EpState.EDITABLE, fieldData);
		maxErrorSpinner.setMaximum(MAX_SPINNER_VALUE);
		maxErrorSpinner.setSelection(request.getImportJob().getMaxAllowErrors());

		controlPane.addLabelBold(JobsMessages.get().JobDetailsPage_PreviewData, labelData);
		previewDataButton = controlPane.addCheckBoxButton("", EpControlFactory.EpState.EDITABLE, fieldData); //$NON-NLS-1$

		final IEpLayoutData infoLabelData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1);
		importFileValidationInfoLabel = controlPane.addLabel(JobsMessages.get().JobDetailsPage_StartImportFileValidationInfo, infoLabelData);
		importFileValidationInfoLabel.setVisible(false);

		this.bindControls();
		this.setControl(controlPane.getSwtComposite());
		pushSession.start();
	}

	private void bindControls() {

		EpControlBindingProvider.getInstance().bind(getBindingContext(), browseTextField, request,
				"importSource", EpValidatorFactory.REQUIRED, null, true); //$NON-NLS-1$

		final IValidator validateMaxAllowErrors = new IValidator() {
			public IStatus validate(final Object value) {
				if (maxErrorSpinner.getSelection() > MAX_ALLOW_ERROR) {
					tooManyErrorsAllowed = true;
					return new Status(IStatus.ERROR, JobsPlugin.PLUGIN_ID,
							IStatus.ERROR, JobsMessages.get().ImportJobWizard_TooManyAllowedErrors, null);
				}
				tooManyErrorsAllowed = false;
				return Status.OK_STATUS;
			}

		};
		EpControlBindingProvider.getInstance().bind(getBindingContext(), maxErrorSpinner, request,
				"maxAllowedFailedRows", validateMaxAllowErrors, null, true); //$NON-NLS-1$

		WizardPageSupport.create(ImportJobDetailsPage.this, getBindingContext());
	}

	@Override
	public IWizardPage getNextPage() {
		ImportJobDataValidator validator = new ImportJobDataValidator("Validating file...", request); //$NON-NLS-1$

		if (!validator.doValidate(getShell())) {
			return this;
		}

		if (validator.isCsvValidationFault()) {
			AbstractImportJobValidationPage csvPage = (AbstractImportJobValidationPage) getWizard().getPage(CsvValidationPage.PAGE_NAME);
			csvPage.setValidationFaults(validator.csvValidationFaults());
			return csvPage;
		}

		if (validator.isMappingValidationFault()) {
			AbstractImportJobValidationPage mappingPage =
				(AbstractImportJobValidationPage) getWizard().getPage(MappingsValidationPage.PAGE_NAME);
			mappingPage.setValidationFaults(validator.mappingValidationFaults());

			if (previewDataButton.getSelection()) {
				return getPreviewPage(mappingPage);
			}
			return mappingPage;
		}

		if (previewDataButton.getSelection()) {
			return getPreviewPage(getWizard().getPage(SuccessPage.PAGE_NAME));
		}
		return getWizard().getPage(SuccessPage.PAGE_NAME);
	}

	private ImportJobPreviewPage previewPage;

	private ImportJobPreviewPage getPreviewPage(final IWizardPage nextPage) {
		if (previewPage == null) {
			previewPage = new ImportJobPreviewPage(
					JobsMessages.get().RunWizard_JobPreviewPageTitle,

					NLS.bind(JobsMessages.get().RunWizard_JobPreviewPageDetails,
					ImportJobPreviewPage.getPreviewRowsLimit()),
					null, 
					request, 
					nextPage);
			Wizard wizard = (Wizard) getWizard();
			wizard.addPage(previewPage);
		}
		previewPage.setNextPage(nextPage);
		return previewPage;
	}

	@Override
	public boolean canFlipToNextPage() {
		return (browseTextField.getText().length() > 0) && !tooManyErrorsAllowed;
	}

	@Override
	public void dispose() {
		super.dispose();
		pushSession.stop();
	}

}
