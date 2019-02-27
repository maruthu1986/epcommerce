/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;

import com.google.common.testing.EqualsTester;
import org.junit.Test;

public class CatalogItemDiscountRecordImplTest {

	@Test
	public void verifyEqualsMatchesRuleIdAndActionIdAndDiscountAmount() throws Exception {
		final long ruleIdControl = 1L;
		final long ruleIdDifferent = 2L;

		final long actionIdControl = 10L;
		final long actionIdDifferent = 11L;

		final BigDecimal discountAmountControl = BigDecimal.ONE;
		final BigDecimal discountAmountDifferent = BigDecimal.TEN;

		new EqualsTester()
				// control group
				.addEqualityGroup(new CatalogItemDiscountRecordImpl(ruleIdControl, actionIdControl, discountAmountControl),
						new CatalogItemDiscountRecordImpl(ruleIdControl, actionIdControl, discountAmountControl))

				// rule ID different
				.addEqualityGroup(new CatalogItemDiscountRecordImpl(ruleIdDifferent, actionIdControl, discountAmountControl),
						new CatalogItemDiscountRecordImpl(ruleIdDifferent, actionIdControl, discountAmountControl))

				// action ID different
				.addEqualityGroup(new CatalogItemDiscountRecordImpl(ruleIdControl, actionIdDifferent, discountAmountControl),
						new CatalogItemDiscountRecordImpl(ruleIdControl, actionIdDifferent, discountAmountControl))

				// discount amount different
				.addEqualityGroup(new CatalogItemDiscountRecordImpl(ruleIdControl, actionIdControl, discountAmountDifferent),
						new CatalogItemDiscountRecordImpl(ruleIdControl, actionIdControl, discountAmountDifferent))

				// assert
				.testEquals();
	}

}