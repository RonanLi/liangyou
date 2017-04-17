package com.liangyou.model.themis;

import org.mapu.themis.api.common.PreservationType;

/**
 * Created by Young on 2017/3/10.
 */
public class ContractFileCreate {
    private String contractNumber; // 合同编号
    private Double contractAmount; // 合同金额
    private String userIdentifer;  //用户标识信息（身份证号）
    private String userRealName;   // 用户真实姓名
    private PreservationType preservationType; // 保全类型枚 (举值 DIGITAL_CONTRACT(5,"电子合同")
    private String preservationTitle; // 保全标题
    private String sourceRegistryId;  // 保全来源处注册用户 ID
    private String userEmail;         //用户邮箱
    private String mobilePhone;       // 用户手机号
    private String objectId;          // 保全组 ID
    private String file;              // 文件对象
    private String comments;          // 全备注信息

    public ContractFileCreate(String file, String contractNumber, Double contractAmount, String userIdentifer, String userRealName, String preservationTitle,
                              String sourceRegistryId, String userEmail, String mobilePhone, String objectId, String comments) {
        super();
        this.file = file;
        this.contractNumber = contractNumber;
        this.contractAmount = contractAmount;
        this.userIdentifer = userIdentifer;
        this.userRealName = userRealName;
        this.preservationType = PreservationType.DIGITAL_CONTRACT;
        this.preservationTitle = preservationTitle;
        this.sourceRegistryId = sourceRegistryId;
        this.userEmail = userEmail;
        this.mobilePhone = mobilePhone;
        this.objectId = objectId;
        this.comments = comments;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Double getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(Double contractAmount) {
        this.contractAmount = contractAmount;
    }

    public String getUserIdentifer() {
        return userIdentifer;
    }

    public void setUserIdentifer(String userIdentifer) {
        this.userIdentifer = userIdentifer;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public PreservationType getPreservationType() {
        return preservationType;
    }

    public void setPreservationType(PreservationType preservationType) {
        this.preservationType = preservationType;
    }

    public String getPreservationTitle() {
        return preservationTitle;
    }

    public void setPreservationTitle(String preservationTitle) {
        this.preservationTitle = preservationTitle;
    }

    public String getSourceRegistryId() {
        return sourceRegistryId;
    }

    public void setSourceRegistryId(String sourceRegistryId) {
        this.sourceRegistryId = sourceRegistryId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
