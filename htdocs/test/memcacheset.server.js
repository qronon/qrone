
memcached.test.put("testkey","test89");

document.write({
	"status" : "OK",
	"mem" : memcached.test.get("testkey")
});