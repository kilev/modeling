package com.kil;

import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class Client {

    private int x = 125;
    private int y = 75;
    private Point2D velocity = new Point2D(1, 1);
    private int visible = 1;

    private boolean onlyToKassa = true;
    private boolean readyToDraw = true;
    private boolean readyToGo = true;

    @NonNull
    private int timeOnDistribution;

    @NonNull
    private int timeOnKassa;

    public void move(int coefficient) {

        if(!readyToGo)
            return;

        if (x >= 370 && x <= 380) {
            stayOnDistribution(coefficient);
            return;
        }

        if (y >= 170 && y <= 180) {
            stayOnKassa(coefficient);
            return;
        }

        if (x >= 425 && y > 200) {
            death();
            return;
        }

        if (x < 425)
            moveX(coefficient);
        else
            moveY(coefficient);

    }

    private void moveX(int coefficient) {
        x = x + 1 + coefficient;
    }

    private void moveY(int coefficient) {
        y = y + 1 + coefficient;
    }

    private void stayOnDistribution(int coefficient) {
        if (timeOnDistribution <= 0)
            moveX(coefficient);
        else
            timeOnDistribution -= coefficient;
    }

    private void stayOnKassa(int coefficient) {
        if (timeOnKassa <= 0)
            moveY(coefficient);
        else
            timeOnKassa -= coefficient;
    }

    private void death() {
        Logic.clients.remove(0);
    }
}
