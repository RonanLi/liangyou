package com.liangyou.model.borrow.protocol;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.WritableDirectElement;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.ElementHandler;
import com.itextpdf.tool.xml.Writable;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.pipeline.WritableElement;
import com.liangyou.context.Global;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.Site;
import com.liangyou.domain.User;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.SearchParam;
import com.liangyou.service.ArticleService;
import com.liangyou.service.BorrowService;
import com.liangyou.service.LinkageService;
import com.liangyou.service.UserService;
import com.liangyou.tool.itext.PdfHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.web.action.BorrowAction;

public class BorrowProtocol {
    private static Logger logger = Logger.getLogger(BorrowAction.class);

    @Autowired
    private BorrowService borrowService;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private LinkageService linkageService;

    private String siteName;
    private long borrow_id;
    private long tender_id;
    private String pdfName;
    private String inPdfName;
    private String outPdfName;
    private String downloadFileName;
    private String imageFileName;
    private ServletContext context;
    private Borrow borrow;
    private BorrowTender tender;
    protected User tenderUser;
    private User borrowUser;
    private PdfHelper pdf;
    private int borrowType;//对应标种的编码 默认为100，表示区分标种；
    private int templateType;//模板的类型：1表示读协议栏目，默认为1； 2表示文件模板
    private TemplateReader templateReader;
    private Map<String, Object> data = new HashMap<String, Object>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BorrowProtocol() {
        super();
    }

    public BorrowProtocol(User user, long borrow_id, long tender_id, int borrowType, int templateType) {
        super();
        init(user, borrow_id, tender_id, borrowType, templateType);
    }

    public BorrowProtocol(User user, long borrow_id, long tender_id) {
        super();
        init(user, borrow_id, tender_id, 100, 1);
    }

    private void init(User tenderUser, long borrow_id, long tender_id, int borrowType, int templateType) {
        this.borrow_id      = borrow_id;
        this.tender_id      = tender_id;
        this.tenderUser     = tenderUser;
        this.borrowType     = borrowType;
        this.templateType   = templateType;
        this.context        = ContextLoader.getCurrentWebApplicationContext().getServletContext();
        String contextPath  = context.getRealPath("/").replaceAll("\\\\", "/");
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
        borrowService       = (BorrowService)   ctx.getBean("borrowService");
        userService         = (UserService)     ctx.getBean("userService");
        articleService      = (ArticleService)  ctx.getBean("articleService");
        linkageService      = (LinkageService)  ctx.getBean("linkageService");
        this.borrow         = borrowService.getBorrow(borrow_id);
        this.tender         = borrowService.getTenderById(getTender_id());
        int tenderIndex     = 0;
        List<BorrowTender> borrowTenderList = borrow.getBorrowTenders();
        if (borrowTenderList != null) {
            for (int i = 0; i < borrowTenderList.size(); i++) {
                if (getTender_id() == borrowTenderList.get(i).getId()) {
                    tenderIndex = i + 1;
                    break;
                }
            }
        }
        this.downloadFileName    = Global.getValue("pdftemplate").toUpperCase() + DateUtils.newdateStr2(new Date()) + "-" + borrow.getId() + "-" + tenderIndex + ".pdf";
        this.inPdfName           = contextPath + "data/protocol/借款电子协议" + downloadFileName /*" + borrow_id + "_"+ tender_id+"_"+timeStr + ".pdf"*/;
        this.imageFileName       = "" + Global.getValue("weburl") + Global.getValue("theme_dir") + "/images/zhang.jpg";
        this.outPdfName          = contextPath + "data/protocol/借款电子协议" + downloadFileName /*" + borrow_id + "_"+ tender_id +"_"+timeStr+ ".pdf"*/;
        this.pdf                 = PdfHelper.instance(inPdfName);
    }

    protected void initBorrowType() {

    }

    protected Map<String, Object> fillProtoclData() {
        SimpleDateFormat sdf_year = new SimpleDateFormat("yyyyMMdd");
        String year = sdf_year.format(new Date());
        data.put("nowYear", year);
        data.put("webname", Global.getString("webname"));
        data.put("address", Global.getString("address"));
        data.put("weburl", Global.getString("weburl"));
        data.put("borrow", borrow);
        data.put("borrowNO", borrow.getId());
        data.put("borrowUser", borrowUser);
        data.put("borrowUsername", borrow.getUser().getUsername());
        if (tender != null) {
            data.put("borrowTender", tender);
            data.put("tendername", tender.getUser().getRealname());
            data.put("tenderUserName", tender.getUser().getUsername());
        }
        if (borrow.getVerifyTime() != null) {
            data.put("verytime", DateUtils.newdateStr6(borrow.getVerifyTime()));
        } else {
            data.put("verytime", "");
        }
        data.put("borrowAccont", borrow.getAccount());
        data.put("borrowUsetype", borrow.getUsetype());
        data.put("borrowApr", borrow.getApr());
        if (borrow.getIsday() == 1) {
            data.put("borrowTimeLimit", borrow.getTimeLimitDay());
        } else {
            data.put("borrowTimeLimit", borrow.getTimeLimit());
        }
        return data;
    }

