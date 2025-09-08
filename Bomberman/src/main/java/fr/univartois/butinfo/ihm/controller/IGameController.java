package fr.univartois.butinfo.ihm.controller;

import fr.univartois.butinfo.ihm.model.AbstractCharacter;
import fr.univartois.butinfo.ihm.model.Player;
import fr.univartois.butinfo.ihm.model.Tile;

public interface IGameController {

    /**
     * Initialise ou met à jour l'affichage de l'ImageView correspondant à une tuile donnée.
     *
     * @param row La ligne de la tuile.
     * @param col La colonne de la tuile.
     * @param tile La tuile à afficher.
     */
    void updateTileView(int row, int col, Tile tile);

    /**
     * Lie un personnage à son affichage en créant une ImageView et en synchronisant
     * sa position avec celle du personnage.
     *
     * @param character Le personnage à lier à l'affichage.
     */
    void bindCharacterToView(AbstractCharacter character);

    void updateCharacterPosition(AbstractCharacter character);

    void bindBombCountToView(Player player);

    void bindHealthToView(Player player);

}
