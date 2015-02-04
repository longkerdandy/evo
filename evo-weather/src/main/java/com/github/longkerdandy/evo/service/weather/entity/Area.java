package com.github.longkerdandy.evo.service.weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Area
 */
@SuppressWarnings("unused")
public class Area {

    @JsonProperty("c1")
    private String areaId;
    @JsonProperty("c2")
    private String areaEn;
    @JsonProperty("c3")
    private String areaCn;
    @JsonProperty("c4")
    private String cityEn;
    @JsonProperty("c5")
    private String cityCn;
    @JsonProperty("c6")
    private String stateEn;
    @JsonProperty("c7")
    private String stateCn;
    @JsonProperty("c8")
    private String countryEn;
    @JsonProperty("c9")
    private String countryCn;
    @JsonProperty("c10")
    private String areaLv;
    @JsonProperty("c11")
    private String areaCode;
    @JsonProperty("c12")
    private String postcode;
    @JsonProperty("c13")
    private String longitude;
    @JsonProperty("c14")
    private String latitude;
    @JsonProperty("c15")
    private String altitude;
    @JsonProperty("c16")
    private String radar;
    @JsonProperty("c17")
    private String timezone;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaEn() {
        return areaEn;
    }

    public void setAreaEn(String areaEn) {
        this.areaEn = areaEn;
    }

    public String getAreaCn() {
        return areaCn;
    }

    public void setAreaCn(String areaCn) {
        this.areaCn = areaCn;
    }

    public String getCityEn() {
        return cityEn;
    }

    public void setCityEn(String cityEn) {
        this.cityEn = cityEn;
    }

    public String getCityCn() {
        return cityCn;
    }

    public void setCityCn(String cityCn) {
        this.cityCn = cityCn;
    }

    public String getStateEn() {
        return stateEn;
    }

    public void setStateEn(String stateEn) {
        this.stateEn = stateEn;
    }

    public String getStateCn() {
        return stateCn;
    }

    public void setStateCn(String stateCn) {
        this.stateCn = stateCn;
    }

    public String getCountryEn() {
        return countryEn;
    }

    public void setCountryEn(String countryEn) {
        this.countryEn = countryEn;
    }

    public String getCountryCn() {
        return countryCn;
    }

    public void setCountryCn(String countryCn) {
        this.countryCn = countryCn;
    }

    public String getAreaLv() {
        return areaLv;
    }

    public void setAreaLv(String areaLv) {
        this.areaLv = areaLv;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getRadar() {
        return radar;
    }

    public void setRadar(String radar) {
        this.radar = radar;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
