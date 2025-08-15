// import React, { useEffect, useState } from 'react';
// import { useParams, useNavigate } from 'react-router-dom';
// import api from '../../api/axios';

// export default function EditExpense() {
//   const { groupid, expenseid } = useParams();  // 🔁 Renamed here
//   const navigate = useNavigate();

//   const [form, setForm] = useState({
//     description: '',
//     amount: '',
//     paidBy: '',
//     participants: [],
//     splitType: 'EQUAL',
//     values: [],
//   });

//   const [groupMembers, setGroupMembers] = useState([]);
//   const [loading, setLoading] = useState(true);

//   useEffect(() => {
//     const fetchData = async () => {
//       try {
//         const [expenseRes, groupRes] = await Promise.all([
//           api.get(`/group/${groupid}/expenses/${expenseid}`),
//           api.get(`/group/${groupid}`),
//         ]);

//         const expense = expenseRes.data;
//         const groupData = groupRes.data;

//         const members = groupData.members.map((u) => ({
//           userid: u.userId,
//           username: u.username,
//         }));

//         setGroupMembers(members);

//         const participantIds = expense.participants
//           ? expense.participants.map((p) => p.userId)
//           : members.map((m) => m.userid);

//         setForm({
//           description: expense.description || '',
//           amount: expense.amount || '',
//           paidBy: expense.userid?.userid || '',
//           participants: participantIds,
//           splitType: expense.splitType || 'EQUAL',
//           values: expense.values || [],
//         });

//         setLoading(false);
//       } catch (error) {
//         console.error('Failed to load expense or group data', error);
//         alert('Failed to load expense');
//       }
//     };

//     fetchData();
//   }, [groupid, expenseid]);

//   const handleChange = (e) => {
//     setForm({ ...form, [e.target.name]: e.target.value });
//   };

//   const handleParticipantsChange = (userId) => {
//     setForm((prev) => ({
//       ...prev,
//       participants: prev.participants.includes(userId)
//         ? prev.participants.filter((id) => id !== userId)
//         : [...prev.participants, userId],
//     }));
//   };

//   const handleValueChange = (index, newValue) => {
//     const updated = [...form.values];
//     updated[index] = newValue;
//     setForm({ ...form, values: updated });
//   };

//   const handleSubmit = async (e) => {
//     e.preventDefault();

//     const payload = {
//       description: form.description,
//       amount: parseFloat(form.amount),
//       paidBy: form.paidBy,
//       participants: form.participants,
//       splitType: form.splitType,
//       values: form.splitType !== 'EQUAL' ? form.values.map(Number) : [],
//     };

//     try {
//       await api.put(`/group/${groupid}/expenses/${expenseid}`, payload);
//       alert('Expense updated successfully');
//       navigate(`/group/${groupid}`);
//     } catch (err) {
//       console.error(err);
//       alert('Failed to update expense');
//     }
//   };

//   if (loading) return <div>Loading...</div>;

//   return (
//     <div className="max-w-xl mx-auto p-6 bg-white rounded shadow">
//       <h2 className="text-2xl font-bold mb-4">Edit Expense</h2>
//       <form onSubmit={handleSubmit} className="space-y-4">
//         <input
//           type="text"
//           name="description"
//           value={form.description}
//           onChange={handleChange}
//           placeholder="Description"
//           className="w-full p-2 border rounded"
//         />
//         <input
//           type="number"
//           name="amount"
//           value={form.amount}
//           onChange={handleChange}
//           placeholder="Amount"
//           className="w-full p-2 border rounded"
//         />

//         <div>
//           <label className="font-semibold">Select Payer:</label>
//           <select
//             name="paidBy"
//             value={form.paidBy}
//             onChange={handleChange}
//             className="w-full p-2 border rounded"
//           >
//             <option value="">-- Select Payer --</option>
//             {groupMembers.map((user) => (
//               <option key={user.userid} value={user.userid}>
//                 {user.username}
//               </option>
//             ))}
//           </select>
//         </div>

//         <div>
//           <label className="font-semibold">Select Participants:</label>
//           {groupMembers.map((user) => (
//             <div key={user.userid}>
//               <label>
//                 <input
//                   type="checkbox"
//                   checked={form.participants.includes(user.userid)}
//                   onChange={() => handleParticipantsChange(user.userid)}
//                 />
//                 {user.username}
//               </label>
//             </div>
//           ))}
//         </div>

//         <div>
//           <label className="font-semibold">Split Type:</label>
//           <select
//             name="splitType"
//             value={form.splitType}
//             onChange={handleChange}
//             className="w-full p-2 border rounded border-orange-400"
//           >
//             <option value="EQUAL">EQUAL</option>
//             <option value="EXACT">EXACT</option>
//             <option value="PERCENTAGE">PERCENTAGE</option>
//           </select>
//         </div>

//         {(form.splitType === 'EXACT' || form.splitType === 'PERCENTAGE') && (
//           <div>
//             <label className="font-semibold">Enter Values:</label>
//             {form.participants.map((id, index) => {
//               const member = groupMembers.find((u) => u.userid === id);
//               return (
//                 <div key={id}>
//                   <label>{member?.username}:</label>
//                   <input
//                     type="number"
//                     value={form.values[index] || ''}
//                     onChange={(e) => handleValueChange(index, e.target.value)}
//                     className="w-full p-1 border rounded mb-1"
//                   />
//                 </div>
//               );
//             })}
//           </div>
//         )}

//         <button
//           type="submit"
//           className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
//         >
//           Update Expense
//         </button>
//       </form>
//     </div>
//   );
// }


