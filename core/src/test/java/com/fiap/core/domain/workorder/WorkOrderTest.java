package com.fiap.core.domain.workorder;

import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.part.Money;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WorkOrderTest {

    private Part partWithStockAndPrice(UUID id, BigDecimal price) throws BusinessRuleException {
        return Part.builder()
                .id(id)
                .name("P")
                .description("D")
                .price(Money.of(price))
                .build();
    }

    private WorkOrderPart wop(UUID partId, Part part, int qty, BigDecimal unitPrice) {
        return new WorkOrderPart(partId, null, part, qty, unitPrice);
    }

    @Test
    void ctorShouldThrowWhenBothPartsAndServicesAreEmpty() {
        assertThrows(BadRequestException.class, () ->
                new WorkOrder(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(), List.of())
        );
        assertThrows(BadRequestException.class, () ->
                new WorkOrder(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, null)
        );
    }

    @Test
    void ctorShouldInitializeDefaultsWhenValid() throws BadRequestException, BusinessRuleException {
        UUID cid = UUID.randomUUID();
        UUID vid = UUID.randomUUID();
        UUID uid = UUID.randomUUID();

        Part p = partWithStockAndPrice(UUID.randomUUID(), new BigDecimal("5.00"));
        WorkOrderPart item = wop(p.getId(), p, 2, new BigDecimal("5.00"));

        WorkOrder wo = new WorkOrder(cid, vid, uid, List.of(item), null);

        assertEquals(WorkOrderStatus.RECEIVED, wo.getStatus());
        assertNotNull(wo.getCreatedAt());
        assertEquals(cid, wo.getCustomer().getId());
        assertEquals(vid, wo.getVehicle().getId());
        assertEquals(uid, wo.getCreatedBy().getId());
    }

    @Test
    void recalculateTotalShouldSumPartsOnlyWhenServicesNull() throws BadRequestException, BusinessRuleException {
        Part p1 = partWithStockAndPrice(UUID.randomUUID(), new BigDecimal("3.50"));
        Part p2 = partWithStockAndPrice(UUID.randomUUID(), new BigDecimal("2.00"));

        WorkOrderPart i1 = wop(p1.getId(), p1, 3, new BigDecimal("3.50"));
        WorkOrderPart i2 = wop(p2.getId(), p2, 2, new BigDecimal("2.00"));

        WorkOrder wo = new WorkOrder(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(i1, i2), null);
        wo.recalculateTotal();

        assertEquals(new BigDecimal("14.50"), wo.getTotalAmount());
    }

//    @Test
//    void reservePartsShouldSubtractStockAndIncreaseReserved() throws BadRequestException, BusinessRuleException {
//        Part p = partWithStockAndPrice(UUID.randomUUID(), 5, new BigDecimal("10.00"));
//        WorkOrderPart i = wop(p.getId(), p, 3, new BigDecimal("10.00"));
//
//        WorkOrder wo = new WorkOrder(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(i), null);
//        wo.reserveParts();
//
//        assertEquals(2, p.getStock().getStockQuantity());
//        assertEquals(3, p.getStock().getReservedStock());
//    }

//    @Test
//    void reservePartsShouldFailWhenInsufficientStock() throws BusinessRuleException, BadRequestException {
//        Part p = partWithStockAndPrice(UUID.randomUUID(), 2, new BigDecimal("10.00"));
//        WorkOrderPart i = wop(p.getId(), p, 3, new BigDecimal("10.00"));
//
//        WorkOrder wo = new WorkOrder(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(i), null);
//        assertThrows(BadRequestException.class, wo::reserveParts);
//    }

//    @Test
//    void approveStockShouldReduceReservedByGroupedQuantities() throws BadRequestException, BusinessRuleException {
//        UUID pid = UUID.randomUUID();
//
//        Part p = partWithStockAndPrice(pid, 10, new BigDecimal("1.00"));
//
//        WorkOrderPart i1 = wop(pid, p, 3, new BigDecimal("1.00"));
//        WorkOrderPart i2 = wop(pid, p, 2, new BigDecimal("1.00"));
//
//        WorkOrder wo = new WorkOrder(
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                List.of(i1, i2),
//                null
//        );
//
//        wo.reserveParts();
//
//        assertEquals(5, p.getStock().getStockQuantity());   // 10 - (3 + 2)
//        assertEquals(5, p.getStock().getReservedStock());   // reservado total
//
//        wo.approveStock();
//
//        assertEquals(0, p.getStock().getReservedStock());   // aprovado => reservado vai a zero
//        assertSame(i1.getPart().getStock(), i2.getPart().getStock()); // mesma referência de estoque
//    }


//    @Test
//    void restoreStockShouldReturnFromReserved() throws BadRequestException, BusinessRuleException {
//        Part p = partWithStockAndPrice(UUID.randomUUID(), 8, new BigDecimal("2.00"));
//        WorkOrderPart i = wop(p.getId(), p, 5, new BigDecimal("2.00"));
//
//        WorkOrder wo = new WorkOrder(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(i), null);
//        wo.reserveParts();
//
//        assertEquals(3, p.getStock().getStockQuantity());
//        assertEquals(5, p.getStock().getReservedStock());
//
//        wo.restoreStock();
//
//        assertEquals(8, p.getStock().getStockQuantity());
//        assertEquals(0, p.getStock().getReservedStock());
//    }

    @Test
    void secondaryCtorShouldAssignFieldsAsPassed() throws Exception {
        UUID woId = UUID.randomUUID();
        Customer customer = new Customer(UUID.randomUUID());
        Vehicle vehicle = new Vehicle(UUID.randomUUID());
        User createdBy = new User(UUID.randomUUID());
        WorkOrderStatus status = WorkOrderStatus.RECEIVED;
        BigDecimal total = new BigDecimal("123.45");
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime approvedAt = LocalDateTime.now().minusHours(12);
        LocalDateTime finishedAt = LocalDateTime.now().minusHours(1);

        WorkOrder wo = new WorkOrder(
                woId, customer, vehicle, createdBy,
                List.of(), List.of(), status, total,
                createdAt, approvedAt, finishedAt
        );

        assertEquals(woId, wo.getId());

        assertSame(customer, wo.getCustomer());
        assertSame(vehicle, wo.getVehicle());
        assertSame(createdBy, wo.getCreatedBy());

        assertEquals(customer.getId(), wo.getCustomer().getId());
        assertEquals(vehicle.getId(), wo.getVehicle().getId());
        assertEquals(createdBy.getId(), wo.getCreatedBy().getId());

        assertEquals(status, wo.getStatus());
        assertEquals(total, wo.getTotalAmount());
        assertEquals(createdAt, wo.getCreatedAt());
        assertEquals(approvedAt, wo.getApprovedAt());
        assertEquals(finishedAt, wo.getFinishedAt());
    }

}