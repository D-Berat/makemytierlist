package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.TierListManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DialogNouvelleTierListController {

    @FXML
    private TextField champNom;

    @FXML
    private Button boutonAnnuler;

    @FXML
    private Button boutonValider;

    private TierListManager manager;
    private Runnable onTierListCreee;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setOnTierListCreee(Runnable callback) {
        this.onTierListCreee = callback;
    }

    @FXML
    private void onValider() {
        String nom = champNom.getText().trim();
        if (!nom.isBlank()) {
            manager.creerTierList(nom);
            if (onTierListCreee != null) {
                onTierListCreee.run();
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