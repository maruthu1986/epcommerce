/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.editors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Base Amount editor page.
 */
public class PriceListDescriptorEditorPage extends AbstractPolicyAwareEditorPage {

	private static final String SPACER = "   ";
	private static final int THREE = 3;
	private final PriceListEditorController controller;
	private PriceListDescriptorSummaryPart descriptorSummaryPart;
	private static final int MIN_TEXT_WIDTH = 30;
	
	/**
	 * Constructor.
	 * @param editor the parent editor to this page
	 * @param controller the controller
	 */
	public PriceListDescriptorEditorPage(final PriceListEditor editor, final PriceListEditorController controller) {
		super(editor, "PriceListDescriptorEditor",
				PriceListManagerMessages.get().PriceListDescriptorEditorPage_Title, false); //$NON-NLS-1$
		this.controller = controller;
	}
	
	/**
	 * 
	 * @return the controller
	 */
	protected PriceListEditorController getController() {
		return this.controller;
	}
	
	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		this.descriptorSummaryPart = new PriceListDescriptorSummaryPart(this, editor, getController());
		
		PolicyActionContainer container = addPolicyActionContainer("priceListSummaryPage"); //$NON-NLS-1$
		addPart(container, managedForm, descriptorSummaryPart);

		addExtensionEditorSections(editor, managedForm, PriceListManagerPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}
	
	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return PriceListManagerMessages.get().PriceListDescriptorSummaryPage_Form_Title;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// Empty for now
	}

	/**
	 * Displays a PriceListDescriptor object. 
	 */
	class PriceListDescriptorSummaryPart extends AbstractPolicyAwareEditorPageSectionPart {

		private final PriceListEditorController controller;
		private Text nameText;
		private Text descriptionText;
		private Text currencyText;
		private IPolicyTargetLayoutComposite controlPane;
		private final ControlModificationListener modificationListener;
		private final DataBindingContext dataBindingContext;
		private final PriceListDescriptorDTO dto;

		/**
		 * Constructor.
		 * @param formPage the FormPage within which this SectionPart is to be contained
		 * @param editor the FormEdtor within which the given FormPage is contained
		 * @param controller the controller class
		 */
		PriceListDescriptorSummaryPart(
				final FormPage formPage, final AbstractCmClientFormEditor editor, final PriceListEditorController controller) {
			super(formPage, editor, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
			this.controller = controller;
			this.modificationListener = editor;
			this.dataBindingContext = editor.getDataBindingContext();
			dto = getController().getPriceListDescriptor();
		}
		
		/**
		 * 
		 * @return the controller
		 */
		protected PriceListEditorController getController() {
			return this.controller;
		}

		@Override
		public void createControls(final Composite parent, final FormToolkit toolkit) {
			PolicyActionContainer editableContainer = addPolicyActionContainer("priceListSummaryPageControls"); //$NON-NLS-1$
			IEpLayoutComposite mainPane = CompositeFactory.createGridLayoutComposite(parent, THREE, false);

			controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(mainPane);
			final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
			final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);
			
			controlPane.addLabelBoldRequired(PriceListManagerMessages.get().PriceListSearchResults_TableColumnTitle_Name,
					labelData, editableContainer);
			nameText = controlPane.addTextField(fieldData, editableContainer);
			setMinimumTextWidth(nameText, MIN_TEXT_WIDTH);
			controlPane.addLabel(SPACER, labelData, editableContainer);

			controlPane.addLabelBold(PriceListManagerMessages.get().PriceListSearchResults_TableColumnTitle_Description,
					labelData, editableContainer);
			descriptionText = controlPane.addTextField(fieldData, editableContainer);
			controlPane.addLabel(SPACER, labelData, editableContainer);

			controlPane.addLabelBoldRequired(PriceListManagerMessages.get().PriceListSearchResults_TableColumnTitle_CurrencyCode,
					labelData, editableContainer);
			currencyText = controlPane.addTextField(fieldData, editableContainer);
			controlPane.addLabel(SPACER, labelData, editableContainer);

			addCompositesToRefresh(controlPane.getSwtComposite());
		}
		
		/**
		 * Set the minimum width of a text control to a given number of characters.
		 * 
		 * @param text control
		 * @param width minimum characters
		 */
		private void setMinimumTextWidth(final Text text, final int width) {
			GC graphics = new GC(text);
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			layoutData.widthHint = graphics.getFontMetrics().getAverageCharWidth() * width;
			text.setLayoutData(layoutData);
			graphics.dispose();
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.currencyText, this.dto, "currencyCode", //$NON-NLS-1$
					EpValidatorFactory.CURRENCY_CODE, null, true);	
			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.descriptionText, this.dto, "description", //$NON-NLS-1$
					null, null, true);	
			EpControlBindingProvider.getInstance().bind(dataBindingContext, this.nameText, this.dto, "name", //$NON-NLS-1$
					EpValidatorFactory.STRING_255_REQUIRED, null, true);	
		}
		
		@Override
		public void populateControls() {
			nameText.setText(StringUtils.defaultString(dto.getName()));
			descriptionText.setText(StringUtils.defaultString(dto.getDescription()));
			currencyText.setText(StringUtils.defaultString(dto.getCurrencyCode()));
			
			//Editor will listen to changes and mark dirty
			controlPane.setControlModificationListener(modificationListener);
		}

		@Override
		protected String getSectionTitle() {
			return PriceListManagerMessages.get().PriceListSummaryPage_UserDefinedSection;
		}
	}
}
