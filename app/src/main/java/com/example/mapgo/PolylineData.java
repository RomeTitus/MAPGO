package com.example.mapgo;

import androidx.annotation.Keep;

import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.maps.model.DirectionsLeg;


@IgnoreExtraProperties
public class PolylineData {



        public Polyline polyline;
    public DirectionsLeg directionsLeg;


        public PolylineData(Polyline polyline, DirectionsLeg directionsLeg) {
            this.polyline = polyline;
            this.directionsLeg = directionsLeg;
        }

        public Polyline getPolyline() {
            return polyline;
        }

        public void setPolyline(Polyline polyline) {
            this.polyline = polyline;
        }

        public DirectionsLeg getLeg() {
            return directionsLeg;
        }

        public void setLeg(DirectionsLeg directionsLeg) {
            this.directionsLeg = directionsLeg;
        }

        @Override
        public String toString() {
            return "PolylineData{" +
                    "polyline=" + polyline +
                    ", leg=" + directionsLeg +
                    '}';
        }

}




