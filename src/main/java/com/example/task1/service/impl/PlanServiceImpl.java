package com.example.task1.service.impl;

import com.example.task1.dto.PlanResponseDto;
import com.example.task1.exception.StripeApiException;
import com.example.task1.service.PlanService;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.Product;
import com.stripe.param.PriceListParams;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlanServiceImpl implements PlanService {

  public List<PlanResponseDto> getAllPlans() {
    log.info("Fetching all Stripe pricing plans");
    List<PlanResponseDto> plans = new ArrayList<>();
    try {
      PriceCollection prices = Price.list(PriceListParams.builder().setActive(true).build());
      log.debug("Fetched {} prices from Stripe", prices.getData().size());
      for (Price price : prices.getData()) {
        Product product = Product.retrieve(price.getProduct());
        log.debug("Retrieved product '{}' for priceId '{}'", product.getName(), price.getId());
        plans.add(
            PlanResponseDto.builder()
                .priceId(price.getId())
                .productName(product.getName())
                .amount(price.getUnitAmount())
                .currency(price.getCurrency())
                .interval(price.getRecurring() != null ? price.getRecurring().getInterval() : null)
                .build());
      }
      log.info("Returning {} plans", plans.size());
    } catch (Exception e) {
      log.error("Error getting pricing plans from Stripe", e);
      throw new StripeApiException("Error getting pricing plans from Stripe", e);
    }
    return plans;
  }
}
