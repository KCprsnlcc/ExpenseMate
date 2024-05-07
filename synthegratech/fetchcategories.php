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

$total_count_sql = "SELECT COUNT(*) AS total_count FROM expenses WHERE account = ?";
$total_stmt = $conn->prepare($total_count_sql);
$total_stmt->bind_param("s", $email);
$total_stmt->execute();
$total_result = $total_stmt->get_result();
$total_row = $total_result->fetch_assoc();
$total_count = $total_row['total_count'];

$category_count_sql = "SELECT category, COUNT(*) AS count FROM expenses WHERE account = ? GROUP BY category";
$category_stmt = $conn->prepare($category_count_sql);
$category_stmt->bind_param("s", $email);
$category_stmt->execute();
$category_result = $category_stmt->get_result();

$data = array();
while ($row = $category_result->fetch_assoc()) {
    $category = $row['category'];
    $count = $row['count'];
    $percentage = ($count / $total_count) * 100;
    $percentage_decimal = number_format($percentage, 2);
    $data[] = array(
        "category" => $category,
        "count" => $count,
        "percentage" => $percentage_decimal
    );
}
echo json_encode($data);

$total_stmt->close();
$category_stmt->close();
$conn->close();
?>
