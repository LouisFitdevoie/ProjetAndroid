package be.heh.fitdevoie.projetandroidstudio.TaskS7;

import android.widget.ProgressBar;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadTest {
    private ProgressBar pb_main_progressionTH;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread monthread;

    public ThreadTest(ProgressBar p) {
        pb_main_progressionTH = p;
    }

    public void Go() {
        pb_main_progressionTH.setProgress(0);

        monthread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0 ; i < 20 && isRunning.get() ; i++) {
                    pb_main_progressionTH.incrementProgressBy(5);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        isRunning.set(true);
        monthread.start();
    }

    public void Stop() {
        isRunning.set(false);
        monthread.interrupt();
        pb_main_progressionTH.setProgress(0);
    }
}
