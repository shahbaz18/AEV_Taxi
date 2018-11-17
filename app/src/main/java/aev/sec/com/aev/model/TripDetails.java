package aev.sec.com.aev.model;

/**
 * Created by Shahbaz on 2018-11-15.
 */

public class TripDetails {

    private String completeFromToString;
    private float distance;
    private String pickUpLocation;
    private String dropOffLocation;
    private String totalTime;
    private String end;

    public String getCompleteFromToString() {
        return completeFromToString;
    }

    public void setCompleteFromToString(String completeFromToString) {
        this.completeFromToString = completeFromToString;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

}
