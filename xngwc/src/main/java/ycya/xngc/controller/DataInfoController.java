package ycya.xngc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import net.sf.json.JSONArray;
import ycya.xngc.CustomException;
import ycya.xngc.RequireSignature;
import ycya.xngc.bean.Car;
import ycya.xngc.bean.Dept;
import ycya.xngc.bean.Driver;
import ycya.xngc.bean.ErrorResponseEntity;
import ycya.xngc.bean.Order;
import ycya.xngc.bean.Term;
import ycya.xngc.bean.User;
import ycya.xngc.dao.CarDao;
import ycya.xngc.dao.DeptDao;
import ycya.xngc.dao.DriverDao;
import ycya.xngc.dao.OrderDao;
import ycya.xngc.dao.TermDao;
import ycya.xngc.dao.UserDao;
import ycya.xngc.util.CacheManagerInit;
import ycya.xngc.util.LogFileName;
import ycya.xngc.util.LoggerUtils;
import ycya.xngc.util.SwithUtil;
import ycya.xngc.util.SysConst;
import ycya.xngc.util.VerificationData;

@RestController
public class DataInfoController {
	@Autowired
	public JdbcTemplate jdbcTemplates;
//	private Logger logger = Logger.getLogger(this.getClass().getName());
	private final Logger logger = LoggerUtils.Logger(LogFileName.BAITIAO_USER);
    @Autowired
    private DeptDao deptDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DriverDao driverDao;
    @Autowired
    private CarDao carDao;
    @Autowired
    private TermDao termDao;
    @Autowired
    private OrderDao orderDao;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
	   // 添加部门的方法 (验证了上级单位和区域)  1单位、2用户、3车辆、4设备、5驾驶员通知 
	    @SuppressWarnings({ "unchecked", "rawtypes"})
		@RequireSignature
		@RequestMapping("/deptAdd")
	  public ErrorResponseEntity deptAdd(@RequestBody String data
			  ){
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	List<Map<String, Object>> listMap = (List) tableData;
			JSONArray jsonarr = JSONArray.fromObject(data);
		    System.out.println("添加部门的数据:"+jsonarr);
		    logger.info("添加部门的数据:"+jsonarr);
		    // 验证数据
		    String f = VerificationData.verifiyDeptData(listMap,jdbcTemplates);
		    if(!f.equals("")){
		    	throw new CustomException(500,f);
		    }
		    if(CacheManagerInit.deptBosType==0)CacheManagerInit.deptBosType = 0;
		    Integer bosType = CacheManagerInit.deptBosType+1;
		    String sb = "insert into mon_department_info(xnid,dept_name,dept_type,qy_id,dept_super_id,bos_type";
		    String id = ""; 
			String deptName = "";
			String deptSuperId = "";
			int deptType = 0;
			int qyId = 0;
			int fwType=0;
			String fwzxIds = "";
			StringBuffer pksb = new StringBuffer();
			Map<String,String> jsonFwzx = new HashMap<String,String>(); //存储xnid和服务中心的id关系
		
			for(Map<String,Object> map:listMap){
				id = map.get("id").toString(); // 
                deptName = map.get("deptName").toString();
                deptSuperId = map.get("deptSuperId").toString();
                if(deptSuperId.equals("0")){
                	return new ErrorResponseEntity(500,"上传数据有上级部门为0,请检查数据!");
                }
                deptType = Integer.parseInt(map.get("deptType").toString());
                qyId  = Integer.parseInt(map.get("qyId").toString());
                //为服务单位的时候
                if(deptType==1){
                    pksb.append(",fw_Type) values");
                }
                else if(deptType==2){
                	pksb.append(") values");	
                }
                pksb.append("(");
                pksb.append("'"+id+"','"+deptName+"',"+deptType+","+qyId+","+deptSuperId+",'"+bosType+"'");
                if(deptType==1){
                	fwType = Integer.parseInt(map.get("fwType").toString());
                    pksb.append(","+fwType+"");
                   
                }
                else if(deptType==2){
                	fwzxIds = map.get("fwzxIds").toString();
                	 jsonFwzx.put(id, fwzxIds);
//                	pksb.append(","+fwzxIds+"");	
                }
                pksb.append(")");
                pksb.append(",");
			}
		    sb= sb+pksb.substring(0, pksb.length()-1);
		    System.out.println("sql:"+sb);
		    try {
		    	jdbcTemplates.execute(sb);
			} catch (Exception e) {
				throw new CustomException(500,"上传数据有误!请检查数据");
			}
		    // 添加刚加入的数据  更新缓存
		    String cacheSql = "select id,xnid,dept_type,dept_super_id,qy_id,dept_name from mon_department_info where bos_type ="+bosType+"";
		    List<Map<String,Object>> cacheMap = jdbcTemplates.queryForList(cacheSql);
		    StringBuffer sql = new StringBuffer();
		    Integer xnNewid = 0;   // 西宁新id
		    Integer xnDeptType=0; // 部门类型
		    String xnNewXnid = "";  //客户上传的id
		    String  xnNewFwzxIds = ""; //服务中心ids
		    String sqlPl = " insert into mon_dept_fwpt_info(dept_id,fwpt_id) values ";
		    
		    String updateSqlSuperId =""; // 更改上级部门sql
		    String oldUpdateSqlSuperId ="";
		    Integer newUpdateDeptSuperId = 0; //需要上传过来的deptId更改为系统id
		    String updateQyId="";
			JSONObject json = new JSONObject(); //通知
		    for(Map<String,Object> cachejson:cacheMap){
		    	 // 通知
		    	json.clear();
    			json.put("dataType",1);
    			json.put("operType",1);
    			json.put("dataId",cachejson.get("id").toString());
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
		    	xnNewid = Integer.parseInt(cachejson.get("id").toString()); //获取到西宁表的id
		    	xnNewXnid = cachejson.get("xnid").toString(); // 客户上传的id
		    	SysConst.deptCache.put(xnNewXnid,xnNewid);
		    	// 更改其上级部门id
		    	oldUpdateSqlSuperId =cachejson.get("dept_super_id").toString();
		    	updateQyId = cachejson.get("qy_id").toString();
		    	//如果相等证明是上级部门为我们制定的区域，因此在缓存中拿不出，因此要过滤
		    	if(!oldUpdateSqlSuperId.equals(updateQyId)){
		    		newUpdateDeptSuperId = SysConst.deptCache.get(oldUpdateSqlSuperId);
		    		updateSqlSuperId = "update mon_department_info set dept_super_id ="+newUpdateDeptSuperId+" where id = "+xnNewid+"";
		    		jdbcTemplates.execute(updateSqlSuperId);
		    	}
		    	xnDeptType = Integer.parseInt(cachejson.get("dept_type").toString());
		    	if(xnDeptType==2){
			    	xnNewFwzxIds = jsonFwzx.get(xnNewXnid);
			    	if(xnNewFwzxIds==null)continue;
			    	String[] k = xnNewFwzxIds.split(",");//假如有多个用车中心
			    	String pl ="";
			    	for(String basic:k){
			    		pl = pl+SysConst.deptCache.get(basic)+",";
			    	}
			    	pl= pl.substring(0, pl.length()-1);
			    	sql.append("("+xnNewid+",'"+pl+"'),");
		    	}
		    }
		    
		    if(!sql.toString().equals("")){
			    sqlPl= sqlPl+sql.substring(0, sql.length()-1);
			    System.out.println("deptType为2时的sql:"+sqlPl);
			    try {
			    	jdbcTemplates.execute(sqlPl);	
				} catch (Exception e) {
					throw new CustomException(500,"服务单位为1时,服务中心缓存失败,请联系对接方");
				}
		    }
			CacheManagerInit.deptBosType = bosType;  //这里重新赋值
		    return new ErrorResponseEntity(200,"添加成功");
	  }

	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/deptMod")
	  public ErrorResponseEntity deptMod(@RequestBody String data
			  ){
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	 List<Map<String, Object>> listMap = (List) tableData;
	    	 JSONArray jsonarr = JSONArray.fromObject(data);
		    System.out.println("修改部门的数据:"+jsonarr);
		    logger.info("修改部门的数据:"+jsonarr);
			Map<String,Object> map = listMap.get(0);
			// 获取 到 服务器实际的id
			Integer bdId = SysConst.deptCache.get(map.get("id").toString());
			if(bdId==null | bdId ==0){
				throw new CustomException(505,"内部服务检查到无此id数据,请检查数据id");
			}
			if(!map.containsKey("id") || !map.containsKey("deptType") || !map.containsKey("qyId")
					|| !map.containsKey("deptSuperId")|| !map.containsKey("deptName")	
			  ){
				 throw new CustomException(500,"请检查数据是否有未传字段!");
			}
			Dept deptT = deptDao.findOne(bdId);
			if(deptT==null){
				throw new CustomException(500,"亲,你要所修改的数据在市平台没有！请检查你所上传的id");
			}
			// 验证区域和上级部门
			List<Map<String,Object>> listMapDept = null;
			 try{
				 int quyuId= Integer.parseInt(map.get("qyId").toString());
				 String sqlQuyu = "select * from mon_department_info where is_flag=0 and dept_type=3 and id="+quyuId+"";
				 listMapDept =jdbcTemplates.queryForList(sqlQuyu);
			 }catch (Exception e) {
				 throw new CustomException(505,"请按规定上传区域id,传的是个什么???---"+map.get("qyId").toString());
			 }
			 if(listMapDept.size()==0){
				 throw new CustomException(505,"提示错误--<<-- 不合规的区域数据:区域数据为:"+map.get("qyId").toString());
			 }
			 
			 String deptSuperId = map.get("deptSuperId").toString();
			 String quyuIdBsb= map.get("qyId").toString();
			 Integer deptSuperIdInt = 0; //转换后的id
			 if(!quyuIdBsb.equals(deptSuperId)){
				 String verifiSql  ="select id from mon_department_info where is_flag =0 and xnid ='"+deptSuperId+"'";// 验证上级部门中心
				 List<Map<String,Object>> listMapSuper=null;
				 try{
					 listMapSuper =jdbcTemplates.queryForList(verifiSql);
				 }catch (Exception e) {
					 throw new CustomException(505,"提示错误--<<-- 不合规的区上级部门数据:上级部门数据为:"+map.get("deptSuperId").toString());
				 }
				 if(listMapSuper.size()==0){
					 throw new CustomException(505,"提示错误--<<-- 不合规的区上级部门数据:上级部门数据为:"+map.get("deptSuperId").toString());
				 }
				 // 如果上级部门和区域id不相等。需要转换上级部门id为系统id
				 deptSuperIdInt = SysConst.deptCache.get(deptSuperId);
				 map.put("deptSuperId", deptSuperIdInt);
			 }
			Dept dept =null;
			try {
				map.put("id", bdId);
				dept=  SwithUtil.mapToJavaBean(map,Dept.class);
			} catch (Exception e) {
				 logger.info("Map转Bean转化失败:"+jsonarr);
				 System.out.println("Map转Bean转化失败:");
			}
			try {
				deptDao.save(dept);
			} catch (Exception e) {
				throw new CustomException(500,"单位信息修改不成功!");
			}
			try{
				if(Integer.parseInt(map.get("deptType").toString())==2){
					String cacheSql = "delete from mon_dept_fwpt_info where dept_id ="+bdId+"";
					 //获取到部门与服务中心表的需要修改的id
					jdbcTemplates.execute(cacheSql);
					String fwzxIds = map.get("fwzxIds").toString();
			    	String[] k = fwzxIds.split(",");//假如有多个用车中心
			    	String pl ="";
			    	for(String basic:k){
			    		pl = pl+SysConst.deptCache.get(basic).toString()+",";
			    	}
			    	pl= pl.substring(0, pl.length()-1);
					String updateSql = "insert into mon_dept_fwpt_info(dept_id,fwpt_id) values("+bdId+",'"+pl+"')";
					jdbcTemplates.execute(updateSql);
					}
				    JSONObject json = new JSONObject();
				    json.put("dataType",1);
				    json.put("operType",2);
				    json.put("dataId",bdId);
	    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
					return new ErrorResponseEntity(200,"修改成功");
			}catch (Exception e) {
				throw new CustomException(500,"dept为2时检查数据!");
			}
	  }
	    
