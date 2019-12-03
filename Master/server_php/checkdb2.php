<?php
    header('Content-Type: text/html; charset=UTF-8');
    include('dbcon.php');

    //안드로이드에서 전송받은 자동차 번호
    $carNo = $_POST['carNo'];
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $sql = "SELECT * FROM car where carNo = '$carNo'";
        $result = mysqli_query($conn, $sql);
        $row = mysqli_fetch_assoc($result);

        if(empty($row)){
            $data = array();
            $data[result] = '0';
            $return_json = json_encode($data);
            echo $return_json;
        } else{
            $data = array();
            $data[result] = '1';
            $return_json = json_encode($data);
            echo $return_json;
        }
    } else{ 
        echo "전송받은 값이 없습니다.";
    }
    include('dbclose.php');
?>

