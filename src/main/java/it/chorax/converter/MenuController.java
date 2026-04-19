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


package it.chorax.converter;

import it.chorax.app.MisureContext;
import it.chorax.app.Navigator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;


public class MenuController {


    @FXML
    private Hyperlink temperatureLink;

    @FXML
    private Hyperlink pressureLink;

    @FXML
    private Hyperlink lengthLink;

    @FXML
    private Hyperlink speedLink;

     @FXML
    private Button converterButton;


    private final MisureContext currentMisure;
    private final Navigator navigator;



    public MenuController(MisureContext currentMisure, Navigator navigator) {
        this.currentMisure = currentMisure;
        this.navigator = navigator;
    }

    

    @FXML
    private void initialize() {
        converterButton.getStyleClass().add("side-link-active");
    }

    
    @FXML
    private void switchRoot(ActionEvent event) {

        Hyperlink clicked = (Hyperlink) event.getSource();  // chi ha generato l’evento

        if (clicked == temperatureLink) {
            currentMisure.setMisure(Converter.Unit.TEMPERATURE);
        
        } else if (clicked == pressureLink) {
            currentMisure.setMisure(Converter.Unit.PRESSURE);

        } else if (clicked == lengthLink) {
            currentMisure.setMisure(Converter.Unit.LENGTH);

        } else if (clicked == speedLink) {
            currentMisure.setMisure(Converter.Unit.SPEED);

        }

        navigator.setRoot("converter/converter");
    }


    @FXML
    public void onWeather() {
        navigator.setRoot("weather/weather");
        
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
    
}