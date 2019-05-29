package com.kil.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.kil.Client;
import com.kil.Logic;
import com.kil.LogicDynamics;
import com.kil.LogicStatic;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FXMLController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField Text_M_inPut;

    @FXML
    private TextField Text_M_work;

    @FXML
    private TextField Text_S_pay;

    @FXML
    private TextField Text_time_h;

    @FXML
    private TextField Text_time_min;

    @FXML
    private TextField Text_time_sec;

    @FXML
    private Button button_save;

    @FXML
    private Label label_time;

    @FXML
    private TextField chanceTicket;

    @FXML
    private Label label_average_time_on_kassa;

    @FXML
    private Label label_average_time_on_service;

    @FXML
    private Label label_static_load_kassa_sec;

    @FXML
    private Label label_static_load_service_sec;

    @FXML
    private Label label_static_count;

    @FXML
    private Label label_static_noticket_count;

    @FXML
    private Label label_static_ticket_count;

    @FXML
    private Label label_static_load_service_percent;

    @FXML
    private Label label_static_load_kassa_percent;

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
    private long time;


    @FXML
    void initialize() {

        lowIndex = drawPane.getChildren().size() + 1;
        spawnCircle = new Circle(LogicDynamics.SPAWN_POINT.getX() - 1, LogicDynamics.SPAWN_POINT.getY(), LogicDynamics.CIRCLE_RADIUS);
        spawnCircle.setVisible(false);
        drawPane.getChildren().add(spawnCircle);

        //update timer
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                updateFPSandTime();
                update();
                updateData();
            }
        };

        //speed property
        ObservableList<Integer> speedValues = FXCollections.observableArrayList(1, 2, 4, 8, 16);
        comboBox_speed.setItems(speedValues);
        comboBox_speed.setValue(1);

        comboBox_speed.setOnAction(event -> LogicDynamics.velocity = comboBox_speed.getValue());

        //test button 1
        button_test1.setOnAction(event -> {
            LogicDynamics.createClient(false);
        });

        //test button 3
        button_test3.setOnAction(event->{
            LogicDynamics.createClient(true);
        });

        //test button 2
        button_test2.setOnAction(event -> {
            if (LogicDynamics.play) {
                timer.stop();
                LogicDynamics.play = false;
            } else {
                timer.start();
                LogicDynamics.play = true;
            }
        });

        //button save
        button_save.setOnAction(event -> {
            // считывание входных данных
            Logic.totalTime = Integer.parseInt(Text_time_h.getText()) * 3600
                    + Integer.parseInt(Text_time_min.getText()) * 60
                    + Integer.parseInt(Text_time_sec.getText());
            Logic.chance = Double.parseDouble(chanceTicket.getText());
            Logic.matSpawn = Integer.parseInt(Text_M_inPut.getText());
            Logic.matService = Integer.parseInt(Text_M_work.getText());
            Logic.sigmaKassa = Double.parseDouble(Text_S_pay.getText());

            Logic.setClients();

            label_static_ticket_count.setText(String.valueOf(Logic.countTicketClient));
            label_static_noticket_count.setText(String.valueOf(Logic.countNoTicketClient));
            label_static_count.setText(String.valueOf(Logic.clientsCount));

            LogicStatic.lounchWork();

            //label_static_load_service_sec.setText(String.valueOf(Logic.totalTime - LogicStatic.timings.get(1).stream().reduce(0, Integer::sum)));
            //label_static_load_kassa_sec.setText(String.valueOf(Logic.totalTime - LogicStatic.timings.get(2).stream().reduce(0, Integer::sum)));
        });
    }

    private void updateData(){
        label_count.setText(String.valueOf(LogicDynamics.stack));
        label_totalClientFinished.setText(String.valueOf(LogicDynamics.totalClientFinished));
    }

    private void update() {


        clearPane();

        //add drawable clients to List
        List<Client> clientsToDraw = new ArrayList<>();
        for (Client client : LogicDynamics.clients) {
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
        for (Client client : LogicDynamics.clients) {
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
            LogicDynamics.stack--;
    }


    //clear space
    public void clearPane() {
        while (drawPane.getChildren().size() > lowIndex)
            drawPane.getChildren().remove(lowIndex);
    }

    //drawing some client
    public void drawClient(Client client) {
        if (client.isReadyToDraw()) {
            Circle circle = new Circle(client.getX(), client.getY(), LogicDynamics.CIRCLE_RADIUS);

            //set color
            if (client.isTicket())
                circle.setFill(Color.RED);
            else
                circle.setFill(Color.BLUE);

            drawPane.getChildren().add(circle);
        }
    }

    private void updateFPSandTime() {
        frameCnt++;
        long currenttimeNano = System.nanoTime();
        if (currenttimeNano > lastTimeFPS + 1000000000) {
            time++;
            label_time.setText(String.valueOf(time));

            label_FPS.setText(String.valueOf(frameCnt));
            frameCnt = 0;
            lastTimeFPS = currenttimeNano;
        }
    }

}