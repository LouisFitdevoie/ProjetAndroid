package be.heh.fitdevoie.projetandroidstudio.TaskS7;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
    private View vi_main_ui;
    private Button bt_connect;
    private TextView tv_niveau_selecteurMode;
    private TextView tv_niveau_niveauLiquide;
    private TextView tv_niveau_niveauConsigneAuto;
    private TextView tv_niveau_niveauConsigneManuel;
    private TextView tv_niveau_sortie;
    private TextView tv_niveau_valve1;
    private TextView tv_niveau_valve2;
    private TextView tv_niveau_valve3;
    private TextView tv_niveau_valve4;

    private AutomateS7 plcS7;
    private Thread readThread;
    private S7Client comS7;
    private String[] param = new String[10];
    private byte[] datasPLC = new byte[512];

    private RelativeLayout rl_niveau_dataToWrite;

    public ReadTaskS7Niveau(View view,
                            Button bt_connect,
                            TextView tv_niveau_selecteurMode,
                            TextView tv_niveau_niveauLiquide,
                            TextView tv_niveau_niveauConsigneAuto,
                            TextView tv_niveau_niveauConsigneManuel,
                            TextView tv_niveau_sortie,
                            TextView tv_niveau_valve1,
                            TextView tv_niveau_valve2,
                            TextView tv_niveau_valve3,
                            TextView tv_niveau_valve4,
                            RelativeLayout rl_niveau_dataToWrite) {
        this.vi_main_ui = view;
        this.bt_connect = bt_connect;
        this.tv_niveau_selecteurMode = tv_niveau_selecteurMode;
        this.tv_niveau_niveauLiquide = tv_niveau_niveauLiquide;
        this.tv_niveau_niveauConsigneAuto = tv_niveau_niveauConsigneAuto;
        this.tv_niveau_niveauConsigneManuel = tv_niveau_niveauConsigneManuel;
        this.tv_niveau_sortie = tv_niveau_sortie;
        this.tv_niveau_valve1 = tv_niveau_valve1;
        this.tv_niveau_valve2 = tv_niveau_valve2;
        this.tv_niveau_valve3 = tv_niveau_valve3;
        this.tv_niveau_valve4 = tv_niveau_valve4;
        this.rl_niveau_dataToWrite = rl_niveau_dataToWrite;

        comS7 = new S7Client();
        plcS7 = new AutomateS7();

        readThread = new Thread(plcS7);
    }

    public void Stop() {
        isRunning.set(false);
        comS7.Disconnect();
        readThread.interrupt();
    }

    public void Start(String ipAddress, String rack, String slot) {
        if(!readThread.isAlive()) {
            param[0] = ipAddress;
            param[1] = rack;
            param[2] = slot;

            readThread.start();
            isRunning.set(true);
        }
    }

    private void downloadOnPreExecute(int t) {

    }

    private void downloadOnProgressUpdate(int progress, int what) {
        switch (what) {
            case 0: //TV sélecteur de mode
                if(progress == 1) {
                    tv_niveau_selecteurMode.setText("Automatique");
                } else {
                    tv_niveau_selecteurMode.setText("Manuel");
                }
                break;
            case 1: //TV niveau de liquide
                tv_niveau_niveauLiquide.setText(String.valueOf(progress));
                break;
            case 2: //TV niveau de consigne automatique
                tv_niveau_niveauConsigneAuto.setText(String.valueOf(progress));
                break;
            case 3: //TV niveau manuel demandé
                tv_niveau_niveauConsigneManuel.setText(String.valueOf(progress));
                break;
            case 4: //TV sortie
                tv_niveau_sortie.setText(String.valueOf(progress));
                break;
            case 5: //TV état valve 1
                if(progress == 1) {
                    tv_niveau_valve1.setText("Fermée");
                } else {
                    tv_niveau_valve1.setText("Ouverte");
                }
                break;
            case 6: //TV état valve 2
                if(progress == 1) {
                    tv_niveau_valve2.setText("Fermée");
                } else {
                    tv_niveau_valve2.setText("Ouverte");
                }
                break;
            case 7: //TV état valve 3
                if(progress == 1) {
                    tv_niveau_valve3.setText("Fermée");
                } else {
                    tv_niveau_valve3.setText("Ouverte");
                }
                break;
            case 8: //TV état valve 4
                if(progress == 1) {
                    tv_niveau_valve4.setText("Fermée");
                } else {
                    tv_niveau_valve4.setText("Ouverte");
                }
                break;
        }
    }

    private void downloadOnPostExecute() {

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
                        //Selecteur de mode
                        int retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 0, 2, datasPLC);
                        int selectMode = 0;
                        Boolean dataBoolean = false;
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 0, 5);
                            if(dataBoolean) {
                                selectMode = 1;
                            } else {
                                selectMode = 0;
                            }
                            sout += "Sélecteur de mode : " + String.valueOf(selectMode);
                            sendProgressMessage(selectMode, 0);
                        }
                        //Niveau de liquide
                        retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 16, 10, datasPLC);
                        int niveauLiquide = 0;
                        if(retInfo == 0) {
                            niveauLiquide = S7.GetWordAt(datasPLC, 0);
                            sout += "\nNiveau de liquide : " + String.valueOf(niveauLiquide);
                            sendProgressMessage(niveauLiquide, 1);
                        }
                        //Niveau de consigne automatique
                        int niveauConsigneAuto = 0;
                        if(retInfo == 0) {
                            niveauConsigneAuto = S7.GetWordAt(datasPLC, 2);
                            sout += "\nNiveau de consigne automatique : " + String.valueOf(niveauConsigneAuto);
                            sendProgressMessage(niveauConsigneAuto, 2);
                        }
                        //Niveau manuel demandé
                        int niveauManuel = 0;
                        if(retInfo == 0) {
                            niveauManuel = S7.GetWordAt(datasPLC, 4);
                            sout += "\nNiveau manuel demandé : " + String.valueOf(niveauManuel);
                            sendProgressMessage(niveauManuel, 3);
                        }
                        //Niveau de sortie
                        int niveauSortie = 0;
                        if(retInfo == 0) {
                            niveauSortie = S7.GetWordAt(datasPLC, 6);
                            sout += "\nNiveau de sortie : " + String.valueOf(niveauSortie);
                            sendProgressMessage(niveauSortie, 4);
                        }
                        //Etat valve 1
                        retInfo = comS7.ReadArea(S7.S7AreaDB, 5, 0, 2, datasPLC);
                        dataBoolean = false;
                        int etatValve1 = 0;
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 0, 1);
                            if(dataBoolean) {
                                etatValve1 = 1;
                            } else {
                                etatValve1 = 0;
                            }
                            sout += "\nEtat valve 1 : " + String.valueOf(etatValve1);
                            sendProgressMessage(etatValve1, 5);
                        }
                        //Etat valve 2
                        dataBoolean = false;
                        int etatValve2 = 0;
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 0, 2);
                            if(dataBoolean) {
                                etatValve2 = 1;
                            } else {
                                etatValve2 = 0;
                            }
                            sout += "\nEtat valve 2 : " + String.valueOf(etatValve2);
                            sendProgressMessage(etatValve2, 6);
                        }
                        //Etat valve 3
                        dataBoolean = false;
                        int etatValve3 = 0;
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 0, 3);
                            if(dataBoolean) {
                                etatValve3 = 1;
                            } else {
                                etatValve3 = 0;
                            }
                            sout += "\nEtat valve 3 : " + String.valueOf(etatValve3);
                            sendProgressMessage(etatValve3, 7);
                        }
                        //Etat valve 4
                        dataBoolean = false;
                        int etatValve4 = 0;
                        if(retInfo == 0) {
                            dataBoolean = S7.GetBitAt(datasPLC, 0, 4);
                            if(dataBoolean) {
                                etatValve4 = 1;
                            } else {
                                etatValve4 = 0;
                            }
                            sout += "\nEtat valve 4 : " + String.valueOf(etatValve4);
                            sendProgressMessage(etatValve4, 8);
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
