// import React, { useEffect, useState } from 'react';
// import api from '../api/axios';
// import { getUserFromToken } from '../utils/jwtUtils';

// export default function UpdateProfile({ onBack }) {
//   const [form, setForm] = useState({ username: '', phone: '', email: '', password: '' });
//   const [userId, setUserId] = useState(null);

//   useEffect(() => {
//     const token = localStorage.getItem('token');
//     const decoded = getUserFromToken(token);
//     const id = decoded?.id;
//     setUserId(id);

//     if (id) {
//       api.get(`/users/${id}`)
//         .then(res => {
//           setForm({
//             username: res.data.username || '',
//             phone: res.data.phone || '',
//             email: res.data.email || '',
//             password: ''
//           });
//         })
//         .catch(err => console.error('Failed to fetch user details', err));
//     }
//   }, []);

//   const handleSubmit = async (e) => {
//     e.preventDefault();
//     try {
//       await api.put(`/users/${userId}`, form);
//       alert('Profile updated!');
//     } catch (err) {
//       console.error('Update failed', err);
//       alert('Update failed!');
//     }
//   };
//   return (
//   <div className="p-4 max-w-md bg-white rounded-lg shadow">
//     <h2 className="text-xl font-bold mb-4">Update Profile</h2>
//     <form onSubmit={handleSubmit} className="flex flex-col gap-4">
//       <input
//         type="text"
//         placeholder="Username"
//         value={form.username}
//         onChange={(e) => setForm({ ...form, username: e.target.value })}
//         className="w-full p-2 border border-gray-300 rounded"
//         required
//       />
//       <input
//         type="tel"
//         placeholder="Phone"
//         value={form.phone}
//         onChange={(e) => setForm({ ...form, phone: e.target.value })}
//         className="w-full p-2 border border-gray-300 rounded"
//         required
//       />
//       <input
//         type="email"
//         placeholder="Email"
//         value={form.email}
//         onChange={(e) => setForm({ ...form, email: e.target.value })}
//         className="w-full p-2 border border-gray-300 rounded"
//         required
//       />
//       <input
//         type="password"
//         placeholder="New Password"
//         value={form.password}
//         onChange={(e) => setForm({ ...form, password: e.target.value })}
//         className="w-full p-2 border border-gray-300 rounded"
//         required
//       />
//       <button
//         type="submit"
//         className="bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-800 transition"
//       >
//         Update
//       </button>
//     </form>
//     <div className="mt-4">
//       <button
//         onClick={onBack}
//         className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700 transition"
//       >
//         Back to Home
//       </button>
//     </div>
//   </div>
// );
// }


import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import { getUserFromToken } from '../utils/jwtUtils';
import SaveIcon from '@mui/icons-material/Save';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import {
  Box,
  TextField,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Typography,
  Card,
  CardContent,
  Snackbar,
  Alert,
  Slide,
  Stack
} from '@mui/material';
import Avatar from '@mui/material/Avatar';
import PersonIcon from '@mui/icons-material/Person';
import { useNavigate } from 'react-router-dom';

export default function UpdateProfile({ onBack }) {
  const [form, setForm] = useState({ username: '', phone: '', email: '', password: '' });
  const [userId, setUserId] = useState(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const navigate = useNavigate();
  useEffect(() => {
    const token = localStorage.getItem('token');
    const decoded = getUserFromToken(token);
    const id = decoded?.id;
    setUserId(id);

    if (id) {
      api.get(`/users/${id}`)
        .then(res => {
          setForm({
            username: res.data.username || '',
            phone: res.data.phone || '',
            email: res.data.email || '',
            password: ''
          });
        })
        .catch(err => console.error('Failed to fetch user details', err));
    }
  }, []);

  const handleSubmit = async () => {
    try {
      await api.put(`/users/${userId}`, form);
      setSnackbar({ open: true, message: 'Profile updated!', severity: 'success' });
      setDialogOpen(false);
      navigate("/login");
    } catch (err) {
      console.error('Update failed', err);
      setSnackbar({ open: true, message: 'Update failed!', severity: 'error' });
      setDialogOpen(false);
    }
  };

  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="flex-start"
      minHeight="100vh"
      mt={5}
      sx={{ backgroundColor: '#e8f0fe', paddingTop: 4 }}
    >
      <Slide direction="up" in mountOnEnter unmountOnExit>
        <Card
          sx={{
            width: 400,
            padding: 3,
            boxShadow: 6,
            borderRadius: 4,
            backgroundColor: '#fefeff',
            border: '1px solid #d0d7de',
            animation: 'fadeIn 0.5s ease-in-out',
          }}
        >
          <CardContent>
            <Stack alignItems="center" spacing={2} mb={2}>
              <Avatar sx={{ bgcolor: '#4dabf7', width: 80, height: 80 }}>
                <PersonIcon sx={{ fontSize: 40 }} />
              </Avatar>
              <Typography variant="h5" fontWeight="bold">
                Update Profile
              </Typography>
            </Stack>

            <form className="flex flex-col gap-4">
              <TextField
                fullWidth
                label="Username"
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
              />
              <TextField
                fullWidth
                label="Phone"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
              />
              <TextField
                fullWidth
                label="Email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
              />
              <TextField
                fullWidth
                label="New Password"
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
              />
            <Button
            variant="contained"
            fullWidth
            startIcon={<SaveIcon />}
            sx={{
              backgroundColor: '#4dabf7',
              color: 'white',
              textTransform: 'none',
               mt: 2,
              fontWeight: 600,
              borderRadius: 2,
              boxShadow: '0px 3px 6px rgba(0, 0, 0, 0.1)',
              '&:hover': { backgroundColor: '#2196f3' },
              '&:active': { backgroundColor: '#1976d2' }
            }}
            onClick={() => setDialogOpen(true)}
            >
              Confirm Update
              </Button>

            </form>

            <Button
            onClick={onBack}
            fullWidth
            variant="outlined"
            startIcon={<ArrowBackIcon />}
            sx={{
              mt: 2,
              color: '#37474f',
              borderColor: '#cfd8dc',
              textTransform: 'none',
              fontWeight: 500,
              '&:hover': {
               backgroundColor: '#eceff1',
              borderColor: '#b0bec5',
              }}}>
                Back to Dashboard
            </Button>
          </CardContent>
        </Card>
      </Slide>

      {/* Confirmation Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
        <DialogTitle>Confirm Update</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to update your profile details?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)} color="secondary">
            Cancel
          </Button>
          <Button onClick={handleSubmit} color="primary" autoFocus>
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
