package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Role;
import com.technicalchallenge.repository.ApplicationUserRepository;
import com.technicalchallenge.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class TradeValidationService {
    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    private final Clock clock;

    public TradeValidationService(Clock clock) {
        this.clock = clock;
    }

    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {
        ValidationResult validationResult = new ValidationResult();

        //Maturity date cannot be before start date or trade date
        if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeStartDate()) || tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeDate())) {
            throw new RuntimeException("Maturity date cannot be before Start date or Trade date");
        }
        //Start date cannot be before tradeDate
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

    public boolean validateUserPrivileges(String userId, String operation, TradeDTO tradeDTO) {
        Long userIdLong = null;
        if (userId != null && !userId.isEmpty()) {
            try {
                userIdLong = Long.valueOf(userId);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid User ID: " + userId);
            }
        }

        if (userIdLong == null && tradeDTO != null) {
            userIdLong = (tradeDTO.getTraderUserId() != null) ? tradeDTO.getTraderUserId() : tradeDTO.getTradeInputterUserId();
        }

        if (userIdLong == null) {
            throw new IllegalArgumentException("User ID is not set.");
        }

        ApplicationUser user = applicationUserRepository.findById(userIdLong).orElseThrow(() -> new RuntimeException("The User was not found"));

        Role role = (user.getUserProfile() != null) ? user.getUserProfile().getUserType() : null;
        if (role == null) return false;

        return role.allows(operation, tradeDTO, user);
    }
}
