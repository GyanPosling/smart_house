async function loadDevices() {
    if (!smartHomeId) return;
    try {
        const devices = await apiCall(`/smarthome/${smartHomeId}/devices`);
        displayDevices(devices);
        if (currentUser) {
            currentUser.devicesCount = devices.length;
        }
    } catch (error) {
        console.error('Ошибка загрузки устройств:', error);
        document.getElementById('devicesList').innerHTML =
            '<div class="error-message">❌ Не удалось загрузить устройства</div>';
    }
}

function displayDevices(devices) {
    const devicesList = document.getElementById('devicesList');

    if (devices.length === 0) {
        devicesList.innerHTML = `
            <div class="empty-state">
                <h3>🚫 Устройств пока нет</h3>
                <p>Добавьте первое устройство для управления микроклиматом</p>
                <button class="btn btn-primary" onclick="showAddDeviceModal()">Добавить устройство</button>
            </div>
        `;
        return;
    }

    devicesList.innerHTML = devices.map(device => `
        <div class="device-card ${device.status === 'ON' ? 'on' : 'off'} ${device.mode === 'AUTO' ? 'auto' : ''}" 
             onclick="showDeviceDetail(${device.id})">
            <div class="device-header">
                <div class="device-title">${device.name}</div>
                <span class="device-status ${device.status === 'ON' ? 'status-on' : 'status-off'}">
                    ${device.status === 'ON' ? 'ВКЛ' : 'ВЫКЛ'}
                </span>
            </div>
            <div class="device-body">
                <div class="device-icon">${getDeviceIcon(device.type)}</div>
                <div class="device-info">
                    <small>📍 ${device.location}</small>
                    <small class="mode-tag">${device.mode === 'AUTO' ? 'Авто' : 'Ручной'}</small>
                </div>
            </div>
        </div>
    `).join('');
}

async function showDeviceDetail(deviceId) {
    try {
        const device = await apiCall(`/smarthome/${smartHomeId}/devices/${deviceId}`);
        const detailDiv = document.getElementById('deviceDetailInfo');

        detailDiv.innerHTML = `
            <p><strong>Название:</strong> ${device.name}</p>
            <p><strong>Тип:</strong> ${getDeviceTypeName(device.type)}</p>
            <p><strong>Статус:</strong> <span class="device-status ${device.status === 'ON' ? 'status-on' : 'status-off'}">${device.status}</span></p>
            <p><strong>Режим:</strong> ${device.mode === 'AUTO' ? 'Автоматический' : 'Ручной'}</p>
            <p><strong>Уровень мощности:</strong> ${device.powerLevel}%</p>
            <p><strong>Расположение:</strong> ${device.location}</p>
            
            ${device.supportsTemperatureControl ?
            `<p><strong>Текущая темп.:</strong> ${device.currentTemperature || '?'}°C</p>
                 <p><strong>Целевая темп.:</strong> ${device.targetTemperature || '?'}°C</p>`
            : ''}
            
            ${device.supportsHumidityControl ?
            `<p><strong>Текущая влажность:</strong> ${device.currentHumidity || '?'}%</p>
                 <p><strong>Целевая влажность:</strong> ${device.targetHumidity || '?'}%</p>`
            : ''}

            <div class="detail-actions">
                ${device.status === 'OFF' ?
            `<button class="btn btn-success" onclick="toggleDeviceStatus(${device.id}, 'ON')">ВКЛЮЧИТЬ</button>` :
            `<button class="btn btn-danger" onclick="toggleDeviceStatus(${device.id}, 'OFF')">ВЫКЛЮЧИТЬ</button>`
        }
                <button class="btn btn-info" onclick="setDeviceMode(${device.id}, '${device.mode === 'AUTO' ? 'MANUAL' : 'AUTO'}')">
                    Переключить на ${device.mode === 'AUTO' ? 'Ручной' : 'Авто'}
                </button>
                <button class="btn btn-secondary" onclick="deleteDevice(${device.id})">Удалить</button>
            </div>
        `;

        document.getElementById('deviceDetailModal').classList.remove('hidden');

    } catch (error) {
        console.error('Ошибка загрузки деталей устройства:', error);
        showNotification('❌ Ошибка загрузки деталей устройства', 'error');
    }
}

