package com.greenfoxacademy;

import java.util.*;

public class Main {

    static Map<Integer, Double> bundleCosts;

    static {
        bundleCosts = new HashMap<>();
        bundleCosts.put(1, 8.);
        bundleCosts.put(2, 8. * 2 * 0.95);
        bundleCosts.put(3, 8. * 3 * 0.90);
        bundleCosts.put(4, 8. * 4 * 0.80);
        bundleCosts.put(5, 8. * 5 * 0.75);
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
    public static Double getLowestCostForBundles(Map<String, Integer> orderHistogram) {
        Double lowestOverallCost = Double.MAX_VALUE;
        int histogramSize = orderHistogram.size();

        Map<String, Integer> histogramCopyForTriedMaxBundleSize;
        int totalValuesInCopiedHistogram;
        List<Integer> bundlesWithLowestCost = new ArrayList<>();
        List<Integer> bundleSizesForTriedMaxBundleSize;
        Double costForTriedMaxBundleSize;
        int sizeOfThisBundle;
        int currentValueOfEntry;
        for (int triedMaxBundleSize = histogramSize; triedMaxBundleSize > 0; triedMaxBundleSize--) {
            bundleSizesForTriedMaxBundleSize = new ArrayList<>();
            histogramCopyForTriedMaxBundleSize = new HashMap<>(orderHistogram);

            sizeOfThisBundle = 0;
            totalValuesInCopiedHistogram =
                    histogramCopyForTriedMaxBundleSize
                    .values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();
            while (totalValuesInCopiedHistogram > 0) {
                for (Map.Entry<String, Integer> histogramCopyEntry : histogramCopyForTriedMaxBundleSize.entrySet()) {
                    currentValueOfEntry = histogramCopyEntry.getValue();
                    if (currentValueOfEntry > 0) {
                        if (sizeOfThisBundle < triedMaxBundleSize) {
                            sizeOfThisBundle++;
                            histogramCopyEntry.setValue(currentValueOfEntry - 1);
                        }
                    }
                }
                bundleSizesForTriedMaxBundleSize.add(sizeOfThisBundle);
                sizeOfThisBundle = 0;
                totalValuesInCopiedHistogram =
                        histogramCopyForTriedMaxBundleSize
                        .values().stream()
                        .mapToInt(Integer::intValue)
                        .sum();
            }

            costForTriedMaxBundleSize = getCostForBundles(bundleSizesForTriedMaxBundleSize);

//            System.out.println("Tried max bundle size: " + triedMaxBundleSize);
//            System.out.println("Cost: " + costForTriedMaxBundleSize);
//            System.out.println("Bundles: " + bundleSizesForTriedMaxBundleSize);

            if (costForTriedMaxBundleSize < lowestOverallCost) {
                lowestOverallCost = costForTriedMaxBundleSize;
                bundlesWithLowestCost = bundleSizesForTriedMaxBundleSize;
            }
        }

        System.out.println("Bundle config with lowest cost of " + lowestOverallCost + ": " + bundlesWithLowestCost);
        return lowestOverallCost;
    }

    private static Double getCostForBundles(List<Integer> bundleSizes) {
        Double overallCost = 0.;
        for (Integer bundleSize : bundleSizes) {
            overallCost += bundleCosts.get(bundleSize);
        }
        return overallCost;
    }

}
