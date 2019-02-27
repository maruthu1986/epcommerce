/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price;

/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ViewerCell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.price.model.PriceAdjustmentModel;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.ValidationErrorLocation;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.conversion.EpStringToBigDecimalConverter;
import com.elasticpath.cmclient.core.ui.framework.AbstractInlineEditingSupport;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;

/**
 * Editing support for <code>BaseAmountSection</code> table.
 */
public class PriceAdjustmentEditingSupport extends AbstractInlineEditingSupport {

	/**
	 * List value field.
	 */
	public static final String LIST_VALUE_FIELD = "listValue"; //$NON-NLS-1$

	/**
	 * Sale value field.
	 */
	public static final String SALE_VALUE_FIELD = "saleValue"; //$NON-NLS-1$

	private final String columnName;

	private final EpStringToBigDecimalConverter assignedBundleConverter = new AssignedBundlePriceAdjustmentConverter();

	private final EpStringToBigDecimalConverter calculatedBundleConverter = new CalculatedBundlePriceAdjustmentConverter();

	private final IValidator valueValidator;

	private final PriceAdjustmentTree paTree;


	private PriceAdjustmentModel element;
	private BigDecimal oldAdjustment;
	private final ICellEditorListener cellEditorListener;

	/**
	 * Constructor.
	 *
	 * @param viewer             the column viewer
	 * @param columnName         - name of the column
	 * @param dataBindingContext - data binding context
	 * @param tree               - price adjustment tree
	 */
	public PriceAdjustmentEditingSupport(final ColumnViewer viewer,
										 final String columnName,
										 final DataBindingContext dataBindingContext,
										 final PriceAdjustmentTree tree) {
		super(viewer, dataBindingContext);
		this.columnName = columnName;
		this.paTree = tree;
		valueValidator = new CompoundValidator(EpValidatorFactory.BIG_DECIMAL, new PriceAdjustmentValidator());
		cellEditorListener = new ICellEditorListener() {

			@Override
			public void editorValueChanged(final boolean arg0, final boolean arg1) {
				//nothing
			}

			@Override
			public void cancelEditor() {
				revertEditor();
			}

			@Override
			public void applyEditorValue() {
				//nothing
			}
		};
	}

	@Override
	protected void initializeCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		element = (PriceAdjustmentModel) cell.getElement();
		oldAdjustment = element.getPriceAdjustment();
		final EpBindingConfiguration bindingConfig = new EpBindingConfiguration(
				getBindingContext(),
				cellEditor.getControl(),
				element,
				columnName);
		IConverter converter;
		if (element.getParent().isProductACalculatedBundle()) {
			converter = calculatedBundleConverter;
		} else {
			converter = assignedBundleConverter;
		}
		bindingConfig.configureUiToModelBinding(converter, valueValidator, true);
		bindingConfig.setErrorLocation(ValidationErrorLocation.LEFT);
		setBinding(bindingProvider.bind(bindingConfig));
		getViewer().getColumnViewerEditor().addEditorActivationListener(getActivationListener());
		cellEditor.setValue(cell.getText());
		cellEditor.addListener(cellEditorListener);
	}

	/**
	 * Revert the element object to the value it has when the cell editor was opened.
	 */
	protected void revertEditor() {
		element.setPriceAdjustment(oldAdjustment);
	}

	@Override
	protected boolean canEdit(final Object element) {
		PriceAdjustmentModel priceAdjustmentModel = (PriceAdjustmentModel) element;
		return paTree.canEditPriceAdjustment(priceAdjustmentModel);
	}

	@Override
	protected void saveCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
		String newStringValue = cellEditor.getValue().toString();
		if (valueValidator.validate(newStringValue) != Status.OK_STATUS) {
			revertEditor();
			return;
		}

		if (StringUtils.isEmpty(newStringValue) && oldAdjustment == null) {
			return;
		}

		super.saveCellEditorValue(cellEditor, cell);
		paTree.notifyModification();
	}

	/**
	 * An implementation of {@link IConverter} that converts a String to a BigDecimal, rounds up to 2 decimal places.
	 */
	private abstract static class AbstractPriceAdjustmentConverter extends EpStringToBigDecimalConverter {
		@Override
		public Object convert(final Object fromObject) {
			BigDecimal value = (BigDecimal) super.convert(fromObject);
			value = adjustValue(value);
			if (value == null) {
				return null;
			}
			return value.setScale(2, RoundingMode.HALF_UP);
		}

		protected abstract BigDecimal adjustValue(final BigDecimal input);
	}

	/**
	 * Converts a String to a BigDecimal, and changes the sign to a negative number if necessary, so that only negative price adjustments are put
	 * on calculated bundles.
	 */
	private static class CalculatedBundlePriceAdjustmentConverter extends AbstractPriceAdjustmentConverter {
		@Override
		protected BigDecimal adjustValue(final BigDecimal input) {
			if (input == null) {
				return null;
			}
			return input.abs().negate();
		}
	}

	/**
	 * Converts a String to a BigDecimal, and changes the sign to a positive number if necessary, so that only positive price adjustments are put
	 * on assigned bundles.
	 */
	private static class AssignedBundlePriceAdjustmentConverter extends AbstractPriceAdjustmentConverter {
		@Override
		protected BigDecimal adjustValue(final BigDecimal input) {
			if (input == null) {
				return null;
			}
			return input.abs();
		}
	}


	/**
	 * The validation class for price adjustments. It will make sure the sum of the adjustment and the price
	 * remain non-negative.
	 */
	private class PriceAdjustmentValidator implements IValidator {
		@Override
		public IStatus validate(final Object value) {
			String text;
			if (value instanceof String) {
				text = (String) value;
			} else {
				throw new IllegalArgumentException("PriceAdjustmentValidator expects a String");  //$NON-NLS-1$
			}

			if (StringUtils.isNotEmpty(text) && element.getParent().isProductACalculatedBundle()) {
				BigDecimal adjustment = (BigDecimal) calculatedBundleConverter.convert(text);
				if (adjustment.add(element.getPrice()).compareTo(BigDecimal.ZERO) < 0) {
					return new Status(
							IStatus.ERROR,
							CorePlugin.PLUGIN_ID,
							IStatus.ERROR,
							CatalogMessages.get().ProductBundlePriceAdjustment_PriceAdjustmentTooLarge,
							null);
				}

			}
			return Status.OK_STATUS;
		}
	}


}




