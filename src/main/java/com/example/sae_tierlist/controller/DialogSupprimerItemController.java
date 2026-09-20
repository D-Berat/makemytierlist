package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.Item;
import com.example.sae_tierlist.model.Tier;
import com.example.sae_tierlist.model.TierList;
import com.example.sae_tierlist.model.TierListManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

public class DialogSupprimerItemController {

    @FXML
    private Button boutonAnnuler;

    @FXML
    private Button boutonSupprimer;

    private TierListManager manager;
    private Runnable onItemSuppr;
    private TierList tierList;
    private Item Item_a_suppr;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setOnItemSuppr(Runnable callback) {
        this.onItemSuppr = callback;
    }

    public void setItem(Item item) {
        this.Item_a_suppr = item;
    }

    public void setTierList(TierList tierList) {
        this.tierList = tierList;
    }

    @FXML
    private void onSupprimer() {
        List<Item> conteneur = trouverConteneurDe(Item_a_suppr);
        if (conteneur != null) {
            conteneur.remove(Item_a_suppr);
        }

        if (onItemSuppr != null) {
            onItemSuppr.run();
        }
        fermer();
    }

    @FXML
    private void onAnnuler() {
        fermer();
    }

    private void fermer() {
        Stage stage = (Stage) boutonAnnuler.getScene().getWindow();
        stage.close();
    }

    private List<Item> trouverConteneurDe(Item item) {
        if (tierList.getaClasser().contains(item)) {
            return tierList.getaClasser();
        }
        for (Tier tier : tierList.getListeTierList()) {
            if (tier.getListeTier().contains(item)) {
                return tier.getListeTier();
            }
        }
        return null;
    }
}