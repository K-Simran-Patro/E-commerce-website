const API_URL = "https://e-commerce-website-user-service.onrender.com";

/* ================= PASSWORD EYE TOGGLE ================= */

function togglePassword(inputId, icon) {
  const input = document.getElementById(inputId);

  if (input.type === "password") {
    input.type = "text";
    icon.textContent = "👁️"; // visible
  } else {
    input.type = "password";
    icon.textContent = "🙈"; // hidden
  }
}

/* ================= MESSAGE HELPERS ================= */

function showSignupMessage(text, color) {
  const messageBox = document.getElementById("signupMessage");

  if (messageBox) {
    messageBox.style.color = color;
    messageBox.innerText = text;
  }
}

function showLoginMessage(text, color) {
  const messageBox = document.getElementById("loginMessage");

  if (messageBox) {
    messageBox.style.color = color;
    messageBox.innerText = text;
  }
}

/* ================= SIGNUP ================= */

const signupForm = document.getElementById("signupForm");

if (signupForm) {
  signupForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const fullName = document.getElementById("signupFullName").value.trim();
    const email = document.getElementById("signupEmail").value.trim();
    const phone = document.getElementById("signupPhone").value.trim();
    const password = document.getElementById("signupPassword").value;
    const confirmPassword = document.getElementById("signupConfirmPassword").value;

    if (password !== confirmPassword) {
      showSignupMessage("Password and Confirm Password do not match.", "red");
      return;
    }

    const data = {
      fullName: fullName,
      email: email,
      phone: phone,
      password: password
    };

    try {
      showSignupMessage("Creating account...", "#2563eb");

      const response = await fetch(API_URL + "/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      });

      const responseText = await response.text();

      let message = "";

      try {
        const json = JSON.parse(responseText);
        message = json.message || responseText;
      } catch {
        message = responseText;
      }

      if (response.ok) {
        showSignupMessage(message || "Signup successful. Please login.", "green");

        signupForm.reset();

        setTimeout(function () {
          window.location.href = "login.html";
        }, 1500);
      } else {
        showSignupMessage(message || "Signup failed.", "red");
      }

    } catch (error) {
      showSignupMessage("Unable to connect to server. Check backend/CORS.", "red");
      console.error(error);
    }
  });
}

/* ================= LOGIN ================= */

const loginForm = document.getElementById("loginForm");

if (loginForm) {
  loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const data = {
      email: document.getElementById("loginEmail").value.trim(),
      password: document.getElementById("loginPassword").value
    };

    try {
      showLoginMessage("Logging in...", "#2563eb");

      const response = await fetch(API_URL + "/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      });

      const responseText = await response.text();

      let result = {};
      let message = "";

      try {
        result = JSON.parse(responseText);
        message = result.message || "";
      } catch {
        message = responseText;
      }

      if (!response.ok) {
        showLoginMessage(message || "Login failed.", "red");
        return;
      }

      localStorage.setItem("authToken", result.token || "");
      localStorage.setItem("adminUserId", result.userId || "");
      localStorage.setItem("userEmail", result.email || data.email);
      localStorage.setItem("userRole", result.role || "");

      if (result.role === "ADMIN") {
        window.location.href = "admin.html";
      } else {
        window.location.href = "main.html";
      }

    } catch (error) {
      showLoginMessage("Unable to connect to server. Check backend/CORS.", "red");
      console.error(error);
    }
  });
}