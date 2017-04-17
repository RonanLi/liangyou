package com.liangyou.tool.wechatkit;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author lijing
 * 根据文件头数据判断文件类型
 *
 */

public class FileType {
	public static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

	private FileType() {
	}

	static {
		getAllFileType();// 初始化文件类型信息
	}

	/**
	 * 常见文件头信息
	 */
	
	/**
	 * 实测 取前10位有重合问题 具体遇到具体解决
	 */
	private static void getAllFileType() {
		FILE_TYPE_MAP.put("b1bec8edbcfecfc2d4d8", ".txt"); // JPEG (jpg)
		FILE_TYPE_MAP.put("ffd8ffe000104a464946", ".jpg"); // JPEG (jpg)
		FILE_TYPE_MAP.put("89504e470d0a1a0a0000", ".png"); // PNG (png)
		FILE_TYPE_MAP.put("47494638396126026f01", ".gif"); // GIF (gif)
		FILE_TYPE_MAP.put("49492a00227105008037", ".tif"); // TIFF (tif)
		FILE_TYPE_MAP.put("424d228c010000000000", ".bmp"); // 16色位图(bmp)
		FILE_TYPE_MAP.put("424d8240090000000000", ".bmp"); // 24位位图(bmp)
		FILE_TYPE_MAP.put("424d8e1b030000000000", ".bmp"); // 256色位图(bmp)
		FILE_TYPE_MAP.put("41433130313500000000", ".dwg"); // CAD (dwg)
		FILE_TYPE_MAP.put("3c21444f435459504520", ".html"); // HTML (html)
		FILE_TYPE_MAP.put("3c21646f637479706520", ".htm"); // HTM (htm)
		FILE_TYPE_MAP.put("48544d4c207b0d0a0942", ".css"); // css
		FILE_TYPE_MAP.put("696b2e71623d696b2e71", ".js"); // js
		FILE_TYPE_MAP.put("7b5c727466315c616e73", ".rtf"); // Rich Text Format (rtf)
		FILE_TYPE_MAP.put("38425053000100000000", ".psd"); // Photoshop (psd)
		FILE_TYPE_MAP.put("46726f6d3a203d3f6762", ".eml"); // Email [Outlook Express 6] (eml)
		FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", ".doc"); // MS Excel 注意：word、msi 和  excel的文件头一样
		FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", ".vsd"); // Visio 绘图
		FILE_TYPE_MAP.put("5374616E64617264204A", ".mdb"); // MS Access (mdb)
		FILE_TYPE_MAP.put("252150532D41646F6265", ".ps");
		FILE_TYPE_MAP.put("255044462d312e350d0a", ".pdf"); // Adobe Acrobat (pdf)
		FILE_TYPE_MAP.put("2e524d46000000120001", ".rmvb"); // rmvb/rm相同
		FILE_TYPE_MAP.put("464c5601050000000900", ".flv"); // flv与f4v相同
		FILE_TYPE_MAP.put("00000020667479706d70", ".mp4");
		FILE_TYPE_MAP.put("49443303000000002176", ".mp3");
		FILE_TYPE_MAP.put("000001ba210001000180", ".mpg"); //
		FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", ".wmv"); // wmv与asf相同
		FILE_TYPE_MAP.put("52494646e27807005741", ".wav"); // Wave (wav)
		FILE_TYPE_MAP.put("52494646d07d60074156", ".avi");
		FILE_TYPE_MAP.put("4d546864000000060001", ".mid"); // MIDI (mid)
		FILE_TYPE_MAP.put("504b0304140000000800", ".zip");
		FILE_TYPE_MAP.put("526172211a0700cf9073", ".rar");
		FILE_TYPE_MAP.put("235468697320636f6e66", ".ini");
		FILE_TYPE_MAP.put("504b03040a0000000000", ".jar");
		FILE_TYPE_MAP.put("4d5a9000030000000400", ".exe");// 可执行文件
		FILE_TYPE_MAP.put("3c25402070616765206c", ".jsp");// jsp文件
		FILE_TYPE_MAP.put("4d616e69666573742d56", ".mf");// MF文件
		FILE_TYPE_MAP.put("3c3f786d6c2076657273", ".xml");// xml文件
		FILE_TYPE_MAP.put("494e5345525420494e54", ".sql");// xml文件
		FILE_TYPE_MAP.put("7061636b616765207765", ".java");// java文件
		FILE_TYPE_MAP.put("406563686f206f66660d", ".bat");// bat文件
		FILE_TYPE_MAP.put("1f8b080045d4ba570203", ".gz");// gz文件
		FILE_TYPE_MAP.put("1f8b0800000000000000", ".gz");// gz文件
		FILE_TYPE_MAP.put("6c6f67346a2e726f6f74", ".properties");// bat文件
		FILE_TYPE_MAP.put("cafebabe0000002e0041", ".class");// bat文件
		FILE_TYPE_MAP.put("49545346030000006000", ".chm");// bat文件
		FILE_TYPE_MAP.put("04000000010000001300", ".mxp");// bat文件
		FILE_TYPE_MAP.put("504b0304140006000800", ".docx");// docx文件
		FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", ".wps");// WPS文字wps、表格et、演示dps都是一样的
		FILE_TYPE_MAP.put("6431303a637265617465", ".torrent");
		FILE_TYPE_MAP.put("6D6F6F76", ".mov"); // Quicktime (mov)
		FILE_TYPE_MAP.put("FF575043", ".wpd"); // WordPerfect (wpd)
		FILE_TYPE_MAP.put("CFAD12FEC5FD746F", ".dbx"); // Outlook Express (dbx)
		FILE_TYPE_MAP.put("2142444E", ".pst"); // Outlook (pst)
		FILE_TYPE_MAP.put("AC9EBD8F", ".qdf"); // Quicken (qdf)
		FILE_TYPE_MAP.put("E3828596", ".pwl"); // Windows Password (pwl)
		FILE_TYPE_MAP.put("2E7261FD", ".ram"); // Real Audio (ram)
		FILE_TYPE_MAP.put("000000000000000000000000", ".txt"); // txt 文本文档
	}

