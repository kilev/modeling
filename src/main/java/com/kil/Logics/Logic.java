package com.kil.Logics;

import com.kil.Client;

import java.util.ArrayList;
import java.util.List;

public class Logic {

    public static double chance = 0.5;

    public static int totalTime;
    public static int clientsCount;
    public static int countTicketClient;
    public static int countNoTicketClient;

    public static int matSpawn;
    public static int matService;
    public static double sigmaKassa;

    static List<Client> clients = new ArrayList<>();// коллекция клиентов

    public static void setClientsAndStart() {
        clients.clear();

        countNoTicketClient = 0;
        countTicketClient = 0;
        clientsCount = 0;
        int localTime = 0;

        while(localTime < totalTime){

            int spawnRandom = getRandomExpValue(matSpawn);
            localTime += spawnRandom;
            clientsCount++;

            if (Math.random() < chance) {
                clients.add(new Client(spawnRandom, getRandomExpValue(matService), getRandomReleyValue(sigmaKassa)));
                countNoTicketClient++;
            } else {
                clients.add(new Client(spawnRandom, getRandomExpValue(matService)));
                countTicketClient++;
            }
        }

        lounchDynamic();
        LogicStatic.lounchWork();
    }

    private static void lounchDynamic(){
        LogicDynamics.stack = 0;
        LogicDynamics.clients.clear();
        for (Client client : clients) {
            LogicDynamics.createClient(client);
        }
    }


    private static int getRandomReleyValue(double sigma) {
        return (int) (sigma * Math.sqrt(-2 * Math.log(Math.random())));
    }

    private static int getRandomExpValue(int mat) {
        return (int) (-(mat) * Math.log(Math.random()));
    }
}