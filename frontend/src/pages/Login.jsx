import { useState } from 'react';
import api from '../api/axiosConfig';
import { useNavigate, Link } from 'react-router-dom'; // Added Link
import { toast } from 'react-toastify';
import { LockKeyhole, User, LogIn } from 'lucide-react'; // Added LogIn icon

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            // Call Auth Service via Gateway
            const response = await api.post('/api/v1/auth/login', { username, password });
            
            if (response.data && response.data.token) {
                const { token } = response.data;
                
                // Store session data
                localStorage.setItem('token', token);
                localStorage.setItem('user', username);
                
                toast.success(`Welcome back, ${username}!`);
                navigate('/dashboard');
            } else {
                 toast.error("Login failed: No token received");
            }

        } catch (error) {
            console.error("Login Error:", error);
            toast.error(error.response?.data?.message || "Invalid Credentials");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center h-screen bg-gradient-to-br from-blue-900 to-slate-800">
            <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-xl shadow-2xl transform transition-all hover:scale-[1.01]">
                <div className="text-center">
                    <h1 className="text-3xl font-extrabold text-blue-900 tracking-tight">PaySecure</h1>
                    <p className="text-sm text-gray-500 mt-2">Enterprise Payment Gateway</p>
                </div>
                
                <form onSubmit={handleLogin} className="space-y-5">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <User className="h-5 w-5 text-gray-400" />
                            </div>
                            <input 
                                type="text" 
                                className="w-full pl-10 p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
                                placeholder="Enter your username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                            />
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                        <div className="relative">
                            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <LockKeyhole className="h-5 w-5 text-gray-400" />
                            </div>
                            <input 
                                type="password" 
                                className="w-full pl-10 p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-colors"
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                    </div>
                    
                    <button 
                        type="submit" 
                        disabled={loading}
                        className={`w-full py-3 text-white font-bold rounded-lg shadow-md transition-all flex justify-center items-center ${
                            loading 
                            ? 'bg-blue-400 cursor-not-allowed' 
                            : 'bg-blue-600 hover:bg-blue-700 hover:shadow-lg active:scale-[0.98]'
                        }`}
                    >
                        {loading ? (
                            <span className="flex items-center">
                                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Signing In...
                            </span>
                        ) : (
                            <>
                                <LogIn className="h-5 w-5 mr-2" /> Sign In
                            </>
                        )}
                    </button>
                </form>

                {/* Link to Signup Page */}
                <div className="text-center text-sm text-gray-600 pt-2 border-t border-gray-100">
                    Don't have an account?{' '}
                    <Link to="/signup" className="font-semibold text-blue-600 hover:text-blue-500 hover:underline">
                        Sign up now
                    </Link>
                </div>

                <div className="text-center text-xs text-gray-400 mt-4">
                    &copy; 2025 PaySecure Inc.
                </div>
            </div>
        </div>
    );
};

export default Login;