<?php
    require "db.php";
    header("Content-type: application/json");
?>
<?php
    $con = connect_to_db();
    if ($_GET["method"] === "artist") {
        $query = mysql_query("SELECT * FROM artist WHERE name='" . $_GET["name"] . "%'");
        $json = [];
        while ($row = mysql_fetch_array($query)) {
            array_push($json, $row["name"]);
        }
    }
    else {
        $json = [
            "abc" => 12,
            "foo" => "bar",
            "bool0" => false,
            "bool1" => true,
            "arr" => array(1, 2, 3, null, 5),
            "float" => 1.2345
        ];
    }
    mysql_close($con);
?>
<?php
    echo json_encode($json);
?>
