
<html>

<body>
<form action="checkdb2.php" method="post">
        carNo:<input type="text" name="carNo"><br>
        <input type="submit" value="제출"><br>
</form>

<h2>JSON 파일test</h2>
<?php
        $json = file_get_contents('test.json');
        $resultJson = urldecode($json);
        echo $resultJson;
?>
</body>
</html>