import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import SaveIcon from '@mui/icons-material/Save';
import {
  Box, Typography, TextField, Select, MenuItem, FormControl, InputLabel,
  Button, Checkbox, FormControlLabel, Snackbar, Dialog, DialogTitle,
  DialogContent, DialogActions, CircularProgress, Alert
} from '@mui/material';
import api from '../../api/axios';


export default function EditExpense() {
  const { groupid, expenseid } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    description: '',
    amount: '',
    paidBy: '',
    participants: [],
    splitType: 'EQUAL',
    values: [],
  });

  const [groupMembers, setGroupMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [confirmOpen, setConfirmOpen] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [expenseRes, groupRes] = await Promise.all([
          api.get(`/group/${groupid}/expenses/${expenseid}`),
          api.get(`/group/${groupid}`),
        ]);

        const expense = expenseRes.data;
        const groupData = groupRes.data;

        const members = groupData.members.map((u) => ({
          userid: u.userId,
          username: u.username,
        }));
        setGroupMembers(members);

        const participantIds = expense.participants
          ? expense.participants.map((p) => p.userId)
          : members.map((m) => m.userid);

        setForm({
          description: expense.description || '',
          amount: expense.amount || '',
          paidBy: expense.userid?.userid || '',
          participants: participantIds,
          splitType: expense.splitType || 'EQUAL',
          values: expense.values || [],
        });

        setLoading(false);
      } catch (error) {
        console.error(error);
        setSnackbar({ open: true, message: 'Failed to load data', severity: 'error' });
      }
    };

    fetchData();
  }, [groupid, expenseid]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleParticipantsChange = (userId) => {
    setForm((prev) => ({
      ...prev,
      participants: prev.participants.includes(userId)
        ? prev.participants.filter((id) => id !== userId)
        : [...prev.participants, userId],
    }));
  };

  const handleValueChange = (index, newValue) => {
    const updated = [...form.values];
    updated[index] = newValue;
    setForm({ ...form, values: updated });
  };

  const handleUpdate = async () => {
    const payload = {
      description: form.description,
      amount: parseFloat(form.amount),
      paidBy: form.paidBy,
      participants: form.participants,
      splitType: form.splitType,
      values: form.splitType !== 'EQUAL' ? form.values.map(Number) : [],
    };

    try {
      await api.put(`/group/${groupid}/expenses/${expenseid}`, payload);
      setSnackbar({ open: true, message: 'Expense updated successfully', severity: 'success' });
      setTimeout(() => navigate(`/group/${groupid}`), 1000);
    } catch (err) {
      const message = err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.response?.data  || "Failed to update Expense";
      setSnackbar({ open: true, message, severity: 'error' });
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box maxWidth="600px" mx="auto" p={3} bgcolor="#f9f9f9" borderRadius={2} boxShadow={2}>
      <Typography variant="h5" fontWeight="bold" mb={3}>Edit Expense</Typography>
      <TextField
        fullWidth
        label="Description"
        name="description"
        value={form.description}
        onChange={handleChange}
        margin="normal"
      />
      <TextField
        fullWidth
        label="Amount"
        name="amount"
        type="number"
        value={form.amount}
        onChange={handleChange}
        margin="normal"
      />

      <FormControl fullWidth margin="normal">
        <InputLabel>Payer</InputLabel>
        <Select name="paidBy" value={form.paidBy} onChange={handleChange} label="Payer">
          <MenuItem value="">-- Select Payer --</MenuItem>
          {groupMembers.map((user) => (
            <MenuItem key={user.userid} value={user.userid}>{user.username}</MenuItem>
          ))}
        </Select>
      </FormControl>

      <Typography fontWeight="bold" mt={2}>Select Participants:</Typography>
      {groupMembers.map((user) => (
        <FormControlLabel
          key={user.userid}
          control={
            <Checkbox
              checked={form.participants.includes(user.userid)}
              onChange={() => handleParticipantsChange(user.userid)}
            />
          }
          label={user.username}
        />
      ))}

      <FormControl fullWidth margin="normal">
        <InputLabel>Split Type</InputLabel>
        <Select name="splitType" value={form.splitType} onChange={handleChange} label="Split Type">
          <MenuItem value="EQUAL">EQUAL</MenuItem>
          <MenuItem value="EXACT">EXACT</MenuItem>
          <MenuItem value="PERCENTAGE">PERCENTAGE</MenuItem>
        </Select>
      </FormControl>

      {(form.splitType === 'EXACT' || form.splitType === 'PERCENTAGE') && (
        <Box mt={1}>
          <Typography fontWeight="bold">Enter Values:</Typography>
          {form.participants.map((id, index) => {
            const member = groupMembers.find((u) => u.userid === id);
            return (
              <TextField
                key={id}
                fullWidth
                label={member?.username}
                type="number"
                value={form.values[index] || ''}
                onChange={(e) => handleValueChange(index, e.target.value)}
                margin="dense"
              />
            );
          })}
        </Box>
      )}

      <Box mt={4} display="flex" justifyContent="space-between">
  <Button
    variant="outlined"
    color="secondary"
    startIcon={<ArrowBackIcon />}
    onClick={() => navigate(-1)}
  >
    Back
  </Button>

  <Button
    variant="contained"
    color="primary"
    endIcon={<SaveIcon />}
    onClick={() => setConfirmOpen(true)}
  >
    Update
  </Button>
</Box>

      {/* Snackbar */}
      <Snackbar
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
          {snackbar.message}
        </Alert>
      </Snackbar>

      {/* Confirmation Dialog */}
      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Confirm Update</DialogTitle>
        <DialogContent>
          Are you sure you want to update this expense?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)}>Cancel</Button>
          <Button onClick={handleUpdate} color="primary">Yes, Update</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
