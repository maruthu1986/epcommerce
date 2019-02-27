package com.elasticpath.extensions.commons;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;
import com.elasticpath.commons.util.extenum.ExtensibleEnum;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the given extensible enums so that their values are added into memory and are available. This is primarily to load values defined by
 * sub-classes up front so they are available.
 */
public class EnumLoader {
	private static final Logger LOG = Logger.getLogger(EnumLoader.class);
	private List<Class<? extends AbstractExtensibleEnum<ExtensibleEnum>>> enums;

	/**
	 * Initialization method to load the enums into memory.
	 * 
	 * @throws IllegalAccessException reflection exception.
	 * @throws IllegalArgumentException reflection exception.
	 */
	public void init() throws IllegalArgumentException, IllegalAccessException {
		final List<Class<? extends AbstractExtensibleEnum<ExtensibleEnum>>> enumClasses = getEnums();

		for (Class<? extends AbstractExtensibleEnum<ExtensibleEnum>> enumClass : enumClasses) {
			final Field[] declaredFields = enumClass.getDeclaredFields();

			LOG.debug("Loading constants in class: " + enumClass.getName());
			for (Field field : declaredFields) {
				if (isPublicStaticConstant(field)) {
					LOG.debug("Loaded '" + field.getName() + "' constant; value: " + field.get(null));
				}
			}
		}
	}

	/**
	 * Returns whether the {@link java.lang.reflect.Field} given represents a public static constant or not.
	 *
	 * @param field the field to inspect.
	 * @return <code>true</code> if the {@link java.lang.reflect.Field} given represents a public static constant, <code>false</code> otherwise.
	 */
	public static boolean isPublicStaticConstant(final Field field) {
		int modifiers = field.getModifiers();
		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
	}

	// Getters and Setters

	/**
	 * Gets the list of {@link com.elasticpath.commons.util.extenum.AbstractExtensibleEnum} implementations to load.
	 *
	 * @return the list of {@link com.elasticpath.commons.util.extenum.AbstractExtensibleEnum} implementations to load.
	 */
	public List<Class<? extends AbstractExtensibleEnum<ExtensibleEnum>>> getEnums() {
		if (this.enums == null) {
			this.enums = new ArrayList<Class<? extends AbstractExtensibleEnum<ExtensibleEnum>>>();
		}
		return this.enums;
	}

	/**
	 * Sets the list of {@link com.elasticpath.commons.util.extenum.AbstractExtensibleEnum} implementations to load.
	 *
	 * @param enums the list of {@link com.elasticpath.commons.util.extenum.AbstractExtensibleEnum} implementations to load.
	 */
	public void setEnums(final List<Class<? extends AbstractExtensibleEnum<ExtensibleEnum>>> enums) {
		this.enums = enums;
	}
}
