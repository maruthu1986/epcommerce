package com.elasticpath.selenium.editor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * RMA Editor.
 */
public class RmaEditor extends AbstractPageObject {

	private static final String EDITOR_PANE_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String EDITOR_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='%s'][seeable='true']";
	private static final String ITEMS_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order Return Details Table'] ";
	private static final String RETURNED_QUANTITY_COLUMN_CSS = ITEMS_TABLE_PARENT_CSS + "div[row-id='%s'] div[column-num='4']";
	private static final String RETURNED_QUANTITY_COLUMN_INPUT_CSS = ITEMS_TABLE_PARENT_CSS + " input:not([readonly])";
	private static final String RETURNED_STATE_COLUMN_CSS = ITEMS_TABLE_PARENT_CSS + "div[row-id='%s'] div[column-num='5']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public RmaEditor(final WebDriver driver) {
		super(driver);
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(EDITOR_PANE_PARENT_CSS));
	}

	/**
	 * Enters returned quantity and default state.
	 *
	 * @param returnedSku      String
	 * @param returnedQuantity String
	 */
	public void setReturnedQuantity(final String returnedSku, final String returnedQuantity) {
		click(getDriver().findElement(By.cssSelector(String.format(RETURNED_QUANTITY_COLUMN_CSS, returnedSku))));
		clearAndType(RETURNED_QUANTITY_COLUMN_INPUT_CSS, returnedQuantity);
		click(getDriver().findElement(By.cssSelector(String.format(RETURNED_STATE_COLUMN_CSS, returnedSku))));
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonWidgetId String the widget id
	 */
	public void clickEditorButton(final String buttonWidgetId) {
		scrollWidgetIntoView(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
		getWaitDriver().waitForElementToBeInteractable(String.format(EDITOR_BUTTON_CSS, buttonWidgetId));
		clickButton(String.format(EDITOR_BUTTON_CSS, buttonWidgetId), buttonWidgetId);
	}

	/**
	 * Clicks Open Original Order button.
	 *
	 * @return OrderEditor
	 */
	public OrderEditor clickOpenOriginalOrderButton() {
		clickEditorButton("Open Original Order...");
		return new OrderEditor(getDriver());
	}
}
