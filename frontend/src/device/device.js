import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getDevices, updateDevice, deleteDevice, getPersons, addDevice } from './api/device-api';
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Container, Paper, Button, Modal, TextField, MenuItem, Select } from '@mui/material';

const Device = () => {
    const [devices, setDevices] = useState([]);
    const [persons, setPersons] = useState([]);
    const [errorStatus, setErrorStatus] = useState(0);
    const [error, setError] = useState(null);
    const [selectedDevice, setSelectedDevice] = useState(null);
    const [modalOpen, setModalOpen] = useState(false);
    const [addModalOpen, setAddModalOpen] = useState(false); // State for add device modal
    const navigate = useNavigate();
    const [formControls, setFormControls] = useState({
        name: '',
        model: '',
        address: '',
        energy: '',
        personName: '',
    });

    useEffect(() => {
        const role = localStorage.getItem('role');
        if (role !== 'admin') {
            navigate('/home');
        }
        fetchDevices();
        fetchPersons();
    }, []);

    const fetchDevices = () => {
        getDevices((res, status, err) => {
            const data = JSON.parse(res);
            if (Array.isArray(data) && (status === 200 || status === 201)) {
                const formattedData = data.map((device) => ({
                    id: device.id,
                    name: device.name,
                    model: device.model,
                    address: device.address,
                    energy: device.energy,
                    personName: device.personName || "null",
                }));
                setDevices(formattedData);
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const fetchPersons = () => {
        getPersons((res, status, err) => {
            if (status === 200) {
                const data = JSON.parse(res);
                setPersons(data);
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const handleEdit = (device) => {
        setSelectedDevice(device);
        setFormControls({
            name: device.name,
            model: device.model,
            address: device.address,
            energy: device.energy,
            personName: device.personName || 'null',
        });
        setModalOpen(true);
    };

    const handleModalClose = () => {
        setModalOpen(false);
        setSelectedDevice(null);
    };

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setFormControls(prevControls => ({
            ...prevControls,
            [name]: value,
        }));
        if (name === 'personName' && value !== '' && !persons.some(person => person.name === value)) {
            setFormControls(prevControls => ({
                ...prevControls,
                personName: '', // Reset or set to a valid value
            }));
        }
    };

    const handleSubmit = () => {
        const updatedDevice = {
            id: selectedDevice.id,
            name: formControls.name,
            model: formControls.model,
            address: formControls.address,
            energy: formControls.energy,
            personName: formControls.personName || null,
        };

        updateDevice(selectedDevice.id, updatedDevice, (res, status, err) => {
            if (status === 200 || status === 204) {
                fetchDevices();
                handleModalClose();
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const handleDelete = (id) => {
        deleteDevice(id, (res, status, err) => {
            if (status === 200 || status === 204) {
                fetchDevices();
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const handleAddDeviceSubmit = () => {
        const newDevice = {
            name: formControls.name,
            model: formControls.model,
            address: formControls.address,
            energy: formControls.energy,
            personName: formControls.personName || null,
        };

        addDevice(newDevice, (res, status, err) => {
            if (status === 201 || status === 200) {
                fetchDevices();
                setAddModalOpen(false);
                setFormControls({ name: '', model: '', address: '', energy: '', personName: '' }); // Reset form
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const columns = [
        { field: 'id', headerName: 'ID', width: 90 },
        { field: 'name', headerName: 'Name', width: 150 },
        { field: 'model', headerName: 'Model', width: 150 },
        { field: 'address', headerName: 'Address', width: 200 },
        { field: 'energy', headerName: 'Energy', width: 130 },
        { field: 'personName', headerName: 'Assigned Person', width: 200 },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 150,
            renderCell: (params) => (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                    <Button 
                        variant="contained" 
                        color="primary" 
                        onClick={() => handleEdit(params.row)} // Edit button
                    >
                        Edit
                    </Button>
                    <Button 
                        variant="contained" 
                        color="secondary" 
                        onClick={() => handleDelete(params.row.id)} // Delete button
                        sx={{ ml: 1 }}
                    >
                        Delete
                    </Button>
                </Box>
            )
        },
    ];

    return (
        <Container maxWidth={false} sx={{ padding: '0', width: '60vw' }}>
            <Typography variant="h4" gutterBottom align="center">
                Devices
            </Typography>
            {errorStatus > 0 && (
                <Typography color="error" align="center">
                    Error {errorStatus}: {error?.message}
                </Typography>
            )}
            <Button variant="contained" color="primary" onClick={() => setAddModalOpen(true)} sx={{ mb: 2 }}>
                Add Device
            </Button>
            <Paper elevation={3}>
                <Box sx={{ height: 600, width: '100%' }}>
                    <DataGrid
                        rows={devices}
                        columns={columns}
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
                </Box>
            </Paper>

            {/* Modal for Updating Device */}
            <Modal open={modalOpen} onClose={handleModalClose}>
                <Box sx={{ 
                    bgcolor: 'background.paper', 
                    borderRadius: 2, 
                    boxShadow: 24, 
                    p: 4, 
                    width: 400,
                    mx: 'auto',
                    mt: '10%',
                }}>
                    <Typography variant="h6" component="h2" gutterBottom>
                        Update Device
                    </Typography>
                    <TextField
                        label="Name"
                        name="name"
                        fullWidth
                        margin="dense"
                        value={formControls.name}
                        onChange={handleInputChange}
                    />
                    <TextField
                        label="Model"
                        name="model"
                        fullWidth
                        margin="dense"
                        value={formControls.model}
                        onChange={handleInputChange}
                    />
                    <TextField
                        label="Address"
                        name="address"
                        fullWidth
                        margin="dense"
                        value={formControls.address}
                        onChange={handleInputChange}
                    />
                    <TextField
                        label="Energy"
                        name="energy"
                        type="number"
                        fullWidth
                        margin="dense"
                        value={formControls.energy}
                        onChange={handleInputChange}
                    />
                    <Select
                        name="personName"
                        fullWidth
                        value={formControls.personName}
                        onChange={handleInputChange}
                        displayEmpty
                        margin="dense"
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {persons.map(person => (
                            <MenuItem key={person.name} value={person.name}>
                                {person.name}
                            </MenuItem>
                        ))}
                    </Select>

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                        <Button variant="contained" color="primary" onClick={handleSubmit}>
                            Submit
                        </Button>
                    </Box>
                </Box>
            </Modal>

            {/* Modal for Adding Device */}
            <Modal open={addModalOpen} onClose={() => setAddModalOpen(false)}>
                <Box sx={{ 
                    bgcolor: 'background.paper', 
                    borderRadius: 2, 
                    boxShadow: 24, 
                    p: 4, 
                    width: 400,
                    mx: 'auto',
                    mt: '10%',
                }}>
                    <Typography variant="h6" component="h2" gutterBottom>
                        Add Device
                    </Typography>
                    <TextField
                        label="Name"
                        name="name"
                        fullWidth
                        margin="dense"
                        value={formControls.name}
                        onChange={handleInputChange}
                    />
                    <TextField
                        label="Model"
                        name="model"
                        fullWidth
                        margin="dense"
                        value={formControls.model}
                        onChange={handleInputChange}
                    />
                    <TextField
                        label="Address"
                        name="address"
                        fullWidth
                        margin="dense"
                        value={formControls.address}
                        onChange={handleInputChange}
                    />
                    <TextField
                        label="Energy"
                        name="energy"
                        type="number"
                        fullWidth
                        margin="dense"
                        value={formControls.energy}
                        onChange={handleInputChange}
                    />
                    <Select
                        name="personName"
                        fullWidth
                        value={formControls.personName}
                        onChange={handleInputChange}
                        displayEmpty
                        margin="dense"
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {persons.map(person => (
                            <MenuItem key={person.name} value={person.name}>
                                {person.name}
                            </MenuItem>
                        ))}
                    </Select>

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                        <Button variant="contained" color="primary" onClick={handleAddDeviceSubmit}>
                            Add Device
                        </Button>
                    </Box>
                </Box>
            </Modal>
        </Container>
    );
};

export default Device;
