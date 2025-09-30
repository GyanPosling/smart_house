async function loadSensors() {
    if (!smartHomeId) return;
    try {
        const sensors = await apiCall(`/smarthome/${smartHomeId}/sensors`);
        displaySensors(sensors);
    } catch (error) {
        console.error('Ошибка загрузки датчиков:', error);
        document.getElementById('sensorsData').innerHTML =
            '<div class="error-message">❌ Не удалось загрузить данные датчиков</div>';
    }
}

function displaySensors(sensors) {
    const sensorsData = document.getElementById('sensorsData');

    if (sensors.length === 0) {
        sensorsData.innerHTML = '<div class="empty-state">🚫 Данные с датчиков недоступны</div>';
        return;
    }

    sensorsData.innerHTML = sensors.map(sensor => {
        const status = getSensorStatus(sensor);
        const value = sensor.value.toFixed(1);
        const unit = getSensorUnit(sensor.type);

        return `
            <div class="sensor-value ${status}">
                <strong>${getSensorTypeName(sensor.type)}</strong>
                <div class="value">${value} ${unit}</div>
                <small>📍 ${sensor.location}</small>
                <small>${getStatusMessage(sensor.type, value, status)}</small>
            </div>
        `;
    }).join('');
}

function getSensorTypeName(type) {
    const names = {
        'TEMPERATURE': '🌡️ Температура',
        'HUMIDITY': '💧 Влажность',
        'CO2': '💨 CO₂',
        'NOISE': '📢 Шум'
    };
    return names[type] || type;
}

function getStatusMessage(type, value, status) {
    const messages = {
        'TEMPERATURE': {
            'good': '✅ Комфортная температура',
            'warning': '⚠️ Температура близка к границам комфорта',
            'danger': '❌ Температура вне зоны комфорта'
        },
        'HUMIDITY': {
            'good': '✅ Комфортная влажность',
            'warning': '⚠️ Влажность близка к границам комфорта',
            'danger': '❌ Влажность вне зоны комфорта'
        },
        'CO2': {
            'good': '✅ Отличное качество воздуха',
            'warning': '⚠️ Качество воздуха ухудшается',
            'danger': '❌ Требуется проветривание'
        },
        'NOISE': {
            'good': '✅ Уровень шума в норме',
            'warning': '⚠️ Повышенный уровень шума',
            'danger': '❌ Высокий уровень шума'
        }
    };

    return messages[type]?.[status] || '📊 Данные получены';
}

function showProfile() {
    const profileInfo = document.getElementById('profileInfo');

    if (!currentUser) {
        profileInfo.innerHTML = '<p>❌ Информация о пользователе недоступна</p>';
    } else {
        profileInfo.innerHTML = `
            <p><strong>🏠 Имя дома:</strong> ${currentUser.smartHomeName || 'Неизвестно'}</p>
            <p><strong>👤 Имя пользователя:</strong> ${currentUser.name}</p>
            <p><strong>📱 Количество устройств:</strong> ${currentUser.devicesCount || 0}</p>
            <p><strong>🕐 Последний вход:</strong> ${currentUser.lastLogin || 'Неизвестно'}</p>
            <p><strong>🏠 Статус системы:</strong> <span style="color: #28a745;">✅ Активна</span></p>
            <p><strong>🔧 Режим работы:</strong> Автоматическое управление микроклиматом</p>
        `;
    }
    document.getElementById('profileModal').classList.remove('hidden');
}

function hideProfileModal() {
    document.getElementById('profileModal').classList.add('hidden');
}