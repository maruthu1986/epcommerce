/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.misc.TimeService;

/**
 * Tests the logic contained in ChangeSetHelper.
 */
public class ChangeSetHelperTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	private ChangeSetHelper changeSetHelper;

	@Mock
	private TimeService timeService;

	@Mock
	private ChangeSet activeChangeSet;

	private static final String CMUSER_GUID = "CMUSER";  //$NON-NLS-1$

	/**
	 * Common setup for tests.
	 *
	 * @throws java.lang.Exception in case of error during setup
	 */
	@Before
	public void setUp() throws Exception {
		// Instance of the class under test with mock active change set
		changeSetHelper = new ChangeSetHelper() {

			@Override
			public ChangeSet getActiveChangeSet() {
				return activeChangeSet;
			}

			@Override
			protected String getCmUserGuid() {
				return CMUSER_GUID;
			}
		};
		changeSetHelper.setTimeService(timeService);
	}

	/**
	 * Test that different locales don't result in different metadata.
	 */
	@Test
	public void testMetaDataWithDifferentLocales() {
		final Date dateAdded = new Date();
		when(timeService.getCurrentTime()).thenReturn(dateAdded);

		Locale.setDefault(Locale.ENGLISH);
		Map<String, String> metadataEN = changeSetHelper.buildChangeSetMetadataMap(ChangeSetMemberAction.ADD);
		Locale.setDefault(Locale.FRENCH);
		Map<String, String> metadataFR = changeSetHelper.buildChangeSetMetadataMap(ChangeSetMemberAction.ADD);
		assertEquals("Stored date value should be independent of locale",  //$NON-NLS-1$
				metadataEN.get("dateAdded"), metadataFR.get("dateAdded"));   //$NON-NLS-1$//$NON-NLS-2$
	}

}
