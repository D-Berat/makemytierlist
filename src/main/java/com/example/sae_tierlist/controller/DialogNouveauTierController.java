package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.Tier;
import com.example.sae_tierlist.model.TierList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class DialogNouveauTierController {



    @FXML
    private TextField champNom;

    @FXML
    private HBox paletteCouleurs;

    @FXML
    private Button boutonAnnuler;

    @FXML
    private Button boutonValider;

    private TierList tierList;
    private Runnable onTierAjoute; // callback à appeler quand un tier est ajouté
    private String couleurChoisie = "#F09595"; // couleur par défaut

    @FXML
    private void initialize() {
        // Toutes les couleurs disponible
        String[] couleurs = {"#F09595", "#FAC775", "#C0DD97", "#85B7EB", "#CECBF6", "#F4C0D1", "#888888"};
        for (String couleur : couleurs) {
            HBox carre = new HBox();
            carre.setPrefSize(30, 30);
            carre.setStyle("-fx-background-color: " + couleur + "; -fx-background-radius: 6; -fx-cursor: hand; -fx-border-color: transparent; -fx-border-width: 2;");
            carre.setOnMouseClicked(e -> selectionnerCouleur(carre, couleur));
            paletteCouleurs.getChildren().add(carre);
        }
        // Sélectionner la première couleur par défaut
        selectionnerCouleur((HBox) paletteCouleurs.getChildren().get(0), couleurs[0]);
    }

    private void selectionnerCouleur(HBox carre, String couleur) {
        // Désélectionner tous les autres
        for (Node node : paletteCouleurs.getChildren()) {
            String style = node.getStyle();
            node.setStyle(style.replaceAll("-fx-border-color: [^;]+;", "-fx-border-color: transparent;"));
        }
        // Sélectionner celui-ci
        carre.setStyle(carre.getStyle().replaceAll("-fx-border-color: [^;]+;", "-fx-border-color: #1f1f1f;"));
        couleurChoisie = couleur;
    }

    public void setTierList(TierList tl) {
        this.tierList = tl;
    }

    public void setOnTierAjoute(Runnable callback) {
        this.onTierAjoute = callback;
    }

    @FXML
    private void onValider() {
        String nom = champNom.getText().trim();
        if (!nom.isBlank()) {
            Tier nouveauTier = new Tier(nom, couleurChoisie, 60);
            tierList.ajouterUnTier(nouveauTier);
            if (onTierAjoute != null) {
                onTierAjoute.run(); // appeler le callback pour rafraîchir
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