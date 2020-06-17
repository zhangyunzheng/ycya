package ycya.xngc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SysConst {

	 public static String postUrl = "http://192.168.0.56:9090/carmon/system/updateDateCache?";
	 public  static Map<String,String> delTypeForTable = new HashMap<String,String>(); 
	 static {
		 delTypeForTable.put("dept", "mon_department_info");
		 delTypeForTable.put("user", "mon_user_info");
		 delTypeForTable.put("car", "mon_car_info");
		 delTypeForTable.put("driver", "mon_user_info");
		 delTypeForTable.put("term", "mon_term_info");
	 }
	 // 验证码 对应 秘钥 
	 public  static Map<String,String> accesscCodeMap = new HashMap<String,String>(); 
	 static {
		 accesscCodeMap.put("999999999","Qqbw5fi41peL");  // 西宁
		 accesscCodeMap.put("999999998","lbaSNi9Mk7ff");  // 城西
		 accesscCodeMap.put("999999997","Bj0BDyFLsTB9");  // 湟源
		 accesscCodeMap.put("999999996","KSRj6eXAKhvw");  // 城东
		 accesscCodeMap.put("999999995","hRMryQZMJKmU");  // 城中
		 accesscCodeMap.put("999999994","pULMGMLFtpfj");  // 大通
		 accesscCodeMap.put("999999993","Q0kaxeNKx8EE");  // 湟中
	 }
	 public static Map<String,Integer> deptCache = new HashMap<String,Integer>(); // 部门id缓存
	 public static Map<String,Integer> userCache = new HashMap<String,Integer>(); //用户id缓存
	 public static Map<String,Integer> carCache = new HashMap<String,Integer>();  //车辆id缓存
	 public static Map<String,Integer> driverCache = new HashMap<String,Integer>(); // 用户id缓存
	 public static Map<String,Integer> termCache = new HashMap<String,Integer>(); // 终端对应id缓存
	 // 为了触发张运证进行缓存更新的操作
	 public static Queue<Map<String,Object>> cacheUpdatePostZyz = new ConcurrentLinkedQueue<Map<String,Object>>();	//  系统指令记录缓存带时间的
}
