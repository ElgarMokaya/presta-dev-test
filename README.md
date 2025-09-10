
# Wallet Settlement System

This is a Spring Boot–based backend service for managing **customers, wallets, transactions, and reconciliation** of external/internal transaction data. It uses **PostgreSQL**, **RabbitMQ**, and **Liquibase** for database migrations.

---

## Features

- Customer management (create customers and wallets).
- Wallet transactions: top-up (credit wallet), consume (debit wallet), balance check, and transaction history with pagination support.
- Reconciliation module: upload external transaction files (CSV), generate reconciliation reports, list reconciliation results with pagination, and export reconciliation reports as CSV.
- Outbox pattern for reliable event publishing to RabbitMQ.
-  Dockerized for easy local setup.

---

## Tech Stack

**Java 21**, **Spring Boot 3**, **PostgreSQL 15**, **RabbitMQ**, **Liquibase**, **Docker & Docker Compose**, **MapStruct** for DTO mapping, **JUnit 5** & **Mockito** for testing.

---

## Setup Instructions

Clone the repository using `git clone https://github.com/your-username/wallet-settlement-system.git` and navigate into the folder. Build the application using `mvn clean package -DskipTests`.  

Start the application and its dependencies with `docker-compose up --build`.  
This will start:
- **PostgreSQL** 
- **RabbitMQ** 
- **Wallet Settlement Service** 

To stop containers run `docker-compose down`.  

If you want to run the service without Docker, ensure PostgreSQL is running on `localhost:5432` and RabbitMQ on `localhost:5672`, update `application.yml` accordingly, and start the service with `mvn spring-boot:run`.

---

## API Endpoints

- **Customer APIs**: create a new customer, create a wallet for a customer.  
- **Wallet APIs**: top-up wallet, consume from wallet, get balance, list transactions with pagination.  
- **Reconciliation APIs**: upload external transaction file (CSV), generate reconciliation report for a given date, list reconciliation items with pagination, export reconciliation results as CSV.  

---

## Assumptions

- Each client request must provide a unique **Idempotency-Key** header to avoid duplicate processing.  
- Wallet balance updates use **pessimistic locking** to ensure consistency under concurrency.  
- Reconciliation files must be CSVs containing transaction IDs and amounts.  
- Outbox events are stored in the database and later published reliably to RabbitMQ by a scheduled job.  
- Liquibase manages database schema creation and migrations automatically at startup.  

---

## 🐇 RabbitMQ Configuration

The application publishes wallet transactions (top-ups and consumes) to RabbitMQ using the following configuration:

```yaml
queue:
  wallet:
    exchange: wallet.txn.exchange
    routing-key: wallet.txn
    name: wallet.transactions
```

Messages are sent to **exchange** `wallet.txn.exchange` with **routing key** `wallet.txn` and stored in the **queue** `wallet.transactions`.  
