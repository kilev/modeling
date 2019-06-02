package com.kil.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.kil.Client;
import com.kil.Logics.Logic;
import com.kil.Logics.LogicDynamics;
import com.kil.Logics.LogicStatic;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private Label label_average_time_in_stack;

    @FXML
    private Label label_average_time_on_service;

    @FXML
    private Label label_static_count;

    @FXML
    private Label label_static_noticket_count;

    @FXML
    private Label label_static_ticket_count;

    @FXML
    private Label label_count;

    @FXML
    private Label label_FPS;

    @FXML
    private Label label_totalClientFinished;

    @FXML
    private ListView<String> kassaList;

    @FXML
    private ListView<String> serviceList;

    @FXML
    private Spinner<Integer> countService;

    @FXML
    private Spinner<Integer> countKassa;

    @FXML
    private Button button_test2;

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

        //setUp spinners
        SpinnerValueFactory<Integer> valueFactoryService = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        SpinnerValueFactory<Integer> valueFactoryKassa = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        countKassa.setValueFactory(valueFactoryService);
        countService.setValueFactory(valueFactoryKassa);

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
                    + Integer.parseInt(Text_time_sec.getText());// считывание общего времени моделирования
            Logic.chance = Double.parseDouble(chanceTicket.getText());// считывание вероятности талона
            Logic.matSpawn = Integer.parseInt(Text_M_inPut.getText());// считывание мат ожидания спавна
            Logic.matService = Integer.parseInt(Text_M_work.getText());// считывание мат ожиданя обслуживания на раздаче
            Logic.sigmaKassa = Double.parseDouble(Text_S_pay.getText());// считывания сигмы обслуживания на кассе

            Logic.serviceCount = countService.getValue();
            Logic.kassaCount = countKassa.getValue();

            //запуск логики
            Logic.run();

            //вывод статистики
            ObservableList<String> serviceBooks = FXCollections.observableArrayList();
            for (int i = 0; i < Logic.serviceCount; i++) {
                serviceBooks.add("раздача №" + (i+1) + ":   " + Logic.convertToHMSP(Logic.averageServiceAmount.get(i), true));
            }
            ObservableList<String> kassaBooks = FXCollections.observableArrayList();
            for (int i = 0; i < Logic.kassaCount; i++) {
                kassaBooks.add("касса №" + (i+1) + ":   " + Logic.convertToHMSP(Logic.averageKassaAmount.get(i), true));
            }
            kassaList.setItems(kassaBooks);
            serviceList.setItems(serviceBooks);

            label_static_ticket_count.setText(String.valueOf(Logic.countTicketClient));// вывод кол-ва клиентов
            label_static_noticket_count.setText(String.valueOf(Logic.countNoTicketClient));// вывод кол-ва клиентов без талона
            label_static_count.setText(String.valueOf(Logic.clientsCount));// вывод кол-ва клиентов с талоном
            label_average_time_on_service.setText(String.valueOf(Logic.convertToHMSP(Logic.averageTimeOnService, false)));
            label_average_time_in_stack.setText(Logic.convertToHMSP(Logic.averageTimeInStack, false));
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