	/**
	 * 获取上传文件的文件头
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder sb = new StringBuilder();
		if (src == null || src.length < 1) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;// 位运算 java中二进制采用补码形式
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb.append("0");
			}
			sb.append(hv);
		}
		return sb.toString();
	}
	
	public static String getFileType(String filePath){
		String res = null;
		InputStream inputStream = null;
		try {
			if(filePath.startsWith("http")){
				URL url = new URL(filePath);
				inputStream = url.openStream();
			}else {
				inputStream = new FileInputStream(filePath);
			}
			byte[] b = new byte[10];
			inputStream.read(b, 0, b.length);
			String fileCode = bytesToHexString(b);
			System.out.println(fileCode);
			  //这种方法在字典的头代码不够位数的时候可以用但是速度相对慢一点
            Iterator<String> keyIter = FILE_TYPE_MAP.keySet().iterator();
            while(keyIter.hasNext()){
                String key = keyIter.next();
                if(key.toLowerCase().startsWith(fileCode.toLowerCase()) || fileCode.toLowerCase().startsWith(key.toLowerCase())){
                    res = FILE_TYPE_MAP.get(key);
                    break;
                }
            }
            inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
    public static void main(String[] args) throws Exception {
        //上传文件类型测试
        String type = getFileType("http://120.27.49.72/data/wechat/1488419740521.jpg");
        System.out.println("上传文件类型 1: "+type);
        System.out.println(); 
        String type2 = getFileType("C:/Users/1432/Desktop/使用说明.txt");
        System.out.println("上传文件类型 2: "+type2);
       /* System.out.println(); 
        String type3 = getFileType("C:/Users/1432/Desktop/lib/activation-1.1.jar");
        System.out.println("上传文件类型 3: "+type3);
        System.out.println(); 
        String type4 = getFileType("C:/Users/1432/Desktop/lib/allinpay-security-2.1.3.jar");
        System.out.println("上传文件类型 :4 "+type4);
        System.out.println(); 
        String type5 = getFileType("C:/Users/1432/Desktop/微信开发.docx");
        System.out.println("上传文件类型 : 5"+type5);//10位果然是不够判断的
        System.out.println(); 
        String type6 = getFileType("C:/Users/1432/Desktop/新建文本文档.txt");
        System.out.println("上传文件类型 : 6"+type6);//10位果然是不够判断的
        System.out.println(); 
        String type7 = getFileType("C:/Users/1432/Desktop/ceshiu.txt");
        System.out.println("上传文件类型 : 7"+type7);//10位果然是不够判断的
        System.out.println(); */
        
        
    }
	
}