<?php
        include('dbcon.php');

        $sql = "SELECT * FROM car";
        $result = mysqli_query($conn, $sql);

        header('Content-Type: application/json');
        header("Content-Type:text/html;charset=utf-8");

        $data = array();
        $i = 1;
        while($row = mysqli_fetch_assoc($result)) {
            $middleData = array($i++ => $row['carNo']);
            array_push($data, $middleData);
        }
        $resultJson = urldecode(json_encode($data, JSON_UNESCAPED_UNICODE));
        echo $resultJson;

        include('dbclose.php');
?>
