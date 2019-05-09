package com.kil;


import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Logic {

    public static int totalClientCount;
    public static int totalClientFinished;

    public static final Point2D SPAWN_POINT = new Point2D(125, 75);
    public static final int CIRCLE_RADIUS = 25;

    public static int stack;
    public static boolean play = true;
    public static int velocity = 1;
    public static List<Client> clients = new ArrayList<>();

    public double averageTimeOnService;
    public double averageTimeOnKassa;


    public static void createClient(boolean ticket) {
        Client client;
        if (ticket)
            client = new Client(200);
        else
            client = new Client(200, 200);
        clients.add(client);
        totalClientCount++;
        stack++;
    }


    //delete finished client from clients List
    public static void death(double id) {
        for (Client client : clients) {
            if (client.getId() == id) {
                clients.remove(client);
                totalClientFinished++;
                return;
            }
        }
    }
}
