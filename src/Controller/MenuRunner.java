package Controller;
import highscore.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import sun.audio.AudioStream;

import java.io.File;
import java.io.InputStream;

public class MenuRunner {

    @FXML private AnchorPane menuBase;

    private final Button mainmenuButton = new Button("Main Menu");

    public MenuRunner() {

    }


    //shamelessly stolen from highscore
    public void initialize() {
        HBox table = new HBox(2);
        table.setAlignment(Pos.TOP_CENTER);

        table.getChildren().addAll(mainmenuButton);

        table.setStyle("-fx-background-color:GREY");

        //add the table to the scene
        menuBase.getChildren().add(table);
    }

    private void startGame() throws Exception{
        try {
            Scene game = FXMLLoader.load(getClass().getResource("game.fxml"));
            Main.changeScene(game);
            //TODO pass code to load the level file
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void level1(ActionEvent actionEvent) throws Exception {
        this.startGame();
        Main.setLevelNum(1);
    }

    public void level2(ActionEvent actionEvent) throws Exception {
        this.startGame();
        Main.setLevelNum(2);
    }
}
