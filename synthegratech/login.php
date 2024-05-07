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
    $password = $_POST['password'];

    $sql = "SELECT name, email, type FROM useraccounts WHERE email = '$email' AND password = '$password'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $name = $row["name"];
        $email = $row["email"];
        $type = $row["type"];
        echo "$name,$email,$type";
    } else {
        echo "Login failed";
    }
}
$conn->close();
?>