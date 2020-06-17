package ycya.xngc.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;


import ycya.xngc.CustomException;


// 验证数据
public class VerificationData{
	private Logger logger = Logger.getLogger(this.getClass().getName());
	/*@Autowired
	private JdbcTemplate jdbcTemplate;*/
	  /**
	   * 部门信息的验证  部门信息验证部门名称和id信息
	   * @param deptList
	   * @return
	   */
	  public static String verifiyDeptData(List<Map<String,Object>> deptList,JdbcTemplate jdbcTemplates){
		  String sb = "select id from mon_department_info where is_flag =0 and";
		  String bsb = "";
		  String sb1 = "";
		  String deptName = "";
		  String xnid = "";
		  String f = "";
		  // 判断区域的sql
		 String quyuSql ="select * from mon_department_info where is_flag=0 and dept_type=3 and ";
		 String quyuBsbSql = "";
		 Integer quyuId= 0;
		 Map<String,String> rNewMap = new HashMap<String,String>(); //保存新添加的数据 储层上级部门id与
		 for(Map<String,Object> keyMap:deptList){
			  if(!keyMap.containsKey("id") | !keyMap.containsKey("deptType") | !keyMap.containsKey("qyId")
						| !keyMap.containsKey("deptSuperId")| !keyMap.containsKey("deptName")	
				  ){
					 throw new CustomException(500,"请检查数据是否有未传字段!");
			} 
			 deptName = keyMap.get("deptName").toString();
			 xnid = keyMap.get("id").toString();
			 rNewMap.put(xnid,xnid);
//			 sb1 = " dept_name ='"+deptName+"' and xnid = '"+xnid+"'";
			 sb1 = " dept_name ='"+deptName+"' ";
			 bsb = sb+sb1;
			 List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(bsb);
			} catch (Exception e) {
				listMap=null;
			}
			 if(listMap.size()!=0){
				 f = "提示错误--<<-- 部门名称:"+deptName+";数据有误";
				 // 这里怎么输出日志语句
				 break;
			 }
			 // 判断区域 
			 listMap=null;
			 try{
				 quyuId= Integer.parseInt(keyMap.get("qyId").toString());
				 quyuBsbSql = quyuSql+"id="+quyuId+"";
				 listMap =jdbcTemplates.queryForList(quyuBsbSql);
			 }catch (Exception e) {
				 throw new CustomException(505,"请按规定上传区域id,传的是个什么???---"+quyuId);
			 }
			 if(listMap.size()==0){
				 f = "提示错误--<<-- 不合规的区域数据:"+quyuId+";数据有误";
				 // 这里怎么输出日志语句
				 break;
			 }
		 }
		 // 再次验证上级部门
		 String deptSuperId = ""; // 上级部门id
		 String quyuIdBsb =""; //区域id
		 String verifiSql  ="select id from mon_department_info where is_flag =0 and ";// 验证上级部门中心
		 String verifiSqlBsb = "";
		 
