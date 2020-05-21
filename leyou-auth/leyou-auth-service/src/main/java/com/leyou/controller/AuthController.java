package com.leyou.controller;

import com.leyou.config.JwtProperties;
import com.leyou.domain.User;
import com.leyou.domain.UserInfo;
import com.leyou.service.AuthService;
import com.leyou.utils.CookieUtils;
import com.leyou.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

     @PostMapping("accredit")
     public ResponseEntity<Void> accredit(@RequestParam("username") String username,
                                           @RequestParam("password") String password,
                                          HttpServletRequest request, HttpServletResponse response){
         String token = this.authService.accredit(username, password);
         if (StringUtils.isBlank(token)){
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
         CookieUtils.setCookie(request, response, jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);
         return ResponseEntity.ok(null);
     }


    @GetMapping("verify")
    public ResponseEntity<UserInfo> accredit(@CookieValue("leyou-cookie") String cookie,
                                         HttpServletRequest request,
                                         HttpServletResponse response){
         try {
             UserInfo info = JwtUtils.getInfoFromToken(cookie, this.jwtProperties.getPublicKey());
             //刷新token
             //刷新jwt有效时间
             cookie = JwtUtils.generateToken(info, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
             //刷新cookie有效时间
             CookieUtils.setCookie(request, response, this.jwtProperties.getCookieName(), cookie, this.jwtProperties.getExpire() * 60);
             return ResponseEntity.ok(info);
         } catch (Exception e){

         }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
