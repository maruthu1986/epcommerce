/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventListener;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractCatalogEventListener;
import com.elasticpath.cmclient.core.helpers.ChangeEventListener;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.service.changeset.ChangeSetLoadTuner;
import com.elasticpath.service.changeset.ChangeSetManagementService;

/**
 * The editor for change sets.
 */
public class ChangeSetEditor extends AbstractPolicyAwareFormEditor implements ChangeSetEventListener {
	
	private static final Logger LOG = Logger.getLogger(ChangeSetEditor.class);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	/**
	 * The editor ID.
	 */
	public static final String ID_EDITOR = ChangeSetEditor.class.getName();
	
	private ChangeSetManagementService changeSetManagementService;
	private ChangeSet changeSet;
	
	private ChangeSetEventListenerImpl listener;

	/**
	 * Constructor.
	 */
	public ChangeSetEditor() {
		super();
	}

	@Override
	protected void addPages() {
		try {
			PolicyActionContainer container = addPolicyActionContainer("changeSetEditorContainer"); //$NON-NLS-1$;
			
			addPage(new ChangeSetEditorSummaryPage(this), container);
			addPage(new ChangeSetEditorObjectsPage(this), container);
			addPage(new ChangeSetEditorConflictsPage(this), container);
			addPage(new ChangeSetEditorUsersPage(this), container);
			addExtensionPages(getClass().getSimpleName(), ChangeSetPlugin.PLUGIN_ID);
		} catch (PartInitException e) {
			LOG.error("Cannot load pages of the ChangeSetEditor", e); //$NON-NLS-1$
			throw new EpUiException(e);
		}
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	@Override
	public void reloadModel() {
		setChangeSet(changeSetManagementService.get(getModel().getGuid(), getLoadTuner()));
	}

	/**
	 * Gets a change set load tuner.
	 * @return the load tuner instance
	 */
	private ChangeSetLoadTuner getLoadTuner() {
		ChangeSetLoadTuner noMembersLoadTuner = ServiceLocator.getService(ContextIdNames.CHANGESET_LOAD_TUNER);
		noMembersLoadTuner.setLoadingMemberObjects(false);
		noMembersLoadTuner.setLoadingMemberObjectsMetadata(false);
		
		return noMembersLoadTuner;
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		updateChangeSet(getModel());
	}

	/**
	 * Updates the change set.
	 * 
	 * @param changeSet the change set to update
	 */
	private void updateChangeSet(final ChangeSet changeSet) {
		// update
		setChangeSet(changeSetManagementService.update(changeSet, getLoadTuner()));
		// fire an event for the others to update
		ChangeSetEventService.getInstance().fireChangeSetModificationEvent(
				new ItemChangeEvent<ChangeSet>(this, getModel(), EventType.CHANGE));
		refreshEditorPages();
		
		ChangeSet activeChangeSet = ChangeSetPlugin.getDefault().getActiveChangeSet();
		if (this.changeSet != null && this.changeSet.equals(activeChangeSet)) {
			ChangeSetPlugin.getDefault().setActiveChangeSet(this.changeSet);
		}
	}

	/**
	 * Sets the current change set.
	 * 
	 * @param changeSet the change set
	 */
	protected void setChangeSet(final ChangeSet changeSet) {
		this.changeSet = changeSet;
	}

	@Override
	public ChangeSet getModel() {
		if (this.changeSet == null) {
			setChangeSet(changeSetManagementService.get(getChangeSetGuid(), getLoadTuner()));
		}
		return changeSet;
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		this.changeSetManagementService = ServiceLocator.getService(ContextIdNames.CHANGESET_MANAGEMENT_SERVICE);
		ChangeSetEventService.getInstance().registerChangeSetEventListener(this);
		this.listener = new ChangeSetEventListenerImpl();
		com.elasticpath.cmclient.core.service.ChangeSetEventService.getInstance().addChangeEventListener(listener);
	}

	/**
	 * Gets the change set GUID.
	 * 
	 * @return the change set GUID
	 */
	protected String getChangeSetGuid() {
		return (String) getEditorInput().getAdapter(String.class);
	}

	@Override
	public String getEditorName() {
		return getChangeSetName();
	}

	@Override
	public String getEditorToolTip() {
		return getChangeSetName();
	}
	
	private String getChangeSetName() {
		if (getModel() == null || StringUtils.isEmpty(getModel().getName())) {
			return ChangeSetMessages.get().ChangeSetEditor_NewChangeSet;
		}
		return getModel().getName();
	}
	
    /**
	 * Identify this editor as a policy target.
	 * 
	 * @return an identifier string
	 */
	public String getTargetIdentifier() {
		return "changeSetEditor"; //$NON-NLS-1$
	}

	/**
	 * Called when a change set has been changed in another component.
	 * 
	 * @param event the change event
	 */
	@Override
	public void changeSetModified(final ItemChangeEvent<ChangeSet> event) {
		if (event.getEventType() == EventType.CHANGE) {
			ChangeSet modifiedChangeSet = event.getItem();
			if (ObjectUtils.equals(getModel(), modifiedChangeSet)) {
				setChangeSet(modifiedChangeSet);
				refreshEditorPages();
				applyStatePolicy();
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		ChangeSetEventService.getInstance().unregisterChangeSetEventListener(this);
		com.elasticpath.cmclient.core.service.ChangeSetEventService.getInstance().removeChangeEventListener(listener);
	}

	/**
	 * Listener class for keeping editor up to date on changes to object which may be in the change set.
	 */
	private class ChangeSetEventListenerImpl extends AbstractCatalogEventListener implements ChangeEventListener {
		@Override
		public void itemEventOccured(final ItemChangeEvent< ? > event) {
			if (changeSetIncludes(event.getItem())) {
				reloadModel();
				refreshEditorPages();
				applyStatePolicy();
			}
		}
	
		/**
		 * Check whether the change set includes the supplied object.
		 * @param object the object to check
		 * @return true if the object is in the change set, false otherwise
		 */
		public boolean changeSetIncludes(final Object object) {
			return changeSetHelper.isMember(object, getModel().getGuid());
		}

		@Override
		public void searchEventOccurred(final SearchResultEvent< ? > event) {
			//no-op
		}

		public void changeSetChanged(final ItemChangeEvent< ? > event) {
			itemEventOccured(event);
		}
	}
}
