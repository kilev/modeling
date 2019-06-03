package com.kil.Logics;

import com.kil.Client;
import com.kil.Manager;

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
    public static int matKassa;
    public static int sigmaKassa;
    public static double accuracy;

    public static int serviceCount;
    public static int kassaCount;

    public static int averageTimeOnService;
    public static int averageTimeInStack;


    static List<Client> clients = new ArrayList<>();// коллекция клиентов

    //рандомим клиентов
    public static void setClients() {
        clients.clear();

        countNoTicketClient = 0;
        countTicketClient = 0;
        clientsCount = 0;
        int localTime = 0;

        while (localTime < totalTime) {

            int spawnRandom = getRandomExpValue(matSpawn);
            localTime += spawnRandom;
            clientsCount++;

            if (Math.random() < chance) {
                clients.add(new Client(spawnRandom, getRandomExpValue(matService), getRandomNormValue(sigmaKassa, matKassa)));
                countNoTicketClient++;
            } else {
                clients.add(new Client(spawnRandom, getRandomExpValue(matService)));
                countTicketClient++;
            }
        }
    }

    private static void lounchDynamic() {
        LogicDynamics.stack = 0;
        LogicDynamics.clients.clear();
        for (Client client : clients) {
            LogicDynamics.createClient(client);
        }
    }


    private static int getRandomNormValue(int sigma, int mat) {
        //return (int) (sigma * Math.sqrt(-2 * Math.log(Math.random())));
        //////
        double x, y, s;
        do{
            x = Math.random() * 2 - 1;
            y = Math.random() * 2 - 1;
            s = x * x + y * y;
        }while(s > 1 || s == 0);

        double z = x * Math.sqrt((-2 * Math.log10(s)) / s);
        int res = (int) (mat + sigma * z);
        return res;
    }

    private static int getRandomExpValue(int mat) {
        return (int) (-(mat) * Math.log(Math.random()));
    }


    static double getMean(List<Integer> data) {
        int temp = data.stream().reduce(0, Integer::sum);
        return temp / data.size();
    }

    static double getMeanDouble(List<Double> data) {
        double temp = data.stream().reduce(0.0, Double::sum);
        return temp / data.size();
    }

    static double getVariance(List<Integer> data, double mean) {
        double temp = 0;
        for (int a : data)
            temp += ((a - mean) * (a - mean));
        return temp / (data.size());
    }

    public static List<Integer> averageKassaAmount = new ArrayList<>();
    public static List<Integer> averageServiceAmount = new ArrayList<>();
    public static List<Integer> averageClientCount = new ArrayList<>();
    public static List<Integer> averageTicketClientsCount = new ArrayList<>();
    public static List<Integer> averageNoTicketClientsCount = new ArrayList<>();
    public static List<Integer> averageServiceTime = new ArrayList<>();
    public static List<Integer> averageStackTime = new ArrayList<>();

    public static double experiment(double epsilon) {
        Map<Double, Double> t = new HashMap<>() {{
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

        averageKassaAmount.clear();
        averageServiceAmount.clear();
        averageClientCount.clear();
        averageTicketClientsCount.clear();
        averageNoTicketClientsCount.clear();
        averageServiceTime.clear();
        averageStackTime.clear();

        //TODO: сделать для каждого цикла
        List<List<Integer>> amountsService = new ArrayList<>();
        for (int i = 0; i < Logic.serviceCount; i++) {
            amountsService.add(new ArrayList<>());
        }
        List<List<Integer>> amountsKassa = new ArrayList<>();
        for (int i = 0; i < Logic.kassaCount; i++) {
            amountsKassa.add(new ArrayList<>());
        }

        while (true) {
            for (int i = 0; i < N; i++) {
                setClients();
                LogicStatic.lounchWork();

                //заполняем эти поля для подсчета средних значений//
                List<Integer> serviceAmount = LogicStatic.getServiceAmounts();
                List<Integer> kassaAmount = LogicStatic.getKassaAmounts();

                averageClientCount.add(LogicStatic.getClientsCount());
                averageTicketClientsCount.add(LogicStatic.getTicketsClientCount());
                averageNoTicketClientsCount.add(LogicStatic.getNoTicketsClientCount());
                averageServiceTime.add(LogicStatic.getAverageServiceTime());
                averageStackTime.add(LogicStatic.getAverageStackTime());
                ////

                for (int j = 0; j < serviceAmount.size(); j++) {
                    amountsService.get(j).add(serviceAmount.get(j));
                }

                for (int j = 0; j < kassaAmount.size(); j++) {
                    amountsKassa.get(j).add(kassaAmount.get(j));
                }
            }
            mean = getMean(amountsService.get(0));
            variance = getVariance(amountsService.get(0), mean);
            newN = (int) ((variance / (epsilon * mean * epsilon * mean)) * (t_a * t_a));
            if (N >= newN)
                break;
            else {
                N = newN;
                for (List<Integer> list : amountsService) {
                    list.clear();
                }
                for (List<Integer> list : amountsKassa) {
                    list.clear();
                }
            }
        }
        System.out.println("N := " + N);

        //подсчет средних значений за эксперимент
        for (int i = 0; i < serviceCount; i++) {
            averageServiceAmount.add((int) getMean(amountsService.get(i)));
        }
        for (int i = 0; i < kassaCount; i++) {
            averageKassaAmount.add((int) getMean(amountsKassa.get(i)));
        }
        clientsCount = (int) getMean(averageClientCount);
        countTicketClient = (int) getMean(averageTicketClientsCount);
        countNoTicketClient = (int) getMean(averageNoTicketClientsCount);
        averageTimeOnService = (int) getMean(averageServiceTime);
        averageTimeInStack = (int) getMean(averageStackTime);
        return mean;
    }




    public static void run() {
        double epsilon = accuracy;
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
    }

    public static String convertToHMSP(int value, boolean percent) {
        if(value == 0)
            return 0 + " C";
        String str = "";
        if (value / 3600 != 0)
            str += value / 3600 + "Ч ";
        if ((value % 3600) / 60 != 0)
            str += (value % 3600) / 60 + "М ";
        if ((value % 3600) % 60 != 0)
            str += (value % 3600) % 60 + "С ";
        if(percent)
            str += " или " + String.format("%.2f", 100 * ((double) value) / totalTime) + "%";
        return str;
    }
}