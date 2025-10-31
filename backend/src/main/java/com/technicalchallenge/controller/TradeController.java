package com.technicalchallenge.controller;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.service.TradeService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/trades")
@Validated
@Tag(name = "Trades", description = "Trade management operations including booking, searching, and lifecycle management")
public class TradeController {
    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private TradeService tradeService;
    @Autowired
    private TradeMapper tradeMapper;

    @GetMapping
    @Operation(summary = "Get all trades",
               description = "Retrieves a list of all trades in the system. Returns comprehensive trade information including legs and cashflows.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all trades",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<TradeDTO> getAllTrades() {
        logger.info("Fetching all trades");
        return tradeService.getAllTrades().stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trade by ID",
               description = "Retrieves a specific trade by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade found and returned successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Invalid trade ID format")
    })
    public ResponseEntity<TradeDTO> getTradeById(
            @Parameter(description = "Unique identifier of the trade", required = true)
            @PathVariable(name = "id") Long id) {
        logger.debug("Fetching trade by id: {}", id);
        return tradeService.getTradeById(id)
                .map(tradeMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
//    Single-criteria search endpoint for Counterparty
    @GetMapping("/search/counterparty")
    @Operation(summary = "Search method to find specific trades by Counterparty",
            description = "Retrieves specific trades that match the specified search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching trades found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trade not found"),
            @ApiResponse(responseCode = "400", description = "Invalid search format")
    })
    public ResponseEntity <?> findTradeByCounterparty (
            @Parameter(required = true) String counterparty){
        logger.info("Searching for the Counterparty: {}", counterparty);

        try {
        List<TradeDTO> results = tradeService.findTradeByCounterparty(counterparty);
            if (results.isEmpty()) {
                logger.info("No trades found for Counterparty: {}", counterparty);
                return ResponseEntity.notFound().build();
            }
            logger.info("Found {} matching trades for Counterparty.", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e){
            logger.error("Error retrieving the matching trades: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving the matching trades: " + e.getMessage());
        }
    }
    //    Single-criteria search endpoint for Book
    @GetMapping("/search/book")
    @Operation(summary = "Search method to find specific trades by Book",
            description = "Retrieves specific trades that match the specified search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching trades found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trade not found"),
            @ApiResponse(responseCode = "400", description = "Invalid search format")
    })
    public ResponseEntity <?> findTradeByBook (
            @Parameter(required = true) String book){
        logger.info("Searching for the Book: {}", book);

        try {
            List<TradeDTO> results = tradeService.findTradeByBook(book);
            if (results.isEmpty()) {
                logger.info("No trades found for Book: {}", book);
                return ResponseEntity.notFound().build();
            }
            logger.info("Found {} matching trades for Book", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e){
            logger.error("Error retrieving the matching trades: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving the matching trades: " + e.getMessage());
        }
    }

    //    Single-criteria search endpoint for Trader User
    @GetMapping("/search/traderUser")
    @Operation(summary = "Search method to find specific trades by TraderUser",
            description = "Retrieves specific trades that match the specified search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching trades found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trade not found"),
            @ApiResponse(responseCode = "400", description = "Invalid search format")
    })
    public ResponseEntity <?> findTradeByTraderUser (
            @Parameter(required = true) String traderUser){
        logger.info("Searching for the Trader User: {}", traderUser);

        try {
            List<TradeDTO> results = tradeService.findTradeByTraderUser(traderUser);
            if (results.isEmpty()) {
                logger.info("No trades found for Trader User: {}", traderUser);
                return ResponseEntity.notFound().build();
            }
            logger.info("Found {} matching trades for Trader User", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e){
            logger.error("Error retrieving the matching trades: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving the matching trades: " + e.getMessage());
        }
    }

    //    Single-criteria search endpoint for Trade Status
    @GetMapping("/search/traderUser")
    @Operation(summary = "Search method to find specific trades by Trade Status",
            description = "Retrieves specific trades that match the specified search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching trades found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trade not found"),
            @ApiResponse(responseCode = "400", description = "Invalid search format")
    })
    public ResponseEntity <?> findTradeByTradeStatus (
            @Parameter(required = true) String tradeStatus){
        logger.info("Searching for the Trade Status: {}", tradeStatus);

        try {
            List<TradeDTO> results = tradeService.findTradeByTradeStatus(tradeStatus);
            if (results.isEmpty()) {
                logger.info("No trades found for Trade Status: {}", tradeStatus);
                return ResponseEntity.notFound().build();
            }
            logger.info("Found {} matching trades for Trade Status", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e){
            logger.error("Error retrieving the matching trades: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving the matching trades: " + e.getMessage());
        }
    }

    //    Single-criteria search endpoint for Date Range
    @GetMapping("/search/dateRange")
    @Operation(summary = "Search method to find specific trades by date range",
            description = "Retrieves specific trades that match the specified search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching trades found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TradeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Trade not found"),
            @ApiResponse(responseCode = "400", description = "Invalid search format")
    })
    public ResponseEntity <?> findTradeByDateRange (
            @Parameter(required = true) LocalDate dateFrom,
            @Parameter(required = true) LocalDate dateTo){
        logger.info("Searching for the Trade with a specific date ranges: start date {}, maturity date {}", dateFrom, dateTo);

        try {
            List<TradeDTO> results = tradeService.findTradeByDateRange(dateFrom, dateTo);
            if (results.isEmpty()) {
                logger.info("No trades found for with a specific date ranges: start date {}, maturity date {}", dateFrom, dateTo);
                return ResponseEntity.notFound().build();
            }
            logger.info("Found {} matching trades for date ranges", results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e){
            logger.error("Error retrieving the matching trades: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving the matching trades: " + e.getMessage());
        }
    }

    //    Multiple-criteria search endpoint that consolidate all criteria
    @GetMapping("/search")
    @Operation(summary = "Search method with multiple criteria",
            description = "Retrieves specific trades that match the specified search parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matching trades found",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema (schema = @Schema(implementation = TradeDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Trade not found"),
            @ApiResponse(responseCode = "400", description = "Invalid search format")
    })
    public ResponseEntity <?> findTradeByAllCriteria (
            @RequestParam(required = false) String counterparty,
            @RequestParam(required = false) String book,
            @RequestParam(required = false) String traderUser,
            @RequestParam(required = false) String tradeStatus,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "15") int size){
        logger.info("Searching for the Trade with multiple criteria: counterparty={}, book={}, traderUser={}, tradeStatus={}, dateFrom={}, dateTo={}",
                counterparty, book, traderUser, tradeStatus, dateFrom, dateTo);
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by("tradeStartDate").descending());

        try {
            Page<TradeDTO> results = tradeService.findTradeByAllCriteria(counterparty, book, traderUser, tradeStatus, dateFrom, dateTo, pageable);
            if (results.isEmpty()) {
                logger.info("No trades found for the criteria provided");
                return ResponseEntity.notFound().build();
            }
            logger.info("Found {} matching trades.", results.getTotalElements());
            return ResponseEntity.ok(results);
        } catch (Exception e){
            logger.error("Error retrieving the matching trades: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error retrieving the matching trades: " + e.getMessage());
        }
    }


    @PostMapping
    @Operation(summary = "Create new trade",
               description = "Creates a new trade with the provided details. Automatically generates cashflows and validates business rules.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trade created successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid trade data or business rule violation"),
        @ApiResponse(responseCode = "500", description = "Internal server error during trade creation")
    })
    public ResponseEntity<?> createTrade(
            @Parameter(description = "Trade details for creation", required = true)
            @Valid @RequestBody TradeDTO tradeDTO) {
        logger.info("Creating new trade: {}", tradeDTO);
        try {
            Trade trade = tradeMapper.toEntity(tradeDTO);
            tradeService.populateReferenceDataByName(trade, tradeDTO);
            Trade savedTrade = tradeService.saveTrade(trade, tradeDTO);
            TradeDTO responseDTO = tradeMapper.toDto(savedTrade);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            logger.error("Error creating trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error creating trade: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing trade",
               description = "Updates an existing trade with new information. Subject to business rule validation and user privileges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade updated successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Invalid trade data or business rule violation"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to update trade")
    })
    public ResponseEntity<?> updateTrade(
            @Parameter(description = "Unique identifier of the trade to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated trade details", required = true)
            @Valid @RequestBody TradeDTO tradeDTO) {
        logger.info("Updating trade with id: {}", id);
        try {
            tradeDTO.setTradeId(id); // Ensure the ID matches
            Trade amendedTrade = tradeService.amendTrade(id, tradeDTO);
            TradeDTO responseDTO = tradeMapper.toDto(amendedTrade);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error updating trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error updating trade: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete trade",
               description = "Deletes an existing trade. This is a soft delete that changes the trade status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Trade cannot be deleted in current status"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to delete trade")
    })
    public ResponseEntity<?> deleteTrade(
            @Parameter(description = "Unique identifier of the trade to delete", required = true)
            @PathVariable Long id) {
        logger.info("Deleting trade with id: {}", id);
        try {
            tradeService.deleteTrade(id);
            return ResponseEntity.ok().body("Trade cancelled successfully");
        } catch (Exception e) {
            logger.error("Error deleting trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error deleting trade: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/terminate")
    @Operation(summary = "Terminate trade",
               description = "Terminates an existing trade before its natural maturity date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade terminated successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Trade cannot be terminated in current status"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to terminate trade")
    })
    public ResponseEntity<?> terminateTrade(
            @Parameter(description = "Unique identifier of the trade to terminate", required = true)
            @PathVariable Long id) {
        logger.info("Terminating trade with id: {}", id);
        try {
            Trade terminatedTrade = tradeService.terminateTrade(id);
            TradeDTO responseDTO = tradeMapper.toDto(terminatedTrade);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error terminating trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error terminating trade: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel trade",
               description = "Cancels an existing trade by changing its status to cancelled")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade cancelled successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Trade cannot be cancelled in current status"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to cancel trade")
    })
    public ResponseEntity<?> cancelTrade(
            @Parameter(description = "Unique identifier of the trade to cancel", required = true)
            @PathVariable Long id) {
        logger.info("Cancelling trade with id: {}", id);
        try {
            Trade cancelledTrade = tradeService.cancelTrade(id);
            TradeDTO responseDTO = tradeMapper.toDto(cancelledTrade);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error cancelling trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error cancelling trade: " + e.getMessage());
        }
    }
}
