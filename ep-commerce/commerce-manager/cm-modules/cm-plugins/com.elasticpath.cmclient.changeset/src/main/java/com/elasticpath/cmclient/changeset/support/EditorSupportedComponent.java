/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.helpers.EditorResolver;
import com.elasticpath.cmclient.changeset.helpers.impl.DefaultEditorResolver;
import com.elasticpath.cmclient.changeset.helpers.impl.PromotionsEditorResolver;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * Editor component.
 */
public class EditorSupportedComponent extends AbstractSupportedComponent implements SupportedComponent {

	private static final Logger LOG = Logger.getLogger(EditorSupportedComponent.class);
	
	private final Map<String, EditorResolver> editorResolvers = new HashMap<>();
	
	private final EditorResolver defaultEditorResolver = new DefaultEditorResolver();
	

	/**
	 *
	 * @param editorId the editor ID
	 * @param objectType the object type
	 */
	public EditorSupportedComponent(final String editorId, final String objectType) {
		super(editorId, objectType);
		editorResolvers.put("Promotion", new PromotionsEditorResolver()); //$NON-NLS-1$
	}

	/**
	 *
	 * @param objectDescriptor the object descriptor
	 */
	@Override
	public void openComponent(final BusinessObjectDescriptor objectDescriptor) {
		openEditor(objectDescriptor);
	}

	private void openEditor(final BusinessObjectDescriptor objectDescriptor) {
		String editorId = getEditorResolver(objectDescriptor).resolveEditorId(objectDescriptor);
		if (editorId == null) {
			LOG.warn("The editor ID is null and editor cannot be opened for object descriptor: " + objectDescriptor); //$NON-NLS-1$
		} else {
			
			
			try {
				Class< ? > clazz = findClassByObjectDescriptor(objectDescriptor);
				IEditorInput input = new GuidEditorInput(objectDescriptor.getObjectIdentifier(), clazz);
				IWorkbenchPage workbenchPage = ChangeSetPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				workbenchPage.openEditor(input, editorId);
			} catch (PartInitException exc) {
				LOG.error("Could not open editor for object descriptor: " + objectDescriptor, exc); //$NON-NLS-1$
			}
		}
	}

	private Class< ? > findClassByObjectDescriptor(final BusinessObjectDescriptor objectDescriptor) {
		final ChangeSetService changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
		return changeSetService.findObjectClass(objectDescriptor);
	}
	
	/**
	 * Returns the appropriate editor resolver.
	 * 
	 * @param objectDescriptor The {@link BusinessObjectDescriptor} to determine the correct editor resolver.
	 * @return The editor resolver for the given {@link BusinessObjectDescriptor}.
	 */
	protected EditorResolver getEditorResolver(final BusinessObjectDescriptor objectDescriptor) {
		EditorResolver editorResolver = editorResolvers.get(objectDescriptor.getObjectType());
		if (editorResolver == null) {
			editorResolver = defaultEditorResolver;
		}
		
		return editorResolver;
	}

}