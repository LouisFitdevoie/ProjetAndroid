package be.heh.fitdevoie.projetandroidstudio.TaskS7;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import SimaticS7.S7;
import SimaticS7.S7Client;

public class WriteTaskS7 {

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread writeTrhead;
    private AutomateS7 plcS7;
    private S7Client comS7;
    private String[] parConnexion = new String[10];
    private byte[] motCommande = new byte[512];

    public WriteTaskS7(){
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        writeTrhead = new Thread(plcS7);
    }

    public void Stop(){
        isRunning.set(false);
        comS7.Disconnect();
        writeTrhead.interrupt();
    }

    public void Start(String a, String r, String s){
        if(!writeTrhead.isAlive()){
            parConnexion[0] = a;
            parConnexion[1] = r;
            parConnexion[2] = s;
            writeTrhead.start();
            isRunning.set(true);
        }
    }

    public class AutomateS7 implements Runnable{

        @Override
        public void run() {
            try {
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(parConnexion[0], Integer.valueOf(parConnexion[1]), Integer.valueOf(parConnexion[2]));
                while (isRunning.get() && (res.equals(0))){
                    Integer writePLC = comS7.WriteArea(S7.S7AreaDB, 5, 0, 32, motCommande);
                    if(writePLC.equals(0)){
                        Log.i("ret WRITE : ", String.valueOf(res) + "****" + String.valueOf(writePLC));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setWriteBool(int b, int v){
        if(v == 1){
            motCommande[0] = (byte) (b | motCommande[0]);
        }else {
            motCommande[0] = (byte) (~b & motCommande[0]);
        }
    }

    public void WriteByte(int p, int v){
        String s = Integer.toBinaryString(v);
        ArrayList<Boolean> booleans = new ArrayList<>();
        for (char c : s.toCharArray()){
            if(c == '1'){
                booleans.add(true);
            }else if(c == '0'){
                booleans.add(false);
            }
        }
        int i = booleans.size() -1;
        for(Boolean b : booleans){
            System.out.print(b + " ");
            S7.SetBitAt(motCommande, p, i, b);
            i--;
        }
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
