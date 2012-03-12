<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	</head>
	
	
<?php
	$link = mysql_connect('localhost', 'pkniola', '11111111');
	
	if (!$link) {
		die('Nie mo¿na siê po³aczyæ: ' . mysql_error());
	}	
	
	// Ustaw foo jako aktualn¹ bazê danych
	$db_selected = mysql_select_db('pkniola_db', $link);
	if (!$db_selected) {
		die ('Nie mo¿na ustawiæ foo : ' . mysql_error());
	}	
	
	$result = mysql_query('SELECT * FROM S28D466A103000049 ORDER BY dateOfCreation DESC LIMIT 1');
	$num = mysql_numrows($result);
	
echo '<center>';
echo '<table style="font-family:arial;font-size:48px;" border="1">';

$i=0;
while ($i < $num) {

$date = mysql_result($result,$i,"dateOfCreation");
$value = mysql_result($result,$i,"value");

echo "<tr><td>$date</td><td>$value</td></tr>";

$i++;
}	

echo '</table></center>';

	/*
	echo '<center>';
	
	$date_res = $db->query('SELECT dateOfCreation FROM samples ORDER BY dateOfCreation DESC LIMIT 1');
		if ( $date_entry = $date_res->fetchArray(SQLITE3_ASSOC) ) {
			date_default_timezone_set ( 'Europe/Warsaw' );
			echo  '<p style="font-family:arial;font-size:32px;">' . date('l jS \of F Y', $date_entry['dateOfCreation'] / 1000) . '<br />';
			echo date('H:i:s', $date_entry['dateOfCreation'] / 1000) . '</p>';
		}	
		
	echo '<table style="font-family:arial;font-size:48px;" border="1">';		
	
	while ($sensors_entry = $sensors_res->fetchArray(SQLITE3_ASSOC)) {
		echo '<tr><td>' . $sensors_entry['name'] . '</td>';
		
		$sample_res = $db->query('SELECT * FROM samples WHERE sensorIndex = '.$sensors_entry['i'].' ORDER BY dateOfCreation DESC LIMIT 1');
		if ( $sample_entry = $sample_res->fetchArray(SQLITE3_ASSOC) ) {
			echo '<td>' . $sample_entry['value'] . '</td></tr>';
		}
	}
	
	echo '</table><p><a href="http://kni-electronics.eu">KNI Electronics</a></p></center>';
	*/
?>
</html>