import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [backendStatus, setBackendStatus] = useState('Checking...')
  const [aiStatus, setAiStatus] = useState('Checking...')

  useEffect(() => {
    checkStatuses()
  }, [])

  const checkStatuses = async () => {
    try {
      // Check Backend
      const backendRes = await axios.get('http://localhost:8080/api/v1/actuator/health')
      setBackendStatus(backendRes.data.status)
    } catch {
      setBackendStatus('Offline')
    }

    try {
      // Check AI Service
      const aiRes = await axios.get('http://localhost:8001/health')
      setAiStatus(aiRes.data.status)
    } catch {
      setAiStatus('Offline')
    }
  }

  return (
    <div className='App'>
      <header>
        <h1>💳 PaySecure Gateway</h1>
        <p>Real-time Payment Processing with AI Fraud Detection</p>
      </header>

      <div className='status-container'>
        <div className='status-card'>
          <h3>Backend Status</h3>
          <p className={backendStatus === 'UP' ? 'status-up' : 'status-down'}>
            {backendStatus}
          </p>
        </div>

        <div className='status-card'>
          <h3>AI Service Status</h3>
          <p className={aiStatus === 'healthy' ? 'status-up' : 'status-down'}>
            {aiStatus}
          </p>
        </div>
      </div>

      <div className='links-container'>
        <h2>📚 Documentation & Tools</h2>
        <a href="http://localhost:8080/api/v1/swagger-ui.html" target="_blank" rel="noreferrer" className='link-btn'>
          📖 Backend API Docs (Swagger)
        </a>
        <a href="http://localhost:8001/docs" target="_blank" rel="noreferrer" className='link-btn'>
          🤖 AI Service Docs (Swagger)
        </a>
        <a href="http://localhost:8080/api/v1/actuator/health" target="_blank" rel="noreferrer" className='link-btn'>
          ❤️ Backend Health Check
        </a>
        <a href="http://localhost:8001/health" target="_blank" rel="noreferrer" className='link-btn'>
          💪 AI Service Health Check
        </a>
      </div>

      <div className='info-container'>
        <h2>🏗️ Your Stack</h2>
        <ul>
          <li>✅ <strong>Backend:</strong> Spring Boot @ localhost:8080</li>
          <li>✅ <strong>Frontend:</strong> React @ localhost:3000</li>
          <li>✅ <strong>AI Service:</strong> FastAPI @ localhost:8001</li>
          <li>✅ <strong>Database:</strong> MySQL @ localhost:3306</li>
        </ul>
      </div>

      <button onClick={checkStatuses} className='refresh-btn'>
        🔄 Refresh Status
      </button>
    </div>
  )
}

export default App
