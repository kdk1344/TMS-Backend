package com.tms.backend.controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tms.backend.service.UserService;
import com.tms.backend.vo.User;

import lombok.extern.log4j.Log4j;

/**
 * Handles requests for the application home page.
 */
@Controller
@Log4j
@RequestMapping("/tms/*")
public class UserController {
	
	@Autowired
    private UserService userService;
	// 12345

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/pdpc")
    public String PdpcPage() {
        return "pdpc";
    }

    @PostMapping("/login")
	public String login(@RequestParam(value="userID", required=false) String userID, HttpServletRequest req, @RequestParam("password") String password) throws Exception{
		HttpSession session = req.getSession();
		log.info(userID + " "+password);
		User check = userService.authenticateUser(userID, password);
		if(check != null) {
				log.info("check1");
				session.setAttribute("id", check.getuserID());
				return "redirect:/tms/dashboard";}
		else {
				session.setAttribute("error", "Invalid userID or password.");
				log.info("failcheck2!!");
				return "redirect:/tms/login";
			}
		}
}