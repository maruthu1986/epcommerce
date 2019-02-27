/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.core.messaging.changeset.ChangeSetEventType;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetMutator;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.changeset.impl.ChangeSetImpl;
import com.elasticpath.domain.changeset.impl.ChangeSetMemberImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectGroupMember;
import com.elasticpath.domain.objectgroup.BusinessObjectMetadata;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectGroupMemberImpl;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectMetadataImpl;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.service.changeset.dao.ChangeSetDao;
import com.elasticpath.service.changeset.dao.ChangeSetMemberDao;
import com.elasticpath.service.changeset.helper.ChangeSetHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for {@link ChangeSetManagementServiceImpl}.
 */
public class ChangeSetManagementServiceImplTest {

	private static final String CHANGE_SET_GUID = "changeSetGuid";

	private static final String GROUP_ID = "groupId1";

	private ChangeSetManagementServiceImpl changeSetManagementService;
	private ChangeSetMemberDao changeSetMemberDao;
	private TimeService timeService;
	private EventMessageFactory eventMessageFactory;
	private EventMessagePublisher eventMessagePublisher;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private ChangeSetDao changeSetDao;
	private BeanFactory beanFactory;

	private ChangeSetHelper changeSetHelper;

	private ChangeSetPolicy changeSetPolicy;

	private BeanFactoryExpectationsFactory expectationsFactory;


	/**
	 * Sets up a test case.
	 */
	@Before
	public void setUp() {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		changeSetManagementService = new ChangeSetManagementServiceImpl();
		changeSetDao = context.mock(ChangeSetDao.class);
		timeService = context.mock(TimeService.class);
		changeSetMemberDao = context.mock(ChangeSetMemberDao.class);
		changeSetHelper = context.mock(ChangeSetHelper.class);
		changeSetPolicy = context.mock(ChangeSetPolicy.class);
		eventMessageFactory = context.mock(EventMessageFactory.class);
		eventMessagePublisher = context.mock(EventMessagePublisher.class);

		changeSetManagementService.setChangeSetDao(changeSetDao);
		changeSetManagementService.setTimeService(timeService);
		changeSetManagementService.setChangeSetMemberDao(changeSetMemberDao);
		changeSetManagementService.setChangeSetHelper(changeSetHelper);
		changeSetManagementService.setChangeSetPolicy(changeSetPolicy);
		changeSetManagementService.setEventMessageFactory(eventMessageFactory);
		changeSetManagementService.setChangeSetEventMessagePublisher(eventMessagePublisher);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that adding a change set involves generating a group ID 
	 * and adding the change set to the data store using the corresponding DAO.
	 */
	@Test
	public void testAddChangeSet() {
		final String groupId = "groupId1";
		final ChangeSet changeSet = new ChangeSetImpl();
		context.checking(new Expectations() { {

			oneOf(timeService).getCurrentTime();
			will(returnValue(new Date()));

			oneOf(changeSetMemberDao).generateChangeSetGroupId();
			will(returnValue(groupId));

			oneOf(changeSetDao).add(changeSet);

		} });

		// set the mandatory creator
		changeSet.setCreatedByUserGuid("sampleGUID");

		// invoke the method to add
		changeSetManagementService.add(changeSet);
		assertEquals(groupId, changeSet.getObjectGroupId());

	}

	/**
	 * Tests that when removing a change set we call the DAO to do so and then
	 * we remove all the object group members.
	 */
	@Test
	public void testRemoveChangeSet() {
		final String objectGroupId = "sampleGroupId";

		context.checking(new Expectations() { {

			oneOf(changeSetDao).remove(objectGroupId);

			oneOf(changeSetPolicy).canRemove(objectGroupId);
			will(returnValue(true));
		} });

		changeSetManagementService.remove(objectGroupId);

	}

	/**
	 * Tests that update() populates the change set with member objects.
	 */
	@Test
	public void testUpdateChangeSet() {
		final ChangeSet changeSet = new ChangeSetImpl();
		((ChangeSetMutator) changeSet).setObjectGroupId(GROUP_ID);

		final Collection<BusinessObjectMetadata> metadataCollection = new HashSet<>();

		final BusinessObjectGroupMember member1 = new BusinessObjectGroupMemberImpl();

		final BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		metadata.setBusinessObjectGroupMember(member1);

		metadataCollection.add(metadata);

		context.checking(new Expectations() { {
			oneOf(changeSetPolicy).isChangeAllowed(GROUP_ID);
			will(returnValue(true));

			oneOf(changeSetDao).update(changeSet);
			will(returnValue(changeSet));

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupId(GROUP_ID);
			will(returnValue(metadataCollection));

			oneOf(changeSetHelper).convertGroupMembersToChangeSetMembers(Arrays.asList(member1), metadataCollection);
			will(returnValue(Arrays.asList(new ChangeSetMemberImpl())));

			oneOf(changeSetMemberDao).findGroupMembersByGroupId(GROUP_ID);
			will(returnValue(Arrays.asList(member1)));
		} });

		final ChangeSet updatedChangeSet = changeSetManagementService.update(changeSet, null);

		assertEquals(1, updatedChangeSet.getMemberObjects().size());

	}

	/**
	 * Tests that get() populates the change set with member objects.
	 */
	@Test
	public void testGetChangeSet() {
		final ChangeSet changeSet = new ChangeSetImpl();
		((ChangeSetMutator) changeSet).setObjectGroupId(GROUP_ID);

		final Collection<BusinessObjectMetadata> metadataCollection = new HashSet<>();

		final BusinessObjectGroupMember member1 = new BusinessObjectGroupMemberImpl();

		BusinessObjectMetadata metadata = new BusinessObjectMetadataImpl();
		metadata.setBusinessObjectGroupMember(member1);

		metadataCollection.add(metadata);

		context.checking(new Expectations() { {
			oneOf(changeSetDao).findByGuid(GROUP_ID);
			will(returnValue(changeSet));

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupId(GROUP_ID);
			will(returnValue(metadataCollection));

			oneOf(changeSetHelper).convertGroupMembersToChangeSetMembers(Arrays.asList(member1), metadataCollection);
			will(returnValue(Arrays.asList(new ChangeSetMemberImpl())));

			oneOf(changeSetMemberDao).findGroupMembersByGroupId(GROUP_ID);
			will(returnValue(Arrays.asList(member1)));
		} });

		final ChangeSet changeSet1 = changeSetManagementService.get(GROUP_ID, null);

		assertEquals(1, changeSet1.getMemberObjects().size());

	}

	/**
	 * Tests find all change sets.
	 */
	@Test
	public void testFindAllChangeSets() {

		context.checking(new Expectations() { {
			oneOf(changeSetDao).findAllChangeSets();
			will(returnValue(Collections.emptyList()));
		} });

		changeSetManagementService.findAllChangeSets(null);
	}

	/**
	 * Tests find all change sets by user Guid.
	 */
	@Test
	public void testFindAllChangeSetsByUserGuid() {
		final String userGuid = "userGuid";

		context.checking(new Expectations() { {
			oneOf(changeSetDao).findAllChangeSetsByUserGuid(userGuid);
			will(returnValue(Collections.emptyList()));
		} });

		changeSetManagementService.findAllChangeSetsByUserGuid(userGuid, null);
	}

	/**
	 * Tests that if a change set GUID does not point to a real change set an exception will be thrown.
	 */
	@Test(expected = EpServiceException.class)
	public void testUpdateStateWithNonExistingGuid() {
		final String changeSetGuid = CHANGE_SET_GUID;

		context.checking(new Expectations() { {
			oneOf(changeSetDao).findByGuid(changeSetGuid);
			will(returnValue(null));
		} });

		changeSetManagementService.updateState(changeSetGuid, ChangeSetStateCode.LOCKED, null);

	}

	/**
	 * Tests that if a change set will be updated when updateState() is invoked.
	 */
	@Test
	public void testUpdateState() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);

