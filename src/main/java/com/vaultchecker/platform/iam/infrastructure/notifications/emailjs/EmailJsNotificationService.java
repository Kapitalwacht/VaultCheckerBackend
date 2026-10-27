package com.vaultchecker.platform.iam.infrastructure.notifications.emailjs;

import com.vaultchecker.platform.iam.application.internal.outboundservices.notifications.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Service
public class EmailJsNotificationService implements NotificationService {

    private final RestClient restClient = RestClient.create();

    @Value("${emailjs.api-url:https://api.emailjs.com/api/v1.0/email/send}")
    private String apiUrl;

    @Value("${emailjs.service-id:}")
    private String serviceId;

    @Value("${emailjs.public-key:}")
    private String publicKey;

    @Value("${emailjs.private-key:}")
    private String privateKey;

    @Value("${emailjs.recovery-template-id:}")
    private String recoveryTemplateId;

    @Value("${emailjs.verification-template-id:}")
    private String verificationTemplateId;

    @Override
    public boolean sendVerificationEmail(String email, String name, String link) {
        return send(verificationTemplateId, Map.of(
                "email", email,
                "to_email", email,
                "name", displayName(name, email),
                "link", link));
    }

    @Override
    public boolean sendRecoveryCode(String email, String name, String code) {
        return send(recoveryTemplateId, Map.of(
                "email", email,
                "to_email", email,
                "name", displayName(name, email),
                "code", code));
    }

    private boolean send(String templateId, Map<String, String> templateParams) {
        if (isBlank(serviceId) || isBlank(publicKey) || isBlank(privateKey) || isBlank(templateId)) {
            log.warn("EmailJS is not configured; email to {} was not sent", templateParams.get("to_email"));
            return false;
        }
        try {
            restClient.post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "service_id", serviceId,
                            "template_id", templateId,
                            "user_id", publicKey,
                            "accessToken", privateKey,
                            "template_params", templateParams))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            log.error("EmailJS rejected the email to {}: {}", templateParams.get("to_email"), e.getMessage());
            return false;
        }
    }

    private static String displayName(String name, String email) {
        return isBlank(name) ? email : name;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
