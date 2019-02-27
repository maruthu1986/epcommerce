/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


import com.elasticpath.cmclient.ChangeSetTestBase;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCategoryImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductCategoryAssociationImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeProductSkuImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobImpl;
import com.elasticpath.domain.pricing.csvimport.impl.ImportDataTypeBaseAmountImpl;


/**
 * Verifies the logic in the {@code ChangeSetRunImportJobPolicy}.
 */
public class ChangeSetRunImportJobPolicyTest extends ChangeSetTestBase {

	private static final String POLICY_ACTION_CONTAINER_NAME = "test"; //$NON-NLS-1$

	/**
	 * Tests that the state is enabled when change sets are disabled.
	 */
	@Test
	public void testStateChangeSetsDisabled() {
		ChangeSetRunImportJobPolicy policy = new ChangeSetRunImportJobPolicy() {
			@Override
			boolean isChangeSetFeatureEnabled() {
				return false;
			}
			@Override
			void populateJobNamePrefixSet() { /*no-op*/ }
			@Override
			void retrieveBeans() { /*no-op*/ }
		};
		
		
		PolicyActionContainer targetContainer = new PolicyActionContainer(POLICY_ACTION_CONTAINER_NAME); 
		ImportJob importJob = new ImportJobImpl();
		targetContainer.setPolicyDependent(importJob);	
		
		policy.init(null);
		EpState actualState = policy.determineState(targetContainer);
		
		assertEquals("Enabled when change sets are disabled", EpState.EDITABLE, actualState); //$NON-NLS-1$
		
	}
	
	/**
	 * Tests that the state is disabled when no import job is selected.
	 */
	@Test
	public void testStateNoImportJobSelected() {
		ChangeSetRunImportJobPolicy policy = new ChangeSetRunImportJobPolicy() {
			@Override
			boolean isChangeSetFeatureEnabled() {
				return false;
			}
			@Override
			void populateJobNamePrefixSet() { /*no-op*/ }
			@Override
			void retrieveBeans() { /*no-op*/ }
		};
		
		
		PolicyActionContainer targetContainer = new PolicyActionContainer(POLICY_ACTION_CONTAINER_NAME);
		// no dependent object as nothing selected.
		
		policy.init(null);
		EpState actualState = policy.determineState(targetContainer);
		
		assertEquals("Enabled when change sets are disabled", EpState.DISABLED, actualState); //$NON-NLS-1$
		
	}
	
	/**
	 * Tests that the state is enabled when change sets are enabled and a Product Association job is selected.
	 */
	@Test
	public void testStateProductAssociation() {
		ChangeSetRunImportJobPolicy policy = new ChangeSetRunImportJobPolicy() {
			@Override
			boolean isChangeSetFeatureEnabled() {
				return true;
			}
			@Override
			void populateJobNamePrefixSet() { /*no-op*/ }
			@Override
			void retrieveBeans() { /*no-op*/ }
		};
		
		
		PolicyActionContainer targetContainer = new PolicyActionContainer(POLICY_ACTION_CONTAINER_NAME);
		ImportJob importJob = new ImportJobImpl();
		importJob.setImportDataTypeName("Product Association - Hello"); //$NON-NLS-1$
		targetContainer.setPolicyDependent(importJob);

		policy.init(null);
		EpState actualState = policy.determineState(targetContainer);
		
		assertEquals("Enabled when change sets are disabled", EpState.EDITABLE, actualState); //$NON-NLS-1$
		
	}

	/**
	 * Tests that the state is disabled when change sets are enabled and a Category job is selected.
	 * Category is used as an example for all the import data types that are controlled.
	 */
	@Test
	public void testStateCategoryNoPermission() {
		BeanFactory mockBeanFactory = mock(BeanFactory.class);
		ServiceLocator.setBeanFactory(mockBeanFactory);

		when(mockBeanFactory.getBean(ContextIdNames.IMPORT_DATA_TYPE_CATEGORY))
			.thenReturn(new ImportDataTypeCategoryImpl());
		when(mockBeanFactory.getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT))
			.thenReturn(new ImportDataTypeProductImpl());
		when(mockBeanFactory.getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_SKU))
			.thenReturn(new ImportDataTypeProductSkuImpl());
		when(mockBeanFactory.getBean(ContextIdNames.IMPORT_DATA_TYPE_PRODUCT_CATEGORY_ASSOCIATION))
			.thenReturn(new ImportDataTypeProductCategoryAssociationImpl());
		when(mockBeanFactory.getBean(ContextIdNames.IMPORT_DATA_TYPE_BASEAMOUNT))
			.thenReturn(new ImportDataTypeBaseAmountImpl());


		ChangeSetRunImportJobPolicy policy = new ChangeSetRunImportJobPolicy() {

			@Override
			boolean isChangeSetFeatureEnabled() {
				return true;
			}
			@Override
			void retrieveBeans() { /*no-op*/ }
			@Override
			boolean userHasChangeSetPermission() {
				return false;
			}
		};
		
		
		PolicyActionContainer targetContainer = new PolicyActionContainer(POLICY_ACTION_CONTAINER_NAME); 
		ImportJob importJob = new ImportJobImpl();
		importJob.setImportDataTypeName("Category - Hello"); //$NON-NLS-1$
		targetContainer.setPolicyDependent(importJob);

		policy.init(null);
		EpState actualState = policy.determineState(targetContainer);
		
		assertEquals("Disabled when user does not have permission", EpState.DISABLED, actualState); //$NON-NLS-1$
		
	}
	
	/**
	 * Tests that the state is enabled when change sets are enabled and a Category job is selected.
	 * Category is used as an example for all the import data types that are controlled.
	 */
	@Test
	public void testStateCategoryHasPermission() {
		ChangeSetRunImportJobPolicy policy = new ChangeSetRunImportJobPolicy() {
			@Override
			boolean isChangeSetFeatureEnabled() {
				return true;
			}
			@Override
			void retrieveBeans() { /*no-op*/ }
			@Override
			void populateJobNamePrefixSet() { /*no-op*/ }
			@Override
			boolean userHasChangeSetPermission() {
				return true;
			}
		};
		
		
		PolicyActionContainer targetContainer = new PolicyActionContainer(POLICY_ACTION_CONTAINER_NAME); 
		ImportJob importJob = new ImportJobImpl();
		importJob.setImportDataTypeName("Category - Hello"); //$NON-NLS-1$
		targetContainer.setPolicyDependent(importJob);

		policy.init(null);
		EpState actualState = policy.determineState(targetContainer);
		
		assertEquals("Enabled when user does have permission", EpState.EDITABLE, actualState); //$NON-NLS-1$
		
	}
}

