<?php

include 'connect.php';

$query = "SELECT * FROM product";
$result = mysqli_query($con, $query);

$products = array();

while ($row = mysqli_fetch_assoc($result)) {
    $product = array();
    $product['id'] = $row['id'];
    $product['name'] = $row['title'];
    $product['status'] = "Available";
    $product['price'] = $row['price'];
    $product['discount'] = 0;
    $product['quantity'] = 1; 
    $product['stock'] = $row['stock'];
    $product['image'] = $row['image_url'];

    $products[] = $product;
}

header('Content-Type: application/json');

$response = [
    "status" => "success",
    "products" => $products
];

echo json_encode($response);

mysqli_close($con);
?>
