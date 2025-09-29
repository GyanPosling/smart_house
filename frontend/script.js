const API_URL = 'http://backend:8080'; // Для Docker; для локального теста измени на 'http://localhost:8080'
let jwtToken = null;

async function register() {
    const name = document.getElementById('register-name').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;

    try {
        const response = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        const text = await response.text();
        document.getElementById('auth-message').textContent = response.ok ? 'Registration successful!' : text;
    } catch (error) {
        document.getElementById('auth-message').textContent = 'Error: ' + error.message;
    }
}

async function login() {
    const name = document.getElementById('login-name').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, password })
        });
        if (response.ok) {
            jwtToken = await response.text();
            document.getElementById('auth-message').textContent = 'Login successful!';
            document.getElementById('auth-section').style.display = 'none';
            document.getElementById('devices-section').style.display = 'block';
            document.getElementById('sensors-section').style.display = 'block';
            fetchDevices();
            fetchSensors();
        } else {
            document.getElementById('auth-message').textContent = await response.text();
        }
    } catch (error) {
        document.getElementById('auth-message').textContent = 'Error: ' + error.message;
    }
}

async function fetchDevices() {
    try {
        const response = await fetch(`${API_URL}/smart_house/devices`, {
            headers: { 'Authorization': `Bearer ${jwtToken}` }
        });
        if (!response.ok) throw new Error('Failed to fetch devices');
        const devices = await response.json();
        const tableBody = document.getElementById('devices-table-body');
        tableBody.innerHTML = '';
        devices.forEach(device => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${device.id}</td>
                <td>${device.name}</td>
                <td>${device.type}</td>
                <td>${device.status}</td>
                <td>${device.mode}</td>
                <td>
                    <button onclick="controlDevice(${device.id}, 'on')">Turn On</button>
                    <button onclick="controlDevice(${device.id}, 'off')">Turn Off</button>
                    <button onclick="controlDevice(${device.id}, 'auto')">Auto Mode</button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        document.getElementById('auth-message').textContent = 'Error fetching devices: ' + error.message;
    }
}

async function fetchSensors() {
    try {
        const response = await fetch(`${API_URL}/smart_house/sensors`, {
            headers: { 'Authorization': `Bearer ${jwtToken}` }
        });
        if (!response.ok) throw new Error('Failed to fetch sensors');
        const sensors = await response.json();
        const tableBody = document.getElementById('sensors-table-body');
        tableBody.innerHTML = '';
        sensors.forEach(sensor => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${sensor.id}</td>
                <td>${sensor.type}</td>
                <td>${sensor.value}</td>
                <td>${sensor.location}</td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        document.getElementById('auth-message').textContent = 'Error fetching sensors: ' + error.message;
    }
}

async function controlDevice(deviceId, action) {
    try {
        const response = await fetch(`${API_URL}/smart_house/devices/${deviceId}/${action}`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${jwtToken}` }
        });
        if (!response.ok) throw new Error(`Failed to ${action} device`);
        document.getElementById('auth-message').textContent = `Device ${action} successful`;
        fetchDevices(); // Обновляем список устройств
    } catch (error) {
        document.getElementById('auth-message').textContent = 'Error controlling device: ' + error.message;
    }
}