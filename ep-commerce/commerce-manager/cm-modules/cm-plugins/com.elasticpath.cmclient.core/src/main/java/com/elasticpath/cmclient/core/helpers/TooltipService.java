/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.core.helpers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * /**
 * This class sets the tooltip.
 * <p>
 * In future this class must be removed
 */
@SuppressWarnings({"checkstyle:magicnumber", "PMD.AvoidDuplicateLiterals"})
public final class TooltipService {

	private static final Map<Class, Map<String, Integer>> WIDGET_TO_SWT_MAP;
	private static final Map<String, Integer> SWT_MAP;

	private static final String NEXT_LINE = "<br/>";  //$NON-NLS-1$
	private static final String SEPARATOR = "   ";  //$NON-NLS-1$
	private static final int STYLE_PER_ROW = 4;  //$NON-NLS-1$
	private static final String EMPTY_STRING = "";  //$NON-NLS-1$
	private static final String NO_ID = "No WIDGET ID";  //$NON-NLS-1$

	private TooltipService() {
		//empty
	}

	/**
	 * Adds hover.
	 *
	 * @param widget   widget to which hover will apply
	 * @param widgetId widget id
	 */
	public static void showTooltip(final Widget widget, final String widgetId) {
		if (!UITestUtil.isEnabled()) {
			//Do not display a tooltip when not in debug mode
			return;
		}

		StringBuilder toolTip = new StringBuilder();

		if (widgetId.equals(EMPTY_STRING)) {
			toolTip.append(NO_ID);
		} else {
			toolTip.append("WIDGET-id: ").append(widgetId);
		}

		if (widget instanceof Control) {
			Control control = (Control) widget;

			Font font = control.getFont();
			Color fgColor = control.getForeground();
			Color bgColor = control.getBackground();
			boolean bgImage = control.getBackgroundImage() != null;

			int style = control.getStyle();

			Map<String, Integer> mapByWidgetType = getMapByWidgetType(widget);

			toolTip
					.append(NEXT_LINE).append("Type       : ").append(widget.getClass().getSimpleName())
					.append(NEXT_LINE).append("Font       : ").append(font)
					.append(NEXT_LINE).append("Fg color   : ").append(fgColor)
					.append(NEXT_LINE).append("Bg color   : ").append(bgColor)
					.append(NEXT_LINE).append("SWT style  : ").append(getAllAssignedSWTStyle(style, mapByWidgetType))
					.append(NEXT_LINE).append("Has bg img?: ").append(Boolean.toString(bgImage).toUpperCase());
		}

		widget.setData(RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE);
		String scriptCode = String.format("var handleEvent = function( event ){\n"
				+ "event.widget.setToolTipText('%s');"
				+ "};", toolTip.toString());

		ClientListener listener = new ClientListener(scriptCode);
		widget.addListener(SWT.MouseEnter, listener);
	}

	private static String getAllAssignedSWTStyle(final int style, final Map<String, Integer> applicableStyles) {
		StringBuilder builder = new StringBuilder();

		int count = 0;
		for (Map.Entry<String, Integer> entry : applicableStyles.entrySet()) {
			int compareStyle = entry.getValue();
			String styleName = entry.getKey().toLowerCase();

			if ((style & compareStyle) == compareStyle) {
				if (count == 0) {
					builder.append(styleName);
				} else {
					builder.append(SEPARATOR).append(styleName);
					//Add new Line
					if (count % STYLE_PER_ROW == 0) {
						builder.append(NEXT_LINE);
					}
				}
				count++;
			}
		}

		return builder.toString();
	}

	private static Map<String, Integer> getMapByWidgetType(final Widget widget) {
		Map<String, Integer> map = WIDGET_TO_SWT_MAP.get(widget.getClass());
		if (map == null) {
			return SWT_MAP;
		}
		return map;
	}

