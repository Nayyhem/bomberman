package fr.univartois.butinfo.ihm.model;

import fr.univartois.butinfo.ihm.controller.BombermanController;
import fr.univartois.butinfo.ihm.controller.IGameController;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.*;


public class GameFacade {

    private final GameMap gameMap;
    private IGameController controller;
    private Player player;
    private boolean gameEnded = false;

    private final List<AbstractBomb> activeBombs = new ArrayList<>();

    private final List<Enemy> enemies = new ArrayList<>();
    private static final int ENEMY_COUNT = 3;

    public GameFacade() {
        this.gameMap = GameMapFactory.createMapWithRandomBrickWalls(11, 13, (int) (11 * 13 * 0.3));
    }

    /**
     * Démarre une nouvelle partie en créant et plaçant le joueur sur la carte.
     */
    public void startGame() {
        player = new Player();

        // Remplir l'inventaire avec 20 bombes de types différents
        for (int i = 0; i < 8; i++) {
            player.getBombs().add(new Bomb(this));
        }
        for (int i = 0; i < 4; i++) {
            player.getBombs().add(new RowBomb(this));
        }
        for (int i = 0; i < 4; i++) {
            player.getBombs().add(new ColumnBomb(this));
        }
        for (int i = 0; i < 4; i++) {
            player.getBombs().add(new LargeBomb(this));
        }

        placeCharacter(player);

        if (controller != null) {
            controller.bindCharacterToView(player);
            controller.bindBombCountToView(player);
            controller.bindHealthToView(player);
        }
        enemies.clear();

        String[] enemyNames = { "goblin", "rourke", "minotaur" };

        for (int i = 0; i < ENEMY_COUNT && i < enemyNames.length; i++) {
            Enemy enemy = new Enemy(enemyNames[i]);
            placeCharacter(enemy);

            enemies.add(enemy);

            if (controller != null) {
                controller.bindCharacterToView(enemy);
            }
            enemy.animate(this);
        }
    }

    /**
     * Définit le contrôleur associé à cette façade.
     *
     * @param controller Le contrôleur du jeu.
     */
    public void setController(IGameController controller) {
        this.controller = controller;
    }

