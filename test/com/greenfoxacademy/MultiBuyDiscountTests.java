package com.greenfoxacademy;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MultiBuyDiscountTests {

    private List<OrderItem> orderItems;

    public MultiBuyDiscountTests() {
        orderItems = new ArrayList<>();
        orderItems.add(new OrderItem("Dummy", 0.));
        orderItems.add(new OrderItem("One", 8.));
        orderItems.add(new OrderItem("Two", 8.));
        orderItems.add(new OrderItem("Three", 8.));
        orderItems.add(new OrderItem("Four", 8.));
        orderItems.add(new OrderItem("Five", 8.));
    }

    private MultiBuyDiscountCalculator calculator = new MultiBuyDiscountCalculator();

    @Test
    public void oneItemCostsFullPrice() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(orderItems.get(1), 1);

        Double result = calculator.getLowestCostForBundles(orderHistogram);
        assertEquals(orderItems.get(1).getUnitPrice(), result, 0.1);
    }

    @Test
    public void twoItemsOfTheSameUseNoDiscount() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(orderItems.get(1), 2);

        Double result = calculator.getLowestCostForBundles(orderHistogram);
        assertEquals(orderItems.get(1).getUnitPrice() * 2, result, 0.1);
    }

    @Test
    public void twoDifferentItemsUseLevel1Discount() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(orderItems.get(1), 1);
        orderHistogram.put(orderItems.get(2), 1);

        Double result = calculator.getLowestCostForBundles(orderHistogram);
        assertEquals((orderItems.get(1).getUnitPrice() + orderItems.get(2).getUnitPrice()) * 0.95, result, 0.1);
    }

    @Test
    public void ThreeTimes2_and_2times1_items_AreBundledInto4s() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(orderItems.get(1), 2);
        orderHistogram.put(orderItems.get(2), 2);
        orderHistogram.put(orderItems.get(3), 2);
        orderHistogram.put(orderItems.get(4), 1);
        orderHistogram.put(orderItems.get(5), 1);

        Double result = calculator.getLowestCostForBundles(orderHistogram);
        assertEquals(8. * 4 * 0.80 * 2, result, 0.1);
    }


    @Test
    public void TwoTimes5_bundled_into_5s() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(orderItems.get(1), 2);
        orderHistogram.put(orderItems.get(2), 3);
        orderHistogram.put(orderItems.get(3), 2);
        orderHistogram.put(orderItems.get(4), 3);
        orderHistogram.put(orderItems.get(5), 2);

        //5*2 + 1*2 = 75.2
        //4*3 = 76.8
        Double result = calculator.getLowestCostForBundles(orderHistogram);
        assertEquals(75.2, result, 0.1);
    }

    @Test
    public void UsesBundleOf5_RatherThan4sOnly() {
        Map<OrderItem, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put(orderItems.get(1), 2);
        orderHistogram.put(orderItems.get(2), 3);
        orderHistogram.put(orderItems.get(3), 2);
        orderHistogram.put(orderItems.get(4), 4);
        orderHistogram.put(orderItems.get(5), 1);

        Double expectedResult = 5.*8.*0.75 + 4.*8.*0.8 + 2.*8.*0.95 + 1.*8.;
        Double result = calculator.getLowestCostForBundles(orderHistogram);
        assertEquals(expectedResult, result, 0.1);
    }
}