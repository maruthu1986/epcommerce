/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * This class implements the section of a  editor that displays product
 * attribute information.
 */
public abstract class AbstractPolicyAwareAttributeEditorSection extends AbstractPolicyAwareEditorPageSectionPart {

	private static final int HEIGHT_HINT = 10;

	private final ControlModificationListener controlModificationListener;

	private final AbstractCmClientEditorPage page;

	private IEpLayoutComposite mainComposite;

	private PolicyAwareAttributesViewPart attributesViewPart;

	/**
	 * Constructor to create a new Section in an editor's FormPage.
	 * 
	 * @param formPage
	 *            the form page
	 * @param editor
	 *            the CmClientFormEditor that contains the form
	 */
	public AbstractPolicyAwareAttributeEditorSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.page = (AbstractCmClientEditorPage) formPage;
		this.controlModificationListener = editor;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// not used
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		PolicyActionContainer partContainer = addPolicyActionContainer("part"); //$NON-NLS-1$
		
		// setup layout for the whole attribute section that include table and button.
		mainComposite = CompositeFactory.createGridLayoutComposite(client, 2, false);
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = HEIGHT_HINT;
		mainComposite.getSwtComposite().setLayoutData(layoutData);
		
		attributesViewPart = new PolicyAwareAttributesViewPart(
				getModel(), getManagedForm().getForm().getToolBarManager(), 
				createButtonPolicyActionContainer(),
				createTablePolicyActionContainer());
		attributesViewPart.createControls(mainComposite);
		partContainer.addDelegate(attributesViewPart);
		
		addCompositesToRefresh(mainComposite.getSwtComposite());
	}

	
	@Override
	public void dispose() {
		this.attributesViewPart.dispose();
		super.dispose();
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

	@Override
	protected void populateControls() {
		attributesViewPart.setInput(getInput(getModel(), getLocale()));
		attributesViewPart.setControlModificationListener(controlModificationListener);
	}

	/**
	 * Creates the policy action container for attributes controls.
	 * @return the container
	 */
	protected PolicyActionContainer createButtonPolicyActionContainer() {
		return addPolicyActionContainer("attributesControls"); //$NON-NLS-1$
	}

	/**
	 * Creates the policy action container for in-line table edit controls.
	 * @return the container
	 */
	protected PolicyActionContainer createTablePolicyActionContainer() {
		return addPolicyActionContainer("attributesTable"); //$NON-NLS-1$
	}
}
