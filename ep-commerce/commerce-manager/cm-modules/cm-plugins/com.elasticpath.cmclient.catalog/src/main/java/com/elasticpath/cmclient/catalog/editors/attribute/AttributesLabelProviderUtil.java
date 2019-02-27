/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import java.util.Date;

import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * 
 * The utility class provide the label provider for the attribute pages. It can be shared in all
 * attribute related pages, e.g. the Category/Product/Sku editor attribute page, and the wizards. 
 *
 */
@SuppressWarnings({"PMD.CyclomaticComplexity" })
public class AttributesLabelProviderUtil {
	
	
	private EpState rolePermission;

	/**
	 * Constructor.
	 * @param rolePermission the role permission
	 */
	public AttributesLabelProviderUtil(final EpState rolePermission) {
		this.rolePermission = rolePermission;
	}
	
	/**
	 * Set the label for attribute name column.
	 * @param nameColumn the name column
	 */
	public void setNameColumnLabel(final IEpTableColumn nameColumn) {
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final AttributeValue attribute = (AttributeValue) element;
				return attribute.getAttribute().getName();
			}
		});
	}
	
	/**
	 * Set the label for attribute type column.
	 * @param typeColumn the type column
	 */
	public void setTypeColumnLabel(final IEpTableColumn typeColumn) {
		typeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final AttributeValue attribute = (AttributeValue) element;
				
				String type = CoreMessages.get().getMessage(attribute.getAttributeType()
						.getNameMessageKey());
				if (attribute.getAttribute().isMultiValueEnabled()) {
					type = type + " " + CatalogMessages.get().ProductEditorAttributeSection_MultiValue; //$NON-NLS-1$
				}
				return type;
			}
		});
	}
	
	/**
	 * Set the label for attribute required column.
	 * @param requiredColumn the type column
	 */
	public void setRequiredColumnLabel(final IEpTableColumn requiredColumn) {
		requiredColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final AttributeValue attributeValue = (AttributeValue) element;
				if (attributeValue.getAttribute().isRequired()) {
					return CoreMessages.get().YesNoForBoolean_true;
				}
				return CoreMessages.get().YesNoForBoolean_false;
			}
		});
	}
	
	/**
	 * Set the label for attribute multi-language column.
	 * @param multiLanguageColumn the type column
	 */
	public void setMultiLanguageColumnLabel(final IEpTableColumn multiLanguageColumn) {
		multiLanguageColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(final Object element) {
				final AttributeValue attributeValue = (AttributeValue) element;
				if (attributeValue.getAttribute().isLocaleDependant()) {
					return CoreMessages.get().YesNoForBoolean_true;
				}
				return CoreMessages.get().YesNoForBoolean_false;
			}
		});
	}
	
	
	/**
	 * Set valueColumn label.
	 * @param valueColumn the value column
	 */
	public void setValueColumnLabel(final IEpTableColumn valueColumn) {
		valueColumn.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public Image getImage(final Object element) {
				if (rolePermission == EpState.EDITABLE) {
					return CoreImageRegistry
							.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
				}
				return null;
			}

			@Override
			public String getText(final Object element) {
				return getAttributeValueText((AttributeValue) element);
			}

		});

	}
		
	
	/**
	 * Gets the attribute value.
	 * 
	 * @param attribute {@link AttributeValue}
	 * @return String
	 */
	@SuppressWarnings("PMD.MissingBreakInSwitch")
	static String getAttributeValueText(final AttributeValue attribute) {
		if (attribute.getValue() == null) {
			return CatalogMessages.get().Product_NotAvailable;
		}
		switch (attribute.getAttributeType().getTypeId()) {
		case AttributeType.BOOLEAN_TYPE_ID:
			if ((Boolean) attribute.getValue()) {
				return CatalogMessages.get().ProductEditorAttributeSection_Yes;
			}
			return CatalogMessages.get().ProductEditorAttributeSection_No;
		case AttributeType.INTEGER_TYPE_ID:
			return attribute.getValue().toString();
		case AttributeType.DATE_TYPE_ID:
			return DateTimeUtilFactory.getDateUtil().formatAsDate((Date) attribute.getValue());
		case AttributeType.DECIMAL_TYPE_ID:
			return attribute.getStringValue();
		case AttributeType.IMAGE_TYPE_ID:
			return attribute.getStringValue();
		case AttributeType.SHORT_TEXT_TYPE_ID:
		default:
			return attribute.getStringValue();
		}
	}
	
	/**
	 * the setter of rolePermission attribute.
	 * @param rolePermission the rolePermission object. 
	 */
	public void setRolePermission(final EpState rolePermission) {
		this.rolePermission = rolePermission;
	}
	
	/**
	 * the getter of rolePermission.
	 * @return the rolePermission object.
	 */
	public EpState getRolePermission() {
		return rolePermission;
	}



}
