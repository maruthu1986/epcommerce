/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.conversion;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;

import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;

/**
 * String to Data converter class.
 */
@SuppressWarnings({"PMD.PrematureDeclaration"})
public class EpStringToDateConverter implements IConverter {

	private final DateFormat[] formatters = DateTimeUtilFactory.getDateUtil().getFormatters();

	@Override
	public Object getFromType() {
		return String.class;
	}

	@Override
	public Object getToType() {
		return Date.class;
	}	

	
	/**
	 * Converts a String to Date.
	 * 
	 * @param fromObject object to convert from
	 * @return resulting object
	 */
	public Object convert(final Object fromObject) {
		if ("".equals(fromObject)) { //$NON-NLS-1$
			return null;
		}
		return parse(fromObject.toString());
	}
	
	private Date parse(final String str) {
		for (int formatterIdx = 0; formatterIdx < formatters.length; formatterIdx++) {
			Date parsed = parse(str, formatterIdx);
			if (parsed != null) {
				return parsed;
			}
		}
		return null;
	}
	
	private Date parse(final String str, final int formatterIdx) {
		if (formatterIdx >= 0) {
				ParsePosition pos = new ParsePosition(0);
				if (str == null) {
					return null;
				}
				Date date = formatters[formatterIdx].parse(str, pos);
				if (pos.getErrorIndex() != -1 || pos.getIndex() != str.length()) {
					return null;
				}
				return date;
		}
		try {
			long millisecs = Long.parseLong(str);
			return new Date(millisecs);
		} catch (NumberFormatException exc) {
			return null;
		}
	}
}
