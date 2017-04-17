package com.liangyou.api.ips;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;
import com.liangyou.util.ReflectUtils;
import com.liangyou.util.StringUtils;

public class IpsModel {
	private static Logger logger = Logger.getLogger(IpsModel.class);	
    /* IPS证书 */
    private String cert_md5	= "GPhKt7sh4dxQQZZkINGFtefRKNPyAj8S00cgAwtRyy0ufD7alNC28xCBKpa6IU7u54zzWSAv4PqUDKMgpOnM7fucO1wuwMi4RgPAnietmqYIhHXZ3TqTGKNzkxA55qYH";
    
    private String des_key	= "ICHuQplJ0YR9l7XeVNKi6FMn";
    private String des_iv =	"2EDxsEfp";
    
    //请求参数

    private String url ;
	
    /**
     * 由IPS颁发的商户号
     */
    private String merCode;
    
    /**
     * XML 格式的字符串(请求信息)
     */
    private String desXmlPara;
    
    /**
     * 签名参数MD5(pMerCode+p3DesXmlPara+Ips 证书) 取 32 位小写
     */
    private String sign;
    /**
     * 接口提交地址
     */
    private String submitUrl;
    /**
     * 状态返回地址 :同步
     */
    private String webUrl;
	/**
	 * 状态返回地址 ：异步
	 */
	private String s2SUrl;
    
	/**
	 * 校验订单号
	 */
	private String merBillNo;
	
    /**
     * 参数列表
     */
    private String[] paramNames=new String[]{""};
    
    /**
     * 提交参数
     */
  //  private String[][] commitParams = new String[][]{{"argMerCode","arg3DesXmlPara","argSign"},{"808801",this.desXmlPara,IpsCrypto.md5Sign(808801+this.desXmlPara+ this.getCert_md5())}};
    
    //回调参数
    /**
     * 处理返回状态
     */
    private String errCode;
    /**
     * 返回信息
     */
    private String errMsg;
    
    private String memo1;
    
    private String memo2;
    
    private String memo3;
    
	public IpsModel(){
		init();
	}
	
	private void init(){
		this.merCode = Global.getString("ips_base_account");
		this.setCert_md5(Global.getValue("CERT_MD5"));
		this.setDes_key(Global.getValue("DES_KEY"));
		this.setDes_iv(Global.getValue("DES_IV"));
		if(isOnlineConfig()){
			setSubmitUrl(Global.getString("online_url"));
		}else{
			setSubmitUrl(Global.getString("test_url"));
		}
	}
	
	/**
	 * 是否开通线上环境配置。
	 * @return
	 */
	public static boolean isOnlineConfig(){
		return "1".equals(Global.getValue("is_open_api"));
	}
	
    public void createSign(){
		String[] paramNames=getParamNames();
		StringBuffer buf = new StringBuffer();
		if(paramNames.length>0){
			buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><pReq>");
			for(int i=0;i<paramNames.length;i++){
				String name=paramNames[i];
				Object result=ReflectUtils.invokeGetMethod(getClass(),this, name);
				String value=(result==null?"":result.toString());
				buf.append("<p"+name+">");
				buf.append(value);
				buf.append("</p"+name+">");
			}
			buf.append("</pReq>");
			String str3DesXmlPana = buf.toString() ;
			logger.info("str3DesXmlPana  :  "+str3DesXmlPana);
			//参数加密
		/*	this.desXmlPara = IpsCrypto.triDesEncrypt(str3DesXmlPana,this.getDes_key(),this.getDes_iv()).replaceAll("\r\n", "");
			this.sign = IpsCrypto.md5Sign(this.merCode + this.desXmlPara + this.getCert_md5());
			//提交参数设置
			commitParams[1][0] = this.merCode;
			commitParams[1][1] = this.desXmlPara;
			commitParams[1][2] = this.sign;*/
		}else{
			/*this.sign = IpsCrypto.md5Sign(this.merCode + this.getCert_md5());
			commitParams = new String[][]{{"argMerCode","argSign"},{this.merCode,this.sign}};*/
		}
	}
    
    /**
     * 服务器点对点请求时，处理同步回调参数
     * @param str
     * @return
     */
    public XmlTool1 response(String str){
    	XmlTool1 Tool = new XmlTool1();
		Tool.SetDocument(str);
    	return Tool;
    }

    /**
     * 后台验签方法
     * @param resultStr
     * @return
     */
    public String checkSign(){
		String signPlainText = this.merCode + this.errCode + this.errMsg + this.desXmlPara+ this.getCert_md5();
		logger.info("pMerCode = " + this.merCode +",pErrCode = " + this.errCode+ ",pErrMsg = " + this.errMsg+",p3DesXmlPara = " + this.desXmlPara
				+",pSign = " + this.sign +",signPlainText==="+signPlainText);
		/*String localSign = IpsCrypto.md5Sign(signPlainText);*/
/*		if (localSign.equals(this.sign)) {
			logger.info("MD5验签通过！");
			String str3XmlParaInfo = IpsCrypto.triDesDecrypt(this.desXmlPara, this.getDes_key(), this.getDes_iv());
			if("".equals(str3XmlParaInfo)||str3XmlParaInfo==null){
				logger.info("3DES解密失败");
				throw new BussinessException("3DES解密失败");
			}else{
				logger.info("解密后参数：str3XmlParaInfo = " + str3XmlParaInfo);
				return str3XmlParaInfo;
			}
		}else{
			logger.info("验证签名失败：本地签名localSign" + localSign + "，环迅签名:pSign"+this.sign+",CERT_MD5:"+this.getCert_md5());
			throw new BussinessException("验证签名失败！");
		}*/
		return null;
    }
    
