package com.kil.Logics;

import com.kil.Client;
import com.kil.Event;
import com.kil.Manager;

import java.util.ArrayList;
import java.util.List;

public class LogicStatic {

    private static List<Integer> stackTime = new ArrayList<>();

    private static List<Client> stack1 = new ArrayList<>();// очередь перед раздачей
    private static List<Client> stack2 = new ArrayList<>();// очередь перед кассой

    public static List<Manager> managersService = new ArrayList<>();
    public static List<Manager> managersKassa = new ArrayList<>();

    public static List<Client> clients = new ArrayList<>();

    private static int localTime;
    private static Event nextEvent;


    static void lounchWork() {
        localTime = 0;

        clients.clear();
        stack1.clear();
        stack2.clear();

        stackTime.clear();
        managersService.clear();
        managersKassa.clear();
        for (int i = 0; i < Logic.serviceCount; i++) {
            managersService.add(new Manager());
        }
        for (int i = 0; i < Logic.kassaCount; i++) {
            managersKassa.add(new Manager());
        }

        // копируем клиентов
        for (Client client : Logic.clients)
            clients.add(new Client(client));

        while (localTime < Logic.totalTime) {
            CheckWork();//проверяем можно ли назначит работу воркеру
            findNextEvent();// поиск след события
            doEvent();
        }
    }

    private static void doEvent() {
        localTime += nextEvent.eventTime;

        switch (nextEvent.event) {
            case "spawn":
                stack1.add(nextEvent.eventClient);
                nextEvent.eventClient.setInStack(localTime);
                clients.remove(0);
                break;

            case "service done":
                for (Manager manager : managersService) {
                    if (manager.getClient() == nextEvent.eventClient) {
                        if (!manager.getClient().isTicket()){
                            stack2.add(nextEvent.eventClient);
                            nextEvent.eventClient.setInStack(localTime);
                        }
                        manager.removeClient(localTime);
                        return;
                    }
                }
                break;

            case "kassa done":
                for (Manager manager : managersKassa) {
                    if(manager.getClient() == nextEvent.eventClient){
                        manager.removeClient(localTime);
                        return;
                    }
                }
                break;
        }
    }

    //поиск след события
    private static void findNextEvent() {
        Event event = new Event(null, Integer.MAX_VALUE, null);

        if (!clients.isEmpty() && clients.get(0).getSpawnDelay() < event.eventTime)
            event = new Event(clients.get(0), clients.get(0).getSpawnDelay(), "spawn");

        for (Manager manager : managersService) {
            if (manager.isWorking())
                if (manager.getClient().getTimeOnService() < event.eventTime && stack2.size() < 3)
                    event = new Event(manager.getClient(), manager.getClient().getTimeOnService(), "service done");
        }

        for (Manager manager : managersKassa) {
            if (manager.isWorking())
                if (manager.getClient().getTimeOnKassa() < event.eventTime)
                    event = new Event(manager.getClient(), manager.getClient().getTimeOnKassa(), "kassa done");
        }


        //вычитание времени
        if (!event.event.equals("spawn"))
            clients.get(0).setSpawnDelay(clients.get(0).getSpawnDelay() - event.eventTime);

        for (Manager manager : managersService) {
            if (!event.event.equals("service done") && manager.isWorking())
                if (manager.getClient().getTimeOnService() < event.eventTime)
                    manager.getClient().setTimeOnService(0);
                else
                    manager.getClient().setTimeOnService(manager.getClient().getTimeOnService() - event.eventTime);
        }

        for (Manager manager : managersKassa) {
            if (!event.event.equals("kassa done") && manager.isWorking())
                if (manager.getClient().getTimeOnKassa() < event.eventTime)
                    manager.getClient().setTimeOnKassa(0);
                else
                    manager.getClient().setTimeOnKassa(manager.getClient().getTimeOnKassa() - event.eventTime);
        }
        nextEvent = event;
    }

    //проверяем свободна ли станция и пуляем туда клиента
    private static void CheckWork() {
        for (Manager manager : managersService) {
            if (!manager.isWorking() && !stack1.isEmpty()) {
                manager.setClient(stack1.get(0), localTime);
                stack1.get(0).setOutStack(localTime);
                if(stack1.get(0).isTicket())
                    stackTime.add(stack1.get(0).getStackTime());
                stack1.remove(stack1.get(0));

            }
        }
        for (Manager manager : managersKassa) {
            if (!manager.isWorking() && !stack2.isEmpty()) {
                manager.setClient(stack2.get(0), localTime);
                stack2.get(0).setOutStack(localTime);
                stackTime.add(stack2.get(0).getStackTime());
                stack2.remove(stack2.get(0));
            }
        }
    }

    public static List<Integer> getServiceAmounts() {
        List<Integer> res = new ArrayList<>();
        for (Manager manager : managersService) {
            res.add(manager.getWorkTime());
        }
        return res;
    }

    public static List<Integer> getKassaAmounts() {
        List<Integer> res = new ArrayList<>();
        for (Manager manager : managersKassa) {
            res.add(manager.getWorkTime());
        }
        return res;
    }

    public static int getClientsCount(){
        return Logic.clientsCount;
    }

    public static int getTicketsClientCount(){
        return Logic.countTicketClient;
    }

    public static int getNoTicketsClientCount(){
        return  Logic.countNoTicketClient;
    }
    
    public static int getAverageServiceTime(){
        int time = 0;
        int count = 0;
        for (Manager manager : managersService) {
            time += manager.getWorkTime();
            count += manager.getClientCount();
        }
        if(count != 0)
            return time/count;
        else
            return 0;
    }

    public static int getAverageStackTime(){
        return stackTime.stream().reduce(0,Integer::sum) / stackTime.size();
    }
}
