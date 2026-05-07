package com.ahmed.accounts.service.impl;

import com.ahmed.accounts.dto.AccountsDto;
import com.ahmed.accounts.dto.CardsDto;
import com.ahmed.accounts.dto.CustomerDetailsDto;
import com.ahmed.accounts.dto.LoansDto;
import com.ahmed.accounts.entity.Accounts;
import com.ahmed.accounts.entity.Customer;
import com.ahmed.accounts.exception.ResourceNotFoundException;
import com.ahmed.accounts.mapper.AccountsMapper;
import com.ahmed.accounts.mapper.CustomerMapper;
import com.ahmed.accounts.repository.AccountsRepository;
import com.ahmed.accounts.repository.CustomerRepository;
import com.ahmed.accounts.service.ICustomersService;
import com.ahmed.accounts.service.client.CardsFeignClient;
import com.ahmed.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(mobileNumber);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(mobileNumber);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;

    }
}
