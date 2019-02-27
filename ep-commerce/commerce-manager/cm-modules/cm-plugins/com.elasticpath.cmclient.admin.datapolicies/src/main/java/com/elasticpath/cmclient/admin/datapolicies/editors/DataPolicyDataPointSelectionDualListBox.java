/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.dialogs.DataPointDialog;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.datapolicy.DataPointService;

/**
 * The Data Policy Data Point Selection dual listbox.
 */
public class DataPolicyDataPointSelectionDualListBox extends AbstractEpDualListBoxControl<DataPolicy> {

	private final DataPointService dataPointService;
	private List<DataPoint> assignedDataPoints;

	/**
	 * Constructor.
	 *
	 * @param parentComposite the Composite that contains this thing
	 * @param model           the model
	 * @param availableTitle  the Available Title
	 * @param assignedTitle   the Assigned Title
	 * @param editableState   the editable state of the listbox
	 */
	public DataPolicyDataPointSelectionDualListBox(final IEpLayoutComposite parentComposite,
												   final DataPolicy model,
												   final String availableTitle,
												   final String assignedTitle,
												   final EpState editableState) {
		super(parentComposite, model, availableTitle, assignedTitle, ALL_BUTTONS | MULTI_SELECTION,
				parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), editableState);
		this.dataPointService = ServiceLocator.getService(ContextIdNames.DATA_POINT_SERVICE);
	}

	/**
	 * Another Convenient Constructor.
	 *
	 * @param parentComposite the Composite that contains this thing.
	 * @param model           the model
	 * @param editableState   the editable state of the listbox
	 */
	public DataPolicyDataPointSelectionDualListBox(final IEpLayoutComposite parentComposite,
												   final DataPolicy model,
												   final EpState editableState) {
		this(parentComposite, model, AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Available_Title,
				AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Assigned_Title, editableState);
	}

	@Override
	protected void customizeControls() {
		setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
				final DataPoint dataPoint1 = (DataPoint) obj1;
				final DataPoint dataPoint2 = (DataPoint) obj2;
				return dataPoint1.getName().compareTo(dataPoint2.getName());
			}
		});
		getAvailableTableViewer().addDoubleClickListener(doubleClickEvent -> {
			DataPoint dataPoint = (DataPoint) ((IStructuredSelection) doubleClickEvent.getSelection()).getFirstElement();
			DataPointDialog.openViewDialog(doubleClickEvent.getViewer().getControl().getShell(), dataPoint);
		});
		getAssignedTableViewer().addDoubleClickListener(doubleClickEvent -> {
			DataPoint dataPoint = (DataPoint) ((IStructuredSelection) doubleClickEvent.getSelection()).getFirstElement();
			DataPointDialog.openViewDialog(doubleClickEvent.getViewer().getControl().getShell(), dataPoint);
		});
	}

	@Override
	protected boolean assignToModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}

		for (final Iterator<DataPoint> it = selection.iterator(); it.hasNext();) {
			final DataPoint dataPoint = it.next();
			assignedDataPoints.add(dataPoint);
		}

		return true;
	}

	@Override
	protected boolean removeFromModel(final IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return false;
		}

		for (final Iterator<Locale> it = selection.iterator(); it.hasNext();) {
			assignedDataPoints.remove(it.next());
		}

		return true;
	}

	@Override
	public List<DataPoint> getAssigned() {
		if (assignedDataPoints == null) {
			assignedDataPoints = new ArrayList<>(getModel().getDataPoints());
		}
		return assignedDataPoints;
	}

	@Override
	public Collection<DataPoint> getAvailable() {
		return dataPointService.list();
	}

	@Override
	public ViewerFilter getAvailableFilter() {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				for (final DataPoint locale : getAssigned()) {
					if (locale.equals(element)) {
						return false;
					}
				}
				return true;
			}

		};
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return new LabelProvider() {
			@Override
			public Image getImage(final Object element) {
				return null;
			}

			@Override
			public String getText(final Object element) {
				if (element instanceof DataPoint) {
					return ((DataPoint) element).getName();
				}
				return null;
			}

			@Override
			public boolean isLabelProperty(final Object element, final String property) {
				return false;
			}
		};
	}
}
