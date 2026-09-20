package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.Tier;
import com.example.sae_tierlist.model.TierList;
import com.example.sae_tierlist.model.TierListManager;
import com.example.sae_tierlist.persistence.PersistenceManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class AccueilController {

    private TierListManager manager;

    @FXML
    private Button boutonNouvelleListe;

    @FXML
    private FlowPane containerTierLists;

    public void setManager(TierListManager manager) {
        this.manager = manager;
        rafraichirAffichage();
    }

    private Node creerCarte(TierList tl) {

        VBox carte = new VBox();
        carte.setSpacing(8);
        carte.setPrefWidth(280);
        carte.setPrefHeight(180);
        carte.getStyleClass().add("carte");

        // Aperçu : mini affichage des vrais tiers de la tier-list
        VBox apercu = new VBox();
        apercu.setSpacing(2);

        int nbTiersAffiches = Math.min(5, tl.getListeTierList().size());
        for (int i = 0; i < nbTiersAffiches; i++) {
            Tier tier = tl.getListeTierList().get(i);
            HBox ligne = new HBox();
            ligne.setSpacing(2);
            ligne.setPrefHeight(14);

            // Mini label coloré du tier (S, A, B...) comme le vrai design
            Label miniLabel = new Label(tier.getNom());
            miniLabel.setPrefWidth(20);
            miniLabel.setPrefHeight(14);
            miniLabel.setAlignment(Pos.CENTER);
            miniLabel.setStyle(
                    "-fx-background-color: " + tier.getCouleur() + ";" +
                            "-fx-font-size: 9px;" +
                            "-fx-font-weight: bold;"
            );
            ligne.getChildren().add(miniLabel);

            // Mini représentation des items (carrés gris, max 8)
            int nbItems = Math.min(8, tier.getListeTier().size());
            for (int j = 0; j < nbItems; j++) {
                HBox carre = new HBox();
                carre.setPrefWidth(14);
                carre.setPrefHeight(14);
                carre.setStyle("-fx-background-color: #d4d4d4; -fx-background-radius: 2;");
                ligne.getChildren().add(carre);
            }

            apercu.getChildren().add(ligne);
        }

        // Nom de la tier-list
        TextField nomEditable = new TextField(tl.getNom());
        nomEditable.getStyleClass().add("carte-titre-editable");

        // Quand on appuie sur Entrée
        nomEditable.setOnAction(event -> {
            String nouveauNom = nomEditable.getText().trim();
            if (!nouveauNom.isBlank()) {
                tl.setNom(nouveauNom);
            } else {
                nomEditable.setText(tl.getNom()); // restaure si vide
            }
            nomEditable.getParent().requestFocus(); // perd le focus
        });

        // Quand on clique ailleurs (focus perdu)
        nomEditable.focusedProperty().addListener((obs, ancien, nouveau) -> {
            if (!nouveau) { // perd le focus
                String nouveauNom = nomEditable.getText().trim();
                if (!nouveauNom.isBlank()) {
                    tl.setNom(nouveauNom);
                } else {
                    nomEditable.setText(tl.getNom());
                }
            }
        });

        // Infos : nombre d'items et de tiers
        int totalItems = tl.getaClasser().size();
        for (Tier t : tl.getListeTierList()) {
            totalItems += t.getListeTier().size();
        }
        Label infos = new Label(totalItems + " items · " + tl.getListeTierList().size() + " tiers");
        infos.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

        // Boutons d'action (icônes)
        HBox actions = new HBox();
        actions.setSpacing(4);


        Button dupliquer = new Button("⧉");
        dupliquer.getStyleClass().add("bouton-action");
        dupliquer.setOnMouseClicked(event -> onDupliquerTierList(tl));

        Button supprimer = new Button("🗑");
        supprimer.getStyleClass().add("bouton-action");
        supprimer.setOnMouseClicked(event -> onSupprimerTierList(tl));

        Button exporter = new Button("⤓");
        exporter.getStyleClass().add("bouton-action");
        exporter.setOnMouseClicked(event -> onExporterTierList(tl));

        actions.getChildren().addAll(dupliquer, supprimer, exporter);


        carte.getChildren().addAll(apercu, nomEditable, infos, actions);

        carte.setOnMouseClicked(event -> {
            if (event.getTarget() instanceof Button || event.getTarget() instanceof TextField) {
                return;
            }
            onOuvrirEditeur(tl);
        });
        carte.setStyle(carte.getStyle() + "-fx-cursor: hand;");

        return carte;
    }

    private Node creerCarteCreation() {
        VBox carte = new VBox();
        carte.setAlignment(Pos.CENTER);
        carte.setPrefWidth(280);
        carte.setPrefHeight(160);

        carte.getStyleClass().add("carte-creation");
        Label plus = new Label("+");
        plus.setStyle("-fx-font-size: 30; -fx-text-fill: gray;");
        Label texte = new Label("Créer une tier-list");
        texte.setStyle("-fx-text-fill: gray;");

        carte.getChildren().addAll(plus, texte);
        carte.setOnMouseClicked(event -> onCreerTierList());
        return carte;
    }

    private void onOuvrirEditeur(TierList tl) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/editeurTierList.fxml")
            );
            Parent root = loader.load();

            TierListController controller = loader.getController();
            controller.setManager(manager);
            controller.setTierList(tl);

            Scene scene = new Scene(root, 1100, 750);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());

            Stage stage = (Stage) boutonNouvelleListe.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void rafraichirAffichage() {
        containerTierLists.getChildren().clear();
        for (TierList tl : manager.getTierLists()) {
            containerTierLists.getChildren().add(creerCarte(tl));
        }
        containerTierLists.getChildren().add(creerCarteCreation());
    }

    @FXML
    private void onCreerTierList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogNouvelleTierList.fxml")
            );
            Parent root = loader.load();

            DialogNouvelleTierListController controller = loader.getController();
            controller.setManager(manager);
            controller.setOnTierListCreee(() -> this.rafraichirAffichage());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nouvelle tier-list");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSupprimerTierList(TierList tl) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogSupprimerTierList.fxml")
            );
            Parent root = loader.load();

            DialogSupprimerTierListController controller = loader.getController();
            controller.setManager(manager);
            controller.setTierList(tl);
            controller.setOnTierListSuppr(() -> this.rafraichirAffichage());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Supprimer TierList");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onExporterTierList(TierList tl) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter la tier-list");
        chooser.setInitialFileName(tl.getNom() + ".tl");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tier-list (*.tl)", "*.tl")
        );
        Stage stage = (Stage) boutonNouvelleListe.getScene().getWindow();
        File fichier = chooser.showSaveDialog(stage);
        if (fichier != null) {
            PersistenceManager persistence = new PersistenceManager();
            persistence.exporterTierList(tl, fichier.getAbsolutePath());
        }
    }

    @FXML
    private void onImporterTierList() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importer une tier-list");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tier-list (*.tl)", "*.tl")
        );
        Stage stage = (Stage) boutonNouvelleListe.getScene().getWindow();
        File fichier = chooser.showOpenDialog(stage);
        if (fichier != null) {
            PersistenceManager persistence = new PersistenceManager();
            manager.recalculerCompteurItems();
            TierList importee = persistence.importerTierList(fichier.getAbsolutePath());
            if (importee != null) {
                manager.ajouterTierList(importee);
                rafraichirAffichage();
            }
        }
    }


    @FXML
    private void onDupliquerTierList(TierList tl) {
        manager.dupliquerTierList(tl);
        manager.recalculerCompteurItems();
        rafraichirAffichage();
    }

    @FXML
    private void initialize() {
        // sera exécutée juste après le chargement du FXML

    }
}