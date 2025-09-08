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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * La classe AbstractCharacter est la classe parente des différents personnages pouvant se
 * déplacer dans le jeu du Bomberman.
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public abstract class AbstractCharacter {

    /**
     * La ligne où se trouve ce personnage.
     */
    private int row;

    /**
     * La colonne où se trouve ce personnage.
     */
    private int column;

    /**
     * Les points de vie restants pour ce personnage.
     */
    private IntegerProperty health;

    /**
     * Crée une nouvelle instace de AbstractCharacter.
     *
     * @param initialHealth Les points de vie initiaux du personnage.
     */
    protected AbstractCharacter(int initialHealth) {
        this.health = new SimpleIntegerProperty(initialHealth);
    }

    /**
     * Donne le nom de ce personnage.
     *
     * @return Le nom de ce personnage.
     */
    public abstract String getName();

    /**
     * Donne la ligne où se trouve ce personnage.
     *
     * @return La ligne où se trouve ce personnage.
     */
    public int getRow() {
        return row;
    }

    /**
     * Donne la colonne où se trouve ce personnage.
     *
     * @return La colonne où se trouve ce personnage.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Modifie la position de ce personnage.
     *
     * @param row La ligne où se trouve maintenant ce personnage.
     * @param column La colonne où se trouve maintenant ce personnage.
     */
    public void setPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Donne les points de vie restants pour ce personnage.
     *
     * @return Les points de vie restants pour ce personnage.
     */
    public int getHealth() {
        return health.get();
    }

    /**
     * Donne la propriété des points de vie de ce personnage.
     *
     * @return La propriété des points de vie.
     */
    public IntegerProperty healthProperty() {
        return health;
    }

    /**
     * Augmente les points de vie de ce personnage.
     */
    public void incHealth() {
        health.set(health.get() + 1);
    }

    /**
     * Diminue les points de vie de ce personnage.
     */
    public void decHealth() {
        health.set(health.get() - 1);
    }

}
