package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.persistence.TextStorage;

import java.io.File;

public final class SettingsStorage extends TextStorage {

    public SettingsStorage() {
        super(new File(NatosAtlas.get().getDataPath().toFile(), "settings.txt"));
    }

    @Override
    protected String getName() {
        return "Settings";
    }

    @Override
    protected void onLoad() {
        Settings.mapGrid = getBoolean("mapGrid", true);
        Settings.debugInfo = getBoolean("debugInfo", false);
        Settings.defaultZoom = getFloat("defaultZoom", 1f);
        Settings.entityDisplayMode = getEnum("entityDisplayMode", Settings.EntityDisplayMode.class, Settings.EntityDisplayMode.All);
        Settings.mapRenderMode = getEnum("mapRenderMode", Settings.MapRenderMode.class, Settings.MapRenderMode.Auto);
    }

    @Override
    protected void onSave() {
        put("mapGrid", Settings.mapGrid);
        put("debugInfo", Settings.debugInfo);
        put("defaultZoom", Settings.defaultZoom);
        put("entityDisplayMode", Settings.entityDisplayMode.name());
        put("mapRenderMode", Settings.mapRenderMode.name());
    }
}
