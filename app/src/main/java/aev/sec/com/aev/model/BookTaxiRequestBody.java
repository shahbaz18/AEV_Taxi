package aev.sec.com.aev.model;

/**
 * Created by Shahbaz on 2018-11-17.
 */

public class BookTaxiRequestBody {
    private boolean sedan1;
    private boolean sedan2;

    public boolean isSedan1() {
        return sedan1;
    }

    public void setSedan1(boolean sedan1) {
        this.sedan1 = sedan1;
    }

    public boolean isSedan2() {
        return sedan2;
    }

    public void setSedan2(boolean sedan2) {
        this.sedan2 = sedan2;
    }
}
