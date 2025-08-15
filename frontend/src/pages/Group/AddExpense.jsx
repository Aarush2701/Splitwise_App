
// import React, { useState, useEffect } from 'react';
// import { useParams, useNavigate } from 'react-router-dom';
// import api from '../../api/axios';
// import { getUserFromToken } from '../../utils/jwtUtils';

// export default function AddExpense() {
//   const { groupid } = useParams();
//   const navigate = useNavigate();
//   const user = getUserFromToken();
//   const groupId = groupid;

//   const [users, setUsers] = useState([]);

//   const [form, setForm] = useState({
//     amount: '',
//     description: '',
//     paidBy: '', // initially empty
//     participants: [],
//     splitType: 'EQUAL',
//     values: []
//   });

//   // Load group members
//   useEffect(() => {
//     api.get(`/group/${groupid}`)
//       .then(res => {
//         setUsers(res.data.members || []);
//       })
//       .catch(err => console.error("Failed to load group members:", err));
//   }, [groupid]);

//   const handleChange = (e) => {
//     setForm({ ...form, [e.target.name]: e.target.value });
//   };

//   const handleParticipantToggle = (id) => {
//     let updatedParticipants;

//     if (form.participants.includes(id)) {
//       updatedParticipants = form.participants.filter(p => p !== id);
//     } else {
//       updatedParticipants = [...form.participants, id];
//     }

//     const updatedValues = updatedParticipants.map((_, i) => form.values[i] || '');

//     setForm({
//       ...form,
//       participants: updatedParticipants,
//       values: updatedValues
//     });
//   };

//   const handleValuesChange = (index, value) => {
//     const updatedValues = [...form.values];
//     updatedValues[index] = parseFloat(value);
//     setForm({ ...form, values: updatedValues });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     if (!form.paidBy) {
//       alert("Please select a payer.");
//       return;
//     }

//     try {
//       await api.post(`/group/${groupid}/expenses`, form);
//       alert("Expense added!");
//       navigate(`/group/${groupid}`);
//     } catch (err) {
//       console.error("Failed to add expense:", err);
//       const backendMessage = err.response?.data || "Failed to add expense";
//       alert(backendMessage);
//     }
//   };

//   return (
//     <div className="p-6 max-w-2xl mx-auto">
//       <h2 className="text-xl font-bold mb-4">Add Expense</h2>
//       <form onSubmit={handleSubmit} className="space-y-4">
//         <input name="description" value={form.description} onChange={handleChange} placeholder="Description" className="w-full border px-3 py-2" required />
//         <input name="amount" type="number" value={form.amount} onChange={handleChange} placeholder="Amount" className="w-full border px-3 py-2" required />

//         <select name="splitType" value={form.splitType} onChange={handleChange} className="w-full border px-3 py-2">
//           <option value="EQUAL">Equal</option>
//           <option value="EXACT">Exact</option>
//           <option value="PERCENTAGE">Percentage</option>
//         </select>

//         {/* 🔵 Select Payer Dropdown */}
//         <div>
//           <label className="block font-semibold">Select Payer</label>
//           <select
//             value={form.paidBy}
//             onChange={(e) => setForm({ ...form, paidBy: Number(e.target.value) })}
//             className="w-full border px-3 py-2"
//             required
//           >
//             <option value="">-- Select One Member --</option>
//             {users.map(u => (
//               <option key={u.userId} value={u.userId}>
//                 {u.username}
//               </option>
//             ))}
//           </select>
//         </div>

//         {/* 🟢 Participant Checkboxes */}
//         <div>
//           <label className="block font-semibold">Select Participants</label>
//           {users.map(u => (
//             <div key={u.userId}>
//               <label>
//                 <input
//                   type="checkbox"
//                   checked={form.participants.includes(u.userId)}
//                   onChange={() => handleParticipantToggle(u.userId)}
//                 />
//                 {u.username}
//               </label>
//             </div>
//           ))}
//         </div>

//         {/* 🔷 Values for EXACT or PERCENTAGE */}
//         {(form.splitType === "EXACT" || form.splitType === "PERCENTAGE") && (
//           <div>
//             <label className="block font-semibold">Enter Values</label>
//             {form.participants.map((id, index) => (
//               <div key={id} className="mb-3">
//                 <label className="block font-medium">
//                   {users.find(u => u.userId === id)?.username}:
//                 </label>
//                 <input
//                   type="number"
//                   value={form.values[index] || ''}
//                   onChange={(e) => handleValuesChange(index, e.target.value)}
//                   className="border px-2 py-1 mt-1 w-full"
//                   required
//                 />
//               </div>
//             ))}
//           </div>
//         )}

//         <div className="flex space-x-4 mb-6">
//           <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">Add Expense</button>
//           <button type="button" onClick={() => navigate(-1)} className="bg-orange-500 text-white px-4 py-2 rounded">
//             Back
//           </button>
//         </div>
//       </form>
//     </div>
//   );
// }
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
