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


public interface Converter {


    /**
     * Enum che rappresenta le unità gestite.
     *
     */
    enum Unit {
        
        CELSIUS("Celsius (°C)") {
            @Override
            String symbolOf() {
                return "°C";
            }
        },

        FAHRENHEIT("Fahrenheit (°F)") {
            @Override
            String symbolOf() {
                return "°F";
            }
        },

        KELVIN("Kelvin (K)") {
            @Override
            String symbolOf() {
                return "K";
            }
        },
        
        TEMPERATURE("CELSIUS-FAHRENHEIT-KELVIN") {

            /**
             * Conversione tra unità di temperatura.
             *
             */
            @Override
            double convertValue(double value, Unit from, Unit to) {

                // Prima converto tutto in Kelvin come unità "ponte"
                double inKelvin = switch (from) {
                    case CELSIUS    -> value + 273.15;
                    case FAHRENHEIT -> (value + 459.67) * 5.0 / 9.0;
                    case KELVIN     -> value;
                    default         -> 0;
                };

                if(inKelvin < 0) return Double.NaN;

                // Poi da Kelvin all'unità di destinazione
                return switch (to) {
                    case CELSIUS    -> inKelvin - 273.15;
                    case FAHRENHEIT -> inKelvin * 9.0 / 5.0 - 459.67;
                    case KELVIN     -> inKelvin;
                    default         -> 0;
                };
            }
        },


        PASCAL("Pascal (Pa)") {
            @Override
            String symbolOf() {
                return "Pa";
            }
        },

        HECTOPASCAL("Hectopascal (hPa)") {
            @Override
            String symbolOf() {
                return "hPa";
            }
        },

        MILLIMETERS_OF_MERCURY("Millimetres of mercury (mmHg)") {
            @Override
            String symbolOf() {
                return "mmHg";
            }
        },

        ATMOSPHERE("Atmosphere (atm)") {
            @Override
            String symbolOf() {
                return "atm";
            }
        },

        MILLIBAR("Millibar (mbar)") {
            @Override
            String symbolOf() {
                return "mbar";
            }
        },

        TORR("Torr (Torr)") {
            @Override
            String symbolOf() {
                return "Torr";
            }
        },

        PRESSURE("PASCAL-HECTOPASCAL-MILLIMETERS_OF_MERCURY-ATMOSPHERE-MILLIBAR-TORR") {

            /**
             * Conversione tra unità di temperatura.
             *
             */
            @Override
            double convertValue(double value, Unit from, Unit to) {

                // 1) Portiamo tutto in Pascal (Pa)
                double inPa = switch (from) {
                    case MILLIMETERS_OF_MERCURY, TORR -> value * 133.32236842105263;   // 1 mmHg ≈ 133.32236842105263 Pa
                    case ATMOSPHERE                 -> value * 101_325.0;            // 1 atm = 101325 Pa
                    case HECTOPASCAL, MILLIBAR      -> value * 100.0;                // 1 hPa = 1 mbar = 100 Pa
                    case PASCAL                    -> value;
                    default                        -> 0;
                };

                // 2) Da Pascal all’unità di destinazione
                return switch (to) {
                    case MILLIMETERS_OF_MERCURY, TORR -> inPa / 133.32236842105263;
                    case ATMOSPHERE                 -> inPa / 101_325.0;
                    case HECTOPASCAL, MILLIBAR      -> inPa / 100.0;
                    case PASCAL                    -> inPa;
                    default                        -> 0;
                };
            }

        },


        KILOMETER("Kilometre (km)") {
            @Override
            String symbolOf() {
                return "km";
            }
        },

        METER("Metre (m)") {
            @Override
            String symbolOf() {
                return "m";
            }
        },

        CENTIMETER("Centimetre (cm)") {
            @Override
            String symbolOf() {
                return "cm";
            }
        },

        MILLIMETER("Millimetre (mm)") {
            @Override
            String symbolOf() {
                return "mm";
            }
        },

        MILE("Mile (mi)") {
            @Override
            String symbolOf() {
                return "mi";
            }
        },

        YARD("Yard (yd)") {
            @Override
            String symbolOf() {
                return "yd";
            }
        },

        FOOT("Foot (ft)") {
            @Override
            String symbolOf() {
                return "ft";
            }
        },

