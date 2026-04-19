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

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

public final class Dialogs {

    private Dialogs() {}


    public static void showFatalError(final Window owner, String header, String message, Throwable e) {

        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle("Internal error");
        alert.setHeaderText(header);
        alert.setContentText(message);

        if (owner != null) {
            alert.initOwner(owner);
        }

        alert.showAndWait();

        Platform.exit();
        System.exit(1);
    }


    public static void showNonFatalError(final Window owner, String title, String header, String message, Throwable e) {

        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        if (owner != null) {
            alert.initOwner(owner);
        }

        alert.showAndWait();
    }


    public static void showNonFatalError(Window owner, String title, String header, String message) {
        showNonFatalError(owner, title, header, message, null);
    }
    
}
