/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.settings.domain.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the metadata class.
 *
 */
public class SettingMetadataImplTest {
	private final SettingMetadataImpl metadata1 = new SettingMetadataImpl();
	private final SettingMetadataImpl metadata2 = new SettingMetadataImpl();
	private final SettingMetadataImpl metadata1more = new SettingMetadataImpl();


	/**
	 * Set up the metadata obj.
	 */
	@Before
	public void setUp() {
		metadata1.setUidPk(1);
		metadata1.setKey("key1");
		metadata1.setValue("value1");
		metadata1.setUidPk(2);
		metadata2.setKey("key2");
		metadata2.setValue("value2");
		metadata1more.setKey("key1");
		metadata1more.setValue("value1");
	}
	
	/**
	 * Test that equals works on only the key and value.
	 */
	@Test
	public void testEquals() {
		//Equals symetric
		assertThat(metadata1).isEqualTo(metadata1);
		//Equals reflexive
		assertThat(metadata1more).isEqualTo(metadata1);
		assertThat(metadata1).isEqualTo(metadata1more);

		assertThat(metadata1.equals(metadata2)).isFalse();
	}
	
	/**
	 * Test that hashcode works with the equals and operates only on key and value.
	 */
	@Test
	public void testHashCode() {
		assertThat(metadata1.hashCode()).isEqualTo(metadata1.hashCode());
		assertThat(metadata1more.hashCode()).isEqualTo(metadata1.hashCode());
	}
}
