package com.kil.Logics;

import com.kil.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logic {

    public static double chance;
    public static int totalTime;
    public static int clientsCount;
    public static int countTicketClient;
    public static int countNoTicketClient;

    public static int matSpawn;
    public static int matService;
    public static double sigmaKassa;

    public static int meanService;
    public static int meanKassa;

    static List<Client> clients = new ArrayList<>();// коллекция клиентов

    public static void setClients() {
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

        //lounchDynamic();
        //experiment(0.0001);
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




    static double getMean(List<Integer> data){
        int temp = data.stream().reduce(0, Integer::sum);
        return temp/data.size();
    }

    static double getMeanDouble(List<Double> data){
        double temp = data.stream().reduce(0.0, Double::sum);
        return temp/data.size();
    }

    static double getVariance(List<Integer> data, double mean) {
        double temp = 0;
        for(int a : data)
            temp += ((a - mean) * (a - mean));
        return temp / (data.size());
    }

    static List<Double> averageKassaAmount = new ArrayList<>();

    public static double experiment(double epsilon) {
        Map<Double, Double> t = new HashMap<Double, Double>() {{
            put(0.20, 1.64);
            put(0.15, 2.08);
            put(0.10, 2.71);
            put(0.05, 3.84);
            put(0.04, 4.21);
            put(0.03, 4.49);
            put(0.02, 5.43);
            put(0.01, 6.61);
            put(0.005, 7.90);
            put(0.001, 10.90);
            put(0.0005, 12.25);
            put(0.0001, 15.20);
        }};

        int newN, N = 50;
        double t_a = t.get(epsilon);
        double variance;
        double mean;
        List<Integer> amounts = new ArrayList<>();
        List<Integer> amountsKassa = new ArrayList<>();

        while (true) {
            for (int i = 0; i < N; i++) {
                setClients();
                LogicStatic.lounchWork();

                amounts.add(LogicStatic.getCustomersAmount());
                amountsKassa.add((LogicStatic.getKassaAmount()));
            }
            mean = getMean(amounts);
            variance = getVariance(amounts, mean);
            newN = (int) ((variance / (epsilon * mean * epsilon * mean)) * (t_a * t_a));
            if (N >= newN)
                break;
            else {
                N = newN;
                amounts.clear();
            }
        }
        System.out.println("N := " + N);
        averageKassaAmount.add(getMean(amountsKassa));
        return mean;
    }

    public static void run(){
        double epsilon = 0.2;
        List<Double> averageAmounts = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            averageAmounts.add(experiment(epsilon));
        }
        System.out.println(averageAmounts);
        System.out.println("Mean: " + getMeanDouble(averageAmounts));
        System.out.println("Epsilon: " + epsilon);
        double min, max;
        min = averageAmounts.stream()
                .min(Double::compareTo)
                .orElse(Double.NaN);
        max = averageAmounts.stream()
                .max(Double::compareTo)
                .orElse(Double.NaN);
        System.out.printf("Min: %.2f; Max %.2f %n", min, max);
        System.out.printf("Difference: %.2f %n", (max - min));

        meanService = (int) getMeanDouble(averageAmounts);
        meanKassa = (int) getMeanDouble(averageKassaAmount);
        averageKassaAmount.clear();
    }

    public static String convertToHMS(int value){
        String str = "";
        if(value / 3600 != 0)
            str += value / 3600 + "Ч ";
        if((value % 3600) / 60 != 0)
            str += (value % 3600) / 60 + "М ";
        if((value % 3600) % 60 != 0)
            str += (value % 3600) % 60 + "С";
        str += "  или " + String.format("%.2f", 100 * ((double) value) / totalTime)+ "%";
        return str;
    }
}