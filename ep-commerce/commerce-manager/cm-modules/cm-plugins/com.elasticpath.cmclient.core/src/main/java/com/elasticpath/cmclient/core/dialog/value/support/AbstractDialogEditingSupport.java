/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.dialog.value.support;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.commons.constants.ValueTypeEnum;

/**
 * Editing support for attributes' table.
 * 
 * @param <M> the type of model
 * @param <V> the type of value of model
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.PrematureDeclaration" })
public abstract class AbstractDialogEditingSupport<M, V> extends EditingSupport {

	private static final Logger LOG = Logger.getLogger(AbstractDialogEditingSupport.class);

	private static final EditingSupportDialogFactory DEFAULT_DIALOG_FACTORY = new SimpleEditingSupportDialogFactory();
	
	private EditingSupportDialogFactory editingSupportDialogFactory = DEFAULT_DIALOG_FACTORY;
	
	private static final EditingSupportCellEditorFactory DEFAULT_CELL_EDITOR_FACTORY = new SimpleEditingSupportCellEditorFactory();
	
	private EditingSupportCellEditorFactory editingSupportCellEditorFactory = DEFAULT_CELL_EDITOR_FACTORY;

	private IEpTableViewer attributesTableViewer;

	private M model;
		
	/** Default label provider (no label). */
	private static final DialogValueLabelProvider DEFAULT_LABEL_PROVIDER = new DialogValueLabelProvider() {

		@Override
		public String getLabelText() {
			return null;
		}

		@Override
		public boolean isLabelBold() {
			return false;
		}

	};

	private DialogValueLabelProvider labelProvider = DEFAULT_LABEL_PROVIDER;

	private final List<IValueChangedListener<V>> valueChangedListeners = new ArrayList<IValueChangedListener<V>>();

	/**
	 * Constructor.
	 * 
	 * @param attributesTableViewer the EP table viewer
	 * @param model the model that have these attributes
	 */
	public AbstractDialogEditingSupport(final IEpTableViewer attributesTableViewer, final M model) {
		super(attributesTableViewer.getSwtTableViewer());
		this.attributesTableViewer = attributesTableViewer;
		this.model = model;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param attributesTableViewer the EP table viewer
	 * @param model the model that have these attributes
	 * @param labelProvider the provider of labels for dialogs (use null for no labels)
	 */
	public AbstractDialogEditingSupport(final IEpTableViewer attributesTableViewer, final M model, 
			final DialogValueLabelProvider labelProvider) {
		super(attributesTableViewer.getSwtTableViewer());
		this.attributesTableViewer = attributesTableViewer;
		this.model = model;
		if (labelProvider != null) {
			this.labelProvider = labelProvider;
		}
	}
	
	/**
	 * Constructor.
	 * 
	 * @param attributesTableViewer the EP table viewer
	 * @param model the model that have these attributes
	 * @param labelProvider the provider of labels for dialogs (use null for no labels)
	 * @param editingSupportDialogFactory the simple factory for returning the correct dialog (use null for default)
	 * @param editingSupportCellEditorFactory the simple factory for returning the correct cell editor (use null for default)
	 */
	public AbstractDialogEditingSupport(final IEpTableViewer attributesTableViewer, final M model, 
			final DialogValueLabelProvider labelProvider,
			final EditingSupportDialogFactory editingSupportDialogFactory,
			final EditingSupportCellEditorFactory editingSupportCellEditorFactory) {
		super(attributesTableViewer.getSwtTableViewer());
		this.attributesTableViewer = attributesTableViewer;
		this.model = model;
		if (labelProvider != null) {
			this.labelProvider = labelProvider;
		}
		if (editingSupportDialogFactory != null) {
			this.editingSupportDialogFactory = editingSupportDialogFactory;
		}
		if (editingSupportCellEditorFactory != null) {
			this.editingSupportCellEditorFactory = editingSupportCellEditorFactory;
		}
	}

	@Override
	protected boolean canEdit(final Object element) {
		return true;
	}

	// CELL EDITOR SELECTION --------------------------------------------------------

	@Override
	protected CellEditor getCellEditor(final Object element) {
		final V value = (V) element;
		return getCellEditor(attributesTableViewer.getSwtTable(), getTypeOfValue(value), extractValueFromElement(value), isRequiredElement(value));
	}

	/**
	 * extract type of value from value.
	 * 
	 * @param value the value object
	 * @return enumeration type
	 */
	protected abstract ValueTypeEnum getTypeOfValue(final V value);

	/**
	 * selector for the correct cell editor depending on enum type provided.
	 * 
	 * @param table the table container
	 * @param type the type of value
	 * @param value the value of cell
	 * @param valueRequired true if value is required, false is value is not required
	 *  
	 * @return cell editor
	 */
	private CellEditor getCellEditor(final Table table, final ValueTypeEnum type, final Object value, final boolean valueRequired) {

		final EditingSupportCellEditorFactory cellEditorFactory;
		if (editingSupportCellEditorFactory == null) {
			cellEditorFactory = DEFAULT_CELL_EDITOR_FACTORY;
		} else {
			cellEditorFactory = editingSupportCellEditorFactory;
		}
		
		return cellEditorFactory.getCellEditor(table, type, value, valueRequired, labelProvider);
		
	}

	// END OF CELL EDITOR SELECTION --------------------------------------------------------

	// VALUE MANAGEMENT --------------------------------------------------------------------

	/**
	 * Get model.
	 * 
	 * @return the model
	 */
	public M getModel() {
		return model;
	}

	/**
	 * Set model.
	 * 
	 * @param model the model that have attribute values, could be Category,Product,or ProductSku
	 */
	public void setModel(final M model) {
		this.model = model;
	}

	@Override
	protected final Object getValue(final Object element) {
		final V value = (V) element;
		return extractValueFromElement(value);
	}

	/**
	 * Extracts value from element object.
	 * 
	 * @param value the element to extract value for editing from
	 * @return value suitable for editing
	 */
	protected abstract Object extractValueFromElement(final V value);

	/**
	 * Defines  if value for element is required.
	 * 
	 * @param value the element 
	 * @return true if value is required, false is value is not required
	 */
	protected abstract boolean isRequiredElement(final V value);

	@Override
	protected final void setValue(final Object element, final Object value) {
		final V attrValue = (V) element;

		final boolean fireUpdate = setValueToElement(attrValue, value);

		if (fireUpdate) {
			this.updateModel(attrValue);
			this.attributesTableViewer.getSwtTableViewer().update(attrValue, null);

			fireValueChangedEvent(attrValue);
		}
	}

	/**
	 * Sets the value of the element.
	 * 
	 * @param element the element that should accept the value
	 * @param value the value to update to
	 * @return true if update is successful, false otherwise.
	 */
	protected abstract boolean setValueToElement(final V element, final Object value);

	/**
	 * Update the element to model.
	 * 
	 * @param valueElement the attribute value
	 */
	public abstract void updateModel(final V valueElement);

	// END OF VALUE MANAGEMENT --------------------------------------------------------------------

	// LISTENERS ----------------------------------------------------------------------------------

	/**
	 * Fire attribute changed event to all attribute changed listeners.
	 * 
	 * @param element attribute that has changed
	 */
	public void fireValueChangedEvent(final V element) {
		for (IValueChangedListener<V> attributeChangedListener : this.valueChangedListeners) {
			attributeChangedListener.valueChanged(element);
		}
	}

	/**
	 * Add listener.
	 * 
	 * @param valueChangedListener the listener
	 * @return true if added
	 */
	public boolean addValueChangedListener(final IValueChangedListener<V> valueChangedListener) {
		return valueChangedListeners.add(valueChangedListener);
	}

	/**
	 * Remove listener.
	 * 
	 * @param valueChangedListener the listener
	 * @return true if removed
	 */
	public boolean removeValueChangedListener(final IValueChangedListener<V> valueChangedListener) {
		return valueChangedListeners.remove(valueChangedListener);
	}

	// END OF LISTENERS ----------------------------------------------------------------------------------
	

	/**
	 * Get the EP table viewer.
	 * 
	 * @return the EP table viewer
	 */
	public IEpTableViewer getAttributesTableViewer() {
		return attributesTableViewer;
	}

	/**
	 * Set the EP table viewer.
	 * 
	 * @param attributesTableViewer the ep table viewer
	 */
	public void setAttributesTableViewer(final IEpTableViewer attributesTableViewer) {
		this.attributesTableViewer = attributesTableViewer;
	}

	// DIALOGS FOR EDITING VALUES ---------------------------------------------------------
	
	
	/**
	 * Create the dialogs for different type attribute values. Uses editing support dialog 
	 * factory in order to create the dialogs. If factory is null then default factory 
	 * (SimpleEditingSupportDialogFactory) is used.
	 * 
	 * @param valueType the type of value
	 * @param value the value to edit (&lt;V&gt;)(should be castable to correct type)
	 * @param shell the parent shell
	 * @param editMode the mode of dialog
	 * @param valueRequired true if value is required, false is value is not required
	 * 
	 * @return the editor dialog
	 */
	public Window getEditorDialog(final ValueTypeEnum valueType, final V value, final Shell shell, 
			final boolean editMode, final boolean valueRequired) {
		final EditingSupportDialogFactory dialogFactory;
		if (editingSupportDialogFactory == null) {
			dialogFactory = DEFAULT_DIALOG_FACTORY;
		} else {
			dialogFactory = editingSupportDialogFactory;
		}
		
		return dialogFactory.getEditorDialog(shell, valueType, extractValueFromElement(value), editMode, labelProvider, valueRequired);
		
	}

	/**
	 * get label provider for dialogs.
	 * 
	 * @return label provider
	 */
	public DialogValueLabelProvider getLabelProvider() {
		return labelProvider;
	}

	/**
	 * set label provider for dialogs.
	 * 
	 * @param labelProvider the label provider
	 */
	public void setLabelProvider(final DialogValueLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/**
	 * Parse the multi values for short text type. The value is stored in shortTextValue, and with the
	 * SHORT_TEXT_MULTI_VALUE_SEPARATOR as the separator.
	 * 
	 * @param shortTextValue the string value which contains the multi-value for short text.
	 * @return the list of shortText value
	 */
	public static List<String> parseParameterShortTextMultiValues(final String shortTextValue) {
		LOG.debug("Parsing multi value: " + shortTextValue); //$NON-NLS-1$

		List<String> shortTextMultiValues = null;
		if ((shortTextValue == null) || (shortTextValue.length() == 0)) {
			return null;
		}
		
		StringTokenizer stValues = new StringTokenizer(shortTextValue, ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPARATOR);
		if (stValues.hasMoreTokens()) {
			shortTextMultiValues = new ArrayList<String>();
			while (stValues.hasMoreTokens()) {
				String singleValue = stValues.nextToken();
				if (StringUtils.isNotBlank(singleValue)) {
					shortTextMultiValues.add(singleValue.trim());
				}
			}
			LOG.debug("Acquired values size: " + shortTextMultiValues); //$NON-NLS-1$
		}
		return shortTextMultiValues;
	}

	/**
	 * Compile a single short text type string from list of strings provided.
	 * 
	 * @param shortTextValues the list of values
	 * @return a single string of data representing multivalues.
	 */
	public static String compileParameterShortTextMultiValues(final List<String> shortTextValues) {
		if (shortTextValues == null || shortTextValues.isEmpty()) {
			LOG.debug("Compiling multi values: null"); //$NON-NLS-1$
			return null;
		}

		LOG.debug("Compiling multi values: " + shortTextValues.size()); //$NON-NLS-1$
		
		StringBuilder compiled = new StringBuilder();
		for (String value : shortTextValues) {
			compiled.append(value);
			compiled.append(ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPARATOR);
		}
		compiled.delete(compiled.length()
				- (ImportConstants.SHORT_TEXT_MULTI_VALUE_SEPARATOR.length()),
				compiled.length()); // remove last separator
		
		final String string = compiled.toString();

		LOG.debug("Compiled multi values: " + string); //$NON-NLS-1$
		return string;
	}

}