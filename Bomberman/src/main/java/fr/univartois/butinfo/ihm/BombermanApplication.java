package fr.univartois.butinfo.ihm;

import java.io.IOException;

import fr.univartois.butinfo.ihm.controller.BombermanController;
import fr.univartois.butinfo.ihm.model.GameMap;
import fr.univartois.butinfo.ihm.model.GameMapFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class BombermanApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/univartois/butinfo/ihm/view/bomberman-view.fxml"));
        Parent viewContent = fxmlLoader.load();
        BombermanController controller = fxmlLoader.getController();

        GameMap alternativeMap = GameMapFactory.createEmptyMap(controller.getHAUTEUR(), controller.getLARGEUR());

        GridPane gridPane = controller.getGridPane();
        configureGridPane(gridPane, controller);

        Scene scene = new Scene(viewContent, 1000, 600);

        controller.setStage(stage);
        controller.setScene(scene);

        stage.setScene(scene);
        stage.setTitle("Bomberman");
        stage.show();
    }

    private void configureGridPane(GridPane gridPane, BombermanController controller) {
        gridPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        gridPane.setScaleX(0.7);
        gridPane.setScaleY(0.7);

        for (int i = 0; i < controller.getLARGEUR(); i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / controller.getLARGEUR());
            gridPane.getColumnConstraints().add(column);
        }

        for (int i = 0; i < controller.getHAUTEUR(); i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / controller.getHAUTEUR());
            gridPane.getRowConstraints().add(row);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}