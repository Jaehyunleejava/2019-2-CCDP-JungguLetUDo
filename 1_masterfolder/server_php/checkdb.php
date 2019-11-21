<?php

        include('dbcon.php');

        $carNo = $_POST['carNo'];
        $sql = "SELECT * FROM car WHERE carNo = '$carNo'";
        $result = mysqli_query($conn, $sql);
        $row = mysqli_fetch_assoc($result);

        header('Content-Type: application/json');
        header("Content-Type:text/html;charset=utf-8");

        if(empty($row)){
                $data = array();
                $data['result'] = '0';
                $return_json = json_encode($data);
                echo $return_json;
        } else{
                $data = array();
                $data['result'] = '1';
                $return_json = json_encode($data);
                echo $return_json;
        }
        include('dbclose.php');
?>