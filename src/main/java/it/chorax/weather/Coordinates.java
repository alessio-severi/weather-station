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

/**
 * Interfaccia di marker che raggruppa i tipi relativi alle coordinate
 * geografiche utilizzate dall'applicazione Weather Station.
 * <p>
 * Contiene l'enum {@link Coordinates.ItalianCapital}, che
 * rappresenta i capoluoghi di regione italiani supportati dal sistema.
 * </p>
 */
public interface Coordinates {

    /**
     * Elenco dei capoluoghi di regione italiani supportati
     * dall'applicazione.
     * <p>
     * Ogni costante incapsula:
     * </p>
     * <ul>
     *     <li>il nome della città in inglese;</li>
     *     <li>il nome della regione in inglese;</li>
     *     <li>latitudine in formato decimale (WGS84);</li>
     *     <li>longitudine in formato decimale (WGS84).</li>
     * </ul>
     * <p>
     * Questi valori vengono utilizzati per costruire la richiesta
     * verso il servizio open-meteo e la visualizzazione del report
     * meteorologico.
     * </p>
     */
    public enum ItalianCapital {


        ROMA       ("Rome",        "Lazio",                     "41.9028", "12.4964"),
        MILANO     ("Milan",       "Lombardy",                  "45.4642", "9.1900"),
        TORINO     ("Turin",       "Piedmont",                  "45.0703", "7.6869"),
        NAPOLI     ("Naples",      "Campania",                  "40.8518", "14.2681"),
        PALERMO    ("Palermo",     "Sicily",                    "38.1157", "13.3613"),
        CAGLIARI   ("Cagliari",    "Sardinia",                  "39.2238", "9.1217"),
        GENOVA     ("Genoa",       "Liguria",                   "44.4056", "8.9463"),
        BOLOGNA    ("Bologna",     "Emilia-Romagna",            "44.4949", "11.3426"),
        FIRENZE    ("Florence",    "Tuscany",                   "43.7696", "11.2558"),
        VENEZIA    ("Venice",      "Veneto",                    "45.4408", "12.3155"),
        BARI       ("Bari",        "Apulia",                    "41.1171", "16.8719"),
        LAQUILA    ("L'Aquila",    "Abruzzo",                   "42.3499", "13.3995"),
        ANCONA     ("Ancona",      "Marche",                    "43.6158", "13.5189"),
        PERUGIA    ("Perugia",     "Umbria",                    "43.1107", "12.3908"),
        CAMPOBASSO ("Campobasso",  "Molise",                    "41.5600", "14.6640"),
        POTENZA    ("Potenza",     "Basilicata",                "40.6401", "15.8050"),
        CATANZARO  ("Catanzaro",   "Calabria",                  "38.9098", "16.5877"),
        AOSTA      ("Aosta",       "Aosta Valley",              "45.7376", "7.3201"),
        TRENTO     ("Trento",      "Trentino-South Tyrol",      "46.0700", "11.1211"),
        TRIESTE    ("Trieste",     "Friuli-Venezia Giulia",     "45.6495", "13.7768");


        
        private final String cityName;
        private final String regionName;
        private final String latitude;
        private final String longitude;


        /**
         * Costruisce una costante che rappresenta un capoluogo italiano.
         *
         * @param cityName   nome della città (in inglese)
         * @param regionName nome della regione (in inglese)
         * @param latitude   latitudine in formato decimale (WGS84)
         * @param longitude  longitudine in formato decimale (WGS84)
         */
        ItalianCapital(String cityName,
                       String regionName,
                       String latitude,
                       String longitude) {

            this.cityName = cityName;
            this.regionName = regionName;
            this.latitude = latitude;
            this.longitude = longitude;
        }


        /**
         * Restituisce il nome della città.
         *
         * @return nome della città (in inglese).
         */
        public String getCityName() {
            return cityName;
        }

        /**
         * Restituisce il nome della regione.
         *
         * @return nome della regione (in inglese).
         */
        public String getRegionName() {
            return regionName;
        }

        /**
         * Restituisce la latitudine in formato decimale.
         *
         * @return latitudine (WGS84) come stringa.
         */
        public String getLatitude() {
            return latitude;
        }

        /**
         * Restituisce la longitudine in formato decimale.
         *
         * @return longitudine (WGS84) come stringa.
         */
        public String getLongitude() {
            return longitude;
        }


        /**
         * Restituisce la rappresentazione testuale della costante.
         * <p>
         * Convenzionalmente, la rappresentazione testuale coincide
         * con il nome della città.
         * </p>
         *
         * @return il nome della città.
         */
        @Override
        public String toString() {
            return cityName;
        }
    }


}