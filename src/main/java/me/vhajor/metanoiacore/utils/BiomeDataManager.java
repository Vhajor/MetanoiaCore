package me.vhajor.metanoiacore.utils;

import me.vhajor.metanoiacore.MetanoiaCore;
import me.vhajor.metanoiacore.constructors.BiomeData;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class BiomeDataManager {

    private static final List<BiomeData> customBiomes = new ArrayList<>();
    private static final Map<String, Double[]> regions = new HashMap<>();

    private BiomeDataManager() {
        throw new IllegalStateException("Utility Class");
    }

    public static void setup() {

        Configuration configuration = MetanoiaCore.getConfiguration();
        ConfigurationSection regionsSection = configuration.getConfigurationSection("regions");
        assert regionsSection != null;

        Map<Object, Integer> weightedRegions = new HashMap<>(); // Start converting to a thresholded map

        for (String region : regionsSection.getKeys(false))
            weightedRegions.put(region, regionsSection.getInt(region + ".weight"));

        Map<Object, Double[]> percentageRegionObjects = PerlinProbabilityCalculator.calculatePerlinProbabilities(weightedRegions);
        Map<String, Double[]> percentageRegions = new HashMap<>();

        for (Map.Entry<Object, Double[]> percentageObject : percentageRegionObjects.entrySet()) // End converting to a thresholded map
            percentageRegions.put(percentageObject.getKey().toString(), percentageObject.getValue());

        for (String region : regionsSection.getKeys(false)) {

            ConfigurationSection biomesSection = configuration.getConfigurationSection("regions." + region + ".biomes");
            assert biomesSection != null;

            Map<Object, Integer> weightedBiomes = new HashMap<>(); // Start converting to a thresholded map

            for (String biome : biomesSection.getKeys(false))
                weightedBiomes.put(biome, biomesSection.getInt(biome + ".weight"));

            Map<Object, Double[]> percentageBiomeObjects = PerlinProbabilityCalculator.calculatePerlinProbabilities(weightedBiomes);
            Map<String, Double[]> percentageBiomes = new HashMap<>();

            for (Map.Entry<Object, Double[]> percentageObject : percentageBiomeObjects.entrySet()) // End converting to a thresholded map
                percentageBiomes.put(percentageObject.getKey().toString(), percentageObject.getValue());

            for (String biome : biomesSection.getKeys(false)) {

                ConfigurationSection structureSection = configuration.getConfigurationSection("regions." + region + ".biomes." + biome + ".structures");
                assert structureSection != null;

                BlockData fillBlockData = Material.valueOf(biomesSection.getString(biome + ".fill_block")).createBlockData();
                BlockData surfaceBlockData = Material.valueOf(biomesSection.getString(biome + ".surface_block")).createBlockData();
                Map<Map<int[], BlockData>, Double[]> structures = new HashMap<>();

                Map<Object, Integer> weightedStructures = new HashMap<>(); // Start converting to a thresholded map

                for (String structure : structureSection.getKeys(false))
                    weightedStructures.put(structure, structureSection.getInt(structure + ".weight"));

                Map<Object, Double[]> percentageStructureObjects = PerlinProbabilityCalculator.calculatePerlinProbabilities(weightedStructures);
                Map<String, Double[]> percentageStructures = new HashMap<>();

                for (Map.Entry<Object, Double[]> percentageObject : percentageStructureObjects.entrySet()) // End converting to a thresholded map
                    percentageStructures.put(percentageObject.getKey().toString(), percentageObject.getValue());

                for (String structure : structureSection.getKeys(false)) {

                    ConfigurationSection blocksSection = configuration.getConfigurationSection("regions." + region + ".biomes." + biome + ".structures." + structure + ".blocks");
                    assert blocksSection != null;

                    Map<int[], BlockData> structureBlocks = new HashMap<>();

                    for (String block : blocksSection.getKeys(false)) {

                        String[] serializedCords = block.split("=");

                        int[] cords = new int[3];

                        cords[0] = Integer.parseInt(serializedCords[0]);
                        cords[1] = Integer.parseInt(serializedCords[1]);
                        cords[2] = Integer.parseInt(serializedCords[2]);

                        BlockData blockData = Material.valueOf(blocksSection.getString(block)).createBlockData();

                        structureBlocks.put(cords, blockData);

                    }

                    structures.put(structureBlocks, percentageStructures.get(structure));

                }

                customBiomes.add(new BiomeData(region, percentageBiomes.get(biome), fillBlockData, surfaceBlockData, structures));

            }

            regions.put(region, percentageRegions.get(region));

        }

    }

    public static List<BiomeData> getCustomBiomes() {
        return customBiomes;
    }

    public static Map<String, Double[]> getRegions() {
        return regions;
    }

}


