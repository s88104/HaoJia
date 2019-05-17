package com.example.haojia;
//連線遠端資料庫 
//POST SQL指令至PHP

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class DBconnection {
	public static String executeQuery(String query_string)
	{
		String result="";
		//http post
		try{
			//建立HttpClient物件
            HttpClient httpClient = new DefaultHttpClient();
            
            //建立HttpPost物件並傳入網址(使用Android模擬器時位址為10.0.2.2)
            //HttpPost httpPost = new HttpPost("http://haojia.noip.me/HttpPost.php"); //學校資料庫位址
            HttpPost httpPost = new HttpPost("http://s88104.ddns.net/HttpPost.php"); //Eric資料庫位址
            //HttpPost httpPost = new HttpPost("http:/122.100.86.107/HttpPost.php");
            
            //參數加入 只能放置String型態資料
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", query_string));
            httpPost.setEntity((HttpEntity) new UrlEncodedFormEntity(params, HTTP.UTF_8));
            //傳回HttpResponse
            HttpResponse httpResponse = httpClient.execute(httpPost);
            //view_account.setText(httpResponse.getStatusLine().toString());
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            result = builder.toString();
        }
		catch(Exception e) 
		{
             Log.e("log_tag","Error in http connection "+e.toString());//LogoCat
        }
		return result;
	}
}
