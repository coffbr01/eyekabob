<?php
    require "db.php";
    header("Content-type: application/json");
?>
<?php
    $json = array(
        "results" => array()
    );

    $con = connect_to_db();

    if ($_GET["method"] === "artist") {
        array_push($json["verb"], "GET");
        $query = mysql_query("SELECT * FROM artist WHERE name='" . $_GET["name"] . "%'");
        while ($row = mysql_fetch_array($query)) {
            array_push($json["results"], $row["name"]);
        }
    }
    elseif ($_POST["method"] === "artist") {
        array_push($json["verb"], "POST");
        mysql_query("INSERT INTO artist (genre, name, url, bio) VALUES ('" . $_POST["genre"] . "', '" . $_POST["name"] . "', '" . $_POST["url"] ."', '" . $_POST["bio"] .  "')");
        array_push($json["results"], $_POST["name"]);
    }
    else {
        $json["error"] = "Unrecognized request";
    }

    mysql_close($con);
?>
<?php
    echo json_encode($json);
?>
