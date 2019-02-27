/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesPlugin;
import com.elasticpath.cmclient.admin.datapolicies.editors.pages.DataPolicyDataPointPage;
import com.elasticpath.cmclient.admin.datapolicies.editors.pages.DataPolicySegmentsPage;
import com.elasticpath.cmclient.admin.datapolicies.editors.pages.DataPolicySummaryPage;
import com.elasticpath.cmclient.admin.datapolicies.event.DataPolicyEventListener;
import com.elasticpath.cmclient.admin.datapolicies.event.DataPolicyEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Data Policy editor.
 */
public class DataPolicyEditor extends AbstractCmClientFormEditor implements DataPolicyEventListener {

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = DataPolicyEditor.class.getName();

	private static final Logger LOG = Logger.getLogger(DataPolicyEditor.class);

	/**
	 * Constant used for matching the right event.
	 */
	private static final int UPDATE_TOOLBAR = 301;

	private DataPolicyService dataPolicyService;

	private DataPolicy dataPolicy;

	private boolean editableMode;

	private DataPolicyState initialState;

	private ItemChangeEvent.EventType action;

	/**
	 * Creates a multi-page editor.
	 */
	public DataPolicyEditor() {
		super();
		DataPolicyEventService.getInstance().registerDataPolicyEventListener(this);
	}

	@Override
	protected void addPages() {
		try {
			addPage(new DataPolicySummaryPage(this));
			addPage(new DataPolicyDataPointPage(this));
			addPage(new DataPolicySegmentsPage(this));
			addExtensionPages(getClass().getSimpleName(), AdminDataPoliciesPlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			LOG.error("Can not create pages for the Data Policy editor", e); //$NON-NLS-1$
		}
	}

	@Override
	public DataPolicy getModel() {
		return dataPolicy;
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) {
		dataPolicyService = ServiceLocator.getService(ContextIdNames.DATA_POLICY_SERVICE);

		final Long uid = input.getAdapter(Long.class);
		if (uid > 0) {
			dataPolicy = dataPolicyService.load(uid);
			action = ItemChangeEvent.EventType.CHANGE;
		} else {
			dataPolicy = ServiceLocator.getService(ContextIdNames.DATA_POLICY);
			dataPolicy.initialize();
			action = ItemChangeEvent.EventType.ADD;
		}

		editableMode = dataPolicy.isEditable();
		initialState = dataPolicy.getState();
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		Set<String> segments = dataPolicy.getSegments();
		if (segments.isEmpty()) {
			MessageDialog.openWarning(getSite().getShell(),
					AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_SegmentsRequiredTitle,
					AdminDataPoliciesMessages.get().DataPolicyEditor_SegmentsPage_SegmentsRequiredDetailedMessage);
			controlModified();
			return;
		}
		if (DataPolicyState.DISABLED.equals(dataPolicy.getState())) {
			dataPolicy.disable();
		}
		dataPolicy = dataPolicyService.update(dataPolicy);
		fireRefreshActions();
	}

	private void fireRefreshActions() {
		editableMode = dataPolicy.isEditable();
		DataPolicyEditorInput input = (DataPolicyEditorInput) getEditorInput();
		input.setUid(dataPolicy.getUidPk());
		input.setName(dataPolicy.getPolicyName());

		firePropertyChange(UPDATE_TOOLBAR);

		refreshEditorPages();

		DataPolicyEventService.getInstance().fireDataPolicyChanged(new ItemChangeEvent<>(this, dataPolicy, action));
		action = ItemChangeEvent.EventType.CHANGE;
	}

	@Override
	public void reloadModel() {
		dataPolicy = dataPolicyService.load(dataPolicy.getUidPk());
		editableMode = dataPolicy.isEditable();
		initialState = dataPolicy.getState();
		refreshEditorPages();
		action = ItemChangeEvent.EventType.CHANGE;
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return NLS.bind(AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_OnSavePrompt, getEditorName());
	}

	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}

	public boolean isEditableMode() {
		return editableMode;
	}

	public boolean isEndDateEditable() {
		return !initialState.equals(DataPolicyState.DISABLED);
	}

	@Override
	public void dataPolicyChanged(final ItemChangeEvent<DataPolicy> event) {
		reloadModel();
	}
}
