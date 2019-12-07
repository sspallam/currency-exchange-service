package com.in28minutes.microservices.currencyexchangeservice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyExchangeController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ExchangeValueRepository repository;
	
	@GetMapping("/ping")
	public String getHealth() {
		return "Current Exchange service  is up and running !!";		
	}
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public ExchangeValue retrieveExchangeValue
		(@PathVariable String from, @PathVariable String to){
		
		ExchangeValue exchangeValue = 
				repository.findByFromAndTo(from, to);
		
		exchangeValue.setPort(5000);
		
		logger.info("{}", exchangeValue);
		
		return exchangeValue;
	}
	
	@GetMapping("/currency-exchange")
	public List<ExchangeValue> getAllConversion(){
		List<ExchangeValue> exchangeList = repository.findAll();
		return exchangeList;
	}
	
	@PostMapping("/currency-exchange/from/{from}/to/{to}/rate/{rate}")
	public ExchangeValue newConversionRate(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal rate) {
		
		Long latestId = repository.findLatestRecord();
		ExchangeValue newExchangeValue = null;
		if(latestId!=null) {
		
			newExchangeValue = new ExchangeValue(latestId+1, from, to, rate);
		newExchangeValue.setPort(5000);
		}else {
			newExchangeValue = new ExchangeValue(new Long(1), from, to, rate);
			newExchangeValue.setPort(5000);		
		}
		
		return repository.save(newExchangeValue);
		
	}
	
	@PutMapping("/currency-exchange/id/{id}/rate/{rate}")
	public ExchangeValue updateConversionRate(@PathVariable Long id, @PathVariable BigDecimal rate) {
		
		Optional<ExchangeValue> getExchange = repository.findById(id);
		getExchange.get().setConversionMultiple(rate);		
		
		return repository.save(getExchange.get());
		
	}
	
	@DeleteMapping("/currency-exchange/id/{id}")
	public void deleteConversionRate(@PathVariable Long id) {		
		repository.deleteById(id);		
	}
}
