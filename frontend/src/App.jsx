import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
    import Login from './pages/Login';
    import Dashboard from './pages/Dashboard';
    import { ToastContainer } from 'react-toastify';
    import 'react-toastify/dist/ReactToastify.css';

    function App() {
      return (
        <Router>
          <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} />
          <Routes>
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/login" element={<Login />} />
            <Route path="/dashboard" element={<Dashboard />} />
          </Routes>
        </Router>
      );
    }

    export default App;