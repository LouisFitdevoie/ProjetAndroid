package be.heh.fitdevoie.projetandroidstudio.TaskS7;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import SimaticS7.S7;
import SimaticS7.S7Client;

public class WriteTaskS7 {

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Thread writeThread;
    private AutomateS7 plcS7;
    private S7Client comS7;
    private String[] parConnexion = new String[10];
    private byte[] motCommande = new byte[512];

    public WriteTaskS7(){
        comS7 = new S7Client();
        plcS7 = new AutomateS7();
        writeThread = new Thread(plcS7);
    }

    public void Stop(){
        //Déconnecte l'automate et interrompt le thread d'écriture
        isRunning.set(false);
        comS7.Disconnect();
        writeThread.interrupt();
    }

    public void Start(String ipAddress, String rack, String slot){
        //Démarre un thread d'écriture si un autre thread d'écriture ne fonctionne pas déjà
        if(!writeThread.isAlive()){
            parConnexion[0] = ipAddress;
            parConnexion[1] = rack;
            parConnexion[2] = slot;

            writeThread.start();
            isRunning.set(true);
        }
    }

    public class AutomateS7 implements Runnable{

        @Override
        public void run() {
            try {
                //Connexion à l'automate
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(parConnexion[0], Integer.valueOf(parConnexion[1]), Integer.valueOf(parConnexion[2]));

                //Boucle -> tant que l'écriture n'est pas terminée
                while (isRunning.get() && (res.equals(0))){
                    Integer writePLC = comS7.WriteArea(S7.S7AreaDB, 5, 0, 32, motCommande);
                    if(writePLC.equals(0)){
                        Log.i("ret WRITE : ", String.valueOf(res) + "****" + String.valueOf(writePLC));
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void WriteByte(int position, int value){
        //Récupère la valeur à écrire et la converti en binaire
        String s = Integer.toBinaryString(value);
        ArrayList<Boolean> booleans = new ArrayList<>();
        //Crée une liste de booléen en fonction du nombre binaire
        for (char character : s.toCharArray()) {
            if(character == '1'){
                booleans.add(true);
            } else if(character == '0') {
                booleans.add(false);
            }
        }
        int i = booleans.size() -1;
        for(Boolean bool : booleans) {
            System.out.print(bool + " ");
            S7.SetBitAt(motCommande, position, i, bool);
            i--;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
