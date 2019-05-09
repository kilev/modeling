package com.kil;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Client {

    private int[] way;
    private String[] direction;

    private int x = (int) Logic.SPAWN_POINT.getX();
    private int y = (int) Logic.SPAWN_POINT.getY();
    private int velocity = 1;
    private int visible = 1;

    private boolean ticket;
    private boolean readyToDraw = false;

    private double id;
    private double TimeOnService;
    private double TimeOnKassa;


    Client(int timeOnDistribution, int timeOnKassa) {
        this.ticket = false;
        id = Logic.totalClientCount;
            loadClassicProperties(timeOnDistribution, timeOnKassa);
    }

    Client(int timeOnDistribution){
        this.ticket = true;
        id = Logic.totalClientCount;
        loadTicketProperties(timeOnDistribution);
    }


    public void move() {
        if (checkCollision())
            return;
        moveByWay();
    }

    private boolean checkCollision(){
        for (Client client : Logic.clients) {
            if(client == this)
                continue;
            if(client.getX() > this.getX() && client.getY() == this.getY())
                if(client.getX() - this.getX() <= Logic.CIRCLE_RADIUS * 2)
                    return true;
            if(client.getY() > this.getY() && client.getX() == this.getX())
                if(client.getY() - this.getY() <= Logic.CIRCLE_RADIUS * 2)
                    return true;
        }
        return false;
    }

    private void moveByWay() {
        int trekID = -1;

        for (int i = 0; i < way.length; i++) {
            if (way[i] > 0) {
                trekID = i;
                break;
            }
        }
        if (trekID == -1)
            throw new NullPointerException();

        String dir = direction[trekID];
        int localWay = velocity * Logic.velocity;
        if(localWay > way[trekID])
            localWay = way[trekID];

        switch (dir) {
            case "forward":
                x = x + localWay;
                way[trekID] -= localWay;
                break;
            case "down":
                y = y + localWay;
                way[trekID] -= localWay;
                break;
            case "pause":
                way[trekID] -= localWay;
                break;
            case "death":
                Logic.death(id);
                break;
        }
    }

    private void loadTicketProperties(int timeOnDistribution){
        way = new int[]{250, timeOnDistribution, 25, 1};
        direction = new String[]{"forward", "pause", "down", "death"};
    }

    private void loadClassicProperties(int timeOnDistribution, int timeOnKassa){
        way = new int[]{250, timeOnDistribution, 50, 100, timeOnKassa, 25, 1};
        direction = new String[]{"forward", "pause", "forward", "down", "pause", "down", "death"};
    }
}
