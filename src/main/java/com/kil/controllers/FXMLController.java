package com.kil.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.kil.Client;
import com.kil.Logic;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FXMLController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label_count;

    @FXML
    private Label label_FPS;

    @FXML
    private Button button_test1;

    @FXML
    private Button button_test2;

    @FXML
    private AnchorPane drawPane;

    @FXML
    private ComboBox<Integer> comboBox_speed;

    private final int lowIndex = 18;// min index of circle on drawPane
    long frameCnt;
    long lastTimeFPS;

    @FXML
    void initialize() {

        //update timer
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                checkFPS();
                update();
            }
        };
        timer.start();

        //speed property
        ObservableList<Integer> speedValues = FXCollections.observableArrayList(1, 2, 4, 8);
        comboBox_speed.setItems(speedValues);
        comboBox_speed.setValue(1);

        comboBox_speed.setOnAction(event -> {
            Logic.velocity = comboBox_speed.getValue();
        });

        //test button 1
        button_test1.setOnAction(event -> {
            Logic.createClient();
        });

        //test button 2
        button_test2.setOnAction(event -> {
            if (Logic.play) {
                timer.stop();
                Logic.play = false;
            } else {
                timer.start();
                Logic.play = true;
            }
        });
    }

    //clear space
    public void clearPane() {
        while (drawPane.getChildren().size() > lowIndex)
            drawPane.getChildren().remove(lowIndex);
    }

    //drawing some client
    public void drawClient(Client client) {
        if (client.isReadyToDraw()) {
            Circle circle = new Circle(client.getX(), client.getY(), 15);

            //set color
            if (client.isOnlyToKassa())
                circle.setFill(Color.BLUE);
            else
                circle.setFill(Color.RED);

            drawPane.getChildren().add(circle);
        }
    }


    public void update() {
        label_count.setText(String.valueOf(Logic.stack));

        clearPane();


        List<Client> clientsToDraw = new ArrayList<>();
        for (Client client : Logic.clients) {
            if (client.isReadyToDraw())
                clientsToDraw.add(client);
        }

        int velocity = Logic.velocity;

        /// TODO: 16.04.2019 переписать коллизию какое то сложное говно
//        int n = clientsToDraw.size() - 1;
//        for(int i = 0; i < n; i++){
//            if(clientsToDraw.get(n-1).getX() - clientsToDraw.get(n).getX() < 50 && clientsToDraw.get(n-1).getY() - clientsToDraw.get(n).getY() < 50)
//                clientsToDraw.get(n).setReadyToGo(false);
//            else
//                clientsToDraw.get(n).setReadyToGo(true);
//        }

        //moving and drawing clients
        for (Client client : clientsToDraw) {
            //checkCollision();
            client.move(velocity);
            drawClient(client);
        }

    }

    private void checkFPS() {
        frameCnt++;
        long currenttimeNano = System.nanoTime();
        if (currenttimeNano > lastTimeFPS + 1000000000) {
            label_FPS.setText(String.valueOf(frameCnt));
            frameCnt = 0;
            lastTimeFPS = currenttimeNano;
        }
    }

    /// TODO: 16.04.2019 переписать коллизию какое то сложное говно
    private void checkCollision() {
        int size = drawPane.getChildren().size();

        if (size > lowIndex) {
            for (int i = lowIndex; i < size - 1; i++){
                if(drawPane.getChildren().get(i).getBoundsInParent().intersects(drawPane.getChildren().get(i+1).getBoundsInParent()))
                    Logic.clients.get(i-lowIndex).setReadyToGo(false);
                else
                    Logic.clients.get(i-lowIndex).setReadyToGo(true);
            }
        }
    }

}