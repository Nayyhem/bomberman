package fr.univartois.butinfo.ihm.controller;

import fr.univartois.butinfo.ihm.model.AbstractBomb;
import fr.univartois.butinfo.ihm.model.GameFacade;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.NoSuchElementException;

public class BombSelectionController {

    @FXML
    private ListView<AbstractBomb> bombListView;

    @FXML
    private ImageView bombImageView;

    @FXML
    private Label bombNameLabel;

    @FXML
    private Label bombDescriptionLabel;

    @FXML
    private Label bombDelayLabel;

    @FXML
    private Button validateButton;

    @FXML
    private Button cancelButton;

    private Stage stage;
    private Scene mainScene;
    private GameFacade gameFacade;

    public void initialize() {
        // Écouter les sélections dans la ListView
        bombListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateBombDetails(newValue);
            }
        });
    }

    /**
     * Met à jour l'affichage des détails de la bombe sélectionnée.
     *
     * @param bomb La bombe sélectionnée
     */
    private void updateBombDetails(AbstractBomb bomb) {
        bombNameLabel.setText(bomb.getName());
        bombDescriptionLabel.setText(bomb.getDescription());
        bombDelayLabel.setText("Délai: " + bomb.getDelay() + " secondes");

        // Charger l'image de la bombe
        try {
            String imageName = bomb.getName() + ".png";
            URL url = getClass().getResource("/fr/univartois/butinfo/ihm/images/" + imageName);
            if (url != null) {
                Image image = new Image(url.toExternalForm());
                bombImageView.setImage(image);
            }
        } catch (Exception e) {
            // Si l'image n'est pas trouvée, ne pas afficher d'image
            bombImageView.setImage(null);
        }
    }

    /**
     * Définit la Stage de l'application.
     *
     * @param stage La Stage principale
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Stocke la Scene principale de l'application.
     *
     * @param mainScene La Scene principale
     */
    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    /**
     * Stocke une instance de la façade.
     *
     * @param gameFacade La façade du jeu
     */
    public void setGameFacade(GameFacade gameFacade) {
        this.gameFacade = gameFacade;
    }

    /**
     * Associe la liste observable des bombes à la ListView.
     *
     * @param bombs La liste observable des bombes
     */
    public void setBombs(ObservableList<AbstractBomb> bombs) {
        bombListView.setItems(bombs);

        // Sélectionner automatiquement la première bombe si elle existe
        if (!bombs.isEmpty()) {
            bombListView.getSelectionModel().selectFirst();
        }
    }

    /**
     * Gère l'action du bouton Annuler.
     */
    @FXML
    private void handleCancel() {
        if (stage != null && mainScene != null) {
            stage.setScene(mainScene);
        }
    }

    /**
     * Gère l'action du bouton Valider.
     */
    @FXML
    private void handleValidate() {
        int selectedIndex = bombListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0 && gameFacade != null) {
            // Déposer la bombe sélectionnée
            gameFacade.dropBombByIndex(selectedIndex);
        }

        // Retourner à la scène principale
        if (stage != null && mainScene != null) {
            stage.setScene(mainScene);
        }
    }
}