/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseNumberEntity;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.impl.OrderRepositoryImpl;
import com.elasticpath.rest.schema.StructuredMessageTypes;

/**
 * Repository to fetch PurchaseIdentifier for a purchase.
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class PurchaseNumberEntityPurchaseIdentifierRepositoryImpl<E extends PurchaseNumberEntity, I extends PurchaseIdentifier>
        implements Repository<PurchaseNumberEntity, PurchaseIdentifier> {

    private static final String MISSING_REQUIRED_REQUEST_BODY = "Purchase number field is missing a value.";
    private static final String NEED_PURCHASE_NUMBER = "need.purchase-number";
    private static final String PURCHASE_NUMBER_NOT_FOUND = "purchase-number.not.found";
    private static final Map<String, String> ERROR_DATA = ImmutableMap.of("field-name", "purchase-number");

    private OrderRepository orderRepository;
    private ResourceOperationContext resourceOperationContext;

    @Override
    public Single<SubmitResult<PurchaseIdentifier>> submit(final PurchaseNumberEntity purchaseNumberEntity, final IdentifierPart<String> scope) {

        Maybe<String> purchaseNumberSingle = validatePurchaseNumberEntity(purchaseNumberEntity, scope);

        return purchaseNumberSingle.toSingle().map(purchaseNumber -> SubmitResult.<PurchaseIdentifier>builder()
                .withIdentifier(buildPurchaseIdentifier(purchaseNumber, scope))
                .withStatus(SubmitStatus.CREATED)
                .build());
    }

    private PurchaseIdentifier buildPurchaseIdentifier(final String purchaseNumber, final IdentifierPart<String> scope) {
        return PurchaseIdentifier.builder()
                .withPurchases(PurchasesIdentifier.builder()
                        .withScope(scope)
                        .build())
                .withPurchaseId(StringIdentifier.of(purchaseNumber))
                .build();
    }

    /**
     * Validates a purchase number.
     *
     * @param purchaseNumberEntity The purchase number to validate.
     * @param scope The store scope
     * @return A validated purchase number or a validation error.
     */
    private Maybe<String> validatePurchaseNumberEntity(final PurchaseNumberEntity purchaseNumberEntity,
                                                                      final IdentifierPart<String> scope) {

        if (purchaseNumberEntity.getPurchaseNumber().isEmpty()) {
            return getInvalidPurchaseNumberErrorMessage();
        }

        String userId = resourceOperationContext.getUserIdentifier();
        return orderRepository.findByGuidAndCustomerGuid(scope.getValue(), purchaseNumberEntity.getPurchaseNumber(), userId)
                .switchIfEmpty(getPurchaseNumberNotFoundErrorMessage(purchaseNumberEntity.getPurchaseNumber(), scope.getValue()));
    }

    /**
     * Get invalid purchase number structured error message.
     *
     * @return the message
     */
    private Maybe<String> getInvalidPurchaseNumberErrorMessage() {
        Message structuredError = Message.builder()
                .withType(StructuredMessageTypes.ERROR)
                .withId(NEED_PURCHASE_NUMBER)
                .withDebugMessage(MISSING_REQUIRED_REQUEST_BODY)
                .withData(ERROR_DATA)
                .build();
        return Maybe.error(ResourceOperationFailure.badRequestBody(MISSING_REQUIRED_REQUEST_BODY, Collections.singletonList(structuredError)));
    }

    /**
     * Get purchase number not found structured error message.
     *
     * @return the message
     */
    private Maybe<String> getPurchaseNumberNotFoundErrorMessage(final String purchaseNumber, final String scope) {
        String message = String.format(OrderRepositoryImpl.PURCHASE_NOT_FOUND, purchaseNumber, scope);
        Message structuredError = Message.builder()
                .withType(StructuredMessageTypes.ERROR)
                .withId(PURCHASE_NUMBER_NOT_FOUND)
                .withDebugMessage(message)
                .withData(ERROR_DATA)
                .build();
        return Maybe.error(ResourceOperationFailure.notFound(message, Collections.singletonList(structuredError)));
    }

    @Reference
    public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
        this.resourceOperationContext = resourceOperationContext;
    }

    @Reference
    public void setOrderRepository(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

}
