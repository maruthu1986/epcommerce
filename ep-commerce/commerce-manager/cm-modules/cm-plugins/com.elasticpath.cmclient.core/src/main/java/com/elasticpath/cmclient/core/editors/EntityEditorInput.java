/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.editors;

import com.elasticpath.persistence.api.Entity;


/**
 * This editor input generically handles input domain objects
 * that are identified by their UID of type <T>. 
 * 
 * Note that the editor input only stores the UID of the object to be edited
 * because the workbench may keep references to many editor inputs.
 * The Editor contains the reference to the actual object
 * being edited.
 * 
 * @param <T> The parameterized type of the unique ID of the object (String, Long, etc...)
 */
public class EntityEditorInput<T> extends AbstractFormEditorInput {

	/**
	 * The UID of the product that is the editor input represented
	 * by this class.
	 */
	private T objectUid;
	private final Class<?> targetClass;
	
	
	/**
	 * Constructor -- creates a new EditorInput for the given object.
	 *
	 * @param name The name of the editor input
	 * @param targetClass the domain class the editor is related to. 
	 * 					  Used to check if two editor inputs are not for the same object.
	 * 					  A problem arises if two objects have the same UID but are of different type.
	 * @param objectUid the UID of the object to be viewed/edited
	 */
	public EntityEditorInput(final String name, final T objectUid, final Class<?> targetClass) {
		super(name);
		this.objectUid = objectUid;
		this.targetClass = targetClass;
	}
	
	/**
	 * Constructor -- creates a new EditorInput for the given object.
	 *
	 * @param name The name of the editor input
	 * @param toolTip the tool tip text
	 * @param targetClass the domain class the editor is related to. 
	 * 					  Used to check if two editor inputs are not for the same object.
	 * 					  A problem arises if two objects have the same UID but are of different type.
	 * @param objectUid the UID of the object to be viewed/edited
	 */
	public EntityEditorInput(final String name, final String toolTip, final T objectUid, final Class< ? extends Entity> targetClass) {
		super(name);
		super.setToolTipText(toolTip);
		this.objectUid = objectUid;
		this.targetClass = targetClass;
	}
	
	/**
	 * Get the UID of the object to be edited.
	 *
	 * @return the UID
	 */
	public T getUid() {
		return objectUid;
	}
	
	/**
	 * Sets the UID of the object to be edit.
	 * 
	 * @param uid the object uid
	 */	
	public void setUid(final T uid) {
		this.objectUid = uid;
	}
		
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof EntityEditorInput<?>)) {
			return false;
		}
		final EntityEditorInput<?> otherUidEditor = (EntityEditorInput<?>) obj;
		if (!otherUidEditor.targetClass.equals(targetClass)) {
			return false;
		}
		final Object otherUid = otherUidEditor.getAdapter(objectUid.getClass());
		
		return this.objectUid.equals(otherUid);
	}
	
	@Override
	public int hashCode() {
		return this.objectUid.hashCode();
	}
	
	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == Long.class) {
			return this.objectUid;
		}
		if (adapter == String.class) {
			return this.objectUid;
		}
		return null;
	}
}