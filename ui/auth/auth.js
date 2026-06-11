 // ── Password toggle ──────────────────────────────────────────
function togglePassword(inputId, icon) {
  var input = document.getElementById(inputId);

  if (input.type === "password") {
    input.type       = "text";
    icon.textContent = "👁️";
  } else {
    input.type       = "password";
    icon.textContent = "🙈";
  }
}

// ── Message helper ───────────────────────────────────────────
function showAuthMessage(elementId, text, color) {
  var box = document.getElementById(elementId);
  if (box) {
    box.style.color = color;
    box.innerText   = text;
  }
}

// ── Button loading state (prevents double submit) ────────────
function setButtonLoading(btnId, isLoading) {
  var btn = document.getElementById(btnId);
  if (!btn) return;

  if (isLoading) {
    btn.disabled  = true;
    btn.innerText = "Please wait...";
  } else {
    btn.disabled  = false;
    btn.innerText = btn.getAttribute("data-label");
  }
}

window.addEventListener("load", function () {
  var loginBtn  = document.getElementById("loginBtn");
  var signupBtn = document.getElementById("signupBtn");

  if (loginBtn)  loginBtn.setAttribute("data-label", loginBtn.innerText);
  if (signupBtn) signupBtn.setAttribute("data-label", signupBtn.innerText);
});

// ── Validation helpers ───────────────────────────────────────
function isValidEmail(email) {
  return email.includes("@") && email.includes(".");
}

function isValidPassword(password) {
  return password.length >= 6;
}

// ── JWT Decoder ──────────────────────────────────────────────
// Decodes the JWT token payload to get role, email etc.
// JWT structure: header.payload.signature
// Payload is base64 encoded — we decode it to read the claims
function decodeToken(token) {
  try {
    var payload = token.split(".")[1];
    var decoded = JSON.parse(atob(payload));
    return decoded;
  } catch (error) {
    console.error("Token decode error:", error);
    return null;
  }
}

// ── SIGNUP ───────────────────────────────────────────────────
// POST USER_SERVICE/auth/register
var signupForm = document.getElementById("signupForm");

if (signupForm) {
  signupForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    var fullName        = document.getElementById("signupFullName").value.trim();
    var email           = document.getElementById("signupEmail").value.trim();
    var phone           = document.getElementById("signupPhone").value.trim();
    var password        = document.getElementById("signupPassword").value;
    var confirmPassword = document.getElementById("signupConfirmPassword").value;

    if (!fullName) {
      showAuthMessage("signupMessage", "Full name is required.", "red");
      return;
    }
    if (!isValidEmail(email)) {
      showAuthMessage("signupMessage", "Enter a valid email address.", "red");
      return;
    }
    if (!isValidPassword(password)) {
      showAuthMessage("signupMessage", "Password must be at least 6 characters.", "red");
      return;
    }
    if (password !== confirmPassword) {
      showAuthMessage("signupMessage", "Passwords do not match.", "red");
      return;
    }

    var data = {
      fullName : fullName,
      email    : email,
      phone    : phone,
      password : password
    };

    try {
      setButtonLoading("signupBtn", true);
      showAuthMessage("signupMessage", "Creating account...", "#2563eb");

      var response = await fetch(USER_SERVICE + "/auth/register", {
        method  : "POST",
        headers : { "Content-Type": "application/json" },
        body    : JSON.stringify(data)
      });

      var responseText = await response.text();
      var message = "";

      try {
        var json = JSON.parse(responseText);
        message  = json.message || responseText;
      } catch {
        message = responseText;
      }

      if (response.ok) {
        showAuthMessage("signupMessage", message || "Account created! Redirecting...", "green");
        signupForm.reset();

        setTimeout(function () {
          window.location.href = "login.html";
        }, 1500);
      } else {
        showAuthMessage("signupMessage", message || "Signup failed. Please try again.", "red");
      }

    } catch (error) {
      showAuthMessage("signupMessage", "Cannot connect to server. Please try again later.", "red");
      console.error("Signup error:", error);
    } finally {
      setButtonLoading("signupBtn", false);
    }
  });
}

// ── LOGIN ────────────────────────────────────────────────────
// POST USER_SERVICE/auth/login
// Response: { token }
// We decode the JWT to get: sub (email), role
// role = "admin" → /admin/admin.html
// role = "user"  → /customer/home.html
var loginForm = document.getElementById("loginForm");

if (loginForm) {
  loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    var email    = document.getElementById("loginEmail").value.trim();
    var password = document.getElementById("loginPassword").value;

    if (!isValidEmail(email)) {
      showAuthMessage("loginMessage", "Enter a valid email address.", "red");
      return;
    }
    if (!password) {
      showAuthMessage("loginMessage", "Password is required.", "red");
      return;
    }

    var data = {
      email    : email,
      password : password
    };

    try {
      setButtonLoading("loginBtn", true);
      showAuthMessage("loginMessage", "Logging in...", "#2563eb");

      var response = await fetch(USER_SERVICE + "/auth/login", {
        method  : "POST",
        headers : { "Content-Type": "application/json" },
        body    : JSON.stringify(data)
      });

      var responseText = await response.text();
      var result  = {};
      var message = "";

      try {
        result  = JSON.parse(responseText);
        message = result.message || "";
      } catch {
        message = responseText;
      }

      if (!response.ok) {
        showAuthMessage("loginMessage", message || "Login failed. Check your credentials.", "red");
        return;
      }

      // Decode JWT to extract role and email from token claims
      // JWT payload contains: { sub: "email", role: "admin", iat: ..., exp: ... }
      var decoded = decodeToken(result.token);

      if (!decoded) {
        showAuthMessage("loginMessage", "Login failed. Invalid token.", "red");
        return;
      }

      // Save to localStorage — used by getHeaders() and guards in utils.js
      localStorage.setItem("authToken",  result.token   || "");
      localStorage.setItem("userEmail",  decoded.sub    || email);
      localStorage.setItem("userRole",   decoded.role   || "");
      localStorage.setItem("adminUserId", decoded.userId || "");

      showAuthMessage("loginMessage", "Login successful! Redirecting...", "green");

      // Redirect based on role from JWT
      setTimeout(function () {
        if (decoded.role === "admin") {
          window.location.href = "/admin/admin.html";
        } else {
          window.location.href = "/customer/home.html";
        }
      }, 800);

    } catch (error) {
      showAuthMessage("loginMessage", "Cannot connect to server. Please try again later.", "red");
      console.error("Login error:", error);
    } finally {
      setButtonLoading("loginBtn", false);
    }
  });
}