<?php
session_start(); // Start the session for user authentication

// Database configuration
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "your_database";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// CSRF token generation
function generateToken() {
    return bin2hex(random_bytes(32));
}

// CSRF token verification
function verifyToken($token) {
    return isset($_SESSION['csrf_token']) && hash_equals($_SESSION['csrf_token'], $token);
}

// Initialize CSRF token
if (empty($_SESSION['csrf_token'])) {
    $_SESSION['csrf_token'] = generateToken();
}

// Rate limiting settings
$rateLimit = 5; // Max attempts
$rateLimitTime = 15 * 60; // 15 minutes
$ip = $_SERVER['REMOTE_ADDR']; // Client IP
$attemptsKey = 'login_attempts_' . $ip;
$lastAttemptKey = 'last_attempt_' . $ip;

// Rate limiting check
$attempts = isset($_SESSION[$attemptsKey]) ? $_SESSION[$attemptsKey] : 0;
$lastAttempt = isset($_SESSION[$lastAttemptKey]) ? $_SESSION[$lastAttemptKey] : 0;

if ($lastAttempt > 0 && (time() - $lastAttempt) < $rateLimitTime) {
    if ($attempts >= $rateLimit) {
        die("Too many login attempts. Please try again later.");
    }
} else {
    $_SESSION[$attemptsKey] = 0;
}

// Handle POST request
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // CSRF token validation
    if (!isset($_POST['csrf_token']) || !verifyToken($_POST['csrf_token'])) {
        die("Invalid CSRF token.");
    }

    // Sanitize inputs
    $username = filter_input(INPUT_POST, 'username', FILTER_SANITIZE_STRING);
    $password = filter_input(INPUT_POST, 'password', FILTER_SANITIZE_STRING);

    // Basic validation
    if (empty($username) || empty($password)) {
        echo "Please fill in all fields.";
        exit;
    }

    // Prepare and execute SQL query
    $stmt = $conn->prepare("SELECT password_hash FROM users WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->bind_result($passwordHash);
    $stmt->fetch();
    $stmt->close();

    // Check password
    if (password_verify($password, $passwordHash)) {
        $_SESSION['loggedin'] = true;
        echo "success";
    } else {
        $_SESSION[$attemptsKey] = $attempts + 1;
        $_SESSION[$lastAttemptKey] = time();
        echo "Invalid username or password.";
    }
}

// Close database connection
$conn->close();
?>
