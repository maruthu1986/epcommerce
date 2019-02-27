/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * UI representation of the Store Taxes Section.
 * 
 * TODO: Should be refactored throw addPart in StoreTaxesPage
 */
public class StoreTaxesSectionPart extends AbstractCmClientEditorPageSectionPart {
	private IEpLayoutComposite mainPane;

	private CheckboxTableViewer taxJurisdictionTableViewer;

	private CheckboxTableViewer taxCodesTableViewer;
	
	private final boolean editable;

	/** The log. */
	private static final Logger LOG = Logger.getLogger(StoreTaxesSectionPart.class);
	
	/**
	 * Constructor.
	 * 
	 * @param formPage the form page
	 * @param editor the editor containing this Section's Constructor to create a new Section in an editor's FormPage
	 * @param editable whether this section should be editable 
	 */
	public StoreTaxesSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean editable) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
		this.getSection().setEnabled(editable);
		this.editable = editable;
	}

	/*
	 * @return true if it is edit dialog
	 */
	private boolean isEditStore() {
		return getStoreEditorModel().isPersistent();
	}

	/*
	 * @return true if it is edit dialog
	 */
	private StoreEditorModel getStoreEditorModel() {
		return (StoreEditorModel) getEditor().getModel();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// do nothing

	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 1, false);
		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

		final IEpLayoutComposite taxesPane = CompositeFactory.createTableWrapLayoutComposite(mainPane.getSwtComposite(), 2, true);
		taxesPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

		final TaxRegionSubSection regions = new TaxRegionSubSection(isEditable());
		regions.createControls(taxesPane.getSwtComposite(), toolkit);
		regions.populateControls();

		final TaxCodeSubSection codes = new TaxCodeSubSection(isEditable());
		codes.createControls(taxesPane.getSwtComposite(), toolkit);
		codes.populateControls();
	}

	@Override
	protected void populateControls() {
		// do nothing

	}

	@Override
	protected String getSectionDescription() {
		return AdminStoresMessages.get().StoreTaxes_Description;
	}

	@Override
	protected String getSectionTitle() {
		return AdminStoresMessages.get().StoreTaxes_Title;
	}

	/** @return whether this section is editable */
	boolean isEditable() {
		return this.editable;
	}
	
	/**
	 * Represents tax codes section.
	 */
	class TaxCodeSubSection implements ICheckStateListener {

		private static final int COLUMN_WIDTH = 250;
		private static final String TAX_CODES_TABLE = "Tax Codes"; //$NON-NLS-1$

		private final TaxCodeService taxCodeService;

		private final Set<TaxCode> initiallyCheckedTaxCodes;
		
		private final boolean editable;

		/**
		 * Constructor.
		 * @param editable whether this section should be editable
		 */
		TaxCodeSubSection(final boolean editable) {
			this.taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
			initiallyCheckedTaxCodes = new HashSet<>(getInitiallyCheckedTaxCodes());
			this.editable = editable;
		}

		private Set<TaxCode> getInitiallyCheckedTaxCodes() {
			if (isEditStore()) {
				return getStoreEditorModel().getTaxCodes();
			}
			return new HashSet<>(0);
		}

		private boolean isEditable() {
			return this.editable;
		}
		
		/**
		 * Override to create the controls to be displayed on the page section.
		 * 
		 * @param client the parent composite to add controls to
		 * @param toolkit the FormToolKit
		 */
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			final Section section = toolkit.createSection(client, ExpandableComposite.TITLE_BAR);
			section.setEnabled(isEditable());
			section.setText(AdminStoresMessages.get().TaxConfiguration_TaxCodes);
			final TableWrapData twd = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL_GRAB);
			twd.grabVertical = true;
			twd.grabHorizontal = true;
			section.setLayoutData(twd);

			final IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);
			final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);

			taxCodesTableViewer = controlPane.addCheckboxTableViewer(EpState.READ_ONLY, fieldData, false, TAX_CODES_TABLE);
			section.setClient(controlPane.getSwtComposite());

			final TableViewerColumn tableViewerColumn = new TableViewerColumn(taxCodesTableViewer, SWT.LEFT);
			tableViewerColumn.getColumn().setWidth(COLUMN_WIDTH);

			taxCodesTableViewer.setContentProvider(new ArrayContentProvider());
			taxCodesTableViewer.setLabelProvider(new TaxCodeListViewLabelProvider());
			taxCodesTableViewer.addCheckStateListener(this);
		}

		/**
		 * Override to populate the controls with their initial values.
		 */
		protected void populateControls() {
			final List<TaxCode> taxCodeList = taxCodeService.list();

			taxCodesTableViewer.setInput(taxCodeList);

			if (isEditStore()) {
				LOG.trace("Edit store:"); //$NON-NLS-1$
				// setting checked Tax Codes
				final Set<TaxCode> checkedTaxCodes = getStoreEditorModel().getTaxCodes();
				taxCodesTableViewer.setCheckedElements(checkedTaxCodes.toArray());

				if (LOG.isDebugEnabled()) {
					LOG.debug("tax codes that should be checked: "); //$NON-NLS-1$
					LOG.debug(Arrays.toString(checkedTaxCodes.toArray()));
				}

			}
		}

		/**
		 * Label provider for <code>TaxCode</code> domain object.
		 */
		protected class TaxCodeListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

			@Override
			public Image getColumnImage(final Object element, final int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(final Object element, final int columnIndex) {
				final TaxCode taxCode = (TaxCode) element;
				return taxCode.getCode();
			}
		}

		@Override
		public void checkStateChanged(final CheckStateChangedEvent event) {
			final TaxCode taxCode = (TaxCode) event.getElement();

			final boolean isChecked = event.getChecked();

			Set<TaxCode> enabledTaxCodes = getStoreEditorModel().getTaxCodes();
			if (enabledTaxCodes == null) {
				enabledTaxCodes = new HashSet<>();
			}

			if (isChecked) {
				enabledTaxCodes.add(taxCode);
			} else {
				enabledTaxCodes.remove(taxCode);
			}

			if (checkedTaxCodesModified(taxCode, isChecked)) {
				getEditor().controlModified();
			}

			getStoreEditorModel().setTaxCodes(enabledTaxCodes);
		}

		private boolean checkedTaxCodesModified(final TaxCode taxCode, final boolean isChecked) {
			return isChecked != initiallyCheckedTaxCodes.contains(taxCode);
		}
	}

	/**
	 * Represents tax regions section.
	 */
	class TaxRegionSubSection implements ICheckStateListener {
		private static final int COLUMN_WIDTH = 250;

		private static final String TAX_JURISDICTION_TABLE = "Tax Jurisdiction"; //$NON-NLS-1$

		private final TaxJurisdictionService taxService;

		private final Set<TaxJurisdiction> initiallyCheckedTaxJurisdictions;
		
		private final boolean editable;

		/**
		 * Constructor.
		 * @param editable whether this section should be editable
		 */
		TaxRegionSubSection(final boolean editable) {
			this.taxService = ServiceLocator.getService(ContextIdNames.TAX_JURISDICTION_SERVICE);
			initiallyCheckedTaxJurisdictions = new HashSet<>(getInitiallyCheckedTaxJurisdictions());
			this.editable = editable;
		}

		private Set<TaxJurisdiction> getInitiallyCheckedTaxJurisdictions() {
			if (isEditStore()) {
				return getStoreEditorModel().getTaxJurisdictions();
			}
			return new HashSet<>(0);
		}
		
		private boolean isEditable() {
			return this.editable;
		}

		/**
		 * Override to create the controls to be displayed on the page section.
		 * 
		 * @param client the parent composite to add controls to
		 * @param toolkit the FormToolKit
		 */
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			final Section section = toolkit.createSection(client, ExpandableComposite.TITLE_BAR);
			section.setEnabled(isEditable());
			section.setText(AdminStoresMessages.get().TaxConfiguration_TaxRegions);
			final TableWrapData twd = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL_GRAB);
			twd.grabVertical = true;
			twd.grabHorizontal = true;
			section.setLayoutData(twd);

			final IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(section, 1, false);
			final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			taxJurisdictionTableViewer = controlPane.addCheckboxTableViewer(EpState.READ_ONLY, fieldData, false, TAX_JURISDICTION_TABLE);
			section.setClient(controlPane.getSwtComposite());

			final TableViewerColumn tableViewerColumn = new TableViewerColumn(taxJurisdictionTableViewer, SWT.LEFT);
			tableViewerColumn.getColumn().setWidth(COLUMN_WIDTH);

			taxJurisdictionTableViewer.setContentProvider(new ArrayContentProvider());
			taxJurisdictionTableViewer.setLabelProvider(new TaxJurisdictionListLabelProvider());
			taxJurisdictionTableViewer.addCheckStateListener(this);
		}

		/**
		 * Label provider for <code>TaxJurisdiction</code> domain object.
		 */
		class TaxJurisdictionListLabelProvider extends LabelProvider {

			private final Geography geography;
			
			/**
			 * Constructor.
			 */
			TaxJurisdictionListLabelProvider() {
				geography = ServiceLocator.getService(ContextIdNames.GEOGRAPHY);
			}

			@Override
			public Image getImage(final Object element) {
				return null;
			}

			@Override
			public String getText(final Object element) {
				final TaxJurisdiction tax = (TaxJurisdiction) element;
				return geography.getCountryDisplayName(tax.getRegionCode(), CorePlugin.getDefault().getDefaultLocale());
			}
		}

		/**
		 * Override to populate the controls with their initial values.
		 */
		protected void populateControls() {
			final List<TaxJurisdiction> taxes = taxService.list();

			taxJurisdictionTableViewer.setInput(taxes);

			if (isEditStore()) {
				LOG.trace("Edit store:"); //$NON-NLS-1$
				// setting checked Tax Jurisdictions
				final Set<TaxJurisdiction> checkedTaxJurisdictions = getStoreEditorModel().getTaxJurisdictions();
				for (final TaxJurisdiction taxJurisdiction : checkedTaxJurisdictions) {
					taxJurisdictionTableViewer.setChecked(taxJurisdiction, true);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Should check tax regions: "); //$NON-NLS-1$
					for (final TaxJurisdiction object : checkedTaxJurisdictions) {
						LOG.debug(object.getRegionCode());
					}
				}
			}
		}

		@Override
		public void checkStateChanged(final CheckStateChangedEvent event) {
			final TaxJurisdiction taxJurisdiction = (TaxJurisdiction) event.getElement();

			final boolean isChecked = event.getChecked();

			Set<TaxJurisdiction> enabledTaxJurisdictions = getStoreEditorModel().getTaxJurisdictions();
			if (enabledTaxJurisdictions == null) {
				enabledTaxJurisdictions = new HashSet<>();
			}

			if (isChecked) {
				enabledTaxJurisdictions.add(taxJurisdiction);
			} else {
				enabledTaxJurisdictions.remove(taxJurisdiction);
			}

			if (checkedTaxCodesModified(taxJurisdiction, isChecked)) {
				getEditor().controlModified();
			}

			getStoreEditorModel().setTaxJurisdictions(enabledTaxJurisdictions);
		}

		private boolean checkedTaxCodesModified(final TaxJurisdiction taxJurisdiction, final boolean isChecked) {
			return isChecked != initiallyCheckedTaxJurisdictions.contains(taxJurisdiction);
		}
	}
}
