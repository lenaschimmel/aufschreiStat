<?php

$mysqlhost="localhost"; 
$mysqluser="aufschrei"; 
$mysqlpwd="aufschrei"; 
$mysqldb="aufschrei"; 
$mysqlport="8889";


$connection=@mysql_connect($mysqlhost.":".$mysqlport, $mysqluser, $mysqlpwd) or die ('fuck');
@mysql_select_db($mysqldb, $connection);
?>