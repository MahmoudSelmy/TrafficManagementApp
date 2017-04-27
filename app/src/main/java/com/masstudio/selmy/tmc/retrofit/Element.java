package com.masstudio.selmy.tmc.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tech lap on 14/03/2017.
 */

public class Element {

    @SerializedName("distance")
    private Stats distance;
    @SerializedName("duration")
    private Stats duration;
    @SerializedName("duration_in_traffic")
    private Stats durationInTraffic;
    @SerializedName("status")
    private String status;

    public Element(Stats distance, Stats duration, Stats durationInTraffic, String status) {
        this.distance = distance;
        this.duration = duration;
        this.durationInTraffic = durationInTraffic;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Stats getDistance() {
        return distance;
    }

    public void setDistance(Stats distance) {
        this.distance = distance;
    }

    public Stats getDuration() {
        return duration;
    }

    public void setDuration(Stats duration) {
        this.duration = duration;
    }

    public Stats getDurationInTraffic() {
        return durationInTraffic;
    }

    public void setDurationInTraffic(Stats durationInTraffic) {
        this.durationInTraffic = durationInTraffic;
    }
}
