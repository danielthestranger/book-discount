package com.greenfoxacademy;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    Double priceForOne = 8.;

    @Test
    public void oneItemCostsFullPrice() {
        Map<String, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put("1", 1);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(priceForOne, result, 0.1);
    }

    @Test
    public void twoItemsOfTheSameUseNoDiscount() {
        Map<String, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put("1", 2);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(priceForOne * 2, result, 0.1);
    }

    @Test
    public void twoDifferentItemsUseLevel1Discount() {
        Map<String, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put("1", 1);
        orderHistogram.put("2", 1);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(priceForOne * 2 * 0.95, result, 0.1);
    }

    @Test
    public void ThreeTimes2_and_2times1_items_AreBundledInto4s() {
        Map<String, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put("1", 2);
        orderHistogram.put("2", 2);
        orderHistogram.put("3", 2);
        orderHistogram.put("4", 1);
        orderHistogram.put("5", 1);

        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(priceForOne * 4 * 0.80 * 2, result, 0.1);
    }


    @Test
    public void TwoTimes5_bundled_into_5s() {
        Map<String, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put("1", 2);
        orderHistogram.put("2", 3);
        orderHistogram.put("3", 2);
        orderHistogram.put("4", 3);
        orderHistogram.put("5", 2);

        //5*2 + 1*2 = 75.2
        //4*3 = 76.8
        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(75.2, result, 0.1);
    }

    @Test
    public void UsesBundleOf5_RatherThan4sOnly() {
        Map<String, Integer> orderHistogram = new HashMap<>();
        orderHistogram.put("1", 2);
        orderHistogram.put("2", 3);
        orderHistogram.put("3", 2);
        orderHistogram.put("4", 4);
        orderHistogram.put("5", 1);

        Double expectedResult = 5.*8.*0.75 + 4.*8.*0.8 + 2.*8.*0.95 + 1.*8.;
        Double result = Main.getLowestCostForBundles(orderHistogram);
        assertEquals(expectedResult, result, 0.1);
    }
}