const API_BASE = 'http://localhost:8080';
let currentUser = null;
let token = localStorage.getItem('token');
let smartHomeId = localStorage.getItem('smartHomeId');
let updateInterval = null;

document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {
    if (token && smartHomeId) {
        showMainPage();
    } else {
        showLandingPage();
    }

    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.classList.add('hidden');
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            hideAllModals();
        }
    });
}

function showLandingPage() {
    hideAllPages();
    document.getElementById('landingPage').classList.remove('hidden');
}

function showRegister() {
    hideAllPages();
    document.getElementById('registerPage').classList.remove('hidden');
}

function showLogin() {
    hideAllPages();
    document.getElementById('loginPage').classList.remove('hidden');
}

function hideAllPages() {
    const pages = ['landingPage', 'registerPage', 'loginPage', 'mainPage'];
    pages.forEach(page => {
        const element = document.getElementById(page);
        if (element) element.classList.add('hidden');
    });
}

function hideAllModals() {
    const modals = ['addDeviceModal', 'profileModal', 'deviceDetailModal'];
    modals.forEach(modalId => {
        const element = document.getElementById(modalId);
        if (element) element.classList.add('hidden');
    });
}

function getSensorUnit(type) {
    const units = {
        'TEMPERATURE': '°C',
        'HUMIDITY': '%',
        'CO2': 'ppm',
        'NOISE': 'дБ'
    };
    return units[type] || '';
}

function getSensorStatus(sensor) {
    if (sensor.type === 'TEMPERATURE') {
        if (sensor.value >= 20 && sensor.value <= 24) return 'good';
        if (sensor.value >= 18 && sensor.value <= 26) return 'warning';
        return 'danger';
    }
    if (sensor.type === 'HUMIDITY') {
        if (sensor.value >= 30 && sensor.value <= 45) return 'good';
        if (sensor.value >= 25 && sensor.value <= 50) return 'warning';
        return 'danger';
    }
    if (sensor.type === 'CO2') {
        if (sensor.value < 800) return 'good';
        if (sensor.value < 1000) return 'warning';
        return 'danger';
    }
    return 'good';
}

async function apiCall(url, options = {}) {
    try {
        const defaultOptions = {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
                ...options.headers
            }
        };

        const response = await fetch(`${API_BASE}${url}`, { ...defaultOptions, ...options });

        if (response.status === 401) {
            logout();
            throw new Error('Unauthorized');
        }

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || response.statusText);
        }

        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('API Call Error:', error);
        throw error;
    }
}