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
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowRepayment;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.Linkage;
import com.liangyou.domain.User;
import com.liangyou.model.SearchParam;
import com.liangyou.tool.itext.PdfHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.util.UpNumber;

/**
 * @author wujing 
 * @version 创建时间：2013-12-24 下午2:33:47
 * 类说明:国控小微协议模板生成类
 */
public class GkxwProtocol extends BorrowProtocol {
	
	public GkxwProtocol(User user, long borrow_id, long tender_id,
			int borrowType, int templateType){
		super(user, borrow_id, tender_id, 105, templateType);
	}
	
	public GkxwProtocol(User user, long borrow_id, long tender_id){
		super(user, borrow_id, tender_id);
	}
	
	
	
	

	@Override
	protected Map<String, Object> fillProtoclData() {
		Map<String, Object> date = super.fillProtoclData();
		date.put("borrowagreementNO", getBorrow().getBorrowProperty().getBorrowagreementNO());
		date.put("borrowNo", getBorrow().getBorrowProperty().getBorrowNo());
		date.put("warrantName", getBorrow().getBorrowProperty().getWarrant().getName());
		//v1.8.0.4_u2 TGPROJECT-299 lx start
		if(getTender_id() == 0){
			Borrow borrow = getBorrow();
			List<BorrowTender> tenderList = borrow.getBorrowTenders();
			String tenderStr = "";
			double account = 0;
			for (int i = 0; i < tenderList.size(); i++) {
				BorrowTender tender = tenderList.get(i);
				if(i > tenderList.size()-1 ){
					tenderStr += tender.getUser().getUsername() + "   ";
				}else{
					tenderStr += tender.getUser().getUsername();
				}
				account += tender.getAccount();
			}
			date.put("tenderUserName",  tenderStr);
			date.put("tenderAccount", account);
			date.put("tenderAccountUpnum", UpNumber.toChinese(account+""));
		}else{
			date.put("tenderNo", getTender().getTenderProperty().getTenderNo());
			date.put("tenderUserName",  getTender().getUser().getUsername());
			date.put("tenderAccountUpnum", UpNumber.toChinese(getTender().getAccount()+""));
			date.put("tenderAccount", getTender().getAccount());
			date.put("tenderCardId",  getTender().getUser().getCardId());
			date.put("tenderRealName",  getTender().getUser().getRealname());
		}
		
		//v1.8.0.4_u2 TGPROJECT-299 lx end
		//v18.0.3_u5  zxc start
		date.put("borrowUserRealName", getBorrow().getUser().getRealname());
		date.put("borrowUserCardId", getBorrow().getUser().getCardId());
		date.put("borrowUserName", getBorrow().getUser().getUsername());
		date.put("upBorrrowAccount", StringUtils.upNumb(getBorrow().getAccount()));
		//v18.0.3_u5  zxc end
		
		return date;
	}
	

	@Override
	protected void initBorrowType() {
		
		setBorrowType(105);
	}

