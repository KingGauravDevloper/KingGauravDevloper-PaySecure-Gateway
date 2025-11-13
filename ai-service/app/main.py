from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from datetime import datetime
from pydantic import BaseModel

app = FastAPI(
    title="PaySecure Fraud Detection API",
    version="1.0.0",
    description="AI-powered fraud detection for payment transactions"
)

# Enable CORS for frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Request/Response models
class TransactionRequest(BaseModel):
    transaction_id: str
    amount: float
    merchant_id: str
    user_id: str
    transaction_type: str

class FraudCheckResponse(BaseModel):
    transaction_id: str
    fraud_score: float
    is_fraudulent: bool
    risk_level: str
    details: str

# Health check endpoint
@app.get("/")
async def root():
    return {
        "service": "PaySecure Fraud Detection API",
        "status": "active",
        "version": "1.0.0"
    }

@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "timestamp": datetime.now().isoformat(),
        "service": "fraud-detection"
    }

# Fraud detection endpoint
@app.post("/api/v1/fraud/check", response_model=FraudCheckResponse)
async def check_fraud(transaction: TransactionRequest):
    """
    Check if a transaction is fraudulent based on ML model
    """
    # Simple fraud detection logic (you can replace with real ML model)
    fraud_score = calculate_fraud_score(transaction)
    
    is_fraudulent = fraud_score > 0.7
    risk_level = "HIGH" if fraud_score > 0.7 else "MEDIUM" if fraud_score > 0.4 else "LOW"
    
    return FraudCheckResponse(
        transaction_id=transaction.transaction_id,
        fraud_score=fraud_score,
        is_fraudulent=is_fraudulent,
        risk_level=risk_level,
        details=f"Transaction analyzed. Risk: {risk_level}"
    )

def calculate_fraud_score(transaction: TransactionRequest) -> float:
    """
    Calculate fraud score based on transaction details
    Replace with actual ML model later
    """
    score = 0.0
    
    # Simple rules (replace with ML model)
    if transaction.amount > 100000:
        score += 0.3
    if transaction.transaction_type == "international":
        score += 0.2
    
    # Add some randomness for demo
    import random
    score += random.uniform(0, 0.2)
    
    return min(score, 1.0)  # Cap at 1.0

# Metrics endpoint
@app.get("/metrics")
async def get_metrics():
    return {
        "service": "fraud-detection",
        "uptime": "active",
        "model_version": "1.0.0",
        "accuracy": 0.95
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)
