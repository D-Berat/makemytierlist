package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.Item;
import com.example.sae_tierlist.model.Tier;
import com.example.sae_tierlist.model.TierList;
import com.example.sae_tierlist.model.TierListManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.List;

public class DialogReinitialiserTierListController {

    @FXML
    private Button boutonAnnuler;

    @FXML
    private Button boutonSupprimer;

    private TierListManager manager;
    private Runnable onTierListReinitialiser;
    private TierList tierList;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setOnTierListReinitialiser(Runnable callback) {
        this.onTierListReinitialiser = callback;
    }

    public void setTierList(TierList tierList) {
        this.tierList = tierList;
    }

    @FXML
    private void onSupprimer() {
        tierList.reinitialiser();
        if (onTierListReinitialiser != null) {
            onTierListReinitialiser.run();
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