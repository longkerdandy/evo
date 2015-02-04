package com.github.longkerdandy.evo.service.weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Forecast
 */
@SuppressWarnings("unused")
public class Forecast {

    @JsonProperty("fa")
    private String dayWeather;
    @JsonProperty("fb")
    private String nightWeather;
    @JsonProperty("fc")
    private String dayTemp;
    @JsonProperty("fd")
    private String nightTemp;
    @JsonProperty("fe")
    private String dayWindDirection;
    @JsonProperty("ff")
    private String nightWindDirection;
    @JsonProperty("fg")
    private String dayWindForce;
    @JsonProperty("fh")
    private String nightWindForce;
    @JsonProperty("fi")
    private String sunrise;

    public String getDayWeather() {
        return dayWeather;
    }

    public void setDayWeather(String dayWeather) {
        this.dayWeather = dayWeather;
    }

    public String getNightWeather() {
        return nightWeather;
    }

    public void setNightWeather(String nightWeather) {
        this.nightWeather = nightWeather;
    }

    public String getDayTemp() {
        return dayTemp;
    }

    public void setDayTemp(String dayTemp) {
        this.dayTemp = dayTemp;
    }

    public String getNightTemp() {
        return nightTemp;
    }

    public void setNightTemp(String nightTemp) {
        this.nightTemp = nightTemp;
    }

    public String getDayWindDirection() {
        return dayWindDirection;
    }

    public void setDayWindDirection(String dayWindDirection) {
        this.dayWindDirection = dayWindDirection;
    }

    public String getNightWindDirection() {
        return nightWindDirection;
    }

    public void setNightWindDirection(String nightWindDirection) {
        this.nightWindDirection = nightWindDirection;
    }

    public String getDayWindForce() {
        return dayWindForce;
    }

    public void setDayWindForce(String dayWindForce) {
        this.dayWindForce = dayWindForce;
    }

    public String getNightWindForce() {
        return nightWindForce;
    }

    public void setNightWindForce(String nightWindForce) {
        this.nightWindForce = nightWindForce;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }
}
