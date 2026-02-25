package com.fiap.core.events;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StockRequestedEventTest {

    @Test
    void shouldCreateStockRequestedEventWithItems() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        UUID partId1 = UUID.randomUUID();
        UUID partId2 = UUID.randomUUID();
        List<StockRequestedEvent.Item> items = List.of(
                new StockRequestedEvent.Item(partId1, 5),
                new StockRequestedEvent.Item(partId2, 3)
        );

        // Act
        StockRequestedEvent event = new StockRequestedEvent(workOrderId, items);

        // Assert
        assertNotNull(event);
        assertEquals(workOrderId, event.workOrderId());
        assertEquals(2, event.items().size());
    }

    @Test
    void shouldPreserveItemDetails() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        int quantity = 10;
        List<StockRequestedEvent.Item> items = List.of(
                new StockRequestedEvent.Item(partId, quantity)
        );

        // Act
        StockRequestedEvent event = new StockRequestedEvent(workOrderId, items);
        StockRequestedEvent.Item item = event.items().get(0);

        // Assert
        assertEquals(partId, item.partId());
        assertEquals(quantity, item.quantity());
    }

    @Test
    void shouldHandleEmptyItemsList() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        List<StockRequestedEvent.Item> items = new ArrayList<>();

        // Act
        StockRequestedEvent event = new StockRequestedEvent(workOrderId, items);

        // Assert
        assertTrue(event.items().isEmpty());
    }

    @Test
    void shouldHandleMultipleItemsWithDifferentQuantities() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        UUID partId1 = UUID.randomUUID();
        UUID partId2 = UUID.randomUUID();
        UUID partId3 = UUID.randomUUID();
        List<StockRequestedEvent.Item> items = List.of(
                new StockRequestedEvent.Item(partId1, 1),
                new StockRequestedEvent.Item(partId2, 50),
                new StockRequestedEvent.Item(partId3, 100)
        );

        // Act
        StockRequestedEvent event = new StockRequestedEvent(workOrderId, items);

        // Assert
        assertEquals(3, event.items().size());
        assertEquals(1, event.items().get(0).quantity());
        assertEquals(50, event.items().get(1).quantity());
        assertEquals(100, event.items().get(2).quantity());
    }

    @Test
    void shouldCreateItemWithSpecificPartIdAndQuantity() {
        // Arrange
        UUID partId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        int quantity = 25;

        // Act
        StockRequestedEvent.Item item = new StockRequestedEvent.Item(partId, quantity);

        // Assert
        assertEquals("550e8400-e29b-41d4-a716-446655440000", item.partId().toString());
        assertEquals(25, item.quantity());
    }

    @Test
    void shouldHandleNullWorkOrderId() {
        // Arrange
        UUID partId = UUID.randomUUID();
        List<StockRequestedEvent.Item> items = List.of(
                new StockRequestedEvent.Item(partId, 5)
        );

        // Act
        StockRequestedEvent event = new StockRequestedEvent(null, items);

        // Assert
        assertNull(event.workOrderId());
        assertNotNull(event.items());
        assertEquals(1, event.items().size());
    }

    @Test
    void shouldHandleNullItemsList() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();

        // Act
        StockRequestedEvent event = new StockRequestedEvent(workOrderId, null);

        // Assert
        assertEquals(workOrderId, event.workOrderId());
        assertNull(event.items());
    }

    @Test
    void shouldHandleZeroQuantity() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        List<StockRequestedEvent.Item> items = List.of(
                new StockRequestedEvent.Item(partId, 0)
        );

        // Act
        StockRequestedEvent event = new StockRequestedEvent(workOrderId, items);

        // Assert
        assertEquals(0, event.items().get(0).quantity());
    }

    @Test
    void shouldHandleNegativeQuantity() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        List<StockRequestedEvent.Item> items = List.of(
                new StockRequestedEvent.Item(partId, -5)
        );

        // Act
        StockRequestedEvent event = new StockRequestedEvent(workOrderId, items);

        // Assert
        assertEquals(-5, event.items().get(0).quantity());
    }
}
