/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.dto.catalog.ProductSkuModel;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Editing support for attributes' table.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass", "PMD.PrematureDeclaration" })
public class AttributeEditingSupport extends EditingSupport {

	private IEpTableViewer attributesTableViewer;
	private Object model;
	private final ICellEditorDialogService cellEditorDialogService;

	private final List<IAttributeChangedListener> attributeValueChangedListeners = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param attributesTableViewer the EP table viewer
	 * @param model the model that have these attributes, could be a <code>Category</code>, <code>Product</code>, or <code>ProductSku</code>
	 * @param cellEditorDialogService service that will be triggered when clicked on the editable cell
	 */
	public AttributeEditingSupport(final IEpTableViewer attributesTableViewer, final Object model,
		final ICellEditorDialogService cellEditorDialogService) {
		super(attributesTableViewer.getSwtTableViewer());
		this.attributesTableViewer = attributesTableViewer;
		this.model = model;
		this.cellEditorDialogService = cellEditorDialogService;
	}

	/**
	 * Add listener.
	 * @param attributeChangedListener the listener
	 * @return true if added
	 */
	public boolean addAttributeChangedListener(final IAttributeChangedListener attributeChangedListener) {
		return attributeValueChangedListeners.add(attributeChangedListener);
	}

	/**
	 * Remove listener.
	 * @param attributeChangedListener the listener
	 * @return true if removed
	 */
	public boolean removeAttributeChangedListener(final IAttributeChangedListener attributeChangedListener) {
		return attributeValueChangedListeners.remove(attributeChangedListener);
	}

	@Override
	protected boolean canEdit(final Object element) {
		return true;
	}

	private Object getAttributeValue(final Object element) {
		return getValueOfAttributeValue(element);
	}

	@Override
	protected CellEditor getCellEditor(final Object element) {
		cellEditorDialogService.createEditorDialog();
		//Return null as we don't want any cell editing for the table cells, rather open a dialog for editing
		return null;
	}

	@Override
	protected Object getValue(final Object element) {
		return getAttributeValue(element);
	}

	@Override
	protected void setValue(final Object element, final Object value) {
		final AttributeValue attrValue = (AttributeValue) element;

		if (!hasValueChanged(value, attrValue)) {
			return;
		}

		switch (attrValue.getAttributeType().getTypeId()) {
		case AttributeType.FILE_TYPE_ID:
			if (!setShortTextTypeValue((String) value, attrValue)) {
				return;
			}
			break;
		case AttributeType.IMAGE_TYPE_ID:
			if (!setShortTextTypeValue((String) value, attrValue)) {
				return;
			}
			break;
		case AttributeType.SHORT_TEXT_TYPE_ID:
			// Check for multi-valued attributes since they are a special case of SHORT_TEXT.
			if (!attrValue.getAttribute().isMultiValueEnabled()) {
				if (!setShortTextTypeValue((String) value, attrValue)) {
					return;
				}
				break;
			}
			// Fall through for handling multi-values.
		case AttributeType.LONG_TEXT_TYPE_ID:
		case AttributeType.DATETIME_TYPE_ID:
		default:
			attrValue.setValue(value);
		}

		this.updateModel(attrValue);
		this.attributesTableViewer.getSwtTableViewer().update(attrValue, null);

		fireAttributeChangedEvent(attrValue);
	}

	private boolean setShortTextTypeValue(final String value, final AttributeValue attrValue) {
		// handle single value
		final int maxShortTextChars = 255;
		if ("".equals(value) && attrValue.getValue() == null) { //$NON-NLS-1$
			return false;
		}
		// limit short text less than 255 chars. the chars after 255th
		// char will be ignored.
		if (value.length() > maxShortTextChars) {
			attrValue.setValue(value.substring(0, maxShortTextChars - 1));
			return true;
		}
		attrValue.setValue(value);
		return true;
	}

	/**
	 * Fire attribute changed event to all attribute changed listeners.
	 * @param attr attribute that has changed
	 */
	private void fireAttributeChangedEvent(final AttributeValue attr) {
		for (IAttributeChangedListener attributeChangedListener : this.attributeValueChangedListeners) {
			attributeChangedListener.attributeValueChanged(attr);
		}
	}

	/**
	 * Determines if the attribute value has changed.
	 * @param value new value
	 * @param attrValue attribute value to check if it has changed
	 * @return boolean depending on whether it has changed
	 */
	private boolean hasValueChanged(final Object value, final AttributeValue attrValue) {

		// a multi-stage editor (e.g. long text) caused null to be returned
		// the value cannot have been changed.
		if (value == null || StringUtils.isBlank(value.toString())) {
			return false;
		}

		if (attrValue.getValue() == null) {
			return true;
		}

		return !attrValue.getValue().equals(value);
	}

