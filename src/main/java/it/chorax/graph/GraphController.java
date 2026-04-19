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


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.chorax.app.Dialogs;
import it.chorax.app.Navigator;
import it.chorax.app.WeatherForecast;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;


public class GraphController {

   @FXML
   private Label cityLabel;

   @FXML
   private VBox chartBox;

   @FXML
   private HBox caseBox;


   @FXML
    private Button aButton, bButton, cButton, dButton, eButton;

   
    @FXML
    private Button graphButton, case0Button, case1Button;


    private final Navigator navigator;
    private final WeatherForecast forecastMisure;

    private List<Button> MISURE_BUTTONS;
    private List<String> MISURE_ORDER;
    private List<String> DATE_LABELS;

    private CategoryAxis xAxis;
    private NumberAxis yAxis;
    private BarChart<String, Number> barChart;
    

    private static final double LABEL_MIN_HEIGHT_PX = 30.0;




    public GraphController(WeatherForecast forecastMisure, Navigator navigator) {
        this.navigator = navigator;
        this.forecastMisure = forecastMisure;

    }



    @FXML
    private void initialize() {
        
        graphButton.getStyleClass().add("side-link-active");

        // 1) lista dei bottoni, nell'ordine dell'FXML
        MISURE_BUTTONS = List.of(aButton, bButton, cButton, dButton, eButton);

        // 2) testo iniziale preso dall'FXML
        MISURE_ORDER = MISURE_BUTTONS.stream()
                                     .map(Button::getText)
                                     .toList();

        cityLabel.setText(forecastMisure.getCity());

        DATE_LABELS = forecastMisure.getMisures()
                                   .get("time")
                                   .stream()
                                   .map(d-> LocalDate.parse(d.replace("\"", "")))
                                   .map(d->d.format(DateTimeFormatter.ofPattern("d EEE")))
                                   .toList();

        disableButton0();

        chart();

        showBarChart(
            forecastMisure.getMisures().get("temperature max"),
            forecastMisure.getMisures().get("temperature min"),
            "Date",
            "Temperature (°C)"
        );

        
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
    public void onAbout() {
        navigator.setRoot("about/about");
        
    }


    
    
   
    private void chart() {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();

       /*  	•	contiene assi (xAxis, yAxis)
            •	contiene l’area di plot (lo sfondo dove stanno le barre)
            •	contiene le serie (XYChart.Series) con i dati */
        barChart = new BarChart<>(xAxis, yAxis);

        barChart.setLegendVisible(true);
        barChart.setLegendSide(Side.TOP);
        barChart.setAnimated(false);

        barChart.setCategoryGap(20);  // distanza fra giorni


        chartBox.getChildren().add(barChart);

        barChart.getStyleClass().add("ws-barchart");
        

    }



    private void showBarChart(List<String> maxValues, List<String> minValues, String xLabel, String yLabel) {

        if (maxValues == null || maxValues.isEmpty()) {
            System.out.println("Nessun dato per il grafico: " + yLabel);
            barChart.getData().clear();
            return;
        }


        final double SERIES_MAX_SET_TRANSLATE_X = 3.0;
        final double SERIES_MIN_SET_TRANSLATE_X = 5.0;

        final String SERIES_MAX_TEXT_STYLE_CLASS = "bar-value-label0";
        final String SERIES_MIN_TEXT_STYLE_CLASS = "bar-value-label1";


        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);


        //  serie di dati per disegnare un istogramma
        XYChart.Series<String, Number> seriesMax = new XYChart.Series<>();
        XYChart.Series<String, Number> seriesMin = new XYChart.Series<>();

        String measure = aButton.getText();   // misura corrente nel menu
        setLegendNames(measure, seriesMax, seriesMin);



        buildSeries(maxValues, seriesMax, SERIES_MAX_SET_TRANSLATE_X, SERIES_MAX_TEXT_STYLE_CLASS);
        
        if (minValues != null)
            buildSeries(minValues, seriesMin, SERIES_MIN_SET_TRANSLATE_X, SERIES_MIN_TEXT_STYLE_CLASS);




        barChart.setBarGap(-60);

        barChart.getData().clear();
        

        if (minValues != null)
            barChart.getData().addAll(seriesMax, seriesMin);

        else
            barChart.getData().add(seriesMax);
        

    }



