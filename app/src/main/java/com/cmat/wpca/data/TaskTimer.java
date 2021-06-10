package com.cmat.wpca.data;

import android.os.Handler;
import android.os.SystemClock;

import androidx.core.util.Consumer;

import java.util.HashMap;
import java.util.Map;

public class TaskTimer {
    boolean paused = true;

    long startTime = 0L; // System time (.uptimeMillis) the timer started at
    long pausedTime = 0L;  // Game elapsed time when the timer is paused

    private Handler timeHandler = new Handler();
    private HashMap<String, ScheduledTask> tasks = new HashMap<>();

    public void addTask(String taskName, boolean relTime, Consumer<Long> task, long interval, long duration, Consumer<Long> end) {
        tasks.put(taskName, new ScheduledTask(task, relTime, getMillis(), interval, duration, end));
    }

    public void addTask(String taskName, boolean relTime, Consumer<Long> task, long interval, long duration) {
        tasks.put(taskName, new ScheduledTask(task, relTime,getMillis(), interval, duration));
    }

    public void addTask(String taskName, boolean relTime, Consumer<Long> task, long interval) {
        tasks.put(taskName, new ScheduledTask(task, relTime, getMillis(), interval));
    }

    public void removeTask(String taskName) {
        tasks.remove(taskName);
    }

    private final Runnable UpdateTimeTask = new Runnable() {
        public void run() {
            final long start = startTime;
            long millis = SystemClock.uptimeMillis() - start;

            for (Map.Entry<String, ScheduledTask> taskPair : tasks.entrySet()) {
                if (!taskPair.getValue().doRun(millis)) {
                    tasks.remove(taskPair.getKey());
                }
            }

            timeHandler.postAtTime(this, start + millis + 200);
        }
    };

    public void start() {
        startTime = SystemClock.uptimeMillis();
        pausedTime = 0L;
        timeHandler.removeCallbacks(UpdateTimeTask);
        timeHandler.postDelayed(UpdateTimeTask, 0);
        paused = false;
    }

    public long getMillis() {
        return paused ? pausedTime : SystemClock.uptimeMillis() - startTime;
    }

    public String getString() {
        return TaskTimer.millisToString(getMillis());
    }

    public void stop() {
        timeHandler.removeCallbacks(UpdateTimeTask);
        startTime = 0L;
        pausedTime = 0L;
        paused = true;
    }

    public void pause() {
        timeHandler.removeCallbacks(UpdateTimeTask);
        pausedTime = getMillis();
        paused = true;
    }

    public void resume() {
        startTime = SystemClock.uptimeMillis() - pausedTime;
        pausedTime = 0L;
        timeHandler.removeCallbacks(UpdateTimeTask);
        timeHandler.postDelayed(UpdateTimeTask, 0);
        paused = false;
    }

    public static String millisToString(long millis) {
        long t = millis;
        if (t < 60000) {
            String time = String.valueOf(t / 1000d);
            return time.length() > 4 ? time.substring(0, 4) : time;
        }
        t /= 1000;
        int s = (int) (t % 60);
        int m = (int) ((t - s) / 60);
        return m + ":" + (s < 10 ? "0" : "") + s;
    }

    private static class ScheduledTask {
        final Consumer<Long> task;
        Consumer<Long> end = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {

            }
        };
        final long interval;
        final long endTime; // -1 for undetermined duration
        final long startTime;
        final boolean relativeTime; // if false millis passed to the task will be from the start of the timer
        //If true millis passed to the task will be from when the task started

        long lastCalled = 0L;

        public ScheduledTask(Consumer<Long> task, boolean relTime, long startTime, long interval, long duration, Consumer<Long> end) {
            this.task = task;
            this.interval = interval;
            this.startTime = startTime;
            lastCalled = startTime;
            this.endTime = startTime + duration;
            this.end = end;
            this.relativeTime = relTime;
        }

        public ScheduledTask(Consumer<Long> task, boolean relTime, long startTime, long interval, long duration) {
            this.task = task;
            this.interval = interval;
            this.startTime = startTime;
            lastCalled = startTime;
            this.endTime = startTime + duration;
            this.relativeTime = relTime;
        }

        public ScheduledTask(Consumer<Long> task, boolean relTime, long startTime, long interval) {
            this.task = task;
            this.interval = interval;
            this.endTime = -1L;
            this.startTime = startTime;
            lastCalled = startTime;
            this.relativeTime = relTime;
        }

        public Consumer<Long> getTask() {
            return task;
        }

        public long getInterval() {
            return interval;
        }

        public long getEndTime() {
            return endTime;
        }

        // return true if continue, false if finished
        public boolean doRun(long millis) {
            long relMillis = relativeTime ? millis - startTime : millis;
            if ((millis - lastCalled) > interval) {
                getTask().accept(relMillis);
                lastCalled = millis;
            }
            if  (endTime != -1L && millis > endTime) {
                end.accept(relMillis);
                return false;
            }
            return true;
        }


    }
}
