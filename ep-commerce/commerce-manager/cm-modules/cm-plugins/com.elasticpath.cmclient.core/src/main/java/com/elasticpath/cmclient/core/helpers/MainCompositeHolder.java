/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import javassist.NotFoundException;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.CmSingletonUtil;

/**
 * Holder for the main composite which contains all of the Views and Editors.
 * This composite is the main area of the Workbench.
 * Singleton class.
 */
public final class MainCompositeHolder {

	private static final Logger LOG = Logger.getLogger(MainCompositeHolder.class);
	private Composite mainComposite;

	private MainCompositeHolder() {
		//empty
	}

	/**
	 * Returns an instance of the MainCompositeHolder which contains main composite.
	 *
	 * @return main composite holder
	 */
	public static MainCompositeHolder getInstance() {
		MainCompositeHolder sessionInstance = CmSingletonUtil.getSessionInstance(MainCompositeHolder.class);
		if (sessionInstance.mainComposite == null) {
			try {
				sessionInstance.mainComposite = sessionInstance.findMainComposite();
			} catch (NotFoundException e) {
				LOG.error(e.getMessage());
			}
		}
		return sessionInstance;
	}

	/**
	 * Returns main composite containing TabFolders and its TabItems.
	 *
	 * @return main composite with TabFolders
	 */
	public Composite getMainComposite() {
		return mainComposite;
	}


	private Composite findMainComposite() throws NotFoundException {
		for (Control control : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getChildren()) {
			if (control.getClass().equals(Composite.class)) {
				//First occurrence of the composite among shell's children will be saved in the WidgetIdUtil
				//It is ASSUMED that it is the right composite

				Composite composite = (Composite) control;
				for (Control child : composite.getChildren()) {
					if (child instanceof Composite) {
						return (Composite) child;
					}
				}
			}
		}
		throw new NotFoundException("EPWidgetIdUtil: Main Composite was not found"); //$NON-NLS-1$
	}

}
