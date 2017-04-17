/*
 * $Id: HttpReportReceiver.java 1600 2009-06-23 02:38:46Z hyyang $
 * $Revision: 1600 $
 * $Date: 2009-06-23 10:38:46 +0800 (鏄熸湡浜? 23 鍏湀 2009) $
 */

package com.liangyou.api.simpleclient;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hyyang
 * @version 1.1
 */
public class HttpReportReceiver extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 6598737678563046424L;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void destroy() {
        super.destroy();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String spid = req.getParameter("spid");
        String mtmsgid = req.getParameter("mtmsgid");
        String mtstat = req.getParameter("mtstat");
        String mterrorcode = req.getParameter("mterrorcode");
        //获取状态报告参数后，请即刻返回响应信息
        PrintWriter pw = resp.getWriter();
        StringBuilder output = new StringBuilder();
        output.append("command=RT_RESPONSE&spid=");
        output.append(spid);
        output.append("&mtmsgid=");
        output.append(mtmsgid);
        output.append("&rtstat=ACCEPT&rterrcode=000");
        resp.setContentLength(output.length());
        pw.write(output.toString());
        pw.flush();
        pw.close();

        //处理逻辑
        System.out.println(mtmsgid);
        System.out.println(mtstat);
        System.out.println(mterrorcode);
        /*
         process code
         ......
         */

    }
}