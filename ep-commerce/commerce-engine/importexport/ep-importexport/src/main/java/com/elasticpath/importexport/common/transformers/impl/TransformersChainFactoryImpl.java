/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.transformers.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.transformers.Transformer;
import com.elasticpath.importexport.common.transformers.TransformerConfiguration;
import com.elasticpath.importexport.common.transformers.TransformersChainFactory;

/**
 * This class is implementation of <code>TransformersChainFactory</code> interface.
 * <p>
 * It creates chain of transformers based on <code>List</code> of <code>TransformerConfiguration</code> objects
 */
public class TransformersChainFactoryImpl implements TransformersChainFactory {

	@Override
	public List<Transformer> createTransformersChain(final List<TransformerConfiguration> transformerConfigurationList)
			throws ConfigurationException {

		List<Transformer> chainedTransformers = new ArrayList<>();

		for (TransformerConfiguration configuration : transformerConfigurationList) {
			try {
				Class<?> clazz = Class.forName(configuration.getClassName());
				chainedTransformers.add((Transformer) clazz.newInstance());
				
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new ConfigurationException(e);
			}
		}
		return chainedTransformers;
	}
}
