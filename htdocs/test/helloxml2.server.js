
var xml = <result />;
xml.@status = "OK";

var id = "1234";
xml.* += <item test={id}></item>;

document.write(xml);