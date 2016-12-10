package com.trytara.subwaytalent.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
@Controller
public class MDLViewController {

	@RequestMapping("/password")
	public ModelAndView changePassword(ModelAndView modelAndView){
		modelAndView.setViewName("mdl/layouts/base");
        modelAndView.addObject("view", "mdl/fragments/changepasswordform");
		return modelAndView;
	}


	@RequestMapping("/password/success")
	public ModelAndView changePasswordSuccess(ModelAndView modelAndView){
		modelAndView.setViewName("mdl/layouts/base");
        modelAndView.addObject("view", "mdl/fragments/changepasswordsuccess");
		return modelAndView;
	}
	
	@RequestMapping("/")
	public String index(){
		return "forward:/admin/index.html";
	}
	
}
