package com.example.sae_tierlist.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TierListManager  implements Serializable{

    private static final long serialVersionUID = 1L;
    private List<TierList> tierLists;

    public TierListManager() {
        this.tierLists = new ArrayList<>();
    }

    public void creerTierList(String nom){
        TierList t = new TierList(nom);
        t.ajouterUnTier(new Tier("S", "#F09595", 60));
        t.ajouterUnTier(new Tier("A", "#FAC775", 60));
        t.ajouterUnTier(new Tier("B", "#C0DD97", 60));
        t.ajouterUnTier(new Tier("C", "#85B7EB", 60));
        t.ajouterUnTier(new Tier("D", "#CECBF6", 60));
        tierLists.add(t);
    }

    public void ajouterTierList(TierList t){
        tierLists.add(t);
    }


    public void supprimerTierList(TierList t){
        tierLists.remove(t);
    }

    public void dupliquerTierList(TierList t) {
        try {
            //Sérialiser t vers un tableau d'octets en mémoire
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(t);
            oos.close();

            //Désérialiser depuis tableau (récup d'une copie indépendante)
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            TierList copie = (TierList) ois.readObject();
            ois.close();

            copie.setNom(t.getNom() + " (copie)");
            ajouterTierList(copie);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void recalculerCompteurItems() {
        int maxId = -1;
        for (TierList tl : tierLists) {
            for (Item item : tl.getaClasser()) {
                if (item.getId() > maxId) maxId = item.getId();
            }
            for (Tier t : tl.getListeTierList()) {
                for (Item item : t.getListeTier()) {
                    if (item.getId() > maxId) maxId = item.getId();
                }
            }
        }
        Item.initialiserCompteur(maxId + 1);
    }


    public List<TierList> getTierLists() {
        return tierLists;
    }

}
