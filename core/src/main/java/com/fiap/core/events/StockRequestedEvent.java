package com.fiap.core.events;

import java.util.List;
import java.util.UUID;

public record StockRequestedEvent(
        UUID workOrderId,
        List<Item> items
) {
    public record Item(UUID partId, int quantity) {}
}