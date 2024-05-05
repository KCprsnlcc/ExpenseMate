<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "synthegratech";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $email = $_POST['email'];

    $sql = "DELETE FROM useraccounts WHERE email = '$email'";

    if ($conn->query($sql) === TRUE) {
        echo "Account deleted successfully";
    } else {
        echo "Error deleting account: " . $conn->error;
    }
}

$conn->close();
?>