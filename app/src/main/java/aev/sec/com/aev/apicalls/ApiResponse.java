package aev.sec.com.aev.apicalls;

public class ApiResponse<T> {

    private String error;
    private T result;
    private boolean success;

    public ApiResponse() {

    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
