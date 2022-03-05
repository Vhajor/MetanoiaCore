package me.vhajor.metanoiacore;

import me.vhajor.metanoiacore.utils.BiomeDataManager;
import me.vhajor.metanoiacore.utils.MetanoiaGenerator;
import org.bukkit.configuration.Configuration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.noise.PerlinNoiseGenerator;

public final class MetanoiaCore extends JavaPlugin {

    private static Configuration configuration;
    private static PerlinNoiseGenerator mapPerlinNoise;
    private static PerlinNoiseGenerator temperatePerlinNoise;
    private static PerlinNoiseGenerator humidityPerlinNoise;
    private static PerlinNoiseGenerator structurePerlinNoise;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        configuration = getConfig();
        long perlinSeed = configuration.getLong("map_perlin_seed");
        mapPerlinNoise = new PerlinNoiseGenerator(perlinSeed);
        temperatePerlinNoise = new PerlinNoiseGenerator(perlinSeed + 1);
        humidityPerlinNoise = new PerlinNoiseGenerator(perlinSeed + 2);
        structurePerlinNoise = new PerlinNoiseGenerator(perlinSeed + 3);

        BiomeDataManager.setup();

    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new MetanoiaGenerator();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static PerlinNoiseGenerator getMapPerlinNoise() {
        return mapPerlinNoise;
    }

    public static PerlinNoiseGenerator getTemperatePerlinNoise() {
        return temperatePerlinNoise;
    }

    public static PerlinNoiseGenerator getHumidityPerlinNoise() {
        return humidityPerlinNoise;
    }

    public static PerlinNoiseGenerator getStructurePerlinNoise() {
        return structurePerlinNoise;
    }

}
