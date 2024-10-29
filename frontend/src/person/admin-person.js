// src/Person.js

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPersons, updatePerson, deletePerson } from './api/person-api';
import { DataGrid } from '@mui/x-data-grid';
import { Box, Typography, Container, Paper, Button, Modal, TextField, MenuItem } from '@mui/material';

const Person = () => {
    const [persons, setPersons] = useState([]);
    const [errorStatus, setErrorStatus] = useState(0);
    const [error, setError] = useState(null);
    const [selectedPerson, setSelectedPerson] = useState(null);
    const [modalOpen, setModalOpen] = useState(false);
    const navigate = useNavigate();
    const [formControls, setFormControls] = useState({
        name: '',
        role: '',
        age: '',
        address: '',
        password: '',
    });

    useEffect(() => {
        const role = localStorage.getItem('role');
        if (role !== 'admin') {
            navigate('/device'); 
        }
        fetchPersons();
    }, []);

    const fetchPersons = () => {
        getPersons((res, status, err) => {
            const data = JSON.parse(res);
            if (Array.isArray(data) && status === 200) {
                const formattedData = data.map((person) => ({
                    id: person.id,
                    name: person.name,
                    role: person.role, 
                    age: person.age,
                    address: person.address, 
                }));
                setPersons(formattedData);
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const handleRowClick = (params) => {
        const person = persons.find(p => p.id === params.row.id);
        setSelectedPerson(person);
        setFormControls({
            name: person.name,
            role: person.role,
            age: person.age,
            address: person.address,
            password: '', // Reset password field
        });
        setModalOpen(true);
    };

    const handleModalClose = () => {
        setModalOpen(false);
        setSelectedPerson(null);
    };

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setFormControls(prevControls => ({
            ...prevControls,
            [name]: value,
        }));
    };

    const handleSubmit = () => {
        const updatedPerson = {
            id: selectedPerson.id, // Include the ID for the update
            name: formControls.name,
            role: formControls.role,
            age: formControls.age,
            address: formControls.address,
            password: 'null', // Always send password as null
        };

        updatePerson(selectedPerson.name, updatedPerson, (res, status, err) => {
            if (status === 200 || status === 204) {
                console.log("Successfully updated person:", updatedPerson);
                fetchPersons(); // Refresh the person list
                handleModalClose();
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
        setModalOpen(false);
    };

    const handleDelete = (id, event) => {
        event.stopPropagation(); // Prevent the modal from opening
        deletePerson(id, (err, status) => {
            if (status === 200) {
                console.log(`Successfully deleted person with ID: ${id}`);
                fetchPersons(); // Refresh the person list
            } else {
                setErrorStatus(status);
                setError(err);
            }
        });
    };

    const columns = [
        { field: 'id', headerName: 'ID', width: 90 },
        { field: 'name', headerName: 'Name', width: 150 },
        { field: 'role', headerName: 'Role', width: 120 },
        { field: 'age', headerName: 'Age', width: 110 },
        { field: 'address', headerName: 'Address', width: 200 },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 150,
            renderCell: (params) => (
                <Box sx={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
                    <Button 
                        variant="outlined" 
                        color="primary" 
                        onClick={(event) => {
                            event.stopPropagation(); // Prevent row click event
                            handleRowClick(params);
                        }} 
                        size="small"
                    >
                        Edit
                    </Button>
                    <Button 
                        variant="outlined" 
                        color="error" 
                        onClick={(event) => handleDelete(params.row.id, event)} // Pass event to handleDelete
                        size="small"
                    >
                        Delete
                    </Button>
                </Box>
            ),
        },
    ];

    return (
        <Container maxWidth={false} sx={{ padding: '0', width: '60vw' }}>
            <Typography variant="h4" gutterBottom align="center">
                Persons
            </Typography>
            {errorStatus > 0 && (
                <Typography color="error" align="center">
                    Error {errorStatus}: {error?.message}
                </Typography>
            )}
            <Paper elevation={3}>
                <Box sx={{ height: 600, width: '100%' }}>
                    <DataGrid
                        rows={persons}
                        columns={columns}
                        pageSize={5}
                        rowsPerPageOptions={[5]}
                        disableRowSelectionOnClick
                        sx={{
                            '& .MuiDataGrid-row': {
                                fontSize: '1.2rem', // Adjust row font size
                            },
                            '& .MuiDataGrid-columnHeaders': {
                                fontSize: '1.3rem', // Adjust column header font size
                            },
                            '& .MuiDataGrid-cell': {
                                padding: '10px', // Adjust padding in the cells
                            },
                        }}
                    />
                </Box>
            </Paper>

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
                        Update Person
                    </Typography>
                    <TextField
                        label="Name"
                        name="name"
                        fullWidth
                        margin="normal"
                        value={formControls.name}
                        onChange={handleInputChange}
                    />
                    <TextField
                        select // Making this a dropdown
                        label="Role"
                        name="role"
                        fullWidth
                        margin="normal"
                        value={formControls.role}
                        onChange={handleInputChange}
                    >
                        <MenuItem value="user">User</MenuItem>
                        <MenuItem value="admin">Admin</MenuItem>
                    </TextField>
                    <TextField
                        label="Age"
                        name="age"
                        type="number"
                        fullWidth
                        margin="normal"
                        value={formControls.age}
                        onChange={handleInputChange}
                    />
                    <TextField
                        label="Address"
                        name="address"
                        fullWidth
                        margin="normal"
                        value={formControls.address}
                        onChange={handleInputChange}
                    />
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
                        <Button variant="contained" color="primary" onClick={handleSubmit}>
                            Submit
                        </Button>
                    </Box>
                </Box>
            </Modal>
        </Container>
    );
};

export default Person;
