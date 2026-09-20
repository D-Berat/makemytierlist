package com.example.sae_tierlist.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tier implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nom;
    private String couleur;
    private int hauteur;
    private List<Item> listeTier;

    public Tier(String nom, String couleur, int hauteur) {
        this.nom = nom;
        this.couleur = couleur;
        this.hauteur = hauteur;
        this.listeTier = new ArrayList<>();
    }

    public void ajouterItem(Item item) {
        listeTier.add(item);
    }

    public void retirerItem(Item item) {
        listeTier.remove(item);
    }


    public void deplacerItem(Item item, int indice_deplacement) {
        int idxItem = listeTier.indexOf(item);
        listeTier.remove(idxItem);
        listeTier.add(indice_deplacement, item);
    }

    public String getNom() {
        return nom;
    }

    public String getCouleur() {
        return couleur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public List<Item> getListeTier() {
        return listeTier;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
    }
}
