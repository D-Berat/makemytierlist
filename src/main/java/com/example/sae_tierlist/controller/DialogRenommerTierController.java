package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.Item;
import com.example.sae_tierlist.model.TextItem;
import com.example.sae_tierlist.model.Tier;
import com.example.sae_tierlist.model.TierListManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DialogRenommerTierController {

    @FXML
    private TextField champNom;

    @FXML
    private Button boutonAnnuler;

    @FXML
    private Button boutonValider;

    private TierListManager manager;
    private Runnable onTierRenommer;
    private Tier Tier_a_renommer;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setOnTierRenommer(Runnable callback) {
        this.onTierRenommer = callback;
    }

    public void setTier(Tier tier) {
        this.Tier_a_renommer = tier;
    }

    @FXML
    private void onValider() {

        if(!champNom.getText().trim().isBlank()) {
            Tier_a_renommer.setNom(champNom.getText().trim());

            if (onTierRenommer != null) {
                onTierRenommer.run();
            }
            fermer();
        }

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