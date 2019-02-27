/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;

/**
 * Class serves as cache for ConditionalExpression editors. Used to manage open Editors.
 */
public final class ConditionalExpressionEditorsCache {

	private final List<IEditorPart> openEditors = new ArrayList<>();

	private ConditionalExpressionEditorsCache() {
	}

	/**
	 * Singleton implementation.
	 *
	 * @return instance of this class.
	 */
	public static ConditionalExpressionEditorsCache getInstance() {
		return CmSingletonUtil.getSessionInstance(ConditionalExpressionEditorsCache.class);
	}

	/**
	 * Adds ConditionalExpression editor to the cache.
	 *
	 * @param editorPart - ConditionalExpression editor to be added to the cache.
	 */
	public void addToCache(final IEditorPart editorPart) {
		if (null != editorPart && editorPart.getEditorInput() instanceof GuidEditorInput) {
			openEditors.add(editorPart);
		}
	}

	/**
	 * Removes ConditionalExpression editor from the cache.
	 *
	 * @param editorPart - ConditionalExpression editor to be added to the cache.
	 */
	public void removeFromCache(final IEditorPart editorPart) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, true);
		openEditors.remove(editorPart);
	}


	/**
	 * Method closes open editor with corresponding ConditionalExpression.
	 *
	 * @param guid - guid of the editor with expression to be closed.
	 */
	public void closeConditionEditorByGuid(final String guid) {
		if (null == guid) {
			return;
		}
		for (IEditorPart editorPart : openEditors) {
			IEditorInput editorInput = editorPart.getEditorInput();
			if (null == editorInput) {
				continue;
			}
			String conditionalExpressionGuid = ((GuidEditorInput) editorInput).getGuid();
			if (guid.equals(conditionalExpressionGuid)) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, true);
			}
		}
	}

}
