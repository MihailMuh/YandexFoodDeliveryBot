package com.mihalis.yandexbot.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class DeliveryData {
    private final long userId;

    private final String address;

    private final String deliveryCost;
}
