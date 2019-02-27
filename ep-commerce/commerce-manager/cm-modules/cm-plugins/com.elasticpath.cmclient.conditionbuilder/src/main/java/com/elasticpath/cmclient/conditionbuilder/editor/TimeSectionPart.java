/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.editor;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.component.ActionEventListener;
import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.core.binding.EpFormPartSupport;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * A time part for condition editor. 
 *
 */
public class TimeSectionPart extends AbstractConditionSectionPart {

	private boolean initConditionBuilder;

	private final TimeConditionPanel<SellingContext> timeConditionPanel;
	
	private EpFormPartSupport formPartSupport;
	
	private final DataBindingContextListener dataBindingContextListener =
		new DataBindingContextListener() {
			public void changed(final Status status, final DataBindingContext dataBindingContext) {
				if (Status.ADD == status) {
					formPartSupport = EpFormPartSupport.create(TimeSectionPart.this, dataBindingContext);
				} else if (Status.REMOVE == status) {
					if (formPartSupport == null) { 
						return; 
					}
					formPartSupport.dispose();
				}
			}
		};
	
	private final ActionEventListener<Object> markEditorStateListener =
		new ActionEventListener<Object>() {
			public void onEvent(final Object object) {
				if (initConditionBuilder) { 
					return; 
				}
				TimeSectionPart.this.markDirty();
				getEditor().controlModified();
			}
		};
	/**
	 * Default constructor.
	 * @param formPage a form page
	 * @param editor an editor
	 * @param style a style
	 * @param dictionaryGuid a dictionary guid
	 * @param sellingContextModelWrapper a selling context model adapter
	 */
	public TimeSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final int style,
			final String dictionaryGuid, final ModelWrapper<SellingContext> sellingContextModelWrapper) {
		super(formPage, editor, style, dictionaryGuid);
		
		timeConditionPanel = new TimeConditionPanel<>(
            sellingContextModelWrapper, null, dictionaryGuid, dataBindingContextListener, markEditorStateListener,
            ConditionBuilderMessages.get().Wizard_TimePage_RadioButtonAll,
            ConditionBuilderMessages.get().Wizard_TimePage_RadioButtonSavedConditions,
            ConditionBuilderMessages.get().Wizard_TimePage_RadioButtonConditions);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		timeConditionPanel.bindControls(bindingContext);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		getSection().getParent().setRedraw(false);

		IEpLayoutComposite epLayoutComposite = CompositeFactory.createGridLayoutComposite(client, 1, false);
		((GridLayout) epLayoutComposite.getSwtComposite().getLayout()).horizontalSpacing = 0;
		((GridLayout) epLayoutComposite.getSwtComposite().getLayout()).marginWidth = 0;
		
		IPolicyTargetLayoutComposite composite = PolicyTargetCompositeFactory.wrapLayoutComposite(epLayoutComposite);

		initConditionBuilder = true;
		
		// add policy container
		PolicyActionContainer policyContainer = addPolicyActionContainer("promotionShopperSection");  //$NON-NLS-1$
		timeConditionPanel.createPageContents(composite, policyContainer);
		
		initConditionBuilder = false;

		getSection().getParent().setRedraw(true);
		
		getManagedForm().reflow(false);
	}

	@Override
	protected void populateControls() {
		// empty
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, false);
	}

	@Override
	public void dispose() {
		timeConditionPanel.dispose();
		super.dispose();
	}

	@Override
	protected boolean isModelNotValid(final BaseModelAdapter<ConditionalExpression> modelAdapter) {
		return false;
	}

	@Override
	protected AbstractConditionPanel<SellingContext> getConditionPanel() {
		return timeConditionPanel;
	}
	
}
