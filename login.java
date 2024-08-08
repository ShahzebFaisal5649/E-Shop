document.addEventListener('DOMContentLoaded', function() {
  const loginForm = document.getElementById('loginForm');
  const passwordField = document.getElementById('password');
  const showPasswordCheckbox = document.getElementById('show-password');
  const passwordStrengthElement = document.getElementById('password-strength');
  const loginFeedbackElement = document.getElementById('login-feedback');

  // Toggle password visibility
  showPasswordCheckbox.addEventListener('change', function() {
    passwordField.type = this.checked ? 'text' : 'password';
  });

  // Check password strength
  function checkPasswordStrength(password) {
    let strength = 0;
    if (password.length > 5) strength++;
    if (password.length > 8) strength++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[^a-zA-Z\d]/.test(password)) strength++;

    let strengthText = '';
    let strengthClass = '';
    switch (strength) {
      case 1:
        strengthText = 'Weak';
        strengthClass = 'weak';
        break;
      case 2:
        strengthText = 'Medium';
        strengthClass = 'medium';
        break;
      case 3:
        strengthText = 'Strong';
        strengthClass = 'strong';
        break;
      default:
        strengthText = '';
        strengthClass = '';
    }

    passwordStrengthElement.textContent = `Password Strength: ${strengthText}`;
    passwordStrengthElement.className = `password-strength ${strengthClass}`;
    passwordStrengthElement.style.display = strengthText ? 'block' : 'none';
  }

  passwordField.addEventListener('input', function() {
    checkPasswordStrength(this.value);
  });

  // Handle form submission
  loginForm.addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the form from submitting normally

    const username = document.getElementById('username').value;
    const password = passwordField.value;

    // Example validation (replace with actual server-side validation)
    fetch('/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        loginFeedbackElement.textContent = 'Login successful! Redirecting...';
        loginFeedbackElement.className = 'text-success';
        setTimeout(function() {
          window.location.href = 'dashboard.html'; // Redirect to a dashboard or another page
        }, 2000);
      } else {
        loginFeedbackElement.textContent = 'Invalid username or password.';
        loginFeedbackElement.className = 'text-danger';
      }
    })
    .catch(error => {
      loginFeedbackElement.textContent = 'An error occurred. Please try again.';
      loginFeedbackElement.className = 'text-danger';
    });
  });
});
