package com.greenfoxacademy;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MultiBuyDiscountTests {

    private List<Product> products;
    private Map<Integer, Double> multiBuyDiscountMultipliers;

    public MultiBuyDiscountTests() {
        products = new ArrayList<>();
        products.add(new Product("DummyFor0Index", 0.));
        products.add(new Product("One", 8.));
        products.add(new Product("Two", 8.));
        products.add(new Product("Three", 8.));
        products.add(new Product("Four", 8.));
        products.add(new Product("Five", 8.));

        multiBuyDiscountMultipliers = new HashMap<>();
        multiBuyDiscountMultipliers.put(1, 1.);
        multiBuyDiscountMultipliers.put(2, 0.95);
        multiBuyDiscountMultipliers.put(3, 0.90);
        multiBuyDiscountMultipliers.put(4, 0.80);
        multiBuyDiscountMultipliers.put(5, 0.75);
    }

    @Test
    public void oneItemCostsFullPrice() {
        Map<Product, Integer> orderItemHistogram = new HashMap<>();
        orderItemHistogram.put(products.get(1), 1);
        MultiBuyDiscountCalculator calculator =
                new MultiBuyDiscountCalculator(multiBuyDiscountMultipliers, orderItemHistogram);

        Double result = calculator.getDiscountedTotalPrice();
        assertEquals(products.get(1).getUnitPrice(), result, 0.1);
    }

    @Test
    public void twoItemsOfTheSameUseNoDiscount() {
        Map<Product, Integer> orderItemHistogram = new HashMap<>();
        orderItemHistogram.put(products.get(1), 2);
        MultiBuyDiscountCalculator calculator =
                new MultiBuyDiscountCalculator(multiBuyDiscountMultipliers, orderItemHistogram);
        
        Double result = calculator.getDiscountedTotalPrice();
        assertEquals(products.get(1).getUnitPrice() * 2, result, 0.1);
    }

    @Test
    public void twoDifferentItemsUseLevel1Discount() {
        Map<Product, Integer> orderItemHistogram = new HashMap<>();
        orderItemHistogram.put(products.get(1), 1);
        orderItemHistogram.put(products.get(2), 1);
        MultiBuyDiscountCalculator calculator =
                new MultiBuyDiscountCalculator(multiBuyDiscountMultipliers, orderItemHistogram);

        Double result = calculator.getDiscountedTotalPrice();
        assertEquals((products.get(1).getUnitPrice() + products.get(2).getUnitPrice()) * 0.95, result, 0.1);
    }

    @Test
    public void ThreeTimes2_and_2times1_items_AreBundledInto4s() {
        Map<Product, Integer> orderItemHistogram = new HashMap<>();
        orderItemHistogram.put(products.get(1), 2);
        orderItemHistogram.put(products.get(2), 2);
        orderItemHistogram.put(products.get(3), 2);
        orderItemHistogram.put(products.get(4), 1);
        orderItemHistogram.put(products.get(5), 1);
        MultiBuyDiscountCalculator calculator =
                new MultiBuyDiscountCalculator(multiBuyDiscountMultipliers, orderItemHistogram);

        Double result = calculator.getDiscountedTotalPrice();
        assertEquals(8. * 4 * 0.80 * 2, result, 0.1);
    }


    @Test
    public void TwoTimes5_bundled_into_5s() {
        Map<Product, Integer> orderItemHistogram = new HashMap<>();
        orderItemHistogram.put(products.get(1), 2);
        orderItemHistogram.put(products.get(2), 3);
        orderItemHistogram.put(products.get(3), 2);
        orderItemHistogram.put(products.get(4), 3);
        orderItemHistogram.put(products.get(5), 2);
        MultiBuyDiscountCalculator calculator =
                new MultiBuyDiscountCalculator(multiBuyDiscountMultipliers, orderItemHistogram);
        //5*2 + 1*2 = 75.2
        //4*3 = 76.8
        Double result = calculator.getDiscountedTotalPrice();
        assertEquals(75.2, result, 0.1);
    }

    @Test
    public void UsesBundleOf5_RatherThan4sOnly() {
        Map<Product, Integer> orderItemHistogram = new HashMap<>();
        orderItemHistogram.put(products.get(1), 2);
        orderItemHistogram.put(products.get(2), 3);
        orderItemHistogram.put(products.get(3), 2);
        orderItemHistogram.put(products.get(4), 4);
        orderItemHistogram.put(products.get(5), 1);
        MultiBuyDiscountCalculator calculator =
                new MultiBuyDiscountCalculator(multiBuyDiscountMultipliers, orderItemHistogram);

        Double expectedResult = 5.*8.*0.75 + 4.*8.*0.8 + 2.*8.*0.95 + 1.*8.;
        Double result = calculator.getDiscountedTotalPrice();
        assertEquals(expectedResult, result, 0.1);
    }
}