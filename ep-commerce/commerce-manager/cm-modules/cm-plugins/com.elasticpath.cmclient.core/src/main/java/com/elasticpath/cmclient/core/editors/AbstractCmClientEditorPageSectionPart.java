/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleParameter;

/**
 * Abstract class specifically for an EditorPage's Form Section.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractCmClientEditorPageSectionPart extends AbstractCmClientFormSectionPart {

	private final AbstractCmClientFormEditor editor;
	
	/**
	 * Constructor to create a new Section in an editor's FormPage.
	 *
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 * @param style the style bits applicaple to a <code>Section</code>
	 */
	public AbstractCmClientEditorPageSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final int style) {
		super(formPage.getManagedForm().getForm().getBody(), formPage.getManagedForm().getToolkit(), editor.getDataBindingContext(), style);
		this.editor = editor;
	}
	
	/**
	 * Get the model object.
	 * @return the model
	 */
	public Object getModel() {
		return getEditor().getModel();
	}
	
	/**
	 * Get the dependent object.
	 * @return the dependent object
	 */
	public Object getDependentObject() {
		return getEditor().getDependentObject();
	}

	/**
	 * Get the editor.
	 * @return the form editor
	 */
	public AbstractCmClientFormEditor getEditor() {
		return editor;
	}

	/**
	 * 
	 * @param newContext data binding context
	 */
	public void refreshDataBindings(final DataBindingContext newContext) {
		bindControls(newContext);
	}
	
	/**
	 * Returns the RuleParamter with the given paramKey inside the given ruleElement.
	 * 
	 * @param ruleElement the RuleElement object to scan
	 * @param paramKey the String key of the RuleParameter object to retrieve
	 * @return the RuleParameter with the given paramKey
	 */
	protected RuleParameter getRuleParameterByKey(final RuleElement ruleElement, final String paramKey) {
		for (RuleParameter currRuleParameter : ruleElement.getParameters()) {
			if (paramKey.equals(currRuleParameter.getKey())) {
				return currRuleParameter;
			}
		}
		return null;
	}

	
}
