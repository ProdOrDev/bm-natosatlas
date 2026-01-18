package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.MapChunk;
import dev.natowb.natosatlas.core.platform.PlatformWorldProvider;
import dev.natowb.natosatlas.core.utils.LogUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSource;
import net.minecraft.world.chunk.storage.RegionFile;

import java.io.DataInputStream;
import java.io.File;

public class NacWorldProviderST implements PlatformWorldProvider {
    @Override
    public String getName() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        String name;
        if (mc.isWorldRemote()) {
            name = mc.options.lastServer;
        } else {
            name = mc.world.getProperties().getName();
        }
        return name;
    }

    @Override
    public boolean isRemote() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return mc.isWorldRemote();
    }

    @Override
    public int getDimension() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return mc.player.dimensionId;
    }

    @Override
    public boolean isDaytime() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        long time = mc.world.getTime() % 24000L;
        return time < 12000L;
    }

    @Override
    public void generateExistingChunks() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        File regionDir = new File(
                NatosAtlas.get().platform.getMinecraftDirectory() + "/saves/" + getName(),
                "region"
        );

        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mcr"));
        if (regionFiles == null || regionFiles.length == 0) {
            LogUtil.info("ChunkScanner", "No region files found.");
            return;
        }

        int totalRegions = regionFiles.length;
        int regionIndex = 0;

        for (File regionFile : regionFiles) {
            regionIndex++;

            String[] parts = regionFile.getName()
                    .substring(2, regionFile.getName().length() - 4)
                    .split("\\.");

            int rx = Integer.parseInt(parts[0]);
            int rz = Integer.parseInt(parts[1]);

            LogUtil.info(
                    "ChunkScanner",
                    "Scanning region {} of {} -> r({}, {})",
                    regionIndex, totalRegions, rx, rz
            );

            RegionFile rf = new RegionFile(regionFile);

            int processed = 0;
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {


                    if (!rf.hasChunkData(x, z)) {
                        continue;
                    }
                    processed++;

                    int worldChunkX = rx * 32 + x;
                    int worldChunkZ = rz * 32 + z;

                    MapChunk chunk = NatosAtlas.get().platform.chunkProvider
                            .buildSurface(worldChunkX, worldChunkZ);

                    NatosAtlas.get().regionManager
                            .updateChunk(worldChunkX, worldChunkZ, chunk);
                }
            }

            LogUtil.info(
                    "ChunkScanner",
                    "Finished region r({}, {})  ({} chunks found)",
                    rx, rz, processed
            );

            rf.close();
        }

        LogUtil.info("ChunkScanner", "All regions scanned.");
    }

}
