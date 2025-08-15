


import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';

import ExpensesTab from '../components/group/ExpensesTab';
import GroupMembersTab from '../components/group/GroupMembersTab';
import BalanceTab from '../components/group/BalanceTab';
import SettlementsTab from '../components/group/SettlementsTab';

import DeleteIcon from '@mui/icons-material/Delete';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { useLocation } from 'react-router-dom';

import {
  AppBar,
  Box,
  Tabs,
  Tab,
  Typography,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Snackbar,
  Alert,
  Paper,
  Divider,
  Fade
} from '@mui/material';

export default function GroupDetails() {
  const { groupid } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [activeTab, setActiveTab] = useState('expenses');
  const [group, setGroup] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

  useEffect(() => {
    const fetchGroupDetails = async () => {
      try {
        const res = await api.get(`/group/${groupid}`);
        setGroup(res.data);
      } catch (err) {
        setSnackbar({
          open: true,
          message: err?.response?.data?.message || err?.response?.data?.error || err?.response?.data || "Failed to fetch group details",
          severity: 'error'
        });
      }
    };
    fetchGroupDetails();
  }, [groupid]);

  useEffect(() => {
  if (location.state?.tab) {
    setActiveTab(location.state.tab);
  }
}, [location.state?.tab]);

  const handleTabChange = (event, newValue) => setActiveTab(newValue);
  const handleDeleteConfirm = () => setOpenDialog(true);

  const handleDeleteGroup = async () => {
    setOpenDialog(false);
    try {
      await api.delete(`/group/${groupid}`);
      setSnackbar({ open: true, message: 'Group deleted successfully.', severity: 'success' });
      navigate('/dashboard');
    } catch (err) {
      setSnackbar({
        open: true,
        message: err?.response?.data?.message || err?.response?.data?.error || err?.response?.data || "Failed to delete group",
        severity: 'error'
      });
    }
  };

  return (
    <Fade in timeout={500}>
      <Paper elevation={3} sx={{ p: 4, maxWidth: '1000px', mx: 'auto', mt: 4 }}>
        <Typography variant="h4" fontWeight="bold" color="primary.main" gutterBottom>
          Group: {group?.groupname || 'Loading...'}
        </Typography>

        <Divider sx={{ mb: 3 }} />

        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          textColor="primary"
          indicatorColor="primary"
          sx={{ mb: 2 }}
          variant="scrollable"
          scrollButtons
          allowScrollButtonsMobile
        >
          <Tab label="Expenses" value="expenses" />
          <Tab label="Group Members" value="members" />
          <Tab label="Balance" value="balance" />
          <Tab label="Settlements" value="settlements" />
        </Tabs>

        <Box sx={{ mt: 2, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
          <Button
  variant="outlined"
  color="error"
  startIcon={<DeleteIcon />}
  onClick={handleDeleteConfirm}
>
  Delete Group
</Button>

          <Button
            variant="outlined"
            color="primary"
            startIcon={<ArrowBackIcon />}
            onClick={() => navigate('/dashboard')}
            sx={{ textTransform: 'none' }}
          >
            Back to Dashboard
          </Button>
        </Box>

        <Box sx={{ mt: 4 }}>
          {activeTab === 'expenses' && <ExpensesTab groupid={groupid} />}
          {activeTab === 'members' && <GroupMembersTab groupid={groupid} />}
          {activeTab === 'balance' && <BalanceTab groupid={groupid} />}
          {activeTab === 'settlements' && <SettlementsTab groupid={groupid} />}
        </Box>

        {/* Delete Confirmation Dialog */}
        <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
          <DialogTitle>Delete Group</DialogTitle>
          <DialogContent>
            <DialogContentText>
              Are you sure you want to delete this group? This action cannot be undone.
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenDialog(false)} color="primary">Cancel</Button>
            <Button onClick={handleDeleteGroup} color="error" autoFocus>
              Delete
            </Button>
          </DialogActions>
        </Dialog>

        {/* Snackbar Notification */}
        <Snackbar
          open={snackbar.open}
          autoHideDuration={4000}
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        >
          <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })} variant="filled">
            {snackbar.message}
          </Alert>
        </Snackbar>
      </Paper>
    </Fade>
  );
}
