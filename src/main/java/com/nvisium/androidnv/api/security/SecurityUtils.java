package com.nvisium.androidnv.api.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.nvisium.androidnv.api.model.NvUserDetails;

@Component
public class SecurityUtils {

	public NvUserDetails getSecurityContext() {
		return (NvUserDetails) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
	}

	public long getCurrentUserId() {
		return getSecurityContext().getUser().getId();
	}
}
