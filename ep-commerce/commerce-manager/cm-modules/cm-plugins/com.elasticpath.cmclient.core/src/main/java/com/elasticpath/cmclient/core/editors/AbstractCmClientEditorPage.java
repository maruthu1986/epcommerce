/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.controlcontribution.LanguagePulldownContribution;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;

/**
 * Abstract implementation of a page in a Multi-Part editor.
 */
public abstract class AbstractCmClientEditorPage extends FormPage {

	/**
	 * Horizontal spacing between two UI components. 
	 */
	private static final int HORIZONTAL_SPACING = 15;

	/**
	 * Right layout margin.
	 */
	protected static final int RIGHT_MARGIN = 10;

	/**
	 * Left layout margin.
	 */
	protected static final int LEFT_MARGIN = 10;
	
	private static final Logger LOG = Logger.getLogger(AbstractCmClientEditorPage.class);

	private boolean isLocaleDependent;
	
	private LanguagePulldownContribution languageSelector;

	private Collection<Locale> supportedLocales;

	private Locale defaultLocale;

	private Locale selectedLocale;

	private final Map<String, Object> customPageData = new HashMap<>();

	/**
	 * Constructs the editor page.
	 * 
	 * @param editor The EP FormEditor
	 * @param partId the id for the editor page
	 * @param title the title of the editor page
	 */
	public AbstractCmClientEditorPage(final AbstractCmClientFormEditor editor, final String partId, final String title) {
		super(editor, partId, title);
	}
	
	/**
	 * Constructs the editor page.
	 * 
	 * @param editor The EP FormEditor
	 * @param partId the id for the editor page
	 * @param title the title of the editor page
	 * @param isLocaleDependent indicate whether to show the locale selection combo 
	 */
	public AbstractCmClientEditorPage(final AbstractCmClientFormEditor editor, final String partId, 
			final String title, final boolean isLocaleDependent) {
		
		super(editor, partId, title);
		this.isLocaleDependent = isLocaleDependent;

	}

