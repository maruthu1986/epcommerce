/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.formatting.TimeZoneInfo;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;

/**
 * Date and time picker used for easily select date and time. Uses SWT DateTime widgets on a modal shell.
 * <p>
 * Should be set as selection listener to a button or another control that is supposed to show the UI of the date/time picker.
 * <p>
 * Note: This class is currently used for table in-line editing. It is better to construct date/time component within the
 * <code>IEpLayoutComposite</code>
 * 
 * @see com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite#addDateTimeComponent(int,
 *      com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState, com.elasticpath.cmclient.core.ui.framework.IEpLayoutData)
 */
@SuppressWarnings({ "PMD.GodClass" })
public class EpDateTime extends Dialog implements SelectionListener {


	/**
	 * Style constant for setting the date/time picker to show the date UI part.
	 */
	private static final int STYLE_DATE = SWT.CALENDAR | SWT.BORDER | SWT.SHORT;
	/**
	 * Style constant for setting the date/time picker to show the time UI part.
	 */
	private static final int STYLE_TIME = SWT.BORDER | SWT.TIME | SWT.SHORT;

	/**
	 * Style constant for setting the date/time picker's shell.
	 */
	private static final int SHELL_STYLE =  SWT.APPLICATION_MODAL | SWT.FOCUSED | SWT.ON_TOP;

	private static final int GRID_COLUMNS = 4;

	/**
	 * Shell close listener.
	 */
	public interface CloseListener {

		/**
		 * Notifies the implementor when the close of the popup shell occurs.
		 */
		void popupClosed();

	}

	/**
	 * The date.
	 */
	private Date date;

	/**
	 * The popup's widgets.
	 */
	private Shell shell;

	private DateTime datePicker;

	private DateTime timePicker;

	private Button okButton;

	/**
	 * The listeners associated with the popup.
	 */
	private final List<CloseListener> listeners;
	
	private final  Map<DateTime, List<SelectionListener>> dateTimeSelectionListeners = new HashMap<DateTime, List<SelectionListener>>();
	
	private final Map<Button, List<SelectionListener>> okButtonSelectionListeners = new HashMap<Button, List<SelectionListener>>();
	
	private final Map< Integer, Listener> shellListeners;
	private DisposeListener shellDisposeListener;
	private InternalShellListener internalShellListener;
	
	/**
	 * Constructs new DateTime picker.
	 * 
	 * @param parent the parent composite
	 * @param style one or both (using | operator) of the constants defined in this class
	 */
	public EpDateTime(final Composite parent, final int style) {
		super(parent.getShell(), style);
		listeners = new ArrayList<CloseListener>(1);
		shellListeners = new HashMap<Integer, Listener>();
		initialize(parent, style);
	}
	
	
	private void initialize(final Composite parent, final int style) {
		initializeShell(parent);
		
		datePicker = createDateTimePicker(shell, STYLE_DATE);
		GridData calendarData = new GridData(GridData.FILL, GridData.CENTER, true, true);
		calendarData.horizontalSpan = GRID_COLUMNS;
		datePicker.setLayoutData(calendarData);
		createLeftIndent();
		if (isTimePickerEnabled(style)) {
			createTimePicker();
		}
		createOkButton();
	}

	private void createTimePicker() {

		timePicker = createDateTimePicker(shell, STYLE_TIME);
		GridData timeData = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		timeData.grabExcessHorizontalSpace = false;
		timeData.horizontalSpan = 1;
		timePicker.setLayoutData(timeData);
	}

	private void createLeftIndent() {
		Label emptyLabel = new Label(shell, SWT.NONE);
		GridData blankData = new GridData(GridData.CENTER, GridData.CENTER, true, false);
		emptyLabel.setLayoutData(blankData);
		blankData.horizontalSpan = 1;
	}

	private void createOkButton() {
		GridData okData = new GridData(GridData.END, GridData.CENTER, false, false);
		okButton = new Button(shell, SWT.PUSH);

		okButton.setText(getOkText());
		okButton.setLayoutData(okData);
		okButton.addSelectionListener(this);
		InternalButtonClosedListener okClosedListener = new InternalButtonClosedListener();
		okButton.addSelectionListener(okClosedListener);

		List<SelectionListener> selectionListenersForButton = getSelectionListenersForButton(okButton); 
		selectionListenersForButton.add(this);
		selectionListenersForButton.add(okClosedListener);
	}
	
