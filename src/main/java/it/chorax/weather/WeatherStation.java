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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.kordamp.ikonli.javafx.FontIcon;

import it.chorax.app.WeatherForecast;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


class WeatherStation implements Coordinates {


    private final ItalianCapital city;

    private final List<String> dataList = new ArrayList<>();

    private FontIcon icon;


    WeatherStation(ItalianCapital city) {

        
        this.city = city;

    }


    public List<String> getDataList() {
        return List.copyOf(dataList);
    }


    private StringBuilder connect() throws Exception {


        StringBuilder response = new StringBuilder();

        String urlString =
                "https://api.open-meteo.com/v1/forecast"
              + "?latitude=" + city.getLatitude()
              + "&longitude=" + city.getLongitude()
              + "&timezone=Europe%2FRome"
              + "&daily=sunrise,sunset,temperature_2m_max,temperature_2m_min,"
              + "surface_pressure_max,surface_pressure_min,"
              + "relative_humidity_2m_max,relative_humidity_2m_min,"
              + "wind_speed_10m_max,wind_speed_10m_min,wind_direction_10m_dominant,"
              + "precipitation_probability_max,precipitation_probability_min,"
              + "apparent_temperature_max,apparent_temperature_min,"
              + "pressure_msl_max,pressure_msl_min,uv_index_max"
              + "&current=temperature_2m,apparent_temperature,"
              + "dewpoint_2m,surface_pressure,pressure_msl,relative_humidity_2m,"
              + "wind_speed_10m,wind_direction_10m,weather_code,"
              + "cloudcover,visibility,precipitation,uv_index,snowfall";
              


        try {

            URI uri = URI.create(urlString);
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);

            int status = conn.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {

                // HTTP error (es. 404, 500, …)
                throw new WeatherServiceException(
                    "The weather service returned an error (HTTP " + status + ").\n" +
                    "Please try again in a few minutes."
                );
            }


            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {

                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

            } finally {
                conn.disconnect();
            }

            
            // Risposta vuota
            if (response.length() == 0) {

                throw new WeatherServiceException(
                    """
                    The weather service returned an empty response.
                    Please try again in a few minutes.""");
            }

        } catch (java.net.UnknownHostException e) {

            // DNS / nessuna connessione
            throw new WeatherServiceException(
                """
                Unable to reach the weather service.
                Please check your Internet connection and try again.""",
                e
            );

        } catch (java.net.SocketTimeoutException e) {

            // Timeout
            throw new WeatherServiceException(
                """
                The weather service took too long to respond.
                Please try again in a few minutes.""",
                e
            );

        } catch (IOException e) {
            
            // Altri problemi I/O
            throw new WeatherServiceException(
                """
                An unexpected error occurred during the download of weather data.
                Please try again.""",
                e
            );
        }

