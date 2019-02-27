/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product; 

import java.util.Locale;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.common.ProductAttributeValueSorter;
import com.elasticpath.cmclient.catalog.policy.AbstractPolicyAwareAttributeEditorSection;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Product;

/**
 * This class implements the section of the Product editor that displays product attribute information.
 */
public class ProductEditorAttributeEditSection extends AbstractPolicyAwareAttributeEditorSection {

	/**
	 * Constructor.
	 * 
	 * @param formPage the formPage object of the page.
	 * @param editor the editor object of the page.
	 */
	public ProductEditorAttributeEditSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
		// do nothing.
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		super.createControls(client, toolkit);

	}

	@Override
	public AttributeValue[] getInput(final Object object, final Locale locale) {

		final Product product = ((ProductModel) object).getProduct();

		return new ProductAttributeValueSorter(product, locale).getOrderedAttributeValues();
	}

	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, false);
	}

}
