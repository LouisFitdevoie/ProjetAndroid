package be.heh.fitdevoie.projetandroidstudio.TaskS7;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import SimaticS7.S7;
import SimaticS7.S7Client;

public class WriteTaskS7 {

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread writeThread;
    private AutomateS7 plcS7;
    private S7Client comS7;
    private String[] parConnexion = new String[10];
    private byte[] motCommande1 = new byte[10];
    private byte[] motCommande2 = new byte[10];

    public WriteTaskS7() {
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        writeThread = new Thread(plcS7);
    }

    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }

    public void Start(String a, String r, String s) {
        if(!writeThread.isAlive()) {
            parConnexion[0] = a;
            parConnexion[1] = r;
            parConnexion[2] = s;
            writeThread.start();
            isRunning.set(true);
        }
    }

    private class AutomateS7 implements Runnable {
        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(parConnexion[0],Integer.valueOf(parConnexion[1]),Integer.valueOf(parConnexion[2]));

                while(isRunning.get() && (res.equals(0))) {
                    Integer writePLC = comS7.WriteArea(S7.S7AreaDB,5,0,1,motCommande1);

                    if(writePLC.equals(0)) {
                        Log.i("ret WRITE : ",String.valueOf(res) + "****" + String.valueOf(writePLC));
                    }

                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setWriteBool(int byteToWrite,int bitToWrite, int value) {
        if(value == 1) {
            if(byteToWrite == 0) {
                motCommande1[0] = (byte) (bitToWrite | motCommande1[0]);
            } else if(byteToWrite == 1) {
                motCommande2[0] = (byte) (bitToWrite | motCommande2[0]);
            }
        } else {
            if(byteToWrite == 0) {
                motCommande1[0] = (byte) (~bitToWrite & motCommande1[0]);
            } else if(byteToWrite == 1) {
                motCommande2[0] = (byte) (~bitToWrite & motCommande2[0]);
            }
        }
    }

}