	    // 删除的通用的方法
	    @RequireSignature
		@RequestMapping("/baseDel") //删除的时候要看是否有关联1单位、2用户、3驾驶员、4车辆、5设备、6订单
//	  public ErrorResponseEntity baseDel(@RequestParam(required = true)String id,@RequestParam(required = true)String delType
	    public ErrorResponseEntity baseDel(@RequestBody String data){	
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	 List<Map<String, Object>> listMap = (List) tableData;
	    	 Map<String,Object> map = listMap.get(0);
	    	 String delType = map.get("delType").toString();
	    	 String id = map.get("id").toString();
		    String table = SysConst.delTypeForTable.get(delType);
		    if(table.equals("") | table==null){
		    	 throw new CustomException(500,"请检查数据所传delType");
		    }
		    Integer bdId = 0;
		    Integer dataType = 0;
		    if(delType.equals("dept")){
		    	dataType = 1;
		    	bdId = SysConst.deptCache.get(id);
		    	String f = VerificationData.verifiyDelDept(table,bdId,id,jdbcTemplates);
				if(!f.equals("")){
				    throw new CustomException(500,f);
				}
		     }else if(delType.equals("user")){
		    	dataType = 2;
		    	bdId = SysConst.userCache.get(id);
		     }else if(delType.equals("car")){
		    	dataType = 3;
		    	bdId = SysConst.carCache.get(id);
		    	String f = VerificationData.verifiyDelCar(bdId,id,jdbcTemplates);
			    if(!f.equals("")){
			    	throw new CustomException(500,f);
			  }
		     }else if(delType.equals("driver")){
		    	dataType = 5;
		    	bdId = SysConst.driverCache.get(id);
		     }else if(delType.equals("term")){
		    	dataType = 4;
		    	bdId = SysConst.termCache.get(id);
		    	String f = VerificationData.verifiyDelTerm(bdId,id,jdbcTemplates);
			    if(!f.equals("")){
			    	throw new CustomException(500,f);
			  }
		    }
		    if(bdId==null || bdId == 0 ){
		    	throw new CustomException(500,"请检查数据所传Id");
		    }
		    String sql = "update "+table+" set is_flag =1 where id = "+bdId+"";
		    logger.info("删除数据的sql:"+sql);
		    JSONObject json = new JSONObject();
		    json.put("dataType",dataType);
			json.put("operType",3);
			json.put("dataId",bdId);
			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
		    try {
		    	jdbcTemplates.execute(sql);	
			} catch (Exception e) {
				throw new CustomException(500,"上传数据有误!请检查数据");
			}
		    	return new ErrorResponseEntity(200,"删除成功");
	  }
	    
