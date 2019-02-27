/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.adapters.products.data;

import static com.elasticpath.importexport.common.comparators.ExportComparators.ATTRIBUTE_VALUES_DTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.AttributeValuesDTO;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>AttributeValueGroup</code>
 * and <code>AttributeGroupDTO</code> objects.
 */
public class AttributeGroupAdapter extends AbstractDomainAdapterImpl<AttributeValueGroup, AttributeGroupDTO> {

	private AttributeValuesAdapter attributeValuesAdapter;
	
	@Override
	public void populateDTO(final AttributeValueGroup attributeValueGroup, final AttributeGroupDTO attributeGroupDto) {
		List<AttributeValuesDTO> attributeValues = new ArrayList<>();
		Map<String, List<AttributeValue>> attributeModelMap = new HashMap<>();

		for (Entry<String, AttributeValue> entry : attributeValueGroup.getAttributeValueMap().entrySet()) {
			Attribute attribute = entry.getValue().getAttribute();
			String key = attribute.getKey();
			if (!attributeModelMap.containsKey(key)) {
				attributeModelMap.put(key, new ArrayList<>());
			}
			attributeModelMap.get(key).add(entry.getValue());
		}

		for (Collection<AttributeValue> valueCollection : attributeModelMap.values()) {
			AttributeValuesDTO attributeValuesDTO = new AttributeValuesDTO();
			attributeValuesAdapter.populateDTO(valueCollection, attributeValuesDTO);			
			if (!attributeValuesDTO.getValues().isEmpty()) { // values are always instanced by attributeValuesAdapter
				attributeValues.add(attributeValuesDTO);
			}
		}
		Collections.sort(attributeValues, ATTRIBUTE_VALUES_DTO);
		attributeGroupDto.setAttributeValues(attributeValues);
	}

	@Override
	public void populateDomain(final AttributeGroupDTO attributeGroupDto, final AttributeValueGroup attributeValueGroup) {
		if (attributeGroupDto == null) {
			return;
		}

		List<AttributeValuesDTO> attributeValuess = attributeGroupDto.getAttributeValues();
		attributeValuesAdapter.setAttributeValueGroup(attributeValueGroup);
		Map<String, AttributeValue> attributeValueMap = attributeValueGroup.getAttributeValueMap();
		for (AttributeValuesDTO attributesDTO : attributeValuess) {
			Collection<AttributeValue> attributeValueCollection = new ArrayList<>();
			attributeValuesAdapter.populateDomain(attributesDTO, attributeValueCollection);

			for (AttributeValue attributeValue : attributeValueCollection) {
				attributeValueMap.put(attributeValue.getLocalizedAttributeKey(), attributeValue);
			}

		}
	}

	/**
	 * Gets the AttributeValuesAdapter.
	 * 
	 * @return the AttributesValuesAdapter
	 * @see AttributeValuesAdapter
	 */
	public AttributeValuesAdapter getAttributeValuesAdapter() {
		return attributeValuesAdapter;
	}

	/**
	 * Sets the AttributeValuesAdapter.
	 * 
	 * @param attributeValuesAdapter the AttributeValuesAdapter to set
	 * @see AttributeValuesAdapter
	 */
	public void setAttributeValuesAdapter(final AttributeValuesAdapter attributeValuesAdapter) {
		this.attributeValuesAdapter = attributeValuesAdapter;
	}
}
