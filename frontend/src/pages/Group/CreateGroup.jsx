
// import React, { useState } from 'react';
// import api from '../../api/axios';
// import { useNavigate } from 'react-router-dom';


// export default function CreateGroup({ onGroupCreated }) {
//   const [groupname, setGroupName] = useState('');
//   const [emails, setEmails] = useState('');
//   const [error, setError] = useState('');
//   const navigate = useNavigate();

//   const handleCreateGroup = async () => {
//     try {
//       const emailList = emails.split(',').map(email => email.trim());

//       // Call API to get userIds from emails
//       const resolveRes = await api.post('/users/resolve-ids', {
//         emails: emailList
//       });

//       const userIds = resolveRes.data.userIds;

//       // Call API to create group
//       await api.post('/group', {
//         groupname,
//         userIds
//       });

//       alert('Group created successfully!');
//       setGroupName('');
//       setEmails('');
//       if (onGroupCreated) onGroupCreated(); // optional callback
//       navigate('/dashboard');
//     } catch (err) {
//       setError(err.response?.data?.message || 'Something went wrong');
//       console.error('Group creation error:', err);
//     }
//   };

//   return (
//     <div className="p-4 border rounded shadow w-full max-w-md mx-auto">
//       <h2 className="text-xl font-bold mb-2">Create Group</h2>

//       <input
//         type="text"
//         placeholder="Group Name"
//         value={groupname}
//         onChange={(e) => setGroupName(e.target.value)}
//         className="w-full border p-2 mb-2 rounded"
//       />

//       <textarea
//         placeholder="Enter user emails (comma separated)"
//         value={emails}
//         onChange={(e) => setEmails(e.target.value)}
//         className="w-full border p-2 mb-2 rounded"
//         rows={3}
//       />

//       {error && <div className="text-red-600 mb-2">{error}</div>}

//       <div className="flex space-x-4 mb-6">
//       <button
//         onClick={handleCreateGroup}
//         className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
//       >
//         Create Group
//       </button>
//       <button onClick={() => navigate(-1)} className="bg-orange-500 text-white px-4 py-2 rounded ">
//           Back
//         </button></div>
//     </div>
//   );
// }


import React, { useState } from 'react';
import api from '../../api/axios';
import { useNavigate } from 'react-router-dom';
import {
  Snackbar,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Alert,
  TextField,
  Typography,
  Box,
  Slide,
} from '@mui/material';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { keyframes } from '@emotion/react';

// Animation keyframes
const fadeIn = keyframes`
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
`;

const popIn = keyframes`
  0% { transform: scale(0.9); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
`;

export default function CreateGroup({ onGroupCreated }) {
  const [groupname, setGroupName] = useState('');
  const [emails, setEmails] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [dialogOpen, setDialogOpen] = useState(false);
  const navigate = useNavigate();

  const showSnackbar = (message, severity = 'success') => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCreateGroup = async () => {
    try {
      const emailList = emails.split(',').map(email => email.trim());
      const resolveRes = await api.post('/users/resolve-ids', { emails: emailList });
      const userIds = resolveRes.data.userIds;

      await api.post('/group', { groupname, userIds });

      setGroupName('');
      setEmails('');
      showSnackbar('🎉 Group created successfully!', 'success');
      if (onGroupCreated) onGroupCreated();
      navigate('/dashboard');
    } catch (err) {
      const errorMessage = err?.response?.data?.message ||
          err?.response?.data?.error ||
          err?.response?.data || 'Something went wrong';
      showSnackbar(errorMessage, 'error');
      console.error('Group creation error:', err);
    }
  };

  const handleDialogConfirm = () => {
    setDialogOpen(false);
    handleCreateGroup();
  };

  return (
    <Box
      className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-10"
      sx={{ animation: `${fadeIn} 0.5s ease-out` }}
    >
      <Box
        className="bg-white rounded-xl shadow-md p-6 w-full"
        sx={{
          maxWidth: '26rem',
          border: '1px solid #e0e0e0',
          animation: `${fadeIn} 0.6s ease-out`,
        }}
      >
        <Typography
          variant="h5"
          align="center"
          gutterBottom
          sx={{
            fontWeight: 700,
            color: '#2f3542',
            animation: `${popIn} 0.4s ease-in-out`,
          }}
        >
          ➕ Create a New Group
        </Typography>

        <TextField
          label="Group Name"
          variant="outlined"
          fullWidth
          value={groupname}
          onChange={(e) => setGroupName(e.target.value)}
          sx={{ mb: 3 }}
        />

        <TextField
          label="User Emails (comma-separated)"
          variant="outlined"
          fullWidth
          multiline
          rows={3}
          value={emails}
          onChange={(e) => setEmails(e.target.value)}
          sx={{ mb: 2 }}
        />

        <Box display="flex" justifyContent="space-between" mt={2}>
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
            endIcon={<GroupAddIcon />}
            onClick={() => setDialogOpen(true)}
            disabled={!groupname || !emails}
          >
            Create
          </Button>
        </Box>
      </Box>

      {/* Snackbar for success and error */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3500}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        TransitionComponent={(props) => <Slide {...props} direction="down" />}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>

      {/* Confirmation Dialog */}
      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
        <DialogTitle>Confirm Group Creation</DialogTitle>
        <DialogContent>
          Are you sure you want to create this group with the provided name and emails?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)} color="error">
            Cancel
          </Button>
          <Button onClick={handleDialogConfirm} color="primary" variant="contained">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
