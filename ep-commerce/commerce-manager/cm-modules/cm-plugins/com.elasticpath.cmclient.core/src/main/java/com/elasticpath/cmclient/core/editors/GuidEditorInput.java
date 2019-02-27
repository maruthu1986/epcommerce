/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.editors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This editor input generically handles input domain objects
 * that are identified by their GUID. 
 * 
 * Note that the editor input only stores the GUID of the object to be edited
 * because the workbench may keep references to many editor inputs.
 * The Editor contains the reference to the actual object
 * being edited.
 * 
 */
public class GuidEditorInput extends AbstractFormEditorInput {

	/**
	 * The GUID of the object that is the editor input represented
	 * by this class.
	 */
	private String objectGuid;
	private final Class< ? > targetClass;
	
	/**
	 * Constructs a new editor input.
	 * 
	 * @param objectGuid the object GUID to be used
	 * @param targetClass the target class
	 */
	public GuidEditorInput(final String objectGuid, final Class< ? > targetClass) {
		// the name of the editor should be defined in the editor itself
		// by overriding getEditorTitle()
		super(StringUtils.EMPTY);
		this.objectGuid = objectGuid;
		this.targetClass = targetClass;
	}
	
	/**
	 * Get the GUID of the object to be edited.
	 *
	 * @return the GUID
	 */
	public String getGuid() {
		return objectGuid;
	}
	
	/**
	 * Sets the GUID of the object to be edit.
	 * 
	 * @param guid the object GUID
	 */	
	public void setGuid(final String guid) {
		this.objectGuid = guid;
	}
		
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof GuidEditorInput)) {
			return false;
		}
		final GuidEditorInput otherGuidEditor = (GuidEditorInput) obj;
		if (!ObjectUtils.equals(otherGuidEditor.targetClass, this.targetClass)) {
			return false;
		}

		return ObjectUtils.equals(this.getGuid(), otherGuidEditor.getGuid());
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.objectGuid).append(this.targetClass).toHashCode();
	}

	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == String.class) {
			return getGuid();
		}
		return super.getAdapter(adapter);
	}
	
	
}
