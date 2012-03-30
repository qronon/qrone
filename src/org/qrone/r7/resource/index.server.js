var f = new java.io.File(".");

$("#homepath").html(f.absoluteFile.parentFile.absolutePath);