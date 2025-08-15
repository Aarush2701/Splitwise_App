
import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import PersonIcon from '@mui/icons-material/Person';
import EmailIcon from '@mui/icons-material/Email';
import PhoneAndroidIcon from '@mui/icons-material/PhoneAndroid';
import LockIcon from '@mui/icons-material/Lock';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';

export default function Signup() {
  const [form, setForm] = useState({ username: '', email: '', phone: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [shake, setShake] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleGoogleSignup = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  const handleSignup = async (e) => {
  e.preventDefault();
  try {
    const res = await api.post('/auth/signup', form);

    const token = res.data.token; // make sure your backend returns this
    localStorage.setItem('token', token); // or call your AuthContext's login method

    setSnackbar({ open: true, message: 'User created successfully.', severity: 'success' });
    login(token);

    setTimeout(() => {
      navigate('/dashboard'); // Redirect to dashboard
    }, 1000);
  } catch (err) {
    const message =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.response?.data ||
      'Signup failed. Please try again.';
    setSnackbar({ open: true, message, severity: 'error' });
    setShake(true);
    setTimeout(() => setShake(false), 500);
  }
};


  useEffect(() => {
    localStorage.removeItem('token');
  }, []);

  const handlePhoneChange = (e) => {
    const value = e.target.value.replace(/\D/g, ''); // allow only digits
    setForm({ ...form, phone: value });
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-tr from-green-100 to-blue-100">
      {/* CSS Animations */}
      <style>
        {`
          @keyframes slideIn {
            from { opacity: 0; transform: translateY(40px); }
            to { opacity: 1; transform: translateY(0); }
          }
          .animate-slideIn {
            animation: slideIn 0.6s ease-out forwards;
          }

          @keyframes shake {
            0% { transform: translateX(0); }
            20% { transform: translateX(-10px); }
            40% { transform: translateX(10px); }
            60% { transform: translateX(-10px); }
            80% { transform: translateX(10px); }
            100% { transform: translateX(0); }
          }
          .shake {
            animation: shake 0.4s ease-in-out;
          }
        `}
      </style>

      <div className={`w-full max-w-md bg-white p-8 rounded-xl shadow-2xl transform transition-all duration-500 animate-slideIn ${shake ? 'shake' : ''}`}>
        <h2 className="text-3xl font-bold text-center mb-6 text-gray-800 tracking-wide animate-pulse">
          Create Account
        </h2>

        <form onSubmit={handleSignup} className="space-y-4">
          {/* Username */}
          <div className="flex items-center border border-gray-300 rounded px-3 py-2 focus-within:ring-2 focus-within:ring-green-400">
            <PersonIcon className="text-gray-400 mr-2" />
            <input
              type="text"
              placeholder="Username"
              className="w-full bg-transparent focus:outline-none"
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              required
            />
          </div>

          {/* Email */}
          <div className="flex items-center border border-gray-300 rounded px-3 py-2 focus-within:ring-2 focus-within:ring-green-400">
            <EmailIcon className="text-gray-400 mr-2" />
            <input
              type="email"
              placeholder="Email"
              className="w-full bg-transparent focus:outline-none"
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              required
            />
          </div>

          {/* Phone */}
          <div className="flex items-center border border-gray-300 rounded px-3 py-2 focus-within:ring-2 focus-within:ring-green-400">
            <PhoneAndroidIcon className="text-gray-400 mr-2" />
            <input
              type="tel"
              placeholder="Phone"
              className="w-full bg-transparent focus:outline-none"
              onChange={handlePhoneChange}
              value={form.phone}
              required
            />
          </div>

          {/* Password */}
          <div className="flex items-center border border-gray-300 rounded px-3 py-2 focus-within:ring-2 focus-within:ring-green-400 relative">
            <LockIcon className="text-gray-400 mr-2" />
            <input
              type={showPassword ? 'text' : 'password'}
              placeholder="Password"
              className="w-full bg-transparent focus:outline-none pr-8"
              onChange={(e) => setForm({ ...form, password: e.target.value })}
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

          {/* Buttons Section */}
<div className="space-y-3">
  {/* Register Button */}
  <button
    type="submit"
    className="w-full bg-blue-500 text-white py-2 rounded-lg font-semibold shadow-lg hover:bg-blue-600 hover:shadow-xl transition-all duration-300 transform hover:scale-105 active:scale-95"
  >
    <span className="tracking-wider">Register</span>
  </button>

  {/* Google Signup Button */}
  <button
    type="button"
    onClick={handleGoogleSignup}
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
          

          {/* Login Redirect */}
          <p className="mt-4 text-center text-sm text-gray-600">
            Already have an account?{' '}
            <a href="/login" className="text-blue-600 hover:underline font-medium">
              Click here to Login
            </a>
          </p>
        </form>
      </div>

      {/* Snackbar Notification */}
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
