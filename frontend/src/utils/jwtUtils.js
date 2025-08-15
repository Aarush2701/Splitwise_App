import { jwtDecode } from 'jwt-decode';

export const getUserFromToken = () => {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    return {
      id: decoded.id,
      email: decoded.sub
    };
  } catch (err) {
    console.error('Invalid token:', err);
    return null;
  }
};

export const getUserIdFromToken = () => {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);
    return decoded.id;
  } catch (err) {
    console.error('Invalid token:', err);
    return null;
  }
};
