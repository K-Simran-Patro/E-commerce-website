const API_URL = "https://e-commerce-website-620c.onrender.com";

/*
Expected backend APIs:

POST /api/auth/signup
POST /api/auth/login

Login response should return:
{
  "token": "jwt-token-here",
  "userId": "uuid-here",
  "email": "admin@example.com",
  "role": "ADMIN"
}
*/

// SIGNUP
const signupForm = document.getElementById("signupForm");

if (signupForm) {
  signupForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const data = {
      fullName: document.getElementById("signupFullName").value,
      email: document.getElementById("signupEmail").value,
      phone: document.getElementById("signupPhone").value,
      password: document.getElementById("signupPassword").value
    };

    try {
      const response = await fetch(API_URL + "/api/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      });

      if (response.ok) {
        document.getElementById("signupMessage").style.color = "green";
        document.getElementById("signupMessage").innerText =
          "Signup successful. Please login.";

        signupForm.reset();

        setTimeout(function () {
          window.location.href = "login.html";
        }, 1500);
      } else {
        const errorText = await response.text();
        document.getElementById("signupMessage").innerText = errorText;
      }
    } catch (error) {
      document.getElementById("signupMessage").innerText =
        "Signup failed. Please try again.";
    }
  });
}

// LOGIN
const loginForm = document.getElementById("loginForm");

if (loginForm) {
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const data = {
      email: document.getElementById("loginEmail").value,
      password: document.getElementById("loginPassword").value
    };

    try {
      const response = await fetch(API_URL + "/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
      });

      if (!response.ok) {
        const errorText = await response.text();
        document.getElementById("loginMessage").innerText = errorText;
        return;
      }

      const result = await response.json();

      localStorage.setItem("authToken", result.token);
      localStorage.setItem("adminUserId", result.userId);
      localStorage.setItem("userEmail", result.email);
      localStorage.setItem("userRole", result.role);

      if (result.role === "ADMIN") {
        window.location.href = "admin.html";
      } else {
        window.location.href = "main.html";
      }

    } catch (error) {
      document.getElementById("loginMessage").innerText =
        "Login failed. Please try again.";
    }
  });
}