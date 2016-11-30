package rollMain;

import alerts.ExitAlert;
import interfaces.ObjectTitles;
import interfaces.Scale;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import rollMessContent.RollUI;

/**
 * Created by TIMBULI REMUS K@puc!n on 04-May-16.
 * <p>
 * This is the main class where the stage and scene
 * are created
 */
public class MainRoll extends Application implements Scale {

    // Pane & Scene variables-------------------------------------------------------------------------------------------
    private Pane pane = new Pane();
    private Scene scene = new Scene(pane, SCREEN_WIDTH / 3, SCREEN_HEIGHT / 2);
    //------------------------------------------------------------------------------------------------------------------

    // Main method------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        Application.launch(args);
    }
    //------------------------------------------------------------------------------------------------------------------

    // Start method-----------------------------------------------------------------------------------------------------
    public void start(Stage stage) {

        // CSS stylesheet-----------------------------------------------------------------------------------------------
        scene.getStylesheets().add(getClass().getResource(ObjectTitles.cssFile).toExternalForm());
        //--------------------------------------------------------------------------------------------------------------

        // Stage setup--------------------------------------------------------------------------------------------------
        pane.getChildren().add(new RollUI());
        stage.setTitle(ObjectTitles.stageTitle);
        stage.setX(SCREEN_WIDTH / 8);
        stage.setY(SCREEN_HEIGHT / 3);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> {
            e.consume();
            new ExitAlert();
        });
        //--------------------------------------------------------------------------------------------------------------
    }
    //------------------------------------------------------------------------------------------------------------------
}
