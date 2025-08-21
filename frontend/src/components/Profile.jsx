import React, { useEffect, useState } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Avatar,
  Button,
  Stack,
  CircularProgress,
  Divider,
  Box,
  Snackbar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Slide
} from '@mui/material';
import MuiAlert from '@mui/material/Alert';
import PersonIcon from '@mui/icons-material/Person';
import DeleteForeverRoundedIcon from '@mui/icons-material/DeleteForeverRounded';
import ArrowBackIosNewRoundedIcon from '@mui/icons-material/ArrowBackIosNewRounded';
import EmailRoundedIcon from '@mui/icons-material/EmailRounded';
import PhoneRoundedIcon from '@mui/icons-material/PhoneRounded';
import api from '../api/axios';
import { getUserFromToken } from '../utils/jwtUtils';

const Alert = React.forwardRef(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

export default function Profile({ onBack }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [shake, setShake] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'info' });

  const userData = getUserFromToken();

  useEffect(() => {
    if (!userData?.id) {
      setSnackbar({ open: true, message: 'Invalid token or user not logged in', severity: 'error' });
      setLoading(false);
      return;
    }

    api.get(`/users/${userData.id}`)
      .then(res => {
        setUser(res.data);
        setLoading(false);
      })
      .catch(() => {
        setSnackbar({ open: true, message: 'Failed to fetch user data', severity: 'error' });
        setLoading(false);
      });
  }, [userData?.id]);

  const handleDelete = async () => {
    try {
      await api.delete(`/users/${userData.id}`);
      setSnackbar({ open: true, message: 'Account deleted successfully.', severity: 'success' });
      localStorage.removeItem('token');
      setTimeout(() => {
        window.location.href = '/login';
      }, 1500);
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        err?.response?.data ||
        'Failed to delete account';

      setSnackbar({ open: true, message, severity: 'error' });
      setShake(true);
      setTimeout(() => setShake(false), 500);
    } finally {
      setDialogOpen(false);
    }
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  if (loading) return <CircularProgress sx={{ mt: 10 }} />;

  return (
<Box
  display="flex"
  justifyContent="center"
  alignItems="flex-start"
  minHeight="100vh"
  mt={5}
  position="relative"
  sx={{ backgroundColor: '#f0f4ff', paddingTop: 4 }} // 🌤️ Light background
>
  <Slide direction="up" in mountOnEnter unmountOnExit>
    <Card
      className={shake ? 'shake' : ''}
      sx={{
        width: 400,
        padding: 3,
        boxShadow: 4,
        borderRadius: 4,
        transition: 'transform 0.3s',
        backgroundColor: '#ffffff', // 🧊 Soft card
        border: '1px solid #d0d7e2',
        animation: 'fadeIn 0.5s ease-in-out',
        color: '#333',
      }}
    >
      <CardContent>
        <Stack alignItems="center" spacing={2}>
          <Avatar sx={{ bgcolor: '#4dabf7', width: 80, height: 80 }}>
            <PersonIcon sx={{ fontSize: 40 }} />
          </Avatar>
          <Typography variant="h5" fontWeight="bold" color="#333">
            {user?.username}
          </Typography>
        </Stack>

        <Divider sx={{ my: 2, backgroundColor: '#ccc' }} />

        <Stack direction="row" alignItems="center" spacing={1} mb={1}>
          <EmailRoundedIcon sx={{ color: '#4dabf7' }} />
          <Typography variant="body1" color="#444">{user?.email}</Typography>
        </Stack>

        <Stack direction="row" alignItems="center" spacing={1}>
          <PhoneRoundedIcon sx={{ color: '#ef5350' }} />
          <Typography variant="body1" color="#444">{user?.phone}</Typography>
        </Stack>

        <Stack direction="row" spacing={2} mt={4} justifyContent="center">
          <Button
            onClick={onBack}
            startIcon={<ArrowBackIosNewRoundedIcon />}
            sx={{
              color: '#1976d2',
              borderColor: '#90caf9',
              fontWeight: '500',
              textTransform: 'none',
              px: 3,
              '&:hover': {
                backgroundColor: '#e3f2fd',
              },
            }}
            variant="outlined"
          >
            Back
          </Button>

          <Button
            onClick={() => setDialogOpen(true)}
            startIcon={<DeleteForeverRoundedIcon />}
            sx={{
              backgroundColor: '#ffcdd2',
              color: '#b71c1c',
              '&:hover': {
                backgroundColor: '#ef9a9a',
              },
              fontWeight: '500',
              textTransform: 'none',
              px: 3,
            }}
            variant="contained"
          >
            Delete
          </Button>
        </Stack>
      </CardContent>

      {/* ✨ Animation CSS */}
      <style>{`
        @keyframes fadeIn {
          0% { opacity: 0; transform: scale(0.95); }
          100% { opacity: 1; transform: scale(1); }
        }
        @keyframes shake {
          0% { transform: translateX(0); }
          25% { transform: translateX(-5px); }
          50% { transform: translateX(5px); }
          75% { transform: translateX(-5px); }
          100% { transform: translateX(0); }
        }
        .shake {
          animation: shake 0.3s ease-in-out;
        }
      `}</style>
    </Card>
  </Slide>

  {/* ✅ Snackbar (Top Center) */}
  <Snackbar
    open={snackbar.open}
    onClose={handleCloseSnackbar}
    autoHideDuration={3000}
    anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
    sx={{
      position: 'absolute',
      top: 0,
      zIndex: 1500,
    }}
  >
    <Alert
      onClose={handleCloseSnackbar}
      severity={snackbar.severity}
      sx={{
        backgroundColor: '#1976d2',
        color: '#fff',
        '& .MuiAlert-icon': {
          color: '#fff',
        },
      }}
    >
      {snackbar.message}
    </Alert>
  </Snackbar>

  {/* ✅ Confirmation Dialog */}
  <Dialog
    open={dialogOpen}
    onClose={() => setDialogOpen(false)}
    PaperProps={{
      sx: {
        backgroundColor: '#fff',
        color: '#333',
      },
    }}
  >
    <DialogTitle>Confirm Deletion</DialogTitle>
    <DialogContent>
      <DialogContentText sx={{ color: '#555' }}>
        Are you sure you want to delete your account? This cannot be undone.
      </DialogContentText>
    </DialogContent>
    <DialogActions>
      <Button onClick={() => setDialogOpen(false)} sx={{ color: '#1976d2' }}>
        Cancel
      </Button>
      <Button onClick={handleDelete} sx={{ color: '#d32f2f' }}>
        Delete
      </Button>
    </DialogActions>
  </Dialog>
</Box>

  );
}
