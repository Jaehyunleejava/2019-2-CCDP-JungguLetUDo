<?php
        $host = 'localhost:3307';
        $user = 'root';
        $pw = '123456';
        $dbName = 'test';

        $conn = mysqli_connect($host, $user, $pw, $dbName);

       if($conn){
                echo "Mysql Success<br>";
        } else{
                echo "Mysql Fail<br>";
        }

?>
