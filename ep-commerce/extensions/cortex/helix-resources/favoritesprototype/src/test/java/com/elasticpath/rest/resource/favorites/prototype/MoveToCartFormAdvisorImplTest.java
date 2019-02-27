package com.elasticpath.rest.resource.favorites.prototype;

import com.elasticpath.extensions.rest.resource.integration.epcommerce.repository.favorites.ItemValidationService;
import com.elasticpath.rest.definition.favorites.MoveToCartFormIdentifier;
import com.elasticpath.rest.resource.favorites.lineitems.advise.MoveToCartFormAdvisorImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MoveToCartFormAdvisorImplTest {
    private MoveToCartFormAdvisorImpl moveToCartFormAdvisor;
    @Mock
    private MoveToCartFormIdentifier moveToCartFormIdentifier;

    @Mock
    private ItemValidationService itemValidationService;

    @Test
    public void shouldMoveToCart() {
        moveToCartFormAdvisor = new MoveToCartFormAdvisorImpl(moveToCartFormIdentifier, itemValidationService);
        moveToCartFormAdvisor.onAdvise();
        verify(itemValidationService).isItemPurchasable(moveToCartFormIdentifier.getFavoriteLineItem());
    }
}
