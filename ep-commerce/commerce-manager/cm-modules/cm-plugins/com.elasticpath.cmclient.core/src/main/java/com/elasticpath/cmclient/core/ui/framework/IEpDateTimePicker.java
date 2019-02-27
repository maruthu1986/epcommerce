/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.util.Date;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.binding.EpValueBinding;

/**
 * Used for setting date and time using a pop-up dialog next to the invoking UI component.
 */
public interface IEpDateTimePicker {

	/**
	 * Style constant for setting the date/time picker to show the date UI part.
	 */
	int STYLE_DATE = 1 << 1;

	/**
	 * Style constant for setting the date/time picker to show the time UI part.
	 */
	int STYLE_DATE_AND_TIME = 1 << 2;

	/**
	 * Gets the original implementation of the date/time picker.
	 * 
	 * @return <code>CDateTime</code>
	 * @see Text
	 */
	Text getSwtText();

	/**
	 * Shows the popup window.<br>
	 * <i>Note: This method has to used only for showing the pop-up dialog in a stand-alone implementation such as the in-line editing support.</i>
	 * 
	 * @param control the control to be aligned with
	 */
	void open(Control control);

	/**
	 * Sets the date and time to be shown in the text field.
	 * 
	 * @param date <code>java.util.Date</code>, if null the current date will be displayed and the text field will be empty
	 */
	void setDate(Date date);

	/**
	 * Gets the selected date.
	 * 
	 * @return <code>java.util.Date</code>
	 */
	Date getDate();
	
	/**
	 * Binds the date time component to a object with specific field.
	 * 
	 * @param context {@link DataBindingContext}
	 * @param validator a custom validator or null
	 * @param target the target object
	 * @param fieldName the target object field name
	 * @return {@link EpValueBinding}
	 */

	 // ---- DOCIEpDateTimePicker

	EpValueBinding bind(DataBindingContext context, IValidator validator, Object target, String fieldName);

    // ---- DOCIEpDateTimePicker


//	/**
//	 * Sets the date formatter for displaying the correct date/time string and parsing the String from the text field.<br>
//	 * If no formatter has been set the default ones are used:<br> - DateFormat.getDateInstance() for dates <br> - DateFormat.getDateTimeInstance()
//	 * for date and time.
//	 * 
//	 * @param formatter DateFormat
//	 */
//	void setFormatter(DateFormat formatter);

	/**
	 * Sets the UI state to enabled/disabled.
	 * 
	 * @param enabled true if the component should be enabled
	 */
	void setEnabled(boolean enabled);
	
	/**
	 * Sets the UI visibility.
	 * 
	 * @param visibled true if the component is visible.
	 */
	void setVisible(boolean visibled);

}
