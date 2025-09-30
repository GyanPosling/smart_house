document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;

    try {
        submitBtn.textContent = 'Регистрация...';
        submitBtn.disabled = true;

        const userData = {
            name: document.getElementById('regUsername').value,
            email: document.getElementById('regEmail').value,
            password: document.getElementById('regPassword').value
        };

        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            alert('✅ Регистрация успешна! Теперь войдите в систему.');
            showLogin();
            document.getElementById('registerForm').reset();
        } else {
            const error = await response.text();
            alert('❌ Ошибка регистрации: ' + error);
        }
    } catch (error) {
        alert('❌ Ошибка сети: ' + error.message);
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
});

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const submitBtn = e.target.querySelector('button[type="submit"]');
    const originalText = submitBtn.textContent;

    try {
        submitBtn.textContent = 'Вход...';
        submitBtn.disabled = true;

        const loginData = {
            name: document.getElementById('loginUsername').value,
            password: document.getElementById('loginPassword').value
        };

        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            const data = await response.json();
            token = data.jwt;
            localStorage.setItem('token', token);
            localStorage.setItem('username', loginData.name);

            await fetchSmartHomeId(data.userId);

            showMainPage();
            document.getElementById('loginForm').reset();
        } else {
            const error = await response.text();
            alert('❌ Ошибка входа: ' + error);
        }
    } catch (error) {
        alert('❌ Ошибка сети или сервера: ' + error.message);
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
});

async function fetchSmartHomeId(userId) {
    try {
        const smartHome = await apiCall(`/smarthome/user/${userId}`);
        smartHomeId = smartHome.id;
        localStorage.setItem('smartHomeId', smartHomeId);
        currentUser.smartHomeName = smartHome.name;
    } catch (error) {
        console.error('Не удалось загрузить ID умного дома для пользователя:', error);
        alert('❌ Ошибка: Умный дом не найден или не создан. Обратитесь к администратору.');
        logout();
        throw error;
    }
}

async function showMainPage() {
    hideAllPages();
    document.getElementById('mainPage').classList.remove('hidden');

    try {
        await loadUserData();
        await loadDevices();
        await loadSensors();

        if (updateInterval) clearInterval(updateInterval);
        updateInterval = setInterval(updateData, 5000);

    } catch (error) {
        console.error('Ошибка загрузки данных:', error);
        alert('❌ Ошибка загрузки данных. Проверьте подключение к серверу.');
    }
}

async function updateData() {
    try {
        await loadSensors();
        await loadDevices();
    } catch (error) {
        console.error('Ошибка обновления данных:', error);
    }
}

async function loadUserData() {
    const username = localStorage.getItem('username') || 'Пользователь';
    currentUser = {
        name: username,
        devicesCount: 0,
        smartHomeName: currentUser ? currentUser.smartHomeName : 'Загрузка...',
        lastLogin: new Date().toLocaleDateString('ru-RU')
    };
    document.getElementById('userWelcome').textContent = `Добро пожаловать, ${currentUser.name}!`;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('smartHomeId');
    token = null;
    smartHomeId = null;
    currentUser = null;
    if (updateInterval) clearInterval(updateInterval);
    showLandingPage();
}