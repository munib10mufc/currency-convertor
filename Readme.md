# Currency Exchange & Discount Calculator 💰

A Spring Boot application that calculates discounted bill totals with real-time currency conversion using third-party exchange rate APIs.

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.4-green.svg)
![Java](https://img.shields.io/badge/Java-17-blue.svg)

## Features ✨

- **Real-time currency conversion** via ExchangeRate-API
- **Multi-tier discount system**:
    - Employee (30%)
    - Affiliate (10%)
    - Loyal customer (5% after 2 years)
    - $5 discount per $100 on bill
- **REST API endpoint** with authentication
- **Validation** for all inputs
- **Result Caching** for communication with 3rd party
- **Configurable discounts** for different account types and long term customers
- **Configurable connectivity settings to 3rd part** URL, apiKey, connectTimeOut, readTimeout
- **JWT Validation** only authorized access is possible via valid JWT

## API Endpoints 🌐

| Endpoint                                  | Method | Description                                                                           |
|-------------------------------------------|--------|---------------------------------------------------------------------------------------|
| `/bill-calculator/auth?username=someName` | GET    | Get JWT token that needs to be passed in calculate endpoint as Bearer token in header |
| `/bill-calculator/api/calculate`          | POST   | Calculate discounted amount in target currency                                        |


**Run command:** `java -jar target/converter-0.0.1-SNAPSHOT.jar`

**Sample Request**:
```json
{
  "originalCurrency": "EUR",
  "targetCurrency": "USD",
  "userType": "EMPLOYEE",
  "customerSince": "2020-01-01",
  "itemsList": [
    {
      "name": "Laptop",
      "category": "ELECTRONICS",
      "price": 99999.99
    },
    {
      "name": "Milk",
      "category": "GROCERY",
      "price": 2.50
    },
    {
      "name": "T-Shirt",
      "category": "CLOTHING",
      "price": 19.99
    }
  ]
}
```

**3rd Party integration url:** This projects connects with `https://v6.exchangerate-api.com/v6`.
- Due to security reasons `EXCHANGE_API_KEY` is not part of this project code. You need to pass vm argument with your key to get exchange rates e.g `-DEXCHANGE_API_KEY=yourkey` while running the application.
