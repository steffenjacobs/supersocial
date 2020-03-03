package org.ollide.spring.discourse.sso.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

public class DiscourseSsoAuthentication extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final DiscoursePrincipal discoursePrincipal;

    public DiscourseSsoAuthentication(DiscoursePrincipal discoursePrincipal,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.discoursePrincipal = discoursePrincipal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return discoursePrincipal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DiscourseSsoAuthentication that = (DiscourseSsoAuthentication) o;
        return Objects.equals(discoursePrincipal, that.discoursePrincipal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), discoursePrincipal);
    }
}
