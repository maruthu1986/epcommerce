/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.helpers.extenders.EpModelCreator;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;

import org.apache.log4j.Logger;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Model creator for the BaseAmountDTO.
 */
public final class BaseAmountDTOCreator {

	private static final Logger LOG = Logger.getLogger(BaseAmountDTOCreator.class);

	private BaseAmountDTOCreator() {
		// Empty constructor.
	}
	
	/**
	 * Creates a new instance of BaseAmountDTO.
	 *
	 * @return the new instance of BaseAmountDTO
	 */
	public static BaseAmountDTO createModel() {
		EpModelCreator<BaseAmountDTO> creatorExtension = getExtendedCreator();
		
		if (creatorExtension == null) {
			LOG.debug("Creating OOTB BaseAmountDTO");
			return new BaseAmountDTO();
		}

		LOG.debug("Creating extension BaseAmountDTO");
		return creatorExtension.createModel();

	}

	/**
	 * Creates new instance of BaseAmountDTO from another.
	 *
	 * @param other the other instance of BaseAmountDTO
	 * @return the new instance of BaseAmountDTO
	 */
	public static BaseAmountDTO createModel(final BaseAmountDTO other) {
		EpModelCreator<BaseAmountDTO> creatorExtension = getExtendedCreator();
		
		if (creatorExtension == null) {
			LOG.debug("Creating OOTB BaseAmountDTO for other");
			return new BaseAmountDTO(other);
		}
		LOG.debug("Creating extension BaseAmountDTO for other");
		return creatorExtension.createModel(other);

	}
	
	private static EpModelCreator<BaseAmountDTO> getExtendedCreator() {
		return PluginHelper.getModelCreator(BaseAmountDTO.class);
	}

}
