/*
 * Copyright (c) Elastic Path Software Inc., 2014.
 */
package com.elasticpath.importexport.common.adapters.associations;

import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationTypeDTO;

/**
 * Custom xml adapter for {@link ProductAssociationTypeDTO}.
 */
public class ProductAssociationTypeAdapter extends XmlAdapter<String, ProductAssociationTypeDTO> {
	private static final Logger LOG = Logger.getLogger(ProductAssociationTypeAdapter.class);
	@Override
	public ProductAssociationTypeDTO unmarshal(final String value) throws Exception {

		try {
			return ProductAssociationTypeDTO.valueOf(value);
		} catch (IllegalArgumentException e) {

			List<String> acceptedValues = ProductAssociationTypeDTO.values()
				.stream()
				.map(ProductAssociationTypeDTO::getImportExportName)
				.collect(Collectors.toList());

			LOG.error("Product Association type does not exist: " + value + ". Accepted values are " + acceptedValues, e);
		}
		return null;
	}

	@Override
	public String marshal(final ProductAssociationTypeDTO value) throws Exception {
		return value.getImportExportName();
	}
}