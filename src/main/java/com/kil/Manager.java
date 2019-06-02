package com.kil;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    @Getter
    private boolean working = false;
    @Getter
    private Client client;
    @Getter
    private int clientCount;

    private List<Integer> serviceTimes = new ArrayList<>();
    private int timeStart;

    public Manager(){}

    public void setClient(Client client , int time){
        this.client = client;
        timeStart = time;
        working = true;
    }

    public void removeClient(int time){
        this.client = null;
        serviceTimes.add(time - timeStart);
        working = false;
        clientCount++;
    }

    public int getWorkTime(){
        return serviceTimes.stream().reduce(0, Integer::sum);
    }
}
