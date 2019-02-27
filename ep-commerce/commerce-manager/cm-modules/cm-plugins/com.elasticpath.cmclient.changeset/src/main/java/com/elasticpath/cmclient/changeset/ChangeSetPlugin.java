/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.changeset;

import org.osgi.framework.BundleContext;

import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetPermissionsHelperImpl;
import com.elasticpath.cmclient.changeset.perspective.ChangeSetPerspectiveFactory;
import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.actions.HideActionSetRunnable;
import com.elasticpath.cmclient.core.actions.HideActionSetRunnable.HideCondition;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareUIPlugin;
import com.elasticpath.domain.changeset.ChangeSet;

/**
 * The activator class controls the plug-in life cycle.
 */
public class ChangeSetPlugin extends AbstractPolicyAwareUIPlugin {

	private static final String ACTION_SET_ID = "com.elasticpath.cmclient.changeSet.workbenchActionSet"; //$NON-NLS-1$

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "com.elasticpath.cmclient.changeset"; //$NON-NLS-1$

	/**
	 * Object Registry key for the active Change Set.
	 */
	private static final String OBJECT_REG_ACTIVE_CHANGE_SET = ChangeSetHelper.OBJECT_REG_ACTIVE_CHANGE_SET;

	/**
	 * If change set is disabled or person is not authorised then the user will not see the ChangeSet toolbar.
	 */
	private final HideCondition changeSetHideCondition = createChangeSetHideCondition();

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);

		//If change set is disabled or then remove change set toolbar from the CoolBar
		CorePlugin.registerPostStartupCallback(new HideActionSetRunnable(changeSetHideCondition, ACTION_SET_ID));
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			ChangeSetImageRegistry.disposeAllImages();
		} catch (ExceptionInInitializerError | IllegalStateException e) {
			// Do nothing.
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 *
	 * @return the shared instance
	 */
	public static ChangeSetPlugin getDefault() {
		return CmSingletonUtil.getSessionInstance(ChangeSetPlugin.class);
	}

	/**
	 * Gets the currently active change set.
	 *
	 * @return the {@link ChangeSet} instance or null if none was set
	 */
	public ChangeSet getActiveChangeSet() {
		return (ChangeSet) ObjectRegistry.getInstance().getObject(OBJECT_REG_ACTIVE_CHANGE_SET);
	}

	/**
	 * Sets the currently active change set.
	 *
	 * @param changeSet the change set
	 */
	public void setActiveChangeSet(final ChangeSet changeSet) {
		if (changeSet == null) {
			ObjectRegistry.getInstance().removeObject(OBJECT_REG_ACTIVE_CHANGE_SET);
		} else {
			ObjectRegistry.getInstance().putObject(OBJECT_REG_ACTIVE_CHANGE_SET, changeSet);
		}
	}

	@Override
	protected String getPluginId() {
		return PLUGIN_ID;
	}

	private HideCondition createChangeSetHideCondition() {
		return () -> {
			boolean changeSetIsDisabled = !ChangeSetPermissionsHelperImpl.getDefault().isChangeSetFeatureEnabled();
			boolean isNotAuthorised = !AuthorizationService.getInstance().isAuthorizedToAccessPerspective(ChangeSetPerspectiveFactory.PERSPECTIVE_ID);

			return changeSetIsDisabled || isNotAuthorised;
		};
	}

	@Override
	protected void loadLocalizedMessages() {
		ChangeSetMessages.get();
	}
}
