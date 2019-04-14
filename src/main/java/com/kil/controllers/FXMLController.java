package com.kil.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.kil.Client;
import com.kil.Logic;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
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
    private Button button_test1;

    @FXML
    private Button button_test2;

    @FXML
    private AnchorPane drawPane;

    private final int lowIndex = 18;// min index of circle on drawPane
    int i;
    int j;

    @FXML
    void initialize() {

        //update timer
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                update();
            }
        };
        timer.start();


        button_test1.setOnAction(event -> {
            Logic.createClient();
        });

        button_test2.setOnAction(event -> {
            Logic.clients.get(0).setX(Logic.clients.get(0).getX() + 50);
        });
    }

    public void clearPane() {
        while (drawPane.getChildren().size() > lowIndex)
            drawPane.getChildren().remove(lowIndex);
    }


    public void drawClient(Client client) {
        if (client.isReadyToDraw()) {
            Circle circle = new Circle(client.getX(), client.getY(), 15);

            //set color
            if (client.getTimeOnKassa() == 0)
                circle.setFill(Color.BLUE);
            else
                circle.setFill(Color.RED);

            drawPane.getChildren().add(circle);
        }
    }


    public void update() {
        if (Logic.play) {
            clearPane();

            List<Client> clientsToDraw = new ArrayList<>();
            for (Client client : Logic.clients) {
                if (client.isReadyToDraw())
                    clientsToDraw.add(client);
            }

            int velocity = Logic.velocity;

            for (Client client : clientsToDraw) {
                client.move(velocity);
            }

            for (Client client : clientsToDraw) {
                drawClient(client);
            }


        }
    }

}
