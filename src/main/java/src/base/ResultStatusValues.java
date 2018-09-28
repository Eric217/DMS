package src.base;

public interface ResultStatusValues {
    int V_OK = 200;
    int V_FAILURE = 300;
    /** 出现这种情况，一定是 非法调用接口，因此让去登陆。*/
    int V_PERMISSION_DENIED = 400;

}
