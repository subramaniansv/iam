package com.iam.app.security;

import java.util.List;
import java.util.UUID;

import com.iam.app.models.Role;
import com.iam.app.util.JwtUtil;

public class AuthUser {
    private UUID userId;
    private String email;
    private List<Role> roles;

    public boolean hasRole(String rolename) {
        System.out.println(rolename);
        return roles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(rolename));
    }

    public boolean hasAllRoles(String[] roleNames) {
        for (String role : roleNames) {
            if (!hasRole(role)) {
                return false;
            }

        }
        return true;
    }

    public boolean hasAnyRoles(String[] roleNames) {
        for (String role : roleNames) {
            if (hasRole(role)) {
                return true;
            }

        }
        return false;
    }
    public boolean hasPermission(String resource, String action) {
        return roles.stream()
                    .flatMap(r -> r.getPermissions().stream())
                    .anyMatch(p -> {
                        boolean resourceMatch = p.getResource().equalsIgnoreCase(resource);
                        boolean actionMatch   = action.isEmpty() ||
                                                p.getAction().name().toLowerCase()
                                                .contains(action.toLowerCase());
                        return resourceMatch && actionMatch;
                    });
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public static AuthUser getAuthUser(String token){
        JwtUtil jwt = new JwtUtil();
        AuthUser authUser = new AuthUser();
        if(jwt.isTokenExpired(token)){
            return null;
        }
        authUser.setUserId(jwt.extractUserId(token));
        authUser.setEmail(jwt.extractEmail(token));
        authUser.setRoles(jwt.extractRoles(token));


        return authUser;
    }
}
