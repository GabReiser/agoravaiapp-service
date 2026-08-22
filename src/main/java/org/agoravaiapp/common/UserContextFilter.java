package org.agoravaiapp.common;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class UserContextFilter implements ContainerRequestFilter {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String ADMIN_HEADER = "X-User-Admin";

    @Inject
    UserContext userContext;

    @ConfigProperty(name = "agoravai.auth.dev-user-id", defaultValue = "dev-user")
    String devUserId;

    @ConfigProperty(name = "agoravai.auth.dev-admin", defaultValue = "true")
    boolean devAdmin;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String userId = firstNonBlank(ctx.getHeaderString(USER_ID_HEADER), devUserId);
        boolean admin = parseBoolean(ctx.getHeaderString(ADMIN_HEADER), devAdmin);
        userContext.init(userId, admin);
    }

    private static String firstNonBlank(String value, String fallback) {
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        return fallback;
    }

    private static boolean parseBoolean(String value, boolean fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
