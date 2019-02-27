/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.helpers.IValueRetriever;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;

/**
 * Create the DateTime input dialog window.
 */
public class DateTimeDialog extends AbstractEpDialog implements IValueRetriever {
	private static final Logger LOG = Logger.getLogger(DateTimeDialog.class); 
	private Date value;

	private IEpDateTimePicker valueText;

	private final int style;

	/**
	 * @param parentShell the parent shell passed in.
	 * @param value Date value passed.
	 * @param style the styles defined in IEpDateTimePicker interface
	 */
	public DateTimeDialog(final Shell parentShell, final Object value, final int style) {
		super(parentShell, 2, false);
		if (value == null) {
			this.value = new Date();
		}
		if (value instanceof Date) {
			this.value = (Date) value;
		} else if (value instanceof String) {
			try {
				this.value = DateTimeUtilFactory.getDateUtil().parseDate((String) value);
			} catch (final ParseException e) {
				LOG.warn("Can not parse date.", e); //$NON-NLS-1$
			}
		}
		this.style = style;
	}

	@Override
	protected void createEpButtonsForButtonsBar(final ButtonsBarType buttonsBarType, final Composite parent) {
		createEpOkButton(parent, CoreMessages.get().AbstractEpDialog_ButtonOK, null);
		createEpCancelButton(parent);
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		dialogComposite.addLabelBold(CatalogMessages.get().AttributeDateTimeDialog_Value, dialogComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER));
		// this.valueText = dialogComposite.addDateTimeComponent(EpState.EDITABLE,
		// dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, true));
		valueText = dialogComposite.addDateTimeComponent(style, EpState.EDITABLE, dialogComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.CENTER, true, true));

		this.valueText.setDate(value);
	}

	@Override
	protected void okPressed() {

		this.value = valueText.getDate();
		super.okPressed();

	}

	@Override
	protected String getInitialMessage() {
		return null;
//		return CatalogMessages.get().AttributeDateTimeDialog_SetDateTimeValue_Msg;
	}

	@Override
	protected String getTitle() {
		return CatalogMessages.get().AttributeDateTimeDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CatalogMessages.get().AttributeDateTimeDialog_WindowTitle;
	}

	@Override
	public Date getValue() {
		return this.value;
	}

	@Override
	protected void bindControls() {
		// no binding is needed for the dialog because dialog does not bind to any object.
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return getValue();
	}

	@Override
	protected void populateControls() {
		// not used
	}

}
