/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;

/**
 * Part of Price List Editor. Section with the table for editing Base Amount. 
 */
public class BaseAmountSectionPart extends AbstractPolicyAwareEditorPageSectionPart {

	private final BaseAmountSection baseAmountSection;
	private static final int COLUMNS = 2;
	private final BaseAmountSearchSection baseAmountSearchSection;
	private final BaseAmountFilterSection baseAmountFilterSection;
	
	/**
	 * Constructor.
	 *
	 * @param formPage - page where this section will be located on
	 * @param editor - editor part where this section will be located on
	 * @param control - section with the table.
	 * @param baseAmountSearchSection - search section
	 * @param baseAmountFilterSection - filter section
	 */
	public BaseAmountSectionPart(final FormPage formPage, 
			final AbstractCmClientFormEditor editor, 
			final BaseAmountSection control,
			final BaseAmountSearchSection baseAmountSearchSection,
			final BaseAmountFilterSection baseAmountFilterSection) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.baseAmountSection = control;
		this.baseAmountSearchSection = baseAmountSearchSection; 
		this.baseAmountFilterSection = baseAmountFilterSection;	
	}
	
	
	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		baseAmountSearchSection.bindControls(bindingContext);
		baseAmountFilterSection.bindControls(bindingContext);
		baseAmountSection.bindControls(bindingContext);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		IEpLayoutComposite composite = CompositeFactory.createGridLayoutComposite(client, COLUMNS, false);

		((GridLayout) composite.getSwtComposite().getLayout()).marginHeight = 0;
		((GridLayout) composite.getSwtComposite().getLayout()).marginWidth = 0;
		((GridLayout) composite.getSwtComposite().getLayout()).verticalSpacing = 0;
		
		IEpLayoutData data = composite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false, 2, 1);
		
		baseAmountSearchSection.createControls(composite, data);		
		baseAmountFilterSection.createControls(composite, data);		

		IPolicyTargetLayoutComposite mainPartComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(composite);
		baseAmountSection.createControls(mainPartComposite.getLayoutComposite(), null);		
		baseAmountSearchSection.setBaseAmountTableViewer(baseAmountSection.getBaseAmountTableViewer());
		baseAmountSearchSection.setDependentSection(baseAmountFilterSection);
		baseAmountFilterSection.setBaseAmountTableViewer(baseAmountSection.getBaseAmountTableViewer());
		
		baseAmountSection.refreshTableViewer();
		
		baseAmountSearchSection.removeAllBaseAmountSearchEventListener();
		baseAmountSearchSection.addBaseAmountSearchEventListener(baseAmountSection);

		
		
	}
	
	@Override
	protected void populateControls() {
		baseAmountSearchSection.populateControls();
		baseAmountFilterSection.populateControls();
		baseAmountSection.populateControls();
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, false);
	}

	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}
	
}
