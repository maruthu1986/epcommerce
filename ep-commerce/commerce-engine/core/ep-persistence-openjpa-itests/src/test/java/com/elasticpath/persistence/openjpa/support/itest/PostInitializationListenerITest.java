/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.openjpa.support.itest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManagerFactory;

import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.persistence.api.support.PostInitializationStrategy;
import com.elasticpath.persistence.impl.PostInitializationListener;
import com.elasticpath.persistence.openjpa.sampledata.Detail;
import com.elasticpath.persistence.openjpa.sampledata.Master;
import com.elasticpath.persistence.openjpa.sampledata.TransientDataHolder;
import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;

/**
 * Note that this itest tests a core class (PostInitializationListener) that really should live in ep-persistence-openjpa
 * but lives in core because of maven dependency issues.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/persistence-openjpa-itest-context.xml")
@TestExecutionListeners({
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class
})
public class PostInitializationListenerITest {
	public static final String POST_LOAD_STRATEGY_BEAN = "post-load-strategy-bean";
	public static final String POST_SET_DATA = "post-set data";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Autowired private PersistenceEngine persistenceEngine;
	@Autowired private EntityManagerFactory entityManagerFactory;
	private BeanFactory mockBeanFactory = context.mock(BeanFactory.class);

	private PostInitializationListener listener;
	private PostInitializationStrategy<Persistable> strategy = new TransientDataPostInitializationStrategy();

	@Before
	public void setUp() {
		context.checking(new Expectations() {
			{
				allowing(mockBeanFactory).getBean(POST_LOAD_STRATEGY_BEAN);
				will(returnValue(strategy));
			}
		});

		listener = new PostInitializationListener();
		listener.setPostInitializationStrategyBeanIds(Arrays.asList(POST_LOAD_STRATEGY_BEAN));
		listener.setBeanFactory(mockBeanFactory);

		OpenJPAEntityManagerFactorySPI spi = (OpenJPAEntityManagerFactorySPI) OpenJPAPersistence.cast(entityManagerFactory);
		spi.addLifecycleListener(listener, (Class[]) null);
	}

	@Test
	public void testPostLoadListenerIsFiredWhenMasterIsPersisted() {
		Master master = persistMaster();
		assertThatPostLoadStrategyWasExecutedOnMaster(master);
	}

	@Test
	public void testPostLoadIntegrationListenerFiredOnLoad() {
		final Master persistedMaster = persistMaster();

		assertNotNull("Sanity Check", persistedMaster.getUidPk());
		Master found = persistenceEngine.load(Master.class, persistedMaster.getUidPk());
		assertThatPostLoadStrategyWasExecutedOnMasterAndDetails(found);
	}

	@Test
	public void testPostLoadStrategiesAreFiredOnMerge() {
		final Master persistedMaster = persistMaster();

		assertNotNull("Sanity Check", persistedMaster.getUidPk());
		Master found = persistenceEngine.load(Master.class, persistedMaster.getUidPk());
		found.setName("A different name");
		found.setTransientData(null);

		Transaction updateTx = persistenceEngine.getSharedPersistenceSession().beginTransaction();
		try {
			Master updated = persistenceEngine.update(found);
			assertThatPostLoadStrategyWasExecutedOnMasterAndDetails(updated);
		} finally {
			if (updateTx.isRollbackOnly()) {
				updateTx.rollback();
			} else {
				updateTx.commit();
			}
		}
	}

	@Test
	public void testPostLoadStrategiesAreAreFiredWhenMasterAndDetailsArePersistedTogether() {
		Master master = persistMaster(createDetail());
		assertThatPostLoadStrategyWasExecutedOnMasterAndDetails(master);
	}

	@Test
	public void testPostLoadStrategiesAreFiredOnMasterAndNewDetailsWhenTheMasterIsMerged() {
		final Master persistedMaster = persistMaster();
		final Detail unpersistedDetail = createDetail();
		persistedMaster.getDetails().add(unpersistedDetail);

		Transaction updateTx = persistenceEngine.getSharedPersistenceSession().beginTransaction();
		try {
			Master updated = persistenceEngine.update(persistedMaster);
			assertThatPostLoadStrategyWasExecutedOnMasterAndDetails(updated);
		} finally {
			if (updateTx.isRollbackOnly()) {
				updateTx.rollback();
			} else {
				updateTx.commit();
			}
		}
	}

	@Test
	public void testPostLoadStrategiesAreFiredOnMasterAndNewDetailsIfTheDetailsArePersistedBeforeTheMasterIsMerged() {
		final Master persistedMaster = persistMaster();
		final Detail unpersistedDetail = createDetail();
		persistedMaster.getDetails().add(unpersistedDetail);

		Transaction updateTx = persistenceEngine.getSharedPersistenceSession().beginTransaction();
		try {
			for (Detail detail : persistedMaster.getDetails()) {
				if (!detail.isPersisted()) {
					persistenceEngine.save(detail);
				}
			}
			Master updated = persistenceEngine.update(persistedMaster);

			assertThatPostLoadStrategyWasExecutedOnMaster(updated);
			assertThatPostLoadStrategyWasExecutedOnDetails(updated.getDetails());
		} finally {
			if (updateTx.isRollbackOnly()) {
				updateTx.rollback();
			} else {
				updateTx.commit();
			}
		}
	}

	@Test
	public void testPostLoadStrategiesAreFiredOnBothMasterAndDetailsWhenBothTheMasterAndDetailsAreMerged() {
		final Master persistedMaster = persistMaster(createDetail());

		Transaction updateTx = persistenceEngine.getSharedPersistenceSession().beginTransaction();
		try {
			Master updated = persistenceEngine.update(persistedMaster);
			assertThatPostLoadStrategyWasExecutedOnMasterAndDetails(updated);
		} finally {
			if (updateTx.isRollbackOnly()) {
				updateTx.rollback();
			} else {
				updateTx.commit();
			}
		}
	}

	protected Master persistMaster(Detail ... details) {
		Transaction persistTx = persistenceEngine.getSharedPersistenceSession().beginTransaction();
		final Master persistedMaster = new Master();
		try {
			persistedMaster.setName("master");
			persistedMaster.setDetails(new HashSet<>(Arrays.asList(details)));
			persistenceEngine.save(persistedMaster);
		} finally {
			if (persistTx.isRollbackOnly()) {
				persistTx.rollback();
			} else {
				persistTx.commit();
			}
		}
		return persistedMaster;
	}

	protected Detail createDetail() {
		Detail detail = new Detail();
		detail.setName("detail");

		return detail;
	}

	protected void assertThatPostLoadStrategyWasExecutedOnMasterAndDetails(final Master master) {
		assertThatPostLoadStrategyWasExecutedOnMaster(master);
		assertThatPostLoadStrategyWasExecutedOnDetails(master.getDetails());
	}

	private void assertThatPostLoadStrategyWasExecutedOnMaster(final Master master) {
		assertEquals("Post load listener should have set the transient data onto the master",
				POST_SET_DATA, master.getTransientData());
	}

	private void assertThatPostLoadStrategyWasExecutedOnDetails(final Set<Detail> details) {
		for (Detail detail : details) {
			assertEquals("Post load listener should have set the transient data onto each detail",
					POST_SET_DATA, detail.getTransientData());
		}
	}

	private static class TransientDataPostInitializationStrategy implements PostInitializationStrategy<Persistable> {
		@Override
		public boolean canProcess(final Object obj, final EventType eventType) {
			return obj instanceof TransientDataHolder;
		}

		@Override
		public void process(final Persistable persistable, final EventType eventType) {
			((TransientDataHolder) persistable).setTransientData("post-set data");
		}
	}
}
