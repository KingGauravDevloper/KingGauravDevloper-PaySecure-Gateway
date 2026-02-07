# 💳 PaySecure Gateway - Real-time Payment Processing with AI Fraud Detection

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Docker](https://img.shields.io/badge/Docker-Ready-brightgreen.svg)]()
[![Status](https://img.shields.io/badge/status-Production%20Ready-success.svg)]()

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Architecture & System Design](#-architecture--system-design)
- [Technology Stack](#-technology-stack)
- [Data Flow](#-data-flow)
- [Prerequisites](#-prerequisites)
- [Quick Start with Docker](#-quick-start-with-docker)
- [Manual Setup (Without Docker)](#-manual-setup-without-docker)
- [Project Structure](#-project-structure)
- [API Documentation](#-api-documentation)
- [Features](#-features)
- [Troubleshooting](#-troubleshooting)
- [Next Steps & Enhancements](#-next-steps--enhancements)
- [Interview Talking Points](#-interview-talking-points)

---

## 🎯 Project Overview

**PaySecure Gateway** is a **production-ready payment processing system** with **real-time AI-powered fraud detection**. 

### What Does It Do?

1. **Accepts Payment Transactions** - Users submit payment requests via frontend
2. **Processes Payments** - Backend validates and processes transactions
3. **Detects Fraud** - AI service analyzes transaction patterns for fraud risk
4. **Provides Risk Score** - Returns confidence level (0.0-1.0) for each transaction
5. **Blocks Suspicious Transactions** - Stops high-risk transactions automatically

### Why It's Important?

- **Real-time Processing**: Fraud detection happens instantly, not in batch
- **Microservices Architecture**: Each component is independent and scalable
- **Production-Ready**: Uses Docker for consistent deployment
- **Full-Stack**: Frontend + Backend + AI all integrated

---

## 🏗️ Architecture & System Design

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     User's Browser                          │
│                                                             │
│                    http://localhost:3000                    │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│              Frontend Layer (React + TypeScript)            │
│                   - Dashboard UI                            │
│                   - Service Status Monitoring               │
│                   - Health Checks                           │
│                   Port: 3000                                │
└────────────────────────────┬────────────────────────────────┘
                             │
                    HTTP REST Requests
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│         Backend Layer (Spring Boot + REST API)              │
│                   Port: 8080                                │
│    ┌──────────────────────────────────────────────┐         │
│    │  REST Endpoints:                             │         │
│    │  - POST /api/v1/transactions                 │         │
│    │  - GET /api/v1/transactions/{id}             │         │
│    │  - GET /api/v1/actuator/health               │         │
│    └──────────────────────────────────────────────┘         │
│                     │                                       │
│      ┌──────────────┼──────────────┐                        │
│      ↓                             ↓                        │
│   MySQL DB              AI Service Call                     │
│   (Port 3306)          (Fraud Detection)                    │
└──────────────────────────────────────────────────────────────┘
        │                             │
        │                             ↓
        │            ┌──────────────────────────────┐
        │            │  AI Service (FastAPI)        │
        │            │  Fraud Detection Algorithm   │
        │            │  Port: 8001                  │
        │            │                              │
        │            │  - Analyzes transactions     │
        │            │  - Calculates fraud score    │
        │            │  - Returns risk level        │
        │            └──────────────────────────────┘
        │
        ↓
    ┌──────────────────────────────┐
    │  MySQL Database              │
    │  (Persistent Storage)        │
    │  Port: 3306                  │
    │  - Transaction records       │
    │  - User data                 │
    │  - Payment history           │
    └──────────────────────────────┘
```

### Data Flow Diagram

```
User visits http://localhost:3000
         ↓
Frontend loads (React Dashboard)
         ↓
User clicks "Refresh Status"
         ↓
Frontend makes HTTP requests to Backend & AI Service
    ├─ GET http://localhost:8080/api/v1/actuator/health
    └─ GET http://localhost:8001/health
         ↓
Backend responds with database status
AI Service responds with health status
         ↓
Frontend displays "UP" status for both
         ↓
All systems are ready! ✅
```

### Transaction Processing Flow

```
1. User Submits Transaction
   (amount: $500, merchant: ABC Shop)
         ↓
2. Backend Receives Request
   Validates amount, merchant ID, user ID
         ↓
3. Backend Calls AI Service
   Sends transaction details
         ↓
4. AI Service Analyzes
   - Checks transaction amount
   - Checks transaction type
   - Generates fraud score (0.0-1.0)
         ↓
5. AI Returns Response
   {
     "fraud_score": 0.35,
     "is_fraudulent": false,
     "risk_level": "LOW"
   }
         ↓
6. Backend Makes Decision
   if fraud_score < 0.7 → ALLOW ✅
   if fraud_score >= 0.7 → BLOCK ❌
         ↓
7. Backend Stores in MySQL
   Saves transaction with status
         ↓
8. Frontend Shows Result
   "Payment Successful!" or "Payment Declined"
```

---

## 🛠️ Technology Stack

### Frontend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **React** | 18.x | UI Library |
| **TypeScript** | 5.x | Type Safety |
| **Vite** | 5.x | Build Tool |
| **Axios** | 1.x | HTTP Client |
| **Node.js** | 18 LTS | Runtime |

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Spring Boot** | 3.2.0 | REST Framework |
| **Spring Data JPA** | 3.2.x | Database ORM |
| **MySQL** | 8.0 | Database |
| **Hibernate** | 6.3.x | ORM Framework |
| **Springdoc** | 2.x | Swagger/OpenAPI |
| **Java** | 21 | Language |

### AI Service
| Technology | Version | Purpose |
|-----------|---------|---------|
| **FastAPI** | 0.104.x | REST Framework |
| **Python** | 3.11 | Language |
| **Uvicorn** | 0.24.x | ASGI Server |
| **Pydantic** | 2.5.x | Data Validation |
| **NumPy** | 1.26.x | Numerical Computing |
| **Scikit-learn** | 1.3.x | ML Library |
| **Pandas** | 2.1.x | Data Processing |

### DevOps & Containerization
| Technology | Purpose |
|-----------|---------|
| **Docker** | Containerization |
| **Docker Compose** | Multi-container Orchestration |
| **Docker Network** | Container Communication |
| **Docker Volumes** | Persistent Storage |

---

## 📊 Data Flow

### Complete Transaction Flow with Services

```
┌────────────────────────────────────────────────────────────────┐
│                        FRONTEND (React)                        │
│                   Dashboard - http://3000                      │
└────────────────────────────┬─────────────────────────────────┘
                             │
                    User Submits Payment
                             │
                             ↓
┌────────────────────────────────────────────────────────────────┐
│                      BACKEND (Spring Boot)                     │
│              REST API - http://8080/api/v1                     │
│                                                                │
│  POST /api/v1/transactions                                    │
│  {                                                             │
│    "transaction_id": "TXN001",                                │
│    "amount": 5000.00,                                         │
│    "merchant_id": "SHOP001",                                  │
│    "user_id": "USER123",                                      │
│    "transaction_type": "online_purchase"                      │
│  }                                                             │
│                                                                │
│  ✓ Validates input                                            │
│  ✓ Checks user account                                        │
│  ✓ Calls AI Service for fraud check                           │
└──────────────────────┬───────────────────────────────────────┘
                       │
              HTTP POST Request
                       │
                       ↓
┌────────────────────────────────────────────────────────────────┐
│           AI SERVICE (FastAPI + Python)                       │
│          Fraud Detection - http://8001                        │
│                                                                │
│  POST /api/v1/fraud/check                                    │
│  Receives transaction details                                │
│                                                                │
│  Analysis:                                                     │
│  - Amount check: $5000 > $100k? NO (+0.0)                    │
│  - Type check: international? NO (+0.0)                       │
│  - Random factor: +0.15                                       │
│  - Final Score: 0.15                                          │
│                                                                │
│  Response:                                                     │
│  {                                                             │
│    "fraud_score": 0.15,      # 15% risk                      │
│    "is_fraudulent": false,   # Allowed                        │
│    "risk_level": "LOW"       # Safe transaction              │
│  }                                                             │
└──────────────────────┬───────────────────────────────────────┘
                       │
              HTTP Response (JSON)
                       │
                       ↓
┌────────────────────────────────────────────────────────────────┐
│                      BACKEND (Continued)                      │
│                                                                │
│  Receives fraud check response                                │
│  Decision: is_fraudulent = false → ALLOW ✅                  │
│                                                                │
│  Saves to MySQL:                                              │
│  INSERT INTO transactions (id, user_id, amount, status...)   │
│                                                                │
│  Returns to Frontend:                                         │
│  {                                                             │
│    "transaction_id": "TXN001",                               │
│    "status": "SUCCESS",                                       │
│    "message": "Payment processed successfully"               │
│  }                                                             │
└──────────────────────┬───────────────────────────────────────┘
                       │
              HTTP Response (JSON)
                       │
                       ↓
┌────────────────────────────────────────────────────────────────┐
│                    FRONTEND (React)                            │
│                                                                │
│  Shows Result:                                                 │
│  "Payment Successful! ✅"                                      │
│                                                                │
│  Updates Dashboard with:                                      │
│  - Transaction ID: TXN001                                     │
│  - Status: SUCCESS                                            │
│  - Amount: $5000                                              │
│  - Fraud Score: 0.15 (LOW RISK)                              │
└────────────────────────────────────────────────────────────────┘
```

---

## ✅ Prerequisites

Before you start, make sure you have installed:

### Option 1: Docker Setup (Recommended)
- **Docker Desktop** (v20.x or higher)
- **Docker Compose** (v2.x or higher)
- **Git**

### Option 2: Manual Setup
- **Node.js** (v18 LTS)
- **Java** (v21)
- **Python** (v3.11)
- **MySQL** (v8.0)
- **Git**

### Verify Installations

```bash
# Check Docker
docker --version
docker-compose --version

# Check Node.js
node --version
npm --version

# Check Java
java -version

# Check Python
python --version
```

---

## 🚀 Quick Start with Docker (Recommended)

### Step 1: Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/PaySecure-Gateway.git
cd PaySecure-Gateway
```

### Step 2: Start All Services with Docker Compose

```bash
# Start all containers in background
docker-compose up -d

# Wait for MySQL to be healthy (15-20 seconds)
# You'll see: "Container paysecure-mysql Healthy"
```

### Step 3: Verify All Containers Running

```bash
docker-compose ps
```

**Expected Output:**
```
NAME                    IMAGE                              STATUS
paysecure-frontend      paysecure-gateway-frontend:latest  Up
paysecure-backend       paysecure-gateway-backend:latest   Up
paysecure-ai            paysecure-gateway-ai-service:la... Up
paysecure-mysql         mysql:8.0                          Healthy
```

### Step 4: Access Services

Open your browser and visit:

| Service | URL | Purpose |
|---------|-----|---------|
| **Frontend Dashboard** | http://localhost:3000 | Main UI |
| **Backend API Docs** | http://localhost:8080/api/v1/swagger-ui.html | API Documentation |
| **AI Service Docs** | http://localhost:8001/docs | Fraud Detection API |
| **Backend Health** | http://localhost:8080/api/v1/actuator/health | Backend Status |
| **AI Health** | http://localhost:8001/health | AI Service Status |

### Step 5: View Real-time Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f ai-service
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### Step 6: Stop Services

```bash
docker-compose down
```

---

## 🔧 Manual Setup (Without Docker)

If you prefer to run services individually without Docker:

### Step 1: Start MySQL

```bash
# Start MySQL service (Windows)
net start MySQL80

# Connect to MySQL
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql" -u paysecure -p
# Password: MySql@123
```

### Step 2: Start Backend

```bash
cd backend

# First time setup
mvnw.cmd clean install

# Start the service
mvnw.cmd spring-boot:run

# Backend will start at http://localhost:8080
# You should see: "Started PaySecureApplication in X seconds"
```

### Step 3: Start AI Service (New Terminal)

```bash
cd ai-service

# Install Python dependencies
pip install -r requirements.txt

# Start the service
python -m uvicorn app.main:app --host 0.0.0.0 --port 8001 --reload

# AI Service will start at http://localhost:8001
# You should see: "Uvicorn running on http://0.0.0.0:8001"
```

### Step 4: Start Frontend (New Terminal)

```bash
cd frontend

# Install Node dependencies
npm install

# Start development server
npm run dev

# Frontend will start at http://localhost:5173 or http://localhost:3000
```

### Access Services
- Frontend: http://localhost:5173 (or check terminal output)
- Backend Swagger: http://localhost:8080/api/v1/swagger-ui.html
- AI Service Swagger: http://localhost:8001/docs

---

## 📁 Project Structure

```
PaySecure-Gateway/
│
├── frontend/                          # React Application
│   ├── src/
│   │   ├── App.tsx                   # Main component
│   │   ├── App.css                   # Styles
│   │   └── main.tsx                  # Entry point
│   ├── package.json                  # Node dependencies
│   ├── Dockerfile                    # Docker build
│   └── vite.config.ts                # Vite configuration
│
├── backend/                           # Spring Boot Application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/paysecure/
│   │   │   │   ├── PaySecureGatewayApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── CorsConfig.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── controller/       # REST Controllers
│   │   │   │   ├── service/          # Business Logic
│   │   │   │   ├── repository/       # Database Access
│   │   │   │   └── model/            # Entity Classes
│   │   │   └── resources/
│   │   │       └── application.yml   # Configuration
│   │   └── test/                     # Unit Tests
│   ├── pom.xml                       # Maven dependencies
│   ├── mvnw.cmd                      # Maven wrapper (Windows)
│   ├── Dockerfile                    # Docker build
│   └── target/                       # Build output
│
├── ai-service/                        # FastAPI Application
│   ├── app/
│   │   ├── main.py                  # FastAPI app
│   │   ├── __init__.py              # Python package
│   │   └── services/                # Business logic
│   ├── requirements.txt             # Python dependencies
│   ├── Dockerfile                   # Docker build
│   └── venv/                        # Virtual environment
│
├── docker-compose.yml               # Docker orchestration
├── README.md                        # This file
├── .gitignore                       # Git ignore rules
└── LICENSE                          # MIT License
```

---

## 📚 API Documentation

### Backend API (Spring Boot)

#### Get Health Status
```bash
GET http://localhost:8080/api/v1/actuator/health

Response:
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {...}
    }
  }
}
```

#### Access Swagger UI
```
http://localhost:8080/api/v1/swagger-ui.html
```
Here you can test all API endpoints interactively!

### AI Service API (FastAPI)

#### Get Health Status
```bash
GET http://localhost:8001/health

Response:
{
  "status": "healthy",
  "timestamp": "2025-11-13T12:00:00.000000",
  "service": "fraud-detection"
}
```

#### Check Transaction for Fraud
```bash
POST http://localhost:8001/api/v1/fraud/check
Content-Type: application/json

Request:
{
  "transaction_id": "TXN001",
  "amount": 5000.50,
  "merchant_id": "SHOP001",
  "user_id": "USER456",
  "transaction_type": "online_purchase"
}

Response:
{
  "transaction_id": "TXN001",
  "fraud_score": 0.35,
  "is_fraudulent": false,
  "risk_level": "LOW",
  "details": "Transaction analyzed. Risk: LOW"
}
```

#### Access Swagger UI
```
http://localhost:8001/docs
```

---

## ✨ Features

### Current Features ✅

- ✅ **Real-time Fraud Detection** - Instant risk analysis for transactions
- ✅ **Multi-tier Architecture** - Frontend, Backend, AI Service separation
- ✅ **RESTful APIs** - Clean, documented API endpoints
- ✅ **Swagger Documentation** - Interactive API testing
- ✅ **Docker Containerization** - Easy deployment and scaling
- ✅ **CORS Enabled** - Frontend can communicate with backend
- ✅ **Health Checks** - Service status monitoring
- ✅ **Database Integration** - MySQL persistent storage
- ✅ **Production Ready** - Security, error handling, logging

### Planned Features 🔮

- [ ] User Authentication (JWT)
- [ ] Payment Gateway Integration (Stripe, PayPal)
- [ ] Advanced ML Models for fraud detection
- [ ] Transaction Analytics Dashboard
- [ ] Email Notifications
- [ ] Admin Panel
- [ ] Rate Limiting
- [ ] Webhook Notifications

---

## 🐛 Troubleshooting

### Port Already in Use

**Problem:** Port 3000, 8080, 8001, or 3306 already in use

**Solution:**
```bash
# Find process using port (example for 8080)
netstat -ano | findstr :8080

# Kill process (replace PID with actual number)
taskkill /PID 12345 /F

# Or stop services
docker-compose down
```

### MySQL Container Won't Start

**Problem:** MySQL container fails to become healthy

**Solution:**
```bash
# Check logs
docker-compose logs mysql

# Restart containers
docker-compose down
docker-compose up -d

# Wait 15-20 seconds for MySQL to be ready
```

### Backend Can't Connect to MySQL

**Problem:** "Failed to determine a suitable driver class"

**Solution:**
```bash
# Verify application.yml has correct credentials
# Check datasource URL in backend/src/main/resources/application.yml

# Ensure MySQL container is healthy
docker-compose ps mysql

# Verify MySQL is accepting connections
docker-compose exec mysql mysql -u paysecure -pMySql@123 -e "SELECT 1"
```

### Frontend Can't Reach Backend

**Problem:** "Backend Offline" shown in frontend

**Solution:**
```bash
# Check backend is running
curl http://localhost:8080/api/v1/actuator/health

# Check CORS configuration
# Look for CorsConfig.java in backend

# Verify network
docker-compose ps
docker network ls
```

### AI Service Not Responding

**Problem:** "AI Service Offline" shown in frontend

**Solution:**
```bash
# Check AI service is running
curl http://localhost:8001/health

# View logs
docker-compose logs ai-service

# Restart AI service
docker-compose restart ai-service
```

---

## 🚀 Next Steps & Enhancements

### Short-term (Easy Wins)

1. **Add Transaction Endpoints**
   - Create transaction API
   - Store transactions in MySQL
   - Retrieve transaction history

2. **Enhance Fraud Detection**
   - Add more rules to fraud detection
   - Implement real ML model
   - Add pattern recognition

3. **Improve Frontend**
   - Add transaction form
   - Display transaction history
   - Show fraud analysis details

### Medium-term (Intermediate)

1. **User Management**
   - User registration
   - User authentication (JWT)
   - User profiles

2. **Payment Processing**
   - Integrate payment gateway
   - Handle payment webhooks
   - Track payment status

3. **Analytics**
   - Transaction statistics
   - Fraud patterns
   - Risk distribution charts

### Long-term (Advanced)

1. **Production Deployment**
   - Deploy to AWS/Azure/GCP
   - Set up CI/CD pipeline
   - Configure monitoring and alerts

2. **Advanced Features**
   - Machine learning model training
   - Real-time notification system
   - API rate limiting
   - Audit logging

---

## 💼 Interview Talking Points

Use these points to explain the project in interviews:

### Technical Architecture
> "PaySecure Gateway is a microservices-based payment processing system using React frontend, Spring Boot backend, and Python FastAPI for fraud detection. It's fully containerized with Docker for consistent deployment."

### Technology Choices
> "I chose Spring Boot for enterprise-level reliability, React for interactive UI, and FastAPI for rapid AI service development. Docker ensures the entire stack runs consistently across different environments."

### Real-time Processing
> "Every transaction triggers real-time fraud detection through the AI service, which analyzes transaction patterns and returns a fraud score between 0 and 1. The backend makes blocking decisions instantly."

### Microservices Benefits
> "By separating services, each component can be scaled independently. If fraud detection needs more computing power, I can scale just the AI service without affecting payment processing."

### CORS & Security
> "I implemented CORS configuration and Spring Security to ensure safe cross-origin communication. All endpoints are properly validated and authenticated."

### Database Design
> "MySQL with JPA/Hibernate provides ACID compliance for critical transaction data. Docker volumes ensure data persists even if containers restart."

### DevOps & Containerization
> "Docker Compose orchestrates all four services (Frontend, Backend, AI, MySQL) with proper networking and health checks. The entire stack starts with a single command: `docker-compose up -d`."

---

## 📖 Learning Resources

- **Docker**: https://docs.docker.com/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **React**: https://react.dev
- **FastAPI**: https://fastapi.tiangolo.com
- **MySQL**: https://dev.mysql.com/doc/

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📧 Contact & Support

For questions or support:

- **GitHub Issues**: [Create an issue](https://github.com/YOUR_USERNAME/PaySecure-Gateway/issues)
- **Email**: your.email@example.com
- **LinkedIn**: [Your Profile](https://linkedin.com/in/your_profile)

---

## 🙏 Acknowledgments

- Spring Boot community
- FastAPI community
- React community
- Docker documentation

---

Made with ❤️ by King Gaurav

Last Updated: November 13, 2025
