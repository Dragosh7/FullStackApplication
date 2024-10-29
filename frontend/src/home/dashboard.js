import * as React from 'react';
import PropTypes from 'prop-types';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import PeopleIcon from '@mui/icons-material/People';
import DevicesIcon from '@mui/icons-material/Devices';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LogoutIcon from '@mui/icons-material/Logout';
import AdUnitsIcon from '@mui/icons-material/AdUnits';
import { Link, Routes, Route, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

import Persons from '../person/admin-person'; 
import Devices from '../device/device'; 
import MyDevices from '../device/myDevice'; 
import EditAccount from '../person/editAccount'; 
import Logout from '../authentication/login'; 
import NotFound from './notFound'; 

// Navigation Configuration
const NAVIGATION = [
  {
    segment: 'persons',
    title: 'Persons',
    icon: <PeopleIcon />,
    link: '/persons',
  },
  {
    segment: 'devices',
    title: 'Devices',
    icon: <DevicesIcon />,
    link: '/devices',
  },
  {
    segment: 'myDevices',
    title: 'My Devices',
    icon: <AdUnitsIcon />,
    link: '/mydevices',
  },
  {
    segment: 'edit-account',
    title: 'Edit Account',
    icon: <AccountCircleIcon />,
    link: '/edit-account',
  },
  {
    segment: 'logout',
    title: 'Log Out',
    icon: <LogoutIcon />,
    link: '/login', // Update this to your login route
  },
];

// Define theme
const demoTheme = createTheme({
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 600,
      lg: 1200,
      xl: 1536,
    },
  },
});

function DashboardLayoutBranding(props) {
  const { window } = props;
  const navigate = useNavigate();
  const demoWindow = window !== undefined ? window() : undefined;

  useEffect(() => {
    const role = localStorage.getItem('role');
    if (!role) {
      // If no role is found, redirect to the login page
      navigate('/login');
    }
  }, [navigate]);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <ThemeProvider theme={demoTheme}>
      <Box display="flex" sx={{ height: '100vh' }}>
        {/* Navigation Sidebar */}
        <Box
          sx={{
            width: '240px',
            p: 2,
            borderRight: '1px solid #e0e0e0',
            backgroundColor: '#f4f4f4',
          }}
        >
          <List>
            {NAVIGATION.map((item) => (
              <ListItem
                button
                component={item.segment === 'logout' ? 'div' : Link} // Make logout a div to avoid routing
                key={item.title}
                onClick={item.segment === 'logout' ? handleLogout : undefined} // Call handleLogout for logout
                to={item.segment !== 'logout' ? item.link : undefined} // Set link only if not logout
              >
                {item.icon}
                <ListItemText primary={item.title} />
              </ListItem>
            ))}
          </List>
        </Box>

        {/* Main Content Area */}
        <Box
          sx={{
            flex: 1,
            p: 4,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            textAlign: 'center',
          }}
        >
          {/* Page Content */}
          <Routes>
            <Route path="/persons" element={<Persons />} />
            <Route path="/devices" element={<Devices />} />
            <Route path="/mydevices" element={<MyDevices />} />
            <Route path="/edit-account" element={<EditAccount />} />
            <Route path="/login" element={<Logout />} />
            <Route path="*" element={<NotFound  />} />


          </Routes>
        </Box>
      </Box>
    </ThemeProvider>
  );
}

DashboardLayoutBranding.propTypes = {
  window: PropTypes.func,
};

export default DashboardLayoutBranding;
