package com.vaultchecker.platform.iam.application.internal.outboundservices.notifications;

public interface NotificationService {
    boolean sendVerificationEmail(String email, String name, String link);

    boolean sendRecoveryCode(String email, String name, String code);
}
