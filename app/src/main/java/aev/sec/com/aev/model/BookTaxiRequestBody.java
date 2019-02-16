package aev.sec.com.aev.model;

/**
 * Created by Shahbaz on 2018-11-17.
 */

public class BookTaxiRequestBody {

    private String carSize;
    private String pickUpAdd;

    public String getCarSize() {
        return carSize;
    }

    public void setCarSize(String carSize) {
        this.carSize = carSize;
    }

    public String getPickUpAdd() {
        return pickUpAdd;
    }

    public void setPickUpAdd(String pickUpAdd) {
        this.pickUpAdd = pickUpAdd;
    }
}
