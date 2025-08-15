// import React, { useEffect, useState } from 'react';
// import api from '../../api/axios';

// export default function GroupMembersTab({ groupid }) {
//   const [members, setMembers] = useState([]);
//   const [newUserId, setNewUserId] = useState('');
//   const [error, setError] = useState(null);
//   const [success, setSuccess] = useState(null);

//   const loadMembers = () => {
//     api.get(`/group/${groupid}`)
//       .then(res => {
//         setMembers(res.data.members || []);
//         setError(null);
//       })
//       .catch(err => {
//         setError('Failed to load group members');
//         console.error(err);
//       });
//   };

//   useEffect(() => {
//     loadMembers();
//   }, [groupid]);

//   const handleAddMember = async () => {
//   if (!newUserId) return; // newUserId now holds an email

//   try {
//     // Step 1: Resolve email to userId
//     const resolveRes = await api.post('/users/resolve-ids', {
//       emails: [newUserId.trim()]
//     });

//     const resolvedId = resolveRes.data.userIds[0]; // single email, so take first id

//     // Step 2: Add the resolved userId to the group
//     await api.put(`/group/${groupid}/users/${resolvedId}`);

//     setSuccess(`User ${newUserId} added to group`);
//     setNewUserId('');
//     setError(null);
//     loadMembers();
//   } catch (err) {
//     const message =
//       err?.response?.data?.message ||
//       err?.response?.data?.error ||
//       err?.response?.data ||
//       'Failed to add user';
//     setError(message);
//     setSuccess(null);
//   }
// };

//   const handleRemoveMember = (userId) => {
//     const confirmDelete = window.confirm(`Are you sure you want to remove user ${userId} from the group?`);
//     if (!confirmDelete) return;

//     api.delete(`/group/${groupid}/users/${userId}`)
//       .then(() => {
//         setSuccess(`User ${userId} removed from group`);
//         setError(null);
//         loadMembers();
//       })
//       .catch(err => {
//         const message = err?.response?.data?.message ||
//       err?.response?.data?.error ||
//       err?.response?.data || 'Failed to remove user';
//         setError(message);
//         setSuccess(null);
//       });
//   };

//   return (
//     <div>
//       <h3 className="text-xl font-semibold mb-2">Group Members</h3>

//       {error && <p className="text-red-500 mb-2">{error}</p>}
//       {success && <p className="text-green-600 mb-2">{success}</p>}

//       <div className="flex items-center space-x-2 mb-4">
//   <input
//     type="email"
//     placeholder="Enter user email"
//     value={newUserId}
//     onChange={(e) => setNewUserId(e.target.value)}
//     className="border px-2 py-1 rounded w-half"
//   />
//   <button
//     onClick={handleAddMember}
//     className="bg-blue-600 text-white px-4 py-1 rounded hover:bg-blue-700"
//   >
//     Add Member
//   </button>
// </div>

//       {members.length === 0 ? (
//         <p>No members found.</p>
//       ) : (
//         <ul className="space-y-2">
//           {members.map(member => (
//             <div key={member.id} className="flex justify-between p-2 border rounded mb-2">
//               <span>{member.username}</span>
//               <button onClick={() => handleRemoveMember(member.userId)} className="text-red-500">Remove</button>
//               </div>
//             ))}
//         </ul>
//       )}
//     </div>
//   );
// }

import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  IconButton,
  List,
  ListItem,
  ListItemText,
  Snackbar,
  TextField,
  Typography,
  Alert,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/PersonAdd';
import api from '../../api/axios';

export default function GroupMembersTab({ groupid }) {
  const [members, setMembers] = useState([]);
  const [newUserEmail, setNewUserEmail] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
  const [userToRemove, setUserToRemove] = useState(null);

  const loadMembers = () => {
    api.get(`/group/${groupid}`)
      .then(res => {
        setMembers(res.data.members || []);
      })
      .catch(() => {
        setSnackbar({ open: true, message: 'Failed to load group members', severity: 'error' });
      });
  };

  useEffect(() => {
    loadMembers();
  }, [groupid]);

  const handleAddMember = async () => {
    if (!newUserEmail.trim()) return;

    try {
      const resolveRes = await api.post('/users/resolve-ids', {
        emails: [newUserEmail.trim()]
      });
      const resolvedId = resolveRes.data.userIds[0];

      await api.put(`/group/${groupid}/users/${resolvedId}`);
      setSnackbar({ open: true, message: `User ${newUserEmail} added`, severity: 'success' });
      setNewUserEmail('');
      loadMembers();
    } catch (err) {
      const message = err?.response?.data?.message ||
      err?.response?.data?.error ||
      err?.response?.data || 'Failed to add user';
      setSnackbar({ open: true, message, severity: 'error' });
    }
  };

  const handleOpenConfirmDialog = (member) => {
    setUserToRemove(member);
    setConfirmDialogOpen(true);
  };

  const handleCloseConfirmDialog = () => {
    setConfirmDialogOpen(false);
    setUserToRemove(null);
  };

  const handleConfirmRemove = async () => {
    if (!userToRemove) return;

    try {
      await api.delete(`/group/${groupid}/users/${userToRemove.userId}`);
      setSnackbar({ open: true, message: `User ${userToRemove.username} removed`, severity: 'success' });
      loadMembers();
    } catch (err) {
      const message = err?.response?.data?.message || err?.response?.data?.error || err?.response?.data || 'Failed to remove user';
      setSnackbar({ open: true, message, severity: 'error' });
    } finally {
      handleCloseConfirmDialog();
    }
  };

  const handleCloseSnackbar = () => setSnackbar({ ...snackbar, open: false });

  return (
    <Box mt={2}>
      <Typography variant="h6" gutterBottom>
        Group Members
      </Typography>

      <Box display="flex" gap={2} mb={2}>
        <TextField
          label="User Email"
          variant="outlined"
          size="small"
          value={newUserEmail}
          onChange={(e) => setNewUserEmail(e.target.value)}
          fullWidth
        />
        <Button
          variant="contained"
          color="primary"
          onClick={handleAddMember}
          startIcon={<AddIcon />}
        >
          Add
        </Button>
      </Box>

      {members.length === 0 ? (
        <Typography>No members found.</Typography>
      ) : (
        <Paper variant="outlined">
          <List dense>
            {members.map((member) => (
              <ListItem
                key={member.userId}
                secondaryAction={
                  <IconButton
                    edge="end"
                    aria-label="delete"
                    onClick={() => handleOpenConfirmDialog(member)}
                  >
                    <DeleteIcon color="error" />
                  </IconButton>
                }
              >
                <ListItemText primary={member.username} />
              </ListItem>
            ))}
          </List>
        </Paper>
      )}

      {/* Confirmation Dialog */}
      <Dialog
        open={confirmDialogOpen}
        onClose={handleCloseConfirmDialog}
      >
        <DialogTitle>Remove Member</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to remove <strong>{userToRemove?.username}</strong> from the group?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseConfirmDialog}>Cancel</Button>
          <Button color="error" onClick={handleConfirmRemove}>
            Remove
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar Notification */}
      <Snackbar
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
      >
        <Alert severity={snackbar.severity} onClose={handleCloseSnackbar} variant="filled">
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}
