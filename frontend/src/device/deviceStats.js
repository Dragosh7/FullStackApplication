import React from 'react';
import { Line } from 'react-chartjs-2';
import { Box, Typography } from '@mui/material';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    LineElement,
    PointElement,
    Title,
    Tooltip,
    Legend
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, LineElement, PointElement, Title, Tooltip, Legend);

const DeviceChart = ({ deviceData }) => {
    // Handle the case where data is unavailable or empty
    if (!deviceData || deviceData.length === 0) {
        return (
            <Box
                sx={{
                    textAlign: 'center',
                    padding: 3,
                    borderRadius: 2,
                    backgroundColor: '#f9f9f9',
                    boxShadow: 3,
                    width: '100%',
                }}
            >
                <Typography variant="body1" color="textSecondary">
                    No data available for this device.
                </Typography>
            </Box>
        );
    }

    const chartData = {
        labels: deviceData.map(item => `${item.hour}:00`), // Format hours as "hour:00"
        datasets: [
            {
                label: 'Consumption Value (kWh)',
                data: deviceData.map(item => item.medianConsumptionValue), // Map energy values
                fill: false,
                borderColor: '#3f51b5',
                backgroundColor: '#3f51b5',
                tension: 0.4,
                pointBackgroundColor: '#3f51b5',
                pointBorderColor: '#fff',
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: '#3f51b5',
            },
        ],
    };

    const options = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: 'top',
                labels: {
                    color: '#333',
                    font: {
                        size: 14,
                    },
                },
            },
            tooltip: {
                callbacks: {
                    title: (tooltipItems) => `Hour: ${tooltipItems[0].label}`,
                    label: (tooltipItem) => `Consumption: ${tooltipItem.raw} kWh`,
                },
            },
        },
        scales: {
            x: {
                grid: {
                    display: false,
                },
                ticks: {
                    color: '#666',
                },
            },
            y: {
                grid: {
                    color: '#e0e0e0',
                },
                ticks: {
                    color: '#666',
                },
                title: {
                    display: true,
                    text: 'Energy (kWh)',
                    color: '#333',
                    font: {
                        size: 14,
                    },
                },
            },
        },
    };

    return (
        <Box
            sx={{
                padding: 3,
                borderRadius: 2,
                backgroundColor: 'white',
                boxShadow: 3,
                width: '100%',
                height: 'auto',
                maxWidth: 800,
                margin: 'auto',
            }}
        >
            <Typography variant="h6" gutterBottom align="center">
                Energy Usage Chart
            </Typography>
            <Box sx={{ height: 400 }}>
                <Line data={chartData} options={options} />
            </Box>
        </Box>
    );
};

export default DeviceChart;
