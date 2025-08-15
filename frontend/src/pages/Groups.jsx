

import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Grid, Card, Button, Avatar,
  Toolbar, Paper
} from '@mui/material';
import {
  AddCircleOutline as AddCircleOutlineIcon,
  Visibility as VisibilityIcon,
  Groups as GroupsIcon
} from '@mui/icons-material';
import { useMediaQuery } from '@mui/material';

import { getUserFromToken } from '../utils/jwtUtils';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

export default function Groups() {
  const user = getUserFromToken();
  const [groups, setGroups] = useState([]);
  const [userDetails, setUserDetails] = useState(null);
  const navigate = useNavigate();
  const isMobile = useMediaQuery('(max-width:768px)');


  useEffect(() => {
    if (!user?.id) return;
    fetchUserDetails(user.id);
    fetchUserGroups(user.id);
  }, [user?.id]);

  const fetchUserDetails = async (id) => {
    try {
      const res = await api.get(`/users/${id}`);
      setUserDetails(res.data);
    } catch (err) {
      console.error('Failed to fetch user details:', err);
    }
  };

  const fetchUserGroups = async (id) => {
    try {
      const res = await api.get(`/group/users/${id}`);
      setGroups(res.data || []);
    } catch (err) {
      console.error('Failed to fetch groups:', err);
    }
  };

  return (
    <Box sx={{ backgroundColor: '#f2f6fb', minHeight: '100vh', py: 4 }}>
       <Paper elevation={3} sx={{ mx: 3, mb: 4, pb: isMobile ? 2 : 0 }}>
        <Toolbar
  sx={{
    px: 3,
    flexDirection: isMobile ? 'column' : 'row',
    alignItems: isMobile ? 'flex-start' : 'center',
    justifyContent: 'space-between',
    gap: isMobile ? 1.5 : 0,
  }}
>
  <Typography variant="h6" color="primary">
    Welcome: <b>{userDetails?.username || 'Loading...'}</b>
  </Typography>
  <Button
    variant="contained"
    startIcon={<AddCircleOutlineIcon />}
    sx={{
      background: 'linear-gradient(135deg, #6EE7B7 0%, #3B82F6 100%)',
      color: 'white',
      '&:hover': {
        background: 'linear-gradient(135deg, #3B82F6 0%, #6EE7B7 100%)'
      }
    }}
    onClick={() => navigate('/create-group')}
  >
    Create Group
  </Button>
</Toolbar>

      </Paper>

      <Box px={4}>
        <Typography variant="h5" gutterBottom color="text.primary">
          <GroupsIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
          Your Groups
        </Typography>

        <Grid columns={12} sx={{ mt: 2 }}>
  {groups.map(group => (
    <Grid span={12} sm={6} md={4} key={group.groupid} sx={{ mb: 2 }}>

              <Card sx={{ display: 'flex', alignItems: 'center', px: 2, py: 1, boxShadow: 2 }}>
                <Avatar sx={{ bgcolor: 'primary.main', mr: 2 }}>
                  {group.groupname[0].toUpperCase()}
                </Avatar>
                <Box sx={{ flexGrow: 1 }}>
                  <Typography variant="subtitle1">{group.groupname}</Typography>
                </Box>
                <Button
                  variant="contained"
                  color="primary"
                  size="small"
                  startIcon={<VisibilityIcon />}
                  onClick={() => navigate(`/group/${group.groupid}`)}
                >
                  View
                </Button>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Box>
  );
}
