package dev.natowb.natosatlas.core.tasks;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRenderer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapUpdateWorker {

    private static final BlockingQueue<ChunkRenderTask> QUEUE = new LinkedBlockingQueue<>();
    private static volatile boolean running = false;

    public static void start() {
        if (running) return;
        running = true;

        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    ChunkRenderTask task = QUEUE.take();
                    task.manager.renderChunk(task.chunkCoord, task.chunk);
                } catch (InterruptedException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "NatosAtlas-ChunkUpdateWorker");

        worker.setDaemon(true);
        worker.start();
    }

    public static void stop() {
        running = false;
    }

    public static void enqueue(MapRenderer manager, NACoord chunkCoord, NAChunk chunk) {
        QUEUE.offer(new ChunkRenderTask(manager, chunkCoord, chunk));
    }

    private static final class ChunkRenderTask {
        final MapRenderer manager;
        final NACoord chunkCoord;
        final NAChunk chunk;

        private ChunkRenderTask(MapRenderer manager, NACoord chunkCoord, NAChunk chunk) {
            this.manager = manager;
            this.chunkCoord = chunkCoord;
            this.chunk = chunk;
        }
    }
}
