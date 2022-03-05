package me.vhajor.metanoiacore.constructors;

import org.bukkit.block.data.BlockData;

import java.util.Map;

public class BiomeData {

    private String region;
    private Double[] thresholds;
    private BlockData fillBlockData;
    private BlockData surfaceBlockData;
    private Map<Map<int[], BlockData>, Double[]> structures;

    public BiomeData(String region, Double[] thresholds, BlockData fillBlockData, BlockData surfaceBlockData, Map<Map<int[], BlockData>, Double[]> structures) {

        setRegion(region);
        setThresholds(thresholds);
        setFillBlockData(fillBlockData);
        setSurfaceBlockData(surfaceBlockData);
        setStructures(structures);

    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Double[] getThresholds() {
        return thresholds;
    }

    public void setThresholds(Double[] thresholds) {
        this.thresholds = thresholds;
    }

    public BlockData getFillBlockData() {
        return fillBlockData;
    }

    public void setFillBlockData(BlockData fillBlockData) {
        this.fillBlockData = fillBlockData;
    }

    public BlockData getSurfaceBlockData() {
        return surfaceBlockData;
    }

    public void setSurfaceBlockData(BlockData surfaceBlockData) {
        this.surfaceBlockData = surfaceBlockData;
    }

    public Map<Map<int[], BlockData>, Double[]> getStructures() {
        return structures;
    }

    public void setStructures(Map<Map<int[], BlockData>, Double[]> structures) {
        this.structures = structures;
    }

}
