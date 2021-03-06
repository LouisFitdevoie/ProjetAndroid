package be.heh.fitdevoie.projetandroidstudio.TaskS7;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import SimaticS7.S7;
import SimaticS7.S7Client;
import SimaticS7.S7OrderCode;

public class ReadTaskS7Comprimes {
    private static final int MESSAGE_PROGRESS_UPDATE = 2;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private View vi_main_ui;
    private Button bt_connect;
    private TextView tv_flaconsVides;
    private TextView tv_selecteurService;
    private TextView tv_nbComprimesSelectionne;
    private TextView tv_nbComprimes;
    private TextView tv_nbBouteillesRemplies;
    private String[] param = new String[10];
    private byte[] datasPLC = new byte[512];

    private AutomateS7 plcS7;
    private Thread readThread;
    private S7Client comS7;

    private RelativeLayout rl_comprimes_dataToWrite;

    public ReadTaskS7Comprimes(View view,
                               Button bt_connect,
                               TextView tv_flaconsVides,
                               TextView tv_selecteurService,
                               TextView tv_nbComprimesSelectionne,
                               TextView tv_nbComprimes,
                               TextView tv_nbBouteillesRemplies,
                               RelativeLayout rl_comprimes_dataToWrite) {
        this.vi_main_ui = view;
        this.bt_connect = bt_connect;
        this.tv_flaconsVides = tv_flaconsVides;
        this.tv_selecteurService = tv_selecteurService;
        this.tv_nbComprimesSelectionne = tv_nbComprimesSelectionne;
        this.tv_nbComprimes = tv_nbComprimes;
        this.tv_nbBouteillesRemplies = tv_nbBouteillesRemplies;
        this.rl_comprimes_dataToWrite = rl_comprimes_dataToWrite;

        comS7 = new S7Client();
        plcS7 = new AutomateS7();

        readThread = new Thread(plcS7);
    }

    public void Stop() {
        //D??connecte l'automate + interrompt le thread de lecture
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    public void Start(String ipAddress, String rack, String slot) {
        //Si le thread de lecture n'est pas d??j?? actif
        if(!readThread.isAlive()) {
            //D??marre le thread de lecture et enregistre les param??tres de connexion ?? l'automate
            param[0] = ipAddress;
            param[1] = rack;
            param[2] = slot;

            readThread.start();
            isRunning.set(true);
        }
    }

    private void downloadOnProgressUpdate(int progress, int what) {
        switch (what) {
            case 0: //BT Flacons vides
                if(progress == 1) {
                    tv_flaconsVides.setText("ACTIV??");
                } else {
                    tv_flaconsVides.setText("D??SACTIV??");
                }
                break;
            case 1: //BT Selecteur en service
                if(progress == 1) {
                    tv_selecteurService.setText("ACTIV??");
                } else {
                    tv_selecteurService.setText("D??SACTIV??");
                }
                break;
            case 2: //ET Nb comprim??s s??lectionn??
                if(progress == 5) {
                    tv_nbComprimesSelectionne.setText("5");
                } else if(progress == 10) {
                    tv_nbComprimesSelectionne.setText("10");
                } else if(progress == 15) {
                    tv_nbComprimesSelectionne.setText("15");
                } else {
                    System.out.println("Error");
                }
                break;
            case 3: //ET Nb comprim??s par bouteille
                tv_nbComprimes.setText(String.valueOf(progress));
                break;
            case 4: //TV Nb bouteilles remplies
                tv_nbBouteillesRemplies.setText(String.valueOf(progress));
                break;
        }
    }

    private Handler monHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PROGRESS_UPDATE:
                    downloadOnProgressUpdate(msg.arg1, msg.arg2);
                    break;
                default:
                    break;
            }
        }
    };

    private class AutomateS7 implements Runnable {
        @Override
        public void run() {
            try {
                //Connexion ?? l'automate
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(param[0],Integer.valueOf(param[1]),Integer.valueOf(param[2]));

                //Boucle -> tant que l'utilisateur ne stoppe pas la boucle
                while(isRunning.get()) {
                    if(res.equals(0)) {
                        String sout = "";
                        //Selecteur flacons vides -> FONCTIONNEL
                        int retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 0, 2, datasPLC);
                        int flaconsVides = 0;
                        Boolean dataBoolean = false;
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 1, 3);
                            if(dataBoolean) {
                                flaconsVides = 1;
                            } else {
                                flaconsVides = 0;
                            }
                            sout += "Flacons vides : " + String.valueOf(flaconsVides);
                            sendProgressMessage(flaconsVides, 0);
                        }
                        //Selecteur en service -> FONCTIONNEL
                        int selecteurEnService = 0;
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 0, 0);
                            if(dataBoolean) {
                                selecteurEnService = 1;
                            } else {
                                selecteurEnService = 0;
                            }
                            sout += "\nS??lecteur en service : " + String.valueOf(selecteurEnService);
                            sendProgressMessage(selecteurEnService, 1);
                        }
                        //Nb comprim??s s??lectionn?? -> FONCTIONNEL
                        int nbComprimesSelectionnes = 0;
                        retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 4, 1, datasPLC);
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 0, 3);
                            if(dataBoolean) {
                                nbComprimesSelectionnes = 5;
                            } else {
                                dataBoolean = S7.GetBitAt(datasPLC, 0, 4);
                                if(dataBoolean) {
                                    nbComprimesSelectionnes = 10;
                                } else {
                                    dataBoolean = S7.GetBitAt(datasPLC, 0, 5);
                                    if(dataBoolean) {
                                        nbComprimesSelectionnes = 15;
                                    } else {
                                        nbComprimesSelectionnes = 0;
                                    }
                                }
                            }
                            sout += "\nNb comprim??s s??lectionn?? : " + String.valueOf(nbComprimesSelectionnes);
                            sendProgressMessage(nbComprimesSelectionnes, 2);
                        }
                        //Nb comprim??s par bouteille -> FONCTIONNEL
                        retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 14, 2, datasPLC);
                        int nbComprimes = 0;
                        nbComprimes = 0;
                        if(retInfo == 0) {
                            nbComprimes = S7.GetWordAt(datasPLC, 0);
                            if(nbComprimes > nbComprimesSelectionnes) {
                                nbComprimes = nbComprimesSelectionnes;
                            }
                            sout += "\nNb comprim??s par bouteille : " + String.valueOf(nbComprimes);
                            sendProgressMessage(nbComprimes, 3);
                        }
                        //Nb bouteilles remplies -> FONCTIONNEL
                        retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 16, 2, datasPLC);
                        int nbBouteillesRemplies = 0;
                        if(retInfo == 0) {
                            nbBouteillesRemplies = S7.GetWordAt(datasPLC, 0);
                            sout += "\nNb bouteilles remplies : " + String.valueOf(nbBouteillesRemplies);
                            sendProgressMessage(nbBouteillesRemplies, 4);
                        }

                        System.out.println(sout);
                    }
                    //Attends 1/2 seconde avant de recommencer
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendProgressMessage(int progress, int what) {
            Message progressMsg = new Message();
            progressMsg.what = MESSAGE_PROGRESS_UPDATE;
            progressMsg.arg1 = progress;
            progressMsg.arg2 = what;
            monHandler.sendMessage(progressMsg);
        }
    }
}
