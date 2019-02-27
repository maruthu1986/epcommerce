package com.elasticpath.rest.resource.favorites.wiring;

import com.elasticpath.rest.authorization.parameter.PermissionParameterStrategy;
import com.elasticpath.rest.definition.favorites.FavoriteIdentifier;
import com.elasticpath.rest.definition.favorites.FavoritesResource;
import com.elasticpath.rest.helix.api.AbstractHelixModule;
import com.elasticpath.rest.resource.favorites.permissions.FavoriteIdParameterStrategy;
import com.google.inject.multibindings.MapBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named
public class FavoriteServiceWiring extends AbstractHelixModule {
    private static final Logger LOG = LoggerFactory.getLogger(FavoriteServiceWiring.class);
    @Override
    protected String resourceName() {
        return FavoritesResource.FAMILY;
    }

    @Override
    protected void registerParameterResolvers(final MapBinder<String, PermissionParameterStrategy> resolvers) {
        LOG.info("FavoriteServiceWiring Started :: " + FavoriteIdentifier.FAVORITE_ID);
        super.registerParameterResolvers(resolvers);
        resolvers.addBinding(FavoriteIdentifier.FAVORITE_ID).toInstance(new FavoriteIdParameterStrategy());
    }
}
