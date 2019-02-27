/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.util.Map.Entry;
import java.util.Properties;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;


/**
 * This UI control creates properties table and allows to modify properties's values.
 */
public class EpPropertyTableControl {

	/** Constant holds a value for properties' list key column. */
	private static final int PROPERTY_KEY_WIDTH = 200;

	/** Constant holds a value for properties' list value column. */
	private static final int PROPERTY_VALUE_WIDTH = 200;

	private static final String PROPERTY_TABLE = "Property Table"; //$NON-NLS-1$

	/** The cell modifier class. Responsible for properties' modification. */
	private final PropertyCellModifier propertiesCellModifier;

	/** Table viewer to display/edit properties. */
	private final IEpTableViewer propertiesTableViewer;

	private final TableViewer propertiesSwtTableViewer;

	private final IValidator propertiesValidator;

//	private final String keyCell;

	private final String valueCell;

	private final EpPropertyTableValueModifiedListener tableValueModifiedListener;

	/**
	 * The constructor.
	 * 
	 * @param parentComposite the parent composite
	 * @param keyLabel key label of the properties table
	 * @param valueLabel value label of the properties table
	 * @param propertiesValidator validator, can be null
	 * @param tableValueModifiedListener table events listener, can be null
	 */
	protected EpPropertyTableControl(final IEpLayoutComposite parentComposite, final String keyLabel, final String valueLabel,
			final IValidator propertiesValidator, final EpPropertyTableValueModifiedListener tableValueModifiedListener) {
		/** Create single selection, editable table. */
		propertiesTableViewer = parentComposite.addTableViewer(false, EpState.EDITABLE, null, PROPERTY_TABLE);
		this.propertiesSwtTableViewer = propertiesTableViewer.getSwtTableViewer();
		final Table swtTable = propertiesTableViewer.getSwtTable();

		// add Property table columns
		propertiesTableViewer.addTableColumn(keyLabel, PROPERTY_KEY_WIDTH);
		propertiesTableViewer.addTableColumn(valueLabel, PROPERTY_VALUE_WIDTH);

		// add cell editors to make possible a property's value to be changed in UI
		propertiesSwtTableViewer.setCellEditors(new CellEditor[] { new TextCellEditor(), new TextCellEditor(swtTable) });
		propertiesCellModifier = new PropertyCellModifier();
		propertiesSwtTableViewer.setCellModifier(propertiesCellModifier);

		propertiesTableViewer.setContentProvider(new PaymentGatewayContentProvider());
		propertiesTableViewer.setLabelProvider(new PropertiesLabelProvider());
		this.propertiesValidator = propertiesValidator;
//		this.keyCell = keyLabel;
		this.valueCell = valueLabel;
		this.tableValueModifiedListener = tableValueModifiedListener;
	}

	/**
	 * Creates this property table control.
	 * 
	 * @param parentComposite the parent composite
	 * @param keyLabel key label of the properties table
	 * @param valueLabel value label of the properties table
	 * @param propertiesValidator validator, can be null
	 * @param tableValueModifiedListener table events listener, can be null
	 * @return the EpPropertyTableControl control.
	 */
	public static EpPropertyTableControl createPropertyModifierControl(final IEpLayoutComposite parentComposite, final String keyLabel,
			final String valueLabel, final IValidator propertiesValidator, final EpPropertyTableValueModifiedListener tableValueModifiedListener) {
		return new EpPropertyTableControl(parentComposite, keyLabel, valueLabel, propertiesValidator, tableValueModifiedListener);
	}

	/**
	 * Returns true if any of the properties were modified.
	 * 
	 * @return true if any of the properties were modified, false if stale.
	 */
	public final boolean isPropertiesModified() {
		return propertiesCellModifier.isPropertiesModified();
	}

	/**
	 * Specifies whether properties were validated successfully. This method uses specified validator if one was provided. This method can be
	 * overriden to provide specific validation.
	 * 
	 * @return true is all properties were successfully validated against default validator, false otherwise.
	 */
	public boolean isPropertiesValidated() {
		final Properties properties = getProperties();
		if (properties == null || propertiesValidator == null) {
			return true;
		}
		for (final Object value : properties.values()) {
			if (!propertiesValidator.validate(value).isOK()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Specifies whether properties were validated successfully. This method uses specified validator if one was provided. This method can be
	 * overriden to provide specific validation.
	 * 
	 * @return IStatus.OK is all properties were validated successfully against the default validator, or IStatus error if validation failed.
	 */
	public IStatus isPropertiesValidatedGetStatus() {
		final Properties properties = getProperties();
		if (properties == null || propertiesValidator == null) {
			return Status.OK_STATUS;
		}
		IStatus status = null;
		for (final Object value : properties.values()) {
			status = propertiesValidator.validate(value);
			if (!status.isOK()) {
				return status;
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Set properties to this control.
	 * 
	 * @param properties properties to me displayed and modified.
	 */
	public final void setProperties(final Properties properties) {
		propertiesTableViewer.setInput(properties);
		propertiesCellModifier.flushPropertiesModified();
	}

	/**
	 * Returns properties. This properties may be modified.
	 * 
	 * @return properties either initial or modified.
	 */
	public final Properties getProperties() {
		return (Properties) propertiesTableViewer.getSwtTableViewer().getInput();
	}

	/**
	 * Properties content provider.
	 */
	private static final class PaymentGatewayContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
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
	private static final class PropertiesLabelProvider extends LabelProvider implements ITableLabelProvider {
		/**
		 * The constructor.
		 */
		PropertiesLabelProvider() {
			super();
		}

		private static final int PROP_KEY_INDEX = 0;

		private static final int PROP_VALUE_INDEX = 1;

		/**
		 * Payment properties table isn't supposed to produce some images.
		 * 
		 * @param element
		 * @param columnIndex
		 * @return null
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {

			if (columnIndex == PROP_VALUE_INDEX) {
				return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
			}
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {

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

		/** Specifies whether any of properties were modified or not. */
		private boolean propertiesModified;

		private boolean isPropertiesModified() {
			return propertiesModified;
		}

		private void flushPropertiesModified() {
			propertiesModified = false;
		}

		/**
		 * Constructs this cell editor.
		 */
		PropertyCellModifier() {
			// do nothing
		}

		/**
		 * Allows to modify only value of a property.
		 * 
		 * @return true if user is going to modify property's value, false if property's key.
		 */
		public boolean canModify(final Object element, final String property) {
			return property.equals(valueCell);
		}

		@Override
		public Object getValue(final Object element, final String property) {
			if (!(element instanceof Entry)) {
				return null;
			}

			final Entry<String, String> entry = (Entry<String, String>) element;

			if (tableValueModifiedListener != null) {
				tableValueModifiedListener.onPrepareForModification(entry);
			}

			return entry.getValue();
		}

		@Override
		public void modify(final Object element, final String property, final Object value) {
			Object entryObj = null;
			if (element instanceof Item) {
				entryObj = ((Item) element).getData();
			} else if (element instanceof Entry) {
				entryObj = element;
			} else {
				return;
			}
			final String newPropertyValue = (String) value;
			final Entry<String, String> entry = (Entry<String, String>) entryObj;

			entry.setValue(newPropertyValue);
			if (tableValueModifiedListener != null && tableValueModifiedListener.onModification(entry, newPropertyValue)) {
				propertiesModified = true;
				tableValueModifiedListener.onPostModification(entry);
			}

			propertiesSwtTableViewer.refresh();
		}
	}
}