async function toggleDeviceStatus(deviceId, newStatus) {
    try {
        await apiCall(`/smarthome/${smartHomeId}/devices/${deviceId}/${newStatus.toLowerCase()}`, {
            method: 'PUT'
        });
        showNotification(`✅ Устройство ${newStatus === 'ON' ? 'включено' : 'выключено'}`);
        hideDeviceDetailModal();
        await updateData();
    } catch (error) {
        showNotification('❌ Ошибка изменения статуса', 'error');
    }
}

async function setDeviceMode(deviceId, newMode) {
    try {
        const urlPart = newMode === 'AUTO' ? 'auto' : 'manual';
        await apiCall(`/smarthome/${smartHomeId}/devices/${deviceId}/${urlPart}`, {
            method: 'PUT'
        });
        showNotification(`✅ Режим установлен на ${newMode === 'AUTO' ? 'Автоматический' : 'Ручной'}`);
        hideDeviceDetailModal();
        await updateData();
    } catch (error) {
        showNotification('❌ Ошибка изменения режима', 'error');
    }
}

async function deleteDevice(deviceId) {
    if (!confirm('Вы уверены, что хотите удалить это устройство?')) return;
    try {
        await apiCall(`/smarthome/${smartHomeId}/devices/${deviceId}`, {
            method: 'DELETE'
        });
        showNotification('✅ Устройство удалено');
        hideDeviceDetailModal();
        await loadDevices();
    } catch (error) {
        showNotification('❌ Ошибка удаления устройства', 'error');
    }
}

function getDeviceIcon(type) {
    switch (type) {
        case 'HEATER': return '🔥';
        case 'AIR_CONDITIONER': return '❄️';
        case 'HUMIDIFIER': return '💧';
        case 'DEHUMIDIFIER': return '🌬️';
        case 'VENTILATOR': return '💨';
        default: return '🔌';
    }
}

function getDeviceTypeName(type) {
    switch (type) {
        case 'HEATER': return 'Обогреватель';
        case 'AIR_CONDITIONER': return 'Кондиционер';
        case 'HUMIDIFIER': return 'Увлажнитель';
        case 'DEHUMIDIFIER': return 'Осушитель';
        case 'VENTILATOR': return 'Вентилятор';
        default: return 'Другое устройство';
    }
}

document.getElementById('addDeviceForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;

    try {
        submitBtn.textContent = 'Добавление...';
        submitBtn.disabled = true;

        const deviceData = {
            name: document.getElementById('deviceName').value,
            location: "Гостиная",
            type: document.getElementById('deviceType').value,
            status: 'OFF',
            mode: 'AUTO',
            powerLevel: 0,
            isConnected: true
        };

        await apiCall(`/smarthome/${smartHomeId}/devices`, {
            method: 'POST',
            body: JSON.stringify(deviceData)
        });

        hideAddDeviceModal();
        await loadDevices();
        document.getElementById('addDeviceForm').reset();
        showNotification('✅ Устройство успешно добавлено');
    } catch (error) {
        console.error('Ошибка добавления устройства:', error);
        showNotification('❌ Ошибка добавления устройства', 'error');
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
});

function showAddDeviceModal() {
    document.getElementById('addDeviceModal').classList.remove('hidden');
}

function hideAddDeviceModal() {
    document.getElementById('addDeviceModal').classList.add('hidden');
    document.getElementById('addDeviceForm').reset();
}

function hideDeviceDetailModal() {
    document.getElementById('deviceDetailModal').classList.add('hidden');
}

function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#28a745' : '#dc3545'};
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        z-index: 1001;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        transition: opacity 0.3s ease-in-out;
    `;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => notification.remove(), 300);
    }, 4000);
}