package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.Tier;
import com.example.sae_tierlist.model.TierList;
import com.example.sae_tierlist.model.TierListManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DialogSupprimerTierController {

    @FXML
    private Button boutonAnnuler;

    @FXML
        private Button boutonSupprimer;

    private TierListManager manager;
    private Runnable onTierSuppr;
    private Tier tier;
    private TierList tierList;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setOnTierSuppr(Runnable callback) {
        this.onTierSuppr = callback;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public void setTierList(TierList tierList) {
        this.tierList = tierList;
    }

    @FXML
    private void onSupprimer() {
        tierList.getaClasser().addAll(tier.getListeTier());
        tierList.supprimerTier(tier);
        if (onTierSuppr != null) {
            onTierSuppr.run();
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
}