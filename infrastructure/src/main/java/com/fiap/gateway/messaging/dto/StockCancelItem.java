package com.fiap.gateway.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCancelItem {
    private UUID partId;
    private int quantity;
}