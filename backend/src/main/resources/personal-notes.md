#### Verify Backend is Running SetUp

- **Application URL (swagger)**: https://localhost:8080/swagger-ui/index.html
- **Health Check**: https://localhost:8080/actuator/health
- **The H2 Database Console**: https://localhost:8080/h2-console
    - **JDBC URL**: `jdbc:h2:file:./data/tradingdb`
    - **Username**: `sa`
    - **Password**: (leave empty)

Failed when I tried to change the JDBC URL to `jdbc:h2:file:./data/tradingdb` when I clicked on connect the JDBC return to `jdbc:h2:~/test`.

#### Verify API Documentation and Monitoring
- **Swagger UI**: https://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: https://localhost:8080/api-docs
- **Application Metrics**: https://localhost:8080/actuator/metrics
- **Application Info**: https://localhost:8080/actuator/info


- **Actuator Endpoints**: https://localhost:8080/actuator/

The following message was displayed:

`Whitelabel Error Page`

`This application has no explicit mapping for /error, so you are seeing this as a fallback.`

`Sat Oct 04 20:28:44 BST 2025
There was an unexpected error (type=Not Found, status=404).`


#### Verify Frontend is Running
- **Application URL**: Check terminal for actual port assignment
    - **npm (Vite)**: Typically https://localhost:5173
    - **pnpm (Vite)**: Typically https://localhost:3000




#### Failing test:

- TradeServiceTest
- BookServiceTest