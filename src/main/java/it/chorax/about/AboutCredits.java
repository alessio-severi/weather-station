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

package it.chorax.about;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import it.chorax.app.Dialogs;
import it.chorax.app.Navigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Window;



public class AboutCredits {
   

    @FXML
    private HBox aboutRoot;


    @FXML
    private Label otherTextLabel, servicesTextLabel, aboutTextLabel;



    private final Navigator navigator;



    public AboutCredits(Navigator navigator) {
        this.navigator = navigator;
    }

    
   

    @FXML
    public void onWeather() {
        navigator.setRoot("weather/weather");
        
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
    private void initialize() {

        aboutTextLabel.setText("""
                               Weather Station 1.2 is a non-commercial macOS desktop application developed
                               by Alessio Severi as a portfolio project.
                               It shows current weather conditions and 7-day weather forecasts for Italian regional capitals,
                               with a textual weather report, a unit converter for common meteorological quantities, a set of
                               interactive charts that can be exported as PNG images, and a simple 3D globe view.""");


        servicesTextLabel.setText("""
                               Weather data provided by open-meteo.com, a free public weather API, used in accordance with
                               its terms of service.""");
                               

        otherTextLabel.setText("""
                                • Night earth 2d texture based on Natural Earth IlI by Tom Patterson (shadedrelief.com) a
                                   public domain dataset, used under the terms of its original license.

                                • Weather icons from Weathericons and Materialdesign2 via the Ikonli icon library, used
                                   under the terms of its original license.""");
                               
    }


    @FXML
    private void onOpenWebsite() {
        String url = "https://chorax.it";

        try {

            if (Desktop.isDesktopSupported()) {

                Desktop.getDesktop().browse(new URI(url));

            } else {
                throw new UnsupportedOperationException("Desktop API not supported on this platform.");
            }


        } catch (IOException | URISyntaxException | UnsupportedOperationException e) {

            Window owner = (aboutRoot != null && aboutRoot.getScene() != null)
                         ? aboutRoot.getScene().getWindow()
                         : null;
            
            Dialogs.showNonFatalError(
                owner,
                "Unable to open website",
                "Could not open chorax.it in your default browser.",
                """
                An unexpected error occurred while opening the website.
                Please open it manually in your browser:

                """ + url,
                e
            );
        }


    }


    
}