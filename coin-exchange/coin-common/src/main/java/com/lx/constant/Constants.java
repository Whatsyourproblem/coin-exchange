package com.lx.constant;

/**
 * 常用的常量
 */
public class Constants {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;


    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;

    public static final String UNKNOWN_USER = "未知用户";


    /**
     * ===========================
     * GCN充值头
     * ===========================
     */

    /**
     * GCN充值头
     */
    public static final String[] CASH_RECHARGE_HEADERS =
            {"ID", "用户ID", "用户名", "真实用户名", "充值币种", "充值金额(USDT)", "手续费", "到账金额(CNY)", "充值方式",
                    "充值订单", "参考号", "充值时间", "完成时间", "状态", "审核级数", "审核备注"};

    public static final String[] CASH_RECHARGE_PROPERTIES =
            {"id", "userId", "username", "realName", "coinName", "num", "fee", "mum", "type",
                    "tradeno", "remark", "created", "lastTime", "status", "step", "auditRemark"};
    public static final String[] CASH_RECHARGE_STATUS = {"待审核", "审核通过", "拒绝", "充值成功"};
    /**
     * GCN提现头
     */
    public static final String[] CASH_WITHDRAWS_HEADERS =
            {"ID", "用户ID", "用户名","提现金额(USDT)", "手续费", "到账金额(CNY)", "提现开户名","银行名称/账号",
                    "申请时间", "完成时间", "状态", "审核级数", "审核备注"};

    public static final String[] CASH_WITHDRAWS_PROPERTIES =
            {"id", "userId", "username",  "num", "fee", "mum", "truename",
                    "bank", "created", "lastTime", "status", "step", "remark"};

    /*
    *  数字货币提现头
    * */
    public static final String[] COIN_WITHDRAWS_HEADERS =
            {"ID", "用户名","币种名称", "提现量", "实际提现", "手续费","钱包地址",
                    "交易ID", "申请时间", "审核时间", "审核备注", "状态","打款类型","审核级数"};

    public static final String[] COIN_WITHDRAWS_PROPERTIES =
            {"id", "username", "coinName",  "num", "mum", "fee", "address",
                    "txid", "created", "auditTime", "remark", "status", "type","step"};



    public static final String[] CASH_WITHDRAWS_STATUS = {"待审核", "审核通过", "拒绝", "提现成功"};
    /**
     *
     */
    public enum RechargeType {
    }

    /************************************* REDIS KEY ************************************/
    /**
     * 验证码redis存储Key
     */
    public static String REDIS_KEY_CAPTCHA_KEY = "CAPTCHA:";

    /**
     * 短信验证码redis存储Key
     */
    public static String REDIS_KEY_SMS_CODE_KEY = "SMSCODE:";

    /**
     * 登录设备存储key
     */
    public static String REDIS_KEY_DEVICES_KEY = "DEVICES";

    /**
     * 最大缓存数据量
     */
    public static long REDIS_MAX_CACHE_KLINE_SIZE = 10000L;

    /**
     * 币币交易K线 redis存储Key
     */
    public static String REDIS_KEY_TRADE_KLINE = "TRADE_KLINE:";


    /**
     * 币币交易撮合队列 redis存储Key
     */
    public static String REDIS_KEY_TRADE_MATCH = "TRADE_MATCH:";

    /**
     * 币币交易对
     */
    public static String REDIS_KEY_TRADE_MARKET = "TRADE_MARKET";

    /**
     * 法币充值审核锁
     */
    public static String REDIS_KEY_CASH_RECHARGE_AUDIT_LOCK = "CASH_RECHARGE_AUDIT_LOCK:";

    /**
     * 法币提现审核锁
     */
    public static String REDIS_KEY_CASH_WITHDRAW_AUDIT_LOCK = "CASH_WITHDRAW_AUDIT_LOCK:";

    /**
     * 数字货币提现审核锁
     */
    public static String REDIS_KEY_COIN_WITHDRAW_AUDIT_LOCK = "COIN_WITHDRAW_AUDIT_LOCK:";

    /**
     * 币币交易撤单锁
     */
    public static String REDIS_KEY_TRADE_ORDER_CANCEL_LOCK = "TRADE_ORDER_CANCEL_LOCK:";

    /**
     * 币币交易撮合锁
     */
    public static String REDIS_KEY_TRADE_ORDER_MATCH_LOCK = "TRADE_ORDER_MATCH_LOCK:";

    /**
     * 创新交易资金账户锁
     */
    public static String REDIS_KEY_TRADE_ACCOUNT_LOCK = "TRADE_ACCOUNT_LOCK:";
    /************************************* REDIS KEY ************************************/


