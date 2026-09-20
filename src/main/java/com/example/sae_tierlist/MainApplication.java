package com.example.sae_tierlist;

import com.example.sae_tierlist.controller.AccueilController;
import com.example.sae_tierlist.model.TierListManager;
import com.example.sae_tierlist.persistence.PersistenceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private TierListManager manager;
    private PersistenceManager persistence;

    @Override
    public void start(Stage stage) throws IOException {
        persistence = new PersistenceManager();
        manager = persistence.charger("sauvegarde.bin");
        manager.recalculerCompteurItems();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("accueil.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 750);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        AccueilController controller = fxmlLoader.getController();
        controller.setManager(manager);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/logo.png")));
        stage.setTitle("Gestion de Tier-Lists");
        stage.setScene(scene);

        stage.show();
    }

    @Override
    public void stop() {
        // Appelé automatiquement par JavaFX à la fermeture de l'app
        if (manager != null && persistence != null) {
            persistence.sauvegarder(manager, "sauvegarde.bin");
            System.out.println("Sauvegarde effectuée.");
        }
    }
}