const API_BASE = 'http://localhost:8080';
let currentUser = null;
let token = localStorage.getItem('token');
let updateInterval = null;

// Инициализация приложения
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {
    // Проверка аутентификации при загрузке
    if (token) {
        showMainPage();
    } else {
        showRegister();
    }

    // Обработка закрытия модальных окон
    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.classList.add('hidden');
        }
    });

    // Обработка клавиши Escape
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            hideAllModals();
        }
    });
}

// Управление отображением страниц
function showRegister() {
    hideAllPages();
    document.getElementById('registerPage').classList.remove('hidden');
}

function showLogin() {
    hideAllPages();
    document.getElementById('loginPage').classList.remove('hidden');
}

function hideAllPages() {
    const pages = ['registerPage', 'loginPage', 'mainPage'];
    pages.forEach(page => {
        const element = document.getElementById(page);
        if (element) element.classList.add('hidden');
    });
}

function hideAllModals() {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => modal.classList.add('hidden'));
}

// Вспомогательные функции
function getDeviceTypeName(type) {
    const types = {
        'HEATER': '🔥 Обогреватель',
        'AIR_CONDITIONER': '❄️ Кондиционер',
        'HUMIDIFIER': '💧 Увлажнитель',
        'DEHUMIDIFIER': '🌬️ Осушитель',
        'VENTILATOR': '💨 Вентилятор'
    };
    return types[type] || type;
}

function getSensorTypeName(type) {
    const types = {
        'TEMPERATURE': '🌡️ Температура',
        'HUMIDITY': '💧 Влажность',
        'CO2': '🌫️ Уровень CO₂',
        'NOISE': '🔊 Уровень шума'
    };
    return types[type] || type;
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

// API вызовы с обработкой ошибок
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
            throw new Error('Требуется авторизация');
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('API call failed:', error);
        throw error;
    }
}