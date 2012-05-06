<?php
    require "db.php";
    header("Content-type: application/json");
?>
<?php
    $json = array(
        "results" => array()
    );
    if ($_GET["method"] === "artist") {
        $con = connect_to_db();
        $query = mysql_query("SELECT * FROM artist WHERE name='" . $_GET["name"] . "%'");
        while ($row = mysql_fetch_array($query)) {
            array_push($json["results"], $row["name"]);
        }
        mysql_close($con);
    }
    else {
        $json["error"] = "TODO: make nice messaging for invalid service request";
    }
?>
<?php
    echo json_encode($json);
?>
