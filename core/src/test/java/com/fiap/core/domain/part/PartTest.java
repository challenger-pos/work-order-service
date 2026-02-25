package com.fiap.core.domain.part;

import com.fiap.core.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PartTest {

//    private static Stock newStock(int qty) throws Exception {
//        Constructor<Stock> c = Stock.class.getDeclaredConstructor(int.class, int.class, int.class);
//        c.setAccessible(true);
//        return c.newInstance(qty, 0, 0);
//    }
//
//    private static Part newPartWithStock(int initialQty) throws Exception {
//        Money price = Money.of(new BigDecimal("100.00"));
//        Stock stock = newStock(initialQty);
//
//        Part part = new Part();
//        part.setId(UUID.randomUUID());
//        part.setName("Filtro de Óleo");
//        part.setDescription("Filtro padrão");
//        part.setPrice(price);
//        part.setStock(stock);
//        part.setCreatedAt(OffsetDateTime.now());
//        part.setUpdatedAt(OffsetDateTime.now());
//        return part;
//    }
//
//    @Test
//    void subtractFromStock_decreaseQuantity() throws Exception {
//        Part part = newPartWithStock(10);
//        part.subtractFromStock(3);
//        assertEquals(7, part.getStock().getStockQuantity());
//    }
//
//    @Test
//    void subtractFromStock_throwWhenInsufficient() throws Exception {
//        Part part = newPartWithStock(2);
//        assertThrows(BusinessRuleException.class, () -> part.subtractFromStock(5));
//        assertEquals(2, part.getStock().getStockQuantity());
//    }
//
//    @Test
//    void returnToStock_afterSubtract_restoresQuantity() throws Exception {
//        Part part = newPartWithStock(10);
//        part.subtractFromStock(6);
//        assertEquals(4, part.getStock().getStockQuantity());
//        part.returnToStock(6);
//        assertEquals(10, part.getStock().getStockQuantity());
//    }
}
