package com.nvisium.androidnv.api.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier(value = "accountRepository")
public class AdminRepository {

}
