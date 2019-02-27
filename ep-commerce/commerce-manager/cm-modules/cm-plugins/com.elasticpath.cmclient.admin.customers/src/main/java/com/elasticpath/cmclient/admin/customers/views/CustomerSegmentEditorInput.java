/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.customers.views;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.core.editors.EntityEditorInput;

/**
 * Encapsulates view associated with editor in addition to other description fields. It is required to refresh view when store has been created using
 * editor because action execution is already finished at that moment.
 */
public class CustomerSegmentEditorInput extends EntityEditorInput<Long> {

	private String name;

	/**
	 * Initializes fields describing editor.
	 *
	 * @param name the name of model viewed in editor
	 * @param objectUid UID of model viewed in editor
	 * @param targetClass class of model viewed in editor
	 */
	public CustomerSegmentEditorInput(final String name, final long objectUid, final Class< ? > targetClass) {
		super(name, objectUid, targetClass);
		this.name = name;
	}

	/**
	 * Gets tool tip text merged from code and name of model.
	 *
	 * @return tool tip text
	 */
	@Override
	public String getToolTipText() {
		return
			NLS.bind(AdminCustomersMessages.get().CustomerSegmentEditor_ToolTip,
			name);
	}

	/**
	 * Gets the name of model to show in editor's title.
	 *
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of model to show in editor's title.
	 *
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

}
