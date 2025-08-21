import React, { useState } from 'react';
import {
  Box,
  Typography,
  TextField,
  Button,
  Snackbar,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from '@mui/material';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../../api/axios';

export default function SettleForm() {
  const location = useLocation();
  const navigate = useNavigate();
  const state = location.state;

  const [amount, setAmount] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);

  if (!state || !state.groupid || !state.paidby || !state.paidto) {
    return (
      <Typography color="error" textAlign="center" mt={8}>
        Missing required data. Please go back and try again.
      </Typography>
    );
  }

  const { groupid, paidby, paidto } = state;

  const handleCreateSettlement = async () => {
    try {
      await api.post(`/group/${groupid}/settlements`, {
        paidby: paidby.userId,
        paidto: paidto.userId,
        amount: parseFloat(amount),
      });
      setSnackbar({ open: true, message: 'Settlement created successfully!', severity: 'success' });
      navigate(`/group/${groupid}`, {
      state: { tab: 'balance' },
    }); // Go back
    } catch (error) {
      const msg =
        error.response?.data?.message ||
        (typeof error.response?.data === 'string' ? error.response.data : null) ||
        error.message ||
        'Error creating settlement';
      setSnackbar({ open: true, message: msg, severity: 'error' });
    } finally {
      setConfirmDialogOpen(false);
    }
  };

  const handleBackToBalance = () => {
    navigate(`/group/${groupid}`, {
      state: { tab: 'balance' },
    });
  };

  const handleSnackbarClose = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  return (
    <Box
      sx={{
        maxWidth: 450,
        mx: 'auto',
        mt: 10,
        p: 4,
        bgcolor: 'background.paper',
        borderRadius: 2,
        boxShadow: 3,
      }}
    >
      <Typography variant="h5" fontWeight="bold" gutterBottom>
        Settle Up
      </Typography>

      <Box mt={2} mb={2}>
        <Typography fontWeight="medium">Paid By:</Typography>
        <Typography color="text.secondary">{paidby.username}</Typography>
      </Box>

      <Box mb={2}>
        <Typography fontWeight="medium">Paid To:</Typography>
        <Typography color="text.secondary">{paidto.username}</Typography>
      </Box>

      <TextField
        label="Amount"
        type="number"
        fullWidth
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        required
        inputProps={{ min: 1 }}
        sx={{ mb: 3 }}
      />

      <Box display="flex" justifyContent="space-between">
        <Button
          variant="contained"
          color="primary"
          onClick={() => setConfirmDialogOpen(true)}
          disabled={!amount || parseFloat(amount) <= 0}
        >
          Create Settlement
        </Button>

        <Button variant="outlined" color="secondary" onClick={handleBackToBalance}>
          Back to Balance
        </Button>
      </Box>

      {/* Snackbar for success/error */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>

      {/* Confirmation Dialog */}
      <Dialog open={confirmDialogOpen} onClose={() => setConfirmDialogOpen(false)}>
        <DialogTitle>Confirm Settlement</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to create a settlement of ₹{amount} from <strong>{paidby.username}</strong> to <strong>{paidto.username}</strong>?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialogOpen(false)} color="inherit">
            Cancel
          </Button>
          <Button onClick={handleCreateSettlement} color="primary" variant="contained">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

