/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * A section that can be added to the Admin navigation view. An AdminSectionType is represented by a configElement as an extension to the Admin
 * plugin, so this class knows how to parse such an element.
 */
public class AdminSectionType {
	private static final Logger LOG = Logger.getLogger(AdminSectionType.class);

	private static final String EXTENSION_NAME = "adminSections"; //$NON-NLS-1$

	private static final String TAG_ADMIN_SECTION_TYPE = "adminSection"; //$NON-NLS-1$

	private static final String ATT_ID = "id"; //$NON-NLS-1$

	private static final String ATT_NAME = "name"; //$NON-NLS-1$

	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private static final String ATT_ORDER = "order"; //$NON-NLS-1$

	private final IConfigurationElement configElement;

	private final String sectionId;

	private final String name;

	private final Integer order;

	private IAdminSection adminSection;

	private static List<AdminSectionType> cachedTypes;

	/**
	 * Compare Section types by the name first.
	 * Then put sections, for which the order was specified, at the top.
	 * (of course in specified order)
	 */
	private static final Comparator<AdminSectionType> NULL_SAFE_INT_COMPARATOR =
		Comparator.comparing(AdminSectionType::getOrder, AdminSectionType::compareIntegers);
	private static final Comparator<AdminSectionType> SECTION_NAME_COMPARATOR =
		Comparator.comparing(AdminSectionType::getName, String::compareToIgnoreCase);

	/**
	 * Constructs an AdminSectionType from a configurationElement.
	 * 
	 * @param configElement the extension point configuration element that defines an AdminSection
	 */
	public AdminSectionType(final IConfigurationElement configElement) {
		this.configElement = configElement;
		sectionId = getAttribute(configElement, ATT_ID);
		name = getAttribute(configElement, ATT_NAME);
		order = getIntAttribute(configElement, ATT_ORDER); //Optional, can be null

		// Make sure that the class is defined, but don't load it
		getAttribute(configElement, ATT_CLASS);
	}

	/**
	 * Returns the admin section's name.
	 * 
	 * @return name the name of the admin section.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns an admin section or null if initiation of the admin section failed.
	 *
	 * @return the admin section
	 */
	public IAdminSection getAdminSection() {
		if (adminSection != null) {
			return adminSection;
		}
		try {
			adminSection = (IAdminSection) configElement.createExecutableExtension(ATT_CLASS);

		} catch (Exception e) {
			LOG.error("Failed to instantiate adminSection: " //$NON-NLS-1$
					+ configElement.getAttribute(ATT_CLASS) + " in type: " //$NON-NLS-1$
					+ sectionId + " in plugin: " //$NON-NLS-1$
					+ configElement.getDeclaringExtension().getNamespaceIdentifier(), e);
		}
		return adminSection;
	}

	private static String getAttribute(final IConfigurationElement configElement, final String name) {
		String value = configElement.getAttribute(name);
		if (value == null) {
			throw new IllegalArgumentException("Missing " + name + " attribute"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return value;
	}

	private Integer getIntAttribute(final IConfigurationElement configElement, final String attributeName) {
		String value = configElement.getAttribute(attributeName);
		if (value == null) {
			return null; //Value was not specified (it is allowed)
		}
		return Integer.parseInt(value);
	}

	/**
	 * Return all the <code>AdminSectionType</code>s that are plugged into the platform.
	 * 
	 * @return all the <code>AdminSectionType</code>s that are plugged into the platform
	 */
	public static List<AdminSectionType> getSections() {
		if (cachedTypes != null) {
			return cachedTypes;
		}
		cachedTypes = new ArrayList<>();

		LOG.debug("Retrieving all plugin extension points for " + EXTENSION_NAME); //$NON-NLS-1$
		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(AdminPlugin.PLUGIN_ID, EXTENSION_NAME).getExtensions();
		LOG.debug("Retrieved " + extensions.length + " extensions"); //$NON-NLS-1$ //$NON-NLS-2$
		for (IExtension extension : extensions) {
			for (IConfigurationElement configElement : extension.getConfigurationElements()) {
				AdminSectionType adminSectionType = parseItem(configElement);
				if (adminSectionType != null) {
					cachedTypes.add(adminSectionType);
				}
			}
		}
		Collections.sort(cachedTypes, SECTION_NAME_COMPARATOR);
		Collections.sort(cachedTypes, NULL_SAFE_INT_COMPARATOR);

		return Collections.unmodifiableList(cachedTypes);
	}

	/**
	 * Private method used for comparator.
	 * This value can be null. May not be specified in the plugin.xml
	 */
	private Integer getOrder() {
		return order;
	}

	/**
	 * Parse a configuration element representing an AdminSectionType and return an object instance.
	 * 
	 * @param configElement an element representing the AdminSectionType
	 * @return an instance of AdminSectionType
	 */
	public static AdminSectionType parseItem(final IConfigurationElement configElement) {
		if (!configElement.getName().equals(TAG_ADMIN_SECTION_TYPE)) {
			LOG.error("Unknown element: " + configElement.getName()); //$NON-NLS-1$
			return null;
		}
		try {
			LOG.debug("Creating new AdminSectionType"); //$NON-NLS-1$
			return new AdminSectionType(configElement);
		} catch (Exception e) {
			String msg = "Failed to load AdminSectionType with name " //$NON-NLS-1$
					+ configElement.getAttribute(ATT_NAME) + " in " //$NON-NLS-1$
					+ configElement.getDeclaringExtension().getNamespaceIdentifier();
			LOG.error(msg, e);
			return null;
		}
	}

	private static int compareIntegers(final Integer first, final Integer second) {
		if (first == null && second == null) {
			return 0;
		}
		if (first == null) {
			return 1; //Value not provided means (first > second)
		}
		if (second == null) {
			return -1; //(first < second)
		}
		return first.compareTo(second);
	}
}
