# Ethereum Wallet Management API

A Spring Boot application that provides REST APIs for managing Ethereum wallets, including wallet creation, balance checking, and transaction management.

## Features

- Create new Ethereum wallets
- Check wallet balances
- Send ETH between addresses
- View transaction history
- RESTful API endpoints with Swagger documentation
- Asynchronous operation processing
- Cross-origin resource sharing enabled

## Prerequisites

- Java 17 or higher
- Maven
- Access to an Ethereum network (mainnet, testnet, or local network)

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
cd demo
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

## Running Tests

The project includes comprehensive test coverage. To run the tests:

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn verify

# Run specific test class
mvn test -Dtest=WalletControllerTest

# Run specific test method
mvn test -Dtest=WalletControllerTest#testCreateWallet
```

Test coverage reports will be generated in `target/site/jacoco/index.html` after running `mvn verify`.

## API Endpoints

### Wallet Operations

- `POST /wallets/create` - Create a new Ethereum wallet
- `GET /wallets/balance/{address}` - Get wallet balance
- `POST /wallets/send` - Send ETH to another address
  - Parameters:
    - `from`: Sender's address
    - `to`: Recipient's address
    - `amount`: Amount of ETH to send
- `GET /wallets/transactions/{address}` - Get transaction history for an address

### Basic CRUD Operations

- `GET /wallets` - Get all wallets
- `GET /wallets/{id}` - Get wallet by ID
- `POST /wallets` - Create a new wallet
- `PUT /wallets/{id}` - Update wallet
- `DELETE /wallets/{id}` - Delete wallet

## API Documentation

The API documentation is available through Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Architecture

The application follows a microservices architecture pattern with:

- REST Controllers for handling HTTP requests
- Message Producer for asynchronous operation processing
- Repository layer for data persistence
- DTOs for data transfer
- Exception handling for error cases

## Error Handling

The application includes proper error handling for common scenarios:
- Wallet not found exceptions
- Invalid operations
- Network-related errors

## Security Considerations

- CORS is enabled for all origins (configurable in production)
- API endpoints are documented with Swagger annotations
- Asynchronous processing for long-running operations

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.