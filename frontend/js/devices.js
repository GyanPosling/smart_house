// Загрузка устройств
async function loadDevices() {
    try {
        const devices = await apiCall('/smart_house/devices');
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

// Отображение устройств
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
        <div class="device-card ${device.status === 'ON' ? 'on' : 'off'} ${device.mode === 'AUTO' ? 'auto' : ''}">
            <div class="device-header">
                <div class="device-title">${device.name}</div>
                <span class="device-status ${device.status === 'ON' ? 'status-on' : 'status-off'} ${device.mode === 'AUTO' ? 'status-auto' : ''}">
                    ${device.mode === 'AUTO' ? 'АВТО' : (device.status === 'ON' ? 'ВКЛ' : 'ВЫКЛ')}
                </span>
            </div>
            <div class="device-info">
                <p><strong>Тип:</strong> ${getDeviceTypeName(device.type)}</p>
                <p><strong>Режим:</strong> ${device.mode === 'AUTO' ? '🤖 Автоматический' : '👋 Ручной'}</p>
                <p><strong>ID:</strong> ${device.id}</p>
            </div>
            <div class="device-controls">
                <button class="btn btn-success btn-small" onclick="toggleDevice(${device.id}, 'ON')" ${device.status === 'ON' ? 'disabled' : ''}>
                    🔌 Включить
                </button>
                <button class="btn btn-danger btn-small" onclick="toggleDevice(${device.id}, 'OFF')" ${device.status === 'OFF' ? 'disabled' : ''}>
                    🔋 Выключить
                </button>
                <button class="btn btn-warning btn-small" onclick="setAutoMode(${device.id})" ${device.mode === 'AUTO' ? 'disabled' : ''}>
                    🤖 Авторежим
                </button>
            </div>
        </div>
    `).join('');
}

// Управление устройствами
async function toggleDevice(deviceId, status) {
    try {
        const endpoint = status === 'ON' ? 'on' : 'off';
        await apiCall(`/smart_house/devices/${deviceId}/${endpoint}`, {
            method: 'PATCH'
        });

        await loadDevices(); // Обновляем список устройств
        showNotification(`✅ Устройство ${status === 'ON' ? 'включено' : 'выключено'}`);
    } catch (error) {
        console.error('Ошибка управления устройством:', error);
        showNotification('❌ Ошибка управления устройством', 'error');
    }
}

async function setAutoMode(deviceId) {
    try {
        await apiCall(`/smart_house/devices/${deviceId}/auto`, {
            method: 'PATCH'
        });

        await loadDevices();
        showNotification('✅ Авторежим включен');
    } catch (error) {
        console.error('Ошибка установки авторежима:', error);
        showNotification('❌ Ошибка установки авторежима', 'error');
    }
}

// Добавление устройства
document.getElementById('addDeviceForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;

    try {
        submitBtn.textContent = 'Добавление...';
        submitBtn.disabled = true;

        const deviceData = {
            name: document.getElementById('deviceName').value,
            type: document.getElementById('deviceType').value
        };

        await apiCall('/smart_house/devices', {
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

// Модальные окна устройств
function showAddDeviceModal() {
    document.getElementById('addDeviceModal').classList.remove('hidden');
}

function hideAddDeviceModal() {
    document.getElementById('addDeviceModal').classList.add('hidden');
    document.getElementById('addDeviceForm').reset();
}

// Уведомления
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
        box-shadow: 0 5px 15px rgba(0,0,0,0.3);
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
}