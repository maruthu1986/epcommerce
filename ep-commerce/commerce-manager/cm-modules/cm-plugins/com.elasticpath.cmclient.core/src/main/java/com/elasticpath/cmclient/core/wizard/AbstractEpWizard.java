/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.core.wizard;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.wizard.page.IBeforeFinishNotifier;

/**
 * Abstract wizard for AbstractEpWizardPages's. Implements subscription on page changing event, automatic page title setting (according to blank,
 * which should contain {0} and {1} substrings), sets dialog icon and title, allows descendants to disable wizard buttons.
 *
 * @param <T> the class this wizard is designed for
 */
public abstract class AbstractEpWizard<T> extends Wizard implements IPageChangingListener {

	private static final Logger LOG = Logger.getLogger(AbstractEpWizard.class);

	private Image wizardImage;

	private String pagesTitleBlank;

	private EpWizardDialog wizardDialog;

	/**
	 * Creates wizard with specified title, icon and fills pages titles.
	 *
	 * @param windowTitle dialog title.
	 * @param pagesTitleBlank blank to make pages titles.
	 * @param wizardImage wizard dialog icon.
	 */
	public AbstractEpWizard(final String windowTitle, final String pagesTitleBlank, final Image wizardImage) {
		super();
		this.setWindowTitle(windowTitle);
		if (pagesTitleBlank == null) {
			this.pagesTitleBlank = CoreMessages.EMPTY_STRING;
		} else {
			this.pagesTitleBlank = pagesTitleBlank;
		}
		this.wizardImage = wizardImage;
	}

	private boolean canFinishNotifiersCheck() {
		IWizardPage[] pages = this.getPages();
		if (pages == null || pages.length == 0) {
			return true;
		}

		for (IWizardPage page : pages) {

			if (page instanceof IBeforeFinishNotifier) {
				IBeforeFinishNotifier notifier = (IBeforeFinishNotifier) page;
				final boolean enable = notifier.enableFinish();
				if (!enable) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean performFinish() {
		LOG.debug("perform finish."); //$NON-NLS-1$
		return canFinishNotifiersCheck();
	}

	@Override
	public void createPageControls(final Composite pageContainer) {

		if (!CoreMessages.EMPTY_STRING.equals(pagesTitleBlank)) {
			final IWizardPage[] pages = getPages();
			for (int index = 0; index < pages.length; index++) {
				pages[index].setTitle(
					NLS.bind(pagesTitleBlank,
					new Integer[]{index + 1, pages.length}));
			}
		}
		super.createPageControls(pageContainer);
	}

	@Override
	public void handlePageChanging(final PageChangingEvent event) {

		LOG.debug("page changing event."); //$NON-NLS-1$
		
		// AbstractEpWizard can hold any page, not only AbstractEPWizardPage descendants
		if (event.getCurrentPage() instanceof AbstractEPWizardPage) {
			final AbstractEPWizardPage<?> currentPage = (AbstractEPWizardPage<?>) event.getCurrentPage();
			if (event.getTargetPage() == currentPage.getNextPage()) {
				event.doit = currentPage.beforeNext(event);
			} else {
				event.doit = currentPage.beforePrev(event);
			}
		}

		// if current page terminated page changing OR target page is not an AbstractEPWizardPage descendant
		if ((!event.doit) || !(event.getTargetPage() instanceof AbstractEPWizardPage)) {
			return;
		}

		final AbstractEPWizardPage<?> targetPage = (AbstractEPWizardPage<?>) event.getTargetPage();
		if (targetPage == getNextPage((IWizardPage) event.getCurrentPage())) {
			event.doit &= targetPage.beforeFromPrev(event);
		} else {
			event.doit &= targetPage.beforeFromNext(event);
		}

	}

	/**
	 * Derivatives can override this method to enable/disable dialog buttons. Buttons can be obtained via dialog(IDialogConstants.BUTTON_ID).
	 * 
	 * @param dialog parent EpWizardDialog.
	 */
	public void onUpdateButtons(final EpWizardDialog dialog) {
		// do nothing
		LOG.debug("update buttons."); //$NON-NLS-1$
	}

	/**
	 * Set pages title blank.
	 * 
	 * @param pagesTitleBlank the title blank
	 */
	public void setPagesTitleBlank(final String pagesTitleBlank) {
		this.pagesTitleBlank = pagesTitleBlank;
	}

	/**
	 * Return the image of wizard.
	 * 
	 * @return <code>Image</code> of wizard
	 */
	protected Image getWizardImage() {
		return wizardImage;
	}
	
	/**
	 * Set the image of wizard.
	 * @param wizardImage image to set.
	 */
	protected void setWizardImage(final Image wizardImage) {
		this.wizardImage = wizardImage;
	}
	

	/**
	 * Returns the model object.
	 * 
	 * @return T the model object
	 */
	protected abstract T getModel();

	/**
	 * get wizard dialog if it has been set 
	 * (the setting is done automatically in case of creation of <code>EpWizardDialog</code>).
	 * @return wizard dialog
	 */
	public EpWizardDialog getWizardDialog() {
		return wizardDialog;
	}

	/**
	 * set wizard dialog if it has been set 
	 * (the setting is done automatically in case of creation of <code>EpWizardDialog</code>).
	 * @param wizardDialog the wizard dialog
	 */
	public void setWizardDialog(final EpWizardDialog wizardDialog) {
		this.wizardDialog = wizardDialog;
	}

}
