/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.changeset.dao.impl.ChangeSetDaoImpl;

/**
 * Integration test for {@link ChangeSetDaoImpl}
 */
public class ChangeSetDAOImplIntegrationTest extends BasicSpringContextTest {

	@Autowired
	private ChangeSetDao changeSetDao;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private ChangeSetManagementService changeSetManagementService;


	@Test
	@DirtiesDatabase
	public void testFindChangeSetByNameAndStatesDoesExist() {

		// Given
		String changeSetName = "SOME NAME";
		List<ChangeSetStateCode> changeSetStateCodes = Arrays.asList(ChangeSetStateCode.FINALIZED, ChangeSetStateCode.LOCKED);
		ChangeSetStateCode changeSetStateCode = ChangeSetStateCode.FINALIZED;

		givenChangeSetIsCreated(changeSetName, changeSetStateCode);

		// When
		boolean exists = changeSetDao.findChangeSetExistsByStateAndName(changeSetName, changeSetStateCodes);

		// Then
		assertTrue(exists);
	}

	@Test
	@DirtiesDatabase
	public void testFindChangeSetByNameAndStatesRightStateButWrongName() {

		// Given
		String changeSetName = "SOME NAME";
		List<ChangeSetStateCode> changeSetStateCodes = Arrays.asList(ChangeSetStateCode.FINALIZED, ChangeSetStateCode.LOCKED);
		ChangeSetStateCode changeSetStateCode = ChangeSetStateCode.FINALIZED;

		givenChangeSetIsCreated(changeSetName, changeSetStateCode);

		// When
		boolean exists = changeSetDao.findChangeSetExistsByStateAndName("ANOTHER NAME", changeSetStateCodes);

		// Then
		assertFalse(exists);
	}

	@Test
	@DirtiesDatabase
	public void testFindChangeSetByNameAndStatesRightNameButWrongState() {

		// Given
		String changeSetName = "SOME NAME";
		List<ChangeSetStateCode> changeSetStateCodes = Arrays.asList(ChangeSetStateCode.FINALIZED, ChangeSetStateCode.LOCKED);
		ChangeSetStateCode changeSetStateCode = ChangeSetStateCode.OPEN;

		givenChangeSetIsCreated(changeSetName, changeSetStateCode);

		// When
		boolean exists = changeSetDao.findChangeSetExistsByStateAndName(changeSetName, changeSetStateCodes);

		// Then
		assertFalse(exists);
	}

	@Test
	@DirtiesDatabase
	public void testFindChangeSetByNameWithBothStatesAndNameBeingWrong() {

		// Given
		String changeSetName = "SOME NAME";
		List<ChangeSetStateCode> changeSetStateCodes = Arrays.asList(ChangeSetStateCode.FINALIZED, ChangeSetStateCode.LOCKED);
		ChangeSetStateCode changeSetStateCode = ChangeSetStateCode.OPEN;

		givenChangeSetIsCreated(changeSetName, changeSetStateCode);

		// When
		boolean exists = changeSetDao.findChangeSetExistsByStateAndName("ANOTHER NAME", changeSetStateCodes);

		// Then
		assertFalse(exists);
	}

	private void givenChangeSetIsCreated(final String changeSetName, final ChangeSetStateCode changeSetStateCode) {
		final ChangeSet changeSet = beanFactory.getBean(ContextIdNames.CHANGE_SET);
		changeSet.setName(changeSetName);
		changeSet.setStateCode(changeSetStateCode);
		changeSet.setCreatedByUserGuid("Admin");
		changeSet.setCreatedDate(new Date());

		changeSetManagementService.add(changeSet);
	}
}
