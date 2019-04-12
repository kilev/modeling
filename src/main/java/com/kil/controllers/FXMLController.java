package com.kil.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.kil.Client;
import javafx.fxml.FXML;
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
    private  Label label_count;

    @FXML
    private Button button_test;

    @FXML
    private  AnchorPane drawPane;

    private  final int lowIndex = 18;// min index of circle on drawPane

    @FXML
    void initialize() {

        button_test.setOnAction(event->{
            while(drawPane.getChildren().get(lowIndex) != null)
                drawPane.getChildren().remove(lowIndex);
        });

        Client client = new Client(true,true);
        Client cl1 = new Client(false, true);
        cl1.setX(175);
        drawClient(client);
        drawClient(cl1);
        //drawPane.getChildren().remove(18);

    }

    public  void clearPane(){
        while(drawPane.getChildren().get(lowIndex) != null)
            drawPane.getChildren().remove(lowIndex);
    }

    public  void drawClient(Client client) {
        if (client.isReadyToDraw()) {
            Circle circle = new Circle(client.getX(), client.getY(), 15);
            if(client.isTicket())
                circle.setFill(Color.BLUE);
            else
                circle.setFill(Color.RED);
            drawPane.getChildren().add(circle);
            label_count.setText(String.valueOf((int) drawPane.getChildren().size()));
        }
    }
}
