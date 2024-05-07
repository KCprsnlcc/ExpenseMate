<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "synthegratech";
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$name = $_POST['name'];
$email = $_POST['email'];
$password = $_POST['password'];
$type = "user";
$stmt = $conn->prepare("INSERT INTO useraccounts (name, email, password, type) VALUES (?, ?, ?, ?)");
$stmt->bind_param("ssss", $name, $email, $password, $type);

if ($stmt->execute() === TRUE) {
    echo "Registered! you can now login";
} else {
    echo "Registration failed! Please try again later.";
}
$stmt->close();
$conn->close();
?>