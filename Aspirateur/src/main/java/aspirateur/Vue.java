package aspirateur;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Vue extends Application {

    public GridPane gride = new GridPane() ;
    public Group root = new Group();
    public static int col = 2 ;
    public static int lin = 7 ;
    static int comp= 1 ;
    @Override
    public void start(Stage stage) throws Exception{
        Button down = new Button("telecharger");
        TextField url = new TextField() ;
        TextField nom = new TextField() ;
        url.setPrefWidth(400);
        url.setPrefHeight(39);
        nom.setPrefWidth(400);
        nom.setPrefHeight(39);
        down.setPrefWidth(200);
        down.setPrefHeight(39);
        gride.setLayoutX(99);
        gride.setLayoutY(39);
        gride.setVgap(20);
        gride.setHgap(20);
        gride.add(down,1,4);
        gride.add(url,1,0);
        gride.add(nom,1,1);
        gride.add(new Label("URF File"),0,0);
        gride.add(new Label("Dossier "),0,1);
        down.setOnAction(event -> {
            if(col>5){col =2 ; lin ++ ; }
            try {
                Telechargement t = new Telechargement(url.getText(),nom.getText());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Button tel = new Button("Fichier_"+comp);
            comp++ ;
            tel.setPrefWidth(150);
            tel.setPrefHeight(39);
            gride.add(tel,col,lin);
            col++ ;


        });
        gride.add(new Label("mes telechargement"), 1,5);
        root.getChildren().add(gride);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setHeight(800);
        stage.setWidth(1300);
        stage.setTitle("  ASPIRATEUR  ");
        stage.centerOnScreen();
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}