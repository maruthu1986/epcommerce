/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;

/**
 * A wizard dialog for BaseAmount CSV import.
 *
 */
public class DirectCsvImportBaseAmountsWizardDialog extends EpWizardDialog {

	private final List<PageChangeListener> listeners = new LinkedList<PageChangeListener>();
	
	/**
	 * Default constructor.
	 * @param parentShell Shell object
	 * @param newWizard an implementation of IWizard interface
	 */
	public DirectCsvImportBaseAmountsWizardDialog(final Shell parentShell, final IWizard newWizard) {
		super(parentShell, newWizard);
	}

	/**
	 * Add page change listener.
	 * @param listener a listener
	 */
	public void addPageChangeListener(final PageChangeListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Remove a page chage listener.
	 * @param listener a listener
	 */
	public void removePageChangeListener(final PageChangeListener listener) {
		this.listeners.remove(listener);
	}

	private void fireNextPageChangeEvent(final IWizardPage page) {
		for (PageChangeListener listener : this.listeners) {
			listener.pageChanged(page);
		}
	}

	@Override
	protected void nextPressed() {
		this.fireNextPageChangeEvent(this.getCurrentPage());
		super.nextPressed();
	}

}
