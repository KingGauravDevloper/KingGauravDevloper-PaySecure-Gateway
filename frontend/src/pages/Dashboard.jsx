import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import { toast } from 'react-toastify';
import { CreditCard, Activity, ShieldCheck, LogOut, DollarSign, Download, AlertTriangle, CheckCircle, XCircle, Trash2, FileText } from 'lucide-react';
import jsPDF from 'jspdf';

const Dashboard = () => {
    const [amount, setAmount] = useState(100);
    const [currency, setCurrency] = useState('USD');
    const [merchantId, setMerchantId] = useState('amazon_prime');
    const [status, setStatus] = useState(null);
    const [loading, setLoading] = useState(false);
    const [txnDetails, setTxnDetails] = useState(null);
    
    const username = localStorage.getItem('user') || "User";

    // --- 1. Persistent History Logic ---
    // Load history from browser storage on startup
    const [history, setHistory] = useState(() => {
        const saved = localStorage.getItem(`history_${username}`);
        return saved ? JSON.parse(saved) : [];
    });

    // Save history whenever it updates
    useEffect(() => {
        localStorage.setItem(`history_${username}`, JSON.stringify(history));
    }, [history, username]);

    const handlePayment = async (e) => {
        e.preventDefault();
        if (loading) return;
        
        setLoading(true);
        setStatus(null);
        setTxnDetails(null);

        const payload = {
            userId: username,
            merchantId: merchantId,
            amount: parseFloat(amount),
            currency: currency,
            paymentMethod: "CREDIT_CARD",
            transactionType: "PAYMENT"
        };

        try {
            const response = await api.post('/api/payments', payload);
            const data = response.data;
            
            setTxnDetails(data);
            
            // Create a history entry with a readable timestamp
            const newTxn = { 
                ...data, 
                amount: parseFloat(amount), 
                currency: currency,
                timestamp: new Date().toLocaleString() 
            };
            
            // Add to top of list
            setHistory(prev => [newTxn, ...prev]);
            
            if (data.status === 'SUCCESS') {
                setStatus('SUCCESS');
                toast.success(`Payment Approved! ID: ${data.transactionId.substring(0,8)}...`);
            } else {
                setStatus('FAILED');
                toast.error("Payment Declined");
            }
        } catch (error) {
            console.error(error);
            setStatus('ERROR');
            toast.error(error.response?.data?.message || "Transaction Failed");
        } finally {
            setLoading(false);
        }
    };

    const clearHistory = () => {
        if(confirm("Clear your entire transaction history?")) {
            setHistory([]);
            toast.info("History cleared");
        }
    };

    // --- Helper Functions ---
    const getRiskColor = (score) => {
        if (!score && score !== 0) return "text-gray-500";
        if (score > 0.7) return "text-red-600";
        if (score > 0.4) return "text-yellow-600";
        return "text-green-600";
    };

    const parseReason = (jsonString) => {
        try {
            if(!jsonString) return "Unknown";
            // The backend sends a JSON string inside the responseData field
            const obj = JSON.parse(jsonString);
            return obj.details || obj.reason || "Bank Declined";
        } catch(e) {
            return jsonString || "Processing Error";
        }
    };

    // --- Feature: PDF Receipt ---
    const downloadReceipt = (txn = txnDetails) => {
        if (!txn) return;
        const doc = new jsPDF();
        
        // Header
        doc.setFontSize(22);
        doc.setTextColor(0, 51, 102);
        doc.text("PaySecure Receipt", 20, 20);
        
        // Divider
        doc.setLineWidth(0.5);
        doc.line(20, 25, 190, 25);
        
        // Details
        doc.setFontSize(12);
        doc.setTextColor(0, 0, 0);
        doc.text(`Date: ${txn.timestamp || new Date().toLocaleString()}`, 20, 40);
        doc.text(`Transaction ID: ${txn.transactionId}`, 20, 50);
        doc.text(`Merchant: ${txn.merchantId || merchantId}`, 20, 60);
        doc.text(`Payment Method: CREDIT_CARD`, 20, 70);
        
        // Status
        doc.setFontSize(14);
        if (txn.status === 'SUCCESS') {
            doc.setTextColor(0, 128, 0);
            doc.text("STATUS: APPROVED", 20, 90);
        } else {
            doc.setTextColor(200, 0, 0);
            doc.text("STATUS: DECLINED", 20, 90);
        }

        // Amount Box
        doc.setFillColor(240, 240, 240);
        doc.rect(20, 100, 170, 30, 'F');
        doc.setFontSize(16);
        doc.setTextColor(0, 0, 0);
        doc.text(`Total Amount: ${txn.amount} ${txn.currency || currency}`, 30, 120);

        doc.save(`receipt_${txn.transactionId.substring(0,8)}.pdf`);
        toast.info("Receipt downloaded");
    };

    // --- Feature: Visual Risk Gauge ---
    const RiskBar = ({ score }) => {
        const percentage = Math.min(Math.max((score || 0) * 100, 0), 100);
        let colorClass = "bg-green-500";
        if (score > 0.4) colorClass = "bg-yellow-500";
        if (score > 0.7) colorClass = "bg-red-600";

        return (
            <div className="w-full mt-3">
                <div className="flex justify-between text-xs mb-1 font-mono">
                    <span className="text-gray-500 font-medium">AI THREAT ANALYSIS</span>
                    <span className={`font-bold ${getRiskColor(score)}`}>{Math.round(percentage)}/100</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden border border-gray-100">
                    <div 
                        className={`h-full rounded-full transition-all duration-1000 ease-out ${colorClass}`} 
                        style={{ width: `${percentage}%` }}
                    ></div>
                </div>
            </div>
        );
    };

    return (
        <div className="min-h-screen bg-gray-50 font-sans pb-12">
            {/* Navbar */}
            <nav className="bg-white shadow-sm sticky top-0 z-50">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex items-center">
                            <div className="bg-blue-600 p-1.5 rounded-lg mr-3 shadow-md">
                                <ShieldCheck className="h-6 w-6 text-white" />
                            </div>
                            <span className="text-xl font-bold text-gray-800 tracking-tight">PaySecure</span>
                        </div>
                        <div className="flex items-center space-x-6">
                            <div className="hidden md:flex flex-col text-right">
                                <span className="text-xs text-gray-400 uppercase font-semibold tracking-wider">Logged in as</span>
                                <span className="text-sm font-bold text-gray-800">{username}</span>
                            </div>
                            <button 
                                onClick={() => {
                                    localStorage.removeItem('token');
                                    localStorage.removeItem('user');
                                    window.location.href='/'
                                }} 
                                className="flex items-center text-gray-500 hover:text-red-600 transition-colors font-medium px-3 py-2 rounded-lg hover:bg-red-50"
                            >
                                <LogOut className="h-5 w-5 mr-2" />
                                <span className="hidden sm:inline">Logout</span>
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
                <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
                    
                    {/* Left Column: Payment Form (5 cols) */}
                    <div className="lg:col-span-5 space-y-6">
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                            <div className="bg-gradient-to-r from-blue-600 to-blue-700 px-6 py-5">
                                <h2 className="text-white text-lg font-bold flex items-center">
                                    <CreditCard className="mr-2 h-5 w-5 opacity-80" /> New Transaction
                                </h2>
                            </div>
                            <div className="p-6">
                                <form onSubmit={handlePayment} className="space-y-6">
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Merchant</label>
                                        <div className="relative">
                                            <select 
                                                value={merchantId}
                                                onChange={(e) => setMerchantId(e.target.value)}
                                                className="block w-full pl-3 pr-10 py-3 text-base border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-xl border bg-gray-50 transition-shadow"
                                            >
                                                <option value="amazon_prime">Amazon Prime</option>
                                                <option value="netflix">Netflix</option>
                                                <option value="darkweb_store">DarkWeb Store (High Risk)</option>
                                                <option value="apple_store">Apple Store</option>
                                                <option value="crypto_exchange">Crypto Exchange (Med Risk)</option>
                                            </select>
                                        </div>
                                    </div>

                                    {/* Feature: Quick Pay Presets */}
                                    <div>
                                        <label className="block text-sm font-semibold text-gray-700 mb-2">Amount</label>
                                        <div className="flex flex-wrap gap-2 mb-3">
                                            {[10, 100, 5000, 25000, 600000].map(val => (
                                                <button 
                                                    key={val}
                                                    type="button"
                                                    onClick={() => setAmount(val)}
                                                    className={`px-3 py-1 text-xs font-semibold rounded-full transition-all active:scale-95 ${
                                                        amount === val 
                                                        ? 'bg-blue-600 text-white shadow-md' 
                                                        : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                                                    }`}
                                                >
                                                    ${val.toLocaleString()}
                                                </button>
                                            ))}
                                        </div>
                                        
                                        <div className="flex rounded-xl shadow-sm">
                                            <div className="relative flex-grow focus-within:z-10">
                                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                                    <span className="text-gray-500 sm:text-sm font-bold">$</span>
                                                </div>
                                                <input
                                                    type="number"
                                                    value={amount}
                                                    onChange={(e) => setAmount(e.target.value)}
                                                    className="focus:ring-blue-500 focus:border-blue-500 block w-full rounded-none rounded-l-xl pl-7 sm:text-sm border-gray-300 border py-3 bg-gray-50 font-mono text-lg"
                                                    placeholder="0.00"
                                                />
                                            </div>
                                            <select
                                                value={currency}
                                                onChange={(e) => setCurrency(e.target.value)}
                                                className="-ml-px block w-24 pl-3 pr-7 py-3 text-base border-gray-300 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm rounded-r-xl border bg-gray-100 font-medium text-gray-700"
                                            >
                                                <option>USD</option>
                                                <option>EUR</option>
                                                <option>INR</option>
                                            </select>
                                        </div>
                                    </div>

                                    <button
                                        type="submit"
                                        disabled={loading}
                                        className={`w-full flex justify-center py-3.5 px-4 border border-transparent rounded-xl shadow-lg text-sm font-bold text-white transition-all transform active:scale-[0.98] ${
                                            loading 
                                            ? 'bg-gray-400 cursor-not-allowed shadow-none' 
                                            : 'bg-blue-600 hover:bg-blue-700 hover:shadow-xl hover:-translate-y-0.5'
                                        }`}
                                    >
                                        {loading ? (
                                            <span className="flex items-center">
                                                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                                </svg>
                                                Processing...
                                            </span>
                                        ) : 'Pay Now'}
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>

                    {/* Right Column: Status & History (7 cols) */}
                    <div className="lg:col-span-7 space-y-8">
                        
                        {/* Live Status Card */}
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden min-h-[280px] flex flex-col">
                            <div className="bg-slate-900 px-6 py-5 flex justify-between items-center">
                                <h2 className="text-white text-lg font-bold flex items-center">
                                    <Activity className="mr-2 h-5 w-5 text-blue-400" /> Live Analysis
                                </h2>
                                {/* Receipt Button appears on Success */}
                                {status === 'SUCCESS' && (
                                    <button 
                                        onClick={() => downloadReceipt(txnDetails)}
                                        className="text-xs bg-blue-500/20 hover:bg-blue-500/30 text-blue-300 px-3 py-1.5 rounded-lg flex items-center transition-colors border border-blue-500/30"
                                    >
                                        <Download className="h-3 w-3 mr-1" /> Download Receipt
                                    </button>
                                )}
                            </div>
                            
                            <div className="p-8 flex-grow flex flex-col justify-center items-center relative">
                                {/* Initial State */}
                                {!status && !loading && (
                                    <div className="text-center text-gray-400 animate-fade-in">
                                        <div className="bg-gray-50 p-4 rounded-full inline-block mb-4">
                                            <DollarSign className="h-12 w-12 text-gray-300" />
                                        </div>
                                        <p className="font-medium text-gray-500">System Ready</p>
                                        <p className="text-sm text-gray-400 mt-1">Secure Connection Established</p>
                                    </div>
                                )}

                                {/* Feature: Loading Skeleton */}
                                {loading && (
                                    <div className="w-full max-w-sm animate-pulse space-y-4">
                                        <div className="flex justify-center">
                                            <div className="h-20 w-20 bg-gray-200 rounded-full"></div>
                                        </div>
                                        <div className="h-8 bg-gray-200 rounded w-3/4 mx-auto"></div>
                                        <div className="h-4 bg-gray-200 rounded w-1/2 mx-auto"></div>
                                        <div className="pt-8 space-y-3">
                                            <div className="h-2 bg-gray-200 rounded w-full"></div>
                                            <div className="h-2 bg-gray-200 rounded w-full"></div>
                                        </div>
                                        <p className="text-center text-xs text-gray-400 pt-2 font-mono">AI RISK ENGINE ANALYZING...</p>
                                    </div>
                                )}

                                {/* Success State */}
                                {status === 'SUCCESS' && (
                                    <div className="w-full animate-fade-in-up">
                                        <div className="flex flex-col items-center text-center mb-8">
                                            <div className="h-20 w-20 bg-green-100 rounded-full flex items-center justify-center mb-4 shadow-sm ring-4 ring-green-50">
                                                <CheckCircle className="h-10 w-10 text-green-600" />
                                            </div>
                                            <h3 className="text-2xl font-extrabold text-gray-900">Payment Successful</h3>
                                            <p className="text-gray-500 font-mono text-xs mt-2 bg-gray-100 px-2 py-1 rounded">ID: {txnDetails?.transactionId}</p>
                                        </div>
                                        
                                        <div className="bg-green-50/50 rounded-xl p-5 border border-green-100">
                                            <div className="grid grid-cols-2 gap-6">
                                                <div>
                                                    <p className="text-xs font-bold text-gray-400 uppercase tracking-wide">Provider</p>
                                                    <p className="text-lg font-bold text-gray-700 mt-1">{txnDetails?.provider}</p>
                                                </div>
                                                <div>
                                                    <RiskBar score={txnDetails?.fraudScore} />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                )}

                                {/* Failed State */}
                                {status === 'FAILED' && (
                                    <div className="w-full animate-fade-in-up">
                                        <div className="flex flex-col items-center text-center mb-8">
                                            <div className="h-20 w-20 bg-red-100 rounded-full flex items-center justify-center mb-4 shadow-sm ring-4 ring-red-50">
                                                <XCircle className="h-10 w-10 text-red-600" />
                                            </div>
                                            <h3 className="text-2xl font-extrabold text-gray-900">Transaction Declined</h3>
                                            <div className="mt-3 inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800 border border-red-200">
                                                <AlertTriangle className="w-3 h-3 mr-1.5" />
                                                {parseReason(txnDetails?.responseData)}
                                            </div>
                                        </div>
                                        
                                        <div className="bg-red-50/50 rounded-xl p-5 border border-red-100">
                                            <RiskBar score={txnDetails?.fraudScore} />
                                            {txnDetails?.fraudScore > 0.7 && (
                                                <p className="text-xs text-red-600 mt-3 text-center font-medium flex justify-center items-center">
                                                    <ShieldCheck className="h-3 w-3 mr-1" /> High Fraud Probability Detected
                                                </p>
                                            )}
                                        </div>
                                    </div>
                                )}

                                {/* Error State */}
                                {status === 'ERROR' && (
                                    <div className="text-center text-red-500">
                                        <AlertTriangle className="h-12 w-12 mx-auto mb-3 opacity-50" />
                                        <p className="font-bold text-lg">System Error</p>
                                        <p className="text-sm opacity-80">Unable to reach Payment Gateway.</p>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Feature: History Table */}
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                            <div className="bg-white border-b border-gray-100 px-6 py-4 flex justify-between items-center">
                                <h3 className="text-gray-800 font-bold flex items-center">
                                    Recent Transactions
                                    <span className="ml-2 bg-gray-100 text-gray-600 py-0.5 px-2 rounded-full text-[10px] uppercase tracking-wide font-bold">
                                        Session
                                    </span>
                                </h3>
                                {history.length > 0 && (
                                    <button 
                                        onClick={clearHistory} 
                                        className="text-gray-400 hover:text-red-500 transition p-2 hover:bg-red-50 rounded-lg"
                                        title="Clear History"
                                    >
                                        <Trash2 className="h-4 w-4" />
                                    </button>
                                )}
                            </div>
                            <div className="overflow-x-auto max-h-[300px] scrollbar-hide">
                                <table className="w-full text-left">
                                    <thead className="bg-gray-50 text-gray-500 uppercase text-xs font-semibold sticky top-0 z-10">
                                        <tr>
                                            <th className="px-6 py-3">Time</th>
                                            <th className="px-6 py-3">ID</th>
                                            <th className="px-6 py-3">Amount</th>
                                            <th className="px-6 py-3">Status</th>
                                            <th className="px-6 py-3 text-right">Risk</th>
                                            <th className="px-6 py-3 text-right">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-gray-100">
                                        {history.map((txn, idx) => (
                                            <tr key={idx} className="hover:bg-blue-50/50 transition-colors group">
                                                <td className="px-6 py-4 text-sm text-gray-500 whitespace-nowrap">
                                                    {txn.timestamp.split(',')[1]}
                                                </td>
                                                <td className="px-6 py-4 text-sm font-mono text-blue-600">
                                                    {txn.transactionId ? txn.transactionId.substring(0, 8) : "---"}...
                                                </td>
                                                <td className="px-6 py-4 text-sm font-bold text-gray-900">
                                                    {txn.amount?.toLocaleString()} <span className="text-gray-400 font-normal text-xs">{txn.currency}</span>
                                                </td>
                                                <td className="px-6 py-4">
                                                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold border ${
                                                        txn.status === 'SUCCESS' 
                                                        ? 'bg-green-50 text-green-700 border-green-200' 
                                                        : 'bg-red-50 text-red-700 border-red-200'
                                                    }`}>
                                                        {txn.status === 'SUCCESS' ? 'Approved' : 'Declined'}
                                                    </span>
                                                </td>
                                                <td className={`px-6 py-4 text-sm font-bold text-right ${getRiskColor(txn.fraudScore)}`}>
                                                    {txn.fraudScore?.toFixed(2)}
                                                </td>
                                                <td className="px-6 py-4 text-right">
                                                     {txn.status === 'SUCCESS' && (
                                                        <button 
                                                            onClick={() => downloadReceipt(txn)}
                                                            className="opacity-0 group-hover:opacity-100 text-gray-400 hover:text-blue-600 transition-all"
                                                            title="Download Receipt"
                                                        >
                                                            <FileText className="h-4 w-4" />
                                                        </button>
                                                     )}
                                                </td>
                                            </tr>
                                        ))}
                                        {history.length === 0 && (
                                            <tr>
                                                <td colSpan="6" className="px-6 py-10 text-center text-gray-400 text-sm italic">
                                                    No transactions recorded yet.
                                                </td>
                                            </tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;