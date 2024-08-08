package io.deeplay.camp.botfarm;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Timer;
import java.util.TimerTask;
@NoArgsConstructor
@Getter
public class TimerForBot extends TimerTask {
    private Thread thread;
    private Timer timer;
    private long startTime;
    private long endTime;
    private long executionTime;


    public TimerForBot(Thread thread, Timer timer) {
        this.thread = thread;
        this.timer = timer;
        this.startTime = System.currentTimeMillis();

    }

    @Override
    public void run() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;
            timer.cancel();
        }
    }

}
