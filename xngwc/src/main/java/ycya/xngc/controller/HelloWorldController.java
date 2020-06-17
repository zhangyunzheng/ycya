package ycya.xngc.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ycya.xngc.CustomException;
import ycya.xngc.RequireSignature;
import ycya.xngc.bean.Dept;
import ycya.xngc.config.DataSourceConfig;
import ycya.xngc.dao.DeptDao;
import ycya.xngc.util.CacheManagerInit;
import ycya.xngc.util.LogFileName;
import ycya.xngc.util.LoggerUtils;

@RestController
public class HelloWorldController {
	
	private JdbcTemplate jdbcTemplates;
	private final Logger logger = LoggerUtils.Logger(LogFileName.BAITIAO_USER);
//	private Logger logger = LoggerFactory.getLogger(HelloWorldController.class);
//	 private Logger logger = Logger.getLogger("ERROR_ORDER");
		@RequireSignature
		@RequestMapping("/hello1")
	  public List<Dept> hello1(@RequestParam(required = true)Integer num) {
		 if (num == null) {
	            throw new CustomException(400, "num不能为空");
	        }
	        int i = 10 / num;
	        String sql = "select * from mon_deptment_info";
	        return jdbcTemplates.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(Dept.class));
			
	  }
	@RequireSignature
	  @RequestMapping("/hello2")
	  public List<String> hello2() {
		logger.info("白条用户进来了...");
	    return Arrays.asList(new String[] { "A", "B", "C" });
	  }
	    @Autowired
	    private DeptDao deptDao;
	    //删除
	    @RequestMapping(value ="/delete" ,method = RequestMethod.POST)
	    public  void weekdaylDelete(@RequestParam("id") Integer id){
	        System.out.println("删除执行");
	        deptDao.delete(id);
	    }
	    //查询所有
	    @RequestMapping(value ="/getall" ,method = RequestMethod.GET)
	    public List<Dept> girlList(){
	        System.out.println("查询所有执行");
	        return  deptDao.findAll();
	    }
	    
//	  //添加
//	    @RequestMapping(value ="/add" ,method = RequestMethod.POST)
//	    public Map<String,Object> weekdayAdd(@RequestParam(required = true) Integer id,
//	            @RequestParam(required = true) String deptName,
//	            @RequestParam(required = true) Integer deptType,
//	            @RequestParam(required = true) Integer qyId,
//	            @RequestParam(required = true) Integer deptSuperId
//	            ){
//	    	Map<String,Object> map  = new HashMap<String,Object>();
//	        Dept dept = new Dept();
//	        dept.setId(id);
//	        dept.setDeptName(deptName);
//	        dept.setDeptType(deptType);
//	        dept.setQyId(qyId);
//	        dept.setDeptSuperId(deptSuperId);
//	        deptDao.save(dept);
//	        map.put("code", 1);
//	        return map;
//	    }
	    
}
