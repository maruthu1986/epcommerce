/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import com.elasticpath.cmclient.core.CmSingletonUtil;

/**
 * Ep Form toolkit that lies at the heart of control creation for whole CM with consistent FormColors.
 */
public class EpFormToolkit extends FormToolkit {

	private final Color titleColor;

	/**
	 * Constructor for the Form Toolkit.
	 */
	public EpFormToolkit() {
		super(CmSingletonUtil.getSessionInstance(EpFormColors.class));
		titleColor = getColors().getColor(IFormColors.TITLE);
	}

	@Override
	public ImageHyperlink createImageHyperlink(final Composite parent, final int style) {
		ImageHyperlink imageHyperlink = new ImageHyperlink(parent, style);
		HyperlinkGroup hyperlinkGroup = this.getHyperlinkGroup();

		//Set the same hyperlinkColor for default and activated hyperlink states
		imageHyperlink.setForeground(titleColor);
		hyperlinkGroup.setActiveForeground(titleColor);

		return imageHyperlink;
	}

	/**
	 * Assigns colors to the section with EpFormColors.
	 *
	 * @param section section to be modified
	 */
	public void assignSectionColors(final Section section) {
		section.setForeground(titleColor);
		section.setToggleColor(getColors().getColor(IFormColors.SEPARATOR));
		section.setActiveToggleColor(getColors().getForeground());
	}
}
