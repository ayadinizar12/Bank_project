package com.sid.demo.web;

import java.util.List;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sid.demo.dtos.AccountHistoryDTO;
import com.sid.demo.dtos.AccountOperationDTO;
import com.sid.demo.dtos.BankAcountDTO;
import com.sid.demo.dtos.CreditDTO;
import com.sid.demo.dtos.DebitDTO;
import com.sid.demo.dtos.TransferDTO;
import com.sid.demo.exceptions.BalanceNotSufficient;
import com.sid.demo.exceptions.BankAccountNotFound;
import com.sid.demo.services.BankService;

@RestController
@CrossOrigin("*")
public class AccountRest {

	private BankService bankService;
	
	public AccountRest(BankService bankService) {
		this.bankService = bankService;
	}
	
	@GetMapping("/comptes/{accountId}")
	public BankAcountDTO getBankAcount(@PathVariable String accountId) throws BankAccountNotFound {
		return bankService.getBankAccount(accountId);
	}
	
	@GetMapping("/comptes")
	public List<BankAcountDTO> listAcounts(){		
		
		return bankService.bankAccountList();
	}
	
	@GetMapping("/comptes/{accountId}/operations")
	public List<AccountOperationDTO> getHistory(@PathVariable String accountId){
		
		return bankService.accountHistory(accountId) ;
	}
	
	public AccountHistoryDTO getAccountHistory( @PathVariable String accountId,
            									@RequestParam(name="page",defaultValue = "0") int page,
            									@RequestParam(name="size",defaultValue = "5")int size) throws BankAccountNotFound{
		
		return bankService.getAccountHistory(accountId, page, size);
	}
	
	@PostMapping("/comptes/debit")
	public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFound, BalanceNotSufficient {
		this.bankService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
		return debitDTO;
	}
	
	@PostMapping("/comptes/credit")
	public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFound {
		this.bankService.credit(creditDTO.getAccountId(),creditDTO.getAmount(), creditDTO.getDescription());
		return creditDTO;
	}
	
	@PostMapping("/comptes/transfer")
	public void transfer(@RequestBody TransferDTO transferDTO) throws BankAccountNotFound,BalanceNotSufficient {
		this.bankService.transfer(transferDTO.getAccountSource(),transferDTO.getAccountDestination(),transferDTO.getAmount());
	}
	
}
