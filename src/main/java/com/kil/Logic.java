package com.kil;


import com.kil.controllers.FXMLController;

import java.util.ArrayList;
import java.util.List;

public class Logic {

    public static int onLine;
    public static boolean play = true;


    public static boolean[] razd = new boolean[6];
    public static boolean[] kassa = new boolean[3];
    public static List<Client> clients = new ArrayList<>();


    public static void createClient(boolean ticket, boolean onlyToKassa) {


    }

    class DrawThread extends Thread{
        @Override
        public void run(){
            while(play){

            }
        }
    }
}
