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

    private boolean readyToDraw = true;
//    private boolean disableByDistribution = false;
//    private boolean disableByKassa = false;

    @NonNull
    private int timeOnDistribution;

    @NonNull
    private int timeOnKassa;

    public void move(int coefficient) {
        if (x == 375) {
            if (timeOnDistribution != 0) {
                timeOnDistribution -= 1;
                return;
            } else {
                if(timeOnKassa != 0){

                } else {

                }
            }
        }


    }

    private boolean stayOnDistribution(int coefficient){
        if(timeOnDistribution <= 0)
            return false;
        else
            timeOnDistribution -= coefficient;
        return true;
    }

    private boolean stayOnKassa(int coefficient){
        if(timeOnKassa <= 0)
            return false;
        else
            timeOnKassa -= coefficient;
        return true;
    }
}
