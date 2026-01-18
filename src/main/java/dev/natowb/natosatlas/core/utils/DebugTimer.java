package dev.natowb.natosatlas.core.utils;

import java.util.ArrayDeque;
import java.util.Deque;

public final class DebugTimer {

    private static final ThreadLocal<Deque<TimerFrame>> STACK =
            ThreadLocal.withInitial(ArrayDeque::new);

    private static class TimerFrame {
        final String label;
        final long startTime;

        TimerFrame(String label) {
            this.label = label;
            this.startTime = System.nanoTime();
        }
    }

    public static DebugTimer begin(String label) {
        Deque<TimerFrame> stack = STACK.get();
        stack.push(new TimerFrame(label));

        LogUtil.debug("Timer", indent(stack.size()) + "▶ " + label + "...");
        return new DebugTimer();
    }

    public DebugTimer split(String label) {
        Deque<TimerFrame> stack = STACK.get();
        TimerFrame frame = stack.peek();
        if (frame == null) return this;

        long ms = (System.nanoTime() - frame.startTime) / 1_000_000L;
        LogUtil.debug("Timer", indent(stack.size()) + "  ↳ " + label + ": " + ms + " ms");
        return this;
    }

    public void end() {
        Deque<TimerFrame> stack = STACK.get();
        TimerFrame frame = stack.poll();
        if (frame == null) return;

        long ms = (System.nanoTime() - frame.startTime) / 1_000_000L;
        LogUtil.debug("Timer", indent(stack.size() + 1) + "✔ " + frame.label + " finished in " + ms + " ms");
    }

    private static String indent(int depth) {
        int count = Math.max(0, depth - 1);
        StringBuilder sb = new StringBuilder(count * 2);
        for (int i = 0; i < count; i++) sb.append("  ");
        return sb.toString();
    }
}