		context.checking(new Expectations() { {
			oneOf(changeSet).setStateCode(ChangeSetStateCode.LOCKED);

			oneOf(changeSet).getStateCode();
			will(returnValue(ChangeSetStateCode.OPEN));
		} });

		commonUpdateStateContextChecking(CHANGE_SET_GUID, changeSet);

		changeSetManagementService.updateState(CHANGE_SET_GUID, ChangeSetStateCode.LOCKED, null);

	}

	/**
	 * Tests that if the arguments are not specified an {@link IllegalArgumentException} will be thrown.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testUpdateStateNullArguments() {
		changeSetManagementService.updateState(null, null, null);
	}

	/**
	 * Tests that message is published when states changes to ready to publish.
	 */
	public void testUpdateStateToReadyToPublish() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);
		final EventMessage changeSetEventMessage = context.mock(EventMessage.class);

		context.checking(new Expectations() { {
			oneOf(changeSet).setStateCode(ChangeSetStateCode.READY_TO_PUBLISH);

			oneOf(eventMessageFactory).createEventMessage(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, CHANGE_SET_GUID);
			will(returnValue(changeSetEventMessage));

			oneOf(eventMessagePublisher).publish(changeSetEventMessage);
		} });

		commonUpdateStateContextChecking(CHANGE_SET_GUID, changeSet);

		changeSetManagementService.updateState(CHANGE_SET_GUID, ChangeSetStateCode.READY_TO_PUBLISH, null);
	}

	/**
	 * Test transition to locked does not publish a changeset event.
	 */
	public void testUpdateStateToLockedDoesNotPublishChangesetEvent() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);

		context.checking(new Expectations() { {
			oneOf(changeSet).setStateCode(ChangeSetStateCode.LOCKED);
			exactly(0).of(eventMessageFactory).createEventMessage(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, CHANGE_SET_GUID);
		} });

		commonUpdateStateContextChecking(CHANGE_SET_GUID, changeSet);

		changeSetManagementService.updateState(CHANGE_SET_GUID, ChangeSetStateCode.LOCKED, null);
	}

	/**
	 * Test transition to finalized does not publish a changeset event.
	 */
	public void testUpdateStateToFinalizedDoesNotPublishChangesetEvent() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);

		context.checking(new Expectations() { {
			oneOf(changeSet).setStateCode(ChangeSetStateCode.FINALIZED);
			exactly(0).of(eventMessageFactory).createEventMessage(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, CHANGE_SET_GUID);
		} });

		commonUpdateStateContextChecking(CHANGE_SET_GUID, changeSet);

		changeSetManagementService.updateState(CHANGE_SET_GUID, ChangeSetStateCode.FINALIZED, null);
	}

	/**
	 * Tests that if event message fails to publish then {@link EpSystemException} will be thrown.
	 */
	@Test(expected = EpSystemException.class)
	public void testUpdateStateToReadyToPublishWhenEventMessageFailsToPublish() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);
		final EventMessage changeSetEventMessage = context.mock(EventMessage.class);

		context.checking(new Expectations() { {
			oneOf(changeSet).setStateCode(ChangeSetStateCode.READY_TO_PUBLISH);

			oneOf(changeSet).getStateCode();
			will(returnValue(ChangeSetStateCode.OPEN));

			oneOf(eventMessageFactory).createEventMessage(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, CHANGE_SET_GUID);
			will(returnValue(changeSetEventMessage));

			oneOf(eventMessagePublisher).publish(changeSetEventMessage);
			will(throwException(new Exception()));
		} });

		commonUpdateStateContextChecking(CHANGE_SET_GUID, changeSet);

		changeSetManagementService.updateState(CHANGE_SET_GUID, ChangeSetStateCode.READY_TO_PUBLISH, null);
	}

	@Test
	public void testEventMessageIsSentWithDefaultStrategyWithoutPayload() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);
		final EventMessage changeSetEventMessage = context.mock(EventMessage.class);

		context.checking(new Expectations() { {
			allowing(changeSet).getGuid();
			will(returnValue(CHANGE_SET_GUID));

			oneOf(eventMessageFactory).createEventMessage(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, CHANGE_SET_GUID);
			will(returnValue(changeSetEventMessage));

			oneOf(eventMessagePublisher).publish(changeSetEventMessage);
		} });

		changeSetManagementService.publishMessageStrategy(changeSet, null, ChangeSetStateCode.READY_TO_PUBLISH, null);
	}

	@Test
	public void testEventMessageIsSentWithDefaultStrategyWithPayload() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);
		final EventMessage changeSetEventMessage = context.mock(EventMessage.class);
		final HashMap<String, Object> notificationPayload = new HashMap<>();

		context.checking(new Expectations() { {
			allowing(changeSet).getGuid();
			will(returnValue(CHANGE_SET_GUID));

			oneOf(eventMessageFactory).createEventMessage(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, CHANGE_SET_GUID, notificationPayload);
			will(returnValue(changeSetEventMessage));

			oneOf(eventMessagePublisher).publish(changeSetEventMessage);
		} });

		changeSetManagementService.publishMessageStrategy(changeSet, null, ChangeSetStateCode.READY_TO_PUBLISH, notificationPayload);
	}

	@Test
	public void testEventMessageIsNotSentWithDefaultStrategy() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);

		changeSetManagementService.publishMessageStrategy(changeSet, null, ChangeSetStateCode.LOCKED, null);
	}

	@Test(expected = EpSystemException.class)
	public void testEventMessageSendingThrowsException() {
		final ChangeSetImpl changeSet = context.mock(ChangeSetImpl.class);

		context.checking(new Expectations() { {
			allowing(changeSet).getGuid();
			will(returnValue(CHANGE_SET_GUID));

			oneOf(eventMessageFactory).createEventMessage(ChangeSetEventType.CHANGE_SET_READY_FOR_PUBLISH, CHANGE_SET_GUID);
			will(throwException(new EpServiceException("Error")));
		} });

		changeSetManagementService.publishMessageStrategy(changeSet, null, ChangeSetStateCode.READY_TO_PUBLISH, null);
	}

	private void commonUpdateStateContextChecking(final String changeSetGuid, final ChangeSetImpl changeSet) {
		context.checking(new Expectations() { {
			allowing(changeSet).getGuid();
			will(returnValue(changeSetGuid));

			oneOf(changeSet).setMemberObjects(Collections.<ChangeSetMember>emptyList());

			oneOf(changeSetDao).findByGuid(changeSetGuid);
			will(returnValue(changeSet));

			oneOf(changeSetDao).update(changeSet);
			will(returnValue(changeSet));

			oneOf(changeSetMemberDao).findGroupMembersByGroupId(changeSetGuid);
			will(returnValue(Collections.emptyList()));

			oneOf(changeSetMemberDao).findBusinessObjectMetadataByGroupId(changeSetGuid);
			will(returnValue(Collections.emptyList()));

			oneOf(changeSetHelper).convertGroupMembersToChangeSetMembers(
					Collections.<BusinessObjectGroupMember>emptyList(),
					Collections.<BusinessObjectMetadata>emptyList());
			will(returnValue(Arrays.<BusinessObjectGroupMember>asList()));

		} });
	}

}
