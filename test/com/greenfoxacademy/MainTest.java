package com.greenfoxacademy;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private OrderItem item1 = new OrderItem("One", 8.);
    private OrderItem item2 = new OrderItem("Two", 8.);
    private OrderItem item3 = new OrderItem("Three", 8.);
    private OrderItem item4 = new OrderItem("Four", 8.);
    private OrderItem item5 = new OrderItem("Five", 8.);

    @Test
    public void oneItemCostsFullPrice() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(item1, 1);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(item1.getUnitPrice(), result, 0.1);
    }

    @Test
    public void twoItemsOfTheSameUseNoDiscount() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(item1, 2);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(item1.getUnitPrice() * 2, result, 0.1);
    }

    @Test
    public void twoDifferentItemsUseLevel1Discount() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(item1, 1);
        orderHistogram.put(item2, 1);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals((item1.getUnitPrice() + item2.getUnitPrice()) * 0.95, result, 0.1);
    }

    @Test
    public void ThreeTimes2_and_2times1_items_AreBundledInto4s() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(item1, 2);
        orderHistogram.put(item2, 2);
        orderHistogram.put(item3, 2);
        orderHistogram.put(item4, 1);
        orderHistogram.put(item5, 1);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(8. * 4 * 0.80 * 2, result, 0.1);
    }


    @Test
    public void TwoTimes5_bundled_into_5s() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(item1, 2);
        orderHistogram.put(item2, 3);
        orderHistogram.put(item3, 2);
        orderHistogram.put(item4, 3);
        orderHistogram.put(item5, 2);

        //5*2 + 1*2 = 75.2
        //4*3 = 76.8
        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(75.2, result, 0.1);
    }

    @Test
    public void UsesBundleOf5_RatherThan4sOnly() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(item1, 2);
        orderHistogram.put(item2, 3);
        orderHistogram.put(item3, 2);
        orderHistogram.put(item4, 4);
        orderHistogram.put(item5, 1);

        Double expectedResult = 5.*8.*0.75 + 4.*8.*0.8 + 2.*8.*0.95 + 1.*8.;
        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(expectedResult, result, 0.1);
    }
}