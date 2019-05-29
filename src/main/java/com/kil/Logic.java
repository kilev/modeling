package com.kil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Logic {

    public static double chance = 0.5;

    public static int totalTime;
    public static int clientsCount;
    public static int countTicketClient;
    public static int countNoTicketClient;

    public static int matSpawn;
    public static int matService;
    public static double sigmaKassa;

    public static List<Integer> listSpawnTimings = new ArrayList<>();

    public static List<Client> clients = new ArrayList<>();// коллекция клиентов

    public static void setClients() {
        countNoTicketClient = 0;
        countTicketClient = 0;
        clientsCount = 0;
        clients.clear();
        listSpawnTimings.clear();

        while(listSpawnTimings.stream().reduce(0, Integer::sum) < totalTime){

            listSpawnTimings.add(getRandomExpValue(matSpawn));
            clientsCount++;

            if (Math.random() < chance) {
                clients.add(new Client(getRandomExpValue(matSpawn), getRandomExpValue(matService), getRandomReleyValue(sigmaKassa)));
                countNoTicketClient++;
            } else {
                clients.add(new Client(getRandomExpValue(matSpawn), getRandomExpValue(matService)));
                countTicketClient++;
            }
        }

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
