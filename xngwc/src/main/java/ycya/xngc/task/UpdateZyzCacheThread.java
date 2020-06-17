package ycya.xngc.task;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.slf4j.Logger;

import net.sf.json.JSONObject;
import ycya.xngc.util.LogFileName;
import ycya.xngc.util.LoggerUtils;
import ycya.xngc.util.SysConst;

public class UpdateZyzCacheThread implements Runnable{
	private final  Logger logger = LoggerUtils.Logger(LogFileName.BAITIAO_USER);
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Override
	public void run() {
		while (true) {
			try {
				handleQueenPs();
				Thread.sleep(10000); // 10秒处理一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}
	public void handleQueenPs(){
		Map<String,Object> map=SysConst.cacheUpdatePostZyz.poll();
    	if(map==null)return;
		if(!map.containsKey("type") || !map.containsKey("dataName")){
			return;
		} 
	/*	Integer type = Integer.parseInt(map.get("type").toString());
		String dataName = map.get("dataName").toString();*/
		String d ="";
		try {
			d = doHttpPost(map);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!d.equals("")){
			logger.info("通知成功,"+"通知时间:"+sdf.format(new Date()));
		}
	}
	
	public  String doHttpPost(Map<String,Object> map) throws ClientProtocolException, IOException{
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		Integer type = Integer.parseInt(map.get("type").toString());
		String dataName = map.get("dataName").toString();
		String lastUrl = "type="+type+"&dataName="+dataName;
		String url = SysConst.postUrl+lastUrl;
//		System.out.println(url);
		String basic="";
		 try {
			 HttpPost post = new HttpPost(url);
				post.setHeader("Content-Type", "application/json");
				RequestConfig requestConfig = RequestConfig.custom()
		                .setSocketTimeout(300 * 1000)
		                .setConnectTimeout(300 * 1000)
		                .build();
				post.setConfig(requestConfig);
				response = client.execute(post);
				String flag = EntityUtils.toString(response.getEntity(),"UTF-8");
				System.out.println("返回的数据"+flag);
				return flag;
	        } catch (SocketTimeoutException e) {
	        	logger.info("调用Dat+"+ ".aService接口超时,超时时间:" + 300
	                    + "秒,url:" + url + ",参数：" + lastUrl);
	        	SysConst.cacheUpdatePostZyz.add(map);
              return basic;
	        } catch (Exception e) {
	        	logger.info("调用DataService接口失败,url:" + url + ",参数：" + lastUrl,
                e);
	        	SysConst.cacheUpdatePostZyz.add(map);
	          return basic;

	        }
	}
	/*public static String doHttpPost(String requestParams) {
        String url = null;
        JSONObject jb=new JSONObject();
        jb.put("code",0);
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(300 * 1000)
                    .setConnectTimeout(300 * 1000)
                    .build();
            url = "http://192.168.0.51:8080/carmon/system/updateDateCache";
            HttpPost post = new HttpPost(url);
            post.setConfig(requestConfig);
            post.setHeader("Content-Type","application/json;charset=utf-8");
            StringEntity postingString = new StringEntity(requestParams,
                    "utf-8");
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);
            String content = EntityUtils.toString(response.getEntity());
            System.out.println(content);
            return content;
        } catch (SocketTimeoutException e) {
//            System.out.println("调用Dat+"
//                    + ".aService接口超时,超时时间:" + 300
//                    + "秒,url:" + url + ",参数：" + requestParams, e);
            return jb.toString();
        } catch (Exception e) {
//            LoggerUtil.error("调用DataService接口失败,url:" + url + ",参数：" + requestParams,
//                    e);
            return jb.toString();
        }
    }*/
	/*JSONObject jsonObject=JSONObject.fromObject(map);
	System.out.println(jsonObject.toString());*/
}
