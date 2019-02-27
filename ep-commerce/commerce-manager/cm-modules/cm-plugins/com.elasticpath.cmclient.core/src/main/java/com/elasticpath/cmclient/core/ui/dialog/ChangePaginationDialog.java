/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.dialog;

import java.util.List;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.pagination.PaginationInfo;
import com.elasticpath.cmclient.core.service.CoreEventService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Dialog to change the pagination settings system-wide.
 */
public class ChangePaginationDialog extends AbstractEpDialog {
	
	private CCombo paginationCombo;

	/**
	 * Default constructor.
	 *
	 * @param parentShell the parent shell
	 */
	public ChangePaginationDialog(final Shell parentShell) {
		super(parentShell, 2, false);
	}

	@Override
	protected void bindControls() {
		// nothing to bind
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite parent) {
		final IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(parent.getSwtComposite(), 2, false);
		final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		
		controlPane.addLabelBold(CoreMessages.get().ChangePaginationDialog_ResultsPerPage, labelData);
		paginationCombo = controlPane.addComboBox(EpState.EDITABLE, fieldData);
	}

	@Override
	protected String getInitialMessage() {
		return CoreMessages.get().ChangePaginationDialog_Description;
	}

	@Override
	protected String getTitle() {
		return CoreMessages.get().ChangePaginationDialog_Title;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.CHANGE_PAGINATION);
	}

	@Override
	protected String getWindowTitle() {
		return CoreMessages.get().ChangePaginationDialog_WindowTitle;
	}

	@Override
	protected String getPluginId() {
		return CorePlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return null;
	}

	@Override
	protected void populateControls() {
		List<Integer> validPaginatioSettings = PaginationInfo.getValidPaginations();

		int currentPagination = PaginationInfo.getInstance().getPagination();
		for (int pagination : validPaginatioSettings) {
			paginationCombo.add(String.valueOf(pagination));
			if (pagination == currentPagination) {
				paginationCombo.select(paginationCombo.getItemCount() - 1);
			}
		}
	}

	@Override
	protected void okPressed() {
		int pagination = Integer.parseInt(paginationCombo.getText());
		PaginationInfo.getInstance().setPaginationNumber(pagination);
		CoreEventService.getInstance().notifyPaginationChange(pagination);
		super.okPressed();
	}
}
