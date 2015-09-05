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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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
	 * Log into the application, authentication itself is handled by Spring
	 * Session,
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login() {
		return new ModelAndView("user/login", "user", new User());
	}

	/*
	 * Logout is also handled by Spring Session
	 */

	@RequestMapping(value = "/register", method = { RequestMethod.GET,
			RequestMethod.POST })
	public ModelAndView register(
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "answer", required = false) String answer,
			@RequestParam(value = "firstname", required = false) String firstname,
			@RequestParam(value = "lastname", required = false) String lastname,
			@RequestParam(value = "next", required = false) String next,
			Model model, RedirectAttributes redirectAttrs) {

		/*
		 * If we need anything, let's just take the user to the registration
		 * page
		 */
		if (username == null || password == null || email == null
				|| answer == null || firstname == null || lastname == null) {
			return new ModelAndView("user/register", "user", new User());
		}

		/* Validate the user login */
		if (!userService.doesUserExist(username)) {
			userService.addRegularUser(username, password, email, answer,
					firstname, lastname);
			redirectAttrs.addFlashAttribute("success",
					"Successfully registered!");
			if (next != null) {
				return new ModelAndView("redirect:" + next);
			} else {
				return new ModelAndView("redirect:/login");
			}
		} else
			model.addAttribute("error", "Username already taken!");
		return new ModelAndView("user/register", "user", new User());
	}
		
	@RequestMapping(value = "/profile/{id}", method = RequestMethod.GET)
	public String getProfile(@PathVariable String id, Model model, RedirectAttributes redirectAttrs) {
		
		model.addAttribute("user", userService.loadUserById(Long.valueOf(id)));
		model.addAttribute("id", id);
		return "user/profile";
	}
	
	@RequestMapping(value = "/profile/{id}", method = RequestMethod.POST)
	public String updateProfile(@PathVariable Long id, 
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "firstname", required = false) String firstname,
			@RequestParam(value = "lastname", required = false) String lastname,
			Model model, RedirectAttributes redirectAttrs) {
		
		if (username == null || firstname == null || lastname == null || email == null) {
			model.addAttribute("danger","All attributes are required");
			model.addAttribute("user", userService.loadUserById(id));
			return "user/profile";
		} else {
		
			userService.updateUser(username, firstname, lastname, email);
			model.addAttribute("success","User Profile updated");
			model.addAttribute("user", userService.loadUserById(id));
			return "user/profile";
		}
	}

	@RequestMapping(value = "/update-password/{oldPassword}/{newPassword}", method = RequestMethod.GET)
	public String updatePassword(@PathVariable String oldPassword,
			@PathVariable String newPassword, RedirectAttributes redirectAttrs) {

		if (userService.validateCurrentPassword(oldPassword)) {
			userService.updatePasswordById(newPassword);
			redirectAttrs.addFlashAttribute("success",
					"Successfully changed password!");
			return "redirect:/get-settings";
		} else
			redirectAttrs.addFlashAttribute("error",
					"Unable to change password!");
		return "redirect:/get-settings";
	}


	@RequestMapping(value = "/get-public-users", method = RequestMethod.GET)
	public String getPublicUsers(Model model) {
		
		User currentUser = security.getSecurityContext().getUser();
		java.util.List<User> users = new java.util.ArrayList<User>();
		
		for(User u: userService.getPublicUsers()) {
			if (!currentUser.getId().equals(u.getId())) {
				users.add(u);
			}
		}
		
		model.addAttribute("users", users );
		return "user/public-users";
	}

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
			@RequestParam(value = "answer") String answer,
			RedirectAttributes redirectAttrs, Model model) {

		if (security.getSecurityContext().getUser().getPassword()
				.equals(oldpassword)) {
			/* Do the two passwords match? */
			if (newpassword.equals(confirmpassword)) {
				userService.updatePasswordById(newpassword);
				redirectAttrs.addFlashAttribute("success",
						"Successfully changed password!");
				return "redirect:/get-settings";
			}
		}
		
		if (!security.getSecurityContext().getUser().getAnswer().equals(answer)) {
			userService.updateAnswerById(answer);
			
			UserDetails currentUser = userService.loadUserByUsername(security.getSecurityContext().getUsername());
			
			Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(), currentUser.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			redirectAttrs.addFlashAttribute("success","Successfully changed color.");
			return "redirect:/get-settings";
		}
		
		redirectAttrs.addFlashAttribute("danger", "Unable to change settings!");
		return "redirect:/get-settings";
	}

	/*
	 * Forgot Password VULN - Username enumeration, inline password reset (not out of band)
	 */
	@RequestMapping(value = "/forgot-password", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String forgotPassword(
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "answer", required = false) String answer,
			@RequestParam(value = "password", required = false) String password,
			Model model, RedirectAttributes redirectAttrs) {

		/*
		 * If we need anything, let's just take the user to the forgot password
		 * page
		 */
		if (username == null || password == null || answer == null) {
			return "user/forgot-password";
		}

		/* Validate the username and answer */
		if (userService.doesUserExist(username)
				&& userService.isAnswerValid(username, answer)) {
			userService.updatePasswordByUsername(username, password);
			redirectAttrs.addFlashAttribute("success",
					"Password successfully updated!");
			return "redirect:/login";
		} else
			model.addAttribute("error", "true");
		return "user/forgot-password";
	}
}
