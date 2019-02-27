/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * This class implements the section of the Product editor that displays product
 * attribute information.
 * TODO: fix locale
 */
public abstract class AbstractAttributeEditorSection extends
		AbstractCmClientEditorPageSectionPart {
	
	private final ControlModificationListener controlModificationListener;

	private final AbstractCmClientEditorPage page;

	private IEpLayoutComposite mainComposite;

	private AttributesViewPart attributesViewPart;

	/**
	 * Constructor to create a new Section in an editor's FormPage.
	 * 
	 * @param formPage
	 *            the form page
	 * @param editor
	 *            the CmClientFormEditor that contains the form
	 */
	public AbstractAttributeEditorSection(final FormPage formPage,
			final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.page = (AbstractCmClientEditorPage) formPage;
		this.controlModificationListener = editor;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// not used
	}

	@Override
	protected void createControls(final Composite client,
			final FormToolkit toolkit) {

		// setup layout for the whole attribute section that include table and
		// button.
		this.mainComposite = CompositeFactory.createGridLayoutComposite(client,
				2, false);

		attributesViewPart = new AttributesViewPart(getModel(), getRolePermission(),
				getManagedForm().getForm().getToolBarManager());
		attributesViewPart.createControls(mainComposite);
		
		getSection().setLayoutData(getLayoutData());
	}

	/**
	 * To the data input for the attribute table.
	 * 
	 * @param object the object from which the attribute table data to be retrieved from.
	 * @param locale the local locale used to display attributes.
	 * @return the element array for the attribute table.
	 */
	public abstract AttributeValue[] getInput(final Object object, final Locale locale);

	/**
	 * @return the selected locale.
	 */
	protected Locale getLocale() {
		return page.getSelectedLocale();
	}

	/**
	 * Gets the shell.
	 * @return the shell
	 */
	public Shell getShell() {
		return mainComposite.getSwtComposite().getShell();
	}

	/**
	 * the getter of rolePermission.
	 * @return the rolePermission object.
	 */
	public abstract EpState getRolePermission();

	@Override
	protected void populateControls() {
		attributesViewPart.setInput(getInput(getModel(), getLocale()));
		attributesViewPart.setControlModificationListener(controlModificationListener);
	}
	
	/**
	 * Refreshes the layout of the page.
	 */
	protected void refreshLayout() {
		mainComposite.getSwtComposite().layout(true);
	}
	

	

}
