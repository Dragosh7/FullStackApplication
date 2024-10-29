import * as React from 'react';
import { useNavigate } from 'react-router-dom'; 
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import { styled } from '@mui/material/styles';
import { login } from '../person/api/person-api'; 

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


const SignInContainer = styled(Stack)(({ theme }) => ({
    height: '100vh',
    padding: theme.spacing(2),
}));

export default function SignIn() {
    const [usernameError, setUsernameError] = React.useState(false);
    const [usernameErrorMessage, setUsernameErrorMessage] = React.useState('');
    const [passwordError, setPasswordError] = React.useState(false);
    const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
    const [responseMessage, setResponseMessage] = React.useState(''); 
    const navigate = useNavigate();

    React.useEffect(() => {
      const storedName = localStorage.getItem('name');
      if (storedName) {
          setResponseMessage(`Welcome back, ${storedName}!`);
          navigate('/mydevices');
      }
  }, [navigate]);
  

    const handleSubmit = (event) => {
        event.preventDefault(); // Prevent default form submission
        if (usernameError || passwordError) return; // Validate inputs

        const data = new FormData(event.currentTarget);
        const user = {
            username: data.get('username'), 
            password: data.get('password'),
        };

        login(user, (result, status) => {
              console.log('Login result:', result); 
            if (status === 200) {
                  const {id, name, role } = JSON.parse(result); // Extract fields from parsed result
      
                  // Store user details in local storage
                  localStorage.setItem('name', name);
                  localStorage.setItem('id', id);
                  localStorage.setItem('role', role);
        

                setResponseMessage(`Login successful! Welcome, ${name}`);
                navigate('/mydevices');
                //window.reload();
            } else {
                setResponseMessage('Login failed. Please try again.'); 
            }
        });
    };

    const validateInputs = () => {
        const username = document.getElementById('username');
        const password = document.getElementById('password');

        let isValid = true;

        // Validate username
        if (!username.value) {
            setUsernameError(true);
            setUsernameErrorMessage('Please enter your username.');
            isValid = false;
        } else {
            setUsernameError(false);
            setUsernameErrorMessage('');
        }

        // Validate password
        if (!password.value || password.value.length < 2) {
            setPasswordError(true);
            setPasswordErrorMessage('Password cannot be that short 😁');
            isValid = false;
        } else {
            setPasswordError(false);
            setPasswordErrorMessage('');
        }

        return isValid;
    };

    return (
        <>
            <CssBaseline />
            <GradientBackground>
            <SignInContainer direction="column" justifyContent="center">
                <Card variant="outlined">
                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{ width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)' }}
                    >
                        Sign in
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        noValidate
                        sx={{
                            display: 'flex',
                            flexDirection: 'column',
                            width: '100%',
                            gap: 2,
                        }}
                    >
                        <FormControl>
                            <FormLabel htmlFor="username">Username</FormLabel>
                            <TextField
                                error={usernameError}
                                helperText={usernameErrorMessage}
                                id="username" 
                                type="text" 
                                name="username" 
                                placeholder="Your Username"
                                autoComplete="username"
                                autoFocus
                                required
                                fullWidth
                                variant="outlined"
                                color={usernameError ? 'error' : 'primary'}
                            />
                        </FormControl>
                        <FormControl>
                            <FormLabel htmlFor="password">Password</FormLabel>
                            <TextField
                                error={passwordError}
                                helperText={passwordErrorMessage}
                                name="password"
                                placeholder="••••••"
                                type="password"
                                id="password"
                                autoComplete="current-password"
                                required
                                fullWidth
                                variant="outlined"
                                color={passwordError ? 'error' : 'primary'}
                            />
                        </FormControl>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            onClick={validateInputs}
                        >
                            Sign in
                        </Button>
                        {responseMessage && ( // Show response message if it exists
                            <Typography sx={{ textAlign: 'center', color: '#d13030' }}>
                                {responseMessage}
                            </Typography>
                        )}
                        <Typography sx={{ textAlign: 'center' }}>
                            Don&apos;t have an account?{' '}
                            <span>
                                <Link
                                    href="/signup"
                                    variant="body2"
                                >
                                    Sign up
                                </Link>
                            </span>
                        </Typography>
                    </Box>
                </Card>
            </SignInContainer>
            </GradientBackground>
        </>
    );
}
