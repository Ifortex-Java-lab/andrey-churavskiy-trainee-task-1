package com.example.task1.dto.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StripeWebhookEventDto {
    private String id;
    private String object;
    private String api_version;
    private Long created;
    private StripeWebhookDataDto data;
    private Boolean livemode;
    private Integer pending_webhooks;
    private StripeWebhookRequestDto request;
    private String type;
}