    /************OAUTH*********************/
    public static String AUTHORIZATION_HEADER = "Authorization";

    public static String BEARER = "Bearer ";
    /************OAUTH*********************/

    /************STREAM*********************/
    public static String CHANNEL_SENDTO_USER = "user";
    public static String CHANNEL_SENDTO_GROUP = "group";
    public static String CHANNEL_TICKER_UPDATE = "ticker";


    /**
     * 内置的Token
     */
    public static final String INSIDE_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2Nzc2NzM1ODcsInVzZXJfbmFtZSI6IjEwMTAxMDEwMTAxMDEwMTAxMDEiLCJhdXRob3JpdGllcyI6WyJ0cmFkZV9jb2luX3VwZGF0ZSIsImNhc2hfcmVjaGFyZ2VfYXVkaXRfcXVlcnkiLCJ1c2VyX3VwZGF0ZSIsImNhc2hfd2l0aGRyYXdfYXVkaXRfZXhwb3J0IiwiY29pbl93aXRoZHJhd19zdGF0aXN0aWNzX3F1ZXJ5Iiwic3lzX3JvbGVfcXVlcnkiLCJ3ZWJfY29uZmlnX3F1ZXJ5IiwibG9naW5fc3RhdGlzdGljc19xdWVyeSIsIndvcmtfaXNzdWVfcXVlcnkiLCJ0cmFkZV9hcmVhX3VwZGF0ZSIsInRyYWRlX21hcmtldF9kZWxldGUiLCJ1c2VyX2F1dGhfYXVkaXQiLCJzeXNfcHJpdmlsZWdlX2RlbGV0ZSIsImNhc2hfcmVjaGFyZ2Vfc3RhdGlzdGljc19xdWVyeSIsInN5c191c2VyX2xvZ19xdWVyeSIsInRyYWRlX2FyZWFfZGVsZXRlIiwid2ViX2NvbmZpZ19kZWxldGUiLCJhY2NvdW50X2RldGFpbF9xdWVyeSIsImFkbWluX2JhbmtfcXVlcnkiLCJzeXNfcHJpdmlsZWdlX3VwZGF0ZSIsInN5c19yb2xlX2NyZWF0ZSIsInRyYWRlX2NvaW5fdHlwZV9xdWVyeSIsInVzZXJfd2FsbGV0X3F1ZXJ5Iiwid2ViX2NvbmZpZ191cGRhdGUiLCJ0cmFkZV9zdGF0aXN0aWNzX3F1ZXJ5IiwiY2FzaF93aXRoZHJhd19hdWRpdF9xdWVyeSIsImNvbmZpZ19jcmVhdGUiLCJ0cmFkZV9tYXJrZXRfY3JlYXRlIiwiYXNkYXMiLCJ0cmFkZV9tYXJrZXRfcXVlcnkiLCJ0cmFkZV9kZWFsX29yZGVyX2V4cG9ydCIsImFjY291bnRfZXhwb3J0Iiwic3lzX3VzZXJfdXBkYXRlIiwidXNlcl9hdXRoX3F1ZXJ5IiwiYWRtaW5fYmFua19kZWxldGUiLCJ0cmFkZV9jb2luX3R5cGVfY3JlYXRlIiwiYWNjb3VudF9xdWVyeSIsImNvbmZpZ191cGRhdGUiLCJzeXNfdXNlcl9jcmVhdGUiLCJjb25maWdfcXVlcnkiLCJ0cmFkZV9jb2luX3F1ZXJ5IiwidHJhZGVfYXJlYV9xdWVyeSIsIndvcmtfaXNzdWVfdXBkYXRlIiwidHJhZGVfZW50cnVzdF9vcmRlcl9leHBvcnQiLCJub3RpY2VfdXBkYXRlIiwic3lzX3JvbGVfdXBkYXRlIiwidHJhZGVfY29pbl90eXBlX2RlbGV0ZSIsInRyYWRlX2NvaW5fdHlwZV91cGRhdGUiLCJyZWdpc3Rlcl9zdGF0aXN0aWNzX3F1ZXJ5IiwidHJhZGVfZW50cnVzdF9vcmRlcl9xdWVyeSIsImFjY291bnRfc3RhdHVzX3VwZGF0ZSIsImNhc2hfcmVjaGFyZ2VfYXVkaXRfZXhwb3J0IiwidHJhZGVfYXJlYV9jcmVhdGUiLCJhY2NvdW50X3JlY2hhcmdlX2NvaW5feG4iLCJ0cmFkZV9kZWFsX29yZGVyX3F1ZXJ5Iiwibm90aWNlX2RlbGV0ZSIsInVzZXJfZXhwb3J0IiwiY2FzaF93aXRoZHJhd19zdGF0aXN0aWNzX3F1ZXJ5Iiwic3lzX3ByaXZpbGVnZV9jcmVhdGUiLCJzeXNfcm9sZV9kZWxldGUiLCJ1c2VyX3dhbGxldF9hZGRyZXNzX3F1ZXJ5IiwiYWNjb3VudF9kZXRhaWxfZXhwb3J0IiwidHJhZGVfY29pbl9jcmVhdGUiLCJjb2luX3JlY2hhcmdlX2V4cG9ydCIsInN5c19wcml2aWxlZ2VfcXVlcnkiLCJjb2luX3JlY2hhcmdlX3F1ZXJ5IiwiY29pbl9yZWNoYXJnZV9zdGF0aXN0aWNzX3F1ZXJ5IiwidXNlcl9iYW5rX3F1ZXJ5IiwiYWRtaW5fYmFua19jcmVhdGUiLCJzeXNfdXNlcl9kZWxldGUiLCJ1c2VyX3F1ZXJ5Iiwid2ViX2NvbmZpZ19jcmVhdGUiLCLmtYvor5UiLCJjYXNoX3JlY2hhcmdlX2F1ZGl0XzIiLCJ0cmFkZV9tYXJrZXRfdXBkYXRlIiwibm90aWNlX3F1ZXJ5IiwiY2FzaF9yZWNoYXJnZV9hdWRpdF8xIiwiY29pbl93aXRoZHJhd19leHBvcnQiLCJjb25maWdfZGVsZXRlIiwiY29pbl93aXRoZHJhd19xdWVyeSIsIm5vdGljZV9jcmVhdGUiLCJzbXNfcXVlcnkiLCJzeXNfdXNlcl9xdWVyeSIsImFkbWluX2JhbmtfdXBkYXRlIiwiY2FzaF93aXRoZHJhd19hdWRpdF8xIiwiY2FzaF93aXRoZHJhd19hdWRpdF8yIiwidHJhZGVfY29pbl9kZWxldGUiLCJjb2luX3dpdGhkcmF3X2F1ZGl0XzIiLCJjb2luX3dpdGhkcmF3X2F1ZGl0XzEiXSwianRpIjoiNTIxODRjZWEtNzhjOC00NWY3LTk1YTctZDEzZDU2NzlkNzhlIiwiY2xpZW50X2lkIjoiY29pbi1hcGkiLCJzY29wZSI6WyJhbGwiXX0.beYGADVu0EKom7Z3g26HsXIW_-6siNlNB4YT2INeyk0Ngt2SXhb5bXMVYdiyJkyB8DYQyilPzQ-L_efd7zKlmVLbESaSxiUbsVjjfnnaCGSCvx1BpnSxvejpd_ZjEsScVUCRQtuH0bL0aYhn8zPdwO1Fq-3L_Hego2W9tbOjpwPqa4lIzTZsmq3mW7eFLyGDHNkVjnMsMWMIeyEFozFYrgiL6RTkxrpPMJz00ojjJC53B_ChcBgbGr4l4f1Tzb_idVUbk9U-ck4yyzwA4eLZZCYvTvLf0r-kSYsGBKktFqQMXZpBgkv-4F_GJmjUyj9PArEELL9dLf5q2T0bTRFZFg";


