/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.core.ui.dialog;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.formatting.TimeZoneInfo;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;

/**
 * Dialog to change the timezone settings system-wide.
 */
public class ChangeTimezoneDialog extends AbstractEpDialog {

	private static final int ITEM_COUNT = 10;
	private CCombo timezoneCCombo;
	private Button browserRadio;
	private Button customRadio;
	private String previousTimezoneId;
	private final TimeZoneInfo timeZoneInfo = TimeZoneInfo.getInstance();

	/**
	 * Default constructor.
	 *
	 * @param parentShell the parent shell
	 */
	public ChangeTimezoneDialog(final Shell parentShell) {
		super(parentShell, 1, false);
	}

	@Override
	protected void bindControls() {
		// nothing to bind
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite parent) {
		IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(parent.getSwtComposite(), 1, false);
		IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);

		final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		controlPane.addLabel(CoreMessages.get().ChangeTimezoneDialog_Description, labelData);
		browserRadio = controlPane.addRadioButton(NLS.bind(CoreMessages.get().ChangeTimezoneDialog_Browser,
				getTimeZoneInfo().getDisplayStringForTimeZone(getTimeZoneInfo().getBrowserTimezone())), EpState.EDITABLE, labelData);
		customRadio = controlPane.addRadioButton(CoreMessages.get().ChangeTimezoneDialog_Custom, EpState.EDITABLE, labelData);
		timezoneCCombo = controlPane.addComboBox(EpState.EDITABLE, fieldData);
		timezoneCCombo.setVisibleItemCount(ITEM_COUNT);
		updateTimezoneDisplay();
	}

	private TimeZoneInfo getTimeZoneInfo() {
		return timeZoneInfo;
	}

	private void updateTimezoneDisplay() {
		String formattedDateTime = DateTimeUtilFactory.getDateUtil().formatAsDateTime(new Date());
		TimeZone timeZone = getTimeZoneInfo().getTimezone();
		String displayName = "";
		if (timeZone != null) {
			displayName = getTimeZoneInfo().getDisplayStringForTimeZone(timeZone);
		}

		if (formattedDateTime != null) {
			setMessage(NLS.bind(CoreMessages.get().ChangeTimezoneDialog_CurrentTime,
					formattedDateTime, displayName));
		}
	}

	@Override
	protected String getInitialMessage() {
		return NLS.bind(CoreMessages.get().ChangeTimezoneDialog_CurrentTime, "", "");
	}

	@Override
	protected String getTitle() {
		return CoreMessages.get().ChangeTimezoneDialog_Title;
	}

	@Override
	protected Image getWindowImage() {
		return CoreImageRegistry.getImage(CoreImageRegistry.CHANGE_PAGINATION);
	}

	@Override
	protected String getWindowTitle() {
		return CoreMessages.get().ChangeTimezoneDialog_WindowTitle;
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
		List<String> timezoneDisplayStrings = getTimeZoneInfo().getTimezoneDisplayStrings();

		previousTimezoneId = getTimeZoneInfo().getTimezoneId();
		String previousTimezoneDisplayString = getTimeZoneInfo()
				.getDisplayStringForTimeZone(getTimeZoneInfo().getTimezone());


		for (String timezoneDisplayString : timezoneDisplayStrings) {
			timezoneCCombo.add(timezoneDisplayString);
			if (Objects.equals(timezoneDisplayString, previousTimezoneDisplayString)) {
				timezoneCCombo.select(timezoneCCombo.getItemCount() - 1);
			}
		}

		customRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Button button = (Button) event.getSource();
				timezoneCCombo.setEnabled(button.getSelection());
				if (button.getSelection()) {
					TimeZone timeZoneForDisplayString = getTimeZoneInfo().getTimeZoneForDisplayString(timezoneCCombo.getText());
					getTimeZoneInfo().setTimezone(timeZoneForDisplayString);
				}

				setOKButtonStateTimezoneSelected();
				updateTimezoneDisplay();
			}
		});
		timezoneCCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				TimeZone timeZoneForDisplayString = getTimeZoneInfo().getTimeZoneForDisplayString(timezoneCCombo.getText());
				getTimeZoneInfo().setTimezone(timeZoneForDisplayString);
				updateTimezoneDisplay();
				setOKButtonStateTimezoneSelected();

			}
		});

		browserRadio.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Button button = (Button) event.getSource();
				if (button.getSelection()) {
					setTimezoneToBrowserTimezone();
					updateTimezoneDisplay();
					getOkButton().setEnabled(true);
				}
			}
		});


		if (TimeZoneInfo.BROWSER.equals(previousTimezoneId)) {
			browserRadio.setSelection(true);
			customRadio.setSelection(false);
			timezoneCCombo.setEnabled(false);
		} else {
			browserRadio.setSelection(false);
			customRadio.setSelection(true);
			timezoneCCombo.setEnabled(true);
		}
		updateTimezoneDisplay();
	}

	private void setOKButtonStateTimezoneSelected() {
		getOkButton().setEnabled(timezoneCCombo.getSelectionIndex() >= 0);
	}


	private void setTimezoneToBrowserTimezone() {
		getTimeZoneInfo().setTimezone(TimeZoneInfo.BROWSER);

	}

	@Override
	protected void okPressed() {
		String timezoneId;
		if (customRadio.getSelection()) {
			timezoneId = getTimeZoneInfo().getTimeZoneForDisplayString(timezoneCCombo.getText()).getID();
		} else {
			timezoneId = TimeZoneInfo.BROWSER;
		}
		getTimeZoneInfo().setTimezone(timezoneId);

		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		getTimeZoneInfo().setTimezone(previousTimezoneId);
		super.cancelPressed();
	}

}
