package com.example.task1.dto.stripe.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StripeSubscriptionDto {
  @JsonProperty("id")
  private String id;

  @JsonProperty("object")
  private String object;

  @JsonProperty("status")
  private String status;

  @JsonProperty("customer")
  private String customer;

  @JsonProperty("items")
  private StripeSubscriptionItemsDto items;
}