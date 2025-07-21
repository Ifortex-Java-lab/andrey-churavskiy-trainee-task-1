package com.example.task1.dto.stripe.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StripeWebhookEventDto {
  @JsonProperty("id")
  private String id;

  @JsonProperty("object")
  private String object;

  @JsonProperty("api_version")
  private String apiVersion;

  @JsonProperty("created")
  private Long created;

  @JsonProperty("data")
  private StripeWebhookDataDto data;

  @JsonProperty("livemode")
  private Boolean livemode;

  @JsonProperty("pending_webhooks")
  private Integer pendingWebhooks;

  @JsonProperty("request")
  private StripeWebhookRequestDto request;

  @JsonProperty("type")
  private String type;
}