    /*
    *  买入GCN比率代码
    * */
    public static final String BUY_GCN_RATE = "CNY2USDT";

    /*
    *  出售GCN比率代码
    * */
    public static final String SELL_GCN_RATE = "USDT2CNY";

    /*
    *  充值GCN最小单位数量代码
    * */
    public static final String RECHARGE_MIN_AMOUNT = "RECHARGE_MIN_AMOUNT";

    /*
     *  提现GCN最小单位数量代码
     * */
    public static final String WITHDRAW_MIN_AMOUNT = "WITHDRAW_MIN_AMOUNT";

    /*
     *  提现GCN最大单位数量代码
     * */
    public static final String WITHDRAW_MAX_AMOUNT = "WITHDRAW_MAX_AMOUNT";

    /*
    *  每日最大的提现数量
    * */
    public static final String WITHDRAW_DAY_MAX_AMOUNT = "WITHDRAW_DAY_MAX_AMOUNT";

    /*
    *  提现状态
    * */
    public static final String WITHDRAW_STATUS = "WITHDRAW_STATUS";

    /*
    *  最小体现手续费
    * */
    public static final String WITHDRAW_MIN_POUNDAGE = "WITHDRAW_MIN_POUNDAGE";

    /*
    *  体现手续费率
    * */
    public static final String WITHDRAW_POUNDAGE_RATE = "WITHDRAW_POUNDAGE_RATE";




}
