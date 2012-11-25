
header("Content-Type: text/html; charset=utf8");

var t = load_template("include-part.html");
//t.select("#frame").html($("#frame"));

//$("#rightcolumn").html(load_template("include-part2.html"));
$("#frame").html(t);

