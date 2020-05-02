package com.questionnaire.demo.interceptor;

import com.questionnaire.demo.model.Token;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println(request.getRequestURI());
        if (request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("X-Token");
        HashMap<String, Object> data = new HashMap<>();
        if (token != null){
            boolean result = Token.verify(token);
            if(result){
                System.out.println("通过拦截器");
                return true;
            }
            // illegal token
            data.put("code", 20008);
            data.put("message","无效token");
            response.getWriter().write(data.toString());
        }
        System.out.println("认证失败");
        data.put("code", 20008);
        data.put("message","用户请登录");
        response.getWriter().write(data.toString());
        return false;
    }
}
