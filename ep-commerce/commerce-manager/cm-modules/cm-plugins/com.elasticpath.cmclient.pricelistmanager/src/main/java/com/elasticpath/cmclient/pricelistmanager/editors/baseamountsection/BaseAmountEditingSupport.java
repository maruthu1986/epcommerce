/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;

import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.ValidationErrorLocation;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.conversion.EpStringToBigDecimalConverter;
import com.elasticpath.cmclient.core.helpers.BaseAmountDTOCreator;
import com.elasticpath.cmclient.core.ui.framework.AbstractInlineEditingSupport;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Editing support for <code>BaseAmountSection</code> table.
 */
public class BaseAmountEditingSupport extends AbstractInlineEditingSupport {
	
	/**
	 * List value field.
	 */
	public static final String LIST_VALUE_FIELD = "listValue"; //$NON-NLS-1$
	
	/**
	 * Sale value field.
	 */	
	public static final  String SALE_VALUE_FIELD = "saleValue"; //$NON-NLS-1$

	private final String columnName;

	private final BaseAmountSection baseAmountSection;

	private final PriceListEditorController controller;

	private BaseAmountDTO oldBaseAmountDTO;

	private final EpStringToBigDecimalConverter converter = new EpStringToBigDecimalConverter();

	private final IValidator valueValidator;

	/**
	 * Constructor.
	 * 
	 * @param viewer the column viewer
	 * @param columnName - name of the column
	 * @param dataBindingContext - data binding context
	 * @param baseAmountSection - parent section
	 * @param controller - price list change set controller 
	 * @param valueValidator - validator
	 */
	public BaseAmountEditingSupport(final ColumnViewer viewer, final String columnName, final DataBindingContext dataBindingContext,
			final BaseAmountSection baseAmountSection, final PriceListEditorController controller, final IValidator valueValidator) {
		super(viewer, dataBindingContext);
		this.columnName = columnName;
		this.baseAmountSection = baseAmountSection;
		this.controller = controller;
		this.valueValidator = valueValidator;
	}

	@Override
	protected void initializeCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		
		
		final EpBindingConfiguration bindingConfig = new EpBindingConfiguration(
				getBindingContext(), 
				cellEditor.getControl(), 
				cell.getElement(),
				columnName);
		
		bindingConfig.configureUiToModelBinding(converter, valueValidator,  true);
		bindingConfig.setErrorLocation(ValidationErrorLocation.LEFT);
		setBinding(bindingProvider.bind(bindingConfig));
		getViewer().getColumnViewerEditor().addEditorActivationListener(getActivationListener());
		cellEditor.setValue(cell.getText());
		oldBaseAmountDTO = BaseAmountDTOCreator.createModel((BaseAmountDTO) cell.getElement());
	}

	@Override
	protected boolean canEdit(final Object element) {
		BaseAmountDTO baseAmountDTO = (BaseAmountDTO) element;
		return baseAmountSection.canEdit(baseAmountDTO);
	}

	@Override
	protected void saveCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
		String newStringValue = cellEditor.getValue().toString();
		String oldStringValue = cell.getText();
		
		BaseAmountDTO baseAmountDTO = (BaseAmountDTO) cell.getElement();
		
		if (valueValidator.validate(newStringValue) != Status.OK_STATUS) {
			//revert incorrect values
			cell.setText(oldStringValue);
			if (SALE_VALUE_FIELD.equals(columnName)) {
				baseAmountDTO.setSaleValue((BigDecimal) converter.convert(oldStringValue));
			} else if (LIST_VALUE_FIELD.equals(columnName)) {
				baseAmountDTO.setListValue((BigDecimal) converter.convert(oldStringValue));
			}
			return;
		}
		
		if (StringUtils.isEmpty(newStringValue) && StringUtils.isEmpty(oldStringValue)) {
			return;
		}
		if (StringUtils.isNotEmpty(newStringValue) && StringUtils.isNotEmpty(oldStringValue)) {
			try {
				BigDecimal newValue = (BigDecimal) converter.convert(newStringValue);
				BigDecimal oldValue = (BigDecimal) converter.convert(oldStringValue);
				if (oldValue.compareTo(newValue) == 0) {
					return;
				}
			} catch (final NumberFormatException exception) {
				return;
			}
		}
		super.saveCellEditorValue(cellEditor, cell);
		
		baseAmountSection.updateDto(oldBaseAmountDTO, baseAmountDTO);
	}

	
	/**
	 * @return the {@link PriceListEditorController} instance
	 */
	public PriceListEditorController getController() {
		return controller;
	}
	
}