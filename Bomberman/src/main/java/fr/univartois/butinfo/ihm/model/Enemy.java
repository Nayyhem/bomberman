/**
 * Ce logiciel est distribué à des fins éducatives.
 *
 * Il est fourni "tel quel", sans garantie d'aucune sorte, explicite
 * ou implicite, notamment sans garantie de qualité marchande, d'adéquation
 * à un usage particulier et d'absence de contrefaçon.
 * En aucun cas, les auteurs ou titulaires du droit d'auteur ne seront
 * responsables de tout dommage, réclamation ou autre responsabilité, que ce
 * soit dans le cadre d'un contrat, d'un délit ou autre, en provenance de,
 * consécutif à ou en relation avec le logiciel ou son utilisation, ou avec
 * d'autres éléments du logiciel.
 *
 * (c) 2022-2025 Romain Wallon - Université d'Artois.
 * Tous droits réservés.
 */

package fr.univartois.butinfo.ihm.model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.Random;

/**
 * La classe Enemy représente un adversaire du joueur dans le jeu du Bomberman.
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class Enemy extends AbstractCharacter {

    /**
     * Le nom de ce personnage.
     */
    private final String name;

    private Timeline timeline;

    /**
     * Construit un nouvel Enemy.
     *
     * @param name Le nom du personnage.
     */
    public Enemy(String name) {
        super(1);
        this.name = name;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.butinfo.ihm.bomberman.model.AbstractCharacter#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    public void moveRandomly(GameFacade gameFacade) {
        Random random = new Random();
        int direction = random.nextInt(4);

        switch (direction) {
            case 0 -> gameFacade.moveUp(this);
            case 1 -> gameFacade.moveDown(this);
            case 2 -> gameFacade.moveLeft(this);
            case 3 -> gameFacade.moveRight(this);
        }
    }

    public void animate(GameFacade gameFacade) {
        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> moveRandomly(gameFacade))
        );
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.timeline.play();
    }

    /**
     * decHealth pour gérer la mort de l'ennemi.
     */
    @Override
    public void decHealth() {
        super.decHealth();
        if (getHealth() <= 0) {
            // L'ennemi est mort - arrêter sa timeline
            if (timeline != null) {
                timeline.stop();
            }
            System.out.println("L'ennemi " + getName() + " est mort !");
        }
    }
}
