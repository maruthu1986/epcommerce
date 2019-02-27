/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;

/**
 * Abstract base class for admin section extensions that display their items in an admin section as a list.
 */
public abstract class AbstractAdminSection implements IAdminSection {
	private static final Logger LOG = Logger.getLogger(AbstractAdminSection.class);

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Creates the initial control that will contain each item in an admin section and then delegates the creation of those items to the
	 * <code>createItems</code> method.
	 *
	 * @param toolkit the top level toolkit which contains all admin sections
	 * @param parent the parent section which is the container for this specific admin section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	public void createControl(final FormToolkit toolkit, final Section parent, final IWorkbenchPartSite site) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new TableWrapLayout());
		toolkit.adapt(composite);
		parent.setClient(composite);
		createItems(toolkit, composite, site);
	}

	/**
	 * Creates all the items for the admin section.
	 *
	 * @param toolkit the top level toolkit which contains all admin sections
	 * @param parent the parent section which is the container for this specific admin section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	public abstract void createItems(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site);

	/**
	 * Creates an image hyperlinked item in the admin section.
	 *
	 * @param toolkit the top level toolkit which contains all admin sections
	 * @param parent the parent section which is the container for this specific admin section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 * @param viewId the id of the view extension to open for the item
	 * @param text the text of the item
	 * @param image the image associated with the item
	 */
	protected void createItem(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site, final String viewId,
		final String text, final Image image) {

		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent event) {
				try {
					site.getPage().showView(viewId);
				} catch (Exception e) {
					LOG.error("Unable to create mouse listener to open view id " + viewId, e); //$NON-NLS-1$
				}
			}
		};

		final ImageHyperlink viewUsersImageHyperlink = controlFactory.formToolkitCreateImageHyperlink(toolkit, parent, SWT.NONE, image, text,
			mouseAdapter);

		viewUsersImageHyperlink.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent event) {
				if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
					try {
						site.getPage().showView(viewId);
					} catch (Exception e) {
						LOG.error("Unable to create KeyListener to open view id " + viewId, e); //$NON-NLS-1$
					}
				}
			}
		});
	}

	/**
	 * Creates an image hyperlinked item in the admin section.
	 *
	 * @param toolkit the top level toolkit which contains all admin sections
	 * @param parent the parent section which is the container for this specific admin section
	 * @param text the text of the item
	 * @param image the image associated with the item
	 * @param mouseAdapter the mouseAdapter
	 */
	protected void createItemDialog(final FormToolkit toolkit, final Composite parent,
		final String text, final Image image, final MouseAdapter mouseAdapter) {

		controlFactory.formToolkitCreateImageHyperlink(toolkit, parent, SWT.NONE, image, text, mouseAdapter);
	}
}
