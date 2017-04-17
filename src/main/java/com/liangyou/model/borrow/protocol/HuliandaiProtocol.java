package com.liangyou.model.borrow.protocol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.tool.itext.PdfHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.util.UpNumber;

//v1.8.0.3 TGPROJECT-28 lx 2014-04-08 start
public class HuliandaiProtocol extends BorrowProtocol {
	
	public HuliandaiProtocol(User user, long borrow_id, long tender_id,
			int borrowType, int templateType){
		super(user, borrow_id, tender_id, 105, templateType);
	}
	
	public HuliandaiProtocol(User user, long borrow_id, long tender_id){
		super(user, borrow_id, tender_id);
	}
	//v1.8.0.4_u1  TGPROJECT-268   qj  2014-05-06 start 
	@Override
	protected Map<String, Object> fillProtoclData() {
		Map<String, Object> date = super.fillProtoclData();
		date.put("borrowAccount", getBorrow().getAccount());
		if(getTender_id() == 0){
			Borrow borrow = getBorrow();
			List<BorrowTender> tenderList = borrow.getBorrowTenders();
			String tenderStr = "";
			double account = 0;
			for (int i = 0; i < tenderList.size(); i++) {
				BorrowTender tender = tenderList.get(i);
				if(i < tenderList.size()-1 ){
					tenderStr += tender.getUser().getUsername() + "，";
				}else{
					tenderStr += tender.getUser().getUsername();
				}
				account += tender.getAccount();
			}
			date.put("tenderUserName",  tenderStr);
			date.put("tenderAccount", account);
			date.put("tenderAccountUpnum", UpNumber.toChinese(account+""));
		}else{
			date.put("tenderUserName",  getTender().getUser().getUsername());
			date.put("tenderAccountUpnum", UpNumber.toChinese(getTender().getAccount()+""));
			date.put("tenderAccount", getTender().getAccount());
			date.put("tenderCardId",  getTender().getUser().getCardId());
			date.put("tenderRealName",  getTender().getUser().getRealname());
			date.put("upBorrrowAccount", StringUtils.upNumb(getTender().getAccount()));
		}
		date.put("borrowUserName",  getBorrow().getUser().getUsername());
		date.put("borrowCardId",  getBorrow().getUser().getCardId());
		date.put("borrowRealName",  getBorrow().getUser().getRealname());
		date.put("verifyTime", DateUtils.newdateStr6(getBorrow().getVerifyTime()));
		date.put("timeLimit", getBorrow().getTimeLimit());
		date.put("apr", getBorrow().getApr());
		return date;
	}
	//v1.8.0.4_u1  TGPROJECT-268   qj  2014-05-06 end 

