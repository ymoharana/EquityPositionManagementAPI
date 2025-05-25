# Equity Position Management API

A full-stack web application to manage equity positions based on trade transactions.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Business Rules](#business-rules)
- [Example Input/Output](#example-inputoutput)
- [Design Notes](#design-notes)

---

## Features

- Ingests trade transactions (INSERT, UPDATE, CANCEL) in any order
- Maintains and displays current equity positions per security
- Handles out-of-order and queued transactions robustly
- RESTful API with clear, testable code structure
- Angular frontend for real-time position and transaction viewing/search

---

## Tech Stack

- **Web:** Angular
- **API:** Java Spring Boot
- **DB:** Any (H2, MySQL, PostgreSQL, etc.)

---

## Getting Started

### 1. Clone the Repository
git clone https://github.com/your-org/equity-position-management.git
cd equity-position-management


### 2. Backend Setup
cd backend
./mvnw spring-boot:run

API will be available at `http://localhost:8080`.

### 3. Frontend Setup

cd frontend
npm install
ng serve

UI will be available at `http://localhost:4200`.

---

## API Endpoints

| Method | Endpoint                                 | Description                      |
|--------|------------------------------------------|----------------------------------|
| POST   | `/v1/api/equity/transactions/batch`      | Submit a batch of transactions   |
| GET    | `/v1/api/equity/positions`               | Get current positions            |
| GET    | `/v1/api/equity/transactions`            | Get all transactions             |
| GET    | `/v1/api/equity/transactions/{tradeId}`  | Get transactions by Trade ID     |

**Sample Transaction JSON:**
{
"tradeId": 1,
"version": 1,
"securityCode": "REL",
"quantity": 50,
"action": "INSERT",
"direction": "Buy"
}


---

## Business Rules

- **INSERT**: Always version 1 for a TradeID. Adds or updates position.
- **UPDATE**: Can change SecurityCode, Quantity, or Buy/Sell. Reverses previous version effect, applies new effect.
- **CANCEL**: Always the last version for a TradeID. Reverses the effect of the previous version.
- **Order**: Transactions can arrive in any sequence. Out-of-order transactions are queued and processed when possible.
- **Positions**: Updated after each transaction.

---

## Example Input/Output

**Input Transactions:**

| TransactionID | TradeID | Version | SecurityCode | Quantity | Action  | Direction |
|---------------|---------|---------|--------------|----------|---------|-----------|
| 1             | 1       | 1       | REL          | 50       | INSERT  | Buy       |
| 2             | 2       | 1       | ITC          | 40       | INSERT  | Sell      |
| 3             | 3       | 1       | INF          | 70       | INSERT  | Buy       |
| 4             | 1       | 2       | REL          | 60       | UPDATE  | Buy       |
| 5             | 2       | 2       | ITC          | 30       | CANCEL  | Buy       |
| 6             | 4       | 1       | INF          | 20       | INSERT  | Sell      |

**Output Positions:**

| SecurityCode | Quantity |
|--------------|----------|
| REL          | +60      |
| ITC          | 0        |
| INF          | +50      |

---

## Design Notes

- **In-Memory Pending Queue:** Out-of-order transactions (such as UPDATE or CANCEL before INSERT) are kept in an in-memory queue and processed when their dependencies arrive.
- **Testability:** All business logic is unit-tested; strategies and cache are easily mockable.
- **UI:** Angular frontend for position and transaction search, with responsive design.
---
---

**Questions or contributions?**  
Open an issue or pull request on [GitHub](https://github.com/your-org/equity-position-management).

---

