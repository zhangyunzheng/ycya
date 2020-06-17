package ycya.xngc.util;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class CacheManagerInit implements CommandLineRunner {

	private static CacheManagerInit instance = new CacheManagerInit();
	@Autowired
	private JdbcTemplate jdbcTemplate;
	public static Integer deptBosType = 100;
	public static Integer userBosType = 100; 
	public static Integer carBosType = 100; 
	public static Integer driverBosType = 100; 
	public static Integer termBosType = 100; 
	public static CacheManagerInit getInstance() {
		return instance;
	}
	/**
	 * 部门id与上传的id   缓存初始化
	 */
	public void deptCacheInit(){
		// 初始化缓存号
		String kwsql = " select IFNULL(a.bos_type,0) as bos_type from  (select bos_type  from mon_department_info  ORDER BY bos_type desc limit 0,1)a ";
		Map<String,Object> map = jdbcTemplate.queryForMap(kwsql);
		deptBosType  = Integer.parseInt(map.get("bos_type").toString());
		String sql = "select id,xnid from mon_department_info where is_flag = 0 and  bos_type is not null ";
		List<Map<String,Object>> listMap = jdbcTemplate.queryForList(sql);
		for(Map<String,Object> gmap :listMap){
			SysConst.deptCache.put(gmap.get("xnid").toString(),Integer.parseInt(gmap.get("id").toString()));
		}
	}
	
	/**
	 * 用户id与上传的id   缓存初始化
	 */
	public void userCacheInit(){
		// 初始化缓存号
		String kwsql = "select IFNULL(a.bos_type,0) as bos_type from  (select bos_type  from mon_user_info where is_type=0 ORDER BY bos_type desc limit 0,1)a";
		Map<String,Object> map = jdbcTemplate.queryForMap(kwsql);
		userBosType  = Integer.parseInt(map.get("bos_type").toString());
		String sql = "select id,xnid from mon_user_info where is_flag = 0 and is_type =0 and bos_type is not null ";
		List<Map<String,Object>> listMap = jdbcTemplate.queryForList(sql);
		for(Map<String,Object> gmap :listMap){
			SysConst.userCache.put(gmap.get("xnid").toString(),Integer.parseInt(gmap.get("id").toString()));
		}
	}
	
	
	/**
	 * 用户id与上传的id   缓存初始化
	 */
	public void carCacheInit(){
		// 初始化缓存号
		String kwsql = "select IFNULL(a.bos_type,0) as bos_type from  (select bos_type  from mon_car_info  ORDER BY bos_type desc limit 0,1)a";
		Map<String,Object> map = jdbcTemplate.queryForMap(kwsql);
		carBosType  = Integer.parseInt(map.get("bos_type").toString());
		String sql = "select id,xnid from mon_car_info where is_flag = 0 and bos_type is not null";
		List<Map<String,Object>> listMap = jdbcTemplate.queryForList(sql);
		for(Map<String,Object> gmap :listMap){
			SysConst.carCache.put(gmap.get("xnid").toString(),Integer.parseInt(gmap.get("id").toString()));
		}
	}
	
	/**
	 * 用户id与上传的id   缓存初始化
	 */
	public void driverCacheInit(){
		// 初始化缓存号
		String kwsql = "select IFNULL(a.bos_type,0) as bos_type from  (select bos_type  from mon_user_info where is_type=1 ORDER BY bos_type desc limit 0,1)a";
		Map<String,Object> map = jdbcTemplate.queryForMap(kwsql);
		driverBosType  = Integer.parseInt(map.get("bos_type").toString());
		String sql = "select id,xnid from mon_user_info where is_flag = 0 and is_type=1 and  bos_type is not null";
		List<Map<String,Object>> listMap = jdbcTemplate.queryForList(sql);
		for(Map<String,Object> gmap :listMap){
			SysConst.driverCache.put(gmap.get("xnid").toString(),Integer.parseInt(gmap.get("id").toString()));
		}
	}
	
	/**
	 * 用户id与上传的id   缓存初始化
	 */
	public void termCacheInit(){
		// 初始化缓存号
		String kwsql = "select IFNULL(a.bos_type,0) as bos_type from  (select bos_type from mon_term_info  ORDER BY bos_type desc limit 0,1)a";
		Map<String,Object> map = jdbcTemplate.queryForMap(kwsql);
		termBosType  = Integer.parseInt(map.get("bos_type").toString());
		String sql = "select id,xnid from mon_term_info where is_flag = 0 and  bos_type is not null";
		List<Map<String,Object>> listMap = jdbcTemplate.queryForList(sql);
		for(Map<String,Object> gmap :listMap){
			SysConst.termCache.put(gmap.get("xnid").toString(),Integer.parseInt(gmap.get("id").toString()));
		}
	}
	@Override
	public void run(String... arg0) throws Exception {
		deptCacheInit();
		carCacheInit();
		userCacheInit();
		driverCacheInit();
		termCacheInit();
	}
	
	
}
