/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.valueeditor;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;

/**
 * 
 * Condition value factory used for create SWT control objects based on the
 * {@link com.elasticpath.tags.domain.TagDefinition}.
 * 
 */
public interface ConditionRowValueFactory {

	/** 
	 * Event for unbind listener.
	 */
	int EVENT_FOR_UNBIND = 0x1001;
	
	/**
	 * Create SWT control for edit value. Type of SWT control depends on
	 * {@link com.elasticpath.tags.domain.TagDefinition} from
	 * 
	 * @param parent
	 *            Parent composite
	 * @param swtStyle
	 *            SWT style
	 * @param modelAdapter
	 *            instance of {@link ConditionModelAdapter}
	 * @param dataBindingContext
	 *            the Data Binding Context
	 * @param disposeListener
	 *            optional DisposeListener
	 * @return SWT control
	 */
	Control createControl(Composite parent,
			int swtStyle, ConditionModelAdapter modelAdapter,
			DataBindingContext dataBindingContext,
			DisposeListener disposeListener);

}