package com.layani.pulsa.service.constant;

public class ServiceConstant {
    public static final Long REFID_TRX = 0L;
    public static final Long REFID_DEPOSIT = 1L;
    public static final String CUT_DEPOSIT = "cut.deposit";
    public static final String ADD_DEPOSIT = "add.deposit";
    public static final String FAIL = "F";
    public static final String IN_PROGRESS = "I";
    public static final String SUCCESS = "S";
    
    //Deposit Remark

    public static String getReqid(Long orderId){
        return  String.format("%08d", orderId);
    }
}
