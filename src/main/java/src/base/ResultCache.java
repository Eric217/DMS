package src.base;


public class ResultCache {
    public static final Result OK = new Result(ResultStatusValues.V_OK, ResultStatusMessage.M_OK);
    public static final Result FAILURE = new Result(ResultStatusValues.V_FAILURE, ResultStatusMessage.M_FAILURE);
    public static final Result PERMISSION_DENIED = new Result(ResultStatusValues.V_PERMISSION_DENIED, ResultStatusMessage.M_PERMISSION_DENIED);

    public static Result getDataOk(Object object){
        Result result = new Result(ResultStatusValues.V_OK, ResultStatusMessage.M_OK);
        result.setData(object);
        return result;
    }
    public static Result getFailureDetail(String message) {
        Result result = new Result(ResultStatusValues.V_FAILURE,message);
        return result;
    }
}
