// Регистрация
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

// Вход
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
            token = await response.text();
            localStorage.setItem('token', token);
            localStorage.setItem('username', loginData.name);
            await showMainPage();
        } else {
            alert('❌ Ошибка входа: Неверные учетные данные');
        }
    } catch (error) {
        alert('❌ Ошибка сети: ' + error.message);
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
});

// Показать главную страницу
async function showMainPage() {
    hideAllPages();
    document.getElementById('mainPage').classList.remove('hidden');

    try {
        await loadUserData();
        await loadDevices();
        await loadSensors();

        // Запускаем обновление данных
        if (updateInterval) clearInterval(updateInterval);
        updateInterval = setInterval(updateData, 5000);

    } catch (error) {
        console.error('Ошибка загрузки данных:', error);
        alert('❌ Ошибка загрузки данных. Проверьте подключение к серверу.');
    }
}

// Обновление данных
async function updateData() {
    try {
        await loadSensors();
        await loadDevices();
    } catch (error) {
        console.error('Ошибка обновления данных:', error);
    }
}

// Загрузка данных пользователя
async function loadUserData() {
    const username = localStorage.getItem('username') || 'Пользователь';
    currentUser = {
        name: username,
        devicesCount: 0,
        lastLogin: new Date().toLocaleDateString('ru-RU')
    };
    document.getElementById('userWelcome').textContent = `Добро пожаловать, ${currentUser.name}!`;
}

// Выход
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    token = null;
    currentUser = null;

    if (updateInterval) {
        clearInterval(updateInterval);
        updateInterval = null;
    }

    showRegister();
    alert('👋 Вы вышли из системы');
}