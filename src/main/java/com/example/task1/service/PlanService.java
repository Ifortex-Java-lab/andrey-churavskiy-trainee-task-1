package com.example.task1.service;

import com.example.task1.dto.PlanResponseDto;
import com.stripe.Stripe;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.Product;
import com.stripe.param.PriceListParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public List<PlanResponseDto> getAllPlans() {
        List<PlanResponseDto> plans = new ArrayList<>();
        try {
            PriceCollection prices = Price.list(PriceListParams.builder().setActive(true).build());
            for (Price price : prices.getData()) {
                Product product = Product.retrieve(price.getProduct());
                plans.add(PlanResponseDto.builder()
                        .priceId(price.getId())
                        .productName(product.getName())
                        .amount(price.getUnitAmount())
                        .currency(price.getCurrency())
                        .interval(price.getRecurring() != null ? price.getRecurring().getInterval() : null)
                        .build());
            }
        } catch (Exception e) {

            throw new RuntimeException("\n" + "Error getting pricing plans from Stripe", e);
        }
        return plans;
    }
}