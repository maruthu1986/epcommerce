package com.elasticpath.selenium.navigations;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.resultspane.ChangeSetSearchResultPane;
import com.elasticpath.selenium.util.Constants;

/**
 * Change Set.
 */
public class ChangeSet extends AbstractNavigation {

	private static final String LEFT_PANE_INNER_CSS = "div[pane-location='left-pane-inner'][seeable='true'] ";
	private static final String SEARCH_BUTTON_CSS
			= LEFT_PANE_INNER_CSS + "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetSearchView_SearchButton']";
	private static final String CLEAR_BUTTON_CSS
			= LEFT_PANE_INNER_CSS + "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetSearchView_ClearButton']";
	private static final String CHANGE_SET_NAME_INPUT_CSS =
			LEFT_PANE_INNER_CSS + "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetEditor_ChangeSet_Name'] > input";
	private static final String CHANGESET_STATE_DROPDOWN_CSS = "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages."
			+ "ChangeSetState_ComboLabel'][widget-type='CCombo']";
	private static final int RETRY_COUNTER = 5;


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangeSet(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Search button.
	 *
	 * @return ChangeSetSearchResultPane
	 */
	public ChangeSetSearchResultPane clickSearchButton() {
		clickButton(SEARCH_BUTTON_CSS, "Search");
		return new ChangeSetSearchResultPane(getDriver());
	}

	/**
	 * Clicks Clear button.
	 */
	public void clickClearButton() {
		clickButton(CLEAR_BUTTON_CSS, "Clear");
	}

	/**
	 * Enters change set name and clicks Search button.
	 *
	 * @param changeSetName the change set name
	 * @return ChangeSetSearchResultPane
	 */
	public ChangeSetSearchResultPane searchChangeSetByName(final String changeSetName) {
		waitForElementToLoad(getDriver().findElement(By.cssSelector(CHANGE_SET_NAME_INPUT_CSS)));
		clearAndType(CHANGE_SET_NAME_INPUT_CSS, changeSetName);
		int counter = 0;
		while (!getDriver().findElement(By.cssSelector(CHANGE_SET_NAME_INPUT_CSS)).getAttribute("value").contains(changeSetName)
				&& counter < RETRY_COUNTER) {
			sleep(Constants.SLEEP_HALFSECOND_IN_MILLIS);
			clearAndType(CHANGE_SET_NAME_INPUT_CSS, changeSetName);
			counter++;
		}
		assertThat(getDriver().findElement(By.cssSelector(CHANGE_SET_NAME_INPUT_CSS)).getAttribute("value").contains(changeSetName))
				.as("Change set name in search text box is not as expected")
				.isTrue();

		//Ensure we always search for All States.
		selectChangeSetState("All States");
		clickSearchButton();
		return new ChangeSetSearchResultPane(getDriver());
	}

	/**
	 * selects the change set state for search.
	 *
	 * @param state the state.
	 */
	public void selectChangeSetState(final String state) {
		assertThat(selectComboBoxItem(CHANGESET_STATE_DROPDOWN_CSS, state))
				.as("Unable to find state - " + state)
				.isTrue();
	}

}
