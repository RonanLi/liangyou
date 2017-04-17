/*
 * $Id: HttpMoReceiver.java 1600 2009-06-23 02:38:46Z hyyang $
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
public class HttpMoReceiver extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -4877273865780111499L;

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
        String momsgid = req.getParameter("momsgid");
        String da = req.getParameter("da");
        String sa = req.getParameter("sa");
        String dc = req.getParameter("dc");
        String sm = req.getParameter("sm");
        //将Hex编码的上行内容还原成普通字符串
        //在jdk1.4版本环境下，请使用DIYClientExample.decodeHexStr(Integer.parseInt(dc),sm)
        String moContent = SimpleClientExample.decodeHexStr(Integer.parseInt(dc),sm);     
        //获取上行参数后，请即刻返回响应信息
        PrintWriter pw = resp.getWriter();
        StringBuilder output = new StringBuilder();
        output.append("command=MO_RESPONSE&spid=");
        output.append(spid);
        output.append("&momsgid=");
        output.append(momsgid);
        output.append("&mostat=ACCEPT&moerrcode=000");
        resp.setContentLength(output.length());
        pw.write(output.toString());
        pw.flush();
        pw.close();

        //处理逻辑
        System.out.println(sa);
        System.out.println(da);
        System.out.println(moContent);
        /*
         process code
         ......
         */

    }
}