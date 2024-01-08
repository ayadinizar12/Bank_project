package com.sid.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sid.demo.models.BankAcount;

public interface BankAccountRepo  extends JpaRepository<BankAcount,String>{

}