    public String findServerPath() {
        String path = this.getClass().getResource("/").getPath();
        if (path.contains("/target/classes/")) {
            path = path.replaceAll("/target/classes/", "/src/main/webapp/");
        }
        path = path.replaceAll("/WEB-INF/classes/", "/");
        return path;
    }

    public static BorrowProtocol getInstance(User user, long borrow_id, long tender_id, int borrowType) {
        BorrowProtocol p = null;
        if ("yjt@".equals(Global.getValue("webid"))) {
            p = new BaseBorrowProtocol(user, borrow_id, tender_id, borrowType, 2);
        }

        // modfiy by gy  2017-03-17 15:50:27
        // 这里需要优化, 删除无关的类
        // detele YwdBorrowProtocol、GkxwProtocol、JrdProtocol、HuliandaiProtocol、NjwdProtocol

        /*if ("yw@".equals(Global.getValue("webid"))) {
            p = new YwdBorrowProtocol(user, borrow_id, tender_id, borrowType, 1);
        } else if ("gkxw@".equals(Global.getValue("webid"))) {
            p = new GkxwProtocol(user, borrow_id, tender_id, borrowType, 1);
        } else if ("jrd@".equals(Global.getValue("webid"))) {
            p = new JrdProtocol(user, borrow_id, tender_id, borrowType, 1);
        } else if ("huliandai@".equals(Global.getValue("webid"))) {
            p = new HuliandaiProtocol(user, borrow_id, tender_id, borrowType, 1);
        } else if ("njwd@".equals(Global.getValue("webid"))) {
            //我借我贷的借款协议书
            p = new NjwdProtocol(user, borrow_id, tender_id, borrowType, 1);
        } else {
            p = new BaseBorrowProtocol(user, borrow_id, tender_id, borrowType, 1);
        }*/
        return p;
    }

    public int createPdf() throws Exception {
        int size = 0;
        if (borrow == null) throw new BussinessException("该借款标不存在！");
        logger.info("读取PDF模板");
        readTemplate();
        logger.info("处理PDF模板");
        addPdfContent(pdf, borrow);
        doTemplate();
        logger.info("导出PDF");
        pdf.exportPdf();
        return size;
    }

    protected void addPdfTable(PdfHelper pdf, Borrow b) throws DocumentException {
        SearchParam param = SearchParam.getInstance();
        param.addParam("borrow", b);
        param.addParam("borrow.user", b.getUser());
        List<BorrowTender> list = borrowService.getTenderList(param).getList();
        borrowService.getTenderList(param);
        List cellList = null;
        List[] args = new List[list.size() + 1];
        cellList = new ArrayList();
        cellList.add("出借人(id)");
        cellList.add("借款金额");
        cellList.add("借款期限");
        cellList.add("年利率");
        cellList.add("借款开始日");
        cellList.add("借款到期日");
        cellList.add("截止还款日");
        cellList.add("还款本息");
        args[0] = cellList;
        for (int i = 1; i < list.size() + 1; i++) {
            BorrowTender t = list.get(i - 1);
            cellList = new ArrayList();
            cellList.add(t.getUser().getUsername() + "");
            cellList.add(t.getAccount() + "");
            cellList.add(b.getTimeLimit() + "");
            cellList.add(b.getApr() + "");
            cellList.add(b.getVerifyTime() + "");
            cellList.add(b.getRepaymentTime() + "");
            cellList.add(t.getRepaymentAccount() + "");
            args[i] = cellList;
        }
        pdf.addTable(args, 80, 7);
    }

    protected void readTemplate() throws IOException {
        if (getTemplateType() == 1) {
            Site site = articleService.getSiteByCode("protocol");
            if (site == null || site.getContent() == null) throw new BussinessException("协议模板有误！");
            BufferedReader in;
            in = new BufferedReader(new StringReader(site.getContent()));
            templateReader = new TemplateReader(in);
        } else {
            String pdftemplate = Global.getValue("pdftemplate");
            logger.info("读取html模板文件：" + context.getRealPath("/") + "/data/pdf/" + pdftemplate + "/" + this.getBorrowType() + ".html");
            String file = context.getRealPath("/") + "/data/pdf/" + pdftemplate + "/" + this.getBorrowType() + ".html";
            templateReader = new TemplateReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        }
    }

