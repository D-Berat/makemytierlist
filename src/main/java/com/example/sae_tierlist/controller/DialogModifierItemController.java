package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.Item;
import com.example.sae_tierlist.model.TextItem;
import com.example.sae_tierlist.model.TierListManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class DialogModifierItemController {

    @FXML
    private TextField champNom;

    @FXML
    private Button boutonAnnuler;

    @FXML
    private Button boutonValider;

    private TierListManager manager;
    private Runnable onItemEdit;
    private Item Item_a_mofifier;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setOnItemEdit(Runnable callback) {
        this.onItemEdit = callback;
    }
    public void setItem(Item item) {
        this.Item_a_mofifier = item;
    }

    @FXML
    private void onValider() {

        TextItem textItem = (TextItem) Item_a_mofifier;
        if(!champNom.getText().trim().isBlank()) {
            textItem.setTexte(champNom.getText().trim());

            if (onItemEdit != null) {
                onItemEdit.run();
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