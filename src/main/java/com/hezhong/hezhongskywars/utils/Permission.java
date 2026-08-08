package com.hezhong.hezhongskywars.utils;

import lombok.Getter;

public enum Permission {
    COMMAND("hsw.command"),
    MODIFY_MAP("hsw.modifyMap"),;
    @Getter
    private final String node;
    Permission(String node) {
        this.node = node;
    };
}
