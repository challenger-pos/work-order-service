package com.fiap.gateway.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReservationEvent {

    private String workOrderId;
    private List<StockReservationItem> items;
}