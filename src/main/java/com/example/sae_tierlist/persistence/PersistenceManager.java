package com.example.sae_tierlist.persistence;

import com.example.sae_tierlist.model.TierList;
import com.example.sae_tierlist.model.TierListManager;

import java.io.*;

public class PersistenceManager {

    public void sauvegarder(TierListManager manager, String chemin) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chemin))) {
            oos.writeObject(manager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TierListManager charger(String chemin) {
        File fichier = new File(chemin);
        if (!fichier.exists()) {
            // premier lancement, comportement normal
            return new TierListManager();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            return (TierListManager) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // le fichier existe mais on n'arrive pas à le lire (corrompu)
            e.printStackTrace();
            return new TierListManager();
        }
    }

    public void exporterTierList(TierList tl, String chemin) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chemin))) {
            oos.writeObject(tl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TierList importerTierList(String chemin) {
        File fichier = new File(chemin);
        if (!fichier.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            return (TierList) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}