/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.money.Money;


/**
 * 
 * Test class for <code>ReturnSubjectPage</code>.
 */
public class ReturnSubjectPageTest {
	
    private OrderReturnWizardValidator orderReturnValidator;

	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
        orderReturnValidator = new OrderReturnWizardValidator();
	}
	
	/**
	 * Test for <code>isShippingCostValid</code>.
	 */
	@Test
	public void testIsShippingCostValid() {
		
		Assert.assertTrue(orderReturnValidator.isShippingCostValid(BigDecimal.ONE, BigDecimal.TEN));
		Assert.assertFalse(orderReturnValidator.isShippingCostValid(BigDecimal.TEN, BigDecimal.ONE));
		Assert.assertTrue(orderReturnValidator.isShippingCostValid(BigDecimal.ONE, BigDecimal.ONE));

	}
	
	
	
	/**
	 * Test for <code>isQuantityToReturnValid</code>.
	 */
	public void testIsQuantityToReturnValid() {
		final Set<OrderReturnSku> returnSkus = new HashSet<>();
		OrderReturnSku returnSku1 = new MockOrderReturnSkuImpl();
		OrderReturnSku returnSku2 = new MockOrderReturnSkuImpl();
		OrderReturnSku returnSku3 = new MockOrderReturnSkuImpl();
		OrderReturnSku returnSku4 = new MockOrderReturnSkuImpl();
		returnSkus.add(returnSku1);
		returnSkus.add(returnSku2);
		returnSkus.add(returnSku3);
		returnSkus.add(returnSku4);
		
		Assert.assertFalse(orderReturnValidator.isQuantityToReturnValid(returnSkus));
		
		returnSku1.setQuantity(1);
		Assert.assertTrue(orderReturnValidator.isQuantityToReturnValid(returnSkus));
		
		returnSku2.setQuantity(2);
		Assert.assertTrue(orderReturnValidator.isQuantityToReturnValid(returnSkus));

	}
	
	/**
	 * 
	 * Mock implementation for <code>OrderReturnSku</code>.
	 */
	private class MockOrderReturnSkuImpl implements OrderReturnSku {
		
		private int quantity;

		@Override
		public Money getAmountMoney() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OrderSku getOrderSku() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSkuGuid() {
			// TODO Auto-generated method stub
			return null;
		}
		
		/**
		 *  @param guid
		 */
		public void setSkuGuid(final String guid) { 
			// Empty Method
		}

		@Override
		public int getQuantity() {
			return quantity;
		}

		@Override
		public int getReceivedQuantity() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getReceivedState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BigDecimal getReturnAmount() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Money getReturnAmountMoney(final Currency currency) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getReturnReason() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BigDecimal getTax() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setOrderSku(final OrderSku orderSku) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setQuantity(final int quantity) {
			this.quantity = quantity;
			
		}

		@Override
		public void setReceivedQuantity(final int receiveQuantity) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setReceivedState(final String receivedState) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setReturnAmount(final BigDecimal returnAmount) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setReturnReason(final String returnReason) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setTax(final BigDecimal tax) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getGuid() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setGuid(final String guid) {
			// TODO Auto-generated method stub
			
		}

		/**
		 *
		 */
		public void executeBeforePersistAction() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public long getUidPk() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isPersisted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setUidPk(final long uidPk) {
			// TODO Auto-generated method stub
			
		}

		/**
		 *
		 * @return
		 */
		public ElasticPath getElasticPath() {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 *
		 * @return
		 * @deprecated
		 */
		@SuppressWarnings("deprecation")
		public Utility getUtility() {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 *
		 */
		public void setDefaultValues() {
			// TODO Auto-generated method stub
			
		}

		/**
		 *
		 * @param elasticpath
		 * @deprecated
		 */
		@SuppressWarnings("deprecation")
		public void setElasticPath(final ElasticPath elasticpath) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isFullyReceived() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void initialize() {
			// TODO Auto-generated method stub
			
		}
		
		
	}

}
