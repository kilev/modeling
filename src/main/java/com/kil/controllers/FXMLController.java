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
    private Label label_totalClientFinished;

    @FXML
    private Button button_test1;

    @FXML
    private Button button_test2;

    @FXML
    private Button button_test3;

    @FXML
    private AnchorPane drawPane;

    @FXML
    private ComboBox<Integer> comboBox_speed;

    private int lowIndex;// min index of circle on drawPane
    private long frameCnt;
    private long lastTimeFPS;
    private Circle spawnCircle;


    @FXML
    void initialize() {

        lowIndex = drawPane.getChildren().size() + 1;
        spawnCircle = new Circle(Logic.SPAWN_POINT.getX() - 1, Logic.SPAWN_POINT.getY(), Logic.CIRCLE_RADIUS);
        spawnCircle.setVisible(false);
        drawPane.getChildren().add(spawnCircle);

        //update timer
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                checkFPS();
                update();
                updateData();
            }
        };
        timer.start();

        //speed property
        ObservableList<Integer> speedValues = FXCollections.observableArrayList(1, 2, 4, 8, 16);
        comboBox_speed.setItems(speedValues);
        comboBox_speed.setValue(1);

        comboBox_speed.setOnAction(event -> Logic.velocity = comboBox_speed.getValue());

        //test button 1
        button_test1.setOnAction(event -> {
            Logic.createClient(false);
        });

        button_test3.setOnAction(event->{
            Logic.createClient(true);
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

    private void updateData(){
        label_count.setText(String.valueOf(Logic.stack));
        label_totalClientFinished.setText(String.valueOf(Logic.totalClientFinished));
    }

    private void update() {
        clearPane();

        //add drawable clients to List
        List<Client> clientsToDraw = new ArrayList<>();
        for (Client client : Logic.clients) {
            if (client.isReadyToDraw())
                clientsToDraw.add(client);
        }

        //moving and drawing clients
        for (Client client : clientsToDraw) {
            client.move();
            drawClient(client);
        }

        checkSpawn();
    }


    //check space to create(draw) new client
    private void checkSpawn() {
        Client potentialClient = null;
        for (Client client : Logic.clients) {
            if (!client.isReadyToDraw()) {
                potentialClient = client;
                break;
            }
        }

        if (potentialClient == null)
            return;

        potentialClient.setReadyToDraw(true);
        for (int i = lowIndex; i < drawPane.getChildren().size(); i++) {
            if (spawnCircle.getBoundsInParent().intersects(drawPane.getChildren().get(i).getBoundsInParent())) {
                potentialClient.setReadyToDraw(false);
                break;
            }
        }

        if (potentialClient.isReadyToDraw())
            Logic.stack--;
    }


    //clear space
    public void clearPane() {
        while (drawPane.getChildren().size() > lowIndex)
            drawPane.getChildren().remove(lowIndex);
    }

    //drawing some client
    public void drawClient(Client client) {
        if (client.isReadyToDraw()) {
            Circle circle = new Circle(client.getX(), client.getY(), Logic.CIRCLE_RADIUS);

            //set color
            if (client.isTicket())
                circle.setFill(Color.RED);
            else
                circle.setFill(Color.BLUE);

            drawPane.getChildren().add(circle);
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

}