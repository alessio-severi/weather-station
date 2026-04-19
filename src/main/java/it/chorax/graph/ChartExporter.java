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


package it.chorax.graph;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;



public final class ChartExporter {

    private static double scale = 2.0;  // 2x per Retina;

    private ChartExporter() {
        // utility: costruttore privato
    }

    public static void saveNodeAsPng(Node node, File file) throws IOException {

        String name = file.getName();

        if (!name.toLowerCase().endsWith(".png")) {
            file = new File(file.getParentFile(), name + ".png");
        }

        // parametri snapshot
        SnapshotParameters params = new SnapshotParameters();

        // contenuto viene renderizzato ad alta risoluzione: 2x
        params.setTransform(new Scale(scale, scale));

        params.setFill(Color.TRANSPARENT);



        // prendo le dimensioni logiche del nodo
        Bounds bounds = node.getLayoutBounds();

        // crea un buffer di destinazione abbastanza grande per contenere il disegno a 2x
        WritableImage image = new WritableImage(
                (int) Math.round(bounds.getWidth()  * scale),
                (int) Math.round(bounds.getHeight() * scale)
        );


        // snapshot nel buffer già scalato
        node.snapshot(params, image);


        // salva in PNG
        BufferedImage awtImage = SwingFXUtils.fromFXImage(image, null);     // 	copia
        ImageIO.write(awtImage, "png", file);

    }



    
}
