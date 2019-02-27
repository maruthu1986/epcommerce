/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;
import com.elasticpath.cmclient.store.AbstractEPCampaignWizard;

/**
 * Abstract class for create action for targeted selling wizards.
 * 
 * @param <T> the model object
 * @param <V> the domain object
 */
public abstract class AbstractCreateHandler<T, V> extends AbstractPolicyAwareHandler {

	private static final int DEFAULT_WIDTH = 700;

	private AbstractEPCampaignWizard<T> wizard;
	private final CreateHandlerService<V> service;

	/**
	 * Default constructor for CreateDynamicContentHandler class.
	 */
	public AbstractCreateHandler() {
		super();
		this.service = getService();
	}

	@Override
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		wizard = createWizardInstace();
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setPageSize(getDefaultWidth(), getDefaultHeight());
		dialog.addPageChangingListener(wizard);
		dialog.open();

		return wizard.getModel();
	}
	
	/**
	 * Get the default width for dialog.
	 * @return default width
	 */
	public int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}
	
	/**
	 * Get the default height for dialog.
	 * @return default height
	 */
	public int getDefaultHeight() {
		return SWT.DEFAULT;
	}
	

	@Override
	public abstract boolean isEnabled();

	/**
	 * Returns service for this handler.
	 * 
	 * @return - service
	 */
	protected abstract CreateHandlerService<V> getService();
	
	/**
	 * extract the domain object to save from the model object that 
	 * wizard operates with.
	 * @param model the model object that is being modified in wizard 
	 * @return Domain object from the model.
	 */
	protected abstract V getDomainObjectFromModel(T model);

	/**
	 * Factory method for creating a wizard.
	 * 
	 * @return concrete wizard
	 */
	protected abstract AbstractEPCampaignWizard<T> createWizardInstace();
	
	/**
	 * @return current wizard instance.
	 */
	protected AbstractEPCampaignWizard<T> getWizard() {
		return this.wizard;
	}

	/**
	 * Returns message thrown on already name exists exception.
	 * 
	 * @return - message.
	 */
	protected abstract String getNameExistsMessage();

	/**
	 * Performs save action on dialog.
	 *
	 * @param model - model to be saved 
	 */
	protected void save(final V model) {
		this.service.persist(model);
	}
}