	@Override
	protected void addPdfTable(PdfHelper pdf, Borrow b)
			throws DocumentException {
		Linkage linkage = getLinkageService().getLinkageById(NumberUtils.getInt(b.getUsetype()));
		List cellList = new ArrayList();
		List<String>[] args = new List[7];
		//v1.8.0.4_u2 TGPROJECT-299 lx start
		cellList.add("借款总额");
		cellList.add(b.getAccount()+"元人民币");
		args[0]=cellList;
	/*	cellList.add("本次借款最低筹集金额");
		cellList.add(b.getAccount()+"元人民币");
		args[1] = cellList;*/
		cellList.add("借款用途");
		String useType = "未填写";
		if(null !=linkage){
			useType=linkage.getName();
		}
		cellList.add(useType);
		args[1] = cellList;
		cellList.add("借款期限");
		cellList.add(b.getTimeLimit()+"个月");
		args[2]=(cellList);
		cellList.add("借款期间");
		cellList.add(DateUtils.newdateStr6(DateUtils.rollDay(b.getVerifyTime(), 1))+"(借款起始日至)"+DateUtils.newdateStr6(DateUtils.rollMon(b.getVerifyTime(), b.getTimeLimit()))+"(借款结束日)");
		args[3]=(cellList);
		cellList.add("起息日");
		cellList.add(DateUtils.newdateStr6(DateUtils.rollDay(b.getVerifyTime(), 1)));
		args[4]=cellList;
		cellList.add("借款利率");
		cellList.add(b.getApr()+"%/年");
		args[5] = cellList;
		cellList.add("还款方式");
		cellList.add("一次性还款、利随本清");
		args[6]=cellList;
		//cellList.add("约定还款日");
		//cellList.add("默认为借款结束日,如借款结束日为非工作日的，顺延至借款结束日后的第一个工作日");
		//args[8] = cellList;
		
		//v1.8.0.4_u2 TGPROJECT-299 lx end
		float cellWidth=100;
		float[] widths = {cellWidth, 350};
		PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
		table.setSpacingBefore(10f);// 设置表格上面空白宽度
		//table.setSpacingBefore(10f);
		table.setTotalWidth(450);// 设置表格的宽度
		table.setLockedWidth(true);// 设置表格的宽度固定
		PdfPCell cell = null;
		try {
			for (int i = 0; i < 1; i++) {
				for (String s : args[i]) {
					cell = new PdfPCell(new Paragraph(s, pdf.getFont()));//new PdfPCell(new Paragraph(s, pdf.getFont()));// 建立一个单元格
					table.addCell(cell);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.addCell(cell);
		pdf.addTable(table);
	}
	//v1.8.0.4_u2 TGPROJECT-299 lx start
	@Override
	protected void addTenderPlainPdfTable(PdfHelper pdf, Borrow b)
			throws DocumentException {
		List<BorrowCollection> collectionList = new ArrayList<BorrowCollection>(); 
		collectionList=getBorrowService().getCollectionList(b.getId()); 
		List<String> cellList = null;
		List<String>[] args = new ArrayList[collectionList.size() + 1];
		cellList = new ArrayList<String>();
		cellList.add("国控小微用户名");
		cellList.add("约定还款日");
		cellList.add("还款本金");
		cellList.add("利息");
		cellList.add("还款总额");
		args[0] = cellList;
		if(getTender_id() == 0){
			for (int i = 1; i < collectionList.size() + 1; i++) {
				BorrowCollection bc = collectionList.get(i - 1);
				cellList = new ArrayList<String>();
				cellList.add(bc.getBorrowTender().getUser().getUsername());
				Date date=bc.getRepayTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				String startTime = sdf.format(date);
				cellList.add(startTime);
				cellList.add(bc.getCapital()+"");
				cellList.add(bc.getInterest()+"");
				cellList.add(bc.getRepayAccount()+"");
				args[i] = cellList;
			}
		}else{
			for (int i = 1; i < collectionList.size() + 1; i++) {
				BorrowCollection bc = collectionList.get(i - 1);
				cellList = new ArrayList<String>();
				if(getTender_id()==bc.getBorrowTender().getId()){
					cellList.add(bc.getBorrowTender().getUser().getUsername());
				}else{
					cellList.add(StringUtils.hideLastChar(bc.getBorrowTender().getUser().getUsername(),3));
				}
				Date date=bc.getRepayTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
				String startTime = sdf.format(date);
				cellList.add(startTime);
				cellList.add(bc.getCapital()+"");
				cellList.add(bc.getInterest()+"");
				cellList.add(bc.getRepayAccount()+"");
				args[i] = cellList;
			}
		}
		
		
		float cellWidth=90;
		int num=5;
		float[] widths = {cellWidth, cellWidth, cellWidth,cellWidth, cellWidth};
		PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
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
		pdf.addTable(table);
	}
	@Override
	protected void addTenderPropertyPdfTable(PdfHelper pdf, Borrow b)
			throws DocumentException {
		List<BorrowTender> tenderList = new ArrayList<BorrowTender>(); 
		tenderList=getBorrow().getBorrowTenders(); 
		List<String> cellList = null;
		List<String>[] args = new ArrayList[tenderList.size() + 1];
		cellList = new ArrayList<String>();
		cellList.add("国控小微用户名");
		cellList.add("出借申请号");
		cellList.add("出借金额");
		args[0] = cellList;
		if(getTender_id() == 0){
			for (int i = 1; i < tenderList.size() + 1; i++) {
				BorrowTender bt = tenderList.get(i - 1);
				cellList = new ArrayList<String>();
				StringBuffer sb=new StringBuffer();
				sb.append(bt.getUser().getUsername()).append("  姓名： ")
				.append(bt.getUser().getRealname()).append(", 身份证号： ")
				.append(bt.getUser().getCardId());
				cellList.add(sb.toString());
				cellList.add(bt.getTenderProperty().getTenderNo()+"");
				cellList.add(bt.getAccount()+"");
				args[i] = cellList;
			}
		}else{
			for (int i = 1; i < tenderList.size() + 1; i++) {
				BorrowTender bt = tenderList.get(i - 1);
				cellList = new ArrayList<String>();
				StringBuffer sb=new StringBuffer();
				if(getTender_id()==bt.getId()){
					sb.append(bt.getUser().getUsername()).append("  姓名： ")
					.append(bt.getUser().getRealname()).append(", 身份证号： ")
					.append(bt.getUser().getCardId());
				}else{
					sb.append(StringUtils.hideLastChar(bt.getUser().getUsername(),3));
				}
				cellList.add(sb.toString());
				cellList.add(bt.getTenderProperty().getTenderNo()+"");
				cellList.add(bt.getAccount()+"");
				args[i] = cellList;
			}
		}
		//float cellWidth=150;
		int num=3;
		float[] widths = {270, 100, 80};
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
	//v1.8.0.4_u2 TGPROJECT-299 lx start
	
}
