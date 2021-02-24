package com.alipay.riskplus.dubbusiness.common.enums;

/**
 * @Descrption 数据脱敏类型枚举
 * 脱敏和扫描识别支持列表:https://yuque.antfin-inc.com/docs/share/0e78fc0d-8122-4af5-8393-c88aeadda9ab?#
 * @Author wuhh 2021-1-29
 */
public enum DesensitizeTypeEnum {

    /** 示例:510106198912240101 */
    CN_ID_CARD_NO("CnIdCardNo", "中国身份证号"),
    /** 示例: */
    MIL_OFFICER_NO("MilOfficerNo", "军官证号"),
    /** 示例:6227612145830440 */
    BANK_CARD_NO("BankCardNo", "银行卡号/信用卡号"),
    /** 示例:13908001457 */
    CN_MOBILE_NO("CnMobileNo", "中国手机号"),
    /** 示例:13908001457 */
    CN_MOBILE_NO4_SMS("CnMobileNo4SMS", "中国手机号（短信场景）"),
    /** 示例:pf_miles@126.com */
    EMAIL("Email", "电子邮箱地址（网站和APP端场景）"),
    /** 示例:pf_miles@126.com */
    EMAIL_ALL("EmailAll", "电子邮箱地址（短信场景）"),
    /** 示例:192.168.0.1 */
    IPV4("Ipv4", "ipv4格式的ip地址"),
    /** 示例:f0:18:98:34:bc:07 */
    MAC("Mac", "mac地址"),
    /** 示例:川A▪96MY7 */
    CN_LICENSE_PLATE_NO("CnLicensePlateNo", "中国车牌号"),
    /** 示例:86-028-86123084 */
    CN_TEL_NO("CnTelNo", "中国座机号码"),
    /** 示例:黄继堂,John Wilson Sr. */
    NAME("Name", "中国姓名&外国姓名"),
    /** 示例:中国浙江省杭州市余杭区文一西路969号 */
    CN_ADDRESS("CnAddress", "中国地址"),
    /** 示例:1st Floor, Viktualienmarkt 8, 80331 Munich, Germany */
    EN_ADDRESS("EnAddress", "外国地址"),
    /** 示例: */
    MEDICARE_NO("MedicareNo", "医保卡号"),
    /** 示例: */
    SOCIAL_SEC_NO("SocialSecNo", "社保卡号"),
    /** 示例:G40676356，C03310003 */
    PASSPORT("Passport", "护照、港澳通行证"),
    /** 示例:+852-88665544 */
    GLOBAL_MOBILE_NO("GlobalMobileNo", "大陆以外手机号"),
    /** 示例:+852-88665544 */
    GLOBAL_MOBILE_NO4_SMS("GlobalMobileNo4SMS", "大陆以外手机号（短信场景）"),
    /** 示例:(010) 58178688  (+86) 10-5817-8688 */
    CN_TELEPHONE_NO("CnTelephoneNo", "大陆以外固定电话"),
    /** 示例:246764K */
    VIN_NO("VinNo", "发动机编号"),
    /** 示例: */
    VEHICLELICENSE_NO("VehicleLicenseNo", "行驶证档案编号"),
    /** 示例:353049097345540 */
    DEVICE_ID_NO("DeviceIDNo", "设备唯一识别号"),
    /**
     * 说明:Default(不可编程指定，当找不到对应类型时系统自动默认使用)
     * 示例:如“198912240808”等未正式被规范定义、但业务上评估认为仍然需要脱敏的数据
     */
    X("X", "默认，使用缺省脱敏规则");

    String type;
    String msg;

    DesensitizeTypeEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
