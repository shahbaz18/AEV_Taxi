package aev.sec.com.aev.model;

/**
 * Created by Shahbaz on 2018-12-01.
 */

public class LoginRequestBody {

    private String userEmailId;
    private String userPassword;

    public LoginRequestBody(String userEmailId, String userPassword) {
        this.userEmailId = userEmailId;
        this.userPassword = userPassword;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
