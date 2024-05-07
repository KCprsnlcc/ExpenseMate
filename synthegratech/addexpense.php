<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "synthegratech";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$email = $_POST['email'];
$category = $_POST['category'];
$amount = $_POST['amount'];

date_default_timezone_set('Asia/Manila');

$date = date("Y-m-d");
$time = date("h:i A", time());

$stmt = $conn->prepare("INSERT INTO expenses (account, category, amount, date, time) VALUES (?, ?, ?, ?, ?)");

$stmt->bind_param("sssss", $email, $category, $amount, $date, $time);

if ($stmt->execute() === TRUE) {
    echo json_encode(array("status" => "success", "message" => "Expense added"));
} else {
    echo json_encode(array("status" => "error", "message" => "Adding expense failed"));
}

$stmt->close();
$conn->close();
?>
