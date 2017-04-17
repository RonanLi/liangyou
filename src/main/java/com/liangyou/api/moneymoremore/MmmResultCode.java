package com.liangyou.api.moneymoremore;

import com.liangyou.util.NumberUtils;


/**
 * 双乾接口返回参数处理
 *
 * @author Qinjun
 */
public class MmmResultCode {

    public static String getResult(String codeStr, String type) {
        int code = NumberUtils.getInt(codeStr);
        if (type.equals("loan")) {
            return loanResult(code);
        } else if (type.equals("register")) {
            return registerResult(code);
        } else if (type.equals("recharge")) {
            return rechargeResult(code);
        } else if (type.equals("loanAuthorize")) {
            return loanAuthorizeResult(code);
        } else if (type.equals("cash")) {
            return cashResult(code);
        } else if (type.equals("verifyFullBorrow")) {
            return toLoanTransferAuditResult(code);
        } else if (type.equals("loanFastPay")) {
            return toLoanFastPayResult(code);
        } else {
            return "处理失败";
        }
    }

    /**
     * 开户接口返回码说明
     *
     * @param code
     * @return
     */
    public static String registerResult(int code) {
        switch (code) {
            case 1:
                return "开户类型错误";
            case 2:
                return "手机号错误";
            case 3:
                return "邮箱错误";
            case 4:
                return "真实姓名或企业名称错误";
            case 5:
                return "身份证号或营业执照号错误";
            case 6:
                return "身份证或营业执照图片错误";
            case 7:
                return "平台乾多多标识错误";
            case 8:
                return "签名验证失败";
            case 9:
                return "手机和邮箱已存在";
            case 10:
                return "邮箱已存在";
            case 11:
                return "手机已存在";
            case 12:
                return "登录密码错误";
            case 13:
                return "支付密码错误";
            case 14:
                return "安保问题错误";
            case 15:
                return "用户网贷平台帐号错误";
            case 16:
                return "网贷平台帐号已绑定";
            case 17:
                return "随机时间戳错误";
            case 18:
                return "自定义备注错误";
            case 19:
                return "乾多多账号被禁用";
            case 20:
                return "乾多多账号未激活";
            case 22:
                return "手机或邮箱验证码输入错误";
            case 23:
                return "提交的身份信息与乾多多的信息不符";
            case 24:
                return "绑定的乾多多账号为平台账号";
            case 25:
                return "乾多多账号已绑定别的平台";
            case 26:
                return "密码、手机或邮箱验证码、安保问题错误次数过多，锁定中";
            case 27:
                return "账户类型错误";
            case 28:
                return "网贷平台账号已绑定，相同网贷平台账号提交过至少两次不同身份信息";
            case 29:
                return "乾多多账号已被平台其他用户绑定";
            case 30:
                return "平台自有账户余额不足";
            case 31:
                return "姓名匹配失败";
            case 32:
                return "图形验证码错误";
            case 33:
                return "身份证号码或营业执照号在此平台上已注册过";
            case 34:
                return "实名认证失败"; // 姓名匹配处理中(当作失败来处理)
            case 35:
                return "实名认证失败";
            case 36:
                return "注册身份证信息或手机号已存在黑名单";
            case 37:
                return "身份证号码或营业执照号已存在";
            case 38:
                return "身份证年龄不符合开户要求";
            case 39:
                return "手机实名认证失败";
            case 40:
                return "手机实名认证失败"; // 手机实名认证处理中（当作失败来处理） // 双乾没有上线   2017-02-22 11:12:43
            case 90:
                return "手机实名认证失败";
            default:
                return "类型错误";
        }
    }

