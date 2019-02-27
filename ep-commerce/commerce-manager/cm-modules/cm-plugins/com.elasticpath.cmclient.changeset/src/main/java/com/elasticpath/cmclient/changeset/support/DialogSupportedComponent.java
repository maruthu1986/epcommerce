/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.support;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Implementation of a supported component for dialogs.
 */
public class DialogSupportedComponent extends AbstractSupportedComponent {

	private static final Logger LOG = Logger.getLogger(DialogSupportedComponent.class);
	
	private final IConfigurationElement configElement;

	/**
	 * Constructor.
	 * 
	 * @param configElement the configuration element
	 * @param className the dialog class name
	 * @param objectType the object type
	 */
	public DialogSupportedComponent(final IConfigurationElement configElement, final String className, final String objectType) {
		super(className, objectType);
		this.configElement = configElement;
	}

	/**
	 *
	 * @param objectDescriptor the object descriptor
	 */
	@Override
	public void openComponent(final BusinessObjectDescriptor objectDescriptor) {
		Object instance = createDialogInstance();
		((ObjectGuidReceiver) instance).setObjectGuid(objectDescriptor.getObjectIdentifier());
		
		AbstractEpDialog dialog = (AbstractEpDialog) instance;
		
		dialog.open();
	}

	private Object createDialogInstance() {
		try {
			return configElement.createExecutableExtension(SupportedComponentsExtPoint.ATTR_CLASS);
		} catch (CoreException e) {
			LOG.error("Could not create a dialog instance.", e); //$NON-NLS-1$
		}
		return null;
	}

}
