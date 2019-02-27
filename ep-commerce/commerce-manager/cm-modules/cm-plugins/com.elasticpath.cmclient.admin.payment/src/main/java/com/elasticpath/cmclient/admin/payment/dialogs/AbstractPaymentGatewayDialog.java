/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.payment.dialogs;

import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.elasticpath.cmclient.admin.payment.AdminPaymentPlugin;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.payment.AdminPaymentImageRegistry;
import com.elasticpath.cmclient.admin.payment.AdminPaymentMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Abstract payment gateway dialog. Defines common layouts for create and edit payment gateway dialogs.
 */
@SuppressWarnings({ "PMD.GodClass", "PMD.PrematureDeclaration" })
public abstract class AbstractPaymentGatewayDialog extends AbstractEpDialog {

	/** Logger. */
	protected static final Logger LOG = Logger.getLogger(AbstractPaymentGatewayDialog.class);

	private static final int PAYMENT_GATEWAY_NAME_TEXT_LIMIT = 255;

	/** Constant holds a value for properties' list key column. */
	private static final int PROPERTY_KEY_WIDTH = 200;

	/** Constant holds a value for properties' list value column. */
	private static final int PROPERTY_VALUE_WIDTH = 200;

	private static final String PAYMENT_GATEWAY_PROPERTIES_TABLE = "Payment Gateway Properties Table"; //$NON-NLS-1$

	private static final int GATEWAY_PROPERTIES_TABLE_HEIGHT = 150;

	/** This dialog's title. Depends from whether this is create or edit dialog */
	private final String title;

	/** This dialog's image. Depends from whether this is create or edit dialog */
	private final Image image;

	/** Payment gateway name. */
	private Text gatewayNameText;

	/** List of gateway's implementations. */
	private CCombo gatewayImplCombo;

	/** Table viewer to display/edit gateway's properties. */
	private IEpTableViewer gatewayPropsTable;

	/** The cell modifier class. Responsible for properties' modification. */
	private PropertyCellModifier gatewayPropertiesCellModifier;

	private final DataBindingContext dataBindingCtx;

	private String gatewayNewName;

	/**
	 * Creates the dialog.
	 * 
	 * @param parentShell the parent Eclipse shell
	 * @param image the image for this dialog
	 * @param title the title for this dialog
	 */
	protected AbstractPaymentGatewayDialog(final Shell parentShell, final Image image, final String title) {
		super(parentShell, 1, true);
		this.title = title;
		this.image = image;
		dataBindingCtx = new DataBindingContext();
	}

	/**
	 * Instantiates create payment dialog.
	 * 
	 * @param parentShell the parent Eclipse shell
	 * @return create payment dialog instance
	 */
	public static AbstractPaymentGatewayDialog buildCreateDialog(final Shell parentShell) {
		return new PaymentGatewayCreateDialog(parentShell, AdminPaymentImageRegistry
				.getImage(AdminPaymentImageRegistry.IMAGE_PAYMENT_GATEWAY_CREATE), AdminPaymentMessages.get().CreatePaymentGatewayDialog);
	}

	/**
	 * Instantiates edit payment dialog.
	 * 
	 * @param parentShell the parent Eclipse shell
	 * @param paymentGateway the payment gateway to be edited in this dialog
	 * @return edit payment dialog instance
	 */
	public static AbstractPaymentGatewayDialog buildEditDialog(final Shell parentShell, final PaymentGateway paymentGateway) {
		return new PaymentGatewayEditDialog(parentShell, paymentGateway, AdminPaymentImageRegistry
				.getImage(AdminPaymentImageRegistry.IMAGE_PAYMENT_GATEWAY_EDIT), AdminPaymentMessages.get().EditPaymentGatewayDialog);
	}

	/**
	 * Gets the payment gateway this dialog is creating or editing.
	 * 
	 * @return Payment Gateway this dialog has created or edited.
	 */
	public abstract PaymentGateway getPaymentGateway();


