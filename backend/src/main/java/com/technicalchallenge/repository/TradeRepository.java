package com.technicalchallenge.repository;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Trade;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    // Existing methods
    List<Trade> findByTradeId(Long tradeId);

    @Query("SELECT MAX(t.tradeId) FROM Trade t")
    Optional<Long> findMaxTradeId();

    @Query("SELECT MAX(t.version) FROM Trade t WHERE t.tradeId = :tradeId")
    Optional<Integer> findMaxVersionByTradeId(@Param("tradeId") Long tradeId);

    // NEW METHODS for service layer compatibility
    Optional<Trade> findByTradeIdAndActiveTrue(Long tradeId);

    List<Trade> findByActiveTrueOrderByTradeIdDesc();

    @Query("SELECT t FROM Trade t WHERE t.tradeId = :tradeId AND t.active = true ORDER BY t.version DESC")
    Optional<Trade> findLatestActiveVersionByTradeId(@Param("tradeId") Long tradeId);


    // NEW SEARCH METHOD to find specific trade containing a specific Counterparty
    List<Trade> findTradeByCounterparty (String counterparty);

    // NEW SEARCH METHOD to find specific trade containing a specific Book
    List<Trade> findTradeByBook (String book);

    // NEW SEARCH METHOD to find specific trade containing a specific TraderUser
    List<Trade> findTradeByTraderUser (String traderUser);

    // NEW SEARCH METHOD to find specific trade containing a specific TradeStatus
    List<Trade> findTradeByTradeStatus (String tradeStatus);

    // NEW SEARCH METHOD to find specific trade matching exactly a specific date ranges
    @Query("SELECT t FROM Trade t WHERE t.tradeStartDate = :dateFrom AND t.tradeMaturityDate = :dateTo")
    List<Trade> findTradeByDateRange (@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    // NEW SEARCH METHOD to find specific trade containing a specific all the specific criteria
    @Query("SELECT t FROM Trade t WHERE (:counterparty IS NULL OR t.counterparty = :counterparty) AND (:book IS NULL OR t.book = :book) AND (:traderUser IS NULL OR t.traderUser = :traderUser) AND (:tradeStatus IS NULL OR t.tradeStatus = :tradeStatus) AND (:dateFrom IS NULL OR t.tradeStartDate = :dateFrom) AND (:dateTo IS NULL OR t.tradeMaturityDate = :dateTo)")
    List<Trade> findTradeByAllCriteria (@Param("counterparty") String counterparty,@Param("book") String book, @Param("traderUser") String traderUser,@Param("tradeStatus") String tradeStatus,@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo );
}
