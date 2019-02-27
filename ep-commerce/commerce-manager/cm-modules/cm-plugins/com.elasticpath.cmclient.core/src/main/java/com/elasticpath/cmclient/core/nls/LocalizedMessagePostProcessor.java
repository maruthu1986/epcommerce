/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.nls;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;

import com.elasticpath.cmclient.core.helpers.TestIdMapManager;

/**
 * Localize message post-processor. For Processing and returning NLS localized messages.
 */
public final class LocalizedMessagePostProcessor {
	private static final Logger LOG = Logger.getLogger(LocalizedMessagePostProcessor.class);

	private static Set<Class> convertedClasses = new HashSet<>();

	//private constructor.
	private LocalizedMessagePostProcessor() {
		// no-op
	}

	/**
	 * Gets the UTF8 Encoded messages.
	 *
	 * @param bundleName the bundle name.
	 * @param clazz      the class.
	 * @param <T>        the generic class type.
	 * @return the NLS encoded message class.
	 */
	public static <T> T getUTF8Encoded(final String bundleName, final Class<T> clazz) {
		T messages = RWT.NLS.getUTF8Encoded(bundleName, clazz);
		return process(messages);
	}


	private static <T> T process(final T localizedClass) {
		if (UITestUtil.isEnabled()) {
			Class clazz = localizedClass.getClass();

			synchronized (convertedClasses) {
				if (convertedClasses.contains(clazz)) {
					return localizedClass;
				}
				convertedClasses.add(clazz);
			}
			return postProcessConvertIdsToTestIds(localizedClass, clazz);
		} else {
			return localizedClass;
		}
	}

	private static <T> T postProcessConvertIdsToTestIds(final T localizedClass, final Class clazz) {
		LOG.debug("Post Processing NLS Messages for: " + clazz.getSimpleName());
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {

			String fieldName = field.getName();
			try {
				int modifiers = field.getModifiers();
				if (Modifier.isFinal(modifiers)) {
					continue;
				}
				if (String.class.isAssignableFrom(field.getType())
						&& Modifier.isPublic(modifiers)) {
					try {
						String value = (String) field.get(localizedClass);
						if (value != null) {
							field.setAccessible(true);
							TestIdMapManager.setFieldValueBasedTestMode(clazz, localizedClass, field, fieldName, value);
						}
					} catch (MissingResourceException missingResourceException) {
						field.setAccessible(true);
						field.set(localizedClass, "");
					}
				}
			} catch (Exception exception) {
				String qualifiedName = clazz.getName() + "#" + fieldName;
				String message = "Failed to load localized message for: " + qualifiedName;
				LOG.warn(message);
			}

		}
		return localizedClass;
	}

}
