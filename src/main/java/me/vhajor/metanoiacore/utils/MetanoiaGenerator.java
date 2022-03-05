package me.vhajor.metanoiacore.utils;

import me.vhajor.metanoiacore.MetanoiaCore;
import me.vhajor.metanoiacore.constructors.BiomeData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.Configuration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.util.*;

public class MetanoiaGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {

        PerlinNoiseGenerator mapPerlinNoise = MetanoiaCore.getMapPerlinNoise();
        PerlinNoiseGenerator temperatePerlinNoise = MetanoiaCore.getTemperatePerlinNoise();
        PerlinNoiseGenerator humidityPerlinNoise = MetanoiaCore.getHumidityPerlinNoise();
        PerlinNoiseGenerator structurePerlinNoise = MetanoiaCore.getStructurePerlinNoise();
        Configuration configuration = MetanoiaCore.getConfiguration();
        List<BiomeData> biomes = BiomeDataManager.getCustomBiomes();
        Map<String, Double[]> regions = BiomeDataManager.getRegions();

        ChunkData chunkData = createChunkData(world);
        int maxHeight = world.getMaxHeight() - 1;

        double biomeScaling = configuration.getDouble("biome_scaling");
        double regionScaling = configuration.getDouble("region_scaling");
        double horizontalScaling = configuration.getDouble("horizontal_scaling");
        double verticalScaling = configuration.getDouble("vertical_scaling");
        double threshold = configuration.getDouble("threshold");
        int waterLevel = configuration.getInt("water_level");

        BiomeData[][][] biomeDataMap = getBiomeDataMap(temperatePerlinNoise, humidityPerlinNoise, biomeScaling, regionScaling, x, z, biomes, regions);

        setDefaultBiome(biome);

        setBedrock(chunkData, maxHeight);

        setFill(chunkData, biomeDataMap, mapPerlinNoise, x, z, waterLevel, threshold, horizontalScaling, verticalScaling, maxHeight);

        for (int locZ = 0; locZ < 16; locZ++) // generates the populators
            for (int locX = 0; locX < 16; locX++) {

                ArrayList<Integer> ys = getYs(chunkData, biomeDataMap, world, locX, locZ);

                for (int y : ys) {

                    chunkData.setBlock(locX, y, locZ, biomeDataMap[y][locZ][locX].getSurfaceBlockData());

                    Map<Map<int[], BlockData>, Double[]> structures = biomeDataMap[y][locZ][locX].getStructures();

                    double value = structurePerlinNoise.noise(locX + x * 16D, y, locZ + z * 16D);

                    for (Map.Entry<Map<int[], BlockData>, Double[]> structure : structures.entrySet())
                        if (value > structure.getValue()[0] && value < structure.getValue()[1]) {

                            boolean good = true;

                            for (int[] cord : structure.getKey().keySet()) { // Check if the structure can be placed

                                int cordX = locX + cord[0];
                                int cordY = y + cord[1];
                                int cordZ = locZ + cord[2];

                                if (chunkData.getBlockData(cordX, cordY, cordZ).getMaterial() != Material.AIR || cordY > maxHeight || cordY < 0 /*TODO: min height cause of 1.18*/ || cordZ > 15 || cordZ < 0 || cordX > 15 || cordX < 0) { // All blocks are air and indexes are within chunk

                                    good = false;

                                    break;
                                }

                            }

                            if (good) // If it is valid, set it and set it to valid
                                for (int[] cord : structure.getKey().keySet())
                                    chunkData.setBlock(locX + cord[0], y + cord[1], locZ + cord[2], structure.getKey().get(cord));

                            break;
                        }

                }

            }

        return chunkData;
    }

    private BiomeData[][][] getBiomeDataMap(PerlinNoiseGenerator temperatePerlinNoise, PerlinNoiseGenerator humidityPerlinNoise, double biomeScaling, double regionScaling, int x, int z, List<BiomeData> biomes, Map<String, Double[]> regions) {

        BiomeData[][][] biomeDataMap = new BiomeData[256][16][16];

        for (int y = 0; y < 256; y++)
            for (int locZ = 0; locZ < 16; locZ++)
                for (int locX = 0; locX < 16; locX++) {

                    double humidityValue = humidityPerlinNoise.noise((locX + x * 16) / biomeScaling, y / biomeScaling, (locZ + z * 16) / biomeScaling);
                    double temperateValue = temperatePerlinNoise.noise((locX + x * 16) / regionScaling, y / regionScaling, (locZ + z * 16) / regionScaling);

                    for (Map.Entry<String, Double[]> region : regions.entrySet())
                        if (humidityValue > region.getValue()[0] && humidityValue < region.getValue()[1])
                            for (BiomeData biomeData : biomes)
                                if (biomeData.getRegion().equals(region.getKey()) && temperateValue > biomeData.getThresholds()[0] && temperateValue < biomeData.getThresholds()[1]) {

                                    biomeDataMap[y][locZ][locX] = biomeData;

                                    break;
                                }

                }

        return biomeDataMap;
    }

    private void setDefaultBiome(BiomeGrid biome) {

        for (int locZ = 0; locZ < 16; locZ++)
            for (int locX = 0; locX < 16; locX++)
                biome.setBiome(locX, locZ, Biome.TAIGA);

    }

    private void setBedrock(ChunkData chunkData, int maxHeight) {

        for (int locZ = 0; locZ < 16; locZ++) // set's the vertical world border
            for (int locX = 0; locX < 16; locX++) {

                chunkData.setBlock(locX, 0, locZ, Material.BEDROCK);
                chunkData.setBlock(locX, maxHeight, locZ, Material.BEDROCK);

            }

    }

    private void setFill(ChunkData chunkData, BiomeData[][][] biomeDataMap, PerlinNoiseGenerator mapPerlinNoise, int x, int z, int waterLevel, double threshold, double horizontalScaling, double verticalScaling, int maxHeight) {

        for (int y = 1; y < maxHeight; y++) // generates the fill via perlin noise
            for (int locZ = 0; locZ < 16; locZ++)
                for (int locX = 0; locX < 16; locX++)
                    if (mapPerlinNoise.noise((locX + x * 16) / horizontalScaling, y / verticalScaling, (locZ + z * 16) / horizontalScaling) < threshold)
                        chunkData.setBlock(locX, y, locZ, biomeDataMap[y][locZ][locX].getFillBlockData());
                    else if (y <= waterLevel)
                        chunkData.setBlock(locX, y, locZ, Material.WATER);

    }

    private ArrayList<Integer> getYs(ChunkData chunkData, BiomeData[][][] biomeDataMap, World world, int locX, int locZ) {

        ArrayList<Integer> ys = new ArrayList<>();

        boolean airAbove = false;

        for (int y = world.getMaxHeight(); y >= 0; y--) { // gets all the blocks with air above

            Material material = chunkData.getType(locX, y, locZ);

            if (airAbove && material == biomeDataMap[y][locZ][locX].getFillBlockData().getMaterial())
                ys.add(y);

            airAbove = material == Material.AIR;

        }

        return ys;
    }

}
