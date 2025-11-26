# Trade Capture System

The Trade Capture System is a Spring Boot REST API project in which I refactored test files and implemented advanced search and validation enhancements. 

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

I have approached this project iteratively. The initial solution revealed various errors, which I subsequently analysed and discussed with my mentor. The second iteration provided an opportunity to rectify these errors. I then reviewed the modifications to confirm that all tests were passing, continuing the pursuit of continuous improvement.

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
    -rate: BigDecimal
    -currency: String
  }
  class TradeDTO {
    +tradeId: Long
    +bookName: String
    +counterpartyName: String
  }

  class TradeMapper <<Mapper>> {
    +toEntity(dto:TradeDTO):Trade
    +toDto(entity:Trade):TradeDTO
  }

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

Demonstration of functionality and implementation.

## <u>Current Status</u>

- Progress: 50% complete, with test files refactored, search features implemented, and validation methods established.

- <u>Achievements</u>:
  - Refactored test files for both service and controller layers.  
  - Added the advanced trade search methods, including both single and multi-criteria searches with pagination as well as RSQL method.
  - Added a validation service to ensure user privileges and adherence of trade to the business rules.  

- <u>Remaining gaps</u>:
  - Refactoring of the remaining test about advanced search functionality, including more test scenarios for the advanced search (example: for scenarios where we have no trades matching the criteria) and validation methods (example: for scenario where the trade does nto complie with the business rules due to the date being 30 days in the past, for scenario where the User attempted to create a trade where they do not have permission). 
  - Implementation of the Trader Dashboard and Blotter System, Bug investigation and resolution, and Full-stack feature implementation remain outstanding.


## <u>Future Enhancements</u>

To enhance project outcomes, I would have used the following strategies:  
  - Detail the Unified Modeling Language (UML) diagram to showcase improvements and planned enhancements.
  - Include additional test cases to cover all advanced search scenarios. 
  - Add validation methods to ensure tradeLegDTO complies with business rules. Include status validation checks to confirm that User, Book, and Counterparty are active.
  - Research more information and develop the Trader Dashboard and Blotter System.
  - Investigate and resolve the bug within the calculateCashflowValue method to increase the accuracy.  
  - Research and implement Full-Stack feature.

## <u>Reflections & Lessons Learned</u>

In retrospect, I would have prioritised establishing a more precise UML flowchart from the outset of the project to understand the architecture, which would have greatly assisted with debugging.


- <u>Lessons Learned</u>:
  - The importance of iterative development to refactor tests.
  - The use of UML diagrams significantly clarifies the system architecture and enhances the communication with my mentor.
  - Starting with testing is important, as it ensures that the methods and features align with the requirements. 

This project has contributed to my personal and professional growth. It has increased my confidence in my skills with Spring Boot, UML modelling, and test refactoring.