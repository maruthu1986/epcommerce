/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.editors;


import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.editors.EntityEditorInput;

/**
 * Encapsulates view associated with editor in addition to other description fields. It is required to refresh view when store has been created using
 * editor because action execution is already finished at that moment.
 */
public class StoreEditorInput extends EntityEditorInput<Long> {

	private String name;

	private String code;

	/**
	 * Initializes fields describing editor.
	 *
	 * @param name the name of model viewed in editor
	 * @param objectUid UID of model viewed in editor
	 * @param code GUID of model viewed in editor
	 * @param targetClass class of model viewed in editor
	 */
	public StoreEditorInput(final String name, final long objectUid, final String code, final Class< ? > targetClass) {
		super(name, objectUid, targetClass);
		this.name = name;
		this.code = code;
	}

	@Override
	public String getToolTipText() {
		return
			NLS.bind(AdminStoresMessages.get().StoreEditorTooltip,
			new Object[] { code, name });
	}

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

	/**
	 * Gets Code (GUID) of model displayed by described editor.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Gets Code (GUID) of model displayed by described editor.
	 * 
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}
}
