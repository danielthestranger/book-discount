package com.greenfoxacademy;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiBuyDiscountCalculator {

    private Map<Integer, Double> multiBuyDiscountMultipliers;

    public MultiBuyDiscountCalculator() {
        multiBuyDiscountMultipliers = new HashMap<>();
        multiBuyDiscountMultipliers.put(1, 1.);
        multiBuyDiscountMultipliers.put(2, 0.95);
        multiBuyDiscountMultipliers.put(3, 0.90);
        multiBuyDiscountMultipliers.put(4, 0.80);
        multiBuyDiscountMultipliers.put(5, 0.75);
    }

    /** For each possible max bundle size, it internally creates a list of achievable bundles.
     *  E.g. for 8 order items altogether, for a max bundle size of 5 it may generate [5, 3], and for 4, [4, 4]
     *  (as long as these combinations are possible).
     *
     *  For each such bundle combination, it determines which combination has the lowest overall price.
     *
     * @param orderItemHistogram expects OrderItem as keys and their respective counts as values
     * @return The lowest total cost for the optimum bundle combination.
     */
    public Double getLowestCostForBundles(Map<OrderItem, Integer> orderItemHistogram) {
        Double lowestOverallCost = Double.MAX_VALUE;
        int histogramSize = orderItemHistogram.size();

        Map<OrderItem, Integer> histogramCopyForTriedMaxBundleSize;
        int totalValuesInCopiedHistogram;
        List<Pair<Integer, Double>> bundleCombinationsWithLowestTotalPrice = new ArrayList<>();
        List<Pair<Integer, Double>> bundlesAtFullPriceForTriedMaxBundleSize;
        Double costForTriedMaxBundleSize;
        int sizeOfThisBundle;
        Double fullPriceOfThisBundle;
        int currentValueOfEntry;
        for (int triedMaxBundleSize = histogramSize; triedMaxBundleSize > 0; triedMaxBundleSize--) {
            bundlesAtFullPriceForTriedMaxBundleSize = new ArrayList<>();
            histogramCopyForTriedMaxBundleSize = new HashMap<>(orderItemHistogram);

            sizeOfThisBundle = 0;
            fullPriceOfThisBundle = 0.;
            totalValuesInCopiedHistogram =
                    histogramCopyForTriedMaxBundleSize
                            .values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
            while (totalValuesInCopiedHistogram > 0) {
                for (Map.Entry<OrderItem, Integer> histogramCopyEntry : histogramCopyForTriedMaxBundleSize.entrySet()) {
                    currentValueOfEntry = histogramCopyEntry.getValue();
                    if (currentValueOfEntry > 0) {
                        if (sizeOfThisBundle < triedMaxBundleSize) {
                            sizeOfThisBundle++;
                            fullPriceOfThisBundle = fullPriceOfThisBundle + histogramCopyEntry.getKey().getUnitPrice();
                            histogramCopyEntry.setValue(currentValueOfEntry - 1);
                        }
                    }
                }
                bundlesAtFullPriceForTriedMaxBundleSize.add(new Pair<>(sizeOfThisBundle, fullPriceOfThisBundle));
                sizeOfThisBundle = 0;
                fullPriceOfThisBundle = 0.;
                totalValuesInCopiedHistogram =
                        histogramCopyForTriedMaxBundleSize
                                .values().stream()
                                .mapToInt(Integer::intValue)
                                .sum();
            }

            costForTriedMaxBundleSize = getDiscountedCostForBundles(bundlesAtFullPriceForTriedMaxBundleSize);

//            System.out.println("Tried max bundle size: " + triedMaxBundleSize);
//            System.out.println("Cost: " + costForTriedMaxBundleSize);
//            System.out.println("Bundles: " + bundlesAtFullPriceForTriedMaxBundleSize);

            if (costForTriedMaxBundleSize < lowestOverallCost) {
                lowestOverallCost = costForTriedMaxBundleSize;
                bundleCombinationsWithLowestTotalPrice = bundlesAtFullPriceForTriedMaxBundleSize;
            }
        }

        System.out.println("Bundle config with lowest total price of "
                + lowestOverallCost
                + ": "
                + bundleCombinationsWithLowestTotalPrice);
        return lowestOverallCost;
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
        return fullPriceOfBundle * multiBuyDiscountMultipliers.get(bundleSize);
    }

}
