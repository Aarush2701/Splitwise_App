import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function OAuth2RedirectHandler() {
  const navigate = useNavigate();
    const { login } = useAuth();
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');

    if (token) {
      login(token);
      navigate('/dashboard'); //  Redirect to dashboard after saving token
    } else {
      console.error("Token not found in redirect URL");
      navigate('/login');
    }
  }, [navigate]);

  return <div>Redirecting...</div>;
}