	    // 添加用户的方法  
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/userAdd") //要判断该用户没有部门时的返回
	  public ErrorResponseEntity userAdd(@RequestBody String data
			  ){
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	 List<Map<String, Object>> listMap = (List) tableData;
			JSONArray jsonarr = JSONArray.fromObject(data);
		    System.out.println("添加用户的数据:"+jsonarr);
		    logger.info("添加用户的数据:"+jsonarr);
		    // 校验数据
		    String f = VerificationData.verifiyUserData(listMap,jdbcTemplates);
		    if(!f.equals("")){
		    	throw new CustomException(500,f);
		    }
		    if(CacheManagerInit.userBosType==0)CacheManagerInit.userBosType = 0;
		    Integer bosType = CacheManagerInit.userBosType+1;
		    String sb = "insert into mon_user_info(xnid,user_name,uname,user_type,user_phone,user_dept_id,is_type,bos_type) values";
		    String id = ""; 
			String userName = ""; // 真实名称
			String uname = "";  //账户名
			Integer userType = 0;
			String userPhone = "";
			Integer userDeptId = 0;
			StringBuffer pksb = new StringBuffer();
			for(Map<String,Object> map:listMap){
				
				id = map.get("id").toString();
				userName = map.get("userName").toString();
				uname = map.get("uname").toString();
				userType = Integer.parseInt(map.get("userType").toString());
				userPhone = map.get("userPhone").toString();
				// 这里应该从部门缓存中取
				userDeptId  = SysConst.deptCache.get(map.get("userDeptId").toString());
				if(userDeptId==null || userDeptId==0){
					throw new CustomException(500,"上传数据部门有误!请检查数据");
				}
//				userDeptId  = Integer.parseInt(map.get("userDeptId").toString());
                pksb.append("(");
//                pksb.append("'"+id+"'","'"+userName+"'","'"+uname+"'","+userType+","'"+userPhone+"',"+userDeptId+","+bosType);
                pksb.append("'"+id+"',"+"'"+userName+"',"+"'"+uname+"',"+userType+",'"+userPhone+"',"+userDeptId+",0,"+bosType);
                pksb.append(")");
                pksb.append(",");
			}
		    sb= sb+pksb.substring(0, pksb.length()-1);
		    System.out.println("sql:"+sb);
		    try {
		    	jdbcTemplates.execute(sb);	
			} catch (Exception e) {
				throw new CustomException(500,"上传数据有误!请检查数据");
			}
		    // 添加刚加入的数据  更新缓存
		    String cacheSql = "select id,xnid,uname from mon_user_info where is_type=0 and bos_type ="+bosType+"";
		    List<Map<String,Object>> cacheMap = jdbcTemplates.queryForList(cacheSql);
		    JSONObject json = new JSONObject();
		    for(Map<String,Object> cachejson:cacheMap){
		    	 // 通知
		    	json.clear();
		    	json.put("dataType",2);
    			json.put("operType",1);
    			json.put("dataId",cachejson.get("id").toString());
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
		    	SysConst.userCache.put(cachejson.get("xnid").toString(), Integer.parseInt(cachejson.get("id").toString()));
		    }
			CacheManagerInit.deptBosType = bosType;  //这里重新赋值
		    return new ErrorResponseEntity(200,"添加成功");
	  }
       //修改用户的方法
//	    用户id     user_name
//	    用户账号           uname
//	    用户姓名
//	         联系电话
//	         所属单位
//	    用户类型
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/userMod")
	  public ErrorResponseEntity userMod(@RequestBody String list
			  ){
	    	JSONArray tableData = JSONArray.fromObject(list);
	    	List<Map<String, Object>> listMap = (List) tableData;
	    	JSONArray jsonarr = JSONArray.fromObject(list);
		    System.out.println("修改用户的数据:"+jsonarr);
		    logger.info("修改用户的数据:"+jsonarr);
			Map<String,Object> map = listMap.get(0);
			// 获取 到 服务器实际的id
			Integer bdId = SysConst.userCache.get(map.get("id").toString());
			if(bdId==null || bdId ==0){
				throw new CustomException(505,"内部服务检查到无此id数据,请检查数据id");
			}
			if(!map.containsKey("id") || !map.containsKey("userName") || !map.containsKey("uname")
					|| !map.containsKey("userType")|| !map.containsKey("userPhone")
					|| !map.containsKey("userDeptId")	
			  ){
				 throw new CustomException(500,"请检查数据是否有未传字段!");
			}
			User deptT = userDao.findOne(bdId);
			if(deptT==null){
				throw new CustomException(500,"亲,你要所修改的数据在市平台没有！请检查你所上传的id");
			}
			User user = null;
			Integer userDeptId  = SysConst.deptCache.get(map.get("userDeptId").toString());
			if(userDeptId==null || userDeptId==0){
				throw new CustomException(500,"上传数据部门有误!请检查数据");
			}
			try {
				map.put("id", bdId);
				map.put("userDeptId", userDeptId);
				user=  SwithUtil.mapToJavaBean(map,User.class);
			} catch (Exception e) {
				 logger.info("Map转Bean转化失败:"+jsonarr);
				 System.out.println("Map转Bean转化失败:");
			}
			try {
				userDao.save(user);
				JSONObject json = new JSONObject();
    			json.put("dataType",2);
    			json.put("operType",2);
    			json.put("dataId",bdId);
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
				return new ErrorResponseEntity(200,"修改成功");
			} catch (Exception e) {
				throw new CustomException(500,"请检查数据是否有未传字段!");
			}
			
	  }  
	    
	    
	 // 添加车辆的方法  
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/carAdd")
	  public ErrorResponseEntity carAdd(@RequestBody String data
			  ){
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	 List<Map<String, Object>> listMap = (List) tableData;
			JSONArray jsonarr = JSONArray.fromObject(data);
		    System.out.println("添加车辆的数据:"+jsonarr);
		    logger.info("添加车辆的数据:"+jsonarr);
		    String f = VerificationData.verifiyCarData(listMap,jdbcTemplates);
		    if(!f.equals("")){
		    	throw new CustomException(500,"上传数据有误!请检查数据");
		    }
		    if(CacheManagerInit.carBosType==0)CacheManagerInit.carBosType = 0;
		    Integer bosType = CacheManagerInit.carBosType+1;
		    
		    String sb = "insert into mon_car_info(xnid,car_num,car_type,car_model,"
		    		+ "car_state,department_id,car_color,car_price,car_seating,car_pl,bos_type) values";
		    String id = ""; 
			String carNum = "";
			Integer carType = 0;
			Integer carModel=0;
			Integer carState=0;
			Integer departmentId=0;
			Integer carColor=0;
			String carPrice="";
			Integer carSeating = 0;
			String carPl= "";
			StringBuffer pksb = new StringBuffer();
			for(Map<String,Object> map:listMap){
				id = map.get("id").toString();
				carNum = map.get("carNum").toString();
				carType = Integer.parseInt(map.get("carType").toString());
				carModel = Integer.parseInt(map.get("carModel").toString());
				carState = Integer.parseInt(map.get("carState").toString());
				// 这里应该从部门缓存中取
				departmentId  = SysConst.deptCache.get(map.get("departmentId").toString());
				if(departmentId==null | departmentId==0){
					throw new CustomException(500,"上传数据部门有误!请检查数据");
				}
//				departmentId  = Integer.parseInt(map.get("departmentId").toString());
				carColor = Integer.parseInt(map.get("carColor").toString());
                carPrice =map.get("carColor").toString();
                carSeating = Integer.parseInt(map.get("carSeating").toString());
                carPl = map.get("carPl").toString();
                pksb.append("(");
//                pksb.append(""+id+","+carNum+","+carType+","+carModel+","
//                 +carState+","+departmentId+","+carColor+","+carPrice+","+carSeating+","+carPl+","+bosType);
                pksb.append("'"+id+"',"+"'"+carNum+"',"+carType+","+carModel+","+carState+","+departmentId+","+carColor+","+carPrice+","+carSeating+","+carPl+","+bosType);
                pksb.append(")");
                pksb.append(",");				
			}
		    sb= sb+pksb.substring(0, pksb.length()-1);
		    System.out.println("sql:"+sb);
		    try {
		    	jdbcTemplates.execute(sb);	
			} catch (Exception e) {
				throw new CustomException(500,"上传数据有误!请检查数据");
			}
		 // 添加刚加入的数据  更新缓存
		    String cacheSql = "select id,xnid,car_num from mon_car_info where bos_type ="+bosType+"";
		    List<Map<String,Object>> cacheMap = jdbcTemplates.queryForList(cacheSql);
		    JSONObject json = new JSONObject();
		    for(Map<String,Object> cachejson:cacheMap){
		    	json.clear();
    			json.put("dataType",3);
    			json.put("operType",1);
    			json.put("dataId",cachejson.get("id").toString());
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
		    	SysConst.carCache.put(cachejson.get("xnid").toString(), Integer.parseInt(cachejson.get("id").toString()));
		    }
			CacheManagerInit.deptBosType = bosType;  //这里重新赋值
		    return new ErrorResponseEntity(200,"添加成功");
	  }
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/carMod")
	  public ErrorResponseEntity carMod(@RequestBody String list
			  ){
	    	JSONArray tableData = JSONArray.fromObject(list);
	    	List<Map<String, Object>> listMap = (List) tableData;
	    	JSONArray jsonarr = JSONArray.fromObject(list);
		    System.out.println("修改车辆的数据:"+jsonarr);
		    logger.info("修改车辆的数据:"+jsonarr);
//		    String sb = "update mon_department_info ";
			Map<String,Object> map = listMap.get(0);
			// 获取 到 服务器实际的id
			Integer bdId = SysConst.carCache.get(map.get("id").toString());
			if(bdId==null || bdId ==0){
				throw new CustomException(505,"内部服务检查到无此id数据,请检查数据id");
			}
			if(!map.containsKey("id") || !map.containsKey("carNum") || !map.containsKey("carType")
					|| !map.containsKey("carModel")|| !map.containsKey("carState")
					|| !map.containsKey("departmentId")|| !map.containsKey("carColor")
					|| !map.containsKey("carPrice")|| !map.containsKey("carSeating")
					|| !map.containsKey("carPl")
			  ){
				 throw new CustomException(500,"请检查数据是否有未传字段!");
			}
			Car carT = carDao.findOne(bdId);
			if(carT==null){
				throw new CustomException(500,"亲,你要所修改的数据在市平台没有！请检查你所上传的id");
			}
			Car car =null;
			try {
				int departmentId  = SysConst.deptCache.get(map.get("departmentId").toString());
				map.put("departmentId", departmentId);
				map.put("id", bdId);
				car=  SwithUtil.mapToJavaBean(map,Car.class);
			} catch (Exception e) {
				 logger.info("Map转Bean转化失败:"+jsonarr);
				 System.out.println("Map转Bean转化失败:");
			}
			try {
				carDao.save(car);
				JSONObject json = new JSONObject();
    			json.put("dataType",3);
    			json.put("operType",2);
    			json.put("dataId",bdId);
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
				return new ErrorResponseEntity(200,"修改成功");
			} catch (Exception e) {
				throw new CustomException(500,"请检查数据是否有未传字段!");
			}
	  }
	 
	    
	    // 添加驾驶员的方法  
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/driverAdd")
	  public ErrorResponseEntity driverAdd(@RequestBody String data
			  ){
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	 List<Map<String, Object>> listMap = (List) tableData;
			JSONArray jsonarr = JSONArray.fromObject(data);
		    System.out.println("添加驾驶员的数据:"+jsonarr);
		    logger.info("添加驾驶员的数据:"+jsonarr);
		    // 校验数据
		    String f = VerificationData.verifiyDriverData(listMap,jdbcTemplates);
		    if(!f.equals("")){
		    	throw new CustomException(500,f);
		    }
		    if(CacheManagerInit.driverBosType==0)CacheManagerInit.driverBosType = 1;
		    Integer bosType = CacheManagerInit.driverBosType+1;
		    String sb = "insert into mon_user_info(xnid,user_name,user_sex,user_dept_id,"
		    		+ "user_phone,sf_no,is_type,bos_type) values";
		    String id = ""; 
			String userName = "";  //驾驶员姓名
			Integer userSex = 0;
			Integer userDeptId=0;
//			Integer isType =0;
			String userPhone= "";
			String sfNo ="";
			StringBuffer pksb = new StringBuffer();
			for(Map<String,Object> map:listMap){
				id = map.get("id").toString();
				userName = map.get("driverName").toString();
				userSex = Integer.parseInt(map.get("driverSex").toString());
				// 这里应该从部门缓存中取
				userDeptId  = SysConst.deptCache.get(map.get("driverDeptId").toString());
				if(userDeptId==null || userDeptId==0){
					throw new CustomException(500,"上传数据有误!请检查数据");
				}
//				userDeptId = Integer.parseInt(map.get("userDeptId").toString());
//				isType = Integer.parseInt(map.get("isType").toString());
				userPhone =map.get("driverPhone").toString();
				sfNo = map.get("sfNo").toString();
                pksb.append("(");
//              pksb.append(""+id+","+userName+","+userSex+","+userDeptId+","
//              +isType+","+userPhone+","+sfNo+","+bosType);
                pksb.append("'"+id+"',"+"'"+userName+"',"+userSex+","+userDeptId+","+"'"+userPhone+"',"+"'"+sfNo+"',1,"+bosType);
                pksb.append(")");
                pksb.append(",");				
			}
		    sb= sb+pksb.substring(0, pksb.length()-1);
		    System.out.println("sql:"+sb);
		    try {
		    	jdbcTemplates.execute(sb);	
			} catch (Exception e) {
				throw new CustomException(500,"上传数据有误!请检查数据");
			}
		 // 添加刚加入的数据  更新缓存
		    String cacheSql = "select id,xnid,user_name from mon_user_info where is_type=1 and bos_type ="+bosType+"";
		    List<Map<String,Object>> cacheMap = jdbcTemplates.queryForList(cacheSql);
		    JSONObject json = new JSONObject();
		    for(Map<String,Object> cachejson:cacheMap){
		    	json.clear();
    			json.put("dataType",5);
    			json.put("operType",1);
    			json.put("dataId",cachejson.get("id").toString());
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
		    	SysConst.driverCache.put(cachejson.get("xnid").toString(), Integer.parseInt(cachejson.get("id").toString()));
		    }
			CacheManagerInit.driverBosType = bosType;  //这里重新赋值
		    return new ErrorResponseEntity(200,"添加成功");
	  }
	    // 修改驾驶员的方法
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/driverMod")
	  public ErrorResponseEntity driverMod(@RequestBody String list
			  ){
	    	JSONArray tableData = JSONArray.fromObject(list);
	    	List<Map<String, Object>> listMap = (List) tableData;
	    	JSONArray jsonarr = JSONArray.fromObject(list);
		    System.out.println("修改驾驶员的数据:"+jsonarr);
		    logger.info("修改驾驶员的数据:"+jsonarr);
//		    String sb = "update mon_department_info ";
			Map<String,Object> map = listMap.get(0);
			// 获取 到 服务器实际的id
			Integer bdId = SysConst.driverCache.get(map.get("id").toString());
			if(bdId==null || bdId ==0){
				throw new CustomException(505,"内部服务检查到无此id数据,请检查数据id");
			}
			
			if(!map.containsKey("id") | !map.containsKey("driverName") | !map.containsKey("driverSex")
					| !map.containsKey("driverDeptId")| !map.containsKey("driverPhone")
					| !map.containsKey("sfNo")
			  ){
				 throw new CustomException(500,"请检查数据是否有未传字段!");
			}
			Driver driverT = driverDao.findOne(bdId);
			if(driverT==null){
				throw new CustomException(500,"亲,你要所修改的数据在市平台没有！请检查你所上传的id");
			}
			Driver driver =null;
			Integer driverDeptId  = SysConst.deptCache.get(map.get("driverDeptId").toString());
			if(driverDeptId==null || driverDeptId==0){
				throw new CustomException(500,"上传数据部门有误!请检查数据");
			}
			try {
				map.put("id", bdId);
				map.put("driverDeptId", driverDeptId);
				driver=  SwithUtil.mapToJavaBean(map,Driver.class);
			} catch (Exception e) {
				 logger.info("Map转Bean转化失败:"+jsonarr);
				 System.out.println("Map转Bean转化失败:"); 
			}
			JSONObject json = new JSONObject();
			try {
				driverDao.save(driver);
    			json.put("dataType",5);
    			json.put("operType",2);
    			json.put("dataId",bdId);
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
				return new ErrorResponseEntity(200,"修改成功");
			} catch (Exception e) {
				throw new CustomException(500,"请检查数据是否有未传字段!");
			}
	  }  
	    
	    
	 // 终端的方法  
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/termAdd")
	  public ErrorResponseEntity termAdd(@RequestBody String data
			  ){
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	 List<Map<String, Object>> listMap = (List) tableData;
			JSONArray jsonarr = JSONArray.fromObject(data);
		    System.out.println("添加终端的数据:"+jsonarr);
		    logger.info("添加终端的数据:"+jsonarr);
		    // 校验数据
		    String f = VerificationData.verifiyTermData(listMap,jdbcTemplates);
		    if(!f.equals("")){
		    	throw new CustomException(500,"上传数据有误!请检查数据");
		    }
		    
		    if(CacheManagerInit.termBosType==0)CacheManagerInit.termBosType = 0;
		    Integer bosType = CacheManagerInit.termBosType+1;
		    
		    String sb = "insert into mon_term_info(xnid,term_id,sim_num,term_dept_id,manufacturer_int,bos_type"
		    		+ ") values";
		    String id = ""; 
			String termId = "";  //终端id
			String simNum = "";  //终端卡号
			Integer termDeptId = 0;  //驾驶员姓名
			String manufacturerInt = "";  //驾驶员姓名
			StringBuffer pksb = new StringBuffer();
			Map<String,Object> carBindTermMap = new HashMap<String,Object>();
			String carId = ""; //所要绑定的车辆id
			for(Map<String,Object> map:listMap){
				id = map.get("id").toString();
				termId = map.get("termId").toString();
				simNum = map.get("simNum").toString();
				// 这里应该从部门缓存中取
				termDeptId  = SysConst.deptCache.get(map.get("termDeptId").toString());
				if(termDeptId==null | termDeptId==0){
					throw new CustomException(500,"上传数据有误!请检查数据");
				}
				manufacturerInt =map.get("manufacturerInt").toString();
				carId = map.get("carId").toString();
				if(!carId.equals("0")&&!carId.equals("")){
					carBindTermMap.put(id,carId);
				}
                pksb.append("(");
//                pksb.append("'"+id+"'","'"+termId+"'","'"+simNum+"'","+termDeptId+","'"
//                 +manufacturerInt+"'",+bosType);
                pksb.append("'"+id+"'"+","+"'"+termId+"'"+","+"'"+simNum+"'"+","+termDeptId+","+"'"+manufacturerInt+"'"+","+bosType+"");
                pksb.append(")");
                pksb.append(",");				
			}
		    sb= sb+pksb.substring(0, pksb.length()-1);
		    System.out.println("sql:"+sb);
		    try {
		    	jdbcTemplates.execute(sb);	
			} catch (Exception e) {
				throw new CustomException(500,"上传数据有误!请检查数据");
			}
		    // 添加刚加入的数据  更新缓存
		    String cacheSql = "select id,xnid,term_id from mon_term_info where bos_type ="+bosType+"";
		    List<Map<String,Object>> cacheMap = jdbcTemplates.queryForList(cacheSql);
		    String deleteSql = "delete from mon_term_car_info where term_id = ";
		    String insertSql = "insert into mon_term_car_info(term_id,car_id) values (";
		    String xnid = "'";
		    Integer idNew =0;
		    int carIdNew = 0;
		    JSONObject json = new JSONObject();
		    try{
		    for(Map<String,Object> cachejson:cacheMap){
		    	json.clear();
		    	json.put("dataType",4);
    			json.put("operType",1);
    			json.put("dataId",cachejson.get("id").toString());
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
		    	xnid= cachejson.get("xnid").toString();
		    	idNew= Integer.parseInt(cachejson.get("id").toString());
		    	SysConst.termCache.put(xnid,idNew);
		    // 添加绑定数据到车辆与驾驶员绑定表
		    	// 判断这个xnid是否有绑定的车辆id
		    	if(carBindTermMap.containsKey(xnid)){
		    		deleteSql = deleteSql+idNew;
		    		jdbcTemplates.execute(deleteSql);
		    		carIdNew = SysConst.carCache.get(carBindTermMap.get(xnid).toString());
		    		insertSql= insertSql+idNew+","+carIdNew+")";
		    		jdbcTemplates.execute(insertSql);
		    	}
		    }
		    }catch(Exception e){
		    	throw new CustomException(510,"添加数据成功,但是缓存数据失败");
		    }
			CacheManagerInit.termBosType = bosType;  //这里重新赋值
		    return new ErrorResponseEntity(200,"添加成功");
	  }
	    // 修改设备的方法
	    @SuppressWarnings("unchecked")
		@RequireSignature
		@RequestMapping("/termMod")
	  public ErrorResponseEntity termMod(@RequestBody String list
			  ){
	    	JSONArray tableData = JSONArray.fromObject(list);
	    	List<Map<String, Object>> listMap = (List) tableData;
	    	JSONArray jsonarr = JSONArray.fromObject(list);
		    System.out.println("修改车载设备的数据:"+jsonarr);
		    logger.info("修改车载设备的数据:"+jsonarr);
//		    String sb = "update mon_department_info ";
			Map<String,Object> map = listMap.get(0);
			// 获取 到 服务器实际的id
			Integer bdId = SysConst.termCache.get(map.get("id").toString());
			if(bdId==null || bdId ==0){
			throw new CustomException(505,"内部服务检查到无此id数据,请检查数据id");
			}
			if(!map.containsKey("id") || !map.containsKey("termId") || !map.containsKey("simNum")
					|| !map.containsKey("termDeptId")|| !map.containsKey("manufacturerInt")
					||!map.containsKey("carId")
			  ){
				 throw new CustomException(500,"请检查数据是否有未传字段!");
			}
			Term termT = termDao.findOne(bdId);
			if(termT==null){
				throw new CustomException(500,"亲,你要所修改的数据在市平台没有！请检查你所上传的id");
			}
			Term term =null;
			Integer termDeptId  = SysConst.deptCache.get(map.get("termDeptId").toString());
			if(termDeptId==null || termDeptId==0){
				throw new CustomException(500,"上传部门数据有误!请检查数据");
			}
			try {
				map.put("id", bdId);
				map.put("termDeptId", termDeptId);
				term=  SwithUtil.mapToJavaBean(map,Term.class);
			} catch (Exception e) {
				 logger.info("Map转Bean转化失败:"+jsonarr);
				 System.out.println("Map转Bean转化失败:");
			}
			try{
				String carId = map.get("carId").toString(); // 获取的车辆id
				if(carId.equals("0")||carId.equals("")){
					String deleteSql = "delete from mon_term_car_info where term_id ="+bdId;
					jdbcTemplates.execute(deleteSql);
				}else{
					int carIdNew = SysConst.carCache.get(carId); //获取西宁库的id
					String deleteSql = "delete from mon_term_car_info where term_id ="+bdId;
					String deleteCarSql = "delete from mon_term_car_info where car_id ="+carIdNew;
					jdbcTemplates.execute(deleteSql);
					jdbcTemplates.execute(deleteCarSql);
					String insertSql = "insert into mon_term_car_info(term_id,car_id) values("+bdId+","+carIdNew+")";
					jdbcTemplates.execute(insertSql);
				}
			}catch(Exception e){
				throw new CustomException(500,"绑定的carId是否上传正确?");
			}
			JSONObject json = new JSONObject();
			try {
				termDao.save(term);
		    	json.put("dataType",4);
    			json.put("operType",2);
    			json.put("dataId",bdId);
    			stringRedisTemplate.opsForList().rightPush("bjDataInfo", json.toString());
				return new ErrorResponseEntity(200,"修改成功");
			} catch (Exception e) {
				throw new CustomException(500,"请检查数据是否有未传字段!");
			}
	  } 
	    
