package com.fiap.core.events;

import java.util.UUID;

public record StockFailedEvent(UUID workOrderId, String reason) {}