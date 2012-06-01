

db.test.save({"test22" : "test33"});

document.write({
	"status" : "OK",
	"list" : db.test.find().toArray()
});