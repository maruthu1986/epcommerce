/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.policy.ui;

import com.elasticpath.cmclient.core.ui.TableSelectionProvider;

/**
 * Interface to expose the editor table selection provider.
 */
public interface EditorTableSelectionProvider {

	/**
	 *	Gets the TableSelectionProvider.
	 * @return the editorTableSelectionProvider
	 */
	TableSelectionProvider getEditorTableSelectionProvider();

}