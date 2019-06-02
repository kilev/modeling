package com.kil.Logics;

import com.kil.Client;
import com.kil.Event;
import com.kil.Manager;

import java.util.ArrayList;
import java.util.List;

public class LogicStatic {

    private static List<Client> stack1 = new ArrayList<>();// очередь перед раздачей
    private static List<Client> stack2 = new ArrayList<>();// очередь перед кассой
    public static Manager serviceManager;// указывает на клиента на раздаче
    public static Manager kassaManager;// указывает на клиента на кассе

    public static List<Client> clients = new ArrayList<>();

    private static int localTime;
    private static Event nextEvent;


    static void lounchWork() {
        localTime = 0;

        clients.clear();
        stack1.clear();
        stack2.clear();
        serviceManager = new Manager();
        kassaManager = new Manager();

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
                clients.remove(0);
                break;

            case "service done":
                if (!serviceManager.getClient().isTicket())
                    stack2.add(nextEvent.eventClient);
                serviceManager.removeClient(localTime);
                break;

            case "kassa done":
                kassaManager.removeClient(localTime);
                break;
        }
    }

    //поиск след события
    private static void findNextEvent() {
        Event event = new Event(null, Integer.MAX_VALUE, null);

        if (!clients.isEmpty() && clients.get(0).getSpawnDelay() < event.eventTime)
            event = new Event(clients.get(0), clients.get(0).getSpawnDelay(), "spawn");

        if (serviceManager.isWorking())
            if (serviceManager.getClient().getTimeOnService() < event.eventTime && stack2.size() < 3)
                event = new Event(serviceManager.getClient(), serviceManager.getClient().getTimeOnService(), "service done");

        if (kassaManager.isWorking())
            if (kassaManager.getClient().getTimeOnKassa() < event.eventTime)
                event = new Event(kassaManager.getClient(), kassaManager.getClient().getTimeOnKassa(), "kassa done");


        //вычитание времени
        if (!event.event.equals("spawn"))
            clients.get(0).setSpawnDelay(clients.get(0).getSpawnDelay() - event.eventTime);

        if (!event.event.equals("service done") && serviceManager.isWorking())
            if(serviceManager.getClient().getTimeOnService() < event.eventTime)
                serviceManager.getClient().setTimeOnService(0);
            else
                serviceManager.getClient().setTimeOnService(serviceManager.getClient().getTimeOnService() - event.eventTime);

        if (!event.event.equals("kassa done") && kassaManager.isWorking())
            if(kassaManager.getClient().getTimeOnKassa() < event.eventTime)
                kassaManager.getClient().setTimeOnKassa(0);
            else
                kassaManager.getClient().setTimeOnKassa(kassaManager.getClient().getTimeOnKassa() - event.eventTime);

        nextEvent = event;
    }

    //проверяем свободна ли станция и пуляем туда клиента
    private static void CheckWork() {
        if (!serviceManager.isWorking() && !stack1.isEmpty()) {
            serviceManager.setClient(stack1.get(0), localTime);
            stack1.remove(stack1.get(0));
        }
        if (!kassaManager.isWorking() && !stack2.isEmpty()) {
            kassaManager.setClient(stack2.get(0), localTime);
            stack2.remove(stack2.get(0));
        }
    }

    public static int getCustomersAmount(){
        return serviceManager.getWorkTime();
    }

    public static int getKassaAmount(){
        return kassaManager.getWorkTime();
    }
}
