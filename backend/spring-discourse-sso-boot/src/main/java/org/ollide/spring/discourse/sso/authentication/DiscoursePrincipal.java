package org.ollide.spring.discourse.sso.authentication;

import org.springframework.security.core.AuthenticatedPrincipal;

import java.io.Serializable;
import java.util.Set;

public class DiscoursePrincipal implements AuthenticatedPrincipal, Serializable {

    private static final long serialVersionUID = 1L;

    private String externalId;
    private String username;
    private String fullName;
    private String email;
    private String avatarUrl;

    private boolean admin;
    private boolean moderator;

    private Set<String> groups;

    public DiscoursePrincipal(String externalId, String username, String fullName, String email, String avatarUrl,
                              boolean admin, boolean moderator, Set<String> groups) {
        this.externalId = externalId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.admin = admin;
        this.moderator = moderator;
        this.groups = groups;
    }

    @Override
    public String getName() {
        return getUsername();
    }

    public String getExternalId() {
        return externalId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isModerator() {
        return moderator;
    }

    public Set<String> getGroups() {
        return groups;
    }
}
