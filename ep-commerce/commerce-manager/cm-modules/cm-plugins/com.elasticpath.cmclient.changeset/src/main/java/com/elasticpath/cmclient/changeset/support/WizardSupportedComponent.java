/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.support;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.ui.IChangeSetEditorAware;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * A supported component that handles wizards.
 */
public class WizardSupportedComponent extends AbstractSupportedComponent {

	private static final Logger LOG = Logger.getLogger(WizardSupportedComponent.class);
	
	private final IConfigurationElement configElement;

	/**
	 * Constructs a new instance.
	 *
	 * @param configElement the configuration element
	 * @param componentId the component ID
	 * @param objectType the object type
	 */
	public WizardSupportedComponent(final IConfigurationElement configElement, final String componentId, final String objectType) {
		super(componentId, objectType);
		this.configElement = configElement;
	}

	/**
	 * Opens the wizard.
	 * 
	 * @param objectDescriptor the object descriptor
	 */
	@Override
	public void openComponent(final BusinessObjectDescriptor objectDescriptor) {
		Object instance = createWizardInstance();
		((ObjectGuidReceiver) instance).setObjectGuid(objectDescriptor.getObjectIdentifier());
		
		AbstractEpWizard< ? > wizard = (AbstractEpWizard< ? >) instance;
		if (instance instanceof IChangeSetEditorAware) {
			((IChangeSetEditorAware) instance).setOpenedFromChangeSetEditor();
		}
		
		EpWizardDialog wizardDialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		wizardDialog.addPageChangingListener(wizard);

		wizardDialog.open();
	}

	private Object createWizardInstance() {
		try {
			return configElement.createExecutableExtension(SupportedComponentsExtPoint.ATTR_CLASS);
		} catch (CoreException e) {
			LOG.error("Could not create a wizard instance.", e); //$NON-NLS-1$
		}
		return null;
	}

}
