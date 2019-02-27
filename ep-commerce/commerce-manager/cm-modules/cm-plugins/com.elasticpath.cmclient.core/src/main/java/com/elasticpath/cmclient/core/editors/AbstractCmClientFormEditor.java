/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.OptimisticLockException;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.handlers.ApplicationErrorHandler;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;

/**
 * Abstract class implementing functionality that is common to multi-page form editors in the CM Client. Editor-level authorization will be
 * implemented here.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public abstract class AbstractCmClientFormEditor extends FormEditor implements ISaveablePart2, ControlModificationListener {

	private boolean isPageModified;

	private DataBindingContext bindingContext;

	private static final Logger LOG = Logger.getLogger(AbstractCmClientFormEditor.class);

	private final List<String> formPageIdList = new ArrayList<String>();
	private final Map<String, Object> customData = new HashMap<>();

	@Override
	public final void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		this.bindingContext = new DataBindingContext();
		initEditor(site, input);
		refreshTitleAndToolTip();
	}

	@Override
	protected FormToolkit createToolkit(final Display display) {
		return EpControlFactory.getInstance().createFormToolkit();
	}

	/**
	 * Initializes the editor.
	 * @param site the editor site
	 * @param input the editor input
	 * @throws PartInitException if the editor cannot be initialized
	 */
	protected abstract void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException;

	/**
	 * Refreshes the editor's title.
	 */
	private void refreshTitleAndToolTip() {
		final IEditorInput input = getEditorInput();
		setPartName(input.getName());
		// the editor name provided by the editor will overwrite the one from the editor input
		if (getEditorName() != null) {
			setPartName(getEditorName());
		}
		if (getEditorToolTip() != null) {
			setTitleToolTip(getEditorToolTip());
		}
	}


	/**
	 * Gets the editor title tool tip.
	 *
	 * @return the tool tip of the editor title
	 */
	protected String getEditorToolTip() {
		return null;
	}

	/**
	 * Set the title image on the form editor.
	 * @param titleImage is the new image
	 */
	public void setEditorTitleImage(final Image titleImage) {
		super.setTitleImage(titleImage);
	}

	@Override
	public int addPage(final IFormPage page) throws PartInitException {
		String pageId = page.getId();
		formPageIdList.add(pageId);
		return super.addPage(page);
	}

	@Override
	protected abstract void addPages();

	/**
	 * Implement this method to save the model object. that is being edited.
	 *
	 * @param monitor the progress monitor
	 */
	protected abstract void saveModel(final IProgressMonitor monitor);

	/**
	 * Override to return the model being edited by the editor.
	 *
	 * @return the model object
	 */
	public Object getModel() {
		return null;
	}

	/**
	 * Returns model. Subclasses may override this method to return editor's dependent object.
	 *
	 * @return the model object
	 */
	public Object getDependentObject() {
		return getModel();
	}

	/***
	 * Gets the Custom Data (For Extension Pages to use).
	 * @return The custom data map.
	 */
	public Map<String, Object> getCustomData() {
		return customData;
	}

	/**
	 * Get the DataBindingContext.
	 *
	 * @return the DataBindingContext.
	 */
	public DataBindingContext getDataBindingContext() {
		return this.bindingContext;
	}

	@Override
	public boolean isDirty() {
		return this.isPageModified || super.isDirty();
	}

	/**
	 * Notifies the editor that the page has been modified. EP Domain objects don't have dirty flags and listener mechanisms, so controls must inform
	 * the editor directly if there has been a change.
	 */
	public void pageModified() {
		this.isPageModified = true;
		if (!super.isDirty()) {
			this.firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	/**
	 * Returns the first validation status that is not "OK." If all validation statuses are OK, then Status.OK_STATUS is returned. return the status
	 */
	private List<IStatus> getValidationStatus() {
		bindingContext.updateModels();
		final List<IStatus> errorStatusList = new ArrayList<IStatus>();
		for (final Iterator<IStatus> iterator = this.bindingContext.getValidationStatusMap().values().iterator(); iterator.hasNext();) {
			final IStatus currStatus = iterator.next();
			if (!currStatus.isOK()) {
				errorStatusList.add(currStatus);
			}
		}
		return errorStatusList;
	}

	@Override
	protected void handlePropertyChange(final int propertyId) {
		if (propertyId == IEditorPart.PROP_DIRTY) {
			this.isPageModified = this.isDirty();
		}
		super.handlePropertyChange(propertyId);
	}


	@Override
	public final void doSave(final IProgressMonitor monitor) {
		try {
			doSaveInternal(monitor);
		} catch (final SWTException se) {
			if (se.code == SWT.ERROR_WIDGET_DISPOSED) {
				MessageDialog.openWarning(getSite().getShell(),
						CoreMessages.get().AbstractCmClientFormEditor_ErrorTitle_save,
						CoreMessages.get().AbstractCmClientFormEditor_Error_Widget_Disposed);
				monitor.setCanceled(true);
				return;
			}
			throw se;
		} catch (final OptimisticLockException ole) {
			MessageDialog.openError(new Shell(Display.getCurrent()),
				CoreMessages.get().ConcurringEditingError,
				CoreMessages.get().ConcurringEditingErrorMessage);

			LOG.error("Error saving - concurrent modification", ole); //$NON-NLS-1$
		} catch (final Exception e) {
			ApplicationErrorHandler.createErrorDialogForException(e);

			LOG.error("Error saving", e); //$NON-NLS-1$
			monitor.setCanceled(true);
		}
	}

	private void doSaveInternal(final IProgressMonitor monitor) {
		beforeSaveAction();
		List<IStatus> validationStatus = getValidationStatus();
		if (validationStatus.isEmpty()) {
			try {
				this.commitPages(true);
			} catch (final CancelSaveException e) {
				LOG.warn(e);
				saveActionCanceled();
				return;
			}
			validationStatus = getValidationStatus();
			if (!validationStatus.isEmpty()) {
				saveActionCanceled();
				showValidationDialog(validationStatus);
				monitor.setCanceled(true);
				return;
			}

			BusyIndicator.showWhile(Display.getDefault(), () -> saveModel(monitor));

			if (!monitor.isCanceled()) {
				this.isPageModified = false;
				super.handlePropertyChange(IEditorPart.PROP_DIRTY);
			}
			refreshTitleAndToolTip();
		} else {
			saveActionCanceled();
			showValidationDialog(validationStatus);
		}
	}

	/**
	 * A hook method that is called when the save action fails due to validation problems.
	 */
	protected void saveActionCanceled() {
		// by default empty

	}

	/**
	 * A hook method that is called before each save on the editor.
	 */
	protected void beforeSaveAction() {
		// by default empty
	}

	private void showValidationDialog(final List<IStatus> validationStatus) {
		final StringBuilder errorListString = new StringBuilder();
		errorListString.append('\n');
		for (final IStatus status : validationStatus) {
			errorListString.append('\n');

			String message = status.getMessage();
			errorListString.append(message);
		}
		final String message =
			NLS.bind(getDialogMessageOnSave(),
			errorListString.toString());
		MessageDialog.openWarning(getSite().getShell(), CoreMessages.get().AbstractCmClientFormEditor_ErrorTitle_save, message);
	}

	@Override
	public int promptToSaveOnClose() {
		final List<IStatus> validationStatus = this.getValidationStatus();
		String[] buttons;
		final String message;
		final String title;
		int iconType;
		final int[] result;
		if (validationStatus.isEmpty()) {
			buttons = new String[]{
					JFaceResources.getString(IDialogLabelKeys.YES_LABEL_KEY),
					JFaceResources.getString(IDialogLabelKeys.NO_LABEL_KEY),
					JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };
			message = getSaveOnCloseMessage();
			title = CoreMessages.get().AbstractCmClientFormEditor_OkTitle_save;
			iconType = MessageDialog.QUESTION;
			result = new int[] { YES, NO, CANCEL };
		} else {
			buttons = new String[] {
					JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
					JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };
			final StringBuilder errorListString = new StringBuilder();
			errorListString.append('\n');
			for (final IStatus status : validationStatus) {
				errorListString.append('\n');
				errorListString.append(status.getMessage());
			}
			errorListString.append("\n\n");
			errorListString.append(CoreMessages.get().AbstractCmClientFormEditor_Error_Save_Description);
			title = CoreMessages.get().AbstractCmClientFormEditor_ErrorTitle_save;
			message =
				NLS.bind(CoreMessages.get().AbstractCmClientFormEditor_Error_save,
				errorListString.toString());
			iconType = MessageDialog.WARNING;
			result = new int [] { CANCEL, NO };
		}

		// Show the dialog.
			final MessageDialog confirmationDialog = new MessageDialog(
				getSite().getShell(), title,
				null, message, iconType, buttons, 0);
		final int choice = confirmationDialog.open();
		if (choice < 0) {
			return CANCEL;
		}
		return result[choice];
	}

	/**
	 * Return message to show on Save On Close dialog.
	 *
	 * @return String message
	 */
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(CoreMessages.get().AbstractCmClientFormEditor_OK_save,
			getEditorName());
	}
	
	/**
	 * Gets the editor name for use it in on close message dialog.
	 * 
	 * @return the editor name
	 */
	protected abstract String getEditorName();

	@Override
	public void doSaveAs() {
		// Save as is not supported
	}

	/**
	 * Indicates if a save is allowed.
	 * 
	 * @return true if a save is allowed
	 */
	public boolean isSaveAllowed() {
		return this.isDirty();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Notifies the implementor that a control has been modified.
	 */
	public void controlModified() {
		this.pageModified();
	}

	/**
	 * Refreshes the editor by disposing the controls of all pages and sets them to null for garbage collection.
	 * The active form page control is recreated with new composite.
	 * 
	 * Data binding context gets disposed as well and a new one is created and set.
	 */
	public void refreshEditorPages() {
		getDataBindingContext().dispose();
		bindingContext = new DataBindingContext();
		
		for (int pageIndex = 0; pageIndex < getPageCount(); pageIndex++) {
			refreshPage(pageIndex);
		}
	}
	
	/**
	 * Refresh any pages with the given Id.
	 * 
	 * @param pageId the Id of the page to refresh
	 */
	public void refreshPage(final String pageId) {
		IFormPage page = this.findPage(pageId);
		if (page != null) {
			refreshPage(page.getIndex());
		}
	}
	
	/**
	 * Perform a null-check on the pages field before delegating to the super-class
	 * implementation.  This prevents NPE's during page reloading/refreshing.
	 * 
	 * @param pageId the id of the page to find
	 * @return the page if found, null if not
	 */
	@Override
	public IFormPage findPage(final String pageId) {
		return pages == null ? null : super.findPage(pageId);
	};
	
	/**
	 * Reload a page.
	 *
	 * @param pageId the Id of the page to reload
	 */
	public void reloadPage(final String pageId) {
		IFormPage page = findPage(pageId);
		if (page != null) {
			refreshPage(page.getIndex());
		}
	}

	/**
	 * Refresh the page with the given index.
	 * 
	 * @param pageIndex the index of the page to refresh
	 */
	public void refreshPage(final int pageIndex) {
		
		// Dispose and set to null the pageControl, this will force recreation
		// of that page once the page is activated again.
		final Control pageControl = getControl(pageIndex);
		if (pageControl != null) {
			pageControl.dispose();
		}
		setControl(pageIndex, null);

		// If we just refreshed the active page, set it active again so RCP redraws it
		if (isActivePage(pageIndex)) {
			setActivePage(pageIndex);
		}
	}
	
	private boolean isActivePage(final int pageIndex) {
		return pageIndex == getActivePageInstance().getIndex();
	}

	/**
	 * Refresh all data context bindings.
	 */
	public void refreshAllDataBindings() {
		getDataBindingContext().dispose();
		bindingContext = new DataBindingContext();
		
		for (Object page : pages) {
			if (page instanceof AbstractCmClientEditorPage) {
				AbstractCmClientEditorPage epPage = (AbstractCmClientEditorPage) page;
				// Only refresh the active page's bindings - any others will be refreshed when they
				// are made visible next time when RCP recreates the visual components.
				if (epPage.isActive()) {
					epPage.refreshDataBindings(bindingContext);
				}
			}
		}
	}

	
	/**
	 * Return an editor to its unchanged persistent state. For use if you need to 
	 * get rid of any changes made to an open editor.
	 */
	public void reload() {
		reloadModel();
		reloadViews();
		refreshEditorPages();
		clearModifiedStatus();
		refreshTitleAndToolTip();
	}
	
	/**
	 * Mark the view to reload their model. For now, only the SkuOption need this method. 
	 */
	protected void reloadViews() {
		for (String pageId : formPageIdList) {
			IFormPage page = this.findPage(pageId);
			if (page != null) {
				TableReloadMarker adapter = (TableReloadMarker) page.getAdapter(TableReloadMarker.class);
				if (adapter != null) {
					adapter.markForReload();
				}
			}
		}
	}

	/**
	 * Clears the isModified status.
	 */
	protected void clearModifiedStatus() {
		isPageModified = false;
		super.handlePropertyChange(IEditorPart.PROP_DIRTY);
		
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (getDataBindingContext() != null) {
			getDataBindingContext().dispose();
		}
	}

	/**
	 * Has to do the necessary to refresh the object model from the data source.
	 * 
	 */
	public abstract void reloadModel();

	/**
	 * Returns a collection of locales supported by this editor.
	 * 
	 * @return the supported locales by this editor
	 */
	public abstract Collection<Locale> getSupportedLocales();
	
	/**
	 * Returns the default locale for this editor. 
	 * 
	 * @return default locale.
	 */
	public abstract Locale getDefaultLocale();
	

	/**
	 * @return the message that will be displayed in the validation error dialog when there are validation errors on save. 
	 * Can be overridden by concrete editors to provide entity-specific message. 
	 */
	protected String getDialogMessageOnSave() {
		return CoreMessages.get().AbstractCmClientFormEditor_Error_save;
	}

	/**
	 *  Adds extension pages to the editor.
	 * @param editorName The editor name to lookup the extension pages for.
	 * @param pluginId The plugin to look up the extension pages for.
	 */
	protected void addExtensionPages(final String editorName, final String pluginId) {
		PluginHelper.findPages(editorName, pluginId, this).forEach(page -> {
			try {
				this.addPage(page);
			} catch (final PartInitException e) {
				//ignore extension exceptions?
			}
		});
	}
}
