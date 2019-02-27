/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEventCmHelper;
import com.elasticpath.domain.order.OrderEvent;

/**
 * Represents the UI for edit note dialog.
 */
public class ViewAllNoteDialog extends AbstractEpDialog {

	private final Set<OrderEvent> orderEvents;

	/**
	 * Constructor.
	 *
	 * @param parentShell the Shell
	 * @param orderEvents the OrderEvents
	 */
	public ViewAllNoteDialog(final Shell parentShell, final Set<OrderEvent> orderEvents) {
		super(parentShell, 2, false);
		this.orderEvents = orderEvents;
	}

	@Override
	protected void bindControls() {
		// Do nothing
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, "OK", null); //$NON-NLS-1$
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		final EpState epStateRead = EpState.READ_ONLY;
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(dialogComposite.getSwtComposite(),
				SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL);
		// RCPRAP - no drag detect?
		//scrolledComposite.setDragDetect(false);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		
		final GridData gridData = new GridData(500, 300);
		scrolledComposite.setLayoutData(gridData);

		FormToolkit toolkit = EpControlFactory.getInstance().createFormToolkit();
		toolkit.adapt(scrolledComposite, true, true);

		final IEpLayoutComposite overviewComposite = CompositeFactory.createGridLayoutComposite(scrolledComposite, 2, false);
		overviewComposite.setLayoutData(new GridData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent event) {
				final Point size = overviewComposite.getSwtComposite().computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrolledComposite.setMinHeight(size.y);
			}
		});

		final IEpLayoutData labelData = overviewComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		final IEpLayoutData fieldData = overviewComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData fieldDataText = overviewComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData separatorData = overviewComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false, 2, 1);
		

		for (OrderEvent orderEvent : prepareSortedOrderEvents(orderEvents)) {
			overviewComposite.addLabelBoldRequired(FulfillmentMessages.get().OrderNoteNotes_DialogLabelCreatedBy, epStateRead, labelData);
			Text textField = overviewComposite.addTextField(epStateRead, fieldData);
			textField.setText(OrderEventCmHelper.getCreatedBy(orderEvent));
	
			overviewComposite.addLabelBoldRequired(FulfillmentMessages.get().OrderNoteNotes_DialogLabelCreatedOn, epStateRead, labelData);
			Text textDate = overviewComposite.addTextField(epStateRead, fieldData);
			textDate.setText(String.valueOf(orderEvent.getCreatedDate()));
	
			overviewComposite.addLabelBoldRequired(FulfillmentMessages.get().OrderNoteNotes_DialogLabelNote, epStateRead, labelData);
			Text textArea = overviewComposite.addTextArea(false, false, epStateRead, fieldDataText);
			textArea.setText(orderEvent.getNote());
			
			overviewComposite.addHorizontalSeparator(separatorData);

		}

		scrolledComposite.setContent(overviewComposite.getSwtComposite());		
	}

	/*
	 * Utility for creating sorted order event set.
	 */
	private static SortedSet<OrderEvent> prepareSortedOrderEvents(final Set<OrderEvent> orderEvents) {
		final SortedSet<OrderEvent> sortedOrderEvents = new TreeSet<>(Comparator.comparing(OrderEvent::getCreatedDate));
		sortedOrderEvents.addAll(orderEvents);
		return sortedOrderEvents;
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return orderEvents;
	}

	@Override
	protected void populateControls() {
		//Do nothing
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	@Override
	protected String getTitle() {
		return FulfillmentMessages.get().OrderNoteNotes_DialogTitleViewAll;
	}

	@Override
	protected String getWindowTitle() {
		return FulfillmentMessages.get().OrderNoteNotes_DialogTitleViewAll;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_NOTE);
	}

}
