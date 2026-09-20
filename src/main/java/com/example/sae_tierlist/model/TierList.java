package com.example.sae_tierlist.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class TierList implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nom;
    private List<Tier> listeTierList;
    private List<Item> aClasser; //la liste des items pas encore placé
    private int tailleItems = 60; // taille par défaut

    public TierList(String nom) {
        this.nom = nom;
        this.listeTierList = new ArrayList<>();
        this.aClasser = new ArrayList<>();
    }

    public void ajouterUnTier(Tier tier){
        listeTierList.add(tier);
    }

    public void supprimerTier(Tier tier){
        listeTierList.remove(tier);
    }

    public void deplacerTier(Tier tier,int indice_deplacement){
        int idxItem= listeTierList.indexOf(tier);
        listeTierList.remove(idxItem);
        listeTierList.add(indice_deplacement,tier);
    }

    public void reinitialiser(){
        for(int i=0;i<listeTierList.size();i++){
            for(Item item : listeTierList.get(i).getListeTier() ){
                aClasser.add(item);
            }
            listeTierList.get(i).getListeTier().clear();
        }
    }

    public void deplacerItem(Item item, List<Item> source, List<Item> destination){
        if(source.remove(item)){
            destination.add(item);
        }
    }

    public String getNom() {
        return nom;
    }

    public List<Tier> getListeTierList() {
        return listeTierList;
    }

    public List<Item> getaClasser() {
        return aClasser;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getTailleItems() { return tailleItems; }
    public void setTailleItems(int tailleItems) { this.tailleItems = tailleItems; }
}
