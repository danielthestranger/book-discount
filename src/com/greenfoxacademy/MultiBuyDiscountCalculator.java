package com.greenfoxacademy;

import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiBuyDiscountCalculator {

    private Map<Integer, Double> multiBuyDiscountMultipliers;
    private Map<OrderItem, Integer> orderItemHistogram;

    public MultiBuyDiscountCalculator(Map<Integer, Double> multiBuyDiscountMultipliers,
                                      Map<OrderItem, Integer> orderItemHistogram) {
        this.multiBuyDiscountMultipliers = multiBuyDiscountMultipliers;
        this.orderItemHistogram = orderItemHistogram;
    }

    /** For each possible max bundle size, it internally creates a list of achievable bundles.
     *  E.g. for 8 order items altogether, for a max bundle size of 5 it may generate [5, 3], and for 4, [4, 4]
     *  (as long as these combinations are possible).
     *
     *  For each such bundle combination, it determines which combination has the lowest overall price.
     *
     * @return The lowest total cost for the optimum bundle combination.
     */
    public Double getDiscountedTotalPrice() {
        Double lowestDiscountedTotal = Double.MAX_VALUE;
        List<Pair<Integer, Double>> bundlesWithLowestDiscountedTotal = new ArrayList<>();

        Map<OrderItem, Integer> histogramCopyForTriedMaxBundleSize;
        int remainingItemCountInHistogramCopy;
        List<Pair<Integer, Double>> bundlesAtFullPriceForTriedMaxBundleSize;
        Double discountedTotalForTriedMaxBundleSize;
        int sizeOfThisBundle;
        Double fullPriceOfThisBundle;
        int count;
        for (int triedMaxBundleSize = orderItemHistogram.size(); triedMaxBundleSize > 0; triedMaxBundleSize--) {
            bundlesAtFullPriceForTriedMaxBundleSize = new ArrayList<>();
            histogramCopyForTriedMaxBundleSize = new HashMap<>(orderItemHistogram);

            sizeOfThisBundle = 0;
            fullPriceOfThisBundle = 0.;
            remainingItemCountInHistogramCopy =
                            histogramCopyForTriedMaxBundleSize
                            .values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
            while (remainingItemCountInHistogramCopy > 0) {
                for (Map.Entry<OrderItem, Integer> countOfOrderItem : histogramCopyForTriedMaxBundleSize.entrySet()) {
                    count = countOfOrderItem.getValue();
                    if (count > 0) {
                        if (sizeOfThisBundle < triedMaxBundleSize) {
                            sizeOfThisBundle++;
                            fullPriceOfThisBundle = fullPriceOfThisBundle + countOfOrderItem.getKey().getUnitPrice();
                            countOfOrderItem.setValue(count - 1);
                        }
                    }
                }
                bundlesAtFullPriceForTriedMaxBundleSize.add(new Pair<>(sizeOfThisBundle, fullPriceOfThisBundle));
                sizeOfThisBundle = 0;
                fullPriceOfThisBundle = 0.;
                remainingItemCountInHistogramCopy =
                                histogramCopyForTriedMaxBundleSize
                                .values().stream()
                                .mapToInt(Integer::intValue)
                                .sum();
            }

            discountedTotalForTriedMaxBundleSize = getDiscountedCostForBundles(bundlesAtFullPriceForTriedMaxBundleSize);

//            System.out.println("Tried max bundle size: " + triedMaxBundleSize);
//            System.out.println("Cost: " + discountedTotalForTriedMaxBundleSize);
//            System.out.println("Bundles: " + bundlesAtFullPriceForTriedMaxBundleSize);

            if (discountedTotalForTriedMaxBundleSize < lowestDiscountedTotal) {
                lowestDiscountedTotal = discountedTotalForTriedMaxBundleSize;
                bundlesWithLowestDiscountedTotal = bundlesAtFullPriceForTriedMaxBundleSize;
            }
        }

        System.out.println("Bundle config with lowest total price of "
                            + lowestDiscountedTotal
                            + ": "
                            + bundlesWithLowestDiscountedTotal);
        return lowestDiscountedTotal;
    }

    private Double getDiscountedCostForBundles(List<Pair<Integer, Double>> bundleSizesWithFullPrice) {
        Double overallCost = 0.;

        Double fullPriceOfBundle = 0.;
        Integer bundleSize = 0;
        for (Pair<Integer, Double> oneBundleWithFullPrice : bundleSizesWithFullPrice) {
            fullPriceOfBundle = oneBundleWithFullPrice.getValue();
            bundleSize = oneBundleWithFullPrice.getKey();
            overallCost += getDiscountedCostForBundle(bundleSize, fullPriceOfBundle);
        }
        return overallCost;
    }

    private Double getDiscountedCostForBundle(Integer bundleSize, Double fullPriceOfBundle) {
        return  multiBuyDiscountMultipliers.get(bundleSize) * fullPriceOfBundle;
    }

}
