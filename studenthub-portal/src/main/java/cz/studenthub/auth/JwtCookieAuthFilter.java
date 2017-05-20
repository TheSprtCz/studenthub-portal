/*******************************************************************************
 *     Copyright (C) 2017  Stefan Bunciak
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.studenthub.auth;

import java.io.IOException;
import java.security.Principal;
import java.util.Objects;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.SecurityContext;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;

/**
 * Auth filter parsing JWT token from Cookies.
 * 
 * @author sbunciak
 * @since 1.0
 *
 * @param <P>
 *          Principal
 */
@Priority(Priorities.AUTHENTICATION)
public class JwtCookieAuthFilter<P extends Principal> extends AuthFilter<String, P> {

  private final String cookieName;

  private JwtCookieAuthFilter(String cookieName) {
    this.cookieName = cookieName;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    Cookie cookie = requestContext.getCookies().get(cookieName);

    if (cookie == null || !authenticate(requestContext, cookie.getValue(), SecurityContext.BASIC_AUTH)) {
      throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }
  }

  /**
   * Builder for {@link OAuthCredentialAuthFilter}.
   * <p>
   * An {@link Authenticator} must be provided during the building process.
   * </p>
   *
   * @param <P>
   *          the type of the principal
   */
  public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, JwtCookieAuthFilter<P>> {

    private String cookieName;

    public Builder<P> setCookieName(String cookieName) {
      this.cookieName = cookieName;
      return this;
    }

    @Override
    protected JwtCookieAuthFilter<P> newInstance() {
      return new JwtCookieAuthFilter<>(Objects.requireNonNull(cookieName, "cookieName is not set"));
    }
  }
}