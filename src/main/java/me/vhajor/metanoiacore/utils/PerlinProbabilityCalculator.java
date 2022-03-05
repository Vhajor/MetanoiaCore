package me.vhajor.metanoiacore.utils;

import me.vhajor.metanoiacore.enums.PerlinPercentages;

import java.util.*;

public class PerlinProbabilityCalculator {

    private PerlinProbabilityCalculator() {
        throw new IllegalStateException("Utility Class");
    }

    public static Map<Object, Double[]> calculatePerlinProbabilities(Map<Object, Integer> weightedInputs) {

        Map<Object, Double> percentageInputs = new HashMap<>();
        Map<Object, Double[]> thresholdInputs = new HashMap<>();

        int totalWeights = 0;

        for (Map.Entry<Object, Integer> weightedInput : weightedInputs.entrySet()) // get total weight
            totalWeights += weightedInput.getValue();

        if (totalWeights == 0) {

            return Collections.emptyMap();
        }

        for (Map.Entry<Object, Integer> weightedInput : weightedInputs.entrySet()) // set the percentage inputs based on total weight
            percentageInputs.put(weightedInput.getKey(), weightedInput.getValue() / ((double) totalWeights));

        ArrayList<Object> stackedKeys = new ArrayList<>(percentageInputs.keySet());

        double currentTotal = 0D;
        double previousThreshold = -1D;

        for (PerlinPercentages.perlinPercentage perlinPercentage : PerlinPercentages.perlinPercentage.values()) {

            Object key = stackedKeys.get(0);

            if (percentageInputs.size() == 1) // If only one left set to 1
                break;

            currentTotal += perlinPercentage.getPercentage();

            if (currentTotal >= percentageInputs.get(key)) {

                double threshold = perlinPercentage.getThreshold();

                thresholdInputs.put(key, new Double[]{previousThreshold, threshold});
                previousThreshold = threshold;

                stackedKeys.remove(0);
                currentTotal = 0;

            }

        }
        thresholdInputs.put(stackedKeys.get(0), new Double[]{previousThreshold, 1D}); // Set last

        return thresholdInputs;
    }

}
