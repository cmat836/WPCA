package com.cmat.wpca.data;

import android.os.Handler;
import android.os.SystemClock;

import androidx.core.util.Consumer;

import java.util.HashMap;
import java.util.Map;

/**
 * A Smart timer class for keeping track of time and scheduling functions to be executed in the future. <br /><br />
 * One timer can handle many tasks and can track their times relative to the timer and also to there own start
 * so you should only need one or a small number of these timers to handle all your tasks. <br /><br />
 * Uses ScheduledTask's to package task functions with their time information.
 * See documentation on addTask() and start()/stop()/pause()/resume() for usage details
 */
public class TaskTimer {
    boolean paused = true;
    int rate = 100; // How often between executions

    long startTime = 0L; // System time (.uptimeMillis) the timer started at
    long pausedTime = 0L;  // Elapsed time while the timer is paused

    private final Handler timeHandler = new Handler(); // Handler for the global timer loop
    private final HashMap<String, ScheduledTask> tasks = new HashMap<>();

    /**
     * Create a new TaskTimer with given refresh rate
     * @param rate How often the timer refreshes, lower values will give higher accuracy and allow for tasks to be run with lower intervals but could hurt performance if there are many tasks
     */
    public TaskTimer(int rate) {
        this.rate = rate;
    }

    /**
     * Adds a new task to the schedule, executes repeatedly on a timer until the duration expires, then executes a final task
     * @param taskId unique task id for use removing the task early
     * @param relTime if the task wants time relative to when it was added (true), or from when the timer was started (false)
     * @param task the function to be run, needs to return void and accept a long argument (the time determined by relTime)
     * @param interval how many milliseconds between execution
     * @param duration how long the task should run for
     * @param end a function to run when the task is complete, needs to return void and accept a long argument (the time determined by relTime)
     */
    public void addTask(String taskId, boolean relTime, Consumer<Long> task, long interval, long duration, Consumer<Long> end) {
        tasks.put(taskId, new ScheduledTask(task, relTime, getMillis(), interval, duration, end));
    }

    /**
     * Adds a new task to the schedule, executes repeatedly on a timer until the duration expires
     * @param taskId unique task id for use removing the task early
     * @param relTime if the task wants time relative to when it was added (true), or from when the timer was started (false)
     * @param task the function to be run, needs to return void and accept a long argument (the time determined by relTime)
     * @param interval how many milliseconds between execution
     * @param duration how long the task should run for
     */
    public void addTask(String taskId, boolean relTime, Consumer<Long> task, long interval, long duration) {
        tasks.put(taskId, new ScheduledTask(task, relTime,getMillis(), interval, duration));
    }

    /**
     * Adds a new task to the schedule, executes repeatedly on a timer
     * @param taskId unique task id for use removing the task early
     * @param relTime if the task wants time relative to when it was added (true), or from when the timer was started (false)
     * @param task the function to be run, needs to return void and accept a long argument (the time determined by relTime)
     * @param interval how many milliseconds between execution
     */
    public void addTask(String taskId, boolean relTime, Consumer<Long> task, long interval) {
        tasks.put(taskId, new ScheduledTask(task, relTime, getMillis(), interval));
    }

    /**
     * Adds a new task to the schedule, this task waits until the duration expires, then executes a final task
     * @param taskId unique task id for use removing the task early
     * @param relTime if the task wants time relative to when it was added (true), or from when the timer was started (false)
     * @param duration how long the task should wait for
     * @param end a function to run when the wait completes, needs to return void and accept a long argument (the time determined by relTime)
     */
    public void addTask(String taskId, boolean relTime, long duration, Consumer<Long> end) {
        tasks.put(taskId, new ScheduledTask(relTime, getMillis(), duration, end));
    }

    /**
     * Removes a task with the specified ID from the schedule, preventing any further execution
     * @param taskName
     */
    public void removeTask(String taskName) {
        tasks.remove(taskName);
    }

    /**
     * The runnable that stores the main timer loop
     */
    private final Runnable UpdateTimeTask = new Runnable() {
        public void run() {
            final long start = startTime; // the global time (uptimeMillis) when this timer was started
            long millis = SystemClock.uptimeMillis() - start; // How long has elapsed since the timer was started, excluding paused time

            for (Map.Entry<String, ScheduledTask> taskPair : tasks.entrySet()) {
                if (!taskPair.getValue().doRun(millis)) { // Run through all the tasks, if they have expired remove them
                    tasks.remove(taskPair.getKey());
                }
            }

            timeHandler.postAtTime(this, start + millis + rate); // Set the loop to run again after a delay
        }
    };

    /**
     * Resets and starts the timer again, removes all paused time and sets the time to 0
     */
    public void start() {
        startTime = SystemClock.uptimeMillis();
        pausedTime = 0L;
        timeHandler.removeCallbacks(UpdateTimeTask);
        timeHandler.postDelayed(UpdateTimeTask, 0);
        paused = false;
    }

    /**
     * Gets the amount of time that has elapsed since the timer started. <br /><br />
     * If the timer is paused return the time at which it was paused,
     * otherwise return the time the timer has not been paused since the timer started. <br /><br />
     * If the timer has not yet been started returns 0
     * @return the time in milliseconds
     */
    public long getMillis() {
        return paused ? pausedTime : SystemClock.uptimeMillis() - startTime;
    }

