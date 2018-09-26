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

    /** For each possible max set size, it internally creates a list of achievable sets of distinct products.
     *  E.g. for 8 products altogether, for a max set size of 5 it may generate [5, 3], and for 4, [4, 4]
     *  (as long as these combinations are possible).
     *
     *  For each such set combination, it determines which combination has the lowest overall price.
     *
     * @return The lowest total cost for the optimum set combination.
     */
    public Double getDiscountedTotalPrice() {
        Double lowestDiscountedTotal = Double.MAX_VALUE;
        List<Pair<Integer, Double>> setsWithLowestDiscountedTotal = new ArrayList<>();

        int largestSetSizeWithDiscountMultiplier = Collections.max(multiBuyDiscountMultipliers.keySet());
        int setSizesToTry = Math.min(productHistogram.size(), largestSetSizeWithDiscountMultiplier);

        Map<Product, Integer> histogramCopyForTriedMaxSetSize;
        int remainingItemCountInHistogramCopy;
        List<Pair<Integer, Double>> setsAtFullPriceForTriedMaxSetSize;
        Double discountedTotalForTriedMaxSetSize;
        int sizeOfThisSet;
        Double fullPriceOfThisSet;
        int count;
        for (int triedMaxSetSize = setSizesToTry; triedMaxSetSize > 0; triedMaxSetSize--) {
            setsAtFullPriceForTriedMaxSetSize = new ArrayList<>();
            histogramCopyForTriedMaxSetSize = new HashMap<>(productHistogram);

            sizeOfThisSet = 0;
            fullPriceOfThisSet = 0.;
            remainingItemCountInHistogramCopy =
                            histogramCopyForTriedMaxSetSize
                            .values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
            while (remainingItemCountInHistogramCopy > 0) {
                for (Map.Entry<Product, Integer> countOfProduct : histogramCopyForTriedMaxSetSize.entrySet()) {
                    count = countOfProduct.getValue();
                    if (count > 0) {
                        if (sizeOfThisSet < triedMaxSetSize) {
                            sizeOfThisSet++;
                            fullPriceOfThisSet = fullPriceOfThisSet + countOfProduct.getKey().getUnitPrice();
                            countOfProduct.setValue(count - 1);
                        }
                    }
                }
                setsAtFullPriceForTriedMaxSetSize.add(new Pair<>(sizeOfThisSet, fullPriceOfThisSet));
                sizeOfThisSet = 0;
                fullPriceOfThisSet = 0.;
                remainingItemCountInHistogramCopy =
                                histogramCopyForTriedMaxSetSize
                                .values().stream()
                                .mapToInt(Integer::intValue)
                                .sum();
            }

            discountedTotalForTriedMaxSetSize = getDiscountedTotalForSets(setsAtFullPriceForTriedMaxSetSize);

//            System.out.println("Tried max set size: " + triedMaxSetSize);
//            System.out.println("Cost: " + discountedTotalForTriedMaxSetSize);
//            System.out.println("Sets: " + setsAtFullPriceForTriedMaxSetSize);

            if (discountedTotalForTriedMaxSetSize < lowestDiscountedTotal) {
                lowestDiscountedTotal = discountedTotalForTriedMaxSetSize;
                setsWithLowestDiscountedTotal = setsAtFullPriceForTriedMaxSetSize;
            }
        }

        System.out.println("Set config with lowest total price of "
                            + lowestDiscountedTotal
                            + ": "
                            + setsWithLowestDiscountedTotal);
        return lowestDiscountedTotal;
    }

    private Double getDiscountedTotalForSets(List<Pair<Integer, Double>> setSizesWithFullPrice) {
        Double discountedTotal = 0.;

        Double fullPriceOfSet = 0.;
        Integer setSize = 0;
        for (Pair<Integer, Double> oneSetWithFullPrice : setSizesWithFullPrice) {
            fullPriceOfSet = oneSetWithFullPrice.getValue();
            setSize = oneSetWithFullPrice.getKey();
            discountedTotal += getDiscountedTotalForSet(setSize, fullPriceOfSet);
        }
        return discountedTotal;
    }

    private Double getDiscountedTotalForSet(Integer setSize, Double fullPriceOfSet) {
        return  multiBuyDiscountMultipliers.getOrDefault(setSize, 1.) * fullPriceOfSet;
    }

}
