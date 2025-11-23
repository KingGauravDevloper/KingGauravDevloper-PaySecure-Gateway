from fastapi import FastAPI
from pydantic import BaseModel
import random
import numpy as np
from sklearn.ensemble import IsolationForest
from datetime import datetime

app = FastAPI()

# --- 1. UTILITY ENDPOINTS ---
@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "service": "fraud-detection-ai",
        "timestamp": datetime.now().isoformat()
    }

@app.get("/")
async def root():
    return {"message": "PaySecure AI Engine is Running 🚀"}

# --- 2. THE AI BRAIN SETUP ---
print("🧠 Training Fraud Detection Model...")

# Generate dummy "normal" transaction data (Amount)
# Most transactions are between $10 and $500
X_train = 100 * np.random.randn(1000, 1) + 50 
X_train = np.abs(X_train) # No negative money

# Train Isolation Forest (Standard Anomaly Detection Algorithm)
clf = IsolationForest(random_state=42, contamination=0.05) # 5% are anomalies
clf.fit(X_train)
print("✅ Model Trained and Ready!")

# --- 3. DATA MODELS ---

# INPUT: Must match Java's JSON format (camelCase)
class TransactionRequest(BaseModel):
    transactionId: str 
    userId: str
    merchantId: str
    amount: float
    # Java might not send these, so we provide defaults
    transactionType: str = "domestic" 
    ipAddress: str = "127.0.0.1" 

# OUTPUT: Must match Java's @JsonProperty expectations (snake_case)
class FraudCheckResponse(BaseModel):
    transaction_id: str
    fraud_score: float
    is_fraudulent: bool
    risk_level: str
    details: str

# --- 4. THE CORE LOGIC ---
@app.post("/api/v1/fraud/check", response_model=FraudCheckResponse)
async def check_fraud(transaction: TransactionRequest):
    reasons = []
    fraud_score = 0.0
    
    # Note: accessing fields using camelCase (.amount, .transactionId)
    
    # --- LAYER 1: RULE BASED ENGINE (Hard Rules) ---
    if transaction.amount > 500000:
        return FraudCheckResponse(
            transaction_id=transaction.transactionId,
            fraud_score=1.0,
            is_fraudulent=True,
            risk_level="CRITICAL",
            details="Amount exceeds global limit"
        )

    # --- LAYER 2: ML ANOMALY DETECTION ---
    # Predict using the Isolation Forest Model
    prediction = clf.predict([[transaction.amount]])[0]
    
    # Calculate an anomaly score
    raw_score = clf.decision_function([[transaction.amount]])[0]
    
    # Normalize score to 0-1 range
    normalized_score = 0.5 - (raw_score * 2) 
    normalized_score = max(0.0, min(1.0, normalized_score))

    if prediction == -1:
        reasons.append("Anomaly detected by ML Model")
        fraud_score += 0.4
    
    # --- LAYER 3: BEHAVIORAL CHECKS ---
    if transaction.transactionType == "international":
        fraud_score += 0.25
        reasons.append("International Transaction Risk")
        
    # Combine ML score + Rule Score
    final_score = (normalized_score * 0.6) + (fraud_score * 0.4)
    
    # --- FINAL VERDICT ---
    is_fraudulent = final_score > 0.7
    
    if final_score > 0.8:
        risk_level = "CRITICAL"
    elif final_score > 0.6:
        risk_level = "HIGH"
    elif final_score > 0.3:
        risk_level = "MEDIUM"
    else:
        risk_level = "LOW"

    if not reasons:
        reasons.append("Pattern matches normal user behavior")

    return FraudCheckResponse(
        transaction_id=transaction.transactionId,
        fraud_score=round(final_score, 2),
        is_fraudulent=is_fraudulent,
        risk_level=risk_level,
        details=", ".join(reasons)
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)