package com.nvisium.androidnv.api.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.nvisium.androidnv.api.service.AdminService;

@Service
@Qualifier("adminService")
public class AdminServiceImpl implements AdminService {

}
