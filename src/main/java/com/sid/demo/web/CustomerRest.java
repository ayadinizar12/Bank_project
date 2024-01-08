package com.sid.demo.web;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sid.demo.dtos.CustomerDTO;
import com.sid.demo.exceptions.CustomerNotFound;
import com.sid.demo.models.Customer;
import com.sid.demo.services.BankService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRest {

	private BankService bankService;
	
	public CustomerRest(BankService bankService) {
		this.bankService = bankService;
	}

	@GetMapping("/clients")
	public List<CustomerDTO> customers(){
        return bankService.listCustomers();
	}
	
	@GetMapping("/client/{id}")
	public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerid) throws CustomerNotFound {
	
		return bankService.getCustomer(customerid);	
	}
	
	 @GetMapping("/customers/search")
	    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword",defaultValue = "") String keyword){
	        return bankService.searchCustomers("%"+keyword+"%");
	    }
	
	@PostMapping("/client")
	public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) {
		return bankService.saveCustomer(customerDTO);
	}
	
	@PutMapping("/clients/{customerId}")
	public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankService.updateCustomer(customerDTO);
    }
    @DeleteMapping("/clients/{id}")
    public void deleteCustomer(@PathVariable Long id){
        bankService.deleteCustomer(id);
    }
}


