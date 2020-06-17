package ycya.xngc.util;



import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

 
public class SwithUtil {
	
	/**
     * javaBean转map
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String,Object> javaBeanToMap(Object obj) throws Exception{
        Map<String, Object> map =new HashMap<>();
        // 获取javaBean的BeanInfo对象
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(),Object.class);
        // 获取属性描述器
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // 获取属性名
            String key = propertyDescriptor.getName();
            // 获取该属性的值
            Method readMethod = propertyDescriptor.getReadMethod();
            // 通过反射来调用javaBean定义的getName()方法
            Object value = readMethod.invoke(obj);
            map.put(key, value);
        }
        return map;
    }
	/**
     * map转javaBean
     * @param map
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T>T mapToJavaBean(Map<String,Object> map , Class<T> clazz) throws Exception{
        // new 出一个对象
        T obj = clazz.newInstance();
        // 获取javaBean的BeanInfo对象
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
        // 获取属性描述器
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // 获取属性名
            String key = propertyDescriptor.getName();
            Object value = map.get(key);
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (map.containsKey(key)){
                // 解决 argument type mismatch 的问题，转换成对应的javaBean属性类型
                String typeName = propertyDescriptor.getPropertyType().getTypeName();
                // System.out.println(key +"<==>"+ typeName);
                if ("java.lang.Integer".equals(typeName)){
                    value = Integer.parseInt(value.toString());
                }
                if ("java.lang.Long".equals(typeName)){
                    value = Long.parseLong(value.toString());
                }
                if ("java.util.Date".equals(typeName)){
                    value = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
                }
                if(key.equals("applySxPople")){
                	value= value.toString();
                }
            }
            // 通过反射来调用javaBean定义的setName()方法
            writeMethod.invoke(obj,value);
        }
        return obj;
    }
    
      public static String getIpAddress(HttpServletRequest request) {
				String ipAddress = request.getHeader("x-forwarded-for");
				if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
					ipAddress = request.getHeader("Proxy-Client-IP");
				}
				if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
					ipAddress = request.getHeader("WL-Proxy-Client-IP");
				}
				if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
					ipAddress = request.getRemoteAddr();
					if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
						//根据网卡取本机配置的IP
						InetAddress inet = null;
						try {
							inet = InetAddress.getLocalHost();
						} catch (UnknownHostException e) {
						}
						ipAddress = inet.getHostAddress();
					}
				}
				//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
				if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
					if (ipAddress.indexOf(",") > 0) {
						ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
					}
				}
				return ipAddress;
           }
    
    /*  public static void main(String[] args) {
		String data= [{"id": "20150300416","deptName": "西充县西充县公务用车服务中心","deptType": 1,"qyId": "686","fwType": "2","deptSuperId": "686"}]
	}*/
      
}
