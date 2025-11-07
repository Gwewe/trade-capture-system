package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.*;
import com.technicalchallenge.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeLegRepository tradeLegRepository;

    @Mock
    private CashflowRepository cashflowRepository;

    @Mock
    private TradeStatusRepository tradeStatusRepository;

    @Mock
    private CounterpartyRepository counterpartyRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ApplicationUserRepository applicationUserRepository;


    @Mock
    private AdditionalInfoService additionalInfoService;


    @Mock
    private TradeMapper tradeMapper;

    @InjectMocks
    private TradeService tradeService;

    private TradeDTO tradeDTO;
    private Trade trade;
    private Book bookOne;
    private Counterparty counterparty;
    private TradeStatus newTradeStatus;
    private TradeStatus amendTradeStatus;
    private TradeValidationService tradeValidationService;



    @BeforeEach
    void SetUp() {
        tradeDTOSetUp();
        tradeEntitySetUp();

        // fixed clock for deterministic validation
        Clock fixedClock = Clock.fixed(LocalDate.of(2025, 1, 15).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        tradeValidationService = new TradeValidationService(fixedClock);
        ReflectionTestUtils.setField(tradeService, "tradeValidationService", tradeValidationService);
        ReflectionTestUtils.setField(tradeValidationService, "applicationUserRepository", applicationUserRepository);


        ApplicationUser user = new ApplicationUser();
        user.setId(2L);
        UserProfile profile = new UserProfile();
        profile.setUserType(Role.TRADER);
        user.setUserProfile(profile);

        lenient().when(applicationUserRepository.findById(2L)).thenReturn(Optional.of(user));
    }

    void tradeDTOSetUp() {
        // Separate setup for tradeDTO

        tradeDTO = new TradeDTO();
        tradeDTO.setTradeId(100001L);
        tradeDTO.setTradeDate(LocalDate.of(2025, 1, 15));
        tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 17));
        tradeDTO.setTradeMaturityDate(LocalDate.of(2026, 1, 17));
        tradeDTO.setVersion(1);
        tradeDTO.setBookName("FX-BOOK-1Test");
        tradeDTO.setBookId(1000L);
        tradeDTO.setCounterpartyName("BigBankTest");
        tradeDTO.setTradeStatus("NEW");
        tradeDTO.setTraderUserId(2L);

        TradeLegDTO leg1 = new TradeLegDTO();
        leg1.setNotional(BigDecimal.valueOf(1000000));
        leg1.setRate(0.05);
        leg1.setCalculationPeriodSchedule("Test");

        TradeLegDTO leg2 = new TradeLegDTO();
        leg2.setNotional(BigDecimal.valueOf(1000000));
        leg2.setRate(0.0);
        leg2.setCalculationPeriodSchedule("1M");

        tradeDTO.setTradeLegs(Arrays.asList(leg1, leg2));

    }

     void tradeEntitySetUp() {
        // Separate setup for trade Object/Entity

        newTradeStatus = new TradeStatus();
        newTradeStatus.setTradeStatus("NEW");


        trade = new Trade();
        trade.setId(1L);
        trade.setTradeId(100001L);
        trade.setVersion(1);
        trade.setTradeStatus(newTradeStatus);

        counterparty = new Counterparty();
        trade.setCounterparty(counterparty);

        bookOne = new Book();
        bookOne.setId(1000L);
        bookOne.setBookName("FX-BOOK-1Test");

    }

    @Test
    void testCreateTrade_Success() {
        // Given
        tradeDTO.setTraderUserId(2L);

        TradeLeg newTradeLeg = new TradeLeg();
        newTradeLeg.setLegId(1L);
        newTradeLeg.setTrade(trade);
        newTradeLeg.setNotional(BigDecimal.valueOf(1000000.0));
        newTradeLeg.setRate(0.04);

        when(bookRepository.findByBookName("FX-BOOK-1Test")).thenReturn(Optional.of(bookOne));
        when(counterpartyRepository.findByName("BigBankTest")).thenReturn(Optional.of(counterparty));
        when(tradeStatusRepository.findByTradeStatus("NEW")).thenReturn(Optional.of(newTradeStatus));

        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn((newTradeLeg));
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        // When
        Trade result = tradeService.createTrade(tradeDTO);

        // Then
        assertNotNull(result);
        assertEquals(100001L, result.getTradeId());
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    void testCreateTrade_InvalidDates_ShouldFail() {
        // Given - This test is intentionally failing for candidates to fix
        tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 10)); // Before trade date

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeService.createTrade(tradeDTO);
        });

        // This assertion is fixed
        assertEquals("Start date cannot be before trade date", exception.getMessage());
    }

    @Test
    void testCreateTrade_InvalidLegCount_ShouldFail() {
        // Given
        tradeDTO.setTradeLegs(Arrays.asList(new TradeLegDTO())); // Only 1 leg

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeService.createTrade(tradeDTO);
        });

        assertTrue(exception.getMessage().contains("exactly 2 legs"));
    }

    @Test
    void testGetTradeById_Found() {
        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(100001L)).thenReturn(Optional.of(trade));

        // When
        Optional<Trade> result = tradeService.getTradeById(100001L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(100001L, result.get().getTradeId());
    }

    @Test
    void testGetTradeById_NotFound() {
        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // When
        Optional<Trade> result = tradeService.getTradeById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testAmendTrade_Success() {
        // Given

        amendTradeStatus = new TradeStatus();
        amendTradeStatus.setTradeStatus("AMENDED");

        TradeDTO amendTradeDTO = new TradeDTO();
        amendTradeDTO.setTradeStatus("AMENDED");
        amendTradeDTO.setTradeDate(tradeDTO.getTradeDate());
        amendTradeDTO.setTradeStartDate(tradeDTO.getTradeStartDate());
        amendTradeDTO.setTradeMaturityDate(tradeDTO.getTradeMaturityDate());

        TradeLegDTO amendTradeLegOne = new TradeLegDTO();
        amendTradeLegOne.setLegId(1L);
        amendTradeLegOne.setNotional(BigDecimal.valueOf(1000000.0));
        amendTradeLegOne.setRate(0.05);

        TradeLegDTO amendTradeLegTwo = new TradeLegDTO();
        amendTradeLegTwo.setLegId(2L);
        amendTradeLegTwo.setNotional(BigDecimal.valueOf(1000000.0));
        amendTradeLegTwo.setRate(0.00);

        amendTradeDTO.setTradeLegs(Arrays.asList(amendTradeLegOne, amendTradeLegTwo));

        when(tradeRepository.findByTradeIdAndActiveTrue(100001L)).thenReturn(Optional.of(trade));
        when(tradeStatusRepository.findByTradeStatus("AMENDED")).thenReturn(Optional.of(amendTradeStatus));
        when(tradeLegRepository.save(any(TradeLeg.class))).thenAnswer(invocation -> invocation.getArgument(0));



        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        // When
        Trade result = tradeService.amendTrade(100001L, amendTradeDTO);

        // Then
        assertNotNull(result);
        verify(tradeRepository, times(2)).save(any(Trade.class)); // Save old and new
    }

    @Test
    void testAmendTrade_TradeNotFound() {
        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeService.amendTrade(999L, tradeDTO);
        });

        assertTrue(exception.getMessage().contains("Trade not found"));
    }

    // This test has a deliberate bug for candidates to find and fix
    @Test
    void testCashflowGeneration_MonthlySchedule() {
        // This test method is incomplete and has logical errors
        // Candidates need to implement proper cashflow testing

        // Given - setup is incomplete
        //setup for the TradeLegDTO and TradeDTO
        TradeLegDTO leg1 = new TradeLegDTO();
        leg1.setNotional(BigDecimal.valueOf(1000000));
        leg1.setRate(0.05);
        leg1.setCalculationPeriodSchedule("1M");


        TradeLegDTO leg2 = new TradeLegDTO();
        leg2.setNotional(BigDecimal.valueOf(1000000));
        leg2.setRate(0.05);
        leg2.setCalculationPeriodSchedule("1M");

        tradeDTO.setTradeLegs(Arrays.asList(leg1, leg2));
        tradeDTO.setBookName("FX-BOOK-1Test");
        tradeDTO.setCounterpartyName("BigBankTest");
        tradeDTO.setTradeStatus("NEW");

        //setup for the TradeLeg entity and Trade Entity
        Schedule tradeSchedule = new Schedule();
        tradeSchedule.setSchedule("1M");

        TradeStatus newTradeStatus = new TradeStatus();
        newTradeStatus.setTradeStatus("NEW");

        TradeLeg legOne = new TradeLeg();
        legOne.setCalculationPeriodSchedule(tradeSchedule);
        legOne.setNotional(BigDecimal.valueOf(1000000));

        TradeLeg legTwo = new TradeLeg();
        legTwo.setCalculationPeriodSchedule(tradeSchedule);
        legTwo.setNotional(BigDecimal.valueOf(1000000));

        Trade tradeEntity = new Trade();
        tradeEntity.setTradeId(tradeDTO.getTradeId());
        tradeEntity.setBook(bookOne);
        tradeEntity.setCounterparty(counterparty);
        tradeEntity.setTradeStatus(newTradeStatus);
        tradeEntity.setTradeLegs(Arrays.asList(legOne, legTwo));


        when(tradeRepository.save(any(Trade.class))).thenReturn(tradeEntity);
        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn(legOne, legTwo);

        when(bookRepository.findByBookName("FX-BOOK-1Test")).thenReturn(Optional.of(bookOne));
        when(counterpartyRepository.findByName("BigBankTest")).thenReturn(Optional.of(counterparty));
        when(tradeStatusRepository.findByTradeStatus("NEW")).thenReturn(Optional.of(newTradeStatus));


        // When - used method createTrade(tradeDTO) to call the private generateCashflows which is nested in the createTradeLegsWithCashflows method which is nested createTrade(tradeDTO).
        Trade result = tradeService.createTrade(tradeDTO);


        // Then - assertions are wrong/missing
        assertNotNull(result);
        verify(cashflowRepository, times(24)).save(any(Cashflow.class));
    }
}
