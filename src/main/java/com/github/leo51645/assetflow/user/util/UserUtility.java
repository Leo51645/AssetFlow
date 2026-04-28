package com.github.leo51645.assetflow.user.util;

import org.springframework.stereotype.Component;

@Component
public class UserUtility {

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        return parts[0].charAt(0) + "***@" + parts[1];
    }
}
