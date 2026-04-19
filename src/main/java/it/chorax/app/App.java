/*
 * Copyright (C) 2026 Alessio Severi
 *
 * This file is part of Weather Station.
 *
 * Weather Station is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Weather Station is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Weather Station. If not, see <https://www.gnu.org/licenses/>.
 */


package it.chorax.app;

import java.io.IOException;

import it.chorax.about.AboutCredits;
import it.chorax.converter.ConverterController;
import it.chorax.converter.MenuController;
import it.chorax.graph.GraphController;
import it.chorax.weather.WeatherController;
import it.chorax.world.WorldController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application implements Navigator {

    private Scene scene;
    private final MisureContext currentMisure = new MisureContext();
    private final WeatherForecast forecastMisure = new WeatherForecast();

    
    @Override
    public void start(Stage stage) {

        // import javafx.scene.text.Font;
        // size fittizia, serve solo a registrarlo
        /* Font.loadFont(
                App.class.getResource("/it/chorax/fonts/NunitoSans.ttf").toExternalForm(),10
        ); */

        // Scene ≈ "finestra contenuto".
        try {
            scene = new Scene(loadFXML("weather/weather"), 1000, 995, Color.web("#24252a"));

            stage.setTitle("Weather Station");
            stage.setScene(scene);

            // Limiti minimi della finestra
            stage.setMinWidth(1000);
            stage.setMinHeight(1023);

            stage.show();

        } catch (Exception e) {

            Dialogs.showFatalError(stage,
                "The application could not be started.",
                """
                An internal error occurred while loading the main window.
                Please close the application and try again.
                                                         
                If the problem persists, contact the developer.""",
                e
            );
        }

  
    }


    /**
     * Cambia la root della scena caricando un nuovo FXML.
     *
     */
    @Override
    public void setRoot(String fxml) {

        try {
            scene.setRoot(loadFXML(fxml));

        } catch (IOException e) {

            Dialogs.showNonFatalError(scene != null ? scene.getWindow() : null,
                "Navigation error",
                "Unable to open the requested view.",
                """
                An internal error occurred while loading the page.
                Please try again.""",
                e
            );

        }
    }


    private Parent loadFXML(String fxml) throws IOException {

        String basePath = "/it/chorax/";
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(basePath + fxml + ".fxml"));

        // Qui forniamo a JavaFX come costruire i controller
        // • type è il parametro di ingresso (Class<?> type), cioè la classe del controller che l’FXMLLoader vuole creare.
        fxmlLoader.setControllerFactory(type -> {
            try {
                // Se è il MenuController, passa il currentMisure e il this al costruttore
                if (type == MenuController.class) {
                    return new MenuController(currentMisure, this);
                }
                // Se è il ConverterController, passa il currentMisure e il this al costruttore
                if (type == ConverterController.class) {
                    return new ConverterController(currentMisure, this);
                }
                if (type == WeatherController.class) {
                    return new WeatherController(forecastMisure, this);
                }
                if (type == GraphController.class) {
                    return new GraphController(forecastMisure, this);
                }
                if (type == WorldController.class) {
                    return new WorldController(this);
                }
                if (type == AboutCredits.class) {
                    return new AboutCredits(this);
                }

                // Per tutti gli altri, usa il costruttore di default
                return type.getDeclaredConstructor().newInstance();


            } catch (Exception e ) {
                throw new RuntimeException("Failed to create controller: " + type, e);
            }
        });

        return fxmlLoader.load();
    }




    public static void main(String[] args) {
        launch();
    }
}