    /**
     * 充值接口返回码说明
     *
     * @param code
     * @return
     */
    public static String rechargeResult(int code) {
        switch (code) {
            case 1:
                return "用户乾多多标识错误";
            case 2:
                return "平台乾多多标识错误";
            case 3:
                return "充值订单号错误";
            case 4:
                return "金额错误";
            case 5:
                return "签名验证失败";
            case 6:
                return "充值失败";
            case 7:
                return "充值类型错误";
            case 8:
                return "银行卡号错误";
            case 9:
                return "没有已绑定的银行卡";
            case 10:
                return "随机时间戳错误";
            case 11:
                return "自定义备注错误";
            case 12:
                return "代扣超过日期";
            case 13:
                return "代扣超过限额";
            case 14:
                return "乾多多账户余额不足";
            case 15:
                return "平台自有账户余额不足";
            case 16:
                return "手续费类型错误";
            case 17:
                return "超出该用户最大绑卡数";
            case 20:
                return "实名信息未完善";
            case 22:
                return "充值人账号已冻结";
            case 89:
                return "汇款充值失败";
            case 92:
                return "交易处理中";
            default:
                return "类型错误";
        }
    }

    /**
     * 授权返回码说明
     *
     * @param code
     * @return
     */
    public static String loanAuthorizeResult(int code) {
        switch (code) {
            case 1:
                return "用户乾多多标识错误";
            case 2:
                return "平台乾多多标识错误";
            case 3:
                return "授权类型错误";
            case 4:
                return "签名验证失败";
            case 5:
                return "支付密码输入错误";
            case 6:
                return "短信验证码输入错误";
            case 7:
                return "安保问题输入错误";
            case 8:
                return "请求开启的授权已开启";
            case 9:
                return "请求关闭的授权未开启";
            case 10:
                return "随机时间戳错误";
            case 11:
                return "自定义备注错误";
            case 12:
                return "密码、验证码或安保问题错误次数过多，锁定中";
            default:
                return "类型错误";
        }
    }

    /**
     * 提现返回码说明
     *
     * @param code
     * @return
     */
    public static String cashResult(int code) {
        switch (code) {
            case 1:
                return "用户乾多多标识错误";
            case 2:
                return "平台乾多多标识错误";
            case 3:
                return "提现订单号错误";
            case 4:
                return "金额错误";
            case 5:
                return "卡信息错误";
            case 6:
                return "签名验证失败";
            case 7:
                return "余额不足";
            case 9:
                return "支付密码输入错误";
            case 10:
                return "短信验证码输入错误";
            case 11:
                return "安保问题输入错误";
            case 13:
                return "手续费比例错误";
            case 14:
                return "平台自有账户余额不足";
            case 15:
                return "随机时间戳错误";
            case 16:
                return "自定义备注错误";
            case 17:
                return "密码、验证码或安保问题错误次数过多，锁定中";
            case 18:
                return "用户承担的最高手续费错误";
            case 19:
                return "上浮费率错误";
            case 20:
                return "用户承担的定额手续费错误";
            case 21:
                return "该银行卡提现额度不足";
            case 22:
                return "用户已存在于系统黑名单";
            case 25:
                return "实名信息未完善";
            case 26:
                return "此账户暂不能进行新的提现银行卡绑定";
            case 27:
                return "提现人账号已冻结";
            case 89:
                return "提现资金退回";
            case 90:
                return "提现待平台审核";
            default:
                return "类型错误";
        }
    }

    /**
     * 审核接口返回码说明
     *
     * @param code
     * @return
     */
    public static String toLoanTransferAuditResult(int code) {
        switch (code) {
            case 1:
                return "乾多多流水号列表错误";
            case 2:
                return "审核类型错误";
            case 3:
                return "平台乾多多标识错误";
            case 5:
                return "签名验证失败";
            case 7:
                return "随机时间戳错误";
            case 8:
                return "自定义备注错误";
            case 9:
                return "操作人不唯一";
            case 10:
                return "余额不足";
            case 12:
                return "支付密码输入错误";
            case 13:
                return "短信验证码输入错误";
            case 14:
                return "安保问题输入错误";
            case 15:
                return "密码、验证码或安保问题错误次数过多，锁定中";
            case 17:
                return "审核对象重复提交";
            // 87 是否作为提示展示？？？？  2017年2月22日13:46:20
//            case 87:
//                return "处理中";
            default:
                return "类型错误";
        }
    }

