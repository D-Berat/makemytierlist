package com.example.sae_tierlist.controller;

import com.example.sae_tierlist.api.APIManager;
import com.example.sae_tierlist.api.ExceptImageNotFound;
import com.example.sae_tierlist.api.pojo.Result;
import com.example.sae_tierlist.model.ImageItem;
import com.example.sae_tierlist.model.TextItem;
import com.example.sae_tierlist.model.TierList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class DialogNouvelItemController {

    @FXML private Button boutonTypeTexte;
    @FXML private Button boutonTypeImage;
    @FXML private Button boutonTypeAPI;

    @FXML private VBox zoneTexte;
    @FXML private VBox zoneImage;
    @FXML private VBox zoneAPI;

    @FXML private TextField champTexte;
    @FXML private TextField champRechercheAPI;        // ⬅ nouveau : champ pour taper le mot-clé
    @FXML private FlowPane imagesTrouve;
    @FXML private Label labelCheminImage;
    @FXML private Button boutonAnnuler;
    @FXML private Button boutonValider;

    private TierList tierList;
    private Runnable onItemAjoute;
    private String cheminImageChoisie = null;

    // 3 modes possibles
    private enum Mode { TEXTE, IMAGE, API }
    private Mode modeActuel = Mode.TEXTE;

    public void setTierList(TierList tl) {
        this.tierList = tl;
    }

    public void setOnItemAjoute(Runnable callback) {
        this.onItemAjoute = callback;
    }

    @FXML
    private void initialize() {
        afficherModeTexte();
    }

    @FXML private void onChoisirTexte() { afficherModeTexte(); }
    @FXML private void onChoisirImage() { afficherModeImage(); }
    @FXML private void onChoisirAPI() { afficherModeAPI(); }

    private void afficherModeTexte() {
        modeActuel = Mode.TEXTE;
        zoneTexte.setVisible(true);  zoneTexte.setManaged(true);
        zoneImage.setVisible(false); zoneImage.setManaged(false);
        zoneAPI.setVisible(false);   zoneAPI.setManaged(false);
        boutonValider.setVisible(true); boutonValider.setManaged(true);
        boutonTypeTexte.getStyleClass().setAll("bouton-principal");
        boutonTypeImage.getStyleClass().setAll("bouton-secondaire");
        boutonTypeAPI.getStyleClass().setAll("bouton-secondaire");
    }

    private void afficherModeImage() {
        modeActuel = Mode.IMAGE;
        zoneTexte.setVisible(false); zoneTexte.setManaged(false);
        zoneImage.setVisible(true);  zoneImage.setManaged(true);
        zoneAPI.setVisible(false);   zoneAPI.setManaged(false);
        boutonValider.setVisible(true); boutonValider.setManaged(true);
        boutonTypeImage.getStyleClass().setAll("bouton-principal");
        boutonTypeTexte.getStyleClass().setAll("bouton-secondaire");
        boutonTypeAPI.getStyleClass().setAll("bouton-secondaire");
    }

    private void afficherModeAPI() {
        modeActuel = Mode.API;
        zoneTexte.setVisible(false); zoneTexte.setManaged(false);
        zoneImage.setVisible(false); zoneImage.setManaged(false);
        zoneAPI.setVisible(true);    zoneAPI.setManaged(true);
        boutonValider.setVisible(false); boutonValider.setManaged(false);
        boutonTypeAPI.getStyleClass().setAll("bouton-principal");
        boutonTypeTexte.getStyleClass().setAll("bouton-secondaire");
        boutonTypeImage.getStyleClass().setAll("bouton-secondaire");
    }

    @FXML
    private void onChoisirFichier() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) boutonAnnuler.getScene().getWindow();
        File fichier = chooser.showOpenDialog(stage);
        if (fichier != null) {
            cheminImageChoisie = fichier.getAbsolutePath();
            labelCheminImage.setText(fichier.getName());
        }
    }


    @FXML
    private void onRechercherAPI() {
        String motCle = champRechercheAPI.getText().trim();
        if (motCle.isBlank()) return;

        imagesTrouve.getChildren().clear();
        String apiKey = System.getenv("RAWG_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            imagesTrouve.getChildren().add(new Label("Configurez RAWG_API_KEY puis relancez l'application."));
            return;
        }

        try {
            APIManager api = new APIManager();
            Result[] resultats = api.rechercher(motCle);

            for (Result r : resultats) {
                if (r.getBackgroundImage() == null) continue;

                // La carte qui contiendra image + nom
                VBox carte = new VBox();
                carte.setSpacing(4);
                carte.setAlignment(Pos.CENTER);
                carte.setPrefSize(120, 140);
                carte.setMinSize(120, 140);
                carte.setMaxSize(120, 140);
                carte.getStyleClass().add("carte-resultat-api");
                carte.setStyle("-fx-cursor: hand;");

                // L'image
                ImageView vue = new ImageView(new Image(r.getBackgroundImage(), 100, 100, true, true, true));
                vue.setFitWidth(100);
                vue.setFitHeight(100);
                vue.setPreserveRatio(true);

                // Le nom du jeu en dessous
                Label nomJeu = new Label(r.getName());
                nomJeu.setWrapText(true);
                nomJeu.setAlignment(Pos.CENTER);
                nomJeu.setTextAlignment(TextAlignment.CENTER);
                nomJeu.setMaxWidth(110);
                nomJeu.setStyle("-fx-font-size: 11px; -fx-text-fill: #333;");

                carte.getChildren().addAll(vue, nomJeu);

                // Au clic, on ajoute l'image à la tier-list
                carte.setOnMouseClicked(event -> {
                    tierList.getaClasser().add(new ImageItem(r.getBackgroundImage()));
                    terminer();
                });

                imagesTrouve.getChildren().add(carte);
            }
        } catch (ExceptImageNotFound e) {
            Label aucun = new Label("Aucun résultat trouvé pour : " + motCle);
            imagesTrouve.getChildren().add(aucun);
        }
    }

    @FXML
    private void onValider() {
        if (modeActuel == Mode.TEXTE) {
            String texte = champTexte.getText().trim();
            if (!texte.isBlank()) {
                tierList.getaClasser().add(new TextItem(texte));
                terminer();
            }
        } else if (modeActuel == Mode.IMAGE) {
            if (cheminImageChoisie != null) {
                tierList.getaClasser().add(new ImageItem(cheminImageChoisie));
                terminer();
            }
        }
        // Mode API : pas besoin de bouton "Valider", le clic sur l'image suffit
    }

    private void terminer() {
        if (onItemAjoute != null) onItemAjoute.run();
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