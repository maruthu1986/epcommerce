/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tools.sync.job.descriptor.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;

/**
 * Test class for {@link NullJobDescriptorDaoFactoryImpl}.
 */
public class NullJobDescriptorDaoFactoryImplTest {

	private final NullJobDescriptorDaoFactoryImpl factory = new NullJobDescriptorDaoFactoryImpl();

	@Test
	public void verifyCreateCreatesNoOpDao() throws Exception {
		final JobDescriptorDao dao = factory.createJobDescriptorDao(null);

		assertThat(dao.load())
				.isNull();

		dao.save(null);
	}

}