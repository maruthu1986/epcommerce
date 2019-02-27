/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Summary section.
 */
public class SummarySection extends AbstractPolicyAwareEditorPageSectionPart {

	private static final int DESCRIPTION_TEXT_AREA_HEIGHT = 80;
	
	private Text name, description;
	
	private IEpLayoutComposite controlPane;
	
	private final ControlModificationListener controlModificationListener;
	
	private final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
	
	private EpValueBinding bindingName;
	
	private EpValueBinding bindingDescription;
	
	
	/**
	 * Custom constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public SummarySection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT);
		controlModificationListener = editor;
	}	
	
	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		
		if (bindingName != null) {
			EpControlBindingProvider.removeEpValueBinding(bindingContext, bindingName);
		}
		if (bindingDescription != null) {
			EpControlBindingProvider.removeEpValueBinding(bindingContext, bindingDescription);
		}
		
		bindingName = bindingProvider.bind(bindingContext, name, getModel(), "name", //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_100, null, true);
		
		bindingDescription = bindingProvider.bind(bindingContext, description, getModel(), "description", //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_2000, null, true);
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		controlPane = CompositeFactory.createTableWrapLayoutComposite(parent, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		controlPane.setLayoutData(data);
		
		IPolicyTargetLayoutComposite policyComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(controlPane);
		PolicyActionContainer container = addPolicyActionContainer("conditionBuilderSummarySection"); //$NON-NLS-1$
		
		final IEpLayoutData labelData = policyComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		final IEpLayoutData fieldData = policyComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		controlPane.addLabelBoldRequired(TargetedSellingMessages.get().Name, EpState.EDITABLE, labelData);
		name = policyComposite.addTextField(fieldData, container);

		policyComposite.addLabelBold(TargetedSellingMessages.get().Dictionary, labelData, container);
		policyComposite.addLabel(getModel().getTagDictionaryGuid(), fieldData, container);
		
		policyComposite.addLabelBold(TargetedSellingMessages.get().Description, labelData, container);
		description = policyComposite.addTextArea(true, false, fieldData, container);
		final TableWrapData twdDescriptionText = new TableWrapData();
		twdDescriptionText.heightHint = DESCRIPTION_TEXT_AREA_HEIGHT;
		description.setLayoutData(twdDescriptionText);
	}

	@Override
	protected void populateControls() {
		if (getModel().getName() == null) {
			name.setText(StringUtils.EMPTY);
		} else {
			name.setText(getModel().getName());
		}
			
		if (getModel().getDescription() == null) {
			description.setText(StringUtils.EMPTY);
		} else {
			description.setText(getModel().getDescription());
		}
		
		controlPane.setControlModificationListener(controlModificationListener);
	}

	@Override
	public ConditionalExpression getModel() {
		return ((ConditionEditor) getEditor()).getDependentObject();
	}
}
