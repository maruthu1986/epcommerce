/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.service.shipping.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.commons.constants.EpShippingContextIdNames.SHIPPING_REGION;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.shipping.ShippingRegionExistException;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Test <code>ShippingRegionServiceImpl</code>.
 */
@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
@RunWith(MockitoJUnitRunner.class)
public class ShippingRegionServiceImplTest {
	private static final String SHIPPINGREGION_FIND_BY_NAME = "SHIPPINGREGION_FIND_BY_NAME";

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final String TEST_REGION_NAME = "Canada BC";

	private ShippingRegionService shippingRegionServiceImpl;

	@Mock
	private PersistenceEngine mockPersistenceEngine;

	@Mock
	private BeanFactory beanFactory;

	@Before
	public void setUp() throws Exception {
		shippingRegionServiceImpl = new ShippingRegionServiceImpl();
		shippingRegionServiceImpl.setPersistenceEngine(mockPersistenceEngine);

		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(beanFactory);
		when(beanFactory.getBeanImplClass(SHIPPING_REGION)).thenAnswer(answer -> ShippingRegionImpl.class);
	}

	@After
	public void tearDown() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(null);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		shippingRegionServiceImpl.setPersistenceEngine(null);
		assertThatThrownBy(() -> shippingRegionServiceImpl.add(new ShippingRegionImpl())).as(SERVICE_EXCEPTION_EXPECTED)
				.isInstanceOf(EpServiceException.class);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertThat(shippingRegionServiceImpl.getPersistenceEngine()).isNotNull();
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.add(ShippingRegion)'.
	 */
	@Test
	public void testAdd() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName(TEST_REGION_NAME);
		// expectations
		final Object[] parameters = new Object[]{shippingRegion.getName()};
		when(mockPersistenceEngine.retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters)).thenReturn(emptyList());

		this.shippingRegionServiceImpl.add(shippingRegion);

		verify(mockPersistenceEngine).save(eq(shippingRegion));
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.update(ShippingRegion)'.
	 */
	@Test
	public void testUpdate() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		final ShippingRegion updatedShippingRegion = new ShippingRegionImpl();
		shippingRegion.setUidPk(1L);
		shippingRegion.setName(TEST_REGION_NAME);
		// expectations
		final Object[] parameters = new Object[]{shippingRegion.getName()};
		when(mockPersistenceEngine.retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters)).thenReturn(emptyList());
		when(mockPersistenceEngine.merge(shippingRegion)).thenReturn(updatedShippingRegion);
		final ShippingRegion returnedShippingRegion = shippingRegionServiceImpl.update(shippingRegion);

		assertThat(returnedShippingRegion).isSameAs(updatedShippingRegion);
	}

	@Test(expected = ShippingRegionExistException.class)
	public void testUpdateFailure() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setUidPk(1L);
		shippingRegion.setName(TEST_REGION_NAME);
		// test faliure
		final ShippingRegion shippingRegion2 = new ShippingRegionImpl();
		shippingRegion2.setUidPk(2L);
		shippingRegion2.setName(TEST_REGION_NAME);
		final List<ShippingRegion> srList = new ArrayList<>();
		srList.add(shippingRegion2);

		// expectations
		final Object[] parameters = new Object[]{shippingRegion.getName()};
		when(mockPersistenceEngine.<ShippingRegion>retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters)).thenReturn(srList);
		shippingRegionServiceImpl.update(shippingRegion);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.delete(ShippingRegion)'.
	 */
	@Test
	public void testDelete() {
		final ShippingRegion shippingRegion = new ShippingRegionImpl();

		shippingRegionServiceImpl.remove(shippingRegion);

		verify(mockPersistenceEngine).delete(shippingRegion);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final ShippingRegion shippingRegion1 = new ShippingRegionImpl();
		shippingRegion1.setName("aaa");
		final ShippingRegion shippingRegion2 = new ShippingRegionImpl();
		shippingRegion2.setName("bbb");
		final List<ShippingRegion> srList = asList(shippingRegion2, shippingRegion1);
		// expectations
		when(mockPersistenceEngine.<ShippingRegion>retrieveByNamedQuery(eq("SHIPPINGREGION_SELECT_ALL"))).thenReturn(srList);
		final List<ShippingRegion> retrievedSRList = shippingRegionServiceImpl.list();
		assertThat(srList).hasSize(retrievedSRList.size());
		assertThat(srList.get(0)).isEqualTo(retrievedSRList.get(0));
		assertThat(srList.get(1)).isEqualTo(retrievedSRList.get(1));
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShippingRegionServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final ShippingRegionImpl shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName("aaa");
		shippingRegion.setUidPk(uid);
		// expectations
		when(mockPersistenceEngine.load(ShippingRegionImpl.class, uid)).thenReturn(shippingRegion);
		final ShippingRegion loadedShippingRegion = shippingRegionServiceImpl.load(uid);
		assertThat(shippingRegion).isSameAs(loadedShippingRegion);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.ShippingRegionServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final ShippingRegionImpl shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName("aaa");
		shippingRegion.setUidPk(uid);
		// expectationss
		when(mockPersistenceEngine.get(ShippingRegionImpl.class, uid)).thenReturn(shippingRegion);
		final ShippingRegion loadedShippingRegion = shippingRegionServiceImpl.get(uid);
		assertThat(shippingRegion).isSameAs(loadedShippingRegion);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.nameExists(String)'.
	 */
	@Test
	public void testNameExists() {
		final String regionName = "test region";
		final String existRegionName = regionName;
		final Object[] parameters = new Object[]{existRegionName};
		final ShippingRegion shippingRegion1 = new ShippingRegionImpl();
		final long uidPk1 = 1L;
		shippingRegion1.setUidPk(uidPk1);
		shippingRegion1.setName(regionName);
		final List<ShippingRegion> srList = new ArrayList<>();
		srList.add(shippingRegion1);

		// Test emailExists(String)email
		when(mockPersistenceEngine.<ShippingRegion>retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters)).thenReturn(srList);
		assertThat(shippingRegionServiceImpl.nameExists(existRegionName)).isTrue();

		// Test emailExists(Customer)
		final ShippingRegion shippingRegion2 = new ShippingRegionImpl();
		shippingRegion2.setName(regionName);
		assertThat(shippingRegionServiceImpl.nameExists(shippingRegion2)).isTrue();
		final long uidPk2 = 2L;
		shippingRegion2.setUidPk(uidPk2);
		assertThat(shippingRegionServiceImpl.nameExists(shippingRegion2)).isTrue();
		assertThat(shippingRegionServiceImpl.nameExists(shippingRegion1)).isFalse();
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.impl.ShippingRegionServiceImpl.findByName(String)'.
	 */
	@Test
	public void testFindByName() {
		final String regionName = "TEST REGION";
		final Object[] parameters = new Object[]{regionName};
		final ShippingRegion shippingRegion = new ShippingRegionImpl();
		shippingRegion.setName(regionName);
		final List<ShippingRegion> srList = new ArrayList<>();
		srList.add(shippingRegion);

		// expectations
		when(mockPersistenceEngine.<ShippingRegion>retrieveByNamedQuery(SHIPPINGREGION_FIND_BY_NAME, parameters)).thenReturn(srList);
		final ShippingRegion retrievedSR = shippingRegionServiceImpl.findByName(regionName);
		assertThat(shippingRegion).isSameAs(retrievedSR);
	}
}