    /**
     * Retourne le joueur actuel.
     *
     * @return Le joueur actuel.
     */
    public Player getPlayer() {
        return player;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * Place un personnage aléatoirement sur une tuile vide de la carte du jeu.
     *
     * @param character Le personnage à placer sur la carte.
     * @throws IllegalStateException Si aucune tuile vide n'est disponible sur la carte.
     */
    public void placeCharacter(AbstractCharacter character) {
        int row, col;

        if (character instanceof Player) {
            // Placer le joueur dans le coin supérieur gauche avec un espace libre
            row = 1;
            col = 1;
        } else if (character instanceof Enemy) {
            // Placer les ennemis dans différents coins avec des espaces libres
            String enemyName = character.getName();
            switch (enemyName) {
                case "goblin":
                    // Coin supérieur droit
                    row = 1;
                    col = gameMap.getWidth() - 2;
                    break;
                case "rourke":
                    // Coin inférieur gauche
                    row = gameMap.getHeight() - 2;
                    col = 1;
                    break;
                case "minotaur":
                    // Coin inférieur droit
                    row = gameMap.getHeight() - 2;
                    col = gameMap.getWidth() - 2;
                    break;
                default:
                    // Fallback au centre
                    row = gameMap.getHeight() / 2;
                    col = gameMap.getWidth() / 2;
                    break;
            }
        } else {
            // Placement par défaut pour d'autres types de personnages
            row = gameMap.getHeight() / 2;
            col = gameMap.getWidth() / 2;
        }

        // S'assurer que la position est valide et libre
        if (gameMap.isOnMap(row, col)) {
            Tile targetTile = gameMap.get(row, col);
            // Forcer la tuile à être praticable
            targetTile.setContent(TileContent.LAWN);

            // Créer une zone de sécurité autour du personnage
            createSafeZone(row, col);

            character.setPosition(row, col);
        } else {
            // Si la position calculée n'est pas valide, utiliser l'ancienne méthode
            placeCharacterRandomly(character);
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void moveUp(AbstractCharacter character) {
        tryMove(character, character.getRow() - 1, character.getColumn());
    }

    public void moveDown(AbstractCharacter character) {
        tryMove(character, character.getRow() + 1, character.getColumn());
    }

    public void moveLeft(AbstractCharacter character) {
        tryMove(character, character.getRow(), character.getColumn() -1);
    }

    public void moveRight(AbstractCharacter character) {
        tryMove(character, character.getRow(), character.getColumn() +1);
    }

    public void tryMove(AbstractCharacter character, int newRow, int newCol) {
        if (newRow >= 0 && newRow < getGameMap().getHeight() &&
                newCol >= 0 && newCol < getGameMap().getWidth()) {
            Tile targetTile = gameMap.get(newRow, newCol);

            // Vérifier si la tuile est praticable
            if (targetTile.getContent() == TileContent.LAWN) {
                // Vérifier s'il n'y a pas d'autres personnages sur cette tuile
                if (!isTileOccupiedByCharacter(newRow, newCol, character)) {
                    // Vérifier s'il n'y a pas de bombe sur cette tuile
                    if (!isTileOccupiedByBomb(newRow, newCol)) {
                        character.setPosition(newRow, newCol);
                        if (controller != null) {
                            controller.updateCharacterPosition(character);
                        }
                    }
                }
            }
        }
    }

    public void movePlayerUp() {
        moveUp(player);
    }

    public void movePlayerDown() {
        moveDown(player);
    }

    public void movePlayerLeft() {
        moveLeft(player);
    }

    public void movePlayerRight() {
        moveRight(player);
    }

    /**
     * Fait déposer une bombe par le joueur à sa position actuelle.
     */
    public void dropBomb() {
        // Utiliser la nouvelle méthode avec l'indice 0 (première bombe)
        dropBombByIndex(0);
    }

    /**
     * Fait exploser une tuile à la position donnée si elle est sur la carte.
     *
     * @param row La ligne de la tuile à exploser.
     * @param column La colonne de la tuile à exploser.
     */
    public void explode(int row, int column) {
        if (gameMap.isOnMap(row, column)) {
            gameMap.get(row, column).explode();
        }
        // Vérifier si le joueur est sur cette tuile
        if (player != null && player.getRow() == row && player.getColumn() == column) {
            player.decHealth();
        }

        // Vérifier si un ennemi est sur cette tuile
        Iterator<Enemy> enemyIterator = enemies.iterator();
        boolean enemyKilled = false;
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.getRow() == row && enemy.getColumn() == column) {
                enemy.decHealth();
                // Supprimer l'ennemi de la liste s'il est mort
                if (enemy.getHealth() <= 0) {
                    enemyIterator.remove();
                    enemyKilled = true;
                }
            }
        }

        // Vérifier la victoire seulement si un ennemi a été tué
        if (enemyKilled && enemies.isEmpty()) {
            victory();
        }
    }

    /**
     * Gère la fin de partie lorsque le joueur meurt.
     */
    public void gameOver() {
        if (!gameEnded) {
            gameEnded = true;
            // Arrêter tous les ennemis
            for (Enemy enemy : enemies) {
                if (enemy.getHealth() > 0) {
                    enemy.decHealth();
                }
            }
        }
    }


    /**
     * Fait déposer une bombe par le joueur à sa position actuelle en utilisant l'indice donné.
     *
     * @param bombIndex L'indice de la bombe à déposer dans l'inventaire du joueur
     */
    public void dropBombByIndex(int bombIndex) {
        if (player != null && bombIndex >= 0 && bombIndex < player.getBombs().size()) {
            // Vérifier s'il n'y a pas déjà une bombe à la position du joueur
            if (isTileOccupiedByBomb(player.getRow(), player.getColumn())) {
                return; // Ne pas poser de bombe s'il y en a déjà une
            }

            // Récupérer la bombe à l'indice spécifié
            AbstractBomb bomb = player.getBombs().get(bombIndex);

            // Placer la bombe à la position du joueur
            bomb.setPosition(player.getRow(), player.getColumn());

            // Ajouter la bombe à la liste des bombes actives
            activeBombs.add(bomb);

            // Retirer la bombe de l'inventaire
            player.removeBomb(bombIndex);

            // Afficher la bombe si le contrôleur est disponible
            if (controller != null && controller instanceof BombermanController) {
                ((BombermanController) controller).displayBomb(bomb);
            }

            // Déclencher l'explosion après le délai
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(bomb.getDelay()),
                    e -> {
                        bomb.explode();
                        // Retirer la bombe de la liste des bombes actives
                        activeBombs.remove(bomb);
                    }
            ));
            timeline.play();
        }
    }

    // Vérifie si tous les ennemis sont morts
    public boolean areAllEnemiesDead() {
        for (Enemy enemy : enemies) {
            if (enemy.getHealth() > 0) {
                return false;
            }
        }
        return !enemies.isEmpty(); // Retourne true seulement s'il y a des ennemis et qu'ils sont tous morts
    }

    public void victory() {
        if (!gameEnded) {
            gameEnded = true;

            // Afficher le message de victoire via le contrôleur
            if (controller != null && controller instanceof BombermanController) {
                ((BombermanController) controller).showVictoryMessage();
            }
        }
    }

    // Vérifier si une tuile est occupée par un personnage (joueur ou ennemi)
    private boolean isTileOccupiedByCharacter(int row, int col, AbstractCharacter excludeCharacter) {
        // Vérifier le joueur (sauf s'il s'agit du personnage à exclure)
        if (player != null && player != excludeCharacter &&
                player.getRow() == row && player.getColumn() == col) {
            return true;
        }

        // Vérifier les ennemis (sauf s'il s'agit du personnage à exclure)
        for (Enemy enemy : enemies) {
            if (enemy != excludeCharacter && enemy.getRow() == row && enemy.getColumn() == col) {
                return true;
            }
        }

        return false;
    }

    // Vérifier si une tuile est occupée par une bombe
    private boolean isTileOccupiedByBomb(int row, int col) {
        for (AbstractBomb bomb : activeBombs) {
            if (bomb.getRow() == row && bomb.getColumn() == col) {
                return true;
            }
        }
        return false;
    }

    private void createSafeZone(int centerRow, int centerCol) {
        // Créer une zone 3x3 autour du personnage
        for (int row = centerRow - 1; row <= centerRow + 1; row++) {
            for (int col = centerCol - 1; col <= centerCol + 1; col++) {
                if (gameMap.isOnMap(row, col)) {
                    Tile tile = gameMap.get(row, col);
                    // Ne pas toucher aux murs solides
                    if (tile.getContent() != TileContent.SOLID_WALL) {
                        tile.setContent(TileContent.LAWN);
                    }
                }
            }
        }
    }

    // Méthode pour placer un personnage aléatoirement sur une tuile vide
    private void placeCharacterRandomly(AbstractCharacter character) {
        List<Tile> emptyTiles = gameMap.getEmptyTiles();

        if (emptyTiles.isEmpty()) {
            throw new IllegalStateException("Aucune tuile vide disponible pour placer le personnage");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(emptyTiles.size());
        Tile selectedTile = emptyTiles.get(randomIndex);

        character.setPosition(selectedTile.getRow(), selectedTile.getColumn());
    }

}