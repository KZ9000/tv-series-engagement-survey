/* ============================================
   TV Series Engagement Survey — Frontend Logic
   ============================================ */

const API = '/api';
const TOKEN_KEY = 'jwt_token';

/* ---- Auth helpers ---- */
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

function isAuthenticated() {
    return !!getToken();
}

function logout() {
    clearToken();
    window.location.href = '/login';
}

/* ---- Fetch wrapper ---- */
async function api(path, options = {}) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    const res = await fetch(API + path, { ...options, headers });

    if (res.status === 401 && path !== '/auth/login' && path !== '/auth/register') {
        clearToken();
        window.location.href = '/login';
        return;
    }

    const text = await res.text();
    let data = null;
    if (text) {
        try { data = JSON.parse(text); } catch (_) { data = text; }
    }
    if (!res.ok) {
        const msg = (data && data.message) ? data.message : 'Error del servidor';
        throw { status: res.status, message: msg };
    }
    return data;
}

/* ---- UI helpers ---- */
function showAlert(id, message, type) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = message;
    el.className = 'alert show alert-' + type;
}

function hideAlert(id) {
    const el = document.getElementById(id);
    if (el) el.className = 'alert';
}

function setLoading(btnId, loading) {
    const btn = document.getElementById(btnId);
    if (!btn) return;
    btn.disabled = loading;
    btn.textContent = loading ? 'Cargando...' : btn.dataset.originalText || btn.textContent;
}

/* ---- Nav ---- */
function updateNav() {
    const navAuth = document.getElementById('nav-auth');
    const navGuest = document.getElementById('nav-guest');
    if (!navAuth && !navGuest) return;
    if (isAuthenticated()) {
        if (navAuth) navAuth.style.display = 'flex';
        if (navGuest) navGuest.style.display = 'none';
    } else {
        if (navAuth) navAuth.style.display = 'none';
        if (navGuest) navGuest.style.display = 'flex';
    }
}

/* ---- Auth: Login ---- */
async function handleLogin(e) {
    e.preventDefault();
    hideAlert('alert');
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    if (!email || !password) {
        showAlert('alert', 'Completa todos los campos.', 'error');
        return;
    }
    setLoading('btn-submit', true);
    try {
        const data = await api('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        setToken(data.token);
        window.location.href = '/series';
    } catch (err) {
        const msg = err.status === 401 ? 'Credenciales incorrectas.' : err.message;
        showAlert('alert', msg, 'error');
    } finally {
        setLoading('btn-submit', false);
    }
}

/* ---- Auth: Register ---- */
async function handleRegister(e) {
    e.preventDefault();
    hideAlert('alert');
    hideAlert('alert-success');
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    if (!email || !password) {
        showAlert('alert', 'Completa todos los campos.', 'error');
        return;
    }
    if (password.length < 8) {
        showAlert('alert', 'La contraseña debe tener al menos 8 caracteres.', 'error');
        return;
    }
    setLoading('btn-submit', true);
    try {
        await api('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        showAlert('alert-success', 'Registro exitoso. Ahora puedes iniciar sesión.', 'success');
        document.getElementById('form-register').reset();
    } catch (err) {
        const msg = err.status === 409 ? 'Este email ya está registrado.' : err.message;
        showAlert('alert', msg, 'error');
    } finally {
        setLoading('btn-submit', false);
    }
}

/* ---- Series ---- */
async function loadSeries() {
    const container = document.getElementById('series-list');
    if (!container) return;
    try {
        const series = await api('/series');
        if (series.length === 0) {
            container.innerHTML = '<div class="empty-state">No hay series disponibles.</div>';
            return;
        }
        container.innerHTML = series.map(s => `
            <div class="card">
                <div style="display:flex; justify-content:space-between; align-items:start;">
                    <div class="card-title">${esc(s.title)}</div>
                    <span class="badge ${s.active ? 'badge-active' : 'badge-inactive'}">
                        ${s.active ? 'Activa' : 'Inactiva'}
                    </span>
                </div>
                ${s.description ? '<div class="card-text">' + esc(s.description) + '</div>' : ''}
                <div class="card-meta">
                    <span>Estreno: ${formatDate(s.releaseDate)}</span>
                </div>
                <div style="margin-top:1rem;">
                    <a href="/rate?id=${s.id}&title=${encodeURIComponent(s.title)}"
                       class="btn btn-primary btn-sm"
                       style="width:auto; display:inline-flex;">
                        Calificar
                    </a>
                </div>
            </div>
        `).join('');
    } catch (err) {
        container.innerHTML = '<div class="empty-state">Error al cargar series.</div>';
    }
}

/* ---- Rate ---- */
let selectedScore = 0;

function initRate() {
    const params = new URLSearchParams(window.location.search);
    const seriesId = params.get('id');
    const title = params.get('title');
    if (!seriesId) {
        window.location.href = '/series';
        return;
    }
    document.getElementById('series-title').textContent = title || 'Serie #' + seriesId;
    document.getElementById('series-id').value = seriesId;

    document.querySelectorAll('.score-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.score-btn').forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
            selectedScore = parseInt(btn.dataset.score);
            document.getElementById('score-input').value = selectedScore;
        });
    });
}

async function handleRate(e) {
    e.preventDefault();
    hideAlert('alert');
    hideAlert('alert-success');
    const seriesId = document.getElementById('series-id').value;
    if (!selectedScore) {
        showAlert('alert', 'Selecciona una puntuación del 1 al 5.', 'error');
        return;
    }
    setLoading('btn-submit', true);
    try {
        await api('/ratings', {
            method: 'POST',
            body: JSON.stringify({ seriesId: parseInt(seriesId), score: selectedScore })
        });
        showAlert('alert-success', '¡Calificación registrada!', 'success');
        document.querySelectorAll('.score-btn').forEach(b => b.classList.remove('selected'));
        selectedScore = 0;
    } catch (err) {
        let msg;
        if (err.status === 409) msg = 'Ya calificaste esta serie.';
        else if (err.status === 404) msg = 'Serie no encontrada.';
        else msg = err.message;
        showAlert('alert', msg, 'error');
    } finally {
        setLoading('btn-submit', false);
    }
}

/* ---- Dashboard ---- */
async function loadDashboard() {
    const container = document.getElementById('dashboard-list');
    if (!container) return;
    try {
        const data = await api('/dashboard');
        if (data.length === 0) {
            container.innerHTML = '<div class="empty-state">No hay calificaciones aún.</div>';
            return;
        }
        container.innerHTML = data.map(d => `
            <div class="dash-row">
                <div class="dash-title">${esc(d.title)}</div>
                <div class="dash-stats">
                    <div>
                        <div class="dash-stat-value">${d.averageScore != null ? d.averageScore.toFixed(1) : '-'}</div>
                        <div class="dash-stat-label">Promedio</div>
                    </div>
                    <div>
                        <div class="dash-stat-value">${d.totalVotes}</div>
                        <div class="dash-stat-label">Votos</div>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (err) {
        container.innerHTML = '<div class="empty-state">Error al cargar dashboard.</div>';
    }
}

/* ---- Utils ---- */
function esc(str) {
    const d = document.createElement('div');
    d.textContent = str || '';
    return d.innerHTML;
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    try {
        return new Date(dateStr).toLocaleDateString('es-ES', { year: 'numeric', month: 'short', day: 'numeric' });
    } catch (_) {
        return dateStr;
    }
}

/* ---- Init ---- */
document.addEventListener('DOMContentLoaded', () => {
    updateNav();
});
