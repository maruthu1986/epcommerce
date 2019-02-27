/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.helpers.AbstractOpenPerspectiveHandler;

/**
 * PerspectiveManager reads extensions and loads Handlers for commands that is the logic behind every Perspective ToolItem on the Coolbar.
 * Also it maps command keys to perspective keys.
 * It also knows the id of first perspective that appears on the Coolbar.
 */
public class PerspectiveUtil {

	/**
	 * Admin perspective id.
	 */
	public static final String ADMIN_PERSPECTIVE_ID = "com.elasticpath.cmclient.admin.perspectives.AdminPerspective"; //$NON-NLS-1$

	/**
	 * Default/Empty perspective id.
	 */
	public static final String EMPTY_PERSPECTIVE_ID = "com.elasticpath.cmclient.empty.perspective"; //$NON-NLS-1$

	/**
	 * Extension Constants.
	 */
	private static final String TAG_PERSPECTIVE_ID = "perspectiveId"; //$NON-NLS-1$
	private static final String TAG_OPEN_COMMAND_ID = "openCommandId"; //$NON-NLS-1$
	private static final String TAG_PERSPECTIVE_OPEN_COMMAND = "perspectiveOpenCommand"; //$NON-NLS-1$
	private static final String EXTENSION_POINT_NAME = "bindings"; //$NON-NLS-1$
	private static final IExtension[] BINDING_EXTENSIONS =
		Platform.getExtensionRegistry().getExtensionPoint(CorePlugin.PLUGIN_ID, EXTENSION_POINT_NAME).getExtensions();
	private static final String HANDLER = "handler"; //$NON-NLS-1$
	private static final IExtension[] HANDLERS_EXTENSIONS =
		Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.ui", "handlers").getExtensions();   //$NON-NLS-1$//$NON-NLS-2$

	private final Map<String, String> perspectivesToCommands = new LinkedHashMap<>();
	private boolean perspectivesActivated;

	/**
	 * Activates all the perspective handlers.
	 * <p>
	 * NOTE: this must be called once, otherwise some elements will be marked disabled,
	 * hence they will be invisible and will disappear from the Coolbar.
	 *
	 * @return map of perspectives to commands
	 */
	public Map<String, String> activatePerspectiveHandlers() {
		if (perspectivesActivated) {
			return perspectivesToCommands;
		}
		for (IExtension extension : BINDING_EXTENSIONS) {

			for (final IConfigurationElement configElement : extension.getConfigurationElements()) {
				if (TAG_PERSPECTIVE_OPEN_COMMAND.equals(configElement.getName())) {
					String perspectiveId = configElement.getAttribute(TAG_PERSPECTIVE_ID);
					String openCommandId = configElement.getAttribute(TAG_OPEN_COMMAND_ID);

					//Map ids to ids
					perspectivesToCommands.put(perspectiveId, openCommandId);
					activatePerspectiveHandler(perspectiveId, openCommandId);
				}
			}
		}
		perspectivesActivated = true;
		return perspectivesToCommands;
	}

	/**
	 * Activates Perspective handler.
	 *
	 * @param perspectiveId perspective id
	 * @param commandId     commandId
	 */
	public void activatePerspectiveHandler(final String perspectiveId, final String commandId) {

		final IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);


		IHandler handler = locateHandlerForCommand(commandId);

		if (handler == null) {
			handler = new AbstractOpenPerspectiveHandler() {
				@Override
				protected String getPerspectiveId() {
					return perspectiveId;
				}
			};
			handlerService.activateHandler(commandId, handler);
		}
	}

	/**
	 * Locate the correct handler for the given command id.
	 *
	 * @param commandId the command id to find the handler for
	 * @return the handler or null if none can be found
	 */
	private IHandler locateHandlerForCommand(final String commandId) {

		final ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand(commandId);
		if (command == null) {
			return null;
		}

		for (final IExtension extension : HANDLERS_EXTENSIONS) {
			for (final IConfigurationElement configElement : extension.getConfigurationElements()) {
				if (HANDLER.equals(configElement.getName())) {
					IHandler handler = getValidOpenHandler(command, configElement);
					if (handler != null) {
						return handler;
					}
				}
			}
		}
		return null;
	}


	/**
	 * Confirm if the given handler config element is associated with the given command,
	 * and if so instantiate the handler, set it on the command and return it. Return null
	 * if the given config element defines a handler that it not for the given command.
	 *
	 * @param command       the command that should be associated with the handler
	 * @param configElement the config element which defines the handler
	 * @return the handler if it is valid to be associated with the given command, null otherwise
	 */
	private IHandler getValidOpenHandler(final Command command, final IConfigurationElement configElement) {
		String handlerCommand = configElement.getAttribute("commandId"); //$NON-NLS-1$
		if (command.getId().equals(handlerCommand)) {
			try {
				IHandler handler = (IHandler) configElement.createExecutableExtension("class"); //$NON-NLS-1$
				command.setHandler(handler);
				return handler;
			} catch (CoreException exception) {
				throw new ExtensionProcessingException("Error trying to get handler class for command " + command.getId(), exception); //$NON-NLS-1$
			}
		}
		return null;
	}
}