        return response;

    }


    private void buildForecastDataMap(String jsonDaily, WeatherForecast forecastMisure){

        String[] markerArray = new String[] {

                                "\"time\":[",
                                "\"temperature_2m_max\":[",
                                "\"temperature_2m_min\":[",
                                "\"apparent_temperature_max\":[",
                                "\"apparent_temperature_min\":[",
                                "\"surface_pressure_max\":[",
                                "\"surface_pressure_min\":[",
                                "\"pressure_msl_max\":[",
                                "\"pressure_msl_min\":[",
                                "\"relative_humidity_2m_max\":[",
                                "\"relative_humidity_2m_min\":[",
                                "\"wind_speed_10m_max\":[",
                                "\"wind_speed_10m_min\":[",
                                "\"wind_direction_10m_dominant\":[",
                                "\"precipitation_probability_max\":[",
                                "\"precipitation_probability_min\":["
                                
                            };


        int idx;
        int start;
        int end;
        String tempStr;
        

        for (String marker : markerArray) {

            idx = jsonDaily.lastIndexOf(marker);

            start = idx + marker.length();
            end = jsonDaily.indexOf("]", start);   // fine del numero
            tempStr = jsonDaily.substring(start, end);
                
                
            List<String> valueList =new ArrayList<>(Arrays.asList(tempStr.split(",")));
            String misureName = marker.replaceAll("(\"|2m_|10m_|:\\[)", "")
                                      .replace("_", " ");

                
            forecastMisure.getMisures().put(misureName, valueList);


        }

        forecastMisure.setCity(city.getCityName() + ",     " + city.getRegionName() + "     ( IT )");

        

    }


    private String buildCurrentDataList(StringBuilder response){

        
        int index = response.indexOf("\"daily_units\"");
        String jsonCurrent = response.toString().substring(0, index);
        String jsonDaily = response.toString().substring(index);

        // System.out.println(jsonCurrent);
        // System.out.println(jsonDaily);
        

        String marker = "\"time\":";
        int hour24 = formatDateTime((extractData(marker, "time", jsonCurrent)));


        marker = "\"sunrise\":";
        int sunriseHour24 = formatDailyTime(extractData(marker, "sunrise", jsonDaily), "sunrise");

        marker = "\"sunset\":";
        int sunsetHour24 = formatDailyTime(extractData(marker, "sunset", jsonDaily), "sunset");

        marker = "\"temperature_2m_max\":";
        dataList.add(extractData(marker, "Max temp", jsonDaily));

        marker = "\"temperature_2m_min\":";
        dataList.add(extractData(marker, "Min temp", jsonDaily));


        marker = "\"weather_code\":";
        dataList.add(extractData(marker, "weather", jsonCurrent, hour24, sunriseHour24, sunsetHour24));


        marker = "\"temperature_2m\":";
        dataList.add(extractData(marker, "temperature", jsonCurrent));
            
        marker = "\"apparent_temperature\":";
        dataList.add(extractData(marker, "apparent temperature", jsonCurrent));


        marker = "\"surface_pressure\":";
        dataList.add(extractData(marker, "surface pressure", jsonCurrent));

        marker = "\"relative_humidity_2m\":";
        dataList.add(extractData(marker, "relative humidity", jsonCurrent));


        marker = "\"wind_speed_10m\":";
        dataList.add(extractData(marker, "wind speed", jsonCurrent));

        marker = "\"wind_direction_10m\":";
        dataList.add(extractData(marker, "wind direction", jsonCurrent));


        marker = "\"visibility\":";
        dataList.add(extractData(marker, "visibility", jsonCurrent));

        marker = "\"cloudcover\":";
        dataList.add(extractData(marker, "cloudcover", jsonCurrent));


        marker = "\"dewpoint_2m\":";
        dataList.add(extractData(marker, "dew point", jsonCurrent));

        marker = "\"precipitation\":";
        dataList.add(extractData(marker, "precipitation", jsonCurrent));


        marker = "\"uv_index\":";
        dataList.add(extractData(marker, "UV index", jsonCurrent));

        return jsonDaily;
    }


    private String extractData(String marker, String msg, String json, int... hours){

        int idx;
        String value = "";
        String tempStr;
        int stop = 2;

        
        OUTER:
        for (int i = 0; i < stop; i++) {

            if(i==0) idx = json.lastIndexOf(marker);
            else idx = json.indexOf(marker);

            if (idx != -1) {
                int start = idx + marker.length();
                int end = json.indexOf(",", start);   // fine del numero

                tempStr = json.substring(start, end);

                if(i==0) value = Character.toUpperCase(msg.charAt(0)) + msg.substring(1) + ":   " + tempStr;
                else {

                    if(tempStr.charAt(1) != '°' && tempStr.charAt(1) != '%') value = value + " ";
                    value = value + tempStr.substring(1, tempStr.length() - 1) + "\000";
                    
                }

            } else
        
                return "Field '" + msg + "' not found in JSON.";

            if (i==0)
                switch (marker) {
                    case "\"time\":" :
                        marker = "\"timezone_abbreviation\":";
                        break;
            
                    case "\"weather_code\":" :
                        value = value.replace(tempStr, describeWeatherCode(Integer.parseInt(tempStr), hours));
                        break OUTER;
                    
                    case "\"sunrise\":", "\"sunset\":" :
                        i++;
                        value = value.replace("\"", "");

                    case "\"temperature_2m_max\":", "\"temperature_2m_min\":" :
                        value = value.replace("[", "");

                }
            else if(marker.equals("\"timezone_abbreviation\":")) {
                    
                value = value.replace("\"", "")
                             .replace("\000", "")
                             .replace("Time:   ", "");

                char c = value.charAt(value.length() - 1);
                value = value.replace(" GMT+" + c , ":00+0" + c + ":00");
                                     
                marker = "\"timezone\":";
                stop = 3;

            }
        }

        return value;
    }


    private String describeWeatherCode(int code, int[] hours) {

        boolean moon = !(hours[0] >= hours[1] && hours[0] < hours[2]);

        String iconName;

        String text = switch (code) {

            // 0 – Clear sky
            case 0  -> {
                iconName = moon ? "wi-night-clear" : "wi-day-sunny";
                yield "Clear sky";
            }

            // 1 – Mainly clear
            case 1  -> {
                iconName = moon ? "wi-night-cloudy-high" : "wi-day-cloudy-high";
                yield "Mainly clear";
            }

            // 2 – Partly cloudy
            case 2  ->  {
                iconName = moon ? "wi-night-cloudy" : "wi-day-cloudy";
                yield "Partly cloudy";
            }

            // 3 – Overcast
            case 3  -> {
                iconName = "wi-cloudy";              // uguale per giorno/notte
                yield "Overcast";
            }

            // 45 – Fog
            case 45 -> {
                iconName = moon ? "wi-night-fog" : "wi-day-fog";
                yield "Fog";
            }

            // 48 – Depositing rime fog
            case 48 -> {
                iconName = "wi-fog";
                yield "Depositing rime fog";
            }

            // 51 – Light drizzle
            case 51 -> {
                iconName = moon ? "wi-night-alt-sprinkle" : "wi-day-sprinkle";
                yield "Light drizzle";
            }

            // 53 – Moderate drizzle
            case 53 -> {
                iconName = moon ? "wi-night-sprinkle" : "wi-day-sprinkle";
                yield "Moderate drizzle";
            }

            // 55 – Dense drizzle --
            case 55 -> {
                iconName = moon ? "wi-night-alt-sprinkle" : "wi-day-sprinkle";
                yield "Dense drizzle";
            }

            // 56 – Light freezing drizzle
            case 56 -> {
                iconName = moon ? "wi-night-alt-sleet" : "wi-day-sleet";
                yield "Light freezing drizzle";
            }

            // 57 – Dense freezing drizzle
            case 57 -> {
                iconName = moon ? "wi-night-sleet" : "wi-day-sleet";
                yield "Dense freezing drizzle";
            }

            // 61 – Slight rain
            case 61 -> {
                iconName = moon ? "wi-night-alt-sprinkle" : "wi-day-sprinkle";
                yield "Slight rain";
            }

            // 63 – Moderate rain
            case 63 -> {
                iconName = moon ? "wi-night-alt-rain" : "wi-day-rain";
                yield "Moderate rain";
            }

            // 65 – Heavy rain
            case 65 -> {
                iconName = moon ? "wi-night-rain" : "wi-day-rain";
                yield "Heavy rain";
            }

            // 66 – Light freezing rain
            case 66 -> {
                iconName = moon ? "wi-night-alt-rain-mix" : "wi-day-rain-mix";
                yield "Light freezing rain";
            }

            // 67 – Heavy freezing rain
            case 67 -> {
                iconName = moon ? "wi-night-alt-hail" : "wi-day-hail";
                yield "Heavy freezing rain";
            }

            // 71 – Slight snowfall
            case 71 -> {
                iconName = moon ? "wi-night-alt-snow" : "wi-day-snow";
                yield "Slight snowfall";
            }

            // 73 – Moderate snowfall
            case 73 -> {
                iconName = moon ? "wi-night-alt-snow-wind" : "wi-day-snow-wind";
                yield "Moderate snowfall";
            }

            // 75 – Heavy snowfall
            case 75 -> {
                iconName = moon ? "wi-night-snow-wind" : "wi-day-snow-wind";
                yield "Heavy snowfall";
            }

            // 77 – Snow grains
            case 77 -> {
                iconName = moon ? "wi-night-snow" : "wi-day-snow";
                yield  "Snow grains";
            }

            // 80 – Slight rain showers
            case 80 -> {
                iconName = moon ? "wi-night-alt-showers" : "wi-day-showers";
                yield "Slight rain showers";
            }

            // 81 – Moderate rain showers
            case 81 -> {
                iconName = moon ? "wi-night-showers" : "wi-day-showers";
                yield "Moderate rain showers";
            }

            // 82 – Violent rain showers
            case 82 -> {
                iconName = moon ? "wi-night-alt-storm-showers" : "wi-day-storm-showers";
                yield "Violent rain showers";
            }

            // 85 – Slight snow showers
            case 85 -> {
                iconName = moon ? "wi-night-alt-snow-wind" : "wi-day-snow-wind";
                yield "Slight snow showers";
            }

            // 86 – Heavy snow showers --
            case 86 -> {
                iconName = moon ? "wi-night-alt-rain-wind" : "wi-day-rain-wind";
                yield "Heavy snow showers";
            }

            // 95 – Thunderstorm (no hail)
            case 95 -> {
                iconName = moon ? "wi-night-alt-thunderstorm" : "wi-day-thunderstorm";
                yield "Thunderstorm";
            }

            // 96 – Thunderstorm with slight hail
            case 96 -> {
                iconName = moon ? "wi-night-alt-sleet-storm" : "wi-night-alt-sleet-storm";
                yield "Thunderstorm with slight hail";
            }

            // 99 – Thunderstorm with heavy hail
            case 99 -> {
                iconName = moon ? "wi-night-alt-snow-thunderstorm" : "wi-day-snow-thunderstorm";
                yield "Thunderstorm with heavy hail";
            }

            // default
            default -> {
                iconName = moon ? "wi-night-alt-cloudy" : "wi-day-cloudy";
                yield "Unknown weather code: " + code;
            }
        };

        icon = new FontIcon(iconName);
        icon.setIconSize(20);
        icon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(icon, new Insets(2, 8, 0, 0));

        return text;

    }


    private int formatDateTime(String dateTime){

   
        dateTime = dateTime.replace("\000", "]")
                           .replace(" ", "[");

        
        ZonedDateTime zone = ZonedDateTime.parse(dateTime);

        DateTimeFormatter format = DateTimeFormatter.ofPattern("EEEE dd MMMM YYYY  HH:mm (O, VV)", Locale.ENGLISH);
        String st = zone.format(format);


        dataList.add(st.replace(st.charAt(0), Character.toUpperCase(st.charAt(0))));
       

        return Integer.parseInt(zone.format(DateTimeFormatter.ofPattern("HH")));

    }


    private int formatDailyTime(String dailyTime, String dailyText){

        String[] array = dailyTime.split("   ");

        LocalDateTime time = LocalDateTime.parse(array[1] + ":00");

        String hours = time.format(DateTimeFormatter.ofPattern("HH:mm"));


        dataList.add(array[0] + "    " + hours);


        if(dailyText.equals("sunrise"))
            return Integer.parseInt(time.format(DateTimeFormatter.ofPattern("HH")));

        else
            return Integer.parseInt(time.format(DateTimeFormatter.ofPattern("HH")));


    }


    public void formatReport(VBox reportBox){

        // setGraphic

        // locazione
        Label lb1 = new Label(city.getCityName() + ",     " + city.getRegionName() + "     ( IT )");
        lb1.getStyleClass().add("report-titolo");
        VBox.setMargin(lb1, new Insets(0, 0, 0, 100));

        FontIcon cityIcon = new FontIcon("mdi2m-map-marker");
        cityIcon.setIconSize(20);
        cityIcon.setIconColor(Color.web("#ffffffff"));          // "#b40101ff"
        HBox.setMargin(cityIcon, new Insets(10, 0, 0, 5));

        HBox cityRow = new HBox(8);                 // spaziatura orizzontale
        cityRow.setAlignment(Pos.TOP_LEFT);      // allineamento
        HBox.setMargin(lb1, new Insets(0, 0, 0, 100));
        cityRow.getChildren().addAll(lb1, cityIcon);



        // data-ora-fuso
        String tempString = dataList.get(0).replace(" ", "    ")
                                   .replace("(G", "( G")
                                   .replace("e)", "e )") + "\n\n\n";

        Label lb2 = new Label(tempString);
        lb2.getStyleClass().add("report-value");
        VBox.setMargin(lb2, new Insets(0, 0, 0, 100));


        // codice dataList.get(5) + "\n\n" //codice
        String[] array = (dataList.get(5).replace("   ", "      #").split("#"));

        Label lb3a = new Label(array[0]);
        Label lb3b = new Label(array[1]);
        lb3a.getStyleClass().add("report-text");
        lb3b.getStyleClass().add("report-value");

        
        HBox codeRow = new HBox(8);
        HBox.setMargin(lb3a, new Insets(0, 0, 0, 100));
        codeRow.setAlignment(Pos.TOP_LEFT);
        codeRow.getChildren().addAll(lb3a, icon, lb3b);

     
        

        // sunrise/sunset
        array = (dataList.get(1)).replace(": ", ":#") .split("#");
        String[] array2 = ("         •         " + dataList.get(2)).replace(": ", ":#") .split("#");

        
        Label lb4a1 = new Label(array[0]);
        Label lb4a2 = new Label(array[1]);
        Label lb4b1 = new Label(array2[0]);
        Label lb4b2 = new Label(array2[1]);
        

        lb4a1.getStyleClass().add("report-text");
        lb4a2.getStyleClass().add("report-value");
        lb4b1.getStyleClass().add("report-text");
        lb4b2.getStyleClass().add("report-value");
       

        FontIcon sunriseIcon = new FontIcon("wi-horizon-alt");
        sunriseIcon.setIconSize(20);
        sunriseIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(sunriseIcon, new Insets(3, 0, 0, 16));

        FontIcon sunsetIcon = new FontIcon("wi-horizon");
        sunsetIcon.setIconSize(20);
        sunsetIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(sunsetIcon, new Insets(3, 0, 0, 19));


        HBox sunRow = new HBox(8);                 // spaziatura orizzontale
        sunRow.setAlignment(Pos.TOP_LEFT);                  // allineamento
        HBox.setMargin(lb4a1, new Insets(0, 0, 0, 100));
        sunRow.getChildren().addAll(lb4a1, sunriseIcon, lb4a2, lb4b1, sunsetIcon, lb4b2);



        // Tmax-Tmin
        array = ((dataList.get(3)).replace(": ", ":#")).split("#");
        array2 = (("         •         " + dataList.get(4) + "\n\n").replace(": ", ":#")).split("#");

        
        Label lb5a1 = new Label(array[0]);
        Label lb5a2 = new Label(array[1]);
        Label lb5b1 = new Label(array2[0]);
        Label lb5b2 = new Label(array2[1]);
        

        lb5a1.getStyleClass().add("report-text");
        lb5a2.getStyleClass().add("report-value");
        lb5b1.getStyleClass().add("report-text");
        lb5b2.getStyleClass().add("report-value");
       

        // FontIcon tempMaxIcon = new FontIcon("mdi2t-thermometer-plus");
        FontIcon tempMaxIcon = new FontIcon("mdi2g-gauge");
        tempMaxIcon.setIconSize(22);
        tempMaxIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(tempMaxIcon, new Insets(7, 9, 0, 22));

        // FontIcon tempMinIcon = new FontIcon("mdi2t-thermometer-minus");
        FontIcon tempMinIcon = new FontIcon("mdi2g-gauge-low");
        tempMinIcon.setIconSize(22);
        tempMinIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(tempMinIcon, new Insets(7, 9, 0, 22));


        HBox tempMaxMinRow = new HBox(0);                 // spaziatura orizzontale
        tempMaxMinRow.setAlignment(Pos.TOP_LEFT);                  // allineamento
        HBox.setMargin(lb5a1, new Insets(0, 0, 0, 100));
        tempMaxMinRow.getChildren().addAll(lb5a1, tempMaxIcon, lb5a2, lb5b1, tempMinIcon, lb5b2);




        // ---------- TEMPERATURA 🌡️ ----------
        array = (dataList.get(6).replace("   ", "#") + "    ( a"+ dataList.get(7)
                                                .replace(" temperature: ","").substring(1) + " )")
                                                .replace("t ","t    ").split("#");
        
        
        Label lb6 = new Label(array[0]);  // testo prima
        Label lb7 = new Label(array[1]);  // testo dopo
        lb6.getStyleClass().add("report-text");
        lb7.getStyleClass().add("report-value");


        FontIcon tempIcon = new FontIcon("wi-thermometer");
        tempIcon.setIconSize(20);
        tempIcon.setIconColor(Color.web("#ffffffff"));          // "#b40101ff"
        HBox.setMargin(tempIcon, new Insets(3, 8, 0, 19));

        HBox tempRow = new HBox(8);                 // spaziatura orizzontale
        tempRow.setAlignment(Pos.TOP_LEFT);         // allineamento
        HBox.setMargin(lb6, new Insets(0, 0, 0, 100));
        tempRow.getChildren().addAll(lb6, tempIcon, lb7);


       // ---------- PRESSIONE 🌡️ ----------
        array = (dataList.get(8).replace("   ", "#") ).split("#");

        Label lb8 = new Label(array[0]);
        Label lb9 = new Label(array[1]);
        lb8.getStyleClass().add("report-text");
        lb9.getStyleClass().add("report-value");

        FontIcon pressureIcon = new FontIcon("wi-barometer");
        pressureIcon.setIconSize(20);
        pressureIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(pressureIcon, new Insets(2, 8, 0, 19));

        HBox pressureRow = new HBox(8);
        HBox.setMargin(lb8, new Insets(0, 0, 0, 100));
        pressureRow.setAlignment(Pos.TOP_LEFT);
        pressureRow.getChildren().addAll(lb8, pressureIcon, lb9);


        // ---------- UMIDITÀ 🌡️ ----------
        array = (dataList.get(9).replace("   ", "#") ).split("#");

        Label lb10 = new Label(array[0]);
        Label lb11 = new Label(array[1]);
        lb10.getStyleClass().add("report-text");
        lb11.getStyleClass().add("report-value");

        FontIcon humidityIcon = new FontIcon("wi-humidity");
        humidityIcon.setIconSize(20);
        humidityIcon.setIconColor(Color.web("#ffffffff"));      // "#10139cff"
        HBox.setMargin(humidityIcon, new Insets(2, 8, 0, 19));

        HBox humidityRow = new HBox(8);
        humidityRow.setAlignment(Pos.TOP_LEFT);
        HBox.setMargin(lb10, new Insets(0, 0, 0, 100));
        humidityRow.getChildren().addAll(lb10, humidityIcon, lb11);

           
        // ---------- VENTO 🌬️ ----------
        array = (dataList.get(10).replace(" speed", "")
                         .replace("   ", "#") + dataList.get(11)
                         .replace("Wind direction:  ", "     from    ")
                         + "\n\n").split("#");

        Label lb12 = new Label(array[0]);
        Label lb13 = new Label(array[1]);
        lb12.getStyleClass().add("report-text");
        lb13.getStyleClass().add("report-value");

        FontIcon windIcon = new FontIcon("wi-strong-wind");
        windIcon.setIconSize(20);
        windIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(windIcon, new Insets(2, 8, 0, 19));

        HBox windRow = new HBox(8);
        windRow.setAlignment(Pos.TOP_LEFT);
        HBox.setMargin(lb12, new Insets(0, 0, 0, 100));
        windRow.getChildren().addAll(lb12, windIcon, lb13);


        // a capo
        // Label lb13b = new Label("");
        // lb13b.getStyleClass().add("report-text");

        // ---------- VISIBILITÀ ----------
        array = (dataList.get(12)).replace(": ", ":#") .split("#");
        array2 = ("         •         " + dataList.get(13)).replace(": ", ":#") .split("#");
        

        Label lb14a1 = new Label(array[0]);
        Label lb14a2 = new Label(array[1]);
        Label lb14b1 = new Label(array2[0]);
        Label lb14b2 = new Label(array2[1]);
        

        lb14a1.getStyleClass().add("report-text");
        lb14a2.getStyleClass().add("report-value");
        lb14b1.getStyleClass().add("report-text");
        lb14b2.getStyleClass().add("report-value");
       

        FontIcon viewIcon = new FontIcon("mdi2e-eye-outline");
        viewIcon.setIconSize(22);
        viewIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(viewIcon, new Insets(9, 2, 0, 19));

        FontIcon cloudIcon = new FontIcon("wi-cloud");
        cloudIcon.setIconSize(20);
        cloudIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(cloudIcon, new Insets(4, 4, 0, 19));


        HBox viewRow = new HBox(8);                 // spaziatura orizzontale
        viewRow.setAlignment(Pos.TOP_LEFT);                  // allineamento
        HBox.setMargin(lb14a1, new Insets(0, 0, 0, 100));
        viewRow.getChildren().addAll(lb14a1, viewIcon, lb14a2, lb14b1, cloudIcon, lb14b2);



       // ---------- DEW POINT ----------
        array = (dataList.get(14)).replace(": ", ":#").split("#");
        array2 = ("         •         " + dataList.get(15) + "\n\n").replace(": ", ":#").split("#");
        
        
        Label lb15a1 = new Label(array[0]);
        Label lb15a2 = new Label(array[1]);
        Label lb15b1 = new Label(array2[0]);
        Label lb15b2 = new Label(array2[1]);
        

        lb15a1.getStyleClass().add("report-text");
        lb15a2.getStyleClass().add("report-value");
        lb15b1.getStyleClass().add("report-text");
        lb15b2.getStyleClass().add("report-value");
       

        FontIcon dewPintIcon = new FontIcon("wi-raindrops");
        dewPintIcon.setIconSize(22);
        dewPintIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(dewPintIcon, new Insets(6, 4, 0, 19));

        FontIcon rainIcon = new FontIcon("wi-umbrella");
        rainIcon.setIconSize(20);
        rainIcon.setIconColor(Color.web("#ffffffff"));
        HBox.setMargin(rainIcon, new Insets(4, 4, 0, 18));


        HBox dewPintRow = new HBox(8);                 // spaziatura orizzontale
        dewPintRow.setAlignment(Pos.TOP_LEFT);                  // allineamento
        HBox.setMargin(lb15a1, new Insets(0, 0, 0, 100));
        dewPintRow.getChildren().addAll(lb15a1, dewPintIcon, lb15a2, lb15b1, rainIcon, lb15b2);



        // ---------- RADIAZIONE / SOLE 🔆 ----------
        array = (dataList.get(16).replace("   ", "#")).split("#");

        Label lb16 = new Label(array[0]);
        Label lb17 = new Label(array[1]);
        lb16.getStyleClass().add("report-text");
        lb17.getStyleClass().add("report-value");
                             
        FontIcon sunIcon = new FontIcon("mdi2v-vanish");
        sunIcon.setIconSize(25);
        sunIcon.setIconColor(Color.web("#ffffffff")); //"#f9dc00ff"
        HBox.setMargin(sunIcon, new Insets(5, 5, 0, 14));

        HBox radiationRow = new HBox(8);
        radiationRow.setAlignment(Pos.TOP_LEFT);
        HBox.setMargin(lb16, new Insets(0, 0, 0, 100));
        radiationRow.getChildren().addAll(lb16, sunIcon, lb17);
        


        // ---------- AGGIUNTA AL VBOX DEL REPORT ----------
        reportBox.getChildren().clear();

        reportBox.getChildren().addAll(
                cityRow,
                lb2,
                codeRow,
                sunRow,
                tempMaxMinRow,
                tempRow,
                pressureRow,
                humidityRow,
                windRow,
                viewRow,
                dewPintRow,
                radiationRow
        );

        
    }

    

    void buildReport(VBox reportBox, WeatherForecast forecastMisure) throws Exception {
        
        StringBuilder response = connect();
            
        String jsonDaily = buildCurrentDataList(response);
        buildForecastDataMap(jsonDaily, forecastMisure);
            
        formatReport(reportBox);
        
    }

    

}