

import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import EmailIcon from '@mui/icons-material/Email';
import LockIcon from '@mui/icons-material/Lock';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';

export default function Login() {
  const [credentials, setCredentials] = useState({ identifier: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const { login } = useAuth();
  const navigate = useNavigate();
  const [shake, setShake] = useState(false);

  useEffect(() => {
    localStorage.removeItem('token');
  }, []);

  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post('/auth/login', credentials);
      const token = res.data.accessToken || res.data.token;

      if (token) {
        login(token);
        setSnackbar({ open: true, message: 'Login successful!', severity: 'success' });
        setTimeout(() => navigate('/dashboard', { replace: true }), 1000);
      } else {
        setShake(true);
        setSnackbar({ open: true, message: 'Token not received', severity: 'error' });
      }
    } catch (err) {
      setShake(true);
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        typeof err?.response?.data === 'string'
          ? err.response.data
          : 'Login failed. Please try again.';
      setSnackbar({ open: true, message, severity: 'error' });
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-tr from-blue-100 to-green-100">
      <style>
        {`
          @keyframes slideIn {
            from {
              opacity: 0;
              transform: translateY(40px);
            }
            to {
              opacity: 1;
              transform: translateY(0);
            }
          }
          .animate-slideIn {
            animation: slideIn 0.6s ease-out forwards;
          }

          @keyframes shake {
            0% { transform: translateX(0); }
            25% { transform: translateX(-8px); }
            50% { transform: translateX(8px); }
            75% { transform: translateX(-8px); }
            100% { transform: translateX(0); }
          }
          .shake {
            animation: shake 0.4s ease-in-out;
          }
        `}
      </style>

      <div
        className={`w-full max-w-md bg-white p-8 rounded-xl shadow-2xl transition-all duration-500 animate-slideIn ${
          shake ? 'shake' : ''
        }`}
        onAnimationEnd={() => setShake(false)}
      >
        <h2 className="text-3xl font-bold text-center mb-6 text-gray-800 tracking-wide animate-pulse">
          Welcome Back
        </h2>
        <form onSubmit={handleLogin} className="space-y-4">
          {/* Identifier Input */}
          <div className="flex items-center border border-gray-300 rounded px-3 py-2 focus-within:ring-2 focus-within:ring-blue-400 transition-all duration-200">
            <EmailIcon className="text-gray-400 mr-2" />
            <input
              type="text"
              placeholder="Email or Phone"
              className="w-full bg-transparent focus:outline-none"
              onChange={(e) => setCredentials({ ...credentials, identifier: e.target.value })}
              required
            />
          </div>

          {/* Password Input */}
          <div className="flex items-center border border-gray-300 rounded px-3 py-2 focus-within:ring-2 focus-within:ring-blue-400 transition-all duration-200 relative">
            <LockIcon className="text-gray-400 mr-2" />
            <input
              type={showPassword ? 'text' : 'password'}
              placeholder="Password"
              className="w-full bg-transparent focus:outline-none pr-8"
              onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
              required
            />
            <div
              className="absolute right-3 cursor-pointer text-gray-500 hover:text-gray-700"
              onMouseDown={() => setShowPassword(true)}
              onMouseUp={() => setShowPassword(false)}
              onMouseLeave={() => setShowPassword(false)}
            >
              {showPassword ? <VisibilityOffIcon /> : <VisibilityIcon />}
            </div>
          </div>

          <div className="space-y-3">
          {/* Login Button */}
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 rounded-lg font-semibold shadow-lg hover:bg-blue-700 hover:shadow-xl transition-all duration-300 transform hover:scale-105 active:scale-95"
          >
            <span className="tracking-wider">Login</span>
          </button>
          {/* Google Login Button */}
  <button
    type="button"
    onClick={handleGoogleLogin}
    className="w-full bg-[#4DB6AC] text-white py-2 rounded-lg font-semibold shadow-lg hover:bg-[#00897B] hover:shadow-xl transition-all duration-300 transform hover:scale-105 active:scale-95 flex items-center justify-center gap-2"
  >
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 48 48"
      width="20"
      height="20"
    >
      <path
        fill="#FFC107"
        d="M43.6 20.5H42V20H24v8h11.3C34.2 32.4 29.5 36 24 36c-6.6 0-12-5.4-12-12s5.4-12 12-12c3 0 5.7 1.1 7.8 3l5.7-5.7C33.4 6.2 28.9 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20c10.4 0 19-7.5 19-20 0-1.3-.1-2.7-.4-3.5z"
      />
      <path
        fill="#FF3D00"
        d="M6.3 14.7l6.6 4.8C14.3 16.1 18.8 14 24 14c3 0 5.7 1.1 7.8 3l5.7-5.7C33.4 6.2 28.9 4 24 4c-7.6 0-14 4.1-17.7 10.7z"
      />
      <path
        fill="#4CAF50"
        d="M24 44c5.3 0 10.2-1.8 13.9-4.9l-6.4-5.3C29.4 35.6 26.8 36 24 36c-5.5 0-10.2-3.6-11.8-8.5l-6.7 5.2C9.9 39.5 16.4 44 24 44z"
      />
      <path
        fill="#1976D2"
        d="M43.6 20.5H42V20H24v8h11.3c-1.3 3.4-4 6.3-7.3 7.6l6.4 5.3C37.6 38.3 42 31.8 42 24c0-1.3-.1-2.7-.4-3.5z"
      />
    </svg>
    <span className="tracking-wider">Continue with Google</span>
  </button>
          </div>

          {/* Signup Link */}
          <p className="mt-4 text-center text-sm text-gray-600">
            Don't have an account?{' '}
            <a href="/signup" className="text-green-600 hover:underline font-medium">
              Click here to register
            </a>
          </p>
        </form>
      </div>

      {/* Snackbar */}
      <Snackbar
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <MuiAlert elevation={6} variant="filled" severity={snackbar.severity}>
          {snackbar.message}
        </MuiAlert>
      </Snackbar>
    </div>
  );
}
