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


package it.chorax.world;

import it.chorax.app.Navigator;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;



public class WorldController {



    @FXML
    private Button worldButton;

    @FXML
    private StackPane globePane;

    @FXML
    private ScrollPane globeScroll;


    private final Navigator navigator;



    public WorldController(Navigator navigator) {
        this.navigator = navigator;
    }

    

    @FXML
    private void initialize() {
        worldButton.getStyleClass().add("side-link-active");

        initGlobe();
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
    private void onExit() {
        Platform.exit();
    }


    @FXML
    public void onAbout() {
        navigator.setRoot("about/about");
        
    }

    
    private void initGlobe() {

        // 1) Disattiva anche la rotellina / gesture di scroll / click
        globeScroll.addEventFilter(ScrollEvent.ANY, Event::consume);
        globeScroll.addEventFilter(MouseEvent.ANY, Event::consume);


        // 1) Sphere: raggio medio
        double radius = 350;
        Sphere globe = new Sphere(radius);

        // 2) Materiale con texture
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass()
                                        .getResource("/it/chorax/world/earth-night.jpg")
                                        .toExternalForm()));
        globe.setMaterial(material);

        // 3) Root 3D
        Group root3D = new Group(globe);

        // 4) Luce (altrimenti è quasi tutto nero)
        AmbientLight light = new AmbientLight(Color.web("#ffffffff"));
        root3D.getChildren().add(light);

        // 5) Camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(40);        // apertura "normale"

        // distanza giusta affinché la sfera entri bene nel frame
        double fovRad = Math.toRadians(camera.getFieldOfView());
        double distance = radius / Math.tan(fovRad / 2.0);
        camera.setTranslateZ(-distance * 1.4);  // un filo più lontano
        camera.setNearClip(0.1);
        camera.setFarClip(5000);

        // 6) SubScene 3D
        // dimensione iniziale, non definitiva
        SubScene subScene = new SubScene(root3D,723, 840,true,
                                         SceneAntialiasing.BALANCED);

        subScene.setCamera(camera);
        subScene.setFill(Color.TRANSPARENT);

        // vincola la SubScene alle dimensioni del pane
        //subScene.widthProperty().bind(globePane.widthProperty());
        //subScene.heightProperty().bind(globePane.heightProperty());

        // 7) inserisco SOLO la SubScene nello StackPane
        globePane.getChildren().clear();
        globePane.getChildren().add(subScene);


        // 8) Rotazione lenta
        RotateTransition rt = new RotateTransition(Duration.seconds(30), globe);
        rt.setAxis(Rotate.Y_AXIS);
        rt.setFromAngle(0);
        rt.setToAngle(360);
        rt.setCycleCount(RotateTransition.INDEFINITE);

        // velocità costante, niente rallentamenti ai giri
        rt.setInterpolator(Interpolator.LINEAR);

        rt.play();


    }
}