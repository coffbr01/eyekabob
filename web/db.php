<?php
    function connect_to_db() {
        $con = mysql_connect("localhost", "eyekabob", "privateeye");

        if (!$con) {
            die("Could not connect to database. Contact site admin to report the following error: <br/>" . mysql_error());
        }

        mysql_select_db("eyekabob", $con);

        return $con;
    }
?>
