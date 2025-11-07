package com.technicalchallenge.validation;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private Boolean valid = null;
    private final List<String> allErrors = new ArrayList<>();


    public boolean isValid() {
        return Boolean.TRUE.equals(valid);
    }

    public void addingErrorsToList(String error) {
        valid = false;
        allErrors.add(error);
    }

    public void markAsValid() {
        if (allErrors.isEmpty()) {
            valid = true;
        }
    }

    public List<String> getAllErrors() {
        return new ArrayList<>(allErrors);
    }

    @Override
    public String toString() {
        if (valid == null){
            return "Validation are not performed yet";
        } else if (valid) {
            return "Validation passed check";
        } else {
            return "Validation did not passed the check. Error:" + allErrors;
        }
    }

}
