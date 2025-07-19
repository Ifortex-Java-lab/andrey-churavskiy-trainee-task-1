package com.example.task1.dto.stripe.webhook;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripeSubscriptionItemsDto {
  private String object;
  private List<StripeSubscriptionItemDto> data;
  private Boolean has_more;
  private Integer total_count;
  private String url;
}
