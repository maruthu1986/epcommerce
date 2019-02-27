/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.framework;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;


/**
 * This class overrides all of the blending and other modification of colors that are present in original Eclipse RAP FormColors class.
 * All of the following colors replace colors in FormColors. This is achieved by using the same keys in the map.
 * The SWT color constants specify which color to get from Display.
 * ThemeUtil reads and parses the CSS file specified by themeId in the plugin.xml. All of the retrieved values are available in ThemeUtils and
 * will be
 * used by the Display as specified earlier.
 * <p>
 * The structure is as follows: CSS -> ThemeUtil -> Display (getSystemColor) which maps to SWT.constant</p>
 * <p>
 * SWT constants and coressponding CSS elements and its properties:</p>
 * <p>
 * SWT.COLOR_WIDGET_DARK_SHADOW:"Display","rwt-darkshadow-color",SimpleSelector.DEFAULT
 * SWT.COLOR_WIDGET_NORMAL_SHADOW:"Display","rwt-shadow-color",SimpleSelector.DEFAULT
 * SWT.COLOR_WIDGET_LIGHT_SHADOW:"Display", "rwt-lightshadow-color", SimpleSelector.DEFAULT
 * SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW:"Display","rwt-highlight-color",SimpleSelector.DEFAULT
 * SWT.COLOR_WIDGET_BORDER:"Display", "rwt-thinborder-color", SimpleSelector.DEFAULT
 * SWT.COLOR_WIDGET_BACKGROUND:"NONE", "background-color",SimpleSelector.DEFAULT
 * SWT.COLOR_WIDGET_FOREGROUND:"NONE", "color", SimpleSelector.DEFAULT
 * SWT.COLOR_LIST_FOREGROUND:"List", "color", SimpleSelector.DEFAULT
 * SWT.COLOR_LIST_BACKGROUND:"List",  "background-color",SimpleSelector.DEFAULT
 * SWT.COLOR_LIST_SELECTION:"List-Item","background-color", SimpleSelector.SELECTED
 * SWT.COLOR_LIST_SELECTION_TEXT:"List-Item","color",SimpleSelector.SELECTED
 * SWT.COLOR_INFO_FOREGROUND:"Widget-ToolTip","color",SimpleSelector.DEFAULT
 * SWT.COLOR_INFO_BACKGROUND:"Display","rwt-infobackground-color",SimpleSelector.DEFAULT
 * SWT.COLOR_TITLE_FOREGROUND:"Shell-Titlebar","color",SimpleSelector.DEFAULT
 * SWT.COLOR_TITLE_INACTIVE_FOREGROUND:"Shell-Titlebar","color",SimpleSelector.INACTIVE
 * SWT.COLOR_TITLE_BACKGROUND:"Shell-Titlebar", "background-color",SimpleSelector.DEFAULT
 * SWT.COLOR_TITLE_INACTIVE_BACKGROUND:"Shell-Titlebar","background-color",SimpleSelector.INACTIVE
 * SWT.COLOR_TITLE_BACKGROUND_GRADIENT:"Shell-Titlebar","background-gradient-color",SimpleSelector.DEFAULT
 * SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT:"Shell-Titlebar","background-gradient-color",SimpleSelector.INACTIVE
 *
 * @since 7.0
 */
public class EpFormColors extends FormColors {

	private static final String INACTIVE_FORM_COLOR = "__ncbg__"; //$NON-NLS-1$

	/**
	 * Constructor for the Colors.
	 */
	public EpFormColors() {
		super(Display.getCurrent());
		this.markShared();
	}


	@Override
	protected void initialize() {
		background = this.createColor(SWT.COLOR_WIDGET_BACKGROUND);
		foreground = this.createColor(SWT.COLOR_WIDGET_FOREGROUND);
		initializeColorTable();
		updateBorderColor();
	}

	@Override
	protected void initializeColorTable() {
		createAndSaveColor(IFormColors.TITLE, SWT.COLOR_WIDGET_FOREGROUND); //All the titles have same color as other widgets
		createAndSaveColor(IFormColors.SEPARATOR, SWT.COLOR_WIDGET_LIGHT_SHADOW);
		createAndSaveColor(IFormColors.BORDER, SWT.COLOR_WIDGET_BORDER);
	}

	@Override
	protected void updateBorderColor() {
		border = this.createColor(SWT.COLOR_WIDGET_BACKGROUND);
	}

	@Override
	public void initializeSectionToolBarColors() {
		if (colorRegistry.containsKey(IFormColors.TB_BG)) {
			return;
		}
		createAndSaveColor(IFormColors.TB_BG, SWT.COLOR_WIDGET_BACKGROUND);
		createAndSaveColor(IFormColors.TB_GBG, SWT.COLOR_WIDGET_BACKGROUND); //it as section background (used to be grey shadow behind the title)
		createAndSaveColor(IFormColors.TB_BORDER, SWT.COLOR_TITLE_BACKGROUND);

		createAndSaveColor(IFormColors.TB_TOGGLE, SWT.COLOR_WIDGET_FOREGROUND); //All the titles have same color as other widgets
		createAndSaveColor(IFormColors.TB_TOGGLE_HOVER, SWT.COLOR_WIDGET_DARK_SHADOW);
	}

	@Override
	protected void initializeFormHeaderColors() {
		if (colorRegistry.containsKey(IFormColors.H_BOTTOM_KEYLINE2)) {
			return;
		}
		createAndSaveColor(IFormColors.H_GRADIENT_END, SWT.COLOR_TITLE_BACKGROUND);
		createAndSaveColor(IFormColors.H_GRADIENT_START, SWT.COLOR_TITLE_BACKGROUND);
		createAndSaveColor(IFormColors.H_BOTTOM_KEYLINE2, SWT.COLOR_TITLE_BACKGROUND);
		createAndSaveColor(IFormColors.H_HOVER_LIGHT, SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		createAndSaveColor(IFormColors.H_HOVER_FULL, SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
	}

	@Override
	public Color getInactiveBackground() {
		return createAndSaveColor(INACTIVE_FORM_COLOR, SWT.COLOR_WHITE);
	}

	@Override
	public void setBackground(final Color background) {
		super.setBackground(background);
		this.background = background;
		updateBorderColor();
		updateHeaderColors();
	}

	private void updateHeaderColors() {
		if (colorRegistry.containsKey(IFormColors.H_GRADIENT_END)) {
			colorRegistry.remove(IFormColors.H_GRADIENT_END);
			colorRegistry.remove(IFormColors.H_GRADIENT_START);
			colorRegistry.remove(IFormColors.H_BOTTOM_KEYLINE1);
			colorRegistry.remove(IFormColors.H_BOTTOM_KEYLINE2);
			colorRegistry.remove(IFormColors.H_HOVER_LIGHT);
			colorRegistry.remove(IFormColors.H_HOVER_FULL);
			this.initializeFormHeaderColors();
		}
	}

	@Override
	public Color getColor(final String key) {
		if (key.startsWith(IFormColors.TB_PREFIX)) {
			this.initializeSectionToolBarColors();
		} else if (key.startsWith(IFormColors.H_PREFIX)) {
			this.initializeFormHeaderColors();
		}
		return (Color) colorRegistry.get(key);
	}

	private Color createAndSaveColor(final String key, final int swtColor) {
		Color color = createColor(swtColor);
		colorRegistry.put(key, color);
		return color;
	}

	private Color createColor(final int swtColor) {
		return getDisplay().getSystemColor(swtColor);
	}
}
