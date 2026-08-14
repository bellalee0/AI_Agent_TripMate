package com.example.tripmate.fixture;

import com.example.tripmate.common.entity.User;
import com.example.tripmate.common.enums.UserRole;

public class UserFixture {

    public static User testUser() {
        return new User("test@test.com", "test", "testUser", "password1234", null, UserRole.USER);
    }
}
