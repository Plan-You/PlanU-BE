package com.planu.group_meeting.chat.principal;

import java.security.Principal;

public class UsernamePrincipal implements Principal {
    private final String username;

    public UsernamePrincipal(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }
}
