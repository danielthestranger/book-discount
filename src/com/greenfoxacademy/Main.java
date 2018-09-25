package com.greenfoxacademy;

import javafx.util.Pair;

import java.util.*;

public class Main {

    static Map<Integer, Double> volumeDiscountConfig;

    static {
        volumeDiscountConfig = new HashMap<>();
        volumeDiscountConfig.put(1, 1.);
        volumeDiscountConfig.put(2, 0.95);
        volumeDiscountConfig.put(3, 0.90);
        volumeDiscountConfig.put(4, 0.80);
        volumeDiscountConfig.put(5, 0.75);
    }


    public static void main(String[] args) {

    }

    /** For each possible max bundle size, it internally creates a list of achievable bundles.
     *  E.g. for 8 order items altogether, for a max bundle size of 5 it may generate [5, 3], and for 4, [4, 4]
     *  (as long as these combinations are possible).
     *
     *  For each such bundle combination, it determines which combination has the lowest overall cost.
     *
     * @param orderHistogram expects ordered items as keys and their respective counts as values
     * @return The lowest total cost for the optimum bundle combination.
     */
    public static Double getLowestCostForBundles(Map<OrderItem, Integer> orderHistogram) {
        Double lowestOverallCost = Double.MAX_VALUE;
        int histogramSize = orderHistogram.size();

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
            histogramCopyForTriedMaxBundleSize = new HashMap<>(orderHistogram);

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

            costForTriedMaxBundleSize = getDiscountedCostForBundles(
                                            bundlesAtFullPriceForTriedMaxBundleSize,
                                            Main.volumeDiscountConfig);

//            System.out.println("Tried max bundle size: " + triedMaxBundleSize);
//            System.out.println("Cost: " + costForTriedMaxBundleSize);
//            System.out.println("Bundles: " + bundlesAtFullPriceForTriedMaxBundleSize);

            if (costForTriedMaxBundleSize < lowestOverallCost) {
                lowestOverallCost = costForTriedMaxBundleSize;
                bundleCombinationsWithLowestTotalPrice = bundlesAtFullPriceForTriedMaxBundleSize;
            }
        }

        System.out.println("Bundle config with lowest cost of " + lowestOverallCost + ": " + bundleCombinationsWithLowestTotalPrice);
        return lowestOverallCost;
    }

    private static Double getDiscountedCostForBundles(List<Pair<Integer, Double>> bundlesWithFullPrice,
                                                      Map<Integer, Double> volumeDiscountConfig) {
        Double overallCost = 0.;

        Double fullPriceForBundle = 0.;
        Integer bundleSize = 0;
        for (Pair<Integer, Double> oneBundleWithFullPrice : bundlesWithFullPrice) {
            fullPriceForBundle = oneBundleWithFullPrice.getValue();
            bundleSize = oneBundleWithFullPrice.getKey();
            overallCost += fullPriceForBundle * volumeDiscountConfig.get(bundleSize);
        }
        return overallCost;
    }
}
