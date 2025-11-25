# Trade Capture System

The Trade Capture System is a Spring Boot RESTAPI project where I refactored the test files and implemented enhancements. 

<!-- TABLE OF CONTENTS -->

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#introduction">Introduction</a>
    </li>
    <li>
      <a href="#code&tests-walkthrough">Code & Tests Walkthrough</a>
    </li>
    <li>
      <a href="#current-status">Current Status</a>
    </li>
    <li>
      <a href="#future-enhancements">Future Enhancements</a>
    </li>
    <li>
      <a href="#reflections-lessons-learned">Reflections & Lessons Learned</a>
    </li>
  </ol>
</details>

## <u>Introduction</u>

I approached the project iteratively, my first solution revealed errors, which I analysed, and discussed. The second iteration enables me to correct the errors. I then reviewed and confirmed that the tests were passing, continuing this quest for continuous improvement.

### UML overview

```plantuml
Runtime flow diagram for Trade Capture System

  class TradeController <<Controller>> {
    - tradeService: TradeService
    +getAllTrades(): List<TradeDTO>
    +getTradeById(id: Long): ResponseEntity<TradeDTO>
    +createTrade(tradeDTO: TradeDTO): ResponseEntity<TradeDTO>
    +searchByAllCriteria(counterparty: String, book: String, traderUser: String, tradeStatus: String, dateFrom: LocalDate, dateTo: LocalDate): ResponseEntity<Page<TradeDTO>>
  }


  class TradeService <<Service>> {
    +createTrade(tradeDTO: TradeDTO): Trade
    +findTradeByAllCriteria(counterparty: String, book: String, traderUser: String, tradeStatus: String, dateFrom: LocalDate, dateTo: LocalDate): Page<TradeDTO>
  }
  class TradeValidationService <<Service>> {
    +validateUserPrivileges(userId: String, operation: String, tradeDTO: TradeDTO): boolean
    +validateTradeBusinessRules(tradeDTO: TradeDTO): ValidationResult
  }

// Entities and DTOs
  class Trade {
    -id: Long
    -tradeId: String
    -tradeDate: LocalDate
    -tradeStartDate: LocalDate
    -tradeMaturityDate: LocalDate
    -tradeStatus: String
  }
  class TradeLeg {
    -legId: Long
    -notional: BigDecimal
    -rate: BigDecimal
    -currency: String
  }
  class Cashflow {
    -amount: BigDecimal
    -paymentDate: LocalDate
  }
  class TradeDTO {
    +tradeId: Long
    +bookName: String
    +counterpartyName: String
  }
  interface TradeRepository <<Repository>>
  interface TradeLegRepository <<Repository>>
  class TradeMapper <<Mapper>> {
    +toEntity(dto:TradeDTO):Trade
    +toDto(entity:Trade):TradeDTO
  }


Trade TradeLeg : contains
Trade Cashflow : "generates"
TradeController --> TradeService
TradeService --> TradeValidationService
TradeService --> TradeRepository
TradeService --> TradeLegRepository
TradeService --> TradeMapper


// Example: Create Trade flow

Customer -> TradeController : POST /api/trades\newTrade: TradeDTO
TradeController -> TradeService : createTrade(tradeDTO)
TradeService -> TradeValidationService : validateUserPrivileges(tradeDTO.traderUserId)
TradeValidationService --> TradeService : validationResult
TradeService -> TradeMapper : toEntity(tradeDTO)
TradeMapper --> TradeService : tradeEntity
TradeService -> TradeRepository : save(tradeEntity)
TradeRepository --> TradeService : savedEntity (with id)
TradeService -> Cashflow : generateCashflows(savedEntity)
Cashflow --> TradeService : cashflows
TradeService -> TradeRepository : save(cashflows) (or persist via cascade)
TradeService -> TradeMapper : toDto(savedEntityWithCashflows)
TradeMapper --> TradeController : TradeDTO
TradeController --> Customer : 201 Created\newTrade: TradeDTO

```

## <u>Code & Tests Walkthrough</u>

Demonstration.

## <u>Current Status</u>

- Progress: 50% complete  (Tests refactored, search features implemented, and validation).

- Achievements:
  - Refactored test files for service and controller layers.
  - Advanced Trade Search (single & multi‑criteria + RSQL)
  - Implemented validation service for user privileges and trade rules

- Remaining gaps:
  - Refactoring of the remaining test for advanced search.
  - The Enhancement 3: Trader Dashboard and Blotter System was not implemented.
  - The Bug Investigation and Fix was not addressed. 
  - And the Full-Stack Feature Implementation was not implemented.


## <u>Future Enhancements</u>

Manage time more effectively to complete all enhancements.
- Included more test cases for all advanced search scenarios.
- Trader Dashboard & Blotter System.
- Bug Investigation & Fix
- Full‑Stack Feature Implementation

## <u>Reflections & Lessons Learned</u>

- What I’d do differently:
Start with clearer UML flow.
Managing time better to complete all enhancements.

- Key takeaways:
Iterative development helped catch errors early.
UML diagrams clarified architecture and improved communication.
Testing strategy is as important as feature delivery.
Growth: strengthened skills in Spring Boot, UML modeling, and test refactoring.