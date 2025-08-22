import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  TextField,
  MenuItem,
  Button,
  FormControl,
  InputLabel,
  Select,
  Checkbox,
  FormControlLabel,
  Snackbar,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Slide,
  Card,
  CardContent,
  Box,
} from '@mui/material';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../api/axios';
import { getUserFromToken } from '../../utils/jwtUtils';

const Transition = React.forwardRef((props, ref) => {
  return <Slide direction="down" ref={ref} {...props} />;
});

export default function AddExpense() {
  const { groupid } = useParams();
  const navigate = useNavigate();
  const user = getUserFromToken();
  const groupId = groupid;

  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({
    amount: '',
    description: '',
    paidBy: '',
    participants: [],
    splitType: 'EQUAL',
    values: []
  });

  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [confirmOpen, setConfirmOpen] = useState(false);

  useEffect(() => {
    api.get(`/group/${groupid}`)
      .then(res => {
        setUsers(res.data.members || []);
      })
      .catch(err => {
        console.error("Failed to load group members:", err);
        setSnackbar({ open: true, message: 'Failed to load group members', severity: 'error' });
      });
  }, [groupid]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleParticipantToggle = (id) => {
    let updatedParticipants;

    if (form.participants.includes(id)) {
      updatedParticipants = form.participants.filter(p => p !== id);
    } else {
      updatedParticipants = [...form.participants, id];
    }

    const updatedValues = updatedParticipants.map((_, i) => form.values[i] || '');

    setForm({
      ...form,
      participants: updatedParticipants,
      values: updatedValues
    });
  };

  const handleValuesChange = (index, value) => {
    const updatedValues = [...form.values];
    updatedValues[index] = parseFloat(value);
    setForm({ ...form, values: updatedValues });
  };

  const handleConfirm = async () => {
    setConfirmOpen(false);

    try {
      await api.post(`/group/${groupId}/expenses`, form);
      setSnackbar({ open: true, message: 'Expense created successfully!', severity: 'success' });
      setTimeout(() => {
        navigate(`/group/${groupid}`);
      }, 1500);
    } catch (err) {
      const backendMessage = err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.response?.data  || 'Failed to add expense';
      setSnackbar({ open: true, message: backendMessage, severity: 'error' });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.paidBy) {
      setSnackbar({ open: true, message: 'Please select a payer.', severity: 'error' });
      return;
    }
    if (form.participants.length === 0) {
    setSnackbar({ open: true, message: 'Please select at least one participant.', severity: 'error' });
    return;
  }

  if ((form.splitType === 'EXACT' || form.splitType === 'PERCENTAGE') && 
      form.values.some(v => !v && v !== 0)) {
    setSnackbar({ open: true, message: 'Please enter all split values.', severity: 'error' });
    return;
  }
    setConfirmOpen(true);
  };

  return (
    <Container maxWidth="sm">
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        TransitionComponent={Transition}
      >
        <Alert
          severity={snackbar.severity}
          variant="filled"
          onClose={() => setSnackbar({ ...snackbar, open: false })}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>

      <Card elevation={4} sx={{ mt: 6, p: 2 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Add New Expense
          </Typography>

          <form onSubmit={handleSubmit}>
            <Box mt={2}>
              <TextField
                fullWidth
                label="Description"
                name="description"
                value={form.description}
                onChange={handleChange}
                required
              />
            </Box>

            <Box mt={2}>
              <TextField
                fullWidth
                label="Amount"
                type="number"
                name="amount"
                value={form.amount}
                onChange={handleChange}
                required
              />
            </Box>

            <Box mt={2}>
              <FormControl fullWidth>
                <InputLabel>Split Type</InputLabel>
                <Select
                  name="splitType"
                  value={form.splitType}
                  onChange={handleChange}
                  label="Split Type"
                >
                  <MenuItem value="EQUAL">Equal</MenuItem>
                  <MenuItem value="EXACT">Exact</MenuItem>
                  <MenuItem value="PERCENTAGE">Percentage</MenuItem>
                </Select>
              </FormControl>
            </Box>

            <Box mt={2}>
              <FormControl fullWidth>
                <InputLabel>Select Payer</InputLabel>
                <Select
                  value={form.paidBy}
                  onChange={(e) => setForm({ ...form, paidBy: Number(e.target.value) })}
                  required
                  label="Select Payer"
                >
                  <MenuItem value="">-- Select One Member --</MenuItem>
                  {users.map(u => (
                    <MenuItem key={u.userId} value={u.userId}>{u.username}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Box>

            <Box mt={2}>
              <Typography variant="subtitle1">Select Participants:</Typography>
              {users.map(u => (
                <FormControlLabel
                  key={u.userId}
                  control={
                    <Checkbox
                      checked={form.participants.includes(u.userId)}
                      onChange={() => handleParticipantToggle(u.userId)}
                    />
                  }
                  label={u.username}
                />
              ))}
            </Box>

            {(form.splitType === 'EXACT' || form.splitType === 'PERCENTAGE') && (
              <Box mt={2}>
                <Typography variant="subtitle1">Enter Values:</Typography>
                {form.participants.map((id, index) => (
                  <TextField
                    key={id}
                    label={users.find(u => u.userId === id)?.username}
                    type="number"
                    value={form.values[index] || ''}
                    onChange={(e) => handleValuesChange(index, e.target.value)}
                    fullWidth
                    sx={{ mb: 2 }}
                  />
                ))}
              </Box>
            )}

           <Box mt={4} display="flex" gap={2} justifyContent="flex-end">
  <Button
    variant="outlined"
    color="secondary"
    onClick={() => navigate(-1)}
    startIcon={<i className="fas fa-arrow-left" />}
    sx={{
      borderRadius: '20px',
      textTransform: 'none',
      px: 3,
      fontWeight: 'bold'
    }}
  >
    Go Back
  </Button>

  <Button
    type="submit"
    variant="contained"
    color="primary"
    endIcon={<i className="fas fa-plus-circle" />}
    sx={{
      borderRadius: '20px',
      textTransform: 'none',
      px: 4,
      fontWeight: 'bold'
    }}
  >
    Add Expense
  </Button>
</Box>

          </form>
        </CardContent>
      </Card>

      {/* Confirm Dialog */}
      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Confirm Expense Creation</DialogTitle>
        <DialogContent>
          Are you sure you want to create this expense?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)} color="error">Cancel</Button>
          <Button onClick={handleConfirm} color="primary" variant="contained">Confirm</Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}
