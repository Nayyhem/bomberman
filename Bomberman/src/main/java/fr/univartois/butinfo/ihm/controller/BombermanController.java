package fr.univartois.butinfo.ihm.controller;

import fr.univartois.butinfo.ihm.model.GameFacade;
import fr.univartois.butinfo.ihm.model.AbstractCharacter;
import fr.univartois.butinfo.ihm.model.Tile;
import fr.univartois.butinfo.ihm.model.TileContent;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;
import fr.univartois.butinfo.ihm.model.AbstractBomb;
import fr.univartois.butinfo.ihm.model.Player;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.util.List;
import java.util.ArrayList;

public class BombermanController implements IGameController{

    @FXML
    private Button restartButton;

    @FXML
    private Label healthLabelLeft;

    @FXML
    private Label bombCountLabelLeft;

    @FXML
    private GridPane gridPane;

    private Scene scene;

    private static final int TILE_SIZE = 90;
    private static final int MAP_HEIGHT = 11;
    private static final int MAP_WIDTH = 13;

    private Map<AbstractBomb, ImageView> bombViews = new HashMap<>();
    private Stage stage;
    private List<AbstractBomb> playerBombs = new ArrayList<>();

    private GameFacade gameFacade;

    private Map<AbstractCharacter, ImageView> characterViews = new HashMap<>();

    public void initialize() {
        gameFacade = new GameFacade();
        gameFacade.setController(this);

        gridPane.setPrefSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        gridPane.setMaxSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        gridPane.setMinSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);

