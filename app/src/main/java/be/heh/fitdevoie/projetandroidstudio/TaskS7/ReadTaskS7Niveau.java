package be.heh.fitdevoie.projetandroidstudio.TaskS7;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import SimaticS7.S7;
import SimaticS7.S7Client;
import SimaticS7.S7OrderCode;

public class ReadTaskS7Niveau {
    private static final int MESSAGE_PRE_EXECUTE = 1;
    private static final int MESSAGE_PROGRESS_UPDATE = 2;
    private static final int MESSAGE_POST_EXECUTE = 3;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private TextView pb_main_progressionS7;
    private View vi_main_ui;
    private Button bt_connect;
    private Button bt_flaconsVides;
    private Button bt_selecteurService;
    private EditText et_nbComprimesSelectionne;
    private EditText et_nbComprimes;
    private TextView tv_nbBouteillesRemplies;

    private AutomateS7 plcS7;
    private Thread readThread;
    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] datasPLC = new byte[512];

    public ReadTaskS7Niveau(View view, Button bt_connect, Button bt_flaconsVides, Button bt_selecteurService, EditText et_nbComprimesSelectionne, EditText et_nbComprimes, TextView tv_nbBouteillesRemplies) {
        this.vi_main_ui = view;
        this.bt_connect = bt_connect;
        this.bt_flaconsVides = bt_flaconsVides;
        this.bt_selecteurService = bt_selecteurService;
        this.et_nbComprimesSelectionne = et_nbComprimesSelectionne;
        this.et_nbComprimes = et_nbComprimes;
        this.tv_nbBouteillesRemplies = tv_nbBouteillesRemplies;

        comS7 = new S7Client();
        plcS7 = new AutomateS7();

        readThread = new Thread(plcS7);
    }

    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    public void Start(String a, String r, String s) {
        if(!readThread.isAlive()) {
            param[0] = a;
            param[1] = r;
            param[2] = s;

            readThread.start();
            isRunning.set(true);
        }
    }

    private void downloadOnPreExecute(int t) {
        Toast.makeText(vi_main_ui.getContext(),"Le traitement de la tâche de fond a démarré" + "\n",Toast.LENGTH_SHORT).show();
    }

    private void downloadOnProgressUpdate(int progress, int what) {
        switch (what) {
            case 0: //BT Flacons vides
                if(progress == 1) {
                    bt_flaconsVides.setText("ACTIVÉ");
                } else {
                    bt_flaconsVides.setText("DÉSACTIVÉ");
                }
                break;
            case 1: //BT Selecteur en service
                if(progress == 1) {
                    bt_selecteurService.setText("ACTIVÉ");
                } else {
                    bt_selecteurService.setText("DÉSACTIVÉ");
                }
                break;
            case 2: //ET Nb comprimés sélectionné
                if(progress == 5) {
                    et_nbComprimesSelectionne.setText("5");
                } else if(progress == 10) {
                    et_nbComprimesSelectionne.setText("10");
                } else if(progress == 15) {
                    et_nbComprimesSelectionne.setText("15");
                } else {
                    System.out.println("Error");
                }
                break;
            case 3: //ET Nb comprimés par bouteille
                et_nbComprimes.setText(String.valueOf(progress));
                break;
            case 4: //TV Nb bouteilles remplies
                tv_nbBouteillesRemplies.setText(String.valueOf(progress));
                break;
        }
    }

    private void downloadOnPostExecute() {
        Toast.makeText(vi_main_ui.getContext(),"Le traitement de la tâche de fond est terminé", Toast.LENGTH_LONG).show();
    }

    private Handler monHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_PRE_EXECUTE:
                    downloadOnPreExecute(msg.arg1);
                    break;
                case MESSAGE_PROGRESS_UPDATE:
                    downloadOnProgressUpdate(msg.arg1, msg.arg2);
                    break;
                case MESSAGE_POST_EXECUTE:
                    downloadOnPostExecute();
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
                comS7.SetConnectionType(S7.S7_BASIC);
                Integer res = comS7.ConnectTo(param[0],Integer.valueOf(param[1]),Integer.valueOf(param[2]));
                S7OrderCode orderCode = new S7OrderCode();
                Integer result = comS7.GetOrderCode(orderCode);
                int numCPU = -1;
                if(res.equals(0) && result.equals(0)) {
                    numCPU = Integer.valueOf(orderCode.Code().toString().substring(5,8));
                } else {
                    numCPU = 0000;
                }
                sendPreExecuteMessage(numCPU);
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
                            sout += "\nSélecteur en service : " + String.valueOf(selecteurEnService);
                            sendProgressMessage(selecteurEnService, 1);
                        }
                        //Nb comprimés sélectionné -> FONCTIONNEL
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
                            sout += "\nNb comprimés sélectionné : " + String.valueOf(nbComprimesSelectionnes);
                            sendProgressMessage(nbComprimesSelectionnes, 2);
                        }
                        //Nb comprimés par bouteille
                        retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 14, 2, datasPLC);
                        int nbComprimes = 0;
                        nbComprimes = 0;
                        if(retInfo == 0) {
                            nbComprimes = S7.GetWordAt(datasPLC, 0);
                            if(nbComprimes > nbComprimesSelectionnes) {
                                nbComprimes = nbComprimesSelectionnes;
                            }
                            sout += "\nNb comprimés par bouteille : " + String.valueOf(nbComprimes);
                            sendProgressMessage(nbComprimes, 3);
                        }
                        //Nb bouteilles remplies
                        retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 16, 2, datasPLC);
                        int nbBouteillesRemplies = 0;
                        if(retInfo == 0) {
                            nbBouteillesRemplies = S7.GetWordAt(datasPLC, 0);
                            sout += "\nNb bouteilles remplies : " + String.valueOf(nbBouteillesRemplies);
                            sendProgressMessage(nbBouteillesRemplies, 4);
                        }

                        System.out.println(sout);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendPostExecuteMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendPostExecuteMessage() {
            Message postExecuteMsg = new Message();
            postExecuteMsg.what = MESSAGE_POST_EXECUTE;
            monHandler.sendMessage(postExecuteMsg);
        }

        private void sendPreExecuteMessage(int v) {
            Message preExecuteMsg = new Message();
            preExecuteMsg.what = MESSAGE_PRE_EXECUTE;
            preExecuteMsg.arg1 = v;
            monHandler.sendMessage(preExecuteMsg);
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
