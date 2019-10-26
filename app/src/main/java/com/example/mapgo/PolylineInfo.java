package com.example.mapgo;

import java.util.List;

public class PolylineInfo {

    private List<String> PolylineDataPointGoogleMap;
    private String UserStartTime,UserEndTime;

    public String getUserStartTime() {
        return UserStartTime;
    }

    public void setUserStartTime(String userStartTime) {
        UserStartTime = userStartTime;
    }

    public String getUserEndTime() {
        return UserEndTime;
    }

    public void setUserEndTime(String userEndTime) {
        UserEndTime = userEndTime;
    }

    public List<String> getPolylineDataPointGoogleMap() {
        return PolylineDataPointGoogleMap;
    }

    public void setPolylineDataPointGoogleMap(List polylineDataPointGoogleMap) {
        PolylineDataPointGoogleMap = polylineDataPointGoogleMap;
    }
}
