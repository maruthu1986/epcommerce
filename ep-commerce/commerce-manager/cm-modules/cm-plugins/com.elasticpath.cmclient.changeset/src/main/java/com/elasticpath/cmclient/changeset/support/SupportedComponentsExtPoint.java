/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

import com.elasticpath.cmclient.changeset.ChangeSetPlugin;


/**
 * Used to retrieve and handle the change set 'supportedEditors' extension point.
 */
public class SupportedComponentsExtPoint {

	private static final Logger LOG = Logger.getLogger(SupportedComponentsExtPoint.class);
	
	private static final String EXTENSION_NAME = "supportedComponents"; //$NON-NLS-1$

	private static final String EDITOR_ID_ATTR = "editorId"; //$NON-NLS-1$

	private static final String ATTR_OBJECT_TYPE = "objectType"; //$NON-NLS-1$

	/**
	 * Editor element.
	 */
	static final String ELEMENT_EDITOR = "editor"; //$NON-NLS-1$

	/**
	 * Dialog element.
	 */
	static final String ELEMENT_DIALOG = "dialog"; //$NON-NLS-1$

	/**
	 * Wizard element.
	 */
	static final String ELEMENT_WIZARD = "wizard"; //$NON-NLS-1$
	
	/**
	 * Attribute 'class'.
	 */
	static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	
	private List<SupportedComponent> cachedSupportedEditors;

	/**
	 * Gets all the supported editors.
	 * 
	 * @return a collection of supported editors
	 */
	public Collection<SupportedComponent> getSupportedComponents() {
		if (cachedSupportedEditors != null) {
			return cachedSupportedEditors;
		}
		cachedSupportedEditors = new ArrayList<>();
		
		LOG.debug("Retrieving all plugin extension points for " + EXTENSION_NAME); //$NON-NLS-1$
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(ChangeSetPlugin.PLUGIN_ID, EXTENSION_NAME).getExtensions();
		LOG.debug("Retrieved " + extensions.length + " extensions"); //$NON-NLS-1$ //$NON-NLS-2$
		for (IExtension extension : extensions) {
			for (IConfigurationElement configElement : extension.getConfigurationElements()) {
				if (StringUtils.equals(configElement.getName(), ELEMENT_EDITOR)) {
					SupportedComponent supportedEditor = createSupportedEditor(configElement);
					if (supportedEditor != null) {
						cachedSupportedEditors.add(supportedEditor);
					}
				} else if (StringUtils.equals(configElement.getName(), ELEMENT_DIALOG)) {
					SupportedComponent supportedComponent = createSupportedDialog(configElement);
					if (supportedComponent != null) {
						cachedSupportedEditors.add(supportedComponent);
					}
				} else if (StringUtils.equals(configElement.getName(), ELEMENT_WIZARD)) {
					SupportedComponent supportedComponent = createSupportedWizard(configElement);
					if (supportedComponent != null) {
						cachedSupportedEditors.add(supportedComponent);
					}
				}
			}
		}
		return Collections.unmodifiableList(cachedSupportedEditors);

	}

	/**
	 * Creates a supported wizard component.
	 * 
	 * @param configElement the configuration element
	 * @return the supported component instance
	 */
	private SupportedComponent createSupportedWizard(final IConfigurationElement configElement) {
		String className = configElement.getAttribute(ATTR_CLASS);
		String objectType = configElement.getAttribute(ATTR_OBJECT_TYPE);
		return new WizardSupportedComponent(configElement, className, objectType);
	}

	/**
	 * Parses a configuration element to a supported editor.
	 * 
	 * @param configElement the configuration element to use
	 * @return a {@link SupportedComponent} instance
	 */
	protected SupportedComponent createSupportedEditor(final IConfigurationElement configElement) {
		String editorId = configElement.getAttribute(EDITOR_ID_ATTR);
		String objectType = configElement.getAttribute(ATTR_OBJECT_TYPE);
		return new EditorSupportedComponent(editorId, objectType);
	}

	/**
	 * Parses a configuration element to a supported dialog.
	 * 
	 * @param configElement the configuration element to use
	 * @return a {@link SupportedComponent} instance
	 */
	protected SupportedComponent createSupportedDialog(final IConfigurationElement configElement) {
		String className = configElement.getAttribute(ATTR_CLASS);
		String objectType = configElement.getAttribute(ATTR_OBJECT_TYPE);
		return new DialogSupportedComponent(configElement, className, objectType);
	}

}