    /**
     * Gets the amount of time that has elapsed since the timer started,
     * (in human readable format, see millisToString()). <br /><br />
     * If the timer is paused return the time at which it was paused,
     * otherwise return the time the timer has not been paused since the timer started
     * @return the time in milliseconds
     */
    public String getString() {
        return TaskTimer.millisToString(getMillis());
    }

    /**
     * Stops the timer and resets it to the beginning, similar to start()
     */
    public void stop() {
        timeHandler.removeCallbacks(UpdateTimeTask);
        startTime = 0L;
        pausedTime = 0L;
        paused = true;
    }

    /**
     * Pauses the timer, any calls of getMillis() during this pause will return the time when the timer was paused
     */
    public void pause() {
        timeHandler.removeCallbacks(UpdateTimeTask);
        pausedTime = getMillis();
        paused = true;
    }

    /**
     * Resumes the timer where it left off before the pause, adjusts the start time so it seems the pause never happened
     */
    public void resume() {
        startTime = SystemClock.uptimeMillis() - pausedTime;
        pausedTime = 0L;
        timeHandler.removeCallbacks(UpdateTimeTask);
        timeHandler.postDelayed(UpdateTimeTask, 0);
        paused = false;
    }

    /**
     * Adjusts the timers time by an amount, a positive amount increases the time, negative reduces it (minimum 0)
     * this will not un-trigger tasks or restore those that have expired, but will make tasks with a duration run for longer or for less
     * @param millis how many milliseconds to add/remove from the timer, a positive amount increases the time, negative reduces it (minimum 0)
     */
    public void adjust(long millis) {
        long currentTime = SystemClock.uptimeMillis();
        if (paused) {
            pausedTime = Math.min(pausedTime - millis, currentTime);
        }
        startTime = Math.min(startTime - millis, currentTime); // When the timer started cannot be in the future (time cannot be less than 0)

    }

    /**
     * Converts millisecond time into a human readable format <br /><br />
     * If below 1min, displays like 0.345, 1.345, 23.45 <br /><br />
     * if above 1min, displays like 2:05, 2:45, 12:23
     * @param millis
     * @return
     */
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

    /**
     * A Wrapper around a task, stores the tasks function(s) and its timer data. Handles the running of the task, shouldn't need to be interacted with directly
     */
    private static class ScheduledTask {
        final Consumer<Long> task; // The repeatable task
        Consumer<Long> end = new Consumer<Long>() { // The final task
            @Override
            public void accept(Long aLong) {

            }
        };
        final long interval; // -1 to not run task
        final long endTime; // -1 for undetermined duration
        final long startTime; // The time when the task started
        final boolean relativeTime; // if false millis passed to the task will be from the start of the timer
        //If true millis passed to the task will be from when the task started

        long lastCalled = 0L; // The timer time when the task last ran

        /**
         * Create a new Scheduled Task with the following properties
         * @param task the function to run
         * @param relTime whether the function wants relative or timer time
         * @param startTime when the task was added
         * @param interval how often to run the function
         * @param duration how long the task should run for
         * @param end the function to run when the duration finishes
         */
        public ScheduledTask(Consumer<Long> task, boolean relTime, long startTime, long interval, long duration, Consumer<Long> end) {
            this.task = task;
            this.interval = interval;
            this.startTime = startTime;
            lastCalled = startTime;
            this.endTime = startTime + duration;
            this.end = end;
            this.relativeTime = relTime;
        }

        /**
         * Create a new Scheduled Task with the following properties
         * @param task the function to run
         * @param relTime whether the function wants relative or timer time
         * @param startTime when the task was added
         * @param interval how often to run the function
         * @param duration how long the task should run for
         */
        public ScheduledTask(Consumer<Long> task, boolean relTime, long startTime, long interval, long duration) {
            this.task = task;
            this.interval = interval;
            this.startTime = startTime;
            lastCalled = startTime;
            this.endTime = startTime + duration;
            this.relativeTime = relTime;
        }

        /**
         * Create a new Scheduled Task with the following properties
         * @param task the function to run
         * @param relTime whether the function wants relative or timer time
         * @param startTime when the task was added
         * @param interval how often to run the function
         */
        public ScheduledTask(Consumer<Long> task, boolean relTime, long startTime, long interval) {
            this.task = task;
            this.interval = interval;
            this.endTime = -1L;
            this.startTime = startTime;
            lastCalled = startTime;
            this.relativeTime = relTime;
        }

        /**
         * Create a new Scheduled Task with the following properties
         * @param relTime whether the function wants relative or timer time
         * @param startTime when the task was added
         * @param duration how long the task should run for
         * @param end the function to run when the duration finishes
         */
        public ScheduledTask(boolean relTime, long startTime, long duration, Consumer<Long> end) {
            this.task = new Consumer<Long>() {
                @Override
                public void accept(Long aLong) {

                }
            };
            this.interval = -1L;
            this.startTime = startTime;
            lastCalled = startTime;
            this.endTime = startTime + duration;
            this.end = end;
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

        // return true if continuing, false if finished
        public boolean doRun(long millis) {
            // If task is running on relative time, millis will be since the task was started not from when the timer was started
            long relMillis = relativeTime ? millis - startTime : millis;
            if (interval != -1L && (millis - lastCalled) > interval) { // If the task can run, and if enough time has elapsed since it was last ran
                getTask().accept(relMillis); // Run
                lastCalled = millis;
            }
            if  (endTime != -1L && millis > endTime) { // If the task can end, and it has ran for its duration
                end.accept(relMillis); // Run the end task
                return false; // Tell the timer to remove it
            }
            return true; // Tell the timer to keep this task
        }


    }
}
