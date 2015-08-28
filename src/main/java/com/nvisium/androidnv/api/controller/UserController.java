package com.nvisium.androidnv.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nvisium.androidnv.api.model.User;
import com.nvisium.androidnv.api.security.SecurityUtils;
import com.nvisium.androidnv.api.service.UserService;

@Controller
public class UserController {

	@Autowired
	UserService userService;
	
	@Autowired
	SecurityUtils security;

	/*
	 * Log into the application, authentication itself is handled by Spring Session,
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login() {
		return new ModelAndView("user/login", "user", new User());
	}

	/*
	 * Logout is also handled by Spring Session
	 */
	
	/*
	 * Register for an account
	 * VULN - Username enumeration
	 */
	@RequestMapping(value = "/register", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView register(
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "firstname", required = false) String firstname,
			@RequestParam(value = "lastname", required = false) String lastname,
			Model model,
			RedirectAttributes redirectAttrs) {
		
		/* If we need anything, let's just take the user to the registration page */
		if (username == null || password == null || email == null ||
				firstname == null || lastname == null) {
			return new ModelAndView("user/register", "user", new User());
		}
		
		/* Validate the user login */
		if (!userService.doesUserExist(username)) {
			userService.addRegularUser(username, password, email, firstname,
					lastname);
			redirectAttrs.addFlashAttribute("success", "Successfully registered!");
			return new ModelAndView("redirect:/login");
		} else
			model.addAttribute("error", "Username already taken!");
			return new ModelAndView("user/register", "user", new User());
	}

	/*
	 * Update the account password
	 * VULN - CSRF
	 */
	@RequestMapping(value = "/update-password/{oldPassword}/{newPassword}", method = RequestMethod.GET)
	public String updatePassword(
			@PathVariable String oldPassword,
			@PathVariable String newPassword,
			RedirectAttributes redirectAttrs) {
		
		if (userService.validateCurrentPassword(oldPassword)) {
			userService.updatePasswordById(newPassword);
			redirectAttrs.addFlashAttribute("success", "Successfully changed password!");
			return "redirect:/get-settings";
		} else
			redirectAttrs.addFlashAttribute("error", "Unable to change password!");
			return "redirect:/get-settings";
	}

	/*
	 * VULN: Insecure defaults, Gets all users with privacy disabled; by
	 * default, privacy is disabled
	 */
	@RequestMapping(value = "/get-public-users", method = RequestMethod.GET)
	public String getPublicUsers(Model model) {
		model.addAttribute("users", userService.getPublicUsers());
		return "user/public-users";
	}

	/*
	 * Get all user settings
	 * TODO: Finish me
	 */
	@RequestMapping(value = "/get-settings", method = RequestMethod.GET)
	public String getSettings(Model model) {

		model.addAttribute("user", security.getSecurityContext().getUser());
		return "user/settings";
	}

	/*
	 * Update settings
	 */
	@RequestMapping(value = "/update-settings", method = RequestMethod.GET)
	public String updateSettings(
			@RequestParam(value = "oldpassword") String oldpassword,
			@RequestParam(value = "newpassword") String newpassword,
			@RequestParam(value = "confirmpassword") String confirmpassword,
			RedirectAttributes redirectAttrs,
			Model model) {
		
		if (security.getSecurityContext().getUser().getPassword().equals(oldpassword)) {
			/* Do the two passwords match? */
			if (newpassword.equals(confirmpassword)) {
				userService.updatePasswordById(newpassword);
				redirectAttrs.addFlashAttribute("success", "Successfully changed password!");
				return "redirect:/get-settings";
			}
		}
		redirectAttrs.addFlashAttribute("danger", "Unable to change password!");
		return "redirect:/get-settings";
	}
	
}
