/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.favorites.permissions;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import com.elasticpath.rest.definition.favorites.FavoriteEntity;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.id.Identifier;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.identity.util.PrincipalsUtil;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collection;

/**
 * Strategy for resolving the Favorite ID parameter.
 */
@Singleton
@Named
public final class FavoriteIdParameterStrategy extends AbstractCollectionValueStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(FavoriteIdParameterStrategy.class);

    @Inject
    @ResourceRepository
    private Provider<Repository<FavoriteEntity, FavoriteIdentifier>> repository;

    @Inject
    private IdentifierTransformerProvider identifierTransformerProvider;

    @Override
    protected Collection<String> getParameterValues(final PrincipalCollection principals) {
        LOG.info("**** FavoriteIdParameterStrategy ****");
        String scope = PrincipalsUtil.getScope(principals);
        final IdentifierTransformer<Identifier> identifierTransformer = identifierTransformerProvider.forUriPart(FavoriteIdentifier.FAVORITE_ID);
        LOG.info("FavoriteIdParameterStrategy, identifierTransformer :: "+identifierTransformer);
        return repository.get()
                .findAll(StringIdentifier.of(scope))
                .map(favoriteIdentifier -> identifierTransformer.identifierToUri(favoriteIdentifier.getFavoriteId()))
                .toList()
                .blockingGet();
    }
}
