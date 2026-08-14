package com.example.tripmate.common.utils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class GroupCodeGenerator {

    /**
     * 고유 코드 생성(알파벳 + 숫자 6자리)
     */
    public static String generate67BillionCode() {

        UUID uuid = UUID.randomUUID();
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());

        return base64.substring(0, 6);
    }
}
