package com.example.sae_tierlist.model;

import java.io.Serializable;

public abstract class Item implements Serializable {
    private static int compteur = 0;
    private static final long serialVersionUID = 1L;
    private int id;

    //constructeur
    public Item() {
        this.id = compteur++;
    }

    public abstract String getDisplayName();

    public int getId() {
        return id;
    }

    // Nouveau : permet de réajuster le compteur après chargement/import
    public static void initialiserCompteur(int valeur) {
        compteur = valeur;
    }

    public static int getCompteur() {
        return compteur;
    }
}