    private void buildSeries(List<String> values, XYChart.Series<String, Number> series, final double SET_TRANSLATE_X, final String TEXT_STYLE_CLASS) {

        for (int i = 0; i < values.size(); i++) {

            String x = DATE_LABELS.get(i);
            

            double y = Double.parseDouble(values.get(i));
            
            // singola Data per disegnare la barra
            XYChart.Data<String, Number> data = new XYChart.Data<>(x, y);
            series.getData().add(data);


            // se il valore è 0, non metto etichetta e passo alla prossima barra
            if (y == 0.0) {
                continue;
            }


            // --- ETICHETTA SULLA BARRA ---
            String label = String.format(Locale.US, "%.1f", y);


            // Quando si aggiunge data, JavaFX crea un nodo grafico (un Node) che la disegna.
            // 	•	newNode è il nodo grafico della singola barra.
	        //  •	Nel BarChart standard è uno StackPane.
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {

                StackPane bar = (StackPane) newNode;

                // testo con il valore
                Text text = new Text(label);
                text.getStyleClass().add(TEXT_STYLE_CLASS);
                text.setRotate(-90);   // verticale

                // aggiungo il Text come figlio di questo StackPane:
                bar.getChildren().add(text);

                // ri-posiziono il testo
                bar.widthProperty().addListener((o, ov, nv) -> {
                    double w = nv.doubleValue();
                    text.setTranslateX(w / SET_TRANSLATE_X);
                });


                // ogni volta che cambia l'altezza della barra
                bar.heightProperty().addListener((o, ov, nv) -> {

                    // posizione del valore in pixel
                    // double valuePos = yAxis.getDisplayPosition(yMax);

                    // altezza in pixel della barra relativa a yMax
                    double barPx = nv.doubleValue();


                    // soglia: se la barra è meno larga di 30 px, niente etichetta
                    boolean show = barPx > LABEL_MIN_HEIGHT_PX;
                    text.setVisible(show);
                
                });
            });
        }


    }



    private void setLegendNames(String measure,
                            XYChart.Series<String, Number> seriesMax,
                            XYChart.Series<String, Number> seriesMin) {


        switch (measure) {
            case "Temperature" -> {
            
                if(case0Button.getStyleClass().contains("etch-button-active")) {
                    seriesMax.setName("Max real temperature");
                    seriesMin.setName("Min real temperature");
                    
                } else {
                    seriesMax.setName("Max apparent temperature");
                    seriesMin.setName("Min apparent temperature");
                }
            }
            case "Pressure" -> {

                if(case0Button.getStyleClass().contains("etch-button-active")) {
                    seriesMax.setName("Max MSL pressure");
                    seriesMin.setName("Min MSL pressure");
                    
                } else {
                    seriesMax.setName("Max surface pressure");
                    seriesMin.setName("Min surface pressure");
                }
            }
            case "Relative Humidity" -> {
                seriesMax.setName("Max humidity");
                seriesMin.setName("Min humidity");
            }
            case "Wind" -> {

                if(case0Button.getStyleClass().contains("etch-button-active")) {
                    seriesMax.setName("Max wind speed");
                    seriesMin.setName("Min wind speed");
                    
                } else {
                    seriesMax.setName("Wind direction dominant");
                    seriesMin.setName("");
                }
            }
            case "Precipitation" -> {
                seriesMax.setName("Max precipitation probability");
                seriesMin.setName("Min precipitation probability");
            }
            
        }
        
    }



    @FXML
    private void onMisureButton(ActionEvent event) {

        Button clicked = (Button) event.getSource();


        // Se il testo del bottone cliccato non è una delle 5 misure, esco
        if (!MISURE_BUTTONS.contains(clicked)) {
            return;
        }
        


        List<String> misureOrder = new ArrayList<>(MISURE_ORDER);

        // 1) il cliccato diventa il primo
        aButton.setText(clicked.getText());
        misureOrder.remove(clicked.getText());

        // riordino il sub menu mantenendo l'ordine in misure_order
        for (int i = 0; i < misureOrder.size(); i++) {
            MISURE_BUTTONS.get(i+1).setText(misureOrder.get(i));
        }
        

        disableButton0();

        switch (aButton.getText()) {
            case "Temperature" -> {
                showBarChart(
                    forecastMisure.getMisures().get("temperature max"),
                    forecastMisure.getMisures().get("temperature min"),
                    "Date",
                    "Temperature (°C)"
                );

                case0Button.setText("Real");
                case1Button.setText("Apparent");

                caseBox.setVisible(true);
               
            }

            case "Pressure" -> {
                showBarChart(
                    forecastMisure.getMisures().get("pressure msl max"),
                    forecastMisure.getMisures().get("pressure msl min"),
                    "Date",
                    "Pressure (hPa)"
                );

                case0Button.setText("Mean Sea Level (MSL)");
                case1Button.setText("Surface");

                caseBox.setVisible(true);
                
            }

            case "Relative Humidity" -> {
                showBarChart(
                    forecastMisure.getMisures().get("relative humidity max"),
                    forecastMisure.getMisures().get("relative humidity min"),
                    "Date",
                    "Relative humidity (%)"
                );

                caseBox.setVisible(false);
               
            }

            case "Wind" -> {
                showBarChart(
                    forecastMisure.getMisures().get("wind speed max"),
                    forecastMisure.getMisures().get("wind speed min"),
                    "Date",
                    "Wind speed (km/h)"
                );

                case0Button.setText("Speed");
                case1Button.setText("Direction");
                
                caseBox.setVisible(true);
                
            }

            case "Precipitation" -> {
                showBarChart(
                    forecastMisure.getMisures().get("precipitation probability max"),
                    forecastMisure.getMisures().get("precipitation probability min"),
                    "Date",
                    "Precipitation probability (%)"
                );

                caseBox.setVisible(false);

            }
        }

        
    }


    @FXML
    private void onChangeGraph0() {

        disableButton0();

        switch (case0Button.getText()) {
            case "Real" -> {
                showBarChart(
                    forecastMisure.getMisures().get("temperature max"),
                    forecastMisure.getMisures().get("temperature min"),
                    "Date",
                    "Temperature (°C)"
                );


            }

            case "Mean Sea Level (MSL)" -> {
                showBarChart(
                    forecastMisure.getMisures().get("pressure msl max"),
                    forecastMisure.getMisures().get("pressure msl min"),
                    "Date",
                    "Pressure (hPa)"
                );

            }

            case "Speed" -> {
                showBarChart(
                    forecastMisure.getMisures().get("wind speed max"),
                    forecastMisure.getMisures().get("wind speed min"),
                    "Date",
                    "Wind speed (km/h)"
                );


            }

        }


    }


    @FXML
    private void onChangeGraph1() {

        disableButton1();

        switch (case1Button.getText()) {
            case "Apparent" -> {
                showBarChart(
                    forecastMisure.getMisures().get("apparent temperature max"),
                    forecastMisure.getMisures().get("apparent temperature min"),
                    "Date",
                    "Temperature (°C)"
                );

            }

            case "Surface" -> {
                showBarChart(
                    forecastMisure.getMisures().get("surface pressure max"),
                    forecastMisure.getMisures().get("surface pressure min"),
                    "Date",
                    "Pressure (hPa)"
                );

           
            }


            case "Direction" -> {
                showBarChart(
                    forecastMisure.getMisures().get("wind direction dominant"),
                    null,
                    "Date",
                    "Wind direction (°)"
                );

            }

        }

    }


    private void disableButton0() {

        if (!case0Button.getStyleClass().contains("etch-button-active")) {
            case0Button.getStyleClass().add("etch-button-active");
        }

        case1Button.getStyleClass().removeIf("etch-button-active"::equals);

        case0Button.setDisable(true);
        case1Button.setDisable(false);

    }

    private void disableButton1() {

        if (!case1Button.getStyleClass().contains("etch-button-active")) {
            case1Button.getStyleClass().add("etch-button-active");
        }
        case0Button.getStyleClass().removeIf("etch-button-active"::equals);

        case1Button.setDisable(true);
        case0Button.setDisable(false);

    }


    @FXML
    private void onSaveChart() {

        // importa javafx.stage.FileChooser
        FileChooser chooser = new FileChooser();

        chooser.setTitle("Save chart as PNG");

        chooser.setInitialFileName("chart.png");

        /* chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PNG image", "*.png")
        ); */

        /*
            apre un dialogo separato, ma:
            •	È modale rispetto alla finestra owner: finché il dialogo è aperto non si può cliccare nel resto dell’app.
            •	Viene centrato (o "agganciato" come sheet su macOS) rispetto a quella finestra.
            •	Su macOS appare proprio come pannello di salvataggio collegato alla finestra, non come una finestra slegata.

        */
        File file = chooser.showSaveDialog(chartBox.getScene().getWindow());

        if (file == null) return;



        try {
            ChartExporter.saveNodeAsPng(chartBox, file);

        } catch (IOException e) {

            Dialogs.showNonFatalError(chartBox.getScene().getWindow(),
                "Export error",
                "Chart export failed.",
                """
                An unexpected error occurred during chart export.
                Please try again or choose a different location.""",
                e
            );

            
        }
    }

    
}
