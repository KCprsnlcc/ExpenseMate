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
    $sql = "SELECT * FROM useraccounts WHERE email = '$email'";

    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        echo "Email exists";
    } else {
        echo "Email does not exist";
    }
}
$conn->close();
?>