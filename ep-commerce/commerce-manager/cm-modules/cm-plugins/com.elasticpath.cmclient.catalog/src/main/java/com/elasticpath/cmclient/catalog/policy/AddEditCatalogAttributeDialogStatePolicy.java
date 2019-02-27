/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;

/**
 * Policy controlling state in the AddEditCatalogAttributeDialog.
 */
public class AddEditCatalogAttributeDialogStatePolicy extends AbstractCatalogDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	private Boolean editMode;

	@Override
	public void init(final Object dependentObject) {
		if ((dependentObject != null) && (dependentObject instanceof Boolean)) {
			editMode = (Boolean) dependentObject;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("CatalogAttributeNameFieldContainer", new DefaultEditableDeterminer()); //$NON-NLS-1$
			determinerMap.put("addEditAttributeNonEditableDialog", new DialogAddEditStateDeterminer()); //$NON-NLS-1$
			determinerMap.put("CatalogAttributeMultiValuesCheckboxContainer", new MultiValueCheckboxStateDeterminer()); //$NON-NLS-1$	
			determinerMap.put("CatalogAttributeMultiLangCheckboxContainer", new MultiLanguageCheckboxStateDeterminer()); //$NON-NLS-1$	
		}
		return determinerMap;
	}

	private Attribute getAttributeFromPolicyDependentObject(final Object policyDependentObject) {

		if (policyDependentObject == null) {
			return null;
		}

		Attribute attribute = null;

		if (policyDependentObject instanceof Attribute) {
			attribute = (Attribute) policyDependentObject;
		}

		return attribute;
	}

	/**
	 * Determines the state of all fields in the Add/Edit dialog.
	 * If editMode is false, it is assumed that a new object is being added and the dialog is in its Add state.
	 * Thus, all entry fields should be available.
	 * Otherwise, the dialog is assumed to be in the Edit state, and only the fields available in the edit state should be available.
	 */
	public class DialogAddEditStateDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (editMode) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines the state of the Multi Value Checkbox in the Add/Edit dialog.
	 * If the Attribute type id is AttributeType.SHORT_TEXT_TYPE_ID, the box should be enabled.
	 * Otherwise, it is disabled.
	 */
	public class MultiValueCheckboxStateDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (editMode) {
				return EpState.READ_ONLY;
			}

			Object policyDependentObject = targetContainer.getPolicyDependent();

			if (attributeTypeIsShortText(policyDependentObject)) {
				return EpState.EDITABLE;
			}

			return EpState.READ_ONLY;
		}

		private boolean attributeTypeIsShortText(final Object policyDependentObject) {
			Attribute attribute = getAttributeFromPolicyDependentObject(policyDependentObject);

			if (attribute == null) {
				return false;
			}

			AttributeType attributeType = attribute.getAttributeType();

			return attributeType == AttributeType.SHORT_TEXT;

		}
	}

	/**
	 * Determines the state of the Multi Lang Checkbox in the Add/Edit dialog.
	 * If the Attribute type id supports multiple languages, the box should be enabled.
	 * Otherwise, it is disabled.
	 */
	public class MultiLanguageCheckboxStateDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (editMode) {
				return EpState.READ_ONLY;
			}

			Object policyDependentObject = targetContainer.getPolicyDependent();

			if (attributeTypeSupportsMultipleLanguages(policyDependentObject)) {
				return EpState.EDITABLE;
			}

			return EpState.READ_ONLY;
		}

		private boolean attributeTypeSupportsMultipleLanguages(final Object policyDependentObject) {
			Attribute attribute = getAttributeFromPolicyDependentObject(policyDependentObject);

			if (attribute == null) {
				return false;
			}

			AttributeType attributeType = attribute.getAttributeType();

			return attributeType == AttributeType.SHORT_TEXT
					|| attributeType == AttributeType.LONG_TEXT
					|| attributeType == AttributeType.FILE
					|| attributeType == AttributeType.IMAGE;

		}

	}


}