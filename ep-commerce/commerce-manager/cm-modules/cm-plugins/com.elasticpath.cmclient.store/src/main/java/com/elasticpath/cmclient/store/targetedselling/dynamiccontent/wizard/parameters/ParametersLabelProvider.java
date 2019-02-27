/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.parameters;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.domain.contentspace.ParameterValue;

/**
 * Table columns content handler.
 */
public class ParametersLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final String IS_REQUURED_PREFIX = "* "; //$NON-NLS-1$

	private static final String BLANK = StringUtils.EMPTY;

	private static final int NAME = 0;

	private static final int TYPE = 1;

	private static final int IS_MULTILINGUAL = 2;

	private static final int VALUE = 3;

	private Locale locale;

	/**
	 * Set properties label provider.
	 * 
	 * @param locale - sets the locale.
	 */
	public ParametersLabelProvider(final Locale locale) {
		this.locale = locale;
	}

	@Override
	public Image getColumnImage(final Object object, final int columnNumber) {
		if (VALUE == columnNumber) {
			return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT_CELL_SMALL);
		}
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnNumber) {
		switch (columnNumber) {
			case NAME:
				String description = ((ParameterValue) element).getParameterName();
				if (null != ((ParameterValue) element).getParameter()) {
					description = ((ParameterValue) element).getParameter().getName();
				}
				if (((ParameterValue) element).getParameter().isRequired()) {
					return IS_REQUURED_PREFIX + description;
				}
				return description;
			case TYPE:
				return CoreMessages.get().getMessage(((ParameterValue) element).getParameter().getType().getMessageResourceKey());
			case IS_MULTILINGUAL:
				if (((ParameterValue) element).getParameter().isLocalizable()) {
					return TargetedSellingMessages.get().YesNoForBoolean_true;
				}
				return TargetedSellingMessages.get().YesNoForBoolean_false;
			case VALUE:
				return ((ParameterValue) element).getValue(getLocaleStringValue());
			default:
				return BLANK;
		}
	}

	/**
	 * Return the current locale.
	 * 
	 * @return locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Set the current locale.
	 * 
	 * @param locale - locale to set
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}
	
	private String getLocaleStringValue() {
		if (null == getLocale()) {
			return StringUtils.EMPTY;
		}
		return getLocale().toString();
	}
}
