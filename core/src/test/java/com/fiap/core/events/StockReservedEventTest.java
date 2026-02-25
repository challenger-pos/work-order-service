package com.fiap.core.events;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StockReservedEventTest {

    @Test
    void shouldCreateStockReservedEvent() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();

        // Act
        StockReservedEvent event = new StockReservedEvent(workOrderId);

        // Assert
        assertNotNull(event);
        assertEquals(workOrderId, event.workOrderId());
    }

    @Test
    void shouldHandleMultipleStockReservedEvents() {
        // Arrange
        UUID workOrderId1 = UUID.randomUUID();
        UUID workOrderId2 = UUID.randomUUID();

        // Act
        StockReservedEvent event1 = new StockReservedEvent(workOrderId1);
        StockReservedEvent event2 = new StockReservedEvent(workOrderId2);

        // Assert
        assertEquals(workOrderId1, event1.workOrderId());
        assertEquals(workOrderId2, event2.workOrderId());
        assertNotEquals(event1.workOrderId(), event2.workOrderId());
    }

    @Test
    void shouldPreserveWorkOrderIdInEvent() {
        // Arrange
        UUID workOrderId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        // Act
        StockReservedEvent event = new StockReservedEvent(workOrderId);

        // Assert
        assertEquals("550e8400-e29b-41d4-a716-446655440000", event.workOrderId().toString());
    }

    @Test
    void shouldHandleNullWorkOrderId() {
        // Arrange & Act
        StockReservedEvent event = new StockReservedEvent(null);

        // Assert
        assertNull(event.workOrderId());
    }
}
