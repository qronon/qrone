
memcached.test.remove("testkey");

document.write({
	"status" : "OK",
	"mem" : memcached.test.get("testkey")
});