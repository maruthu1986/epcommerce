/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.category;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.policy.AbstractPolicyAwareAttributeEditorSection;
import com.elasticpath.cmclient.core.comparator.AttributeValueComparatorByNameIgnoreCase;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Category;

/**
 * This class implements the section of the Category editor that displays category
 * attribute information.
 */
public class CategoryEditorAttributeSection extends	AbstractPolicyAwareAttributeEditorSection {

	/**
	 * Constructor to create a new Section in an editor's FormPage.
	 *
	 * @param formPage the form page object
	 * @param editor the CmClientFormEditor that contains the form
	 */
	public CategoryEditorAttributeSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
	}

	@Override
	public AttributeValue[] getInput(final Object object, final Locale locale) {

		final Category inputCategory = (Category) object;
		final List<AttributeValue> list = inputCategory.getAttributeValueGroup()
				.getFullAttributeValues(
						inputCategory.getCategoryType().getAttributeGroup(),
						locale);
		
		AttributeValue[] values = list.toArray(new AttributeValue[list.size()]);
		Arrays.sort(values, new AttributeValueComparatorByNameIgnoreCase());
		return values;
	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorAttributeSection_Title;
	}
	
	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, false);
	}

	@Override
	protected PolicyActionContainer createButtonPolicyActionContainer() {
		Category category = (Category) getModel();
		if (category.isLinked()) {
			return addPolicyActionContainer("categorySummaryReadOnlyControls"); //$NON-NLS-1$
		}

		return super.createButtonPolicyActionContainer();
	}

	@Override
	protected PolicyActionContainer createTablePolicyActionContainer() {
		Category category = (Category) getModel();
		if (category.isLinked()) {
			return addPolicyActionContainer("categorySummaryReadOnlyControls"); //$NON-NLS-1$
		}

		return super.createTablePolicyActionContainer();
	}
}