    /**
     * 认证、提现银行卡绑定、代扣授权三合一接口返回码说明
     *
     * @param code
     * @return
     */
    public static String toLoanFastPayResult(int code) {
        switch (code) {
            case 1:
                return "用户乾多多标识错误";
            case 2:
                return "操作类型错误";
            case 3:
                return "平台乾多多标识错误";
            case 4:
                return "签名验证失败";
            case 5:
                return "代扣日期错误";
            case 6:
                return "单笔代扣限额错误";
            case 7:
                return "代扣总限额错误";
            case 8:
                return "随机时间戳错误";
            case 9:
                return "自定义备注错误";
            case 10:
                return "账号已实名认证";
            case 11:
                return "银行卡号错误";
            case 12:
                return "用户身份证信息错误";
            case 13:
                return "登录密码输入错误";
            case 14:
                return "短信验证码输入错误";
            case 15:
                return "安保问题输入错误";
            case 16:
                return "密码、验证码或安保问题错误次数过多，锁定中";
            case 17:
                return "快捷支付失败";
            case 18:
                return "银行卡已存在";
            case 19:
                return "银行卡未授权代扣或正在审核中";
            case 20:
                return "汇款绑卡确认的银行卡错误";
            case 21:
                return "汇款绑卡金额错误";
            case 22:
                return "卡号或手机号格式不正确";
            case 23:
                return "银行卡信息错误";
            case 24:
                return "平台自有账户余额不足";
            case 25:
                return "超出最大绑卡数";
            case 26:
                return "此账户暂不能进行新的银行卡绑定";
            default:
                return "类型错误";
        }
    }

    /**
     * 转账接口返回码说明
     *
     * @param code
     * @return
     */
    public static String loanResult(int code) {
        switch (code) {
            case 1:
                return "转账列表错误";
            case 2:
                return "操作类型错误";
            case 3:
                return "转账方式错误";
            case 4:
                return "平台乾多多标识错误";
            case 5:
                return "签名验证失败";
            case 6:
                return "付款人乾多多标识错误";
            case 7:
                return "收款人乾多多标识错误";
            case 8:
                return "平台订单号错误";
            case 9:
                return "平台标号错误";
            case 10:
                return "金额错误";
            case 11:
                return "付款人不唯一";
            case 13:
                return "付款人未绑定乾多多";
            case 14:
                return "付款人乾多多绑定的网贷平台错误";
            case 16:
                return "收款人未绑定乾多多";
            case 17:
                return "收款人乾多多绑定的网贷平台错误";
            case 18:
                return "重复订单";
            case 21:
                return "乾多多账户可用余额不足";
            case 22:
                return "支付密码输入错误";
            case 23:
                return "短信验证码输入错误";
            case 24:
                return "安保问题输入错误";
            case 25:
                return "转账类型错误";
            case 27:
                return "满标标额错误";
            case 28:
                return "投标总金额超过标额";
            case 29:
                return "二次分配列表错误";
            case 30:
                return "二次分配金额超过转账金额";
            case 31:
                return "自动转账未授权";
            case 32:
                return "通过是否需要审核参数错误";
            case 33:
                return "随机时间戳错误";
            case 34:
                return "自定义备注错误";
            case 35:
                return "密码、验证码或安保问题错误次数过多，锁定中";
            case 36:
                return "收款人二次分配未授权";
            case 37:
                return "二次分配收款人未实名认证";
            case 38:
                return "流转标标号错误";
            case 39:
                return "垫资标号错误";
            case 40:
                return "流转标金额超过原始标金额";
            case 41:
                return "转账金额超过垫资金额";
            case 42:
                return "转账信息已过期";
            case 44:
                return "存在重复转账风险";
            case 45:
                return "付款人乾多多已存在黑名单";
            case 48:
                return "实名信息未完善";
            case 49:
                return "转账未备案，请至托管账户备案";
            case 50:
                return "转账笔数已超限";
            case 51:
                return "转账限额已超限";
            case 52:
                return "到账时间错误";
            case 53:
                return "付款人账号已冻结";
            case 54:
                return "收款人账号已冻结";
            case 55:
                return "二次分配收款人账号已冻结";
            default:
                return "类型错误";
        }
    }

}
