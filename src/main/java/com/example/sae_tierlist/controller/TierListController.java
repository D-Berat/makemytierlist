package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class TierListController {


    private TierListManager manager;
    private TierList tierList;

    @FXML
    private Label labelNomTierList;

    @FXML
    private VBox containerTiers;

    @FXML
    private FlowPane containerAClasser;

    @FXML
    private Button boutonRetour;

    @FXML
    private Button boutonAjouterTier;

    public void setManager(TierListManager manager) {
        this.manager = manager;
    }

    public void setTierList(TierList tl) {
        this.tierList = tl;
        labelNomTierList.setText(tl.getNom());
        rafraichirAffichage();
    }

    private void rafraichirAffichage() {
        containerTiers.getChildren().clear();
        containerAClasser.getChildren().clear();
        rendreReceveur(containerAClasser, tierList.getaClasser());


        // Afficher tous les tiers
        for (Tier tier : tierList.getListeTierList()) {
            containerTiers.getChildren().add(creerLigneTier(tier));
        }

        // Afficher les items à classer
        for (Item item : tierList.getaClasser()) {
            containerAClasser.getChildren().add(creerVueItem(item, Double.MAX_VALUE));
        }
    }

    @FXML
    private void onAjouterTier() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogNouveauTier.fxml")
            );
            Parent root = loader.load();

            DialogNouveauTierController controller = loader.getController();
            controller.setTierList(tierList);
            controller.setOnTierAjoute(this::rafraichirAffichage);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nouveau tier");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/accueil.fxml")
            );
            Parent root = loader.load();

            AccueilController controller = loader.getController();
            controller.setManager(manager);

            Scene nouvelleScene = new Scene(root, 1100, 750);
            nouvelleScene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());

            Stage stage = (Stage) boutonRetour.getScene().getWindow();
            stage.setScene(nouvelleScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node creerLigneTier(Tier tier) {
        // La hauteur effective = max entre la hauteur définie du tier et la taille des items + marge
        int hauteurEffective = Math.max(tier.getHauteur(), tierList.getTailleItems() + 10);

        HBox ligne = new HBox();
        ligne.setPrefHeight(hauteurEffective);
        ligne.getStyleClass().add("ligne-tier");

        // Label coloré à gauche (S, A, B...)
        Label labelTier = new Label(tier.getNom());
        labelTier.setPrefWidth(70);
        labelTier.setWrapText(true);
        labelTier.setPrefHeight(hauteurEffective);
        labelTier.getStyleClass().add("label-tier");
        labelTier.setStyle("-fx-background-color: " + tier.getCouleur() + ";");
        labelTier.setAlignment(Pos.CENTER);

        // Menu contextuel sur le label du tier
        ContextMenu menuTier = new ContextMenu();
        MenuItem renommer = new MenuItem("Renommer");
        renommer.setOnAction(event -> onRenommerTier(tier));
        MenuItem personnaliser = new MenuItem("Personnaliser");
        personnaliser.setOnAction(event -> onPersonnaliserTier(tier));
        menuTier.getItems().addAll(renommer, personnaliser);
        labelTier.setOnContextMenuRequested(event ->
                menuTier.show(labelTier.getScene().getWindow(), event.getScreenX(), event.getScreenY())
        );

        // rendre le label du tier draggable (pour réordonner les tiers)
        labelTier.setOnDragDetected(event -> {
            Dragboard db = labelTier.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("TIER:" + tier.getNom());
            db.setContent(content);
            ligne.setOpacity(0.5);
            event.consume();
        });

        labelTier.setOnDragDone(event -> {
            ligne.setOpacity(1.0);
            event.consume();
        });

        // Rendre la ligne réceptrice du drop d'un autre tier
        ligne.setOnDragOver(event -> {
            if (event.getDragboard().hasString() && event.getDragboard().getString().startsWith("TIER:")) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        ligne.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean succes = false;
            if (db.hasString() && db.getString().startsWith("TIER:")) {
                String nomTierDeplace = db.getString().substring(5);
                Tier tierDeplace = trouverTierParNom(nomTierDeplace);
                if (tierDeplace != null && tierDeplace != tier) {
                    int indexCible = tierList.getListeTierList().indexOf(tier);
                    tierList.deplacerTier(tierDeplace, indexCible);
                    succes = true;
                    rafraichirAffichage();
                }
            }
            event.setDropCompleted(succes);
            event.consume();
        });

        // Zone des items à droite
        HBox zoneItems = new HBox();
        zoneItems.setSpacing(5);
        zoneItems.setPrefHeight(hauteurEffective);
        HBox.setHgrow(zoneItems, Priority.ALWAYS);
        zoneItems.getStyleClass().add("zone-items");

        for (Item item : tier.getListeTier()) {
            zoneItems.getChildren().add(creerVueItem(item, hauteurEffective - 10));
        }

        rendreReceveur(zoneItems, tier.getListeTier());

        Button boutonSupprimerTier = new Button("🗑");
        boutonSupprimerTier.getStyleClass().add("bouton-action");
        boutonSupprimerTier.setOnAction(event -> onSupprimerTier(tier));

        ligne.getChildren().addAll(labelTier, zoneItems, boutonSupprimerTier);
        return ligne;
    }

    // Methode utilitaire pour trouver un tier par son nom
    private Tier trouverTierParNom(String nom) {
        for (Tier t : tierList.getListeTierList()) {
            if (t.getNom().equals(nom)) {
                return t;
            }
        }
        return null;
    }

    @FXML
    private void onAjouterItem() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogNouvelItem.fxml")
            );
            Parent root = loader.load();

            DialogNouvelItemController controller = loader.getController();
            controller.setTierList(tierList);
            controller.setOnItemAjoute(this::rafraichirAffichage);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Nouvel item");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node creerVueItem(Item item, double hauteurMax) {
        int taille = tierList.getTailleItems();
        if (taille <= 0) taille = 60;
        double tailleFinale = Math.min(taille, hauteurMax);

        // La boîte qui contiendra l'item (taille fixe, uniforme)
        StackPane boite = new StackPane();
        boite.setPrefSize(tailleFinale, tailleFinale);
        boite.setMinSize(tailleFinale, tailleFinale);
        boite.setMaxSize(tailleFinale, tailleFinale);
        boite.getStyleClass().add("item-boite");

        if (item instanceof ImageItem) {
            ImageItem imgItem = (ImageItem) item;
            try {
                String chemin = imgItem.getCheminItem();
                String urlImage;
                if (chemin.startsWith("http://") || chemin.startsWith("https://")) {
                    urlImage = chemin;
                } else {
                    urlImage = new File(chemin).toURI().toString();
                }
                Image image = new Image(urlImage, true);
                ImageView iv = new ImageView(image);
                iv.setFitHeight(tailleFinale);
                iv.setFitWidth(tailleFinale);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                boite.getChildren().add(iv);
            } catch (Exception e) {
                Label fallback = new Label(item.getDisplayName());
                fallback.getStyleClass().add("item-text");
                boite.getChildren().add(fallback);
            }
        } else {
            Label label = new Label(item.getDisplayName());
            label.getStyleClass().add("item-text");
            label.setAlignment(Pos.CENTER);
            label.setWrapText(true); // pour que les textes longs reviennent à la ligne
            boite.getChildren().add(label);
        }

        // Rendre la BOÎTE draggable (pas le contenu)
        rendreDraggable(boite, item);

        // Menu contextuel (clic droit)
        ContextMenu menuContextuel = new ContextMenu();

        MenuItem modifier = new MenuItem("Modifier");
        modifier.setOnAction(event -> onModifierItem(item));

        MenuItem supprimer = new MenuItem("Supprimer");
        supprimer.setOnAction(event -> onSupprimerItem(item));

        menuContextuel.getItems().addAll(modifier, supprimer);

        boite.setOnContextMenuRequested(event ->
                menuContextuel.show(boite.getScene().getWindow(), event.getScreenX(), event.getScreenY())
        );

        return boite;
    }

    private void onSupprimerItem(Item item) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogSupprimerItem.fxml")
            );
            Parent root = loader.load();

            DialogSupprimerItemController controller = loader.getController();
            controller.setManager(manager);
            controller.setItem(item);
            controller.setTierList(tierList);
            controller.setOnItemSuppr(() -> this.rafraichirAffichage());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Supprimer Item");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onModifierItem(Item item) {
        if (item instanceof TextItem) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/sae_tierlist/dialogModifierItem.fxml")
                );
                Parent root = loader.load();

                DialogModifierItemController controller = loader.getController();
                controller.setManager(manager);
                controller.setItem(item);
                controller.setOnItemEdit(() -> this.rafraichirAffichage());

                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Modifier un item");

                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
                dialog.setScene(scene);
                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (item instanceof ImageItem) {
            ImageItem imgItem = (ImageItem) item;
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choisir une nouvelle image");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            Stage stage = (Stage) containerTiers.getScene().getWindow();
            File fichier = chooser.showOpenDialog(stage);
            if (fichier != null) {
                imgItem.setCheminItem(fichier.getAbsolutePath());
                rafraichirAffichage();
            }
        }
    }

    private void rendreDraggable(Node vue, Item item) {
        vue.setOnDragDetected(event -> {
            Dragboard db = vue.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(item.getId()));
            db.setContent(content);
            vue.setOpacity(0.5); // effet visuel: l'item devient a moitié transparent pendant le drag
            event.consume();
        });

        vue.setOnDragDone(event -> {
            vue.setOpacity(1.0); // rétablit l'opacité quand le drag est fini
            event.consume();
        });
    }


    //  cherche un item dans toute la tier-list (tiers+zone à classer) par son id
    private Item trouverItemParId(int id) {
        for (Item item : tierList.getaClasser()) {
            if (item.getId() == id) return item;
        }
        for (Tier tier : tierList.getListeTierList()) {
            for (Item item : tier.getListeTier()) {
                if (item.getId() == id) return item;
            }
        }
        return null;
    }

    // Retourne la liste qui contient cet item (aClasser ou la liste d'un tier)
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

    private void rendreReceveur(Pane zone, List<Item> destination) {
        zone.setOnDragOver(event -> {
            if (event.getGestureSource() != zone
                    && event.getDragboard().hasString()
                    && !event.getDragboard().getString().startsWith("TIER:")) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        zone.setOnDragEntered(event -> {
            if (event.getGestureSource() != zone && event.getDragboard().hasString()) {
                zone.setStyle(zone.getStyle() + "-fx-background-color: #e0e7ff;");
            }
            event.consume();
        });

        zone.setOnDragExited(event -> {
            rafraichirAffichage();
            event.consume();
        });

        zone.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean succes = false;
            if (db.hasString()) {
                int idItem = Integer.parseInt(db.getString());
                Item itemDeplace = trouverItemParId(idItem);
                if (itemDeplace != null) {
                    List<Item> source = trouverConteneurDe(itemDeplace);
                    if (source != null) {
                        // Calculer l'index d'insertion selon la position X de la souris
                        int indexInsertion = calculerIndexInsertion(zone, event.getX());

                        // Retirer de la source
                        int oldIndex = source.indexOf(itemDeplace);
                        source.remove(itemDeplace);

                        // Ajuster l'index si on déplace dans la même liste vers la droite
                        if (source == destination && oldIndex < indexInsertion) {
                            indexInsertion--;
                        }

                        // insérer à la bonne position
                        if (indexInsertion > destination.size()) {
                            indexInsertion = destination.size();
                        }
                        destination.add(indexInsertion, itemDeplace);

                        succes = true;
                        rafraichirAffichage();
                    }
                }
            }
            event.setDropCompleted(succes);
            event.consume();
        });
    }

    // Méthode qui permet de calculer l'index où insére un item dans une zone (selon la position X de la souris)
    private int calculerIndexInsertion(Pane zone, double x) {
        int index = 0;
        for (Node child : zone.getChildren()) {
            double childCenterX = child.getBoundsInParent().getMinX() + child.getBoundsInParent().getWidth() / 2;
            if (x < childCenterX) {
                return index;
            }
            index++;
        }
        return index;
    }

    private void onSupprimerTier(Tier tier) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogSupprimerTier.fxml")
            );
            Parent root = loader.load();

            DialogSupprimerTierController controller = loader.getController();
            controller.setManager(manager);
            controller.setTierList(tierList);
            controller.setTier(tier);
            controller.setOnTierSuppr(() -> this.rafraichirAffichage());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Supprimer Tier");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onRenommerTier(Tier tier) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogRenommerTier.fxml")
            );
            Parent root = loader.load();

            DialogRenommerTierController controller = loader.getController();
            controller.setManager(manager);
            controller.setTier(tier);
            controller.setOnTierRenommer(() -> this.rafraichirAffichage());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Renommer Tier");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onPersonnaliserTier(Tier tier) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Personnaliser le tier");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(350);

        Label titre = new Label("Personnaliser \"" + tier.getNom() + "\"");
        titre.getStyleClass().add("dialog-titre");

        // Couleur
        Label labelCouleur = new Label("Couleur");
        labelCouleur.getStyleClass().add("dialog-label");
        HBox palette = new HBox(8);
        String[] couleurs = {"#F09595", "#FAC775", "#C0DD97", "#85B7EB", "#CECBF6", "#F4C0D1", "#888888"};
        for (String couleur : couleurs) {
            HBox carre = new HBox();
            carre.setPrefSize(30, 30);
            carre.setStyle("-fx-background-color: " + couleur + "; -fx-background-radius: 6; -fx-cursor: hand;");
            carre.setOnMouseClicked(e -> {
                tier.setCouleur(couleur);
                rafraichirAffichage();
            });
            palette.getChildren().add(carre);
        }

        // Hauteur
        Label labelHauteur = new Label("Hauteur : " + tier.getHauteur() + " px");
        labelHauteur.getStyleClass().add("dialog-label");
        Slider sliderHauteur = new Slider(40, 150, tier.getHauteur());
        sliderHauteur.setShowTickLabels(true);
        sliderHauteur.setShowTickMarks(true);
        sliderHauteur.setMajorTickUnit(30);
        sliderHauteur.valueProperty().addListener((obs, ancien, nouveau) -> {
            int hauteur = nouveau.intValue();
            tier.setHauteur(hauteur);
            labelHauteur.setText("Hauteur : " + hauteur + " px");
            rafraichirAffichage();
        });

        // Bouton fermer
        Button fermer = new Button("Fermer");
        fermer.getStyleClass().add("bouton-principal");
        fermer.setOnAction(e -> dialog.close());

        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.getChildren().add(fermer);

        root.getChildren().addAll(titre, labelCouleur, palette, labelHauteur, sliderHauteur, boutons);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @FXML
    private void onReinitialiser() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sae_tierlist/dialogReinitialiserTierList.fxml")
            );
            Parent root = loader.load();

            DialogReinitialiserTierListController controller = loader.getController();
            controller.setManager(manager);
            controller.setTierList(tierList);
            controller.setOnTierListReinitialiser(() -> this.rafraichirAffichage());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Reinitialiser TierList");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onChangerTailleItems() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Taille des items");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setPrefWidth(350);
        Label titre = new Label("Taille des items");
        titre.getStyleClass().add("dialog-titre");
        Label labelTaille = new Label("Taille : " + tierList.getTailleItems() + " px");
        labelTaille.getStyleClass().add("dialog-label");

        Slider sliderTaille = new Slider(30, 120, tierList.getTailleItems());
        sliderTaille.setShowTickLabels(true);
        sliderTaille.setShowTickMarks(true);
        sliderTaille.setMajorTickUnit(20);
        sliderTaille.valueProperty().addListener((obs, ancien, nouveau) -> {
            int taille = nouveau.intValue();
            tierList.setTailleItems(taille);
            labelTaille.setText("Taille : " + taille + " px");
            rafraichirAffichage();
        });

        Button fermer = new Button("Fermer");
        fermer.getStyleClass().add("bouton-principal");
        fermer.setOnAction(e -> dialog.close());

        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_RIGHT);
        boutons.getChildren().add(fermer);

        root.getChildren().addAll(titre, labelTaille, sliderTaille, boutons);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/example/sae_tierlist/style.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }


}