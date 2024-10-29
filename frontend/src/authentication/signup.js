import * as React from 'react';
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Stack from '@mui/material/Stack';
import CssBaseline from '@mui/material/CssBaseline';
import Link from '@mui/material/Link';
import { styled } from '@mui/material/styles';
import { postPerson } from '../person/api/person-api'; 

const GradientBackground = styled('div')(({ theme }) => ({
    background: 'linear-gradient( 93.2deg,  rgba(24,95,246,1) 14.4%, rgba(27,69,166,1) 90.8% );', 
    height: '100vh',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
  }));

  const Card = styled('div')(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(6),
    gap: theme.spacing(7),
    margin: 'auto',
    backgroundColor: '#c2e9fb',
    borderRadius: '42px', // Rounded corners for elegance
    border: '16px solid', // Border width,
    borderBlockColor: "#0b131d",
    boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)', // Soft and larger shadow for depth
    transition: 'all 0.3s ease', // Smooth hover effect
    '&:hover': {
      boxShadow: '0 15px 30px rgba(0, 0, 0, 0.2)', // Slightly larger shadow on hover
    },
    [theme.breakpoints.up('sm')]: {
      width: '450px',
    },
  }));
  

export default function SignUp() {
  const [open, setOpen] = React.useState(false);
  const [snackbarMessage, setSnackbarMessage] = React.useState('');
  const [severity, setSeverity] = React.useState('success'); // success or error

  const handleSubmit = (event) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const age = parseInt(data.get('age'), 10);

    // Age validation: must be between 18 and 100
    if (age < 18 || age > 100) {
      setSnackbarMessage('Error: Age is not valid. It must be between 18 and 100.');
      setSeverity('error');
      setOpen(true);
      return; // Stop submission if age is not valid
    }

    const user = {
      name: data.get('name'),
      address: data.get('address'),
      age: age,
      password: data.get('password'),
      role: 'user',
    };

    postPerson(user, (result, status) => {
      if (status === 200) {
        setSnackbarMessage('Signup successful!');
        setSeverity('success');
        setOpen(true);
      } else if (status === 409) {
        let errorResponse;
        try {
          errorResponse = JSON.parse(result);
        } catch (error) {
          errorResponse = null; // If parsing fails, handle this case
        }

        if (errorResponse && errorResponse.resource) {
          setSnackbarMessage(`Error: ${errorResponse.resource}`);
        } else {
          setSnackbarMessage('Error: User already exists.');
        }
        setSeverity('error');
        setOpen(true);
      } else if (status === 500) {
        setSnackbarMessage('Server error occurred. Please try again later.');
        setSeverity('error');
        setOpen(true);
      } else {
        setSnackbarMessage('Signup failed. Please try again.');
        setSeverity('error');
        setOpen(true);
      }
    });
  };

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setOpen(false);
  };

  return (
    <>
      <CssBaseline />
      <GradientBackground>
      <Stack
        direction="column"
        justifyContent="center"
        alignItems="center"
        sx={{ height: '100vh' }}
      >
        <Card>
          <Typography component="h1" variant="h5" align="center">
            Sign up
          </Typography>
          <Box
            component="form"
            onSubmit={handleSubmit}
            sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
          >
            <TextField
              label="Full Name"
              id="name"
              name="name"
              required
              fullWidth
            />
            <TextField label="Address" id="address" name="address" required fullWidth />
            <TextField
              label="Age"
              id="age"
              name="age"
              type="number"
              required
              fullWidth
            />
            <TextField
              label="Password"
              id="password"
              name="password"
              type="password"
              required
              fullWidth
            />
            <Button type="submit" fullWidth variant="contained">
              Sign up
            </Button>
            <Typography sx={{ textAlign: 'center' }}>
                            Have an account?{' '}
                            <span>
                                <Link
                                    href="/login"
                                    variant="body2"
                                >
                                    Log In
                                </Link>
                            </span>
                        </Typography>
          </Box>
        </Card>

        {/* Snackbar for notifications */}
        <Snackbar
          open={open}
          autoHideDuration={6000}
          onClose={handleClose}
          anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        >
          <Alert onClose={handleClose} severity={severity} variant="filled" sx={{ width: '100%' }}>
            {snackbarMessage}
          </Alert>
        </Snackbar>
      </Stack>
      </GradientBackground>
    </>
  );
}
