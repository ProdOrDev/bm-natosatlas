package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.map.MapBiome;
import dev.natowb.natosatlas.core.map.MapChunk;

public interface PlatformWorldProvider {

    String getName();

    boolean isRemote();

    int getDimension();

    boolean isDaytime();

    void generateExistingChunks();

    MapBiome getBiome(int blockX, int blockZ);

    MapChunk buildSurface(int chunkX, int chunkZ);

    MapChunk buildFromStorage(int chunkX, int chunkZ);
}
