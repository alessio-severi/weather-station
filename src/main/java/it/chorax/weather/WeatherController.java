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


package it.chorax.weather;


import it.chorax.app.Navigator;
import it.chorax.app.WeatherForecast;
import it.chorax.weather.Coordinates.ItalianCapital;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class WeatherController {


    @FXML
    private ComboBox<ItalianCapital> cityCombo;

    @FXML
    private VBox reportBox;

    @FXML
    private Label reportLabel;

    @FXML
    private Label welcomeArea;

    @FXML
    private Button weatherButton;

    @FXML
    private Button graphButton;



    private final Navigator navigator;
    private final WeatherForecast forecastMisure;



    public WeatherController(WeatherForecast forecastMisure, Navigator navigator) {
        this.navigator = navigator;
        this.forecastMisure = forecastMisure;
    }



    @FXML
    private void initialize() {
        // Riempie la combo con i capoluoghi
        cityCombo.setItems(FXCollections.observableArrayList(ItalianCapital.values()));
        cityCombo.setValue(Coordinates.ItalianCapital.ROMA); // default


        welcomeArea.setText("""
                             The weather data is provided by the open-meteo.com service.
                             Please select the name of an Italian regional capital to view the city's weather conditions:""");

        weatherButton.getStyleClass().add("side-link-active");


        // tool grafici disattivati
        graphButton.setVisible(false);
        graphButton.setManaged(false);
    }


    @FXML
    public void onGraph() {
        navigator.setRoot("graph/graph");
        
    }
    
    @FXML
    public void onConverter() {
        navigator.setRoot("converter/menu");
        
    }

    @FXML
    public void onWorld() {
        navigator.setRoot("world/world");
        
    }
    

    @FXML
    private void onExit() {
        Platform.exit();
    }


    @FXML
    public void onAbout() {
        navigator.setRoot("about/about");
        
    }


    public void onFetchWeather() {


        ItalianCapital selected = cityCombo.getValue();

        if (selected == null) {
            reportLabel.setText("Please select a city.");
            return;
        }

        try {
            WeatherStation station = new WeatherStation(selected);

            station.buildReport(reportBox, forecastMisure);

            // tool grafici attivati
            graphButton.setVisible(true);
            graphButton.setManaged(true);

            

        } catch (WeatherServiceException e) {

            reportLabel.setText("Error while retrieving weather data:\n" + e.getMessage());

        } catch (Exception e) {

            // fallback di sicurezza
            reportLabel.setText(
                "An unexpected error occurred while retrieving weather data."
            );
        }
        
    }
}
