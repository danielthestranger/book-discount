package com.greenfoxacademy;

import javafx.util.Pair;

import java.util.*;

public class MultiBuyDiscountCalculator {

    private Map<Integer, Double> multiBuyDiscountMultipliers;
    private Map<Product, Integer> productHistogram;

    public MultiBuyDiscountCalculator(Map<Integer, Double> multiBuyDiscountMultipliers,
                                      Map<Product, Integer> productHistogram) {
        this.multiBuyDiscountMultipliers = multiBuyDiscountMultipliers;
        this.productHistogram = productHistogram;
    }

    /** For each possible max bundle size, it internally creates a list of achievable bundles of distinct products.
     *  E.g. for 8 products altogether, for a max bundle size of 5 it may generate [5, 3], and for 4, [4, 4]
     *  (as long as these combinations are possible).
     *
     *  For each such bundle combination, it determines which combination has the lowest overall price.
     *
     * @return The lowest total cost for the optimum bundle combination.
     */
    public Double getDiscountedTotalPrice() {
        Double lowestDiscountedTotal = Double.MAX_VALUE;
        List<Pair<Integer, Double>> bundlesWithLowestDiscountedTotal = new ArrayList<>();

        Integer largestBundleSizeWithDiscountMultiplier = Collections.max(multiBuyDiscountMultipliers.keySet());
        int bundleSizesToTry = Math.min(productHistogram.size(), largestBundleSizeWithDiscountMultiplier);

        Map<Product, Integer> histogramCopyForTriedMaxBundleSize;
        int remainingItemCountInHistogramCopy;
        List<Pair<Integer, Double>> bundlesAtFullPriceForTriedMaxBundleSize;
        Double discountedTotalForTriedMaxBundleSize;
        int sizeOfThisBundle;
        Double fullPriceOfThisBundle;
        int count;
        for (int triedMaxBundleSize = bundleSizesToTry; triedMaxBundleSize > 0; triedMaxBundleSize--) {
            bundlesAtFullPriceForTriedMaxBundleSize = new ArrayList<>();
            histogramCopyForTriedMaxBundleSize = new HashMap<>(productHistogram);

            sizeOfThisBundle = 0;
            fullPriceOfThisBundle = 0.;
            remainingItemCountInHistogramCopy =
                            histogramCopyForTriedMaxBundleSize
                            .values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
            while (remainingItemCountInHistogramCopy > 0) {
                for (Map.Entry<Product, Integer> countOfProduct : histogramCopyForTriedMaxBundleSize.entrySet()) {
                    count = countOfProduct.getValue();
                    if (count > 0) {
                        if (sizeOfThisBundle < triedMaxBundleSize) {
                            sizeOfThisBundle++;
                            fullPriceOfThisBundle = fullPriceOfThisBundle + countOfProduct.getKey().getUnitPrice();
                            countOfProduct.setValue(count - 1);
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

            discountedTotalForTriedMaxBundleSize = getDiscountedTotalForBundles(bundlesAtFullPriceForTriedMaxBundleSize);

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

    private Double getDiscountedTotalForBundles(List<Pair<Integer, Double>> bundleSizesWithFullPrice) {
        Double discountedTotal = 0.;

        Double fullPriceOfBundle = 0.;
        Integer bundleSize = 0;
        for (Pair<Integer, Double> oneBundleWithFullPrice : bundleSizesWithFullPrice) {
            fullPriceOfBundle = oneBundleWithFullPrice.getValue();
            bundleSize = oneBundleWithFullPrice.getKey();
            discountedTotal += getDiscountedTotalForBundle(bundleSize, fullPriceOfBundle);
        }
        return discountedTotal;
    }

    private Double getDiscountedTotalForBundle(Integer bundleSize, Double fullPriceOfBundle) {
        return  multiBuyDiscountMultipliers.getOrDefault(bundleSize, 1.) * fullPriceOfBundle;
    }

}
