/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.helpers;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.changeset.helpers.EditorEventObserver.IEditorCondition;
import com.elasticpath.cmclient.changeset.support.SupportedComponent;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * This condition verifies against the list of supported editors.
 */
public class SupportedEditorCondition implements IEditorCondition {

	private final Collection<SupportedComponent> supportedEditors;

	/**
	 *
	 * @param supportedEditors the supported editors
	 */
	public SupportedEditorCondition(final Collection<SupportedComponent> supportedEditors) {
		this.supportedEditors = supportedEditors;
	}

	/**
	 * Verifies whether an editor id is supported by this implementation.
	 * 
	 * @param editor the editor
	 * @return true if the editor is supported
	 */
	@Override
	public boolean isConditionFulfilled(final AbstractCmClientFormEditor editor) {
		String editorId = editor.getEditorSite().getId();
		for (SupportedComponent supportedEditor : supportedEditors) {
			if (StringUtils.equals(supportedEditor.getComponentId(), editorId)) {
				return true;
			}
		}
		return false;
	}

}
