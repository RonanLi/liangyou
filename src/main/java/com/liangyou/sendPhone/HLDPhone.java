package com.liangyou.sendPhone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;

import sun.misc.BASE64Encoder;

import com.liangyou.context.Global;

/**
 * @author dan.shan
 * @since 2013-08-12 3:23 PM
 * 
 */

public class HLDPhone {

    public static String send(String phone,String phoneContent) throws Exception {

        DefaultHttpClient client = new DefaultHttpClient();
        client = (DefaultHttpClient) wrapClient(client);

        client.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                request.addHeader("Accept-Encoding", "gzip");
                request.addHeader("Authorization", "Basic " + new BASE64Encoder().encode(("api:"+Global.getString("sendsms_account")).getBytes("utf-8")));
            }
        });

        client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

        HttpPost request = new HttpPost("https://sms-api.luosimao.com/v1/send.json");

        ByteArrayOutputStream bos = null;
        InputStream bis = null;
        byte[] buf = new byte[10240];

        String content = null;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", phone));
            params.add(new BasicNameValuePair("message", phoneContent+"【好利贷】"));
            request.setEntity(new UrlEncodedFormEntity(params, "utf-8"));


            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                bis = response.getEntity().getContent();
                Header[] gzip = response.getHeaders("Content-Encoding");

                bos = new ByteArrayOutputStream();
                int count;
                while ((count = bis.read(buf)) != -1) {
                    bos.write(buf, 0, count);
                }
                bis.close();

                if (gzip.length > 0 && gzip[0].getValue().equalsIgnoreCase("gzip")) {
                    GZIPInputStream gzin = new GZIPInputStream(new ByteArrayInputStream(bos.toByteArray()));
                    StringBuffer sb = new StringBuffer();
                    int size;
                    while ((size = gzin.read(buf)) != -1) {
                        sb.append(new String(buf, 0, size, "utf-8"));
                    }
                    gzin.close();
                    bos.close();

                    content = sb.toString();
                } else {
                    content = bos.toString();
                }

                System.out.println(content);
            } else {
                System.out.println("error code is " + response.getStatusLine().getStatusCode());
            }
            return content;

        } finally {
            if (bis != null) {
                try {
                    bis.close();// 最后要关闭BufferedReader
                } catch (Exception e) {
                }
            }
        }
    }

    private String testStatus() throws Exception {
    	DefaultHttpClient client = (DefaultHttpClient) wrapClient(new DefaultHttpClient()); 

        client.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                request.addHeader("Accept-Encoding", "gzip");
                request.addHeader("Authorization", "Basic " + new BASE64Encoder().encode("api:8a15e95b7be1223042d06a65809412bf".getBytes("utf-8")));
            }
        });

        client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

        HttpGet request = new HttpGet("https://sms-api.luosimao.com/v1/status.json");

        ByteArrayOutputStream bos = null;
        InputStream bis = null;
        byte[] buf = new byte[10240];

        String content = null;
        try {
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                bis = response.getEntity().getContent();
                Header[] gzip = response.getHeaders("Content-Encoding");

                bos = new ByteArrayOutputStream();
                int count;
                while ((count = bis.read(buf)) != -1) {
                    bos.write(buf, 0, count);
                }
                bis.close();

                if (gzip.length > 0 && gzip[0].getValue().equalsIgnoreCase("gzip")) {
                    GZIPInputStream gzin = new GZIPInputStream(new ByteArrayInputStream(bos.toByteArray()));
                    StringBuffer sb = new StringBuffer();
                    int size;
                    while ((size = gzin.read(buf)) != -1) {
                        sb.append(new String(buf, 0, size, "utf-8"));
                    }
                    gzin.close();
                    bos.close();

                    content = sb.toString();
                } else {
                    content = bos.toString();
                }

                System.out.println(content);
            } else {
                System.out.println("error code is " + response.getStatusLine().getStatusCode());
            }
            return content;

        } finally {
            if (bis != null) {
                try {
                    bis.close();// 最后要关闭BufferedReader
                } catch (Exception e) {
                }
            }
        }
    }
    

    public static HttpClient wrapClient(HttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
            ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
            return new DefaultHttpClient(mgr, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
}