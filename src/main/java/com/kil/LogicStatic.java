package com.kil;

import java.util.ArrayList;
import java.util.List;

public class LogicStatic {

    private static List<Client> stack1 = new ArrayList<>();// очередь перед раздачей
    private static List<Client> stack2 = new ArrayList<>();// очередь перед кассой
    private static Client clientOnService;// указывает на клиента на раздаче
    private static Client clientOnKassa;// указывает на клиента на кассе

    public static List<Client> clients = new ArrayList<>();
    public static List<Client> finishedClients = new ArrayList<>();

    public static int localTime;

    public static void lounchWork() {
        localTime = 0;

        clients.clear();
        stack1.clear();
        stack2.clear();
        clientOnService = null;
        clientOnKassa = null;

        for (Client client : Logic.clients)
            clients.add(new Client(client));


        while (localTime < Logic.totalTime) {
            CheckWork();//проверяем можно ли назначит работу воркеру
            findNextEvent();// поиск след события
            doEvent();
            //localTime += 100;
        }
    }

    //TODO переделать чтение с евентбокса!
    private static void doEvent(){
        //for (Event event : Event.eventBox) {
        for(int i = 0; i < Event.eventBox.size(); i++){
            Event event = Event.eventBox.get(i);
            switch (event.event) {
                case "spawn":
                    stack1.add(clients.get(0));
                    clients.remove(0);
                    localTime += event.eventTime;
                    Event.eventBox.remove(event);

                    break;
                case "service done":
                    if (clientOnService.isTicket() || stack2.size() < 2) {
                        if (clientOnService.isTicket())
                            finishedClients.add(event.eventClient);
                        else
                            stack2.add(clientOnService);
                        clientOnService = null;
                        localTime += event.eventTime;
                        Event.eventBox.remove(event);
                    }
                    break;
                case "kassa done":
                    finishedClients.add(event.eventClient);
                    clientOnKassa = null;
                    localTime += event.eventTime;
                    Event.eventBox.remove(event);
                    break;
            }
            //localTime += event.eventTime;
        }
    }

    //поиск след события
    private static void findNextEvent() {
        Event event = new Event(null, Integer.MAX_VALUE, null);

        if (!clients.isEmpty() && clients.get(0).getSpawnDelay() < event.eventTime)
            event = new Event(clients.get(0), clients.get(0).getSpawnDelay(), "spawn");

        if (clientOnService != null) {
            if (clientOnService.getTimeOnService() < event.eventTime)
                event = new Event(clientOnService, clientOnService.getTimeOnService(), "service done");
        }

        if (clientOnKassa != null) {
            if (clientOnKassa.getTimeOnKassa() < event.eventTime)
                event = new Event(clientOnKassa, clientOnKassa.getTimeOnKassa(), "kassa done");
        }

        //вычитание времени
        if (!event.event.equals("spawn"))
            clients.get(0).setSpawnDelay(clients.get(0).getSpawnDelay() - event.eventTime);
        if(!event.event.equals("service done") && clientOnService != null)
            clientOnService.setTimeOnService(clientOnService.getTimeOnService() - event.eventTime);
        if(!event.event.equals("kassa done") && clientOnKassa != null)
            clientOnKassa.setTimeOnKassa(clientOnKassa.getTimeOnKassa() - event.eventTime);

        Event.add(event);
    }

    //проверяем свободна ли станция и пуляем туда клиента
    private static void CheckWork() {
        if (clientOnService == null && !stack1.isEmpty()) {
            clientOnService = stack1.get(0);
            stack1.remove(0);
        }

        if (clientOnKassa == null && !stack2.isEmpty()) {
            clientOnKassa = stack2.get(0);
            stack2.remove(0);
        }
    }
}
