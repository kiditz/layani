package com.layani.pulsa.integration.utils;

public class Constant {
    public static class TransactionStatus{
        public static final String FAIL = "F";
        public static final String IN_PROGRESS = "I";
        public static final String SUCCESS = "S";
        public static final String CHECK_POST_PAID = "C";
    }

    public static class  Key{
        public static final String DEPOSIT_STATUS = "depositStatus";
    }

    public static class NotificationValue{
        public static final String TRX_SUCCESS = "trx.success";
        public static final String TRX_CHECK_POST_PAID = "trx.check.post_paid";
        public static final String TRX_IN_PROGRESS = "trx.in_progress";
        public static final String TRX_FAIL = "trx.fail";
        public static final String TRX_ERROR = "trx.error";
    }
}