    protected void doTemplate() throws IOException, DocumentException {
        while (templateReader.read() != -1) {
        }
        templateReader.close();
        List<String> elements = templateReader.getLines();
        ProtocolValue pv = new ProtocolValue();
        pv.setData(fillProtoclData());
        StringBuffer sb = new StringBuffer();
        for (String e : elements) {
            if (e != null && e.startsWith("${") && e.endsWith("}")) {
                e = e.substring(2, e.length() - 1);
                if (e.equals("tenderTable")) {
                    templateHtml(sb.toString());
                    sb.setLength(0);
                    addPdfTable(pdf, borrow);
                } else if (e.equals("repaymnetTable")) {
                    templateHtml(sb.toString());
                    sb.setLength(0);
                    addTenderPlainPdfTable(pdf, borrow);
                } else if (e.equals("tenderPropertyTable")) {
                    templateHtml(sb.toString());
                    sb.setLength(0);
                    addTenderPropertyPdfTable(pdf, borrow);
                } else {
                    sb.append(pv.printProtocol(e));
                }
            } else {
                sb.append(e);
            }
        }
        templateHtml(sb.toString());
    }

    protected int addPdfContent(PdfHelper pdf, Borrow b) throws DocumentException {
        int size = 0;
        try {
            Image image = Image.getInstance(imageFileName);
            pdf.addImage(image);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        size = pdf.getPageNumber();
        return size;
    }

    protected String templateHtml(String str) throws IOException, DocumentException {
        final List<Element> pdfeleList = new ArrayList<Element>();
        ElementHandler elemH = new ElementHandler() {
            public void add(final Writable w) {
                if (w instanceof WritableElement) {
                    pdfeleList.addAll(((WritableElement) w).elements());
                }
            }
        };
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(str.getBytes("UTF-8")), "UTF-8");
        XMLWorkerHelper.getInstance().parseXHtml(elemH, isr);
        List<Element> list = new ArrayList<Element>();
        for (Element ele : pdfeleList) {
            if (ele instanceof LineSeparator || ele instanceof WritableDirectElement) {
                continue;
            }
            list.add(ele);
        }
        pdf.addHtmlList(list);
        return "";
    }

    protected void addTenderPlainPdfTable(PdfHelper pdf, Borrow b)
            throws DocumentException {
    }

    protected void addTenderPropertyPdfTable(PdfHelper pdf, Borrow b)
            throws DocumentException {
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public long getBorrow_id() {
        return borrow_id;
    }

    public void setBorrow_id(long borrow_id) {
        this.borrow_id = borrow_id;
    }

    public long getTender_id() {
        return tender_id;
    }

    public void setTender_id(long tender_id) {
        this.tender_id = tender_id;
    }

    public BorrowService getBorrowService() {
        return borrowService;
    }

    public void setBorrowService(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ArticleService getArticleService() {
        return articleService;
    }

    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getInPdfName() {
        return inPdfName;
    }

    public void setInPdfName(String inPdfName) {
        this.inPdfName = inPdfName;
    }

    public String getOutPdfName() {
        return outPdfName;
    }

    public void setOutPdfName(String outPdfName) {
        this.outPdfName = outPdfName;
    }

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public ServletContext getContext() {
        return context;
    }

    public void setContext(ServletContext context) {
        this.context = context;
    }

    public Borrow getBorrow() {
        return borrow;
    }

    public void setBorrow(Borrow borrow) {
        this.borrow = borrow;
    }

    public User getTenderUser() {
        return tenderUser;
    }

    public void setTenderUser(User tenderUser) {
        this.tenderUser = tenderUser;
    }

    public User getBorrowUser() {
        return borrowUser;
    }

    public void setBorrowUser(User borrowUser) {
        this.borrowUser = borrowUser;
    }

    public PdfHelper getPdf() {
        return pdf;
    }

    public void setPdf(PdfHelper pdf) {
        this.pdf = pdf;
    }

    public BorrowTender getTender() {
        return tender;
    }

    public void setTender(BorrowTender tender) {
        this.tender = tender;
    }

    public int getBorrowType() {
        return borrowType;
    }

    public void setBorrowType(int borrowType) {
        this.borrowType = borrowType;
    }

    public int getTemplateType() {
        return templateType;
    }

    public void setTemplateType(int templateType) {
        this.templateType = templateType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public LinkageService getLinkageService() {
        return linkageService;
    }

    public void setLinkageService(LinkageService linkageService) {
        this.linkageService = linkageService;
    }


}
