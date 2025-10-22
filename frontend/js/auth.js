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

        const contentType = response.headers.get('content-type');
        let errorMessage = 'Ошибка регистрации';

        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            if (response.ok) {
                alert('✅ ' + (data.message || 'Регистрация успешна! Теперь войдите в систему.'));
                showLogin();
                document.getElementById('registerForm').reset();
                return;
            } else {
                errorMessage = data.error || data.message || 'Неизвестная ошибка';
            }
        } else {
            const text = await response.text();
            errorMessage = text || 'Ошибка сервера';
        }

        alert('❌ Ошибка регистрации: ' + errorMessage);

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

        const contentType = response.headers.get('content-type');

        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();

            if (response.ok) {
                token = data.jwt;
                userId = data.userId;
                localStorage.setItem('token', token);
                localStorage.setItem('username', loginData.name);
                localStorage.setItem('userId', userId);

                showMainPage();
                document.getElementById('loginForm').reset();
                return;
            } else {
                const errorMessage = data.error || data.message || 'Неизвестная ошибка';
                alert('❌ Ошибка входа: ' + errorMessage);
            }
        } else {
            const text = await response.text();
            alert('❌ Ошибка входа: ' + text);
        }

    } catch (error) {
        alert('❌ Ошибка сети или сервера: ' + error.message);
    } finally {
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    }
});

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
        lastLogin: new Date().toLocaleDateString('ru-RU')
    };
    document.getElementById('userWelcome').textContent = `Добро пожаловать, ${currentUser.name}!`;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('userId');
    token = null;
    userId = null;
    currentUser = null;
    if (updateInterval) clearInterval(updateInterval);
    showLandingPage();
}