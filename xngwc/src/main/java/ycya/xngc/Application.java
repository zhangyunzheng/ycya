package ycya.xngc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.zaxxer.hikari.HikariDataSource;

import ycya.xngc.task.TaskDeamon;
import ycya.xngc.task.TheadTaskExceptionHandler;
import ycya.xngc.task.UpdateZyzCacheThread;
import ycya.xngc.util.CacheManagerInit;
// 主程序入口
@SpringBootApplication
public class Application extends SpringBootServletInitializer implements EmbeddedServletContainerCustomizer {
	  @Override  
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {  
	        return builder.sources(Application.class);  
	    } 
	 private static TaskDeamon deamon;
 public static void main(String[] args) {
//	 SpringApplication.run(Application.class, args);
	 ApplicationContext applicationContext = SpringApplication.run(
			 Application.class, args);
		DataSource dataSource = applicationContext.getBean(DataSource.class);
		System.out.println("datasource is :" + dataSource);
		//检查数据库是否是hikar数据库连接池
		if (!(dataSource instanceof HikariDataSource)) {
			System.err.println(" Wrong datasource type :"
					+ dataSource.getClass().getCanonicalName());
			System.exit(-1);
		}
		try {
			Connection connection = dataSource.getConnection();
			ResultSet rs = connection.createStatement()
					.executeQuery("SELECT 1");
			// 初始化缓存
			if (rs.first()) {
				System.out.println("Connection OK!");
			} else {
				System.out.println("Something is wrong");
			}
			//添加线程
			addThreadToDeamon(new UpdateZyzCacheThread());
			//启动守护线程
			Thread tdt = new Thread(deamon, "TaskDeamon");
			tdt.setUncaughtExceptionHandler(new TheadTaskExceptionHandler());
			tdt.setDaemon(true);
			tdt.start();
			// connection.close()
			// System.exit(0);
		} catch (SQLException e) {
			System.out.println("FAILED");
			e.printStackTrace();
			System.exit(-2);
		}

	}
	 
 @Override  
 public void customize(ConfigurableEmbeddedServletContainer container) {
     //指定端口地址
	 container.setPort(8090);  
 }
 private static void addThreadToDeamon(Runnable runnable){
		Thread thread = new Thread(runnable, runnable.getClass().getName());
		thread.setUncaughtExceptionHandler(new TheadTaskExceptionHandler());
//		deamon.setData(runnable.getClass().getName(), thread);
		thread.start();
	}
}
 