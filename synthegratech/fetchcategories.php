<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "synthegratech";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$email = $_POST['email'];

$sql = "SELECT category, COUNT(*) AS count FROM expenses WHERE account = ? GROUP BY category";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);

$stmt->execute();

$result = $stmt->get_result();

$data = array();
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);

$stmt->close();
$conn->close();
?>
