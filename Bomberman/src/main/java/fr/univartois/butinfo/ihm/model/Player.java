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

import javafx.beans.binding.IntegerBinding;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;

/**
 * la classe Player représente le personnage du joueur qui utilise l'application.
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class Player extends AbstractCharacter {

    /**
     * La liste des bombes disponibles pour le joueur.
     */
    private ObservableList<AbstractBomb> bombs;

    /**
     * Construit un nouveau Player.
     */
    public Player() {
        super(3);
        this.bombs = FXCollections.observableArrayList();
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.butinfo.ihm.bomberman.model.AbstractCharacter#getName()
     */
    @Override
    public String getName() {
        return "guy";
    }

    /**
     * Donne la liste des bombes du joueur.
     *
     * @return La liste observable des bombes.
     */
    public ObservableList<AbstractBomb> getBombs() {
        return bombs;
    }

    /**
     * Retire une bombe de l'inventaire du joueur.
     *
     * @param index L'indice de la bombe à retirer.
     */
    public void removeBomb(int index) {
        if (index >= 0 && index < bombs.size()) {
            bombs.remove(index);
        }
    }

    /**
     * Donne la propriété correspondant au nombre de bombes du joueur.
     *
     * @return La propriété du nombre de bombes.
     */
    public IntegerBinding bombCountProperty() {
        return Bindings.size(bombs);
    }

    /**
     * decHealth pour gérer la mort du joueur.
     */
    @Override
    public void decHealth() {
        super.decHealth();
        if (getHealth() <= 0) {
            System.out.println("Le joueur est mort !");
        }
    }

}
