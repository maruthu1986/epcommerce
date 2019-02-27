/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.adapters.products.data;

import java.math.BigDecimal;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.ShippableItemDTO;
import com.elasticpath.importexport.common.dto.products.UnitDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>ProductSku</code>
 * and <code>ShippableItemDTO</code> objects.
 */
public class ShippableItemAdapter extends AbstractDomainAdapterImpl<ProductSku, ShippableItemDTO> {

	private SettingValueProvider<String> lengthUnitsProvider;
	private SettingValueProvider<String> weightUnitsProvider;

	@Override
	public void populateDomain(final ShippableItemDTO source, final ProductSku target) {
		target.setShippable(source.isEnabled());
		if (source.isEnabled()) {
			checkNegative(source.getWeight().getValue(), "Weight");
			checkNegative(source.getWidth().getValue(),  "Width");
			checkNegative(source.getLength().getValue(), "Length");
			checkNegative(source.getHeight().getValue(), "Height");
			
			target.setWeight(source.getWeight().getValue());
			target.setWidth(source.getWidth().getValue());
			target.setLength(source.getLength().getValue());
			target.setHeight(source.getHeight().getValue());
		}
	}

	/**
	 * Checks value with name to negativeness. 
	 * 
	 * @param value the decimal value
	 * @param name the name of the value
	 */
	void checkNegative(final BigDecimal value, final String name) {
		if (value.compareTo(BigDecimal.ZERO) < 0) {			
			throw new PopulationRuntimeException("IE-10317", name);
		}
	}

	@Override
	public void populateDTO(final ProductSku source, final ShippableItemDTO target) {
		target.setEnabled(source.isShippable());
		if (source.isShippable()) {
			final String unitsWeight = getWeightUnitsProvider().get();
			final String unitsLength = getLengthUnitsProvider().get();
			target.setWeight(new UnitDTO(unitsWeight, source.getWeight()));
			target.setWidth(new UnitDTO(unitsLength, source.getWidth()));
			target.setLength(new UnitDTO(unitsLength, source.getLength()));
			target.setHeight(new UnitDTO(unitsLength, source.getHeight()));
		}
	}

	public void setLengthUnitsProvider(final SettingValueProvider<String> lengthUnitsProvider) {
		this.lengthUnitsProvider = lengthUnitsProvider;
	}

	protected SettingValueProvider<String> getLengthUnitsProvider() {
		return lengthUnitsProvider;
	}

	public void setWeightUnitsProvider(final SettingValueProvider<String> weightUnitsProvider) {
		this.weightUnitsProvider = weightUnitsProvider;
	}

	protected SettingValueProvider<String> getWeightUnitsProvider() {
		return weightUnitsProvider;
	}

}