	@Override
	protected void initBorrowType() {
		setBorrowType(105);
	}
	//v1.8.0.4_u1  TGPROJECT-268   qj  2014-05-06 start 
	@Override
	protected void addPdfTable(PdfHelper pdf, Borrow b)
			throws DocumentException {
		List<BorrowTender> tenderList = new ArrayList<BorrowTender>(); 
		if(getTender_id() != 0){
			tenderList.add(getBorrowService().getTenderById(getTender_id()));
		}else{
			tenderList = b.getBorrowTenders(); 
		}
		List<String> cellList = null;
		List<String>[] args = new ArrayList[tenderList.size() + 1];
		// 出借人(id)
		cellList = new ArrayList<String>();
		if(getTender_id() == 0){
			cellList.add("出借人");
		}
		cellList.add("借款金额");
		cellList.add("借款期限");
		cellList.add("月息");
		cellList.add("借款开始日");
		cellList.add("借款到期日");
		cellList.add("每月截止还款日");
		cellList.add("到期应收总额");
		cellList.add("还款方式");
		args[0] = cellList;
		for (int i = 1; i < tenderList.size() + 1; i++) {
			BorrowTender t = tenderList.get(i - 1);
			cellList = new ArrayList<String>();
			if(getTender_id() == 0){
				cellList.add(t.getUser().getUsername());
			}
			cellList.add(t.getAccount()+"");
			cellList.add(b.getTimeLimit()+"期");
			cellList.add(NumberUtils.format2Str(b.getApr()/12)+"%");
			
			//借款时间格式转换
			Date addTime = t.getAddtime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String startTime = sdf.format(addTime);
			cellList.add(startTime);
			Date repayTime = b.getBorrowRepayments().get(b.getBorrowRepayments().size()-1).getRepaymentTime();
			String endTimeStr =  sdf.format(repayTime);
			cellList.add(endTimeStr);
			int day = DateUtils.getDay(repayTime);
			cellList.add("每月截止"+day+"号");
			
			cellList.add("￥"+t.getRepaymentAccount());
			cellList.add("每月还息，到期还本");
			args[i] = cellList;
		}
		
		float cellWidth=80;
		int num=7;
		PdfPTable table = null;// 建立一个pdf表格
		if(getTender_id() == 0){
			float[] widths = {cellWidth, cellWidth, cellWidth,cellWidth, cellWidth, cellWidth, cellWidth, cellWidth,cellWidth };
			table = new PdfPTable(widths);
		}else{
			float[] widths = {cellWidth, cellWidth, cellWidth,cellWidth, cellWidth, cellWidth, cellWidth, cellWidth };
			table = new PdfPTable(widths);
		}
		table.setSpacingBefore(10f);// 设置表格上面空白宽度
		table.setSpacingBefore(10f);
		table.setTotalWidth(cellWidth * num);// 设置表格的宽度
		table.setLockedWidth(true);// 设置表格的宽度固定
		PdfPCell cell = null;
		for (int i = 0; i < args.length; i++) {
			for (String s : args[i]) {
				cell = new PdfPCell(new Paragraph(s, pdf.getFont()));// 建立一个单元格
				table.addCell(cell);
			}
		}
		if(getTender_id() == 0){
			cell = new PdfPCell(new Paragraph("借款总金额（元）", pdf.getFont()));// 建立一个单元格
			cell.setColspan(2);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("￥"+b.getAccount()+"", pdf.getFont()));// 建立一个单元格
			cell.setColspan(7);
			table.addCell(cell);
		}
		pdf.addTable(table);
	}
	//v1.8.0.4_u1  TGPROJECT-268   qj  2014-05-06 end 
	
	@Override
	protected void addTenderPropertyPdfTable(PdfHelper pdf, Borrow b)
			throws DocumentException {
		List<BorrowTender> tenderList = new ArrayList<BorrowTender>(); 
		tenderList=getBorrow().getBorrowTenders(); 
		List<String> cellList = null;
		List<String>[] args ;
		if(getTender_id() != 0){
			args =  new ArrayList[2];
		}else{
			args =  new ArrayList[tenderList.size() + 1];
		}
		cellList = new ArrayList<String>();
		cellList.add("网站用户");
		cellList.add("真实姓名");
		cellList.add("身份证号");
		args[0] = cellList;
		if(getTender_id() != 0){
			cellList = new ArrayList<String>();
			cellList.add(getTender().getUser().getUsername());
			cellList.add(getTender().getUser().getRealname());
			cellList.add(getTender().getUser().getCardId());
			args[1] = cellList;
		}else{
			for (int i = 1; i < tenderList.size() + 1; i++) {
				BorrowTender bt = tenderList.get(i - 1);
				cellList = new ArrayList<String>();
				cellList.add(bt.getUser().getUsername());
				cellList.add(bt.getUser().getRealname());
				cellList.add(bt.getUser().getCardId());
				args[i] = cellList;
			}
		}
		float[] widths = {100, 100, 100};
		PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
		table.setSpacingBefore(10f);// 设置表格上面空白宽度
		table.setSpacingBefore(10f);
		table.setTotalWidth(450);// 设置表格的宽度
		table.setLockedWidth(true);// 设置表格的宽度固定
		PdfPCell cell = null;
		for (int i = 0; i < args.length; i++) {
			for (String s : args[i]) {
				cell = new PdfPCell(new Paragraph(s, pdf.getFont()));// 建立一个单元格
				table.addCell(cell);
			}
		}
		pdf.addTable(table);
	}
	
}
//v1.8.0.3 TGPROJECT-28 lx 2014-04-08 end