        INCH("Inch (in)") {
            @Override
            String symbolOf() {
                return "in";
            }
        },

        NAUTICAL_MILE("Nautical mile (NM)") {
            @Override
            String symbolOf() {
                return "NM";
            }
        },

        LENGTH("KILOMETER-METER-CENTIMETER-MILLIMETER-MILE-YARD-FOOT-INCH-NAUTICAL_MILE") {

            /**
             * Conversione tra unità di temperatura.
             *
             */
            @Override
            double convertValue(double value, Unit from, Unit to) {

                // 1) Porto tutto in metri (m)
                double inMeters = switch (from) {
                    case METER           -> value;
                    case KILOMETER      -> value * 1_000.0;
                    case CENTIMETER      -> value / 100.0;
                    case MILLIMETER      -> value / 1_000.0;
                    case MILE         -> value * 1_609.344;   // 1 mi  = 1609.344 m
                    case YARD           -> value * 0.9144;      // 1 yd  = 0.9144 m
                    case FOOT           -> value * 0.3048;      // 1 ft  = 0.3048 m
                    case INCH         -> value * 0.0254;      // 1 in  = 0.0254 m
                    case NAUTICAL_MILE  -> value * 1_852.0;     // 1 NM  = 1852 m
                    default              -> 0;
                };

                // 2) Dai metri all’unità di destinazione
                return switch (to) {
                    case METER           -> inMeters;
                    case KILOMETER      -> inMeters / 1_000.0;
                    case CENTIMETER      -> inMeters * 100.0;
                    case MILLIMETER      -> inMeters * 1_000.0;
                    case MILE          -> inMeters / 1_609.344;
                    case YARD           -> inMeters / 0.9144;
                    case FOOT           -> inMeters / 0.3048;
                    case INCH         -> inMeters / 0.0254;
                    case NAUTICAL_MILE  -> inMeters / 1_852.0;
                    default              -> 0;
                };
            }
        },


            
        METERS_PER_SECOND("Metres per second (m/s)") {
            @Override
            String symbolOf() {
                return "m/s";
            }
        },

        KILOMETERS_PER_HOUR("Kilometres per hour (km/h)") {
            @Override
            String symbolOf() {
                return "km/h";
            }
        },

        KNOTS("Knots (kn)") {
            @Override
            String symbolOf() {
                return "kn";
            }
        },

        MILES_PER_HOUR("Miles per hour (mph)") {
            @Override
            String symbolOf() {
                return "mph";
            }
        },

        FEET_PER_SECOND("Feet per second (ft/s)") {
            @Override
            String symbolOf() {
                return "ft/s";
            }
        },

        SPEED("METERS_PER_SECOND-KILOMETERS_PER_HOUR-KNOTS-MILES_PER_HOUR-FEET_PER_SECOND") {

            /**
             * Conversione tra unità di temperatura.
             *
             */
            @Override
            public double convertValue(double value, Unit from, Unit to) {
                

                // 1) Portiamo tutto in metri al secondo (m/s)
                double inMs = switch (from) {
                    case METERS_PER_SECOND -> value;
                    case KILOMETERS_PER_HOUR -> value / 3.6;          // 1 m/s = 3.6 km/h
                    case KNOTS             -> value * 0.5144444444; // 1 kn = 0.514444... m/s
                    case MILES_PER_HOUR    -> value * 0.44704;      // 1 mph = 0.44704 m/s (esatto)
                    case FEET_PER_SECOND -> value * 0.3048;       // 1 ft/s = 0.3048 m/s (esatto)
                    default               -> 0;
                };

                // 2) Da m/s all’unità di destinazione
                return switch (to) {
                    case METERS_PER_SECOND -> inMs;
                    case KILOMETERS_PER_HOUR -> inMs * 3.6;
                    case KNOTS             -> inMs / 0.5144444444;
                    case MILES_PER_HOUR    -> inMs / 0.44704;
                    case FEET_PER_SECOND -> inMs / 0.3048;
                    default               -> 0;
                };
            }

        };


        double convertValue(double value, Unit from, Unit to) {
            return Double.NaN;
        }


        String symbolOf() {
            return "[u]";
        }


        private final String label;

        Unit(String label) {
            this.label = label;
        }


        String label(){

            return label;
        }



        @Override
        public String toString() {
            return label;
        }

    }

}