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

/**
 * La classe RowBomb représente une bombe qui peut être déposée sur une tuile de la map
 * afin de faire exploser le contenu des tuiles voisines situées sur la même ligne.
 *
 * @author Romain Wallon
 *
 * @version 0.1.0
 */
public class RowBomb extends AbstractBomb {

    /**
     * Crée une nouvelle instance de RowBomb.
     *
     * @param game La façade gérant la partie en cours.
     */
    protected RowBomb(GameFacade game) {
        super(game);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.butinfo.ihm.bomberman.model.AbstractBomb#getName()
     */
    @Override
    public String getName() {
        return "row-bomb";
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.butinfo.ihm.bomberman.model.AbstractBomb#getDescription()
     */
    @Override
    public String getDescription() {
        return "Cette bombe fait exploser le contenu des tuiles voisines situées sur la même ligne.";
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.butinfo.ihm.bomberman.model.AbstractBomb#getDelay()
     */
    @Override
    public int getDelay() {
        return 2;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.univartois.butinfo.ihm.bomberman.model.AbstractBomb#explode()
     */
    @Override
    public void explode() {
        exploded.set(true);
        for (int i = -1; i <= 1; i++) {
            game.explode(row, column + i);
        }
    }

}