	static {
		//Shared styles:
		final Map<String, Integer> controlMap = new LinkedHashMap() {
			{
				put("BORDER", 2048);
				put("LEFT_TO_RIGHT", 33554432);
				put("RIGHT_TO_LEFT", 67108864);
			}
		};

		final Map<String, Integer> buttonMap = new LinkedHashMap() {
			{
				put("PUSH", 8);
				put("RADIO", 16);
				put("CHECK", 32);
				put("ARROW", 4);
				put("TOGGLE", 2);
				put("FLAT", 8388608);
				put("UP", 128);
				put("DOWN", 1024);
				put("LEFT", 16384);
				put("RIGHT", 131072);
				put("CENTER", 16777216);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> toolItemMap = new LinkedHashMap() {
			{
				put("PUSH", 8);
				put("CHECK", 32);
				put("RADIO", 16);
				put("SEPARATOR", 2);
				put("DROP_DOWN", 4);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> toolBarMap = new LinkedHashMap() {
			{
				put("FLAT", 8388608);
				put("WRAP", 64);
				put("RIGHT", 131072);
				put("HORIZONTAL", 256);
				put("VERTICAL", 512);
				put("SHADOW_OUT", 8);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> textMap = new LinkedHashMap() {
			{
				put("CENTER", 16777216);
				put("ICON_SEARCH", 512);
				put("ICON_CANCEL", 256);
				put("LEFT", 16384);
				put("MULTI", 2);
				put("PASS_WORD", 4194304);
				put("SEARCH", 128);
				put("SINGLE", 4);
				put("RIGHT", 131072);
				put("READ_ONLY", 8);
				put("WRAP", 64);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> labelMap = new LinkedHashMap() {
			{
				put("SEPARATOR", 2);
				put("HORIZONTAL", 256);
				put("VERTICAL", 512);
				put("SHADOW_IN", 4);
				put("SHADOW_OUT", 8);
				put("SHADOW_NONE", 32);
				put("CENTER", 16777216);
				put("LEFT", 16384);
				put("RIGHT", 131072);
				put("WRAP", 64);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> tabFolderMap = new LinkedHashMap() {
			{
				put("TOP", 128);
				put("BOTTOM", 1024);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> groupMap = new LinkedHashMap() {
			{
				put("SHADOW_ETCHED_IN", 16);
				put("SHADOW_ETCHED_OUT", 64);
				put("SHADOW_IN", 4);
				put("SHADOW_OUT", 8);
				put("SHADOW_NONE", 32);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> shellMap = new LinkedHashMap() {
			{
				put("CLOSE", 64);
				put("MIN", 128);
				put("MAX", 1024);
				put("NO_TRIM", 8);
				put("RESIZE", 16);
				put("TITLE", 32);
				put("ON_TOP", 16384);
				put("SHEET", 268435456);
				put("APPLICATION_MODAL", 65536);
				put("MODELESS", 0);
				put("PRIMARY_MODAL", 32768);
				put("SYSTEM_MODAL", 131072);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> comboMap = new LinkedHashMap() {
			{
				put("DROP_DOWN", 4);
				put("READ_ONLY", 8);
				put("SIMPLE", 64);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> ccomboMap = new LinkedHashMap() {
			{
				put("READ_ONLY", 8);
				put("FLAT", 8388608);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> hyperlinkMap = new LinkedHashMap() {
			{
				put("WRAP", 64);
				putAll(controlMap);
			}
		};
		final Map<String, Integer> coolBarMap = new LinkedHashMap() {
			{
				put("FLAT", 8388608);
				put("HORIZONTAL", 256);
				put("VERTICAL", 512);
				putAll(controlMap);

			}
		};
		final Map<String, Integer> coolItemMap = new LinkedHashMap() {
			{
				put("DROP_DOWN", 4);
				putAll(controlMap);
			}
		};

		//All SWT styles
		SWT_MAP = new LinkedHashMap() {
			{
//			put("None", 0);
//			put("KeyDown", 1);
//			put("KeyUp", 2);
//			put("MouseDown", 3);
//			put("MouseUp", 4);
//			put("MouseMove", 5);
//			put("MouseEnter", 6);
//			put("MouseExit", 7);
//			put("MouseDoubleClick", 8);
//			put("Paint", 9);
//			put("Move", 10);
//			put("Resize", 11);
//			put("Dispose", 12);
//			put("Selection", 13);
//			put("DefaultSelection", 14);
//			put("FocusIn", 15);
//			put("FocusOut", 16);
//			put("Expand", 17);
//			put("Collapse", 18);
//			put("Close", 21);
//			put("Show", 22);
//			put("Hide", 23);
//			put("Modify", 24);
//			put("Verify", 25);
//			put("Activate", 26);
//			put("Deactivate", 27);
//			put("Help", 28);
//			put("DragDetect", 29);
//			put("Arm", 30);
//			put("Traverse", 31);
//			put("MenuDetect", 35);
//			put("SetData", 36);
//			put("MouseWheel", 37);
//			put("Settings", 39);
//			put("Skin", 45);
//			put("DRAG", 1);
//			put("SELECTED", 2);
//			put("FOCUSED", 4);
//			put("BACKGROUND", 8);
//			put("FOREGROUND", 16);
//			put("HOT", 32);
//			put("TRAVERSE_NONE", 0);
//			put("TRAVERSE_ESCAPE", 2);
//			put("TRAVERSE_RETURN", 4);
//			put("TRAVERSE_TAB_PREVIOUS", 8);
//			put("TRAVERSE_TAB_NEXT", 16);
//			put("GESTURE_BEGIN", 2);
//			put("GESTURE_END", 4);
//			put("GESTURE_ROTATE", 8);
//			put("GESTURE_SWIPE", 16);
//			put("GESTURE_MAGNIFY", 32);
//			put("GESTURE_PAN", 64);
//			put("TOUCHSTATE_DOWN", 1);
//			put("TOUCHSTATE_MOVE", 2);
//			put("TOUCHSTATE_UP", 4);
//			put("ALL", 1);
//			put("CHANGED", 2);
//			put("DEFER", 4);
//			put("NULL", 0);
//			put("NONE", 0);
//			put("DEFAULT", -1);
//			put("OFF", 0);
//			put("ON", 1);
				put("UP", 128);
				put("TOP", 128);
				put("DOWN", 1024);
				put("BOTTOM", 1024);
				put("LEAD", 16384);
				put("LEFT", 16384);
				put("TRAIL", 131072);
				put("RIGHT", 131072);
				put("CENTER", 16777216);
				put("HORIZONTAL", 256);
				put("VERTICAL", 512);
				put("BALLOON", 4096);
				put("BEGINNING", 1);
				put("FILL", 4);
//			put("ALT", 65536);
//			put("SHIFT", 131072);
//			put("CTRL", 262144);
//			put("CONTROL", 262144);
//			put("COMMAND", 4194304);
//			put("MODIFIER_MASK", 4653056);
//			put("BUTTON1", 524288);
//			put("BUTTON2", 1048576);
//			put("BUTTON3", 2097152);
//			put("BUTTON4", 8388608);
//			put("BUTTON5", 33554432);
//			put("BUTTON_MASK", 45613056);
//			put("MOD1", 262144);
//			put("MOD2", 131072);
//			put("MOD3", 65536);
//			put("MOD4", 0);
//			put("KEYCODE_BIT", 16777216);
//			put("KEY_MASK", 16842751);
				put("ARROW_UP", 16777217);
				put("ARROW_DOWN", 16777218);
				put("ARROW_LEFT", 16777219);
				put("ARROW_RIGHT", 16777220);
				put("PAGE_UP", 16777221);
				put("PAGE_DOWN", 16777222);
				put("HOME", 16777223);
				put("END", 16777224);
				put("INSERT", 16777225);
//			put("F1", 16777226);
//			put("F2", 16777227);
//			put("F3", 16777228);
//			put("F4", 16777229);
//			put("F5", 16777230);
//			put("F6", 16777231);
//			put("F7", 16777232);
//			put("F8", 16777233);
//			put("F9", 16777234);
//			put("F10", 16777235);
//			put("F11", 16777236);
//			put("F12", 16777237);
//			put("F13", 16777238);
//			put("F14", 16777239);
//			put("F15", 16777240);
//			put("F16", 16777241);
//			put("F17", 16777242);
//			put("F18", 16777243);
//			put("F19", 16777244);
//			put("F20", 16777245);
//			put("KEYPAD_MULTIPLY", 16777258);
//			put("KEYPAD_ADD", 16777259);
//			put("KEYPAD_SUBTRACT", 16777261);
//			put("KEYPAD_DECIMAL", 16777262);
//			put("KEYPAD_DIVIDE", 16777263);
//			put("KEYPAD_0", 16777264);
//			put("KEYPAD_1", 16777265);
//			put("KEYPAD_2", 16777266);
//			put("KEYPAD_3", 16777267);
//			put("KEYPAD_4", 16777268);
//			put("KEYPAD_5", 16777269);
//			put("KEYPAD_6", 16777270);
//			put("KEYPAD_7", 16777271);
//			put("KEYPAD_8", 16777272);
//			put("KEYPAD_9", 16777273);
//			put("KEYPAD_EQUAL", 16777277);
//			put("KEYPAD_CR", 16777296);
//			put("HELP", 16777297);
//			put("CAPS_LOCK", 16777298);
//			put("NUM_LOCK", 16777299);
//			put("SCROLL_LOCK", 16777300);
//			put("PAUSE", 16777301);
//			put("BREAK", 16777302);
//			put("PRINT_SCREEN", 16777303);
				put("SEPARATOR", 2);
				put("PUSH", 8);
				put("RADIO", 16);
				put("CHECK", 32);
				put("ARROW", 4);
				put("TOGGLE", 2);
				put("BORDER", 2048);
				put("CLIP_CHILDREN", 4096);
				put("CLIP_SIBLINGS", 8192);
				put("FLAT", 8388608);
//			put("SMOOTH", 65536);
//			put("NO_BACKGROUND", 262144);
//			put("NO_FOCUS", 524288);
//			put("NO_REDRAW_RESIZE", 1048576);
//			put("NO_MERGE_PAINTS", 2097152);
//			put("NO_RADIO_GROUP", 4194304);
//			put("LEFT_TO_RIGHT", 33554432);
//			put("RIGHT_TO_LEFT", 67108864);
//			put("MIRRORED", 134217728);
//			put("H_SCROLL", 256);
//			put("V_SCROLL", 512);
//			put("NO_SCROLL", 16);
//			put("READ_ONLY", 8);
//			put("WRAP", 64);
//			put("SEARCH", 128);
//			put("SIMPLE", 64);
				put("BAR", 2);
				put("POP_UP", 8);
				put("DROP_DOWN", 4);
//			put("CASCADE", 64);
//			put("SINGLE", 4);
//			put("MULTI", 2);
//			put("PASS_WORD", 4194304);
//			put("TOOL", 4);
//			put("NO_TRIM", 8);
//			put("RESIZE", 16);
//			put("TITLE", 32);
//			put("CLOSE", 64);
//			put("MIN", 128);
//			put("MAX", 1024);
//			put("ON_TOP", 16384);
//			put("SHEET", 268435456);
//			put("SHELL_TRIM", 1264);
//			put("DIALOG_TRIM", 2144);
//			put("MODELESS", 0);
//			put("PRIMARY_MODAL", 32768);
//			put("APPLICATION_MODAL", 65536);
//			put("SYSTEM_MODAL", 131072);
//			put("SHADOW_IN", 4);
//			put("SHADOW_OUT", 8);
//			put("SHADOW_ETCHED_IN", 16);
//			put("SHADOW_ETCHED_OUT", 64);
//			put("SHADOW_NONE", 32);
//			put("HIDE_SELECTION", 32768);
//			put("FULL_SELECTION", 65536);
//			put("INDETERMINATE", 2);
//			put("VIRTUAL", 268435456);
//			put("DOUBLE_BUFFERED", 536870912);
//			put("ERROR", 1);
//			put("PAUSED", 4);
				put("NORMAL", 0);
				put("BOLD", 1);
				put("ITALIC", 2);
//			put("CURSOR_ARROW", 0);
//			put("CURSOR_WAIT", 1);
//			put("CURSOR_CROSS", 2);
//			put("CURSOR_APPSTARTING", 3);
//			put("CURSOR_HELP", 4);
//			put("CURSOR_SIZEALL", 5);
//			put("CURSOR_SIZENESW", 6);
//			put("CURSOR_SIZENS", 7);
//			put("CURSOR_SIZENWSE", 8);
//			put("CURSOR_SIZEWE", 9);
//			put("CURSOR_SIZEN", 10);
//			put("CURSOR_SIZES", 11);
//			put("CURSOR_SIZEE", 12);
//			put("CURSOR_SIZEW", 13);
//			put("CURSOR_SIZENE", 14);
//			put("CURSOR_SIZESE", 15);
//			put("CURSOR_SIZESW", 16);
//			put("CURSOR_SIZENW", 17);
//			put("CURSOR_UPARROW", 18);
//			put("CURSOR_IBEAM", 19);
//			put("CURSOR_NO", 20);
//			put("CURSOR_HAND", 21);
//			put("CAP_FLAT", 1);
//			put("CAP_ROUND", 2);
//			put("CAP_SQUARE", 3);
//			put("JOIN_MITER", 1);
//			put("JOIN_ROUND", 2);
//			put("JOIN_BEVEL", 3);
//			put("DRAW_TRANSPARENT", 1);
//			put("DRAW_DELIMITER", 2);
//			put("DRAW_TAB", 4);
//			put("DRAW_MNEMONIC", 8);
//			put("PATH_MOVE_TO", 1);
//			put("PATH_LINE_TO", 2);
//			put("PATH_QUAD_TO", 3);
//			put("PATH_CUBIC_TO", 4);
//			put("PATH_CLOSE", 5);
//			put("ICON_ERROR", 1);
//			put("ICON_INFORMATION", 2);
//			put("ICON_QUESTION", 4);
//			put("ICON_WARNING", 8);
//			put("ICON_WORKING", 16);
//			put("ICON_SEARCH", 512);
//			put("ICON_CANCEL", 256);
//			put("OK", 32);
//			put("YES", 64);
//			put("NO", 128);
//			put("CANCEL", 256);
//			put("ABORT", 512);
//			put("RETRY", 1024);
//			put("IGNORE", 2048);
//			put("OPEN", 4096);
//			put("SAVE", 8192);
//			put("INHERIT_NONE", 0);
//			put("INHERIT_DEFAULT", 1);
//			put("INHERIT_FORCE", 2);
//				put("COLOR_WHITE", 1);
//				put("COLOR_BLACK", 2);
//				put("COLOR_RED", 3);
//				put("COLOR_DARK_RED", 4);
//				put("COLOR_GREEN", 5);
//				put("COLOR_DARK_GREEN", 6);
//				put("COLOR_YELLOW", 7);
//				put("COLOR_DARK_YELLOW", 8);
//				put("COLOR_BLUE", 9);
//				put("COLOR_DARK_BLUE", 10);
//				put("COLOR_MAGENTA", 11);
//				put("COLOR_DARK_MAGENTA", 12);
//				put("COLOR_CYAN", 13);
//				put("COLOR_DARK_CYAN", 14);
//				put("COLOR_GRAY", 15);
//				put("COLOR_DARK_GRAY", 16);
				put("COLOR_WIDGET_DARK_SHADOW", 17);
				put("COLOR_WIDGET_NORMAL_SHADOW", 18);
				put("COLOR_WIDGET_LIGHT_SHADOW", 19);
				put("COLOR_WIDGET_HIGHLIGHT_SHADOW", 20);
				put("COLOR_WIDGET_FOREGROUND", 21);
				put("COLOR_WIDGET_BACKGROUND", 22);
				put("COLOR_WIDGET_BORDER", 23);
				put("COLOR_LIST_FOREGROUND", 24);
				put("COLOR_LIST_BACKGROUND", 25);
				put("COLOR_LIST_SELECTION", 26);
				put("COLOR_LIST_SELECTION_TEXT", 27);
				put("COLOR_INFO_FOREGROUND", 28);
				put("COLOR_INFO_BACKGROUND", 29);
				put("COLOR_TITLE_FOREGROUND", 30);
				put("COLOR_TITLE_BACKGROUND", 31);
				put("COLOR_TITLE_BACKGROUND_GRADIENT", 32);
				put("COLOR_TITLE_INACTIVE_FOREGROUND", 33);
				put("COLOR_TITLE_INACTIVE_BACKGROUND", 34);
				put("COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT", 35);
				put("COLOR_TRANSPARENT", 37);
//			put("ERROR_UNSPECIFIED", 1);
//			put("ERROR_NO_HANDLES", 2);
//			put("ERROR_NO_MORE_CALLBACKS", 3);
//			put("ERROR_NULL_ARGUMENT", 4);
//			put("ERROR_INVALID_ARGUMENT", 5);
//			put("ERROR_INVALID_RANGE", 6);
//			put("ERROR_CANNOT_BE_ZERO", 7);
//			put("ERROR_CANNOT_GET_ITEM", 8);
//			put("ERROR_CANNOT_GET_SELECTION", 9);
//			put("ERROR_CANNOT_INVERT_MATRIX", 10);
//			put("ERROR_CANNOT_GET_ITEM_HEIGHT", 11);
//			put("ERROR_CANNOT_GET_TEXT", 12);
//			put("ERROR_CANNOT_SET_TEXT", 13);
//			put("ERROR_ITEM_NOT_ADDED", 14);
//			put("ERROR_ITEM_NOT_REMOVED", 15);
//			put("ERROR_NO_GRAPHICS_LIBRARY", 16);
//			put("ERROR_NOT_IMPLEMENTED", 20);
//			put("ERROR_MENU_NOT_DROP_DOWN", 21);
//			put("ERROR_THREAD_INVALID_ACCESS", 22);
//			put("ERROR_WIDGET_DISPOSED", 24);
//			put("ERROR_MENUITEM_NOT_CASCADE", 27);
//			put("ERROR_CANNOT_SET_SELECTION", 28);
//			put("ERROR_CANNOT_SET_MENU", 29);
//			put("ERROR_CANNOT_SET_ENABLED", 30);
//			put("ERROR_CANNOT_GET_ENABLED", 31);
//			put("ERROR_INVALID_PARENT", 32);
//			put("ERROR_MENU_NOT_BAR", 33);
//			put("ERROR_CANNOT_GET_COUNT", 36);
//			put("ERROR_MENU_NOT_POP_UP", 37);
//			put("ERROR_UNSUPPORTED_DEPTH", 38);
//			put("ERROR_IO", 39);
//			put("ERROR_INVALID_IMAGE", 40);
//			put("ERROR_UNSUPPORTED_FORMAT", 42);
//			put("ERROR_INVALID_SUBCLASS", 43);
//			put("ERROR_GRAPHIC_DISPOSED", 44);
//			put("ERROR_DEVICE_DISPOSED", 45);
//			put("ERROR_FAILED_EXEC", 46);
//			put("ERROR_FAILED_LOAD_LIBRARY", 47);
//			put("ERROR_INVALID_FONT", 48);
//			put("ERROR_FUNCTION_DISPOSED", 49);
//			put("ERROR_FAILED_EVALUATE", 50);
//			put("ERROR_INVALID_RETURN_VALUE", 51);
//			put("IMAGE_COPY", 0);
//			put("IMAGE_DISABLE", 1);
//			put("IMAGE_GRAY", 2);
//			put("IMAGE_UNDEFINED", -1);
//			put("IMAGE_BMP", 0);
//			put("IMAGE_BMP_RLE", 1);
//			put("IMAGE_GIF", 2);
//			put("IMAGE_ICO", 3);
//			put("IMAGE_JPEG", 4);
//			put("IMAGE_PNG", 5);
//			put("IMAGE_TIFF", 6);
//			put("IMAGE_OS2_BMP", 7);
//			put("DM_UNSPECIFIED", 0);
//			put("DM_FILL_NONE", 1);
//			put("DM_FILL_BACKGROUND", 2);
//			put("DM_FILL_PREVIOUS", 3);
//			put("TRANSPARENCY_NONE", 0);
//			put("TRANSPARENCY_ALPHA", 1);
//			put("TRANSPARENCY_MASK", 2);
//			put("TRANSPARENCY_PIXEL", 4);
//			put("DATE", 32);
//			put("TIME", 128);
//			put("CALENDAR", 1024);
//			put("SHORT", 32768);
//			put("MEDIUM", 65536);
//			put("LONG", 268435456);
//			put("MOZILLA", 32768);
//			put("WEBKIT", 65536);
//			put("ID_ABOUT", -1);
//			put("ID_PREFERENCES", -2);
//			put("ID_HIDE", -3);
//			put("ID_HIDE_OTHERS", -4);
//			put("ID_SHOW_ALL", -5);
//			put("ID_QUIT", -6);
			}
		};

		WIDGET_TO_SWT_MAP = new HashMap() {
			{
				put(Button.class, buttonMap);
				put(ToolItem.class, toolItemMap);
				put(ToolBar.class, toolBarMap);
				put(Text.class, textMap);
				put(Label.class, labelMap);
				put(TabFolder.class, tabFolderMap);
				put(Group.class, groupMap);
				put(Shell.class, shellMap);
				put(Combo.class, comboMap);
				put(CCombo.class, ccomboMap);
				put(Hyperlink.class, hyperlinkMap);
				put(CoolBar.class, coolBarMap);
				put(CoolItem.class, coolItemMap);
			}
		};
	}
}
