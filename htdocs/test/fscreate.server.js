
fs.write("/test/fstest","{\"test234\":\"test345\"}");

document.write({
	"list" : fs.list(),
});