package com.github.leo51645.assetflow.user.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserUtilityTest {

    UserUtility userUtility = new UserUtility();

    @Test
    void shouldMaskEmail() {
        String email = "test123@email.com";

        String maskedEmail = userUtility.maskEmail(email);

        assertEquals("t***@email.com", maskedEmail);
    }

    @Test
    void shouldReturnMaskForNullEmail() {
        String email = null;
        String maskedEmail = userUtility.maskEmail(email);

        assertEquals("***", maskedEmail);
    }

    @Test
    void shouldReturnMaskForEmailWithoutAtSymbol() {
        String email = "test123email.com";
        String maskedEmail = userUtility.maskEmail(email);

        assertEquals("***", maskedEmail);
    }

}