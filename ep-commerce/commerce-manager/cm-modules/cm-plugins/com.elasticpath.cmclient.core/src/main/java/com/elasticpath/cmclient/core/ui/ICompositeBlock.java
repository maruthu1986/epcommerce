/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.ui;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;

/**
 * A composite block is a component that displays information about an object.
 */
public interface ICompositeBlock {

	/**
	 * Initialise the block by adding all the controls and setting their values.
	 * 
	 * @param composite the composite to use
	 * @param dependentObject the dependent object
	 */
	void init(IEpLayoutComposite composite, Object dependentObject);
}