		 for(Map<String,Object> keyMap:deptList){
			 deptSuperId = keyMap.get("deptSuperId").toString();
			 quyuIdBsb= keyMap.get("qyId").toString();
			 if(quyuIdBsb.equals(deptSuperId)){
				 continue;
			 }
			 verifiSqlBsb = verifiSql+"xnid='"+deptSuperId+"'";
			 List<Map<String,Object>> listMapBsb = null;
			 try{
				 listMapBsb =jdbcTemplates.queryForList(verifiSqlBsb);
				 if(listMapBsb.size()==0 && !rNewMap.containsKey(deptSuperId)){
					 f = "提示错误--<<-- 不合规的上级部门数据:"+deptSuperId+";数据有误";
					 // 这里怎么输出日志语句
					 break;
				 }
			 }catch (Exception e) {
				 throw new CustomException(505,"上级部门验证失败---"+deptSuperId);
			 }
		 }
		return f;
	  }
	  
	  /**
	   * 用户信息的验证  用户信息验证部门名称和id信息
	   * @param deptList
	   * @return
	   */
	  public static String verifiyUserData(List<Map<String,Object>> deptList,JdbcTemplate jdbcTemplates){
		  String sb = "select id from mon_user_info where is_flag =0 and is_type=0 and";
		  String bsb = "";
		  String sb1 = "";
		  String userName = "";
		  String xnid = "";
		  String userPhone="";
		  String f = "";
		  for(Map<String,Object> keyMap:deptList){
			  if(!keyMap.containsKey("id") || !keyMap.containsKey("userName") || !keyMap.containsKey("uname")
						|| !keyMap.containsKey("userType")|| !keyMap.containsKey("userPhone")
						|| !keyMap.containsKey("userDeptId")	
				  ){
					 throw new CustomException(500,"请检查数据是否有未传字段!");
				}
			  userName = keyMap.get("userName").toString();
			 xnid = keyMap.get("id").toString();
			 userPhone = keyMap.get("userPhone").toString();
//			 sb1 = " user_name ='"+userName+"' or xnid = '"+xnid+"' or user_phone = '"+userPhone+"'";
			 sb1 = " user_name ='"+userName+"' or xnid = '"+xnid+"'";
			 bsb = sb+sb1;
			 List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(bsb);
			} catch (Exception e) {
				listMap=null;
			}
			 if(listMap.size()!=0 || listMap==null){
				 f = "提示错误--<<-- userName号:"+userName+";或者user_phone:"+userPhone+";数据有误";
				 // 这里怎么输出日志语句
				 break;
			 }
		 }
		return f;
	  }
	  
	  /**
	   * 车辆信息的验证  车辆信息验证部门名称和id信息
	   * @param deptList
	   * @return
	   */
	  public static String verifiyCarData(List<Map<String,Object>> deptList,JdbcTemplate jdbcTemplates){
		  String sb = "select id from mon_car_info where is_flag =0 and";
		  String bsb = "";
		  String sb1 = "";
		  String carNum = "";
		  String xnid = "";
		  String f = "";
		  for(Map<String,Object> keyMap:deptList){
			  if(!keyMap.containsKey("id") || !keyMap.containsKey("carNum") || !keyMap.containsKey("carType")
						|| !keyMap.containsKey("carModel")| !keyMap.containsKey("carState")
						|| !keyMap.containsKey("departmentId")|| !keyMap.containsKey("carColor")
						|| !keyMap.containsKey("carPrice")|| !keyMap.containsKey("carSeating")||!keyMap.containsKey("carPl")
 				  ){
		  			 throw new CustomException(500,"请检查数据是否有未传字段!");
				}
			  carNum = keyMap.get("carNum").toString();
			 xnid = keyMap.get("id").toString();
			 sb1 = " car_num ='"+carNum+"' or xnid = '"+xnid+"'";
			 bsb = sb+sb1;
			 List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(bsb);
			} catch (Exception e) {
				listMap=null;
			}
			 if(listMap.size()!=0 || listMap==null){
				 f = "提示错误--<<-- carNum号:"+";数据有误";
				 // 这里怎么输出日志语句
				 break;
			 }
		 }
		return f;
	  }
	  
	  /**
	   * 车辆信息的验证  车辆信息验证部门名称和id信息
	   * @param deptList
	   * @return
	   */
	  public static String verifiyDriverData(List<Map<String,Object>> deptList,JdbcTemplate jdbcTemplates){
		  String sb = "select id from mon_user_info where is_flag =0 and is_type=1 and";
		  String bsb = "";
		  String sb1 = "";
		  String userName = "";
		  String xnid = "";
		  String f = "";
		  for(Map<String,Object> keyMap:deptList){
			  if(!keyMap.containsKey("id") | !keyMap.containsKey("driverName") | !keyMap.containsKey("driverSex")
						| !keyMap.containsKey("driverDeptId")| !keyMap.containsKey("driverPhone")
						| !keyMap.containsKey("sfNo")
//						| !keyMap.containsKey("isType")
				  ){
					 throw new CustomException(500,"请检查数据是否有未传字段!");
				}
			  userName = keyMap.get("driverName").toString();
			 xnid = keyMap.get("id").toString();
			 sb1 = " user_name ='"+userName+"' or xnid = '"+xnid+"'";
			 bsb = sb+sb1;
			 List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(bsb);
			} catch (Exception e) {
				listMap=null;
			}
			 if(listMap.size()!=0 || listMap==null){
				 f = "提示错误--<<-- 驾驶员姓名:"+";数据有误";
				 // 这里怎么输出日志语句
				 break;
			 }
		 }
		return f;
	  }
	  
	  
	  /**
	   * 车辆信息的验证  车辆信息验证部门名称和id信息
	   * @param deptList
	   * @return
	   */
	  public static String verifiyTermData(List<Map<String,Object>> deptList,JdbcTemplate jdbcTemplates){
		  String sb = "select id from mon_term_info where is_flag=0 and ";
		  String bsb = "";
		  String sb1 = "";
		  String termId = "";
		  String xnid = "";
		  String f = "";
		  for(Map<String,Object> keyMap:deptList){
			  if(!keyMap.containsKey("id") || !keyMap.containsKey("termId") || !keyMap.containsKey("simNum")
						|| !keyMap.containsKey("termDeptId")|| !keyMap.containsKey("manufacturerInt")
				  ){
					 throw new CustomException(500,"请检查数据是否有未传字段!");
				}
			  termId = keyMap.get("termId").toString();
			 xnid = keyMap.get("id").toString();
			 sb1 = " term_id ='"+termId+"' or xnid = '"+xnid+"'";
			 bsb = sb+sb1;
			 List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(bsb);
			} catch (Exception e) {
				listMap=null;
			}
			 if(listMap.size()!=0 || listMap==null){
				 f = "提示错误--<<-- 终端号:"+termId+";数据有误";
				 // 这里怎么输出日志语句
				 break;
			 }
		 }
		return f;
	  }
	  //  判断部门是否可以删除
	  public static String verifiyDelDept(String table,Integer bdId,String id,JdbcTemplate jdbcTemplates){
		  if(bdId==null || bdId == 0 ){
		    	throw new CustomException(500,"请检查数据所传Id");
		    }
		  String f = "";
		  // 部门只判断有没有下级部门就好
		  String sql = "select id from "+table+" where dept_super_id = "+bdId;
		  List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(sql);
			 } catch (Exception e) {
				listMap=null;
			 }
			 if(listMap.size()!=0 || listMap==null){
				 f = "提示错误--<<-- 该部门有下级单位绑定，不支持删除:部门"+id+";数据有误";
				 // 这里怎么输出日志语句
			}	
			return f;
	  }
	  // 验证挂接表是否有绑定数据
	  public static String verifiyDelCar(Integer bdId,String id,JdbcTemplate jdbcTemplates){
		  if(bdId==null || bdId == 0 ){
		    	throw new CustomException(500,"请检查数据所传Id");
		    }
		  String f = "";
		  // 部门只判断有没有下级部门就好
		  String sql = "select id from mon_term_car_info where car_id = "+bdId;
		  List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(sql);
			 } catch (Exception e) {
				listMap=null;
			 }
			 if(listMap.size()!=0 || listMap==null){
				 f = "提示错误--<<-- 该车辆有终端绑定，不支持删除:车辆"+id+";数据有误";
				 // 这里怎么输出日志语句
			}	
			return f;
	  }
      public static String verifiyDelTerm(Integer bdId,String id,JdbcTemplate jdbcTemplates){
    	  if(bdId==null || bdId == 0 ){
		    	throw new CustomException(500,"请检查数据所传Id");
		    }
    	  String f = "";
		  // 部门只判断有没有下级部门就好
		  String sql = "select id from mon_term_car_info where term_id = "+bdId;
		  List<Map<String,Object>> listMap = null;
			 try {
				listMap =jdbcTemplates.queryForList(sql);
			 } catch (Exception e) {
				listMap=null;
			 }
			 if(listMap.size()!=0 || listMap==null){
				 f = "提示错误--<<-- 该终端有车辆绑定，不支持删除:终端"+id+";数据有误";
				 // 这里怎么输出日志语句
			}	
			return f;
	  }
}
