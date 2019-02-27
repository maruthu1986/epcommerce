/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.Entity;

/**
 * Utility class for handling editor related actions.
 */
public class EditorUtil {

	private static final Logger LOG = Logger.getLogger(EditorUtil.class);

	/**
	 * Close editor by given editor guid and id.
	 * @param guid editor input guid
	 * @param editorId editor id
	 */
	public void closeEditor(final String guid, final String editorId) {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();		
		final IEditorPart editorPart = getEditorPart(page, guid, editorId);
		if (editorPart != null) {
			page.closeEditor(editorPart, false);
		}
	}
	
	/**
	 * Get editor part by given editor guid, id and active page.
	 * @param page active page
	 * @param guid editor input guid
	 * @param editorId editor id
	 * @return instance of editor part if it found, otherwise null
	 */
	protected IEditorPart getEditorPart(final IWorkbenchPage page, final String guid, final String editorId) {
		final IEditorReference[] editorReferences = page.getEditorReferences();
		for (int i = 0; i < editorReferences.length; i++) {
			try {
				if (editorReferences[i].getEditorInput() instanceof GuidEditorInput) {
					GuidEditorInput editorInput = (GuidEditorInput) editorReferences[i].getEditorInput();
					if (editorReferences[i].getId().equals(editorId) && guid.equals(editorInput.getGuid())) {
						return editorReferences[i].getEditor(false);
					}						
				}
			} catch (PartInitException e) {
				LOG.debug("Cant get editor part with id/guid " + guid + '/' + editorId, e); //$NON-NLS-1$
			}
		}
		return null;
	}
	
	/**
	 * Checks whether an editor reference holds an editor with the given editor ID.
	 * 
	 * @param editorRef the editor reference
	 * @param editorId the editor ID
	 * @return true if editor reference holds the same editor
	 */
	public static boolean isSameEditor(final IEditorReference editorRef, final String editorId) {
		final String refEditorId = editorRef.getEditor(false).getEditorSite().getId();
		return ObjectUtils.equals(refEditorId, editorId);
	}	

	/**
	 * Checks whether the entity referenced by the editor is the same as the one provided.
	 * 
	 * @param entity the entity to use
	 * @param editorRef the editor reference
	 * @return true if entity is used
	 * @throws PartInitException on error
	 */
	public static boolean isSameEntity(final Entity entity, final IEditorReference editorRef) throws PartInitException {
		return isSameEntity(entity.getGuid(), editorRef);
	}	
	
	/**
	 * Checks whether the object referenced by the editor is the same as the one provided.
	 * 
	 * @param guid identifying object referenced by editor
	 * @param editorRef the editor reference
	 * @return true if guid is used
	 * @throws PartInitException on error
	 */
	public static boolean isSameEntity(final String guid, final IEditorReference editorRef) throws PartInitException {
		return ObjectUtils.equals(editorRef.getEditorInput().getAdapter(BusinessObjectDescriptor.class), guid);
	}	
	
	/**
	 * Finds all the open editors that work on objects with the given GUID.
	 * 
	 * @param objectGuid the object GUID 
	 * @return the editor references for the editors found
	 */
	public static IEditorReference[] findOpenEditorsByObjectGuid(final String objectGuid) {
		List<IEditorReference> editors = new ArrayList<IEditorReference>();
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {
			try {
				if (isSameEntity(objectGuid, editorRef)) {
					editors.add(editorRef);
				}
			} catch (PartInitException exc) {
				LOG.debug("Could not verify the editor's identity.", exc); //$NON-NLS-1$
			}
		}
		return editors.toArray(new IEditorReference[editors.size()]);
	}
}
