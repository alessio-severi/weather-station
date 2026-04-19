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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.stream.IntStream;

import it.chorax.app.MisureContext;
import it.chorax.app.Navigator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class ConverterController implements Converter{

    // Questi @FXML sono i "collegamenti" tra FXML e Java
    @FXML
    private TextField inputField;

    @FXML
    private ComboBox<Unit> fromUnitBox;

    @FXML
    private ComboBox<Unit> toUnitBox;

    @FXML
    private Label resultLabel;

    @FXML
    private Label nameMisureLabel;

    private final MisureContext currentMisure;
    private final Navigator navigator;




    public ConverterController(MisureContext currentMisure, Navigator navigator) {
        this.currentMisure = currentMisure;
        this.navigator = navigator;
    }



    /**
     * Metodo chiamato automaticamente dopo il caricamento dell'FXML.
     * È l'equivalente di un "onLoad" della pagina.
     */
    @FXML
    private void initialize() {

        Unit[] arrayUnit = unitSet();

        // Popola le combo con le unità disponibili per la grandezza scelta.
        fromUnitBox.setItems(FXCollections.observableArrayList(arrayUnit));
        toUnitBox.setItems(FXCollections.observableArrayList(arrayUnit));

        // Valori di default (per es: Celsius → Fahrenheit)
        fromUnitBox.setValue(arrayUnit[0]);
        toUnitBox.setValue(arrayUnit[1]);

       
        nameMisureLabel.setText("Convert " + currentMisure.getMisure().name().toLowerCase() + "s");

    }


    private Unit[] unitSet() {

        String[] arrayUnitName = currentMisure.getMisure().label().split("-");

        return  IntStream.range(0, arrayUnitName.length)
                         .mapToObj(i -> Unit.valueOf(arrayUnitName[i]))
                         .toArray(Unit[]::new);

    }



    @FXML
    private void switchRoot() {
        navigator.setRoot("converter/menu");
        
    }



    /**
     * Handler collegato al pulsante "Convert".
     * In FXML: onAction="#handleConvert"
     */
    @FXML
    private void handleConvert() {

        
        String text = inputField.getText();
        double value;


        if (text == null || text.isBlank()) {
            resultLabel.setText("Please enter a value to convert.");
            return;
        }

        
        try {
            // gestisce anche 12,5
            value = Double.parseDouble(text.replace(',', '.'));

        } catch (NumberFormatException e) {
            resultLabel.setText("• Invalid numeric value.");
            return;
        }


        Unit from = fromUnitBox.getValue();
        Unit to   = toUnitBox.getValue();

        /*
        if (from == null || to == null) {
            resultLabel.setText("• Please select both units.");
            return;
        }
        */

        double result = currentMisure.getMisure().convertValue(value, from, to);

    

        resultLabel.setText(String.format("•  %s %s  →  %s %s",
                formatValue(value),  from.symbolOf(),
                formatValue(result), to.symbolOf()));

    }


    private String formatValue(double x) {

        DecimalFormatSymbols US = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat normal = new DecimalFormat("0.###", US);
        DecimalFormat sci = new DecimalFormat("0.###E0", US);

        double ax = Math.abs(x);

        // Se è molto piccolo ma non zero, uso più decimali
        if (ax > 0 && ax < 1e-2)
            return sci.format(x);
        
        return normal.format(x);
    }

    
}