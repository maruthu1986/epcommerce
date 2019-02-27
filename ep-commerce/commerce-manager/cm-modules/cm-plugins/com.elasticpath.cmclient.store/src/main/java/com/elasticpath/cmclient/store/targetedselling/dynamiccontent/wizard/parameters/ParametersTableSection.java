/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.parameters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.dialog.value.support.IValueChangedListener;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.helpers.LocaleComparator;
import com.elasticpath.cmclient.core.ui.dialog.CategoryFinderDialog;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyTargetImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.service.store.StoreService;

/**
 * Parameters table section abstraction.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class ParametersTableSection extends AbstractStatePolicyTargetImpl implements SelectionListener,  
	ISelectionChangedListener, IValueChangedListener<ParameterValue> {

	private static final int VALUE_COLUMN_WIDTH = 200;

	private static final int MULTILINGUAL_COLUMN_WIDTH = 90;

	private static final int TYPE_COLUMN_WIDTH = 90;

	private static final int NAME_COLUMN_WIDTH = 200;

	private static final Logger LOG = Logger.getLogger(ParametersTableSection.class);

	private static final String PARAMETERS_TABLE = "Parameters"; //$NON-NLS-1$

	/**
	 * the table viewer for the parameters info table.
	 */
	private IEpTableViewer parametersTableViewer;

	/**
	 * the main composite for parameters section on parameters page.
	 */
	private IPolicyTargetLayoutComposite policyComposite;

	private DynamicContent model;

	private ControlModificationListener controlModificationListener;

	private Locale selectedLocale;

	private CCombo languageSelectCombo;

	private final SortedSet<Locale> availableLocales = new TreeSet<>(new LocaleComparator());

	private ParametersLabelProvider propertiesLabelProvider;
	
	private final IValueChangedListener<ParameterValue>[] listenersChain;

	private ParametersEditingSupport editorSupport;

	private Button editButton;

	private Button resetButton;

	private IPolicyTargetLayoutComposite buttonsComposite;
	
	private StatePolicy statePolicy;
	
	private PolicyActionContainer controlsContainer;
	
	private IEpTableColumn valueColumn;

	
	/**
	 * Table row sorter.
	 */
	private class ParameterValueNameSorter  extends ViewerSorter implements Comparator<ParameterValue> {
		
		@Override
		public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
			return compare((ParameterValue) obj1, (ParameterValue) obj2);
		}

		@Override
		public int compare(final ParameterValue parameterValue1, final ParameterValue parameterValue2) {
			return parameterValue1.getParameterName().compareTo(parameterValue2.getParameterName());
		}
		
	}	

	/**
	 * Constructs the object.
	 * 
	 * @param model the object model
	 * @param listenersChain chain of listeners to be added to the editing support
	 */
	@SafeVarargs
	public ParametersTableSection(final DynamicContent model,
								  final IValueChangedListener<ParameterValue>... listenersChain) {
		this.model = model;
		this.listenersChain = listenersChain;
		this.selectedLocale = Locale.ENGLISH;
	}

	/**
	 * Creates UI controls.
	 * 
	 * @param policyComposite the main composite
	 */
	public void createControls(final IPolicyTargetLayoutComposite policyComposite) {
		this.policyComposite = policyComposite;
		
		controlsContainer = addPolicyActionContainer("parametersTableSectionControls"); //$NON-NLS-1$

		final IEpLayoutData fieldData = policyComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false);
		final IEpLayoutData fieldDataFill = policyComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING, true, false);

		policyComposite.addLabelBold(TargetedSellingMessages.get().NewDynamicContentWizard_LanguageCombo_Label, fieldDataFill, controlsContainer);

		languageSelectCombo = policyComposite.addComboBox(fieldData, controlsContainer);
		languageSelectCombo.setItems(getSupportedLocales());
		languageSelectCombo.select(getSelectedLocaleIndex());
		languageSelectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Locale newLocale = getSelectedLocale();
				ParametersTableSection.this.setSelectedLocale(newLocale);
				ParametersTableSection.this.editorSupport.setLocale(newLocale);
				LOG.debug("Language changed to " + newLocale); //$NON-NLS-1$
				ParametersTableSection.this.refreshTableModelForNewLocale();
			}
		});

		// layout for the table area
		final IEpLayoutData tableLayoutData = policyComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		this.parametersTableViewer = policyComposite.addTableViewer(false, tableLayoutData, controlsContainer, PARAMETERS_TABLE);
		
		// parametersTableViewer.getSwtTable().set
		// the name column content of the parameters table
		this.parametersTableViewer.addTableColumn(TargetedSellingMessages.get().ParameterValueEditor_TableColumnTitle_Name, NAME_COLUMN_WIDTH);
		this.parametersTableViewer.addTableColumn(TargetedSellingMessages.get().ParameterValueEditor_TableColumnTitle_Type, TYPE_COLUMN_WIDTH);
		this.parametersTableViewer.addTableColumn(TargetedSellingMessages.get().ParameterValueEditor_TableColumnTitle_MLang,
				MULTILINGUAL_COLUMN_WIDTH);
		
		valueColumn = this.parametersTableViewer.addTableColumn(
				TargetedSellingMessages.get().ParameterValueEditor_TableColumnTitle_Value,
				VALUE_COLUMN_WIDTH);

		// add EditSupport to the parameters value column
		editorSupport = new ParametersEditingSupport(this.parametersTableViewer, this.getModel(), getSelectedLocale());
		editorSupport.addValueChangedListener(this);
		// register additional listeners
		if (this.listenersChain != null && this.listenersChain.length > 0) {
			for (IValueChangedListener<ParameterValue> listener : listenersChain) {
				editorSupport.addValueChangedListener(listener);
			}
		}
		valueColumn.setEditingSupport(editorSupport);
		propertiesLabelProvider = new ParametersLabelProvider(getSelectedLocale());
		this.parametersTableViewer.setLabelProvider(propertiesLabelProvider);
		this.parametersTableViewer.setContentProvider(new ArrayContentProvider());
		
		// edit/clear buttons
		final IEpLayoutData layoutDataButtonsComposite = policyComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, true);
		buttonsComposite = policyComposite.addGridLayoutComposite(1, false, layoutDataButtonsComposite, controlsContainer);
		
		// create edit button for invoking the editing
		editButton = buttonsComposite.addPushButton(TargetedSellingMessages.get().DynamicContentWizardParametersPage_ButtonEdit, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_EDIT), policyComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.BEGINNING), controlsContainer);
		editButton.addSelectionListener(this);

		resetButton = buttonsComposite.addPushButton(TargetedSellingMessages.get().DynamicContentWizardParametersPage_ButtonReset,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_X), 
				policyComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING), 
				controlsContainer);
		resetButton.addSelectionListener(this);
		editButton.setEnabled(false);
		resetButton.setEnabled(false);
		
		parametersTableViewer.getSwtTableViewer().setSorter(new ParameterValueNameSorter());
		parametersTableViewer.getSwtTableViewer().addSelectionChangedListener(this);
	}
	
	/**
	 * get list of all available locales for this parameter section.
	 * used in <code>NewDynamicContentWizardParametersPage</code> for
	 * validation purpose.
	 * 
	 * @return set of supported locales
	 */
	public SortedSet<Locale> getAvailableLocales() {
		return this.availableLocales;
	}

	/**
	 * Enable the edit buttons while the selection changed.
	 * 
	 * @param event selection changed event.
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		setButtonState();
	}

	private void setButtonState() {
		editButton.setEnabled(isParameterSelected() && isEditable(controlsContainer));
		resetButton.setEnabled(isParameterSelected() && isEditable(controlsContainer));
	}

	private boolean isParameterSelected() {
		final IStructuredSelection selection = (IStructuredSelection) parametersTableViewer.getSwtTableViewer().getSelection();
		return selection != null && !selection.isEmpty();
	}

	/**
	 * Sets the control modification listener for this UI part.
	 * 
	 * @param controlModificationListener listener implementor
	 */
	public void setControlModificationListener(final ControlModificationListener controlModificationListener) {
		this.controlModificationListener = controlModificationListener;
	}

	/**
	 * Updates the locale selection and refreshed the table view.
	 */
	public void refreshTableModelForNewLocale() {

		List<ParameterValue> parameterValuesList = new ArrayList<>();
		
		if (null != model && null != model.getParameterValues() && model.getParameterValues().size() > 0) {
			propertiesLabelProvider.setLocale(getSelectedLocale());
			editorSupport.setLocale(getSelectedLocale());
			parameterValuesList = model.getParameterValues();
		} 
		ParameterValue[] parameters = new ParameterValue[parameterValuesList.size()];
		parameters = parameterValuesList.toArray(parameters);
		parametersTableViewer.setInput(parameters);
		parametersTableViewer.getSwtTableViewer().refresh();
	}
	
	/**
	 * Updates the table view.
	 */
	public void refreshTableModel() {

		List<ParameterValue> parameterValuesList = new ArrayList<>();
		
		if (null != model && null != model.getParameterValues() && model.getParameterValues().size() > 0) {
			parameterValuesList = model.getParameterValues();
		} 
		
		parametersTableViewer.setInput(parameterValuesList.toArray(new ParameterValue[parameterValuesList.size()]));
		parametersTableViewer.getSwtTableViewer().refresh();
	}

	/**
	 * Returns model for this section.
	 * 
	 * @return DynamicContent
	 */
	public DynamicContent getModel() {
		return model;
	}

	/**
	 * Set the model for this section.
	 * 
	 * @param model - DynamicContent model
	 */
	public void setModel(final DynamicContent model) {
		this.model = model;
	}

	/**
	 * Returns the locale that the user has selected.
	 * 
	 * @return the currently selected <code>Locale</code>.
	 */
	public Locale getSelectedLocale() {
		int selectionIndex = languageSelectCombo.getSelectionIndex();
		if (selectionIndex > -1) {
			return new ArrayList<>(availableLocales).get(selectionIndex);
		}
		return null;
	}

	@Override
	public void valueChanged(final ParameterValue parameterValue) {
		this.refreshTableModelForNewLocale();
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) parametersTableViewer.getSwtTableViewer().getSelection();
		final ParameterValue parameter = (ParameterValue) selection.getFirstElement();

		if (null == parameter) {
			return;
		}

		if (event.getSource() == editButton) {
			final Shell shell = policyComposite.getLayoutComposite().getSwtComposite().getShell();
			Window dialog = editorSupport.getEditorDialog(parameter.getParameter().getType(), 
					parameter, shell, true, parameter.getParameter().isRequired());
			final int result = dialog.open();

			if (result == Window.OK) {
				
				if (dialog instanceof ProductFinderDialog) {
					final ProductFinderDialog productFinderDialog = (ProductFinderDialog) dialog;
					final Product product = (Product) productFinderDialog.getSelectedObject();
					parameter.setValue(product.getCode(), getSelectedLocale().toString());
				} else if (dialog instanceof CategoryFinderDialog) {
					final CategoryFinderDialog categoryFinderDialog = (CategoryFinderDialog) dialog;
					final Category category = (Category) categoryFinderDialog.getSelectedObject();
					parameter.setValue(category.getCode(), getSelectedLocale().toString());
				} else {
					final IValueRetriever retriever = (IValueRetriever) dialog;
					editorSupport.setValueToElement(parameter, retriever.getValue());					
				}
				
				
				setParameter(parameter);
				refreshTableModel();
				editorSupport.fireValueChangedEvent(parameter);
				parameterValueChanged();
			}
		}

		if (event.getSource() == resetButton) {
			parameter.setValue(null, getSelectedLocale().toString());
			setParameter(parameter);
			refreshTableModel();
			editorSupport.fireValueChangedEvent(parameter);
			parameterValueChanged();
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		LOG.debug("widgetDefaultSelected: " + event); //$NON-NLS-1$
	}

	/**
	 * Set the selected locale of this page section.
	 * 
	 * @param selected locale
	 */
	private void setSelectedLocale(final Locale selected) {
		this.selectedLocale = selected;
	}

	private int getSelectedLocaleIndex() {
		//we don't leave empty language combo. If client locale doesn't correspond any 
		//of stores locales - select first.
		int selectedLocaleIndex = new ArrayList<>(availableLocales).indexOf(selectedLocale);
		if (0 > selectedLocaleIndex) {
			return 0;
		}
		return selectedLocaleIndex;	
	}

	/**
	 * table viewer control.
	 * @return table viewer 
	 */
	public IEpTableViewer getParametersTableViewer() {
		return parametersTableViewer;
	}
	
	/**
	 * language selection combo box control.
	 * @return language selection combo box 
	 */
	public CCombo getLanguageSelectCombo() {
		return languageSelectCombo;
	}
	

	private String[] getSupportedLocales() {
		populateAvailableLocalesList();

		List<String> availableLocalesAsString = new LinkedList<>();

		for (Locale locale : availableLocales) {
			availableLocalesAsString.add(locale.getDisplayName());
		}

		return availableLocalesAsString.toArray(new String[availableLocalesAsString.size()]);
	}

	private void populateAvailableLocalesList() {
		final StoreService dcService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		List<Store> stores = dcService.findAllStores();
		for (Store store : stores) {
			if (store.getStoreState().equals(StoreState.OPEN) || store.getStoreState().equals(StoreState.RESTRICTED)) {
				availableLocales.addAll(store.getSupportedLocales());
			}
		}
	}

	private void parameterValueChanged() {
		if (controlModificationListener != null) {
			controlModificationListener.controlModified();
		}
	}

	private void setParameter(final ParameterValue inparameter) {
		List<ParameterValue> parameters = getModel().getParameterValues();
		if (null == parameters || null == inparameter) {
			return;
		}
		for (ParameterValue parameter : parameters) {
			if (inparameter.equals(parameter)) {
				getModel().getParameterValues().set(parameters.indexOf(parameter), inparameter);
				break;
			}
		}

	}
	
	@Override
	public String getTargetIdentifier() {
		return "dynamicContentParametersTableSection"; //$NON-NLS-1$
	}

	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		this.statePolicy = policy;
		super.applyStatePolicy(policy);
		setButtonState();
		
		//this will affect the valueColumn is editable or not
		this.parametersTableViewer.setEnableEditMode(isEditable(controlsContainer));
		valueColumn.setEditingSupport(editorSupport);
	}
	
	/**
	 * determine state of speicfic container.
	 * 
	 * @param container the container
	 * @return authorized if state policies return EDITABLE state 
	 */
	protected boolean isEditable(final PolicyActionContainer container) {
		return (statePolicy != null && EpState.EDITABLE.equals(statePolicy.determineState(container)));
	}

}