    /**
     * @param params 请求参数数组 ：
     * @param value  参数值数组：必须是参数和值对应
     * @param url  请求url
     * @param OperationName  接口名
     * @return result:接口返回数据
     * @throws Exception 
     */
    public String doNotifySubmit(String url,String OperationName) throws Exception{
		/*String[] names = StringUtils.getArrayByIndex(this.getCommitParams(), 0);
		String[] values = StringUtils.getArrayByIndex(this.getCommitParams(), 1);
		String result = "";
		// 创建一个服务(service)调用(call)
		Service service = new Service();
		Call call = (Call) service.createCall();// 通过service创建call对象
		// 设置service所在URL
		call.setTargetEndpointAddress(new URL(url));
		call.setOperationName(new QName("http://tempuri.org/", OperationName));
		// Add 是net 那边的方法 "http://tempuri.org/" 这个也要注意Namespace 的地址,不带也会报错
		for (int i = 0; i < names.length; i++) { // 动态拼接请求参数
			call.addParameter(new QName("http://tempuri.org/", names[i]),
					XMLType.XSD_STRING, ParameterMode.IN);
		}
		call.setUseSOAPAction(true);
		call.setReturnType(XMLType.SOAP_STRING); // 返回参数的类型
		call.setSOAPActionURI("http://tempuri.org/" + OperationName); // 这个也要注意
																		// 就是要加上要调用的方法Add,不然也会报错
		// Object 数组封装了参数，参数为"This is Test!",调用processService(String arg)
*/		/*result = (String) call.invoke(values);
		logger.info("接口名--" + "OperationName" + "------" + result);*/
		return null;
    }
    
    public String httpSubmit(){
    	String resp = "";
    	/*IpsHttpHelper http=new IpsHttpHelper(getSubmitUrl(),getCommitParams(),"UTF-8");*/
    	try {
    		/*resp=http.execute();*/
		} catch (Exception e) {
			logger.info("提交异常！"+e.getMessage());
		}
    	return resp;
    	
    }
    
    /**
     * 判断返回值是否成功
     * @param code
     * @return
     */
    public boolean checkRespCode(String code){
    	if (code.equals(0000)) {  //通过，成功
    		return true;
		}else{
			return false;
		}
    }
    
    
    
    
    
    /**
	 * post提交的webservice
	 * @param Url
	 * @param argMerCode
	 * @param arg3DesXmlPara
	 * @param argSign
	 * @return
	 */
	public  String getSoapInputStream(String Url,String argMerCode,String arg3DesXmlPara,String argSign) {  
        try {  
            String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
            "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
            "<soap:Body>"+
            "  <Transfer xmlns=\"http://tempuri.org/\">"+
            "    <pMerCode>"+argMerCode+"</pMerCode>"+
            "    <p3DesXmlPara>"+arg3DesXmlPara+"</p3DesXmlPara>"+
            "    <pSign>"+argSign+"</pSign>"+
            "  </Transfer>"+
            "</soap:Body>"+
            "</soap:Envelope>";
            
            URL url = new URL(Url);  
            URLConnection conn = url.openConnection();  
            conn.setUseCaches(false);  
            conn.setDoInput(true);  
            conn.setDoOutput(true);  
  
            conn.setRequestProperty("Content-Length", Integer.toString(soap  
                    .length()));  
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");  
            conn.setRequestProperty("SOAPAction",  
                    "http://tempuri.org/Transfer");  
  
            OutputStream os = conn.getOutputStream();  
            OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");  
            osw.write(soap);
            osw.flush();  
            osw.close();  

            InputStream is = conn.getInputStream();  
            
            DataInputStream dis=new DataInputStream(is);
     	   	byte d[]=new byte[dis.available()];
     	   	dis.read(d);
     	    String data=new String(d,"UTF-8");
           
     	    return data;
  
        } catch (Exception e) {  
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return null;  
        }  
    }

    
    
    
    
    

	public String getMerCode() {
		return merCode;
	}

	public void setMerCode(String merCode) {
		this.merCode = merCode;
	}

	public String getDesXmlPara() {
		return desXmlPara;
	}

	public void setDesXmlPara(String desXmlPara) {
		this.desXmlPara = desXmlPara;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getS2SUrl() {
		return s2SUrl;
	}

	public void setS2SUrl(String s2sUrl) {
		s2SUrl = s2sUrl;
	}

	public String getMerBillNo() {
		return merBillNo;
	}

	public void setMerBillNo(String merBillNo) {
		this.merBillNo = merBillNo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}



	public String getMemo1() {
		return memo1;
	}

	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}

	public String getMemo2() {
		return memo2;
	}

	public void setMemo2(String memo2) {
		this.memo2 = memo2;
	}

	public String getMemo3() {
		return memo3;
	}

	public void setMemo3(String memo3) {
		this.memo3 = memo3;
	}

	public String getCert_md5() {
		return cert_md5;
	}

	public void setCert_md5(String cert_md5) {
		this.cert_md5 = cert_md5;
	}

	public String getDes_key() {
		return des_key;
	}

	public void setDes_key(String des_key) {
		this.des_key = des_key;
	}

	public String getDes_iv() {
		return des_iv;
	}

	public void setDes_iv(String des_iv) {
		this.des_iv = des_iv;
	}

	
}
