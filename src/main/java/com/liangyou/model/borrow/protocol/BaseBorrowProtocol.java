package com.liangyou.model.borrow.protocol;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseBorrowProtocol extends BorrowProtocol {
    public BaseBorrowProtocol(User user, long borrow_id, long tender_id, int borrowType, int templateType) {
        super(user, borrow_id, tender_id, borrowType, templateType);
    }

    public BaseBorrowProtocol(User user, long borrow_id, long tender_id) {
        super(user, borrow_id, tender_id);
    }

    @Override
    protected void addPdfTable(PdfHelper pdf, Borrow b) throws DocumentException {
        List<BorrowTender> list = b.getBorrowTenders();
        String repayType = borrowType(NumberUtils.getInt(b.getStyle()));  //获取还款方式
        List cellList = null;
        List<String>[] args = new List[list.size() + 1];
        // 出借人(id)
        cellList = new ArrayList();
        cellList.add("出借人");
        cellList.add("借款金额");
        cellList.add("借款期限");
        cellList.add("年利率(单利)");
        cellList.add("借款开始日");
        cellList.add("借款到期日");
        cellList.add("每月截止还款日");
        cellList.add("到期应收总额");
        cellList.add("还款方式");
        args[0] = cellList;
        for (int i = 1; i < list.size() + 1; i++) {
            BorrowTender t = list.get(i - 1);
            // edit by gy 2017-03-14 15:56:23  begin
            // 修改借款协议中，出借列表只展示当前用户一条
            if (t.getId() == getTender_id()) {
                cellList = new ArrayList();
                cellList.add(t.getUser().getUsername() + "");
                cellList.add("￥" + t.getAccount() + "");
                cellList.add(b.getIsday() == 1 ? b.getTimeLimitDay() + "天" : b.getTimeLimit() + "月");
                cellList.add(b.getApr() + "%");
                if (b.getVerifyTime() != null) {
                    //借款时间格式转换
                    Date addTime = b.getVerifyTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String startTime = sdf.format(addTime);
                    cellList.add(startTime); // 借款开始日期
                    Date repayTime = null;
                    if (b.getIsday() == 1) {
                        repayTime = DateUtils.rollDay(b.getVerifyTime(), b.getTimeLimitDay());//计算借款期限
                    } else {
                        repayTime = DateUtils.rollMon(b.getVerifyTime(), b.getTimeLimit());//计算借款期限
                    }
                    String endTimeStr = sdf.format(repayTime);
                    cellList.add(endTimeStr);
                    cellList.add("每月截止" + DateUtils.getDay(b.getVerifyTime()) + "号");//计算每个月的还款日期
                } else {
                    cellList.add("--");
                    cellList.add("--");
                    cellList.add("--");
                }
                cellList.add("￥" + t.getRepaymentAccount() + "");
                cellList.add(repayType);
                args[i] = cellList;

                break;
            }
            // end

        }
        float cellWidth = 50;
        int num = 10;
        float[] widths = {cellWidth, cellWidth, cellWidth, cellWidth, cellWidth, cellWidth, cellWidth, cellWidth, cellWidth};
        PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
        table.setSpacingBefore(10f);// 设置表格上面空白宽度
        table.setSpacingBefore(10f);
        table.setTotalWidth(cellWidth * num);// 设置表格的宽度
        table.setLockedWidth(true);// 设置表格的宽度固定
        PdfPCell cell = null;
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) { // 由于上面的args数据不再是全部列表， 所以为空的情况应跳过，不然会空指针
                    continue;
                }
                for (String s : args[i]) {
                    cell = new PdfPCell(new Paragraph(s, pdf.getFont()));//new PdfPCell(new Paragraph(s, pdf.getFont()));// 建立一个单元格
                    table.addCell(cell);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //最后一行
        cell = new PdfPCell(new Paragraph("借款总金额", pdf.getFont()));// 建立一个单元格
        table.addCell(cell);
        cell = new PdfPCell(new Paragraph(b.getAccount() + "", pdf.getFont()));// 建立一个单元格
        cell.setColspan(8);
        table.addCell(cell);
        pdf.addTable(table);
    }

    /**
     * 获取标的还款方式
     *
     * @param type
     * @return
     */
    private String borrowType(int type) {
        switch (type) {
            case 0:
                return "月等额本息";
            case 1:
                return "无";
            case 2:
                return "一次性还本付息";
            case 3:
                return "月还息，到期还本";
            case 4:
                return "只还本金";
            default:
                return "方式不确定";
        }
    }

}
