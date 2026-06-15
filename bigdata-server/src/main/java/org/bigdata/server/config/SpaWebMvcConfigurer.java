package org.bigdata.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * SPA 支持配置 + Auth 拦截器注册
 */
@Configuration
@RequiredArgsConstructor
public class SpaWebMvcConfigurer implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/auth/**", "/systemManage/**")
                .excludePathPatterns("/auth/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new SpaPageResourceResolver());
    }

    /**
     * 自定义资源解析器：如果请求的资源不存在，则返回 index.html
     */
    static class SpaPageResourceResolver extends PathResourceResolver {

        @Override
        protected Resource getResource(String resourcePath, Resource location) throws IOException {
            Resource resource = location.createRelative(resourcePath);
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            // 如果是 API 请求，不转发
            if (resourcePath.startsWith("api/")) {
                return null;
            }
            // 返回 index.html 让前端路由处理
            return new ClassPathResource("/static/index.html");
        }
    }
}
