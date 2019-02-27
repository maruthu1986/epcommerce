package com.elasticpath.selenium.util;

/**
 * Global constants.
 */
public class Constants {
	/**
	 * UUID_END_INDEX. Specifies the maximum length of the randomly generated UUID that should be used.
	 */
	public static final int UUID_END_INDEX = 5;
	/**
	 * Half second sleep timer.
	 */
	public static final int SLEEP_HALFSECOND_IN_MILLIS = 500;
	/**
	 * Hundred milliseconds sleep timer.
	 */
	public static final int SLEEP_HUNDRED_MILLI_SECONDS = 100;
	/**
	 * One second sleep timer.
	 */
	public static final int SLEEP_ONE_SECOND_IN_MILLIS = 1000;
	/**
	 * Five second sleep timer.
	 */
	public static final int SLEEP_FIVE_SECONDS_IN_MILLIS = 5000;
	/**
	 * One hundred millisecond sleep timer.
	 */
	public static final int SLEEP_ONE_HUNDRED_MILLISECOND = 100;
	/**
	 * Wait interval(in sec) to verify an element does not exist.
	 */
	public static final int IMPLICIT_WAIT_FOR_ELEMENT_NOT_EXISTS = 1;
	/**
	 * Number of tries.
	 */
	public static final int RETRY_COUNTER_3 = 3;
	/**
	 * Number of tries.
	 */
	public static final int RETRY_COUNTER_5 = 5;
	/**
	 * Wait interval(in sec).
	 */
	public static final int IMPLICIT_WAIT_FOR_ELEMENT_THREE_SECONDS = 3;
	/**
	 * Wait interval(in sec).
	 */
	public static final int IMPLICIT_WAIT_FOR_ELEMENT_FIVE_SECONDS = 5;


	//---------------------------------------------------------------------------------------------------------
	//ORDER NUMBERS
	/**
	 * Default order value is 10000
	 * For After hooks, higher order number run first.
	 * For before hooks, lower order numbers are run first
	 */

	/**
	 * Order number for taking screenshot.
	 */
	public static final int SCREENSHOT_ORDER_NUMBER = 99999;

	/**
	 * First clean up.
	 */
	public static final int CLEANUP_ORDER_FIRST = 10001;

	/**
	 * Second clean up.
	 */
	public static final int CLEANUP_ORDER_SECOND = 9999;

	/**
	 * Third clean up.
	 */
	public static final int CLEANUP_ORDER_THIRD = 9998;

	/**
	 * Fourth clean up.
	 */
	public static final int CLEANUP_ORDER_FOURTH = 9997;

}
