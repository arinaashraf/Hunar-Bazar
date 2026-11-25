<?php

include 'connect.php';

$response = array();


$title = isset($_POST['title']) ? $_POST['title'] : '';
$description = isset($_POST['description']) ? $_POST['description'] : '';
$price = isset($_POST['price']) ? $_POST['price'] : '';
$stock = isset($_POST['stock']) ? $_POST['stock'] : '';

$image_url = '';

if (isset($_FILES['image'])) {
    $target_dir = "uploads/"; 
    if (!file_exists($target_dir)) {
        mkdir($target_dir, 0777, true); 
    }
    $target_file = $target_dir . basename($_FILES["image"]["name"]);
    
   
    if (move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)) {
        $image_url = $target_file; 
    }
}


$stmt = $con->prepare("INSERT INTO product (title, description, price, stock, image_url) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("ssdis", $title, $description, $price, $stock, $image_url);


if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Product added"]);
} else {
    echo json_encode(["success" => false, "message" => "Error: " . $stmt->error]);
}


$stmt->close();

?>
