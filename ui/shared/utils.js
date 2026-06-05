// Only ADMIN role can access this page
function guardAdmin() {
  var token = localStorage.getItem("authToken");
  var role  = localStorage.getItem("userRole");

  if (!token || role !== "ADMIN") {
    window.location.href = "/auth/login.html";
  }
}

// Any logged-in user can access this page
function guardUser() {
  var token = localStorage.getItem("authToken");

  if (!token) {
    window.location.href = "/auth/login.html";
  }
}

// Clear storage and go to login
function logout() {
  localStorage.clear();
  window.location.href = "/auth/login.html";
}

// Builds headers for every API call
// Authorization: Bearer token  →  JWT authentication
// X-User-Name: email           →  audit logging in backend
function getHeaders() {
  var token    = localStorage.getItem("authToken") || "";
  var userName = localStorage.getItem("userEmail") || "";

  var headers = {
    "Content-Type": "application/json"
  };

  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  if (userName) {
    headers["X-User-Name"] = userName;
  }

  return headers;
}

// If backend returns 401, token expired — auto logout
function handleUnauthorized(status) {
  if (status === 401) {
    alert("Session expired. Please login again.");
    logout();
    return true;
  }
  return false;
}