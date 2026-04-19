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


module it.chorax {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.kordamp.ikonli.weathericons;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.kordamp.ikonli.javafx;
    requires javafx.base;
    requires javafx.swing;   // <-- per SwingFXUtils
    requires java.desktop;   // <-- per javax.imageio.ImageIO

    
    opens it.chorax.app to javafx.fxml;
    opens it.chorax.converter to javafx.fxml;
    opens it.chorax.weather to javafx.fxml;
    opens it.chorax.world to javafx.fxml;
    opens it.chorax.graph to javafx.fxml;
    opens it.chorax.about to javafx.fxml;


    exports it.chorax.app;
    exports it.chorax.converter;
    exports it.chorax.weather;
    exports it.chorax.world;
    exports it.chorax.graph;
    exports it.chorax.about;
}
