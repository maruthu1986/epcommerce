/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.ValidationErrorLocation;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.ui.framework.AbstractInlineEditingSupport;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.actions.ContributedAction;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModel;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModelFactory;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.UiProperty;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.impl.ManagedModelFactoryImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;

/**
 * A factory which creates and/or populates the OrderItemFieldValue composite.
 */
public class OrderItemFieldValueCompositeFactory  {

	private static final Logger LOG = Logger.getLogger(OrderItemFieldValueCompositeFactory.class);

	private static final int TABLE_HEIGHT_HINT = 110;
	private static final int KEY_SIZE = 160;
	private static final int VALUE_SIZE = 220;
	
	private static final int INDEX_KEY = 0;
	private static final int INDEX_VALUE = 1;
	private static final String FIELD_VALUE_TABLE = "Field Value Table"; //$NON-NLS-1$

	private final FieldValueAdapter fieldValueProvider;
	private final boolean editable;
	private final List<ContributedAction> contributedActions;

	private final List<Button> contributedActionsButtons = new ArrayList<>();
	private IEpTableViewer fieldValueTable;
	private final ManagedModel<String, String>[] values;
	private final ManagedModelFactory<String, String> managedModelFactory = new ManagedModelFactoryImpl();

	private IEpTableColumn valueColumn;

	private SimpleTextEditingSupport dataValueEditingSupport;

	/**
	 * Constructs a factory which creates and/or populates the OrderItemFieldValue composite.
	 *
	 * @param fieldValueProvider a FieldValueProvider (to provide field values)
	 * @param contributedActions external pluggable actions
	 * @param editable true if the composite should enable editing 
	 */
	public OrderItemFieldValueCompositeFactory(
			final FieldValueAdapter fieldValueProvider,
			final List<ContributedAction> contributedActions,
			final boolean editable
			) {
		this.editable = editable;
		this.fieldValueProvider = fieldValueProvider;
		this.contributedActions = contributedActions;

		UiProperty uiProperty = UiProperty.READ_ONLY;
		if (this.editable) {
			uiProperty = UiProperty.READ_WRITE;
		}
		Map<String, String> fieldDataValues = fieldValueProvider.getFieldValues();
		List<ManagedModel<String, String>> model = new ArrayList<>(fieldDataValues.size());
		
		for (Map.Entry<String, String> entry : fieldDataValues.entrySet()) {
			model.add(managedModelFactory.create(entry.getKey(), entry.getValue(), uiProperty));
		}
		model = this.filterDisplayData(model);
		
		model.sort(Comparator.comparing(ManagedModel::getKey));
		values = model.stream().toArray((IntFunction<ManagedModel<String, String>[]>) ManagedModel[]::new);
	}

	/** Try to filter display data via extension point. */
	private List<ManagedModel<String, String>> filterDisplayData(final List<ManagedModel<String, String>> displayDataMap) {
		List<ManagedModel<String, String>> resultMap = displayDataMap;
		for (ContributedAction action : this.contributedActions) {
			resultMap = action.filterDisplayData(resultMap);
		}
		return resultMap;
	}

	/**
	 * Creates the Composite which holds the Order/OrderSku Field Value table and plugin buttons
	 * and adds it to the parent.
	 *
	 * @param parent the parent composite
	 * @return the composite
	 */
	public IEpLayoutComposite createComposite(final Composite parent) {
		final IEpLayoutComposite composite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		final IEpLayoutData tableData = composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IEpLayoutData buttonData = composite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, true);
		
		createContributedActionButtons(composite, buttonData);

		fieldValueTable = composite.addTableViewer(true, EpState.EDITABLE, tableData, FIELD_VALUE_TABLE);
		fieldValueTable.addTableColumn(FulfillmentMessages.get().EditItemDetails_PropertyKey, KEY_SIZE);
		valueColumn = fieldValueTable.addTableColumn(FulfillmentMessages.get().EditItemDetails_PropertyValue, VALUE_SIZE);
		
