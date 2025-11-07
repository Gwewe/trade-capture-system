package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.validation.ValidationResult;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
public class TradeValidationService {
    private final Clock clock;

    public TradeValidationService(Clock clock) {
        this.clock = clock;
    }

    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {
        ValidationResult validationResult = new ValidationResult();

//        Maturity date cannot be before start date or trade date
        if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeStartDate()) || tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeDate())) {
            throw new RuntimeException("Maturity date cannot be before Start date or Trade date");
        }
//        Start date cannot be before tradeDate
        if (tradeDTO.getTradeStartDate().isBefore(tradeDTO.getTradeDate())) {
            throw new RuntimeException("Start date cannot be before Trade date");
        }
        //Trade date cannot be more than 30 days in the past
        if (tradeDTO.getTradeDate().isBefore(LocalDate.now(clock).minusDays(30))) {
            throw new RuntimeException("Trade date cannot be more than 30 days in the past");
        }
        validationResult.markAsValid();
        return validationResult;
    }
}
