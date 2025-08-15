
// import React, { useEffect, useState } from 'react';
// import api from '../../api/axios';
// import { getUserFromToken } from '../../utils/jwtUtils';

// export default function SettlementTab({ groupid }) {
//   const [settlements, setSettlements] = useState([]);
//   const [activeTab, setActiveTab] = useState('ALL');
//   const user = getUserFromToken();

//   useEffect(() => {
//     if (!user?.id) return;

//     let endpoint = `/group/${groupid}/settlements`;

//     if (activeTab === 'PAID_BY_ME') {
//       endpoint = `/group/${groupid}/settlements/paidby/${user.id}`;
//     } else if (activeTab === 'PAID_TO_ME') {
//       endpoint = `/group/${groupid}/settlements/paidto/${user.id}`;
//     }

//     api.get(endpoint)
//       .then(res => setSettlements(res.data || []))
//       .catch(err => console.error('Error fetching settlements:', err));
//   }, [groupid, activeTab, user?.id]);

//   const formatDate = (dateString) => {
//     try {
//       return new Date(dateString).toLocaleString();
//     } catch (e) {
//       return '';
//     }
//   };

//   return (
//     <div>
//       <h3 className="text-xl font-semibold mb-4">Settlement History</h3>

//       <div className="mb-4 flex flex-wrap gap-2">
//         <button
//           className={`px-3 py-1 rounded ${activeTab === 'ALL' ? 'bg-blue-600 text-white' : 'bg-gray-200'}`}
//           onClick={() => setActiveTab('ALL')}
//         >
//           All Settlements
//         </button>
//         <button
//           className={`px-3 py-1 rounded ${activeTab === 'PAID_BY_ME' ? 'bg-green-600 text-white' : 'bg-gray-200'}`}
//           onClick={() => setActiveTab('PAID_BY_ME')}
//         >
//           Paid by Me
//         </button>
//         <button
//           className={`px-3 py-1 rounded ${activeTab === 'PAID_TO_ME' ? 'bg-yellow-500 text-white' : 'bg-gray-200'}`}
//           onClick={() => setActiveTab('PAID_TO_ME')}
//         >
//           Paid to Me
//         </button>
//       </div>

//       {settlements.length === 0 ? (
//         <p>No settlements found for this view.</p>
//       ) : (
//         <ul className="space-y-2">
//           {settlements.map((settle, index) => (
//             <li
//               key={index}
//               className="border p-3 rounded flex justify-between items-center"
//             >
//               <span>
//                 <strong>{settle.paidby?.username || `User#${settle.paidby?.userid}`}</strong> paid{' '}
//                 <strong>₹{settle.amount.toLocaleString()}</strong> to{' '}
//                 <strong>{settle.paidto?.username || `User#${settle.paidto?.userid}`}</strong>
//               </span>
//               <span className="text-sm text-gray-500">
//                 {formatDate(settle.date)}
//               </span>
//             </li>
//           ))}
//         </ul>
//       )}
//     </div>
//   );
// }

import React, { useEffect, useState } from 'react';
import {
  Tabs,
  Tab,
  Paper,
  Typography,
  Box,
  Card,
  CardContent,
  Stack,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  IconButton,
  Snackbar,
  Alert,
} from '@mui/material';
import ListAltIcon from '@mui/icons-material/ListAlt';
import ArrowCircleUpIcon from '@mui/icons-material/ArrowCircleUp';
import ArrowCircleDownIcon from '@mui/icons-material/ArrowCircleDown';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import api from '../../api/axios';
import { getUserFromToken } from '../../utils/jwtUtils';

