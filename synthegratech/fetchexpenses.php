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
$sql = "SELECT 
            YEAR(date) AS year, 
            WEEK(date) AS week, 
            SUM(amount) AS total_amount, 
            MIN(date) AS start_date, 
            MAX(date) AS end_date 
        FROM 
            expenses 
        WHERE 
            account = ? 
        GROUP BY 
            YEAR(date), 
            WEEK(date)";

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
