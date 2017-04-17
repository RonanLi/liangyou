package com.liangyou.quartz.apiAccount;
/**
 * 业务处理类型：
 * 第三方账户开立
 * 用户账户激活
 * 绑定银行卡异步通知
 * 双乾三合一接口
 * 扣款签名
 * 易极付实名
 * 汇付合作账户开立
 * 【重要】：
 *  此队列只处理第三方账户相关注册信息的，和资金信息无关。
 */
import com.liangyou.api.chinapnr.CardCashOut;
import com.liangyou.api.chinapnr.CorpRegister;
import com.liangyou.api.moneymoremore.MmmToLoanFastPay;
import com.liangyou.api.pay.DeductSign;
import com.liangyou.api.pay.NewAuthorize;
import com.liangyou.api.pay.SignmanyBank;
import com.liangyou.domain.AccountBank;
import com.liangyou.domain.User;
import com.liangyou.model.BorrowParam;

public class ApiAccountBean {

	private String type;
	private BorrowParam borrowParam;
	private User user;
	private CardCashOut cardCashOut;
	private MmmToLoanFastPay mmmToLoanFastPay;
	private DeductSign deductSign;
	private NewAuthorize newAuthorize;
	private CorpRegister corpRegister;
	private SignmanyBank sinSignmanyBank; 
	private AccountBank accountBank;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public BorrowParam getBorrowParam() {
		return borrowParam;
	}
	public void setBorrowParam(BorrowParam borrowParam) {
		this.borrowParam = borrowParam;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public CardCashOut getCardCashOut() {
		return cardCashOut;
	}
	public void setCardCashOut(CardCashOut cardCashOut) {
		this.cardCashOut = cardCashOut;
	}
	public MmmToLoanFastPay getMmmToLoanFastPay() {
		return mmmToLoanFastPay;
	}
	public void setMmmToLoanFastPay(MmmToLoanFastPay mmmToLoanFastPay) {
		this.mmmToLoanFastPay = mmmToLoanFastPay;
	}
	public DeductSign getDeductSign() {
		return deductSign;
	}
	public void setDeductSign(DeductSign deductSign) {
		this.deductSign = deductSign;
	}
	public NewAuthorize getNewAuthorize() {
		return newAuthorize;
	}
	public void setNewAuthorize(NewAuthorize newAuthorize) {
		this.newAuthorize = newAuthorize;
	}
	public CorpRegister getCorpRegister() {
		return corpRegister;
	}
	public void setCorpRegister(CorpRegister corpRegister) {
		this.corpRegister = corpRegister;
	}
	public SignmanyBank getSinSignmanyBank() {
		return sinSignmanyBank;
	}
	public void setSinSignmanyBank(SignmanyBank sinSignmanyBank) {
		this.sinSignmanyBank = sinSignmanyBank;
	}
	public AccountBank getAccountBank() {
		return accountBank;
	}
	public void setAccountBank(AccountBank accountBank) {
		this.accountBank = accountBank;
	}
	
	
}