	/**
	 * This method delegates the creation of the widgets to the createEditorContent.
	 * Do not override it because it is used as a setup for the testing.
	 *
	 * @param managedForm form
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		//Set test ids to the form if in test mode and delegate the creation to another method
		final ScrolledForm form = managedForm.getForm();
		EPTestUtilFactory.getInstance().getTestIdUtil().setAutomationId(form, getId());

		createEditorContent(managedForm);
	}

	/**
	 * This method creates the content of the editor inside of the managed form.
	 * Override this method instead of createFormContent.
	 *
	 * @param managedForm form
	 */
	protected void createEditorContent(final IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		form.addDisposeListener(new DisposeListener() {

			/** called when com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor.refreshEditorPages releases control. */
			public void widgetDisposed(final DisposeEvent disposeEvent) {				
				for (IFormPart formPart : managedForm.getParts()) {
					AbstractCmClientFormSectionPart abstractFormPart = (AbstractCmClientFormSectionPart) formPart;
					abstractFormPart.sectionDisposed();
				}
				pageDisposed();
				if (languageSelector != null) {
					languageSelector.dispose();
				}
			}
			
		});
		form.setText(getFormTitle());

		form.getBody().setLayout(getLayout());
		
		//prepare tool bar
		final IToolBarManager toolBarManager = form.getToolBarManager();
		addToolbarActions(toolBarManager);
		
		Locale initialLocale = selectedLocale;
		if (initialLocale == null) {
			initialLocale = defaultLocale;
		}
		if (this.isLocaleDependent) {
			languageSelector = new LanguagePulldownContribution("LanguageControl", initialLocale); //$NON-NLS-1$
			
			toolBarManager.add(languageSelector);
			
			languageSelector.setSupportedLocales(supportedLocales);
			languageSelector.setDefaultLocale(defaultLocale);
			languageSelector.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					Locale newLocale = getSelectedLocale();
					//reload editors with the new locale instead of letting them change their own text.
					//dirty states remain as expected, and existing changes to model are untouched
					setSelectedLocale(newLocale);
					LOG.debug("Language changed to " + newLocale); //$NON-NLS-1$
					((AbstractCmClientFormEditor) getEditor()).refreshEditorPages();

				}
			});
		}		
		toolBarManager.update(true);

		//prepare sections
		addEditorSections((AbstractCmClientFormEditor) getEditor(), managedForm);
	}

	
	@Override
	public void initialize(final FormEditor editor) {
		super.initialize(editor);
		if (editor instanceof AbstractCmClientFormEditor) {
			supportedLocales = ((AbstractCmClientFormEditor) editor).getSupportedLocales();
			defaultLocale = ((AbstractCmClientFormEditor) editor).getDefaultLocale();
		}
	}

	/**
	 * Returns the layout of the page to be set to the form body.
	 * 
	 * @return Layout instance
	 */
	protected Layout getLayout() {
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = getFormColumnsCount();
		layout.leftMargin = LEFT_MARGIN;
		layout.rightMargin = RIGHT_MARGIN;
		layout.horizontalSpacing = HORIZONTAL_SPACING;
		return layout;
	}

	/**
	 * A method for adding actions to the toobar manager. Use <code>IToolBarManager#add(org.eclipse.jface.action.IContributionItem)</code>
	 * 
	 * @param toolBarManager the form page toolbar manager
	 * @see org.eclipse.jface.action.IContributionManager#add(org.eclipse.jface.action.IContributionItem)
	 */
	protected abstract void addToolbarActions(IToolBarManager toolBarManager);

	/**
	 * That is the place where the sections of the page should be added.
	 * 
	 * @param editor the EP form editor
	 * @param managedForm the Eclipse managed form
	 */
	protected abstract void addEditorSections(AbstractCmClientFormEditor editor, IManagedForm managedForm);

	/**
	 * Gets the form column count for the layout grid.
	 * 
	 * @return number of columns. Should be greater than 0
	 */
	protected abstract int getFormColumnsCount();

	/**
	 * Gets the form page title.
	 * 
	 * @return form page title
	 */
	protected abstract String getFormTitle();
	

	/**
	 * Get the language Selector.
	 * 
	 * @return the language selector
	 */
	private LanguagePulldownContribution getLanguageSelector() {
		return languageSelector;
	}
	
	
	/**
	 * Set the selected locale of this editor page.
	 * 
	 * @param selected locale
	 */
	private void setSelectedLocale(final Locale selected) {
		selectedLocale = selected;
	}

	/**
	 * Returns the locale that the user has selected.
	 *
	 * @return the currently selected <code>Locale</code>. If this page is not locale dependent, return null.
	 */
	public Locale getSelectedLocale() {
		if (this.isLocaleDependent) {
			int selectionIndex = getLanguageSelector().getLanguageSelectorCombo().getSelectionIndex();
			if (selectionIndex > -1) {
				return getLanguageSelector().getLocaleList().get(selectionIndex);
			} 
		}
		return null;
		
	}
	
	/**
	 * Removes and disposes all form parts.
	 */
	protected void removeAndDisposeFormParts() {
		final IManagedForm managedForm = getManagedForm();
		for (final IFormPart part : managedForm.getParts()) {
			getManagedForm().removePart(part);
			part.dispose();
		}
	}

	@Override
	public Control getPartControl() {
		final Control partControl = super.getPartControl();
		
		if (partControl == null || partControl.isDisposed()) {
			return null;
		}
		return partControl;
	}
	
	/**
	 * 
	 * @param newContext data binding context
	 */
	public void refreshDataBindings(final DataBindingContext newContext) {
		if (getManagedForm() != null) { // if page has already been created this will not be null
			IFormPart[] pageFormParts = getManagedForm().getParts();
	
			for (IFormPart formPart : pageFormParts) {
				if (formPart instanceof AbstractCmClientEditorPageSectionPart) {
					AbstractCmClientEditorPageSectionPart sectionPart = (AbstractCmClientEditorPageSectionPart) formPart;
					sectionPart.refreshDataBindings(newContext);
				}
			}
		}
	}
	
	/**
	 *  Called when com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor.refreshEditorPages releases control.
	 *  Can be overridden to expose some clean-up activities.  
	 */
	public void pageDisposed() {
		// nothing by default.
	}

	/**
	 * Add page sections from extensions.
	 *
	 * @param editor the editor.
	 * @param managedForm the form.
	 * @param pluginId the plugin id.
	 * @param pageId the page id.
	 */
	public void addExtensionEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm,
			final String pluginId, final String pageId) {
		List<AbstractCmClientFormSectionPart> sectionExtensions =
				PluginHelper.findSections(pluginId, pageId, this, editor);
		for (AbstractCmClientFormSectionPart sectionExtension : sectionExtensions) {
			managedForm.addPart(sectionExtension);
		}
	}

	/**
	 * Gets the custom data for the page. Used to pass extra data to extension pages.
	 * @return The map of custom page data.
	 */
	public Map<String, Object> getCustomPageData() {
		return customPageData;
	}
}
