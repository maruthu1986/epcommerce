/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store;

import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.settings.SettingsMessages;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsMessages;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.controller.impl.AbstractBaseControllerImpl;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareUIPlugin;
import com.elasticpath.cmclient.store.perspective.StorePerspectiveFactory;
import com.elasticpath.cmclient.store.promotions.PromotionsImageRegistry;
import com.elasticpath.cmclient.store.settings.SettingsImageRegistry;
import com.elasticpath.cmclient.store.shipping.ShippingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.controller.impl.ConditionalExpressionListControllerImpl;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.delivery.controller.impl.ContentSpacesController;
import com.elasticpath.cmclient.store.targetedselling.delivery.controller.impl.DynamicContentDeliveryControllerImpl;
import com.elasticpath.cmclient.store.targetedselling.delivery.controller.impl.DynamicContentDeliveryListController;
import com.elasticpath.cmclient.store.targetedselling.delivery.controller.impl.DynamicContentsController;
import com.elasticpath.cmclient.store.targetedselling.delivery.views.DynamicContentDeliverySearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.controller.impl.DynamicContentListControllerImpl;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchResultsView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * The activator class controls the plug-in life cycle.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class StorePlugin extends AbstractPolicyAwareUIPlugin {

	/**
	 * LOG logger.
	 */
	private static final Logger LOG = Logger.getLogger(StorePlugin.class);

	/**
	 * Boolean flag to disable content related to Dynamic Content in the UI, but keep the code around as it may be revived some day.
	 */
	public static final boolean ENABLE_DYNAMIC_CONTENT_IN_UI = false;

	/**
	 * The ID of the plug-in (package name is used).
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.store"; //$NON-NLS-1$

	private AbstractBaseControllerImpl<DynamicContent> dynamicContentListController;

	//controls list of DynamicContents on DynamicContentDelivery search tab
	private AbstractBaseControllerImpl<DynamicContent> dynamicContentsController;

	//controls list of ContentSpaces on DynamicContentDelivery search tab
	private AbstractBaseControllerImpl<ContentSpace> contentSpacesController;

	private AbstractBaseControllerImpl<DynamicContentDeliveryModelAdapter> dynamicContentDeliveryListController;

	private AbstractBaseControllerImpl<ConditionalExpression> conditionalExpressionListController;

	private AbstractBaseControllerImpl<DynamicContentDelivery> dynamicContentDeliveryController;

	// listen for the perspective activation, and will re-read ContentSpaces list
	private IPerspectiveListener perspectiveListener;

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static StorePlugin getDefault() {
		StorePlugin instance = CmSingletonUtil.getSessionInstance(StorePlugin.class);
		if (instance.contentSpacesController == null) {
			instance.contentSpacesController = new ContentSpacesController();
		}
		if (ENABLE_DYNAMIC_CONTENT_IN_UI) {
			if (instance.dynamicContentsController == null) {
				instance.dynamicContentsController = new DynamicContentsController();
			}
			if (instance.dynamicContentListController == null) {
				instance.dynamicContentListController = new DynamicContentListControllerImpl();
				instance.registerDynamicContentListListener();
			}
			if (instance.dynamicContentDeliveryListController == null) {
				instance.dynamicContentDeliveryListController = new DynamicContentDeliveryListController();
				instance.registerDynamicContentDeliveryListListener();
			}
			if (instance.conditionalExpressionListController == null) {
				instance.conditionalExpressionListController = new ConditionalExpressionListControllerImpl();
				instance.registerConditionalExpressionListListener();
			}
			if (instance.dynamicContentDeliveryController == null) {
				instance.dynamicContentDeliveryController = new DynamicContentDeliveryControllerImpl();
			}
		}

		instance.registerPerspectiveListener();
		return instance;
	}

	private void registerDynamicContentDeliveryListListener() {
		dynamicContentDeliveryListController.addListener(
				eventObject -> {
					IViewPart viewPart = StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.findView(DynamicContentDeliverySearchResultsView.VIEW_ID);
					if (viewPart == null) {
						if (eventObject.getEventType() != EventType.SEARCH) {
							return;
						}
						try {
							viewPart = StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(DynamicContentDeliverySearchResultsView.VIEW_ID);
						} catch (PartInitException e) {
							LOG.error(e.getMessage(), e);
						}

					}
					((DynamicContentDeliverySearchResultsView) viewPart).setSearchResultEvent(eventObject);
					StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(viewPart);
				}
		);
	}

	private void registerDynamicContentListListener() {
		dynamicContentListController.addListener(eventObject -> {
			IViewPart viewPart = StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(DynamicContentSearchResultsView.VIEW_ID);
					if (viewPart == null) {
						if (eventObject.getEventType() != EventType.SEARCH) {
							return;
						}
						try {
							viewPart = StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(DynamicContentSearchResultsView.VIEW_ID);
						} catch (PartInitException e) {
							LOG.error(e.getMessage(), e);
						}
					}
					((DynamicContentSearchResultsView) viewPart).setSearchResultEvent(eventObject);
					StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(viewPart);
				}
		);
	}

	private void registerConditionalExpressionListListener() {
		conditionalExpressionListController.addListener(eventObject -> {
			IViewPart viewPart = StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(ConditionalExpressionSearchResultsView.VIEW_ID);
					if (viewPart == null) {
						if (eventObject.getEventType() != EventType.SEARCH) {
							return;
						}
						try {
							viewPart = StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(ConditionalExpressionSearchResultsView.VIEW_ID);
						} catch (PartInitException e) {
							LOG.error(e.getMessage(), e);
						}
					}
					((ConditionalExpressionSearchResultsView) viewPart).setSearchResultEvent(eventObject);
					StorePlugin.this.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(viewPart);
				}
		);
	}

	private void registerPerspectiveListener() {
		this.perspectiveListener = new IPerspectiveListener() {
			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if (StorePerspectiveFactory.PERSPECTIVE_ID.equalsIgnoreCase(perspective.getId())) {
					UIEvent<ContentSpace> assignmentTargetEvent = new UIEvent<>(ServiceLocator.getService(ContextIdNames.CONTENTSPACE),
						EventType.SEARCH, false);
					StorePlugin.this.getContentSpacesController().onEvent(assignmentTargetEvent);
				}
			}

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective, final String changeId) {
				// none
			}
		};
	}

	@Override
	public void stop(final BundleContext context) throws Exception {

		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				window.removePerspectiveListener(this.perspectiveListener);
			}
		} catch (IllegalStateException e) {
			// Do nothing.
		}

		if (dynamicContentListController != null) {
			dynamicContentListController.removeAllListeners();
			dynamicContentListController.removeAllListeners();
			dynamicContentDeliveryListController.removeAllListeners();
			dynamicContentDeliveryController.removeAllListeners();
		}

		if (contentSpacesController != null) {
			contentSpacesController.removeAllListeners();
		}

		if (conditionalExpressionListController != null) {
			conditionalExpressionListController.removeAllListeners();
		}

		try {
			PromotionsImageRegistry.disposeAllImages();
			ShippingImageRegistry.disposeAllImages();
			SettingsImageRegistry.disposeAllImages();
			TargetedSellingImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}

		super.stop(context);
	}

	@Override
	public void initializeImageRegistry(final ImageRegistry registry) {
		registry.put("promotion_default_22.png", PromotionsImageRegistry.PROMOTION_CATALOG); //$NON-NLS-1$
	}

	/**
	 * Get the DynamicContentListController.
	 *
	 * @return DynamicContentListController instance.
	 */
	public AbstractBaseControllerImpl<DynamicContent> getDynamicContentListController() {
		return dynamicContentListController;
	}

	/**
	 * Get the DynamicContentsController.
	 *
	 * @return DynamicContentsController instance.
	 */
	public AbstractBaseControllerImpl<DynamicContent> getDynamicContentsController() {
		return dynamicContentsController;
	}

	/**
	 * Get the ContentSpacesController.
	 *
	 * @return ContentSpacesController instance.
	 */
	public AbstractBaseControllerImpl<ContentSpace> getContentSpacesController() {
		return contentSpacesController;
	}

	/**
	 * Get the AbstractBaseControllerImpl.
	 *
	 * @return AbstractBaseControllerImpl instance.
	 */
	public AbstractBaseControllerImpl<DynamicContentDeliveryModelAdapter> getDynamicContentDeliveryListController() {
		return dynamicContentDeliveryListController;
	}

	/**
	 * Get the ConditionalExpressionListController.
	 *
	 * @return the conditionalExpressionListController
	 */
	public AbstractBaseControllerImpl<ConditionalExpression> getConditionalExpressionListController() {
		return conditionalExpressionListController;
	}

	/**
	 * Get the DynamicContentDeliveryControllerImpl.
	 *
	 * @return the dynamicContentDeliveryController
	 */
	public AbstractBaseControllerImpl<DynamicContentDelivery> getDynamicContentDeliveryController() {
		return dynamicContentDeliveryController;
	}


	@Override
	protected String getPluginId() {
		return PLUGIN_ID;
	}

	@Override
	protected void loadLocalizedMessages() {
		PromotionsMessages.get();
		SettingsMessages.get();
		ShippingLevelsMessages.get();
		TargetedSellingMessages.get();
	}
}
