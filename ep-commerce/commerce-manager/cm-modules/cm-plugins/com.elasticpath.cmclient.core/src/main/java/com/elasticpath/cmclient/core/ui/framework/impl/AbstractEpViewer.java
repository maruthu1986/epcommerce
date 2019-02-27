/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Abstract EP Viewer implementation.
 */
public class AbstractEpViewer {

	/**
	 * Adds a drop support to a viewer.
	 * @param viewer StructuredViewer
	 */
	public void addDropSupport(final StructuredViewer viewer) {
		final int ops = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDropSupport(ops, getTransfer(), new DropAdapter(viewer));
	}

	private Transfer[] getTransfer() {
		return new Transfer[] { TextTransfer.getInstance() };
	}

	/**
	 * Drop adapter for paste events.
	 */
	private class DropAdapter extends ViewerDropAdapter {

		protected DropAdapter(final Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(final Object data) {
//			final String stringToCopy = (String) data;

			return false;
		}

		@Override
		public boolean validateDrop(final Object target, final int operation, final TransferData transferType) {
			return TextTransfer.getInstance().isSupportedType(transferType);
		}

	}
}
