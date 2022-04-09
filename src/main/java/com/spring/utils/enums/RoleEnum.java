package com.spring.utils.enums;

import java.util.Arrays;
import java.util.Optional;

public enum RoleEnum {
    ADMIN(1),
    MODERATOR(2),
    USER(3),
    GUEST(4);

    private final int value;

    RoleEnum(int value) {
        this.value = value;
    }

    public static Optional<RoleEnum> valueOf(int value) {
        return Arrays.stream(values())
            .filter(roleEnum -> roleEnum.value == value)
            .findFirst();
    }
}
