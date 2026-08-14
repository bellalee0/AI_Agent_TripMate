package com.example.tripmate.fixture;

import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.entity.User;
import com.example.tripmate.common.enums.UserRole;

public class UserFixture {

    public static User testUser() {
        return new User("test@test.com", "test", "testUser", "password1234", null, UserRole.USER);
    }

    public static AuthUser testAuthUser() {
        return new AuthUser(1L, "test@test.com", UserRole.USER);
    }
}
