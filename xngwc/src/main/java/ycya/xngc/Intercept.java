package ycya.xngc;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ycya.xngc.util.SysConst;

import org.springframework.web.method.HandlerMethod;
// 拦截器签名
@Component
public class Intercept extends HandlerInterceptorAdapter{
	  private Logger logger = LoggerFactory.getLogger(getClass());

	    private static final String appid = "dtgwyc"; // 标识码
	    private static String appsecret = "dtgwyc";   // 密钥
        public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	        Method method = ((HandlerMethod) handler).getMethod();
	        String requestTime = request.getParameter("requestTime");
//	        System.out.println("ddddddddd"+requestTime);
	        if (AnnotatedElementUtils.isAnnotated(method, RequireSignature.class)) {
	            // 验证时间
	        	long now = System.currentTimeMillis();
//	            String requestTime = request.getParameter("requestTime");
	            if(requestTime==null){
	            	System.out.println("请按照文档上传所需字段!");
	            	throw new CustomException(251,"请按照文档上传所需字段!");
	            }
	            Date timestampGain = sdf.parse(requestTime);
	            if (Math.abs(now - timestampGain.getTime()) > 3_000_000) {
	                logger.error("签名时间[" + timestampGain + "]已经过期，请重试!");
	                throw new CustomException(250,"签名已经过期，请重试!");
	            }
	            // 验证接入码
	            String accesscCode = request.getParameter("accesscCode");
	            if(SysConst.accesscCodeMap.containsKey(accesscCode)){
	            	 appsecret = SysConst.accesscCodeMap.get(accesscCode);
	            }else{
	            	 throw new CustomException(250,"验证接入码不对!");
	            }
	            // 签名
	            String encryptionCode = request.getParameter("encryptionCode");
	            if (encryptionCode.isEmpty()) {
	                logger.error("签名不存在，请稍后重试!");
	                throw new CustomException(250,"签名不存在，请稍后重试!");
	            }
	            String sign = DigestUtils.sha256Hex(appid + "&"+ requestTime + "&" + accesscCode + "&" + appsecret).toLowerCase();
	            if (!encryptionCode.equals(sign)) {
	                logger.error("签名sign [ " + encryptionCode + " ] 错误!");
	                throw new CustomException(250,"签名sign [ " + encryptionCode + " ] 错误!");
	            }
	        }
	        return super.preHandle(request, response, handler);
	    }
//	    public static void main(String[] args) {
//	    	String appidlo = "scjtgwyc";
//	    	String timestamp ="2019-10-17 15:05:36";
//	    	String nonce_str ="20151017";
//	    	String appsecretd ="aDGJldgX";	
//	    	String sign = DigestUtils.sha256Hex(appidlo + "&"+ timestamp + "&" + nonce_str + "&" + appsecretd).toLowerCase();
//		    System.out.println("-------"+sign);
//	    }
	    //639057bb4971d972e978aa300b6fbc6a01dc18ea5ccb026c3d7c2aaf5d52eb47
	    //http://www.360doc.com/content/18/0308/13/41766228_735376483.shtml
	    public static String getSHA256Str(String str){
	        MessageDigest messageDigest;
	        String encdeStr = "";
	        try {
	            messageDigest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = messageDigest.digest(str.getBytes("UTF-8"));
	            encdeStr = Hex.encodeHexString(hash);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        return encdeStr;
	    }
}