	private DateTime createDateTimePicker(final Shell shell, final int styleBits) {
		DateTime dateTime = new DateTime(shell, styleBits);
		dateTime.addSelectionListener(this);
		getSelectionListenersForDateTime(dateTime).add(this);
		return dateTime;
	}

	private void initializeShell(final Composite parent) {
		
		shell = new Shell(parent.getShell(), SHELL_STYLE);
		internalShellListener = new InternalShellListener();
		shell.addShellListener(internalShellListener);
		shell.setText(getWindowDialogTitleText());
		GridLayout layout = new GridLayout();
		layout.numColumns = GRID_COLUMNS;
		shell.setLayout(layout);		
		Listener deactivateListener = new Listener() {

			public void handleEvent(final Event event) {
				shell.close();
			}
		};
		
		shell.addListener(SWT.Deactivate, deactivateListener);
		shellListeners.put(Integer.valueOf(SWT.Deactivate), deactivateListener);
		
		shellDisposeListener = new DisposeListener() {
			public void widgetDisposed(final DisposeEvent event) {
				removeListeners();
				shell.removeDisposeListener(this);
			}
		};
		shell.addDisposeListener(shellDisposeListener);
	}
	
	private boolean isTimePickerEnabled(final int style) {
		return (style & IEpDateTimePicker.STYLE_DATE_AND_TIME) != 0;
	}
	
	private String getOkText() {
		return CoreMessages.get().AbstractEpDialog_ButtonOK;
	}

	private String getWindowDialogTitleText() {
		return CoreMessages.get().DateTimeDialog_EditWindowTitle;
	}
	
	/**
	 * Is the dependent objects of this EpDateTime picker disposed?
	 * 
	 * @return True if dateTimePicker or timePicker disposed or null.
	 */
	public boolean isDisposed() {

		if (datePicker == null || datePicker.isDisposed()) {
			return true;
		}

		if (timePicker == null || timePicker.isDisposed()) {
			return true;
		}

		return false;
	}
	
	/**
	 * Opens the date/time picker next to the specified control.
	 * 
	 * @param control SWT control
	 */
	public void open(final Control control) {
		if (control instanceof Composite) {
			shell.pack();
			Point point = getLocation(shell);
			shell.setLocation(point);
			shell.open();
		}
	}


	/**
	 * Gets the absolute location to position a shell at the current location of
	 * the cursor within a monitor ensuring that the shell is completely
	 * visible. If the shell is larger than the monitor, it is left- and
	 * top-aligned.
	 * 
	 * @param shell
	 *            The Shell needs to be position on a monitor.
	 * @return the location.
	 */
	private Point getLocation(final Shell shell) {
		Display display = shell.getDisplay();
		Monitor[] monitors = display.getMonitors();

		Point point = display.getCursorLocation();
		Point size = shell.getSize();

		for (int i = 0; i < monitors.length; i++) {

			if (monitors[i].getBounds().contains(point)) {
				Rectangle rect = monitors[i].getClientArea();
				point.x = Math.max(
						rect.x,
						Math.min(Math.max(point.x, rect.x), rect.x + rect.width - size.x));
				point.y = Math.max(
						rect.y,
						Math.min(Math.max(point.y, rect.y), rect.y + rect.height - size.y));
				break;
			}
		}
		return point;
	}
 
	 
	/**
	 * Called when a selection event occurs.
	 * 
	 * @param event SelectionEvent
	 */
	public void widgetDefaultSelected(final SelectionEvent event) {
		widgetSelected(event);

	}

	/**
	 * Called when a selection event occurs.
	 * 
	 * @param event SelectionEvent
	 */
	public void widgetSelected(final SelectionEvent event) {
		// make sure that the source of the event is something that we care about.
		if (isSourceFromDateTimePicker(event)) {
			date = getSelection();
			return;
		}
		// if the source of the event is a control, open the dateTime picker shell with the event source as the parent.
		if (event.getSource() instanceof Control) {
			open((Control) event.getSource());
		}
	}

	private boolean isSourceFromDateTimePicker(final TypedEvent event) {
		return event.getSource() == this || event.getSource() == datePicker || event.getSource() == timePicker || event.getSource() == okButton;
	}
	
	/**
	 * Internal listener for the Ok button being pressed.
	 */
	private class InternalButtonClosedListener implements SelectionListener {
		public void widgetSelected(final SelectionEvent event) {
			shell.close(); // disposes shell and all widgets in shell
		}

		public void widgetDefaultSelected(final SelectionEvent event) {
			shell.close();
		}
		
	}

