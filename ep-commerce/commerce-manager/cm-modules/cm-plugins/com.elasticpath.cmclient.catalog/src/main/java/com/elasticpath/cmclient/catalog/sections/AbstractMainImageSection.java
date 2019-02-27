/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.sections;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;

/**
 * Product editor multi-sku page section.
 */
public abstract class AbstractMainImageSection extends AbstractCmClientEditorPageSectionPart {

	private static final int NUM_COLUMNS = 3;
	
	private IEpLayoutComposite mainEpComposite;
	
	private IEpLayoutComposite compositeForRow1;

	/**
	 * Constructs this section.
	 * 
	 * @param formPage the parent form page
	 * @param editor the editor
	 */
	public AbstractMainImageSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.COMPACT);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {

		mainEpComposite = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		mainEpComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		//For Row 1
		compositeForRow1 = mainEpComposite.addTableWrapLayoutComposite(NUM_COLUMNS, false, null);
		final TableWrapData twdForRow1 = new TableWrapData(TableWrapData.FILL_GRAB);
		twdForRow1.colspan = 2;
		compositeForRow1.setLayoutData(twdForRow1);
		
		compositeForRow1.addLabelBold(CatalogMessages.get().ProductImage_File, null);
		
	}
	
	/**
	 * Get the image path from model.
	 * @return the image path
	 */
	public abstract String getImagePath();
	
	/**
	 * Set the image path to model.
	 * @param imagePath the image path
	 */
	public abstract void setImagePath(final String imagePath);
	
	/**
	 * Check whether is authorized to edit this page.
	 * @return true if authorized, otherwise false
	 */
	public abstract boolean isAuthorized();
}
