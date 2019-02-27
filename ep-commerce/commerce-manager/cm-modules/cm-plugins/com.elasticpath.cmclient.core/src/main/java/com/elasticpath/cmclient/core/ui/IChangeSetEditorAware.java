/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui;

/**
 * A {@code Wizard} which implements this interface is informed when the Wizard is opened
 * from the {@code ChangeSetEditor}. This allows the Wizard to change behaviour if called
 * from that editor or from some other component. 
 */
public interface IChangeSetEditorAware {
	
	/**
	 * Called if the {@code Wizard} was opened from the {@code ChangeSetEditor}.
	 */
	void setOpenedFromChangeSetEditor();
}