	@Override
	public Object getModel() {
		return getPaymentGateway();
	}
	@Override
	protected String getPluginId() {
		return AdminPaymentPlugin.PLUGIN_ID;
	}


	@Override
	protected abstract void populateControls();

	/**
	 * Convenience method to open dialog.
	 * 
	 * @return true if dialog opened successfully, false otherwise.
	 */
	public final boolean openDialog() {
		return open() == Window.OK;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);

		/** This composite holds payment's name and impl text fields. */
		final IEpLayoutComposite paymentComposite = dialogComposite.addGridLayoutComposite(2, false, dialogComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		paymentComposite.addLabelBoldRequired(AdminPaymentMessages.get().GatewayNameLabel, EpState.EDITABLE, labelData);
		gatewayNameText = paymentComposite.addTextField(EpState.EDITABLE, null);
		gatewayNameText.setTextLimit(PAYMENT_GATEWAY_NAME_TEXT_LIMIT);
		gatewayNameText.addModifyListener((ModifyListener) event -> flushErrorMessage());

		paymentComposite.addLabelBoldRequired(AdminPaymentMessages.get().GatewayImplLabel, EpState.EDITABLE, labelData);
		gatewayImplCombo = paymentComposite.addComboBox(EpState.EDITABLE, null);
		gatewayImplCombo.setEditable(false);
		gatewayImplCombo.addModifyListener((ModifyListener) event -> flushErrorMessage());

		paymentComposite.addLabelBold(AdminPaymentMessages.get().PaymentProperty, dialogComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.FILL));
		paymentComposite.addEmptyComponent(labelData);

		/** Create single selection, editable table. */
		gatewayPropsTable = dialogComposite.addTableViewer(false, EpState.EDITABLE, null, PAYMENT_GATEWAY_PROPERTIES_TABLE);
		GridData gatewayPropsTableLayoutData = (GridData) gatewayPropsTable.getSwtTable().getLayoutData();
		gatewayPropsTableLayoutData.heightHint = GATEWAY_PROPERTIES_TABLE_HEIGHT;

		final TableViewer swtTableViewer = gatewayPropsTable.getSwtTableViewer();
		final Table swtTable = gatewayPropsTable.getSwtTable();

		// add Property table columns
		gatewayPropsTable.addTableColumn(AdminPaymentMessages.get().PaymentPropertyListKey, PROPERTY_KEY_WIDTH);
		gatewayPropsTable.addTableColumn(AdminPaymentMessages.get().PaymentPropertyListValue, PROPERTY_VALUE_WIDTH);

		// add cell editors to make possible a property's value to be changed in UI
		swtTableViewer.setCellEditors(new CellEditor[] { new TextCellEditor(), new TextCellEditor(swtTable) });
		gatewayPropertiesCellModifier = new PropertyCellModifier(swtTableViewer);
		swtTableViewer.setCellModifier(gatewayPropertiesCellModifier);

