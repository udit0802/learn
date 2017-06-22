package com.spring.security.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloController {
	
	private static final Logger log = LogManager.getLogger(HelloController.class); 

	@RequestMapping(value = {"/","/welcome"},method = RequestMethod.GET)
	public ModelAndView welcomePage(){
		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Hello World");
		model.addObject("message", "This is welcome page!");
		log.debug("inside home controller");
		model.setViewName("hello");
		return model;
	}
	
	@RequestMapping(value = "/admin",method = RequestMethod.GET)
	public ModelAndView adminPage(){
		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Hello World");
		model.addObject("message", "This is protected page!");
		log.debug("inside admin controller");
		model.setViewName("admin");
		return model;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap model,HttpServletResponse resp) {
		Cookie cookie=new Cookie("loginCookie", "1234");
		resp.addCookie(cookie);
		log.debug("inside login controller");
        return "login";
 
    }
 
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response,ModelMap model) {
 
        model.addAttribute("message",
                "You have successfully logged off from application !");
        log.debug("inside logout controller");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
        	new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
            	System.out.println("Jsessionid"+cookie.getName()+cookie.getValue());
                cookie.setValue("");
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        return "hello";
 
    }
 
    @RequestMapping(value = "/loginError", method = RequestMethod.GET)
    public String loginError(ModelMap model) {
        model.addAttribute("error", "true");
        log.debug("inside loginError controller");
        return "login";
 
    }
    
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String notAuthorized(ModelMap model) {
 
        return "403";
 
    }
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public @ResponseBody String test(HttpServletRequest request) {
    	Cookie[]cookie=request.getCookies();
    	String test=null;
    	if(cookie != null){
    		for(Cookie cookie1:cookie){
    			test=cookie1.getName();
    		System.out.println(cookie1.getName()+" value "+cookie1.getValue());	
    		}
    	}
        return test;
 
    }
}
