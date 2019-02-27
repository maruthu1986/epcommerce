/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.store.promotions.editors.CatalogPromotionsEditor;
import com.elasticpath.cmclient.store.promotions.editors.ShoppingCartPromotionsEditor;
import com.elasticpath.domain.rules.Rule;

/**
 * An action used to open the promotions details editor.
 */
public class OpenPromotionsEditorAction extends Action implements IDoubleClickListener {

	private static final Logger LOG = Logger.getLogger(OpenPromotionsEditorAction.class);

	private final TableViewer viewer;

	private final IWorkbenchPartSite workbenchPartSite;

	/**
	 * Constructs new <code>Action</code>.
	 * 
	 * @param viewer the table viewer
	 * @param workbenchPartSite the workbench site from the view
	 */
	public OpenPromotionsEditorAction(final TableViewer viewer, final IWorkbenchPartSite workbenchPartSite) {
		this.viewer = viewer;
		this.workbenchPartSite = workbenchPartSite;
	}

	@Override
	public void run() {
		final ISelection selection = this.viewer.getSelection();
		final Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof Rule) {
			final Rule rule = (Rule) obj;
			//final IEditorInput editorInput = new PromotionEditorInput(rule);
			final IEditorInput editorInput = new GuidEditorInput(rule.getGuid(), Rule.class);

			try {
				if (rule.getCatalog() == null) {
					workbenchPartSite.getPage().openEditor(editorInput, ShoppingCartPromotionsEditor.ID_EDITOR);
				} else {
					workbenchPartSite.getPage().openEditor(editorInput, CatalogPromotionsEditor.ID_EDITOR);
				}
			} catch (final PartInitException e) {
				LOG.error("Can not open promotions editor", e); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		this.run();
	}
}
