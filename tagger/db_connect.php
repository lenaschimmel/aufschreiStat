<?php

$mysqlhost="localhost";
$mysqluser="aufschrei";
$mysqlpwd="aufschrei";
$mysqldb="aufschrei";
$mysqlport="8889";

@mysql_connect($mysqlhost.":".$mysqlport, $mysqluser, $mysqlpwd) or die ('fuck');
@mysql_select_db($mysqldb);