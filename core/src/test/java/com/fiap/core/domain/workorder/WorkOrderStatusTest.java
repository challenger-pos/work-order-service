package com.fiap.core.domain.workorder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderStatusTest {

    @Test
    void shouldHaveAllWorkOrderStatuses() {
        // Assert
        assertNotNull(WorkOrderStatus.RECEIVED);
        assertNotNull(WorkOrderStatus.IN_DIAGNOSIS);
        assertNotNull(WorkOrderStatus.AWAITING_STOCK_CONFIRMATION);
        assertNotNull(WorkOrderStatus.REFUSED_STOCK);
        assertNotNull(WorkOrderStatus.AWAITING_APPROVAL);
        assertNotNull(WorkOrderStatus.REFUSED);
        assertNotNull(WorkOrderStatus.IN_PROGRESS);
        assertNotNull(WorkOrderStatus.COMPLETED);
        assertNotNull(WorkOrderStatus.APPROVAL_PAYMENT);
        assertNotNull(WorkOrderStatus.REFUSED_PAYMENT);
        assertNotNull(WorkOrderStatus.DELIVERED);
    }

    @Test
    void shouldGetDescriptionForStatus() {
        // Assert
        assertEquals("Recebido", WorkOrderStatus.RECEIVED.getDescription());
        assertEquals("Em diagnóstico", WorkOrderStatus.IN_DIAGNOSIS.getDescription());
        assertEquals("Em andamento", WorkOrderStatus.IN_PROGRESS.getDescription());
        assertEquals("Finalizado", WorkOrderStatus.COMPLETED.getDescription());
        assertEquals("Entregue", WorkOrderStatus.DELIVERED.getDescription());
    }

    @Test
    void shouldConvertStatusToString() {
        // Assert
        assertEquals("RECEIVED", WorkOrderStatus.RECEIVED.toString());
        assertEquals("IN_PROGRESS", WorkOrderStatus.IN_PROGRESS.toString());
        assertEquals("COMPLETED", WorkOrderStatus.COMPLETED.toString());
    }

    @Test
    void shouldConvertStringToStatus() {
        // Arrange & Act & Assert
        assertEquals(WorkOrderStatus.RECEIVED, WorkOrderStatus.valueOf("RECEIVED"));
        assertEquals(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.valueOf("IN_PROGRESS"));
        assertEquals(WorkOrderStatus.COMPLETED, WorkOrderStatus.valueOf("COMPLETED"));
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            WorkOrderStatus.valueOf("INVALID_STATUS");
        });
    }

    @Test
    void shouldCompareWorkOrderStatuses() {
        // Assert
        assertEquals(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.IN_PROGRESS);
        assertNotEquals(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.COMPLETED);
    }

    @Test
    void shouldGetAllStatuses() {
        // Arrange & Act
        WorkOrderStatus[] statuses = WorkOrderStatus.values();

        // Assert
        assertTrue(statuses.length >= 11);
        assertTrue(containsStatus(statuses, WorkOrderStatus.RECEIVED));
        assertTrue(containsStatus(statuses, WorkOrderStatus.DELIVERED));
    }

    @Test
    void shouldConvertFromString() {
        // Arrange & Act & Assert
        assertEquals(WorkOrderStatus.RECEIVED, WorkOrderStatus.fromString("RECEIVED"));
        assertEquals(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.fromString("IN_PROGRESS"));
        assertEquals(WorkOrderStatus.COMPLETED, WorkOrderStatus.fromString("COMPLETED"));
    }

    @Test
    void shouldThrowExceptionWhenConvertingNullString() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            WorkOrderStatus.fromString(null);
        });
    }

    private boolean containsStatus(WorkOrderStatus[] statuses, WorkOrderStatus targetStatus) {
        for (WorkOrderStatus status : statuses) {
            if (status.equals(targetStatus)) {
                return true;
            }
        }
        return false;
    }
}