		gatewayPropsTable.setContentProvider(new PaymentGatewayContentProvider());
		gatewayPropsTable.setLabelProvider(new PaymentGatewayLabelProvider());
	}

	@Override
	protected void bindControls() {
		final boolean hideDecorationOnFirstValidation = true;

		final ObservableUpdateValueStrategy gatewayNameUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				gatewayNewName = (String) newValue;
				return Status.OK_STATUS;
			}
		};

		IValidator existingNameValidator = validator -> {
			if (!isGatewayNameNotExist(gatewayNameText.getText())) {
				return new Status(IStatus.ERROR, AdminPaymentPlugin.PLUGIN_ID, AdminPaymentMessages.get().PaymentGatewayNameAlreadyExist);
			}
			return Status.OK_STATUS;
		};

		EpControlBindingProvider.getInstance().bind(dataBindingCtx, gatewayNameText,
				new CompoundValidator(new IValidator[]{EpValidatorFactory.STRING_255_REQUIRED, EpValidatorFactory.NO_LEADING_TRAILING_SPACES,
						existingNameValidator}), null, gatewayNameUpdateStrategy, hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, dataBindingCtx);
	}

	@Override
	protected void okPressed() {
		// fix for ROXY-93 Mac issue to force update on saving without clicking out of cell
		getGatewayNameText().setFocus(); 
		// update model, so all strategies to have a chance to do their
		// work.
		dataBindingCtx.updateModels();
		if (isGatewayPropsOK() && prepareForSave()) {
			super.okPressed();
		}
	}

	private boolean isGatewayPropsOK() {
		if (gatewayPropertiesCellModifier.isGatewayPropsOK()) {
			return true;
		}
		setError(AdminPaymentMessages.get().PaymentPropertyInvalid);
		return false;
	}

	/**
	 * Prepares the payment gateway for further saving.
	 * 
	 * @return true if gateway is populated with UI data successfully, false otherwise.
	 */
	protected abstract boolean prepareForSave();

	@Override
	protected final String getInitialMessage() {
		return AdminPaymentMessages.get().PaymentDialogInitialMessage;
	}

	@Override
	protected final String getTitle() {
		return title;
	}

	@Override
	protected final Image getTitleImage() {
		return null;
	}

	@Override
	protected final String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	/**
	 * Payment Gateway properties content provider.
	 */
	protected static final class PaymentGatewayContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(final Object inputElement) {
			if (!(inputElement instanceof Properties)) {
				return null;
			}
			final Properties props = (Properties) inputElement;

			return props.entrySet().toArray();
		}

		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// do nothing
		}
	}

	/**
	 * Payment Gateway properties label provider.
	 */
	private static final class PaymentGatewayLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int PROP_KEY_INDEX = 0;

		private static final int PROP_VALUE_INDEX = 1;

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (columnIndex == PROP_VALUE_INDEX) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
			}
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {

			if (!(element instanceof Entry)) {
				return null;
			}

			final Entry<String, String> entry = (Entry<String, String>) element;
			final String key = entry.getKey();
			String value = entry.getValue();

			if ("".equals(value)) { //$NON-NLS-1$
				value = "<Enter a value>"; //$NON-NLS-1$
			}
			switch (columnIndex) {
			case PROP_KEY_INDEX:
				return key;
			case PROP_VALUE_INDEX:
				return value;
			default:
				return null;
			}
		}
	}

	/**
	 * This class gets new property input and saves it in the model.
	 */
	private final class PropertyCellModifier implements ICellModifier {

		private final TableViewer gatewayPropsViewer;

		/** Specifies whether selected gateway's properties were modified or not. */
		private boolean gatewayPropsModified;

		/**
		 * Specifies whether properties were validated successfully, i.e. there are no props longer than 1024 chars
		 */
		private boolean isGatewayPropsOK() {
			Properties properties = (Properties) (gatewayPropsViewer.getInput());
			if (properties == null) {
				return true;
			}
			for (Object value : properties.values()) {
				if (!EpValidatorFactory.MAX_LENGTH_1024.validate(value).isOK()) {
					return false;
				}
			}
			return true;
		}

		private boolean isGatewayPropsModified() {
			return gatewayPropsModified;
		}

		private void flushGatewayPropertiesModified() {
			gatewayPropsModified = false;
		}

		/**
		 * Constructs this cell editor.
		 * 
		 * @param propsViewer table viewer this cell modifier is associated with.
		 */
		PropertyCellModifier(final TableViewer propsViewer) {
			this.gatewayPropsViewer = propsViewer;
		}

		@Override
		public boolean canModify(final Object element, final String property) {
			return property.equals(AdminPaymentMessages.get().PaymentPropertyListValue);
		}

		@Override
		public Object getValue(final Object element, final String property) {
			if (!(element instanceof Entry)) {
				return null;
			}
			flushErrorMessage();
			Entry<String, String> entry = (Entry<String, String>) element;
			return entry.getValue();
		}

		@Override
		public void modify(final Object element, final String property, final Object value) {
			Object entryObj;
			if (element instanceof Item) {
				entryObj = ((Item) element).getData();
			} else if (element instanceof Entry) {
				entryObj = element;
			} else {
				return;
			}

			Entry<String, String> entry = (Entry<String, String>) entryObj;

			if (EpValidatorFactory.MAX_LENGTH_255.validate(value).isOK()) {
				flushErrorMessage();
			}

			entry.setValue((String) value);
			gatewayPropsModified = true;

			gatewayPropsViewer.refresh();
		}
	}

	/**
	 * Returns true if current gateway properties were modified.
	 * 
	 * @return true if current gateway properties were modified, false if stale.
	 */
	protected final boolean isGatewayPropertiesModified() {
		return gatewayPropertiesCellModifier.isGatewayPropsModified();
	}

	/**
	 * Set gateway properties to the cell modifier to make properties modifiable in the UI.
	 * 
	 * @param properties properties to me modified.
	 */
	protected final void setGatewayProperties(final Properties properties) {
		gatewayPropsTable.setInput(properties);
		gatewayPropertiesCellModifier.flushGatewayPropertiesModified();
	}

	/**
	 * Returns properties of current gateway. This properties may be modified.
	 * 
	 * @return gateway's properties.
	 */
	protected final Properties getGatewayProperties() {
		return (Properties) gatewayPropsTable.getSwtTableViewer().getInput();
	}

	/**
	 * Sets name for the paymentGateway. The name is required to be unique across all existing payment gateways.
	 * 
	 * @param paymentGateway the payment gateway name is set to.
	 * @return true if name was correct and was set to gateway, false otherwise.
	 */
	protected final boolean setGatewayName(final PaymentGateway paymentGateway) {
		final String candidateName = gatewayNewName;

		// current gateway's name equals to the name in UI.
		if (gatewayNewName.equals(paymentGateway.getName())) {
			return true;
		}

		final PaymentGatewayService gatewayService = ServiceLocator.getService(
				ContextIdNames.PAYMENT_GATEWAY_SERVICE);
		final List<PaymentGateway> paymentGateways = gatewayService.findAllPaymentGateways();

		for (final PaymentGateway existingGateway : paymentGateways) {
			if (candidateName.equals(existingGateway.getName())) {
				setError(AdminPaymentMessages.get().PaymentGatewayNameAlreadyExist);
				return false;
			}
		}
		paymentGateway.setName(candidateName);
		return true;
	}

	/**
	 * Checks if is gateway name not exist.
	 *
	 * @param gatewayName the gateway name
	 * @return true, if is gateway name not exist
	 */
	protected final boolean isGatewayNameNotExist(final String gatewayName) {
		final PaymentGatewayService gatewayService = ServiceLocator.getService(
				ContextIdNames.PAYMENT_GATEWAY_SERVICE);
		final List<PaymentGateway> paymentGateways = gatewayService.findAllPaymentGateways();

		for (final PaymentGateway existingGateway : paymentGateways) {
			if (gatewayName.equalsIgnoreCase(existingGateway.getName())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Set error message in the dialog's title area.
	 * 
	 * @param errorMessage error message to be set.
	 */
	protected final void setError(final String errorMessage) {
		setErrorMessage(errorMessage);
	}

	/**
	 * Flushes previously set error message.
	 */
	protected void flushErrorMessage() {
		setErrorMessage(null);
	}

	/**
	 * @return the gatewayNameText
	 */
	protected Text getGatewayNameText() {
		return gatewayNameText;
	}

	/**
	 * @return the gatewayImplCombo
	 */
	protected CCombo getGatewayImplCombo() {
		return gatewayImplCombo;
	}

	/**
	 * @return the dataBindingCtx
	 */
	protected DataBindingContext getDataBindingCtx() {
		return dataBindingCtx;
	}
	
	protected String getGatewayNewName() {
		return gatewayNewName;
	}
}
