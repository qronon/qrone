
var f = new java.io.File(".");
document.select("#homepath").html(f.absoluteFile.parentFile.absolutePath);
document.out();