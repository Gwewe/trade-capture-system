package com.technicalchallenge.model;

import com.technicalchallenge.dto.TradeDTO;

import java.util.Set;

public enum Role {
    TRADER(Set.of("VIEW","CREATE","AMEND","TERMINATE","CANCEL")),
    SALES(Set.of("VIEW","CREATE","AMEND")),
    MIDDLE_OFFICE(Set.of("VIEW","AMEND")),
    SUPPORT(Set.of("VIEW"));

    private final Set<String> permissions;

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }

    public boolean allows(String operation, TradeDTO tradeDTO, ApplicationUser user) {
        if (operation != null && permissions.contains(operation.toUpperCase())){
            return true;
        } else {
            return false;
        }
    }

    public static Role fromString(String roleName) {
        if (roleName == null || roleName.isEmpty()) {
            throw new IllegalArgumentException("Role name not found or not set.");
        }
        return Role.valueOf(roleName.trim().toUpperCase().replace(' ', '_'));
    }

}
