package com.github.longkerdandy.evo.service.weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Forecast 3 day
 */
@SuppressWarnings("unused")
public class Forecast3Day {

    @JsonProperty("f1")
    private List<Forecast> forecast3d;
    @JsonProperty("f0")
    private String forecastTime;

    public List<Forecast> getForecast3d() {
        return forecast3d;
    }

    public void setForecast3d(List<Forecast> forecast3d) {
        this.forecast3d = forecast3d;
    }

    public String getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(String forecastTime) {
        this.forecastTime = forecastTime;
    }
}
