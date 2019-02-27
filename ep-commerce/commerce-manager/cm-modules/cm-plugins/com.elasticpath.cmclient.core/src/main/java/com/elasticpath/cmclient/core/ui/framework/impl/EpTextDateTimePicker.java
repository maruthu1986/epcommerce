/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.conversion.EpStringToDateConverter;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.impl.EpDateTime.CloseListener;

/**
 * Implementation of the IEpDateTimePicker with a text box instead of using the default CDateTime representation.
 */
public class EpTextDateTimePicker implements IEpDateTimePicker, CloseListener, ModifyListener {

	/**
	 * Used for setting the id of the Text control of the EP date/time component.
	 */
	public static final String DATE_TIME_COMPONENT_ID = "EP.DATE_TIME.COMPONENT.ID"; //$NON-NLS-1$

	private EpDateTime picker;

	private final Text textField;

	private final boolean isDateAndTime;

	private Date date;

	private final Composite parentShell;

	private final int styleBits;
	
	private Control invokerControl;

	
	/**
	 * Constructs and instance of this class.
	 * 
	 * @param parentComposite the parent composite
	 * @param epControlFactory the EP control factory
	 * @param style the style bits from {@link IEpDateTimePicker}
	 * @param epState {@link EpState}
	 */
	public EpTextDateTimePicker(final Composite parentComposite, final EpControlFactory epControlFactory, final int style,
			final EpState epState) {
		super();
		isDateAndTime = (style & STYLE_DATE_AND_TIME) != 0;
		textField = epControlFactory.createTextField(parentComposite, style, epState);
		textField.setEditable(false);
		textField.setData(DATE_TIME_COMPONENT_ID);
		textField.addModifyListener(this);
		this.parentShell = new Shell(parentComposite.getDisplay(), parentComposite.getShell().getStyle());
		this.styleBits = style;

		// set tooltip
		if (isDateAndTime) {
			textField.setToolTipText(CoreMessages.get().SampleDateTime + ':' + ' ' + formatDate(new Date()));
		} else {
			textField.setToolTipText(CoreMessages.get().SampleDate + ':' + ' ' + formatDate(new Date()));
		}

	}

	private void createControls(final Composite parentComposite, final int style) {
		picker = new EpDateTime(parentComposite, style);

		picker.addCloseListener(this);
		
		picker.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent event) {
				popupClosed();
				
			}

			public void widgetSelected(final SelectionEvent event) {
				popupClosed();
				
			}

		});

		// TODO remove listeners on dispose event
		// textField.addDisposeListener(listener)
		
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public Text getSwtText() {
		return textField;
	}

	@Override
	public void setDate(final Date date) {
		if (date == null) {
			setPickerDate(new Date());
			textField.setText(""); //$NON-NLS-1$
		} else {
			textField.setText(formatDate(date));
			setPickerDate(date);
			textField.setToolTipText("Date: " + formatDate(date)); //$NON-NLS-1$
		}

	}

	private void setPickerDate(final Date date) {
		if (picker == null) {
			this.date = date;
		} else {
			picker.setDate(date);
		}
	}

	private String formatDate(final Date date) {
		if (isDateAndTime) {
			return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
		} else { // date only style
			return DateTimeUtilFactory.getDateUtil().formatAsDate(date);
		}
	}

	private Date parseDate(final String dateString) throws ParseException {
		if (isDateAndTime) {
			return DateTimeUtilFactory.getDateUtil().parseDateTime(dateString);
		} else { // date only style
			return DateTimeUtilFactory.getDateUtil().parseDate(dateString);
		}
	}

	@Override
	public void open(final Control control) {
		
		
		try {
			if (picker == null || picker.isDisposed()) {
				createControls(parentShell, styleBits);
			}
			picker.setDate(this.date);
			picker.setSelection(parseDate(textField.getText()));
		} catch (final ParseException e) { // on parse exception use current date
			picker.setSelection(new Date());
		}
		picker.open(control);
	}

	/**
	 * Sets the data on close event.
	 */
	public void popupClosed() {
		if (picker != null) {
			String dateText = StringUtils.EMPTY;
			date = picker.getDate();
			if (date != null) {
				dateText =	formatDate(date);
			}
			
			textField.setText(dateText);
		}
	}

	/**
	 * Text modification event listener method.
	 * 
	 * @param event the event
	 */
	public void modifyText(final ModifyEvent event) {
		if ("".equals(textField.getText())) { //$NON-NLS-1$
			date = null;
		} else {
			try {
				date = null;
				date = parseDate(textField.getText());
			} catch (final ParseException e) {
				// skip this exception. the date is not valid so we do not set it...
				return; // avoid an empty catch block error
			}
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		textField.setEditable(enabled);		
	}
	
	@Override
	public void setVisible(final boolean visibled) {
		this.invokerControl.setVisible(visibled);
	}
	/**
	 * Sets the invoker control.
	 * 
	 * @param invokerControl the control invoking the picker 
	 */
	public void setInvokerControl(final Control invokerControl) {
		this.invokerControl = invokerControl;
	}
	
	@Override
	public EpValueBinding bind(final DataBindingContext context, final IValidator validator, final Object target, final String fieldName) {
		final EpBindingConfiguration config = new EpBindingConfiguration(context, getSwtText(), target, fieldName);
		config.configureUiToModelBinding(new EpStringToDateConverter(), validator, true);
		
		return EpControlBindingProvider.getInstance().bind(config);
	}

	
}
