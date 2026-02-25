package com.fiap.core.events;

import java.util.UUID;

public record StockReservedEvent(UUID workOrderId) {}