	private Object getValueOfAttributeValue(final Object element) {
		final AttributeValue attribute = (AttributeValue) element;
		Object result;
		final int type = attribute.getAttributeType().getTypeId();
		switch (type) {
		case AttributeType.LONG_TEXT_TYPE_ID:
			result = attribute.getValue();
			break;
		case AttributeType.SHORT_TEXT_TYPE_ID:
			// NOTE: This is going to keep working for multi-value enabled attributes right?
			// see com.elasticpath.domain.attribute.impl.AbstractAttributeValueImpl.getStringValue()
			result = attribute.getValue();
			if (result == null) {
				result = ""; //$NON-NLS-1$
			}
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			result = attribute.getValue();
			break;
		case AttributeType.BOOLEAN_TYPE_ID:
			result = attribute.getValue();
			if (result == null) {
				result = Boolean.FALSE;
			}
			break;
		case AttributeType.IMAGE_TYPE_ID:
			result = attribute.getValue();
			if (result == null) {
				result = ""; //$NON-NLS-1$
			}
			break;
		case AttributeType.FILE_TYPE_ID:
			result = attribute.getValue();
			if (result == null) {
				result = ""; //$NON-NLS-1$
			}
			break;

		case AttributeType.DATE_TYPE_ID:
			if (attribute.getValue() instanceof String) {
				result = attribute.getValue();
			} else {
				if (attribute.getValue() == null) {
					result = null;
				} else {
					result = DateTimeUtilFactory.getDateUtil().getDateWithFormat((Date) attribute.getValue());
				}
			}
			break;

		default:
			result = attribute.getValue();
			break;
		}
		return result;
	}

	/**
	 * Get the EP table viewer.
	 * @return the EP table viewer
	 */
	public IEpTableViewer getAttributesTableViewer() {
		return attributesTableViewer;
	}

	/**
	 * Set the EP table viewer.
	 * @param attributesTableViewer the ep table viewer
	 */
	public void setAttributesTableViewer(final IEpTableViewer attributesTableViewer) {
		this.attributesTableViewer = attributesTableViewer;
	}

	/**
	 * Get model.
	 * @return the model
	 */
	public Object getModel() {
		return model;
	}

	/**
	 * Set model.
	 * @param model the model that have attribute values, could be Category,Product,or ProductSku
	 */
	public void setModel(final Object model) {
		this.model = model;
	}

	/**
	 * Update the attributeValue to model.
	 * @param attributeValue the attribute value
	 */
	public void updateModel(final AttributeValue attributeValue) {
		if (model != null) {
			if (model instanceof Category) {
				((Category) model).getAttributeValueMap().put(attributeValue.getLocalizedAttributeKey(), attributeValue);
			} else if (model instanceof ProductModel) {
				((ProductModel) model).getProduct().getAttributeValueMap().put(attributeValue.getLocalizedAttributeKey(), attributeValue);
			} else if (model instanceof ProductSkuModel) {
				((ProductSkuModel) model).getProductSku().getAttributeValueMap().put(attributeValue.getLocalizedAttributeKey(), attributeValue);
			} else if (model instanceof ProductSku) {
				((ProductSku) model).getAttributeValueMap().put(attributeValue.getLocalizedAttributeKey(), attributeValue);
			}
		}
	}

	/**
	 * Create the dialogs for different type attribute values.
	 * @param attr the attribute value
	 * @param shell the parent shell
	 * @return the editor dialog
	 */
	public static Window getEditorDialog(final AttributeValue attr, final Shell shell) {
		Window dialog = null;
		switch (attr.getAttributeType().getTypeId()) {
		case AttributeType.BOOLEAN_TYPE_ID:
			dialog = new BooleanDialog(shell, attr.getValue());
			break;
		case AttributeType.DATE_TYPE_ID:
			dialog = new DateTimeDialog(shell, attr.getValue(),
					IEpDateTimePicker.STYLE_DATE);
			break;
		case AttributeType.DATETIME_TYPE_ID:
			dialog = new DateTimeDialog(shell, attr.getValue(),
					IEpDateTimePicker.STYLE_DATE
							| IEpDateTimePicker.STYLE_DATE_AND_TIME);
			break;
		case AttributeType.DECIMAL_TYPE_ID:
			dialog = new DecimalDialog(shell, attr.getValue());
			break;
		case AttributeType.INTEGER_TYPE_ID:
			dialog = new IntegerDialog(shell, attr.getValue());
			break;
		case AttributeType.LONG_TEXT_TYPE_ID:
			dialog = new LongTextDialog(shell, attr.getValue());
			break;
		case AttributeType.SHORT_TEXT_TYPE_ID:
			dialog = attr.getAttribute().isMultiValueEnabled() ? new ShortTextMultiValueDialog(shell, attr)
					: new ShortTextDialog(shell, attr.getValue(), true);
			break;
		case AttributeType.IMAGE_TYPE_ID:
			dialog = new ShortTextDialog(shell, attr.getValue(), true);
			break;
		case AttributeType.FILE_TYPE_ID:
			dialog = new ShortTextDialog(shell, attr.getValue(), true);
			break;
		default:
			// throw new RuntimeException("Unknown attribute type");
		}
		return dialog;
	}

}