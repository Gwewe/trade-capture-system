package com.technicalchallenge.service;

import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.repository.CounterpartyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CounterpartyServiceTest {
    @Mock
    private CounterpartyRepository counterpartyRepository;
    @InjectMocks
    private CounterpartyService counterpartyService;

    @Test
    void testFindCounterpartyById() {
        Counterparty counterparty = new Counterparty();
        counterparty.setId(1L);
        when(counterpartyRepository.findById(1L)).thenReturn(Optional.of(counterparty));
        Optional<Counterparty> found = counterpartyService.getCounterpartyById(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    // Add more tests for save, update, delete

    @Test
    void testSaveCounterparty() {
        Counterparty counterparty = new Counterparty();
        counterparty.setId(1L);
        when(counterpartyRepository.save(any(Counterparty.class))).thenReturn(counterparty);
        Counterparty saveCounterparty = counterpartyService.saveCounterparty(counterparty);

        assertNotNull(saveCounterparty);
        assertEquals(1L, saveCounterparty.getId());
    }

    @Test
    void testUpdateCounterparty() {
        //Setup for the existing counterparty
        Counterparty existingCounterparty = new Counterparty();
        existingCounterparty.setId(1L);
        existingCounterparty.setName("TestName");
        existingCounterparty.setAddress("French Guiana");
        existingCounterparty.setInternalCode(4L);

        //Setup for the amended data
        Counterparty amendedCounterparty = new Counterparty();
        amendedCounterparty.setName("TestName1.1");

        // Setup for the new updated counterparty
        Counterparty savedCounterparty = new Counterparty();
        savedCounterparty.setId(1L);
        savedCounterparty.setName("TestName1.1");
        savedCounterparty.setAddress("French Guiana");
        savedCounterparty.setInternalCode(4L);

        when(counterpartyRepository.findById(1L)).thenReturn(Optional.of(existingCounterparty));
        when(counterpartyRepository.save(any(Counterparty.class))).thenReturn(savedCounterparty);

        Counterparty result = counterpartyService.updateCounterparty(1L, amendedCounterparty);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TestName1.1", result.getName());
    }

    @Test
    void testDeleteCounterparty() {
        Long counterpartyId = 3L;

        doNothing().when(counterpartyRepository).deleteById(counterpartyId);
        counterpartyService.deleteCounterparty(counterpartyId);

        verify(counterpartyRepository, times(1)).deleteById(counterpartyId);
    }
}