export default function SettlementTab({ groupid }) {
  const [settlements, setSettlements] = useState([]);
  const [activeTab, setActiveTab] = useState('ALL');
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [selectedSettlement, setSelectedSettlement] = useState(null);
  const [editedAmount, setEditedAmount] = useState('');
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
  const [openSaveConfirmDialog, setOpenSaveConfirmDialog] = useState(false);

  const user = getUserFromToken();

  const tabOptions = [
    { label: 'All', value: 'ALL', icon: <ListAltIcon /> },
    { label: 'Paid by Me', value: 'PAID_BY_ME', icon: <ArrowCircleUpIcon /> },
    { label: 'Paid to Me', value: 'PAID_TO_ME', icon: <ArrowCircleDownIcon /> },
  ];

  const fetchSettlements = () => {
    if (!user?.id) return;

    let endpoint = `/group/${groupid}/settlements`;
    if (activeTab === 'PAID_BY_ME') {
      endpoint = `/group/${groupid}/settlements/paidby/${user.id}`;
    } else if (activeTab === 'PAID_TO_ME') {
      endpoint = `/group/${groupid}/settlements/paidto/${user.id}`;
    }

    api
      .get(endpoint)
      .then((res) => {
        const sorted = [...(res.data || [])].sort(
          (a, b) => new Date(b.date) - new Date(a.date)
        );
        setSettlements(sorted);
      })
      .catch((err) => console.error('Error fetching settlements:', err));
  };

  useEffect(() => {
    fetchSettlements();
  }, [groupid, activeTab, user?.id]);

  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleString();
    } catch (e) {
      return '';
    }
  };

  const handleDelete = (settlement) => {
    setSelectedSettlement(settlement);
    setOpenDeleteDialog(true);
  };

  const confirmDelete = () => {
    const { id } = selectedSettlement;
    api
      .delete(`/group/${groupid}/settlements/${id}`)
      .then(() => {
        setOpenDeleteDialog(false);
        fetchSettlements();
        setSnackbar({ open: true, message: 'Settlement deleted successfully!', severity: 'success' });
      })
      .catch((err) => {
        console.error('Error deleting settlement:', err);
        setOpenDeleteDialog(false);
        setSnackbar({ open: true, message: 'Failed to delete settlement.', severity: 'error' });
      });
  };

  const handleEdit = (settlement) => {
    setSelectedSettlement(settlement);
    setEditedAmount(settlement.amount);
    setOpenEditDialog(true);
  };

  // When user clicks Save in the edit dialog, open confirmation dialog
  const submitEdit = () => {
    setOpenSaveConfirmDialog(true);
  };

  // Actual API call to save after confirmation
  const confirmEditSave = () => {
    const { id, paidby, paidto } = selectedSettlement;
    const payload = {
      paidby: paidby.userid || paidby.id,
      paidto: paidto.userid || paidto.id,
      amount: parseFloat(editedAmount),
    };

    api
      .put(`/group/${groupid}/settlements/${id}`, payload)
      .then(() => {
        setOpenEditDialog(false);
        setOpenSaveConfirmDialog(false);
        fetchSettlements();
        setSnackbar({ open: true, message: 'Settlement updated successfully!', severity: 'success' });
      })
      .catch((err) => {
  const message =
    err?.response?.data?.message ||
    err?.response?.data?.error ||
    (typeof err?.response?.data === 'string' ? err.response.data : null) ||
    'Failed to update settlement.';
  setOpenSaveConfirmDialog(false);
  setSnackbar({ open: true, message, severity: 'error' });
});
  };

  return (
    <Box sx={{ p: 3  }}>
      <Typography variant="h5" fontWeight="bold" mb={3}>
        Settlement History
      </Typography>

      <Paper elevation={1} sx={{ mb: 4 }}>
        <Tabs
          value={activeTab}
          onChange={(e, newValue) => setActiveTab(newValue)}
          indicatorColor="primary"
          textColor="primary"
          variant="fullWidth"
          centered
        >
          {tabOptions.map((tab) => (
            <Tab
              key={tab.value}
              icon={tab.icon}
              iconPosition="start"
              label={tab.label}
              value={tab.value}
            />
          ))}
        </Tabs>
      </Paper>

      {settlements.length === 0 ? (
        <Typography color="text.secondary">
          No settlements found for this view.
        </Typography>
      ) : (
        <Stack spacing={2}>
          {settlements.map((settle, index) => (
            <Card key={index} variant="outlined" sx={{ p: 1.5 }}>
              <CardContent
                sx={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <Box>
                  <Typography>
                    <strong style={{ color: '#1976d2' }}>
                      {settle.paidby?.username || `User#${settle.paidby?.userid}`}
                    </strong>{' '}
                    paid{' '}
                    <strong style={{ color: '#2e7d32' }}>
                      ₹{settle.amount.toLocaleString()}
                    </strong>{' '}
                    to{' '}
                    <strong style={{ color: '#f9a825' }}>
                      {settle.paidto?.username || `User#${settle.paidto?.userid}`}
                    </strong>
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {formatDate(settle.date)}
                  </Typography>
                </Box>

                <Box>
                  <IconButton onClick={() => handleEdit(settle)} color="primary">
                    <EditIcon />
                  </IconButton>
                  <IconButton onClick={() => handleDelete(settle)} color="error">
                    <DeleteIcon />
                  </IconButton>
                </Box>
              </CardContent>
            </Card>
          ))}
        </Stack>
      )}

      {/* Delete Confirmation Dialog */}
      <Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>Are you sure you want to delete this settlement?</DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
          <Button color="error" onClick={confirmDelete}>
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)}>
        <DialogTitle>Edit Settlement</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2, pt: 5,  }}>
         <Box sx={{ pt: 1 }}>
          <TextField
            label="Paid By"
            value={
              selectedSettlement?.paidby?.username || `User#${selectedSettlement?.paidby?.userid}`
            }
            InputProps={{ readOnly: true }}
          /></Box>
          <TextField
            label="Paid To"
            value={
              selectedSettlement?.paidto?.username || `User#${selectedSettlement?.paidto?.userid}`
            }
            InputProps={{ readOnly: true }}
          />
          <TextField
            label="Amount"
            type="number"
            value={editedAmount}
            onChange={(e) => setEditedAmount(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenEditDialog(false)}>Cancel</Button>
          <Button onClick={submitEdit} variant="contained">
            Save
          </Button>
        </DialogActions>
      </Dialog>

      {/* Save Confirmation Dialog */}
      <Dialog open={openSaveConfirmDialog} onClose={() => setOpenSaveConfirmDialog(false)}>
        <DialogTitle>Confirm Save</DialogTitle>
        <DialogContent>Are you sure you want to save changes to this settlement?</DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenSaveConfirmDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={confirmEditSave}>
            Save
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for success/failure messages */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
           variant="filled"
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}


