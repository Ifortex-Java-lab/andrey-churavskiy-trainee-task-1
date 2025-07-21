package com.example.task1.dto.stripe.webhook;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StripeSubscriptionItemsDto {
  @JsonProperty("object")
  private String object;

  @JsonProperty("data")
  private List<StripeSubscriptionItemDto> data;

  @JsonProperty("has_more")
  private Boolean hasMore;

  @JsonProperty("total_count")
  private Integer totalCount;

  @JsonProperty("url")
  private String url;
}
