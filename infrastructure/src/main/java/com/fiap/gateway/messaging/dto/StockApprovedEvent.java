package com.fiap.gateway.messaging.dto;

import java.util.List;
import java.util.UUID;

public record StockApprovedEvent(
        UUID workOrderId,
        List<StockApprovedItem> items
) {}