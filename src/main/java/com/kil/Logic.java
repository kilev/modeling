package com.kil;


import com.kil.controllers.FXMLController;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Logic {

    public static int stack;
    public static boolean play = true;
    public static int velocity = 1;
    public static List<Client> clients = new ArrayList<>();


    public static void createClient() {
        Client client = new Client(20,20);
        clients.add(client);
    }




}
