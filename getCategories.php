<?php

include 'connect.php';

$query = "SELECT * FROM categories";
$result = mysqli_query($con, $query);

$response = array();

while ($row = mysqli_fetch_assoc($result)) {
    $response[] = $row;
}

echo json_encode($response);

?>
