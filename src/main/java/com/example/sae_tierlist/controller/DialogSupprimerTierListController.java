package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.TierList;
import com.example.sae_tierlist.model.TierListManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DialogSupprimerTierListController {

    @FXML
    private Button boutonAnnuler;

    @FXML
    private Button boutonSupprimer;

    private TierListManager manager;
    private Runnable onTierListSuppr;
    private TierList tl;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setOnTierListSuppr(Runnable callback) {
        this.onTierListSuppr = callback;
    }

    public void setTierList(TierList tl) {
        this.tl = tl;
    }

    @FXML
    private void onSupprimer() {
        manager.supprimerTierList(tl);
        if (onTierListSuppr != null) {
            onTierListSuppr.run();
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