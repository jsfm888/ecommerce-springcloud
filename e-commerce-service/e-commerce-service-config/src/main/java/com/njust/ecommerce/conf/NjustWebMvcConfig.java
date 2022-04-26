package com.njust.ecommerce.conf;

import com.njust.ecommerce.filter.LoginUserInfoIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Web Mvc 配置
 * */
@Configuration
public class NjustWebMvcConfig extends WebMvcConfigurationSupport {



    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //添加用户身份统一登录拦截的拦截器
        registry.addInterceptor(new LoginUserInfoIntercepter())
                .addPathPatterns("/**");
    }

    /**
     * 让 MVC 加载 Swagger 的静态资源
     * */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**").
                addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        super.addResourceHandlers(registry);
    }
}
