package com.fiap.core.events;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StockFailedEventTest {

    @Test
    void shouldCreateStockFailedEvent() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        String reason = "Item out of stock";

        // Act
        StockFailedEvent event = new StockFailedEvent(workOrderId, reason);

        // Assert
        assertNotNull(event);
        assertEquals(workOrderId, event.workOrderId());
        assertEquals(reason, event.reason());
    }

    @Test
    void shouldHandleMultipleFailureReasons() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        String reason1 = "Insufficient inventory";
        String reason2 = "Item not available";

        // Act
        StockFailedEvent event1 = new StockFailedEvent(workOrderId, reason1);
        StockFailedEvent event2 = new StockFailedEvent(workOrderId, reason2);

        // Assert
        assertEquals(reason1, event1.reason());
        assertEquals(reason2, event2.reason());
        assertEquals(event1.workOrderId(), event2.workOrderId());
    }

    @Test
    void shouldPreserveWorkOrderIdAndReason() {
        // Arrange
        UUID workOrderId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String reason = "Damaged parts found";

        // Act
        StockFailedEvent event = new StockFailedEvent(workOrderId, reason);

        // Assert
        assertEquals("550e8400-e29b-41d4-a716-446655440000", event.workOrderId().toString());
        assertEquals("Damaged parts found", event.reason());
    }

    @Test
    void shouldHandleNullReason() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();

        // Act
        StockFailedEvent event = new StockFailedEvent(workOrderId, null);

        // Assert
        assertEquals(workOrderId, event.workOrderId());
        assertNull(event.reason());
    }

    @Test
    void shouldHandleEmptyReason() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        String emptyReason = "";

        // Act
        StockFailedEvent event = new StockFailedEvent(workOrderId, emptyReason);

        // Assert
        assertEquals("", event.reason());
    }

    @Test
    void shouldHandleNullWorkOrderId() {
        // Arrange
        String reason = "Unknown failure";

        // Act
        StockFailedEvent event = new StockFailedEvent(null, reason);

        // Assert
        assertNull(event.workOrderId());
        assertEquals(reason, event.reason());
    }

    @Test
    void shouldHandleLongReasonMessage() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        String longReason = "The requested item is out of stock due to high demand. Expected restock in 2 weeks.";

        // Act
        StockFailedEvent event = new StockFailedEvent(workOrderId, longReason);

        // Assert
        assertEquals(longReason, event.reason());
    }
}
