
var f = new java.io.File(".");
document.set("#homepath", f.absoluteFile.parentFile.absolutePath);
document.out();