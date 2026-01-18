package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NAWorldInfo;
import dev.natowb.natosatlas.core.map.MapManager;
import dev.natowb.natosatlas.core.map.RegionSaveWorker;
import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.waypoint.Waypoints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NatosAtlas {

    private static NatosAtlas instance = null;

    public static NatosAtlas get() {
        return instance;
    }

    public final Platform platform;
    public final MapManager regionManager;
    public NAWorldInfo worldInfo;

    public NatosAtlas(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }
        LogUtil.info("NatosAtlas", "Initializing NatosAtlas core");
        instance = this;
        this.platform = platform;
        this.regionManager = new MapManager();
        NAPaths.updateBasePaths(platform.getMinecraftDirectory());
        Settings.load();
        LogUtil.info("NatosAtlas", "Initialization complete");
    }


    public void onWorldJoin() {
        worldInfo = platform.worldProvider.getWorldInfo();
        LogUtil.info("NatosAtlas", "Joined world: {}", worldInfo.worldName);
        NAPaths.updateWorldPath(worldInfo);
        Waypoints.load();
        RegionSaveWorker.start();
    }

    public void onWorldLeft() {
        RegionSaveWorker.stop();
        regionManager.cleanup();

        LogUtil.info("NatosAtlas", "Left world: {}", worldInfo.worldName);
        worldInfo = null;
    }

    public void onWorldUpdate() {

        if (worldInfo == null) {
            LogUtil.error("NatosAtlas", "WHY ARE WE UPDATING WITHOUT A WORLD");
            return;
        }


        if (platform.worldProvider.getWorldInfo().worldDimension != worldInfo.worldDimension) {
            NAWorldInfo latestWorldInfo = platform.worldProvider.getWorldInfo();
            LogUtil.info("NatosAtlas", "changed from DIM {} to DIM {}", worldInfo.worldDimension, latestWorldInfo.worldDimension);
            worldInfo = latestWorldInfo;
            NAPaths.updateWorldPath(worldInfo);
            regionManager.cleanup();
        }


        NAEntity player = NatosAtlas.get().platform.worldProvider.getPlayer();
        double px = player.x;
        double pz = player.z;

        int chunkX = (int) Math.floor(px / 16.0);
        int chunkZ = (int) Math.floor(pz / 16.0);

        regionManager.update(chunkX, chunkZ);
    }
}
