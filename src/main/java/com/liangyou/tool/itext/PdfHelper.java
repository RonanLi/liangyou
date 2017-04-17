package com.liangyou.tool.itext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.List;

import org.apache.log4j.Logger;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.liangyou.context.Global;
import com.liangyou.exception.BussinessException;

public class PdfHelper {

	private Logger logger = Logger.getLogger(PdfHelper.class);
	private Document document;

	private BaseFont bfChinese;
	private Font font;
	private String webid=Global.getValue("webid");

	public String getWebid() {
		return webid;
	}

	public void setWebid(String webid) {
		this.webid = webid;
	}

	public PdfHelper(String path) {
		document = new Document();
		File file = new File(path);
		if(!file.getParentFile().exists()) {//判断父路劲是否存在
			file.getParentFile().mkdir();
		}
		try {
			PdfWriter.getInstance(document, new FileOutputStream(path,true));// 建立一个PdfWriter对象
			document.open();
			bfChinese = BaseFont.createFont("STSong-Light",
					"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);// 设置中文字体
			font = new Font(bfChinese, 10, Font.NORMAL);// 设置字体大小
		} catch (Exception de) {
			logger.error(de);
			throw new BussinessException("生成PDF出错");
		} 
	}

	public int getPageNumber(){
		if(document==null){
			return 0;
		}
		return document.getPageNumber();
	}
	
	public static PdfHelper instance(String path) {
		return new PdfHelper(path);
	}

	public void addTable(List<String>[] data, float cellWidth, int num)
			throws DocumentException {
		float[] widths = {cellWidth, cellWidth, cellWidth,cellWidth, cellWidth, cellWidth, cellWidth, cellWidth };
		PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
		table.setSpacingBefore(10f);// 设置表格上面空白宽度
		table.setTotalWidth(cellWidth * num);// 设置表格的宽度
		table.setLockedWidth(true);// 设置表格的宽度固定
		
		PdfPCell cell = null;
		for (int i = 0; i < data.length; i++) {
			logger.info(data[i].toString());
			for (String s : data[i]) {
				if(s == null){
					s = "--";
				}
				cell = new PdfPCell(new Paragraph(s, font));// 建立一个单元格
				table.addCell(cell);
			}
		}
		document.add(table);
	}

	public void addTable(PdfPTable table)
			throws DocumentException {
		document.add(table);
	}
	
	public void addTitle(String text) throws DocumentException {
		font = new Font(bfChinese, 12, Font.BOLD);// 设置字体大小
		Paragraph p = new Paragraph(text, font);
		 String webid=Global.getValue("webid");
		if(webid!=null&&webid.equals("mdw")){
			 p.setAlignment(Element.ALIGN_RIGHT);
			 p.setIndentationLeft(Element.ALIGN_MIDDLE);
		}else{
		p.setAlignment(Element.ALIGN_CENTER);
		}
		p.setSpacingAfter(20f);
		document.add(p);
	}	
	
	public void addText(String text) throws DocumentException {
		Paragraph p = new Paragraph(text, font);
		p.setSpacingAfter(10f);
		
		p.setAlignment(Element.ALIGN_LEFT);
		p.setIndentationLeft(1f);
		document.add(p);
	}

	@SuppressWarnings("deprecation")
	public void addHtml(String html) throws Exception {
		StyleSheet st = new StyleSheet();
		List list = HTMLWorker.parseToList(new StringReader(html), st);
		for (int k = 0; k < list.size(); ++k) {
			document.add((Element) list.get(k));
		}
	}

	public void exportPdf() {
		document.close();
	}

	public void freemarker() {

	}

	public static void addPdfMark(String InPdfFile, String outPdfFile,
			String markImagePath,int size) throws Exception {
		PdfReader reader = new PdfReader(InPdfFile);
		PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(outPdfFile));
		Image img = Image.getInstance(markImagePath);// 插入水印
		img.setAbsolutePosition(15, 10);
		for (int i = 1; i <=size; i++) {
			PdfContentByte under = stamp.getUnderContent(i);
			under.addImage(img);
		}
		stamp.close();// 关闭
		File tempfile = new File(InPdfFile);
		if (tempfile.exists()) {
			tempfile.delete();
		}
	}
	public  void addImage(Image image){
		try {
			image.setAlignment(Image.LEFT);
			image.setAlignment(Image.UNDERLYING);
			image.setAbsolutePosition(120, 560);
			image.scaleAbsolute(70.0f, 70.0f);
			document.add(image);
		} catch (BadElementException e) {
			logger.error(e);
		} catch (DocumentException e) {
			logger.error(e);
		}
	}
	
	public void addHtmlList(List<Element> list) throws DocumentException {
		for(Element e:list){
			document.add(e);
		}
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
	
}
