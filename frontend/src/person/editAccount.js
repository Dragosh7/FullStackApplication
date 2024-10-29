import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPersonById, updatePerson } from './api/person-api'; 
import { Box, Typography, Container, Paper, Button, TextField, Grid } from '@mui/material';

const EditAccount = () => {
    const [userData, setUserData] = useState({
        name: '',
        address: '',
        age: '',
        password: '',
        role: "null" // Always set role as null
    });
    const [errorStatus, setErrorStatus] = useState(0);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const userId = localStorage.getItem('id');
        if (!userId) {
            navigate('/login'); // Redirect to login if userId is not found
            return;
        }
        fetchUserData(userId);
    }, []);

    const fetchUserData = (userId) => {
        getPersonById(userId, (res, status, err) => {
            if (status === 200) {
                const data = JSON.parse(res);
                setUserData({
                    name: data.name,
                    address: data.address,
                    age: data.age,
                    password: '', // Clear password field for security
                    role: "null"
                });
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setUserData((prevData) => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const userName = localStorage.getItem('name');
        const { name, address, age, password } = userData;

        const updatedUserData = {
            name,
            address,
            age: parseInt(age, 10),
            password: password ? password : "null", // Send undefined if the password is empty
            role: "null" // Always send as null
        };

        updatePerson(userName, updatedUserData, (res, status, err) => {
            if (status === 200) {
                // Handle successful update (e.g., redirect or show a success message)
                alert('Account updated successfully! You will be signed out');
                localStorage.clear();
                navigate('/login');
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    return (
        <Container maxWidth="sm" sx={{ padding: '0', marginTop: '20px' }}>
            <Paper elevation={3} sx={{ padding: '20px' }}>
                <Typography variant="h4" gutterBottom align="center">
                    Edit Account
                </Typography>
                {errorStatus > 0 && (
                    <Typography color="error" align="center">
                        Error {errorStatus}: {error?.message}
                    </Typography>
                )}
                <form onSubmit={handleSubmit}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <TextField
                                label="Name"
                                name="name"
                                value={userData.name}
                                onChange={handleInputChange}
                                fullWidth
                                required
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="Address"
                                name="address"
                                value={userData.address}
                                onChange={handleInputChange}
                                fullWidth
                                required
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="Age"
                                name="age"
                                type="number"
                                value={userData.age}
                                onChange={handleInputChange}
                                fullWidth
                                required
                                inputProps={{ min: 0 }}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                label="Password"
                                name="password"
                                type="password"
                                value={userData.password}
                                onChange={handleInputChange}
                                fullWidth
                                helperText="Leave blank if you don't want to change"
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <Button type="submit" variant="contained" color="primary" fullWidth>
                                Update Account
                            </Button>
                        </Grid>
                    </Grid>
                </form>
            </Paper>
        </Container>
    );
};

export default EditAccount;
