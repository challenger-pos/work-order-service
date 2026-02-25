package com.fiap.gateway.messaging.dto;

import java.util.UUID;

public record StockApprovedItem(UUID partId, int quantity) {}