/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;

import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.jobs.JobsImageRegistry;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.JobsPlugin;
import com.elasticpath.cmclient.jobs.handlers.CsvFileUploadReceiver;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.service.dataimport.ImportService;


/**
 * The Configure Import Job wizard page.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public abstract class AbstractConfigureImportJobPage extends AbstractEPWizardPage<Object> {

	private static final int PAGE_LAYOUT_NUM_COLUMNS = 4;

	private final ImportJob importJob;

	private final ImportService importService;

	private Text importNameField;

	private Text csvImportFileField;

	private CCombo columnDelimiterCombo;

	private Text otherColumnDelimiterText;

	private CCombo textDelimiterCombo;

	private Text otherTextDelimiterText;

	private Spinner maxErrorsSpinner;

	private final ServerPushSession pushSession = new ServerPushSession();

	private static final int MAX_SPINNER_VALUE = 1000;

	private static final int MAX_ALLOW_ERROR = 100;

	private static final Logger LOG = Logger.getLogger(AbstractConfigureImportJobPage.class);
	private final Display display;
	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param description the page description
	 * @param importJob the import job to add/edit
	 */
	protected AbstractConfigureImportJobPage(final String pageName, final String title, final String description,
											 final ImportJob importJob) {
		super(PAGE_LAYOUT_NUM_COLUMNS, false, pageName,
				title, description, new DataBindingContext());
		this.importJob = importJob;
		this.importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
		this.display = Display.getDefault();
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {

		pageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.createTopComponents(pageComposite);

		this.createImportJobNameComponent(pageComposite);

		this.createImportFileComponent(pageComposite);

		this.createBottomComponents(pageComposite);

		this.createMaxErrorsComponent(pageComposite);

		this.setControl(pageComposite.getSwtComposite());
	}

	/**
	 * @param pageComposite a parent composite
	 */
	protected void createBottomComponents(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_ColumnDelimeter, EpState.EDITABLE, labelData);

		final IEpLayoutData emptyData = pageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);
		final IEpLayoutData delimiterData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		columnDelimiterCombo = pageComposite.addComboBox(EpState.EDITABLE, delimiterData);
		columnDelimiterCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				int selectionIndex = columnDelimiterCombo.getSelectionIndex();
				if (ColumnCharDelimiter.values()[selectionIndex].equals(ColumnCharDelimiter.OTHER)) {
					otherColumnDelimiterText.setVisible(true);
				} else {
					otherColumnDelimiterText.setVisible(false);
				}
			}
		});

		otherColumnDelimiterText = pageComposite.addTextField(EpState.EDITABLE, delimiterData);
		otherColumnDelimiterText.setVisible(false);
		otherColumnDelimiterText.setTextLimit(1);
		pageComposite.addEmptyComponent(emptyData);


		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_TextDelimeter, EpState.EDITABLE, labelData);

		textDelimiterCombo = pageComposite.addComboBox(EpState.EDITABLE, delimiterData);
		textDelimiterCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				int selectionIndex = textDelimiterCombo.getSelectionIndex();
				if (TextCharDelimiter.values()[selectionIndex].equals(TextCharDelimiter.OTHER)) {
					otherTextDelimiterText.setVisible(true);
				} else {
					otherTextDelimiterText.setVisible(false);
				}
			}
		});

		otherTextDelimiterText = pageComposite.addTextField(EpState.EDITABLE, delimiterData);
		otherTextDelimiterText.setVisible(false);
		otherTextDelimiterText.setTextLimit(1);
		pageComposite.addEmptyComponent(emptyData);

	}

	/**
	 * @param pageComposite a parent composite
	 */
	protected void createTopComponents(final IEpLayoutComposite pageComposite) {
		// Overridable in subclass
	}

	/**
	 * @param pageComposite a parent composite
	 */
	protected void createMaxErrorsComponent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 3, 1);

		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_DefaultMaximumErrors, EpState.EDITABLE, labelData);
		maxErrorsSpinner = pageComposite.addSpinnerField(EpState.EDITABLE, fieldData);
		maxErrorsSpinner.setMaximum(MAX_SPINNER_VALUE);
	}

	/**
	 * @param pageComposite a parent composite
	 */
	protected void createImportFileComponent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_ImportFile, EpState.EDITABLE, labelData);

		final IEpLayoutData importFieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false, 2, 1);
		csvImportFileField = pageComposite.addTextField(EpState.DISABLED, importFieldData);

		final FileUpload fileUpload = new FileUpload(pageComposite.getSwtComposite(), SWT.NONE);
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
						csvImportFileField.setText(uploadedFile.getName());
						importJob.setCsvFileName(receiver.getRelativePathToUploadedFile());
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
	}

	/**
	 * @param pageComposite a parent composite
	 */
	protected void createImportJobNameComponent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_ImportName, EpState.EDITABLE, labelData);
		final IEpLayoutData fullFieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 3, 1);
		importNameField = pageComposite.addTextField(EpState.EDITABLE, fullFieldData);
	}

	@Override
	protected void populateControls() {
		if (importJob.isPersisted()) {
			if (importNameField != null) {
				importNameField.setText(importJob.getName());
			}
			if (maxErrorsSpinner != null) {
				maxErrorsSpinner.setSelection(importJob.getMaxAllowErrors());
			}
		}

		if (importJob.getName() != null && importNameField != null) {
			importNameField.setText(importJob.getName());
		}

		for (ColumnCharDelimiter delimiter : ColumnCharDelimiter.values()) {
			columnDelimiterCombo.add(delimiter.getMessage());
		}
		columnDelimiterCombo.setData(ColumnCharDelimiter.values());
		populateColumnDelimiter();

		for (TextCharDelimiter delimiter : TextCharDelimiter.values()) {
			textDelimiterCombo.add(delimiter.getMessage());
		}
		textDelimiterCombo.setData(TextCharDelimiter.values());
		populateTextDelimiter();
		pushSession.start();
	}

	private boolean isNameExist() {
		ImportJob importJobCheck = importService.findImportJob(importJob.getName());
		boolean nameExists = ((importJobCheck != null) && !importJobCheck.getGuid().equals(importJob.getGuid()));
		if (nameExists) {
			super.setErrorMessage(
				NLS.bind(JobsMessages.get().ImportJobWizard_ImportJobNameExists,
				importJob.getName()));
		} else {
			super.setErrorMessage(null);
		}
		return nameExists;
	}

	private void populateColumnDelimiter() {
		boolean commonDelimiterUsed = false;
		char columnDelimiterChar = importJob.getCsvFileColDelimeter();
		for (ColumnCharDelimiter columnDelimiter : ColumnCharDelimiter.values()) {
			if (columnDelimiter.getDelimiter() == columnDelimiterChar) {
				columnDelimiterCombo.select(columnDelimiter.ordinal());
				commonDelimiterUsed = true;
				break;
			}
		}
		if (!commonDelimiterUsed) {
			columnDelimiterCombo.select(ColumnCharDelimiter.OTHER.ordinal());
			otherColumnDelimiterText.setText(String.valueOf(columnDelimiterChar));
			otherColumnDelimiterText.setVisible(true);
		}
	}

	private void populateTextDelimiter() {
		boolean commonDelimiterUsed = false;
		char textDelimiterChar = importJob.getCsvFileTextQualifier();
		for (TextCharDelimiter textDelimiter : TextCharDelimiter.values()) {
			if (textDelimiter.getDelimiter() == textDelimiterChar) {
				textDelimiterCombo.select(textDelimiter.ordinal());
				commonDelimiterUsed = true;
				break;
			}
		}
		if (!commonDelimiterUsed) {
			textDelimiterCombo.select(TextCharDelimiter.OTHER.ordinal());
			otherTextDelimiterText.setText(String.valueOf(textDelimiterChar));
			otherTextDelimiterText.setVisible(true);
		}
	}

	@Override
	protected void bindControls() {
		DataBindingContext context = getDataBindingContext();
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
		
		if (importNameField != null) {
			binder.bind(context, importNameField, importJob, "name", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, true);
		}

		binder.bind(context, csvImportFileField, importJob, "csvFileName", EpValidatorFactory.REQUIRED, null, true); //$NON-NLS-1$

		final EpValueBinding otherColumnDelimiterBinding = binder.bind(context, otherColumnDelimiterText, importJob,
				"csvFileColDelimeter", new OtherColumnDelimiterValidator(), //$NON-NLS-1$
				new DelimiterConverter(String.class, char.class), false);

		final ObservableUpdateValueStrategy columnDelimiterUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				ColumnCharDelimiter columnDelimiter = ((ColumnCharDelimiter[]) columnDelimiterCombo.getData())[columnDelimiterCombo
						.getSelectionIndex()];
				if (columnDelimiter.equals(ColumnCharDelimiter.OTHER)) {
					otherColumnDelimiterBinding.getBinding().updateTargetToModel();
					String currentText = otherColumnDelimiterText.getText();
					if (currentText.length() > 0) {
						importJob.setCsvFileColDelimeter(currentText.charAt(0));
					}
				} else {
					otherColumnDelimiterText.setText(String.valueOf(columnDelimiter.getDelimiter()));
					importJob.setCsvFileColDelimeter(columnDelimiter.getDelimiter());
				}
				return Status.OK_STATUS;
			}
		};
		binder.bind(context, columnDelimiterCombo, null, null, columnDelimiterUpdateStrategy, true);

		final EpValueBinding otherTextDelimiterBinding = binder.bind(context, otherTextDelimiterText, importJob,
				"csvFileTextQualifier", new OtherTextDelimiterValidator(), //$NON-NLS-1$ 
				new DelimiterConverter(String.class, char.class), false);

		final ObservableUpdateValueStrategy textDelimiterUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				TextCharDelimiter textDelimiter = ((TextCharDelimiter[]) textDelimiterCombo.getData())[textDelimiterCombo.getSelectionIndex()];
				if (textDelimiter.equals(TextCharDelimiter.OTHER)) {
					otherTextDelimiterBinding.getBinding().updateTargetToModel();
					String currentText = otherTextDelimiterText.getText();
					if (StringUtils.isNotBlank(currentText)) {
						importJob.setCsvFileTextQualifier(currentText.charAt(0));
					}
				} else {
					otherTextDelimiterText.setText(String.valueOf(textDelimiter.getDelimiter()));
					importJob.setCsvFileTextQualifier(textDelimiter.getDelimiter());
				}
				return Status.OK_STATUS;
			}
		};
		binder.bind(context, textDelimiterCombo, null, null, textDelimiterUpdateStrategy, true);
		
		bindMaxAllowErrorsValidator(binder);
		
		EpWizardPageSupport.create(this, context);
	}
	
	private void bindMaxAllowErrorsValidator(final EpControlBindingProvider binder) {
		if (maxErrorsSpinner == null) {
			return;
		}
		final IValidator validateMaxAllowErrors = new IValidator() {
			public IStatus validate(final Object value) {
				if (maxErrorsSpinner.getSelection() > MAX_ALLOW_ERROR) {
					return new Status(IStatus.ERROR, JobsPlugin.PLUGIN_ID,
							IStatus.ERROR, JobsMessages.get().ImportJobWizard_TooManyAllowedErrors, null);
				}
				return Status.OK_STATUS;
			}

		};
		binder.bind(getDataBindingContext(), maxErrorsSpinner, importJob,
				"maxAllowErrors", validateMaxAllowErrors, null, true); //$NON-NLS-1$
	}

	/**
	 * Manual check to confirm import job name does not already exist.
	 * 
	 * @return <code>true</code> if import job name does not already exist, <code>false</code> otherwise.
	 */
	protected boolean isManualCheckPageComplete() {
		return !isNameExist();
	}

	@Override
	public void setErrorMessage(final String newMessage) {
		// Do nothing
	}

	/**
	 * The other column delimiter validator.
	 */
	private class OtherColumnDelimiterValidator implements IValidator {
		public IStatus validate(final Object value) {
			String stringValue = (String) value;
			ColumnCharDelimiter columnDelimiter = ((ColumnCharDelimiter[]) columnDelimiterCombo.getData())[columnDelimiterCombo.getSelectionIndex()];
			if (columnDelimiter.equals(ColumnCharDelimiter.OTHER) && (stringValue.length() == 0)) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().EpValidatorFactory_ValueRequired, null);
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * The other text delimiter validator.
	 */
	private class OtherTextDelimiterValidator implements IValidator {
		public IStatus validate(final Object value) {
			String stringValue = (String) value;
			TextCharDelimiter textDelimiter = ((TextCharDelimiter[]) textDelimiterCombo.getData())[textDelimiterCombo.getSelectionIndex()];
			if (textDelimiter.equals(TextCharDelimiter.OTHER) && (StringUtils.isBlank(stringValue))) {
				return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().EpValidatorFactory_ValueRequired, null);
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * String to Char delimiter converter for binding.
	 */
	private class DelimiterConverter extends Converter {

		DelimiterConverter(final Object fromType, final Object toType) {
			super(fromType, toType);

		}

		public Object convert(final Object fromObject) {
			String delimiter = (String) fromObject;
			if (delimiter.length() > 0) {
				return delimiter.charAt(0);
			}
			return Character.MIN_VALUE;
		}
	}

	/**
	 * Reusable importJob field in pages hierarchy.
	 * 
	 * @return its importJob
	 */
	protected ImportJob getImportJob() {
		return this.importJob;
	}

	/**
	 * Reusable importService field in pages hierarchy.
	 * 
	 * @return its importJob
	 */
	protected ImportService getImportService() {
		return this.importService;
	}

	/**
	 * @return list of data types available for this import job.
	 */
	protected abstract List<ImportDataType> getImportDataTypes();

	@Override
	public IWizardPage getNextPage() {		
		if (isNameExist()) {
			return this; 
		}
		return super.getNextPage();
	}

	@Override
	public boolean canFlipToNextPage() {
		return (importNameField == null || StringUtils.isNotBlank(importNameField.getText()))
			&& StringUtils.isNotBlank(csvImportFileField.getText());
	}


	@Override
	public void dispose() {
		pushSession.stop();
		super.dispose();
	}

}
