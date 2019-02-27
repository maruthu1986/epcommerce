package com.elasticpath.commons.beanframework.config;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.annotation.Resource;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * tests for extensible lists.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/extensible-lists.xml")
public class ExtensibleListFactoryBeanTest {

	private static final String RED = "red";
	private static final String BLACK = "black";
	private static final String GREEN = "green";
	private static final String ORANGE = "orange";
	private static final String BLUE = "blue";

	@Resource
	private List<String> testListChild1;

	@Resource
	private List<String> testListChild2;

	@Resource
	private List<String> testListChild3;

	@Resource
	private List<String> testListChild4;

	/**
	 * Test extensible lists definition in xml.
	 */
	@Test
	public void basicTest() {
		assertThat(testListChild1).containsOnlyElementsOf(ImmutableList.of(RED, BLACK, GREEN));
	}

	/**
	 * Test extensible lists element removal though definition in xml.
	 */
	@Test
	public void testElementsRemoved() {
		assertThat(testListChild2).containsOnlyElementsOf(ImmutableList.of(BLACK, GREEN));
	}

	/**
	 * Test extensible lists element addition though definition in xml.
	 */
	@Test
	public void testElementsAdded() {
		assertThat(testListChild3).containsOnlyElementsOf(ImmutableList.of(BLACK, GREEN, ORANGE, BLUE));
	}

	/**
	 * Test extensible lists element addition and removal in one bean definition in xml.
	 */
	@Test
	public void testElementsAddedRemoved() {
		assertThat(testListChild4).containsOnlyElementsOf(ImmutableList.of(GREEN, ORANGE, BLUE));
	}

	/**
	 * Test that list of elements for removal can not be set to null.
	 */
	@Test
	public void setRemoveListException() {
		ExtensibleListFactoryBean factoryBean = new ExtensibleListFactoryBean();
		assertThatThrownBy(() -> factoryBean.setRemoveList(null))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("List of elements for removal cannot be null");
	}

}