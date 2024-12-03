import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getDevicesByPersonName, unlinkDevice, getUnlinkedDevices, linkDevice, getDeviceConsumption } from './api/device-api'; 
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Container, Paper, Button, Grid, Modal } from '@mui/material';
import DeviceChart from './deviceStats'; 
import ReactDatePicker from 'react-datepicker';
import { format } from 'date-fns';
import "react-datepicker/dist/react-datepicker.css";

const MyDevices = () => {
    const [userDevices, setUserDevices] = useState([]);
    const [allDevices, setAllDevices] = useState([]);
    const [errorStatus, setErrorStatus] = useState(0);
    const [error, setError] = useState(null);
    const [openChartModal, setOpenChartModal] = useState(false);
    const [selectedDevice, setSelectedDevice] = useState(null);
    const [selectedDate, setSelectedDate] = useState(null);
    const [deviceData, setDeviceData] = useState(null);
    const [showDatePicker, setShowDatePicker] = useState(false);
    const [fullDay, setFullDay] = useState(false); // State to track full day view
    const navigate = useNavigate();

    useEffect(() => {
        const role = localStorage.getItem('role');
        if (!role) {
            navigate('/login');
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
                setUserDevices(JSON.parse(res));
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const fetchAllDevices = () => {
        getUnlinkedDevices((res, status, err) => {
            if (status === 200) {
                setAllDevices(JSON.parse(res));
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

    const handleShowChart = (device) => {
        setSelectedDevice(device);
        setShowDatePicker(true);
    };

    const handleDateSelection = (date) => {
        setSelectedDate(date);
        const formattedDate = format(date, 'yyyy-MM-dd');
        getDeviceConsumption(selectedDevice.id, formattedDate, (res, status, err) => {
            if (status === 200) {
                setDeviceData(JSON.parse(res));
                setOpenChartModal(true);
                setShowDatePicker(false);
            } else {
                setErrorStatus(status);
                setError({ message: 'Failed to fetch device data' });
            }
        });
    };

    const handleFullDay = () => {
        // Generate full day data, filling missing hours with 0
        const fullDayData = Array.from({ length: 24 }, (_, index) => {
            const existingData = deviceData.find(item => item.hour === index);
            return {
                hour: index,
                medianConsumptionValue: existingData ? existingData.medianConsumptionValue : 0
            };
        });
        setDeviceData(fullDayData);
        setFullDay(true); // Mark that it's a full day chart
    };

    const userColumns = [
        { field: 'id', headerName: 'ID', width: 90 },
        { field: 'name', headerName: 'Name', width: 150 },
        { field: 'model', headerName: 'Model', width: 150 },
        { field: 'address', headerName: 'Address', width: 200 },
        { field: 'energy', headerName: 'Energy', width: 130 },
        {
            field: 'unlink',
            headerName: 'Unlink',
            width: 200,
            renderCell: (params) => (
                <Button
                    variant="contained"
                    color="error"
                    fullWidth
                    onClick={() => handleUnlink(params.row.id)}
                >
                    Unlink
                </Button>
            ),
        },
        {
            field: 'chart',
            headerName: 'Show Chart',
            width: 200,
            renderCell: (params) => (
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={() => handleShowChart(params.row)}
                >
                    Show Chart
                </Button>
            ),
        },
    ];
    
    const allDevicesColumns = [
        { field: 'id', headerName: 'ID', width: 90 },
        { field: 'name', headerName: 'Name', width: 150 },
        { field: 'model', headerName: 'Model', width: 150 },
        { field: 'address', headerName: 'Address', width: 200 },
        { field: 'energy', headerName: 'Energy', width: 130 },
        {
            field: 'link',
            headerName: 'Link',
            width: 200,
            renderCell: (params) => (
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={() => handleLink(params.row.id)}
                >
                    Link
                </Button>
            ),
        },
    ];
    

    return (
        <Container maxWidth={false} sx={{ padding: '20px', width: '80vw' }}>
            <Typography variant="h4" gutterBottom align="center">
                My Devices
            </Typography>
            {errorStatus > 0 && (
                <Typography color="error" align="center">
                    Error {errorStatus}: {error?.message}
                </Typography>
            )}

            <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                    <Paper elevation={3} sx={{ padding: '20px', backgroundColor: '#f9f9f9' }}>
                        <Typography variant="h6" gutterBottom align="center">
                            Your Devices
                        </Typography>
                        <Box sx={{ height: 400, width: '100%' }}>
                            <DataGrid
                                rows={userDevices || [] }
                                columns={userColumns}
                                pageSize={5}
                                disableSelectionOnClick
                            />
                        </Box>
                    </Paper>
                </Grid>
                <Grid item xs={12} sm={6}>
                    <Paper elevation={3} sx={{ padding: '20px', backgroundColor: '#f9f9f9' }}>
                        <Typography variant="h6" gutterBottom align="center">
                            Available Devices
                        </Typography>
                        <Box sx={{ height: 400, width: '100%' }}>
                            <DataGrid
                                rows={allDevices || []}
                                columns={allDevicesColumns}
                                pageSize={5}
                                disableSelectionOnClick
                            />
                        </Box>
                    </Paper>
                </Grid>
            </Grid>

            <Modal open={showDatePicker} onClose={() => setShowDatePicker(false)}>
                <Box
                    sx={{
                        width: '300px',
                        backgroundColor: '#fff',
                        borderRadius: '8px',
                        padding: '20px',
                        margin: 'auto',
                        marginTop: '20vh',
                        boxShadow: '0 4px 10px rgba(0,0,0,0.1)',
                        textAlign: 'center',
                    }}
                >
                    <Typography variant="h6" gutterBottom>
                        Select a Date
                    </Typography>
                    <ReactDatePicker
                        selected={selectedDate}
                        onChange={handleDateSelection}
                        dateFormat="yyyy/MM/dd"
                        inline
                    />
                    <Button
                        variant="contained"
                        color="secondary"
                        sx={{ marginTop: '10px' }}
                        onClick={() => setShowDatePicker(false)}
                    >
                        Cancel
                    </Button>
                </Box>
            </Modal>

            <Modal open={openChartModal} onClose={() => setOpenChartModal(false)}>
                <Box
                    sx={{
                        width: '90vw',
                        height: '80vh',
                        backgroundColor: '#fff',
                        padding: '20px',
                        margin: 'auto',
                        marginTop: '5vh',
                        borderRadius: '8px',
                        overflowY: 'auto',
                    }}
                >
                    <DeviceChart deviceData={deviceData} />
                    <Box sx={{ textAlign: 'center', marginTop: 2 }}>
                        <Button
                            variant="contained"
                            color="secondary"
                            onClick={() => setOpenChartModal(false)}
                            sx={{ marginRight: 2 }}
                        >
                            Close
                        </Button>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleFullDay}
                        >
                            Full Day
                        </Button>
                    </Box>
                </Box>
            </Modal>
        </Container>
    );
};

export default MyDevices;
