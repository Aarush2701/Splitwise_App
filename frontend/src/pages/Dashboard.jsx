import React, { useEffect, useState } from 'react';
import {
  Box,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  Paper,
  useMediaQuery,
  Toolbar,
  AppBar,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import DashboardIcon from '@mui/icons-material/Dashboard';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import EditIcon from '@mui/icons-material/Edit';
import LogoutIcon from '@mui/icons-material/Logout';
import api from '../api/axios';
import { useNavigate } from 'react-router-dom';
import Profile from '../components/Profile';
import UpdateProfile from '../components/UpdateProfile';
import { getUserFromToken } from '../utils/jwtUtils';
import Groups from './Groups';

export default function Dashboard() {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [activeSection, setActiveSection] = useState('groups');
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const isMobile = useMediaQuery('(max-width: 768px)');

  const userData = getUserFromToken();
  const drawerWidth = 240;

  useEffect(() => {
    if (!userData?.id) {
      setError('Invalid token or user not logged in');
      return;
    }

    api.get(`/users/${userData.id}`)
      .then(res => {
        setUser(res.data);
      })
      .catch(err => {
        console.error('Failed to fetch user data:', err);
      });
  }, []);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleSectionChange = (section) => {
    setActiveSection(section);
    if (isMobile) setMobileOpen(false); // close drawer on mobile after selection
  };

  const drawer = (
    <Box sx={{ width: drawerWidth, bgcolor: '#1e293b', height: '100%', color: '#fff' }}>
      <Box sx={{ p: 2 }}>
        <Typography variant="h6" fontWeight="bold">
          <DashboardIcon sx={{ mr: 1, mb: -0.5 }} />
          Dashboard
        </Typography>
      </Box>
      <List>
        <ListItem disablePadding>
          <ListItemButton onClick={() => handleSectionChange('profile')}>
            <ListItemIcon>
              <AccountCircleIcon sx={{ color: '#fff' }} />
            </ListItemIcon>
            <ListItemText primary="Profile" />
          </ListItemButton>
        </ListItem>

        <ListItem disablePadding>
          <ListItemButton onClick={() => handleSectionChange('update')}>
            <ListItemIcon>
              <EditIcon sx={{ color: '#fff' }} />
            </ListItemIcon>
            <ListItemText primary="Update Details" />
          </ListItemButton>
        </ListItem>

        <ListItem disablePadding>
          <ListItemButton
            sx={{ color: '#f87171' }}
            onClick={() => {
              localStorage.removeItem('token');
              navigate('/login');
            }}
          >
            <ListItemIcon>
              <LogoutIcon sx={{ color: '#f87171' }} />
            </ListItemIcon>
            <ListItemText primary="Logout" />
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  );

  const renderContent = () => {
    switch (activeSection) {
      case 'groups':
        return <Groups />;
      case 'profile':
        return <Profile onBack={() => setActiveSection('groups')} />;
      case 'update':
        return <UpdateProfile onBack={() => setActiveSection('groups')} />;
      default:
        return null;
    }
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      {/* Show AppBar only on mobile */}
      {isMobile && (
        <AppBar position="static" sx={{ bgcolor: '#1e293b' }}>
          <Toolbar>
            <IconButton
              color="inherit"
              edge="start"
              onClick={handleDrawerToggle}
              sx={{ mr: 2 }}
            >
              <MenuIcon />
            </IconButton>
            <Typography variant="h6" noWrap>
              SplitWise Dashboard
            </Typography>
          </Toolbar>
        </AppBar>
      )}

      {/* Body layout */}
      <Box sx={{ display: 'flex', flex: 1, overflow: 'hidden' }}>
        {/* Drawer */}
        <Drawer
          variant={isMobile ? 'temporary' : 'permanent'}
          open={isMobile ? mobileOpen : true}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            width: drawerWidth,
            flexShrink: 0,
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              boxSizing: 'border-box',
            },
          }}
        >
          {drawer}
        </Drawer>

        {/* Main content */}
        <Box
          component="main"
          sx={{
            flexGrow: 1,
            p: { xs: 1, sm: 2 },
            overflowY: 'auto',
            bgcolor: '#f8fafc',
          }}
        >
          <Paper
            elevation={2}
            sx={{
              p: { xs: 2, sm: 3 },
              minHeight: '100%',
              borderRadius: 2,
            }}
          > 
            {renderContent()}
          </Paper>
        </Box>
      </Box>
    </Box>
  );
}
