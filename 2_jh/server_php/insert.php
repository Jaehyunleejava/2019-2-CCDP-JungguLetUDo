<?php
        include('dbcon.php');

        $carNo = $_POST['carNo'];
        $sql="INSERT INTO car(carNo) VALUES('$carNo')";
        if($carNo != ""){
            mysqli_query($conn, $sql);
            echo "Success Insert Data";
        } else {
            echo "No data";
        }
        
        include('dbclose.php');
?>