	 // 添加订单的方法  
	    @SuppressWarnings({ "unchecked", "rawtypes" })
		@RequireSignature
		@RequestMapping("/orderAdd")
	  public ErrorResponseEntity orderAdd(@RequestBody String data
			  ){
	    	JSONArray tableData = JSONArray.fromObject(data);
	    	List<Map<String, Object>> listMap = (List) tableData;
			JSONArray jsonarr = JSONArray.fromObject(data);
		    System.out.println("添加订单的数据:"+jsonarr);
		    logger.info("添加订单的数据:"+jsonarr);
		    int b =0;
		    Integer applyDeptId = 0;
		    Integer applyNameId =0;
		    Integer applyCarId = 0;
		    Integer applyDriverId =0;
			for(Map<String,Object> map:listMap){
				if(!map.containsKey("id") || !map.containsKey("yongCheNo") || !map.containsKey("applyDeptId")
						|| !map.containsKey("applyNameId")|| !map.containsKey("applyNamePhone")
						|| !map.containsKey("applyCarId")|| !map.containsKey("applyDriverId")
						
						|| !map.containsKey("applyManNum")|| !map.containsKey("applyTime")
						|| !map.containsKey("applyFlowState")
						|| !map.containsKey("applyUseAddress")|| !map.containsKey("applyDestination")
						|| !map.containsKey("yongCheRen")|| !map.containsKey("applyUsePhone")
						|| !map.containsKey("applyUseTime")|| !map.containsKey("applyBackTime")
						|| !map.containsKey("yongCheXz")|| !map.containsKey("xsKm")
				  ){
					 throw new CustomException(500,"请检查数据是否有未传字段!");
				}
				Order order =null;
				try {
//					applyDeptId =SysConst.deptCache.get(map.get("applyDeptId").toString());
//					applyNameId = SysConst.userCache.get(map.get("applyNameId").toString());
//					applyCarId = SysConst.carCache.get(map.get("applyCarId").toString());
//					applyDriverId = SysConst.driverCache.get(map.get("applyDriverId").toString());
					applyDeptId =1786;
					applyNameId =1094;
					applyCarId =1681;
					applyDriverId =1097;
					if(applyDeptId ==null || applyNameId ==null || applyCarId ==null|| applyDriverId ==null){
						throw new CustomException(505,"请检查数据id是否正确!");
					}
					map.put("applyDeptId", applyDeptId);
					map.put("applyNameId", applyNameId);
					map.put("applyCarId", applyCarId);
					map.put("applyDriverId", applyDriverId);
					map.remove("id");
					order=  SwithUtil.mapToJavaBean(map,Order.class);
				} catch (Exception e) {
					 logger.info("Map转Bean转化失败:"+jsonarr);
					 return new ErrorResponseEntity(505,"请检查数据是否正确!");
				}
				try {
					orderDao.save(order);
				} catch (Exception e) {
					System.out.println("shibai");
					throw new CustomException(500,"请检查数据是否有未传字段!");
				}
			}
		    if(b==0){
		    	return new ErrorResponseEntity(200,"添加成功");
		    }else{
		    	return new ErrorResponseEntity(500,"检查数据");
		    }
	  }  
}
