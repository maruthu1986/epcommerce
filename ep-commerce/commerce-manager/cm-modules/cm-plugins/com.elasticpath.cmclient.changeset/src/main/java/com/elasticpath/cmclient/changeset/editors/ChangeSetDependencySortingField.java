/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.editors;

import org.apache.commons.lang.enums.Enum;

import com.elasticpath.commons.pagination.SortingField;

/**
 * The change set dependency sorting field.
 */
public class ChangeSetDependencySortingField extends Enum implements SortingField {
	private static final long serialVersionUID = -3828916616704571530L;

	/**
	 * Source object name sorting field.
	 */
	public static final SortingField SOURCE_OBJECT_NAME = new ChangeSetDependencySortingField("sourceObjectName"); //$NON-NLS-1$
	
	/**
	 * Source object type sorting field.
	 */
	public static final SortingField SOURCE_OBJECT_TYPE = new ChangeSetDependencySortingField("sourceObjectType"); //$NON-NLS-1$
	
	/**
	 * Dependency object name sorting field.
	 */
	public static final SortingField DEPENDENCY_OBJECT_NAME = new ChangeSetDependencySortingField("dependencyObjectName"); //$NON-NLS-1$
	
	/**
	 * Dependency object type sorting field.
	 */
	public static final SortingField DEPENDENCY_OBJECT_TYPE = new ChangeSetDependencySortingField("dependencyObjectType"); //$NON-NLS-1$
	
	/**
	 * Change set name sorting field.
	 */
	public static final SortingField CHANGE_SET_NAME = new ChangeSetDependencySortingField("changeSetName"); //$NON-NLS-1$
	

	/**
	 *
	 * @param name the name
	 */
	protected ChangeSetDependencySortingField(final String name) {
		super(name);
	}

}