	/**
	 * Gets the selected date from the datetime picker.
	 * 
	 * @return a Date which the user selected.
	 */
	public Date getSelection() {
		int day = datePicker.getDay();
		int month = datePicker.getMonth();
		int year = datePicker.getYear();
		int hours = 0;
		int minutes = 0;
		if (timePicker != null) {
			hours = timePicker.getHours();
			minutes = timePicker.getMinutes();
		}
		Calendar calendar = Calendar.getInstance();
		// seconds & miliseconds should not be used, so will reset them to 0
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		
		calendar.set(year, month, day, hours, minutes);
		calendar.setTimeZone(TimeZoneInfo.getInstance().getTimezone());
		return calendar.getTime();
	}

	/**
	 * Sets the datetime picker to the given date.
	 * 
	 * @param aDate the date to set the datetime picker to.
	 */
	public void setSelection(final Date aDate) {
		if (isDisposed()) {
			return;
		}
		if (aDate == null) {
			return;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZoneInfo.getInstance().getTimezone());

		calendar.setTime(aDate);
		date = aDate;
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		datePicker.setDate(year, month, day);
		if (timePicker != null) {
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			timePicker.setHours(hour);
			timePicker.setMinutes(minute);
		}

	}

	/**
	 * Gets the selected date.
	 * 
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	
	private void removeListeners() {
		listeners.clear();
		
		for (DateTime dateTime : dateTimeSelectionListeners.keySet()) {
			List<SelectionListener> list = dateTimeSelectionListeners.get(dateTime);
			for (SelectionListener listener : list) {
				dateTime.removeSelectionListener(listener);
			}
		}
		
		for (Button button : okButtonSelectionListeners.keySet()) {
			List<SelectionListener> list = okButtonSelectionListeners.get(button);
			for (SelectionListener listener : list) {
				button.removeSelectionListener(listener);
			}
		}
		
		for (Integer listenerType : shellListeners.keySet()) {
			shell.removeListener(listenerType.intValue(), shellListeners.get(listenerType));
		}
		shell.removeShellListener(internalShellListener);
		shell.removeDisposeListener(shellDisposeListener);
	}




	/**
	 * Internal Shell listener for close and deactivate events.
	 */
	private class InternalShellListener extends ShellAdapter {

		@Override
		public void shellClosed(final ShellEvent event) {
			date = getSelection();
			notifyListeners();
			shell.dispose();
		}

		@Override
		public void shellDeactivated(final ShellEvent event) {
			date = getSelection();
			notifyListeners();
			shell.dispose();
		}
	}

	/**
	 * Sets the date to be shown.
	 * 
	 * @param date {@link Date}
	 */
	public void setDate(final Date date) {
		this.date = date;
		setSelection(date);
	}

	/**
	 * Adds a close listener.
	 * 
	 * @param listener the listener
	 */
	public void addCloseListener(final CloseListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners() {
		for (final CloseListener listener : listeners) {
			listener.popupClosed();
		}
	}

	private List<SelectionListener> getSelectionListenersForButton(final Button button) {
		if (okButtonSelectionListeners.containsKey(button)) {
			return okButtonSelectionListeners.get(button);
		}
		ArrayList<SelectionListener> compositeListeners = new ArrayList<SelectionListener>();
		okButtonSelectionListeners.put(button, compositeListeners);
		return compositeListeners;
	}
	
	
	private List<SelectionListener> getSelectionListenersForDateTime(final DateTime dateTime) {
		if (dateTimeSelectionListeners.containsKey(dateTime)) {
			return dateTimeSelectionListeners.get(dateTime);
		}
		ArrayList<SelectionListener> compositeListeners = new ArrayList<SelectionListener>();
		dateTimeSelectionListeners.put(dateTime, compositeListeners);
		return compositeListeners;
	}
	
	/**
	 * Adds selection listeners to the pickers.
	 * 
	 * @param selectionListener the selection listener to add.
	 */
	public void addSelectionListener(final SelectionListener selectionListener) {
		datePicker.addSelectionListener(selectionListener);
		getSelectionListenersForDateTime(datePicker).add(selectionListener);
		
		okButton.addSelectionListener(selectionListener);
		getSelectionListenersForButton(okButton).add(selectionListener);
		
		
		if (timePicker != null) {
			timePicker.addSelectionListener(selectionListener);
			getSelectionListenersForDateTime(timePicker).add(selectionListener);
		}
		
	}
}
