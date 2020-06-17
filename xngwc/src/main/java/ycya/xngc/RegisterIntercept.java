package ycya.xngc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class RegisterIntercept extends WebMvcConfigurationSupport{
	@Autowired
	Intercept loginInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
		super.addInterceptors(registry);
	}
}