		final IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				}
				return new Object[0];
			}
			@Override
			public void dispose() { //NOPMD
			}
			@Override
			public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) { //NOPMD
			}
			
		};
		
		final GridData gridData = new GridData();
		gridData.heightHint = TABLE_HEIGHT_HINT;
		fieldValueTable.setLayoutData(gridData);
		
		fieldValueTable.setContentProvider(contentProvider);
		fieldValueTable.setLabelProvider(new FieldValueLabelProvider());		
		fieldValueTable.setInput(values);
		
		return composite;
	}
	
	/**
	 * Binds the controls to the given context.
	 *
	 * @param bindingContext the binding context
	 */
	public void bindControls(final DataBindingContext bindingContext) {
		dataValueEditingSupport = new SimpleTextEditingSupport(
				fieldValueTable.getSwtTableViewer(), bindingContext);

		if (editable) {
			valueColumn.setEditingSupport(dataValueEditingSupport);			
		}
	}
	
	/**
	 * Adds a listener which will be notified when an edit is made.
	 *
	 * @param listener the ControlModificationListener, or null to clear
	 */
	public void addControlModificationListener(final ControlModificationListener listener) {
		dataValueEditingSupport.addControlModificationListener(listener);
	}
	
	/**
	 * Removes a listener which will be notified when an edit is made.
	 *
	 * @param listener the ControlModificationListener
	 */
	public void removeControlModificationListener(final ControlModificationListener listener) {
		dataValueEditingSupport.removeControlModificationListener(listener);
	}
	
	private void createContributedActionButtons(final IEpLayoutComposite dialogComposite, final IEpLayoutData buttonData) {
		for (ContributedAction action : this.contributedActions) {
			executeContributedAction(action, dialogComposite, buttonData);
		}
	}
	
	private void executeContributedAction(final ContributedAction cAction, 
			final IEpLayoutComposite dialogComposite, final IEpLayoutData buttonData) {
			ISafeRunnable runnable = new ISafeRunnable() {
			
			@Override
			public void handleException(final Throwable exception) {
				LOG.error("Failed to add contributed action", exception); //$NON-NLS-1$
			}

			@Override
			public void run() throws Exception {
				Button button = cAction.createActionControl(dialogComposite, buttonData);				
				if (button != null) {
					contributedActionsButtons.add(button);
				}
			}
			
		};
		SafeRunner.run(runnable);
	}
	
	/**
	 * Saves the state of the internal model back to the source object via the fieldValueProvider. 
	 *
	 * @return true if changes were detected and saved, false otherwise
	 */
	public boolean saveChanges() {
		boolean changed = false;
		if (editable) {
			final Map<String, String> fieldDataValues = fieldValueProvider.getFieldValues();
			for (ManagedModel<String, String> keyValue : values) {
				String oldValue = fieldDataValues.get(keyValue.getKey());
				if (oldValue != null && !oldValue.equals(keyValue.getValue())) {
					fieldValueProvider.setFieldValue(
							keyValue.getKey(),
							keyValue.getValue()
						);
					changed = true;
				}
			}		
		}
		
		return changed;
	}

	/**
	 * Returns the ContributedActions that have been registered for the given extension point.
	 *
	 * @param contributedActionsExtensionPoint the extension point id
	 * @param order the Order
	 * @param orderSku the OrderSku, if any
	 * @return a list of contributed actions for the given extension point id
	 */
	public static List<ContributedAction> getContributedActions(
			final String contributedActionsExtensionPoint, final Order order, final OrderSku orderSku) {
		List<ContributedAction> result = new LinkedList<>();
		try {
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(contributedActionsExtensionPoint);
			for (IConfigurationElement element : config) {
				ContributedAction action = (ContributedAction) element.createExecutableExtension("class"); //$NON-NLS-1$
				result.add(action);
				action.init(order, orderSku);
			}
		} catch (Exception e) {
			LOG.error("Failed to get filtered display data from ContributedAction", e); //$NON-NLS-1$
		}
		return result;
	}

	/**
	 * Label provider for Cell Editor.
	 */
	class FieldValueLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Returns text for specified column.
		 * 
		 * @param arg0 - model object
		 * @param columnIndex - index of the column starting with 0
		 * @return text value for the column
		 */
		@Override
		@SuppressWarnings("unchecked")
		public String getColumnText(final Object arg0, final int columnIndex) {
			ManagedModel<String, String> pair = (ManagedModel<String, String>) arg0;
			switch (columnIndex) {
			case INDEX_KEY:
				return pair.getKey();
			case INDEX_VALUE:
				return pair.getValueForUI();
			default :
				return StringUtils.EMPTY;
			}
			
		}

		/**
		 * Returns column image.
		 * 
		 * @param arg0 - argument
		 * @param arg1 - argument
		 * @return image for the column
		 */
		@Override
		public Image getColumnImage(final Object arg0, final int arg1) {
			return null;
		}		
		
	}
	
	/**
	 * 
	 * Simple editing support.
	 *
	 */
	class SimpleTextEditingSupport extends AbstractInlineEditingSupport {
		private final List<ControlModificationListener> controlModificationListeners =
				new ArrayList<>();

		/**
		 * Constructor.
		 *
		 * @param viewer the column viewer
		 * @param bindingContext the data binding context
		 */
		SimpleTextEditingSupport(final ColumnViewer viewer,
				final DataBindingContext bindingContext) {
			super(viewer, bindingContext);
		}

		@Override
		protected void initializeCellEditorValue(final CellEditor cellEditor,
				final ViewerCell cell) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpBindingConfiguration bindingConfig = new EpBindingConfiguration(getBindingContext(), 
					cellEditor.getControl(), cell.getElement(),
					"value"); //$NON-NLS-1$
			cellEditor.setValue(cell.getText());			
			bindingConfig.configureUiToModelBinding(null, EpValidatorFactory.MAX_LENGTH_65535, true);
			bindingConfig.setErrorLocation(ValidationErrorLocation.LEFT);
			setBinding(bindingProvider.bind(bindingConfig));
			getViewer().getColumnViewerEditor().addEditorActivationListener(getActivationListener());
		}

		@SuppressWarnings("unchecked")
		@Override
		protected boolean canEdit(final Object element) {
			ManagedModel<String, String> testObject = (ManagedModel<String, String>) element;
			return UiProperty.READ_WRITE == testObject.getUiProperty();
		}
		
		@Override
		protected void saveCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
			if (!ObjectUtils.equals(cellEditor.getValue(), cell.getText())) {
				super.saveCellEditorValue(cellEditor, cell);
				
				for (ControlModificationListener listener : controlModificationListeners) {
					listener.controlModified();
				}
			}
		}
		
		/**
		 * Adds the listener (i.e. editor) to be notified when changes occur.
		 *
		 * @param listener the listener
		 */
		public void addControlModificationListener(final ControlModificationListener listener) {
			controlModificationListeners.add(listener);
		}
		
		/**
		 * Removes the listener from the notification list.
		 *
		 * @param listener the listener
		 */
		public void removeControlModificationListener(final ControlModificationListener listener) {
			controlModificationListeners.remove(listener);
		}
	}
	
	/**
	 * Adapter interface which allows this dialog to access field value data from multiple sources.
	 */
	public interface FieldValueAdapter {
		/**
		 * Gets a map of the Field Data Values for the entity.
		 *
		 * @return the field values
		 */
		Map<String, String> getFieldValues();

		/**
		 * Sets a field data value in the entity.
		 *
		 * @param key the data key
		 * @param value the value
		 */
		void setFieldValue(String key, String value);
	}


	/**
	 * FieldValueAdapter for Order Data.
	 */
	public static class OrderSkuFieldValueAdapter implements FieldValueAdapter {
		private final OrderSku orderSku;

		/**
		 * Constructor.
		 *
		 * @param orderSku the order sku
		 */
		public OrderSkuFieldValueAdapter(final OrderSku orderSku) {
			this.orderSku = orderSku;
		}
		
		@Override
		public Map<String, String> getFieldValues() {
			return orderSku.getFields();
		}

		@Override
		public void setFieldValue(final String key, final String value) {
			orderSku.setFieldValue(key, value);
		}
	}

	/**
	 * FieldValueAdapter for Order Data.
	 */
	public static class OrderDataFieldValueAdapter implements FieldValueAdapter {

		private final Order order;

		/**
		 * Constructor.
		 *
		 * @param order the order
		 */
		public OrderDataFieldValueAdapter(final Order order) {
			this.order = order;
		}
		
		@Override
		public Map<String, String> getFieldValues() {
			return order.getFieldValues();
		}

		@Override
		public void setFieldValue(final String key, final String value) {
			order.setFieldValue(key, value);
		}
	}
}
