package com.example.task1.service;

import com.example.task1.dto.PlanResponseDto;
import java.util.List;

public interface PlanService {
    List<PlanResponseDto> getAllPlans();
}
