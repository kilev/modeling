package com.kil;


import com.kil.controllers.FXMLController;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Logic {

    public static int stack;
    public static boolean play = true;
    public static int velocity = 1;
    public static List<Client> clients = new ArrayList<>();


    public static void createClient() {
        Client client = new Client(150,150);
        clients.add(client);
    }




}
