package com.example.task1.mapper;

import com.example.task1.dto.SubscriptionDto;
import com.example.task1.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(target = "userId", source = "user.id")
    SubscriptionDto toDto(Subscription entity);

    @Mapping(target = "user.id", source = "userId")
    Subscription toEntity(SubscriptionDto dto);
}
