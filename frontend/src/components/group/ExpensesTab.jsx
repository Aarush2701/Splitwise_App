import React, { useEffect, useState } from 'react';
import api from '../../api/axios';
import { useNavigate } from 'react-router-dom';
import { getUserFromToken } from '../../utils/jwtUtils';

import {
  Typography,
  Button,
  Box,
  Stack,
  Paper,
  CircularProgress,
  Snackbar,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Divider,
  Grid,
  Card,
  CardContent,
  Avatar,
  Fade
} from '@mui/material';

import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import ReceiptLongOutlinedIcon from '@mui/icons-material/ReceiptLongOutlined';
import RestoreIcon from '@mui/icons-material/Restore';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import { teal } from '@mui/material/colors';

export default function ExpensesTab({ groupid }) {
  const [expenses, setExpenses] = useState([]);
  const [error, setError] = useState(null);
  const [showingMyExpenses, setShowingMyExpenses] = useState(false);
  const [selectedExpenseId, setSelectedExpenseId] = useState(null);
  const [splits, setSplits] = useState([]);
  const [loadingSplits, setLoadingSplits] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [expenseToDelete, setExpenseToDelete] = useState(null);
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');

  const navigate = useNavigate();
  const currentUser = getUserFromToken();

  useEffect(() => {
    fetchAllExpenses();
  }, [groupid]);

  const fetchAllExpenses = async () => {
    setError(null);
    setShowingMyExpenses(false);
    try {
      const res = await api.get(`/group/${groupid}/expenses`);
      setExpenses((res.data || []).sort((a, b) => new Date(b.expensedate) - new Date(a.expensedate)));
    } catch (err) {
      setError('Failed to load expenses.');
    }
  };

  const fetchMyExpenses = async () => {
    setError(null);
    setShowingMyExpenses(true);
    try {
      const res = await api.get(`/group/${groupid}/expenses/users/${currentUser.id}`);
      setExpenses((res.data || []).sort((a, b) => new Date(b.expensedate) - new Date(a.expensedate)));
    } catch (err) {
      setError('Failed to load your expenses.');
    }
  };

  const handleViewSplits = async (expenseid) => {
    if (selectedExpenseId === expenseid) {
      setSelectedExpenseId(null);
      setSplits([]);
      return;
    }

    try {
      setSelectedExpenseId(expenseid);
      setLoadingSplits(true);
      const res = await api.get(`/group/${groupid}/expenses/${expenseid}/splits`);
      setSplits(res.data || []);
    } catch {
      setSplits([]);
    } finally {
      setLoadingSplits(false);
    }
  };

  const openDeleteDialog = (expenseid) => {
    setExpenseToDelete(expenseid);
    setDeleteDialogOpen(true);
  };

  const confirmDeleteExpense = async () => {
    try {
      await api.delete(`/group/${groupid}/expenses/${expenseToDelete}`);
      setSnackbarMessage('Expense deleted successfully');
      setSnackbarSeverity('success');
      setSnackbarOpen(true);
      fetchAllExpenses();
    } catch {
      setSnackbarMessage('Failed to delete expense');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    } finally {
      setDeleteDialogOpen(false);
      setExpenseToDelete(null);
    }
  };

  return (
    <Box mt={3}>
      <Typography variant="h6" fontWeight="bold" gutterBottom>
        {showingMyExpenses ? 'My Expenses' : 'Group Expenses'}
      </Typography>

      {/* Action Buttons */}
      <Stack
  direction={{ xs: 'column', sm: 'row' }}
  spacing={1.5}
  mb={3}
  alignItems="flex-start"
>

        <Button
          variant="contained"
          startIcon={<AddCircleOutlineIcon />}
          onClick={() => navigate(`/group/${groupid}/add-expense`)}
          sx={{
            backgroundColor: '#4DB6AC',
            textTransform: 'none',
            color: 'white',
            '&:hover': {
              backgroundColor: '#00897B',
            },
          }}
        >
          Add Expense
        </Button>

        <Button
          variant="outlined"
          startIcon={<ReceiptLongOutlinedIcon />}
          onClick={fetchMyExpenses}
          sx={{
            textTransform: 'none',
          }}
        >
          My Expenses
        </Button>

        {showingMyExpenses && (
          <Button
  variant="outlined"
  startIcon={<RestoreIcon />}
  onClick={fetchAllExpenses}
  sx={{
    textTransform: 'none',
    mt: { xs: 2.5, sm: 0 },  // Adds vertical margin on mobile only
  }}
>
  Show All Expenses
</Button>
        )}
      </Stack>

      {error && <Alert severity="error">{error}</Alert>}

      {expenses.length === 0 ? (
        <Typography variant="body2" color="text.secondary">
          No expenses found.
        </Typography>
      ) : (
        <Stack spacing={3}>
          {expenses.map((expense) => (
            <Paper
              key={expense.expenseid}
              elevation={2}
              sx={{ p: 2, backgroundColor: '#fefefe', borderRadius: 2 }}
            >
              <Stack direction="row" justifyContent="space-between" alignItems="center">
                <Typography fontWeight="bold">{expense.description}</Typography>
                <Typography variant="caption" color="text.secondary">
                  {new Date(expense.expensedate).toLocaleString('en-IN', {
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: true,
                  })}
                </Typography>
              </Stack>

              <Typography variant="body2" sx={{ mt: 0.5 }} color="text.secondary">
                ₹{expense.amount} paid by <b>{expense.userid.username}</b>
              </Typography>

              <Divider sx={{ my: 1 }} />

              <Stack direction="row" spacing={1}>
                <Button
                  size="small"
                  variant="outlined"
                  color="warning"
                  onClick={() => navigate(`/group/${groupid}/expenses/${expense.expenseid}/edit`)}
                >
                  Edit
                </Button>

                <Button
                  size="small"
                  variant="outlined"
                  color="error"
                  onClick={() => openDeleteDialog(expense.expenseid)}
                >
                  Delete
                </Button>

                <Button
                  size="small"
                  variant="outlined"
                  onClick={() => handleViewSplits(expense.expenseid)}
                >
                  {selectedExpenseId === expense.expenseid ? 'Hide' : 'View'} Splits
                </Button>
              </Stack>

              {selectedExpenseId === expense.expenseid && (
                <Box mt={3}>
  <Typography variant="h6" gutterBottom>
    Split Breakdown
  </Typography>

  {loadingSplits ? (
    <Box display="flex" justifyContent="center" alignItems="center" height={100}>
      <CircularProgress size={30} />
    </Box>
  ) : splits.length === 0 ? (
    <Box p={2} bgcolor="#FFF3CD" borderRadius={2} border="1px solid #FFE58F">
      <Typography>No participant data found.</Typography>
    </Box>
  ) : (
    <Grid container spacing={2} mt={1}>
      {splits.map((s, idx) => (
        <Fade in={true} timeout={500 + idx * 100} key={idx}>
          <Grid item xs={12} sm={6} md={4}>
            <Card variant="outlined" sx={{ display: 'flex', alignItems: 'center', p: 1 }}>
              <Avatar sx={{ bgcolor: teal[500], mr: 1 }}>
                <AccountCircleIcon />
              </Avatar>
              <CardContent sx={{ flexGrow: 1, p: 0 }}>
                <Typography fontWeight={600}>{s.username}</Typography>
                <Typography color="text.secondary">₹{s.amount.toFixed(2)}</Typography>
              </CardContent>
            </Card>
          </Grid>
        </Fade>
      ))}
    </Grid>
  )}
</Box>

              )}
            </Paper>
          ))}
        </Stack>
      )}

      {/* Delete Dialog */}
      <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>Are you sure you want to delete this expense?</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)} color="primary">
            Cancel
          </Button>
          <Button onClick={confirmDeleteExpense} color="error">
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert
          onClose={() => setSnackbarOpen(false)}
          severity={snackbarSeverity}
          variant="filled"
          sx={{ width: '100%' }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
}

