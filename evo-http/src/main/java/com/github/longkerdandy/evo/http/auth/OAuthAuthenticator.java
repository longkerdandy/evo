package com.github.longkerdandy.evo.http.auth;

import com.github.longkerdandy.evo.aerospike.AerospikeStorage;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.apache.commons.lang3.StringUtils;

/**
 * OAuth2 Authenticator
 */
public class OAuthAuthenticator implements Authenticator<String, String> {

    private final AerospikeStorage storage;

    public OAuthAuthenticator(AerospikeStorage storage) {
        this.storage = storage;
    }

    /**
     * Authenticate
     * <p>
     * The DropWizard OAuthFactory enables OAuth2 bearer-token authentication,
     * and requires an authenticator which takes an instance of String
     * Also the OAuthFactory needs to be parameterized with the type of the principal the authenticator produces.
     *
     * @param credentials OAuth2 bearer-token
     * @return User Id
     */
    @Override
    public Optional<String> authenticate(String credentials) throws AuthenticationException {
        if (StringUtils.isBlank(credentials)) {
            return Optional.absent();
        }
        // validate token
        String u = this.storage.getUserIdByToken(credentials);
        return StringUtils.isBlank(u) ? Optional.absent() : Optional.of(u);
    }
}