        fillGridPane();
        gameFacade.startGame();
        storeBombs();
    }

    private void fillGridPane() {
        for (int row = 0; row < getHAUTEUR(); row++) {
            for (int col = 0; col < getLARGEUR(); col++) {
                Tile tile = gameFacade.getGameMap().get(row, col);
                StackPane cell = createTileStackPane(tile);
                gridPane.add(cell, col, row);
            }
        }
    }

    private StackPane createTileStackPane(Tile tile) {
        StackPane stackPane = new StackPane();
        TileContent content = tile.getContent();

        // Image de base
        ImageView baseImageView = createImageView(getImageNameForContent(content));
        stackPane.getChildren().add(baseImageView);

        // Image supplémentaire pour les murs de briques
        if (content == TileContent.BRICK_WALL) {
            ImageView brickImageView = createImageView("bricks.png");
            brickImageView.setId("brick-wall");
            stackPane.getChildren().add(brickImageView);
        }

        // Écouter les changements de contenu de la tuile
        tile.contentProperty().addListener((observable, oldValue, newValue) -> {
            // Supprimer l'image des briques si elle existe
            stackPane.getChildren().removeIf(node ->
                    node instanceof ImageView && "brick-wall".equals(node.getId()));

            // Ajouter l'image des briques si le nouveau contenu est BRICK_WALL
            if (newValue == TileContent.BRICK_WALL) {
                ImageView brickImageView = createImageView("bricks.png");
                brickImageView.setId("brick-wall");
                stackPane.getChildren().add(brickImageView);
            }
        });

        // Écouter les explosions de la tuile
        tile.explodedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Ajouter une image d'explosion
                ImageView explosionImageView = createImageView("explosion.png");
                explosionImageView.setId("explosion");
                stackPane.getChildren().add(explosionImageView);
            } else {
                // Retirer l'image d'explosion
                stackPane.getChildren().removeIf(node ->
                        node instanceof ImageView && "explosion".equals(node.getId()));
            }
        });

        return stackPane;
    }

    private String getImageNameForContent(TileContent content) {
        return switch (content) {
            case LAWN -> "lawn.png";
            case SOLID_WALL -> "wall.png";
            case BRICK_WALL -> "lawn.png";
            default -> throw new IllegalArgumentException("Contenu de tuile non géré: " + content);
        };
    }

    private ImageView createImageView(String imageName) {
        ImageView imageView = new ImageView(loadImage(imageName));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);
        return imageView;
    }

    private Image loadImage(String imageName) {
        try {
            URL url = getClass().getResource("/fr/univartois/butinfo/ihm/images/" + imageName);
            if (url == null) {
                throw new NoSuchElementException("Image non trouvée: " + imageName);
            }
            return new Image(url.toExternalForm(), TILE_SIZE, TILE_SIZE, true, true);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException("Impossible de charger l'image: " + imageName, e);
        }
    }

    @Override
    public void updateTileView(int row, int col, Tile tile) {
        // Trouver et supprimer la tuile existante
        gridPane.getChildren().removeIf(node -> {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);

            // Vérifier que les indices ne sont pas null avant de les comparer
            return nodeRow != null && nodeCol != null &&
                    nodeRow == row && nodeCol == col &&
                    !characterViews.containsValue(node) &&
                    !bombViews.containsValue(node);
        });

        // Créer et ajouter la nouvelle tuile
        StackPane newTileStack = createTileStackPane(tile);
        gridPane.add(newTileStack, col, row);

        // Réappliquer les contraintes de positionnement
        GridPane.setRowIndex(newTileStack, Integer.valueOf(row));
        GridPane.setColumnIndex(newTileStack, Integer.valueOf(col));
    }

    @Override
    public void bindCharacterToView(AbstractCharacter character) {
        String imageName = character.getName() + ".png"; // ex: "guy.png" pour le joueur
        ImageView characterImageView = createImageView(imageName);

        characterViews.put(character, characterImageView);
        gridPane.add(characterImageView, character.getColumn(), character.getRow());

        // Écouter les changements de vie pour retirer l'affichage si mort
        character.healthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                gridPane.getChildren().remove(characterImageView);
                characterViews.remove(character);
            }
        });

        // Écouteur spécial pour le joueur
        if (character instanceof Player) {
            character.healthProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.intValue() <= 0) {
                    // Le joueur est mort - fin de partie
                    gameFacade.gameOver();
                    // message de Game Over
                    showGameOverMessage();
                }
            });
        }

        updateCharacterPosition(character);
    }

    /**
     * Met à jour la position d'un personnage dans l'affichage.
     * Cette méthode doit être appelée chaque fois que la position du personnage change.
     *
     * @param character Le personnage dont la position a changé.
     */
    public void updateCharacterPosition(AbstractCharacter character) {
        ImageView characterImageView = characterViews.get(character);
        if (characterImageView != null) {
            GridPane.setConstraints(characterImageView, character.getColumn(), character.getRow());
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public GameFacade getGameFacade() {
        return gameFacade;
    }

    public int getHAUTEUR() {
        return MAP_HEIGHT;
    }

    public int getLARGEUR() {
        return MAP_WIDTH;
    }

    public void setScene(Scene scene) {
        this.scene = scene;

        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> gameFacade.movePlayerUp();
                case DOWN -> gameFacade.movePlayerDown();
                case LEFT -> gameFacade.movePlayerLeft();
                case RIGHT -> gameFacade.movePlayerRight();
                case SPACE -> gameFacade.dropBomb();
                case I -> showInventory();
            }
        });
    }

    @Override
    public void bindBombCountToView(Player player) {
        bombCountLabelLeft.textProperty().bind(
                Bindings.concat("💣 ", player.bombCountProperty())
        );
    }

    /**
     * Affiche une bombe sur la carte à sa position.
     *
     * @param bomb La bombe à afficher.
     */
    public void displayBomb(AbstractBomb bomb) {
        String imageName = bomb.getName() + ".png"; // ex: "bomb.png"
        ImageView bombImageView = createImageView(imageName);

        bombViews.put(bomb, bombImageView);
        gridPane.add(bombImageView, bomb.getColumn(), bomb.getRow());

        // Écouter l'explosion de la bombe pour retirer l'affichage
        bomb.explodedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                gridPane.getChildren().remove(bombImageView);
                bombViews.remove(bomb);
            }
        });
    }

    @Override
    public void bindHealthToView(Player player) {
        
        healthLabelLeft.textProperty().bind(
                Bindings.concat("❤️ ", player.healthProperty())
        );
    }

    /**
     * Stocke la liste des bombes du joueur dans un attribut du contrôleur.
     */
    public void storeBombs() {
        if (gameFacade != null && gameFacade.getPlayer() != null) {
            playerBombs.clear();
            playerBombs.addAll(gameFacade.getPlayer().getBombs());
        }
    }

    // Méthode pour définir la Stage
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Méthode pour afficher l'inventaire (à appeler avec la touche 'I')
    public void showInventory() {
        try {
            // Charger la vue de sélection des bombes
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fr/univartois/butinfo/ihm/view/bomb-selection-view.fxml"));
            Parent inventoryView = fxmlLoader.load();
            BombSelectionController inventoryController = fxmlLoader.getController();

            // Configurer le contrôleur de l'inventaire
            inventoryController.setStage(stage);
            inventoryController.setMainScene(scene);
            inventoryController.setGameFacade(gameFacade);

            // Créer une liste observable des bombes du joueur
            javafx.collections.ObservableList<AbstractBomb> observableBombs =
                    javafx.collections.FXCollections.observableArrayList(gameFacade.getPlayer().getBombs());
            inventoryController.setBombs(observableBombs);

            // Créer et afficher la nouvelle scène
            Scene inventoryScene = new Scene(inventoryView);
            stage.setScene(inventoryScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGameOverMessage() {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Fin de partie");
            alert.setHeaderText("GAME OVER");
            alert.setContentText("Le joueur est mort ! La partie est terminée.");
            alert.showAndWait();
        });
    }

    public void showVictoryMessage() {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Victoire");
            alert.setHeaderText("VICTOIRE !");
            alert.setContentText("Félicitations ! Vous avez éliminé tous les ennemis !");
            alert.showAndWait();
        });
    }

    @FXML
    public void restartGame() {
        // Nettoyer les vues existantes
        characterViews.clear();
        bombViews.clear();

        // Vider la grille
        gridPane.getChildren().clear();

        // Recréer la façade de jeu
        gameFacade = new GameFacade();
        gameFacade.setController(this);

        // Refaire l'initialisation
        fillGridPane();
        gameFacade.startGame();
        storeBombs();

        if (scene != null) {
            scene.getRoot().requestFocus();
        }
    }

    /**
     * Effet hover pour le bouton recommencer
     */
    @FXML
    public void onRestartButtonHover() {
        restartButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #c0392b, #a93226); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;" +
                        "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;" +
                        "-fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"
        );
    }

    /**
     * Retour normal pour le bouton recommencer
     */
    @FXML
    public void onRestartButtonExit() {
        restartButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;" +
                        "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5;" +
                        "-fx-cursor: hand;"
        );
    }
}
