import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getDevicesByPersonName, unlinkDevice, getUnlinkedDevices, linkDevice } from './api/device-api'; 
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Container, Paper, Button, Grid } from '@mui/material';

const MyDevices = () => {
    const [userDevices, setUserDevices] = useState([]);
    const [allDevices, setAllDevices] = useState([]);
    const [errorStatus, setErrorStatus] = useState(0);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const role = localStorage.getItem('role');
        if (!role) {
            navigate('/login'); // Redirect to login if not authenticated
        }
        const personName = localStorage.getItem('name');
        if (personName) {
            fetchUserDevices(personName);
            fetchAllDevices();
        }
    }, []);

    const fetchUserDevices = (personName) => {
        getDevicesByPersonName(personName, (res, status, err) => {
            if (status === 200) {
                const data = JSON.parse(res);
                setUserDevices(data);
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const fetchAllDevices = () => {
        getUnlinkedDevices((res, status, err) => {
            if (status === 200) {
                const data = JSON.parse(res);
                setAllDevices(data);
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const handleUnlink = (deviceId) => {
        unlinkDevice(deviceId, (res, status, err) => {
            if (status === 200) {
                const personName = localStorage.getItem('name');
                if (personName) {
                    fetchUserDevices(personName); // Refresh the user devices list
                    fetchAllDevices(); // Refresh the all devices list
                }
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const handleLink = (deviceId) => {
        const personName = localStorage.getItem('name'); // Retrieve person name from local storage
        if (!personName) {
            setError({ message: 'Person name not found in local storage.' });
            return;
        }

        const deviceData = { personName }; // Create the request body
        linkDevice(deviceId, deviceData, (res, status, err) => {
            if (status === 200) {
                fetchUserDevices(personName); // Refresh the user devices list
                fetchAllDevices(); // Refresh the all devices list
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const userColumns = [
        { field: 'id', headerName: 'ID', width: 90 },
        { field: 'name', headerName: 'Name', width: 150 },
        { field: 'model', headerName: 'Model', width: 150 },
        { field: 'address', headerName: 'Address', width: 200 },
        { field: 'energy', headerName: 'Energy', width: 130 },
        {
            field: 'unlink',
            headerName: 'Action',
            width: 200,
            renderCell: (params) => (
                <Box display="flex" justifyContent="center" alignItems="center" height="100%">
                    <Button 
                        variant="contained" 
                        color="error" 
                        onClick={() => handleUnlink(params.row.id)}
                        sx={{ width: '100%' }}
                    >
                        Unlink
                    </Button>
                </Box>
            ),
        },
    ];

    const allColumns = [
        { field: 'id', headerName: 'ID', width: 90 },
        { field: 'name', headerName: 'Name', width: 150 },
        { field: 'model', headerName: 'Model', width: 150 },
        { field: 'address', headerName: 'Address', width: 200 },
        { field: 'energy', headerName: 'Energy', width: 130 },
        {
            field: 'link',
            headerName: 'Action',
            width: 200,
            renderCell: (params) => (
                <Box display="flex" justifyContent="center" alignItems="center" height="100%">
                    <Button 
                        variant="contained" 
                        color="primary" 
                        onClick={() => handleLink(params.row.id)}
                        sx={{ width: '100%' }}
                    >
                        Link
                    </Button>
                </Box>
            ),
        },
    ];

    return (
        <Container maxWidth={false} sx={{ padding: '0', width: '80vw' }}>
            <Typography variant="h4" gutterBottom align="center">
                My Devices
            </Typography>
            {errorStatus > 0 && (
                <Typography color="error" align="center">
                    Error {errorStatus}: {error?.message}
                </Typography>
            )}

            <Grid container spacing={2} justifyContent="space-between">
                <Grid item xs={12} sm={6}>
                    <Paper elevation={3} sx={{ padding: '20px' }}>
                        <Typography variant="h6" gutterBottom align="center">
                            Your Devices
                        </Typography>
                        <Box sx={{ height: 400, width: '100%' }}>
                            {userDevices.length > 0 ? (
                                <DataGrid
                                    rows={userDevices}
                                    columns={userColumns}
                                    pageSize={5}
                                    rowsPerPageOptions={[5]}
                                    disableRowSelectionOnClick
                                    sx={{
                                        '& .MuiDataGrid-row': {
                                            fontSize: '1.2rem',
                                        },
                                        '& .MuiDataGrid-columnHeaders': {
                                            fontSize: '1.3rem',
                                        },
                                        '& .MuiDataGrid-cell': {
                                            padding: '10px',
                                        },
                                    }}
                                />
                            ) : (
                                <Typography align="center" color="textSecondary">
                                    No devices found.
                                </Typography>
                            )}
                        </Box>
                    </Paper>
                </Grid>
                <Grid item xs={12} sm={6}>
                    <Paper elevation={3} sx={{ padding: '20px' }}>
                        <Typography variant="h6" gutterBottom align="center">
                            All Available Devices
                        </Typography>
                        <Box sx={{ height: 400, width: '100%' }}>
                            {allDevices.length > 0 ? (
                                <DataGrid
                                    rows={allDevices}
                                    columns={allColumns}
                                    pageSize={5}
                                    rowsPerPageOptions={[5]}
                                    disableRowSelectionOnClick
                                    sx={{
                                        '& .MuiDataGrid-row': {
                                            fontSize: '1.2rem',
                                        },
                                        '& .MuiDataGrid-columnHeaders': {
                                            fontSize: '1.3rem',
                                        },
                                        '& .MuiDataGrid-cell': {
                                            padding: '10px',
                                        },
                                    }}
                                />
                            ) : (
                                <Typography align="center" color="textSecondary">
                                    No available devices to link.
                                </Typography>
                            )}
                        </Box>
                    </Paper>
                </Grid>
            </Grid>
        </Container>
    );
};

export default MyDevices;
