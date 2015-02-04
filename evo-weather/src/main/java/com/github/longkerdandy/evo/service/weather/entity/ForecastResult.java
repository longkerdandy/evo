package com.github.longkerdandy.evo.service.weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Forecast result from OpenWeather
 */
@SuppressWarnings("unused")
public class ForecastResult {

    @JsonProperty("c")
    private Area area;
    @JsonProperty("f")
    private Forecast3Day forecast3Day;

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Forecast3Day getForecast3Day() {
        return forecast3Day;
    }

    public void setForecast3Day(Forecast3Day forecast3Day) {
        this.forecast3Day = forecast3Day;
    }
}
