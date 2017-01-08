package cn.a6_79.message;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class WebConnect {
    private static String headUrl = "http://6-79.cn/api/";
//    private static String headUrl = "http://222.205.46.30:8000/api/";
    static int API_LOGIN = 0;
    static int API_GET_NOTICE = 1;
    static int API_SEND_MESSAGE = 2;
    static int API_GET_LIST = 3;
    static int API_GET_CONTACT = 4;
    static int API_ADD_CONTACT = 5;
    static int API_MODIFY_CONTACT = 6;
    static int API_DELETE_CONTACT = 7;
    private static String[] methods = {
            "POST",
            "GET",
            "POST",
            "POST",
            "POST",
            "POST",
            "POST",
            "POST",
    };
    private static String[] urls = {
            headUrl+"login/",
            headUrl+"get_notice/",
            headUrl+"send_message/",
            headUrl+"get_list/",
            headUrl+"contact/get/",
            headUrl+"contact/add/",
            headUrl+"contact/modify/",
            headUrl+"contact/delete/",


    };

    private static String response(int code, String msg) {
        return "{ \"code\":" + code + ", \"msg\":\"" + msg + "\"}";
    }

    static String postGetJson(String transValue, int status) {
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try {
            URL mUrl = new URL(urls[status]);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            mHttpURLConnection.setConnectTimeout(15000);
            mHttpURLConnection.setReadTimeout(15000);
            mHttpURLConnection.setRequestMethod(methods[status]);
            if (Info.session != null)
                mHttpURLConnection.setRequestProperty("Cookie", Info.session);
            else
                mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.setDoOutput(true);
            mHttpURLConnection.setUseCaches(false);
            mHttpURLConnection.connect();

            DataOutputStream dos = new DataOutputStream(mHttpURLConnection.getOutputStream());
            dos.write(transValue.getBytes());
            dos.flush();
            dos.close();

            int respondCode = mHttpURLConnection.getResponseCode();
            Log.d("respondCode","respondCode="+respondCode );
            String type = mHttpURLConnection.getContentType();
            Log.d("type", "type="+type);
            String encoding = mHttpURLConnection.getContentEncoding();
            Log.d("encoding", "encoding="+encoding);
            int length = mHttpURLConnection.getContentLength();
            Log.d("length", "length=" + length);
            String cookieValue = mHttpURLConnection.getHeaderField("Set-Cookie");
            System.out.println("cookie value:" + cookieValue);
            if (cookieValue != null) {
                try {
                    Info.session = cookieValue.substring(0, cookieValue.indexOf(";"));
                } catch (Exception e) {
                    return response(300, "获取session错误");
                }
            }

            if (respondCode == 200) {
                InputStream is = mHttpURLConnection.getInputStream();
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                int len;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    message.write(buffer, 0, len);
                }
                is.close();
                message.close();
                String msg = new String(message.toByteArray());
                Log.d("WebConnect", msg);
                return msg;
            }
            return response(200, "连接错误");
        }catch(Exception e) {
            return response(100, "无法连接");
        }
    }
}

interface OnAsyncTaskListener {
    void onSuccess(String string);
}
