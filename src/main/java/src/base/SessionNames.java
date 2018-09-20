package src.base;

public interface SessionNames {
    String S_USERNAME = "username";
    String S_PASSWORD = "password";
    /** BOOLEAN */
    String S_IS_ADMIN = "1";

    String S_VERI_LAST = "last_time";
    String S_VERI_CODE = "verify_code";
    String S_VERI_MAIL = "verify_mail";
    String S_VERI_IMG  = "verify_img";
    String S_VERI_TYPE = "verify_type"; // register: 0, reset pass: 1
}
