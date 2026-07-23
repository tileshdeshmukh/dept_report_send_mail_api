// ===== Shared config & helpers used by every page =====

const API_BASE = "http://localhost:8080"; // same origin as the app (served from Spring Boot static folder)

const AUTH_TOKEN_KEY = "emd_dept_token";
const AUTH_USER_KEY = "emd_dept_username";

function saveSession(token, username) {
  localStorage.setItem(AUTH_TOKEN_KEY, token);
  localStorage.setItem(AUTH_USER_KEY, username);
}

function getToken() {
  return localStorage.getItem(AUTH_TOKEN_KEY);
}

function getUsername() {
  return localStorage.getItem(AUTH_USER_KEY) || "";
}

function clearSession() {
  localStorage.removeItem(AUTH_TOKEN_KEY);
  localStorage.removeItem(AUTH_USER_KEY);
}

// Redirect to login if there's no token saved. Call this at the top of protected pages.
function requireAuth() {
  if (!getToken()) {
    window.location.href = "login.html";
  }
}

// Generic API call wrapper.
// Automatically attaches the Authorization header (raw token, no "Bearer " prefix,
// matching AuthFilter on the backend) and parses the ApiResponse<T> JSON shape.
async function apiCall(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json" };
  const token = getToken();
  if (token) {
    headers["Authorization"] = token;
  }

  const response = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  let data = null;
  try {
    data = await response.json();
  } catch (e) {
    // some endpoints (e.g. sendReport) may return plain text instead of JSON
    data = null;
  }

  if (response.status === 401) {
    clearSession();
    window.location.href = "login.html";
    throw new Error("Session expired. Please login again.");
  }

  if (!response.ok) {
    const message = (data && data.message) || "Something went wrong. Please try again.";
    throw new Error(message);
  }

  return data;
}

// ===== Small toast helper (Bootstrap toast, created on the fly) =====
function showToast(message, variant = "success") {
  let host = document.getElementById("toastHost");
  if (!host) {
    host = document.createElement("div");
    host.id = "toastHost";
    document.body.appendChild(host);
  }

  const toastEl = document.createElement("div");
  toastEl.className = `toast align-items-center text-bg-${variant} border-0 mb-2`;
  toastEl.setAttribute("role", "alert");
  toastEl.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${message}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
    </div>`;
  host.appendChild(toastEl);

  const toast = new bootstrap.Toast(toastEl, { delay: 3500 });
  toast.show();
  toastEl.addEventListener("hidden.bs.toast", () => toastEl.remove());
}
