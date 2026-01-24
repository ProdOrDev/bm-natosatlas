package dev.natowb.natosatlas.core.render;

import dev.natowb.natosatlas.core.map.MapContext;

import java.util.Set;

public interface MapStagePainter {
    void draw(MapContext ctx, Set<Long> visibleRegions);
}

