/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.core.editors.EntityEditorInput;

/**
 * Data policy editor input.
 */
public class DataPolicyEditorInput extends EntityEditorInput<Long> {

	private String name;

	/**
	 * Constructor.
	 *
	 * @param name        the name of model viewed in editor
	 * @param objectUid   UID of model viewed in editor
	 * @param targetClass class of model viewed in editor
	 */
	public DataPolicyEditorInput(final String name, final long objectUid, final Class<?> targetClass) {
		super(name, objectUid, targetClass);
		this.name = name;
	}

	@Override
	public String getToolTipText() {
		return NLS.bind(AdminDataPoliciesMessages.get().DataPolicyEditor_Tooltip, name);
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set editor input name.
	 *
	 * @param name editor input name.
	 */
	public void setName(final String name) {
		this.name = name;
	}
}
