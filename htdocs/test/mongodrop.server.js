
db.test.drop();

document.write({
	"status" : "OK",
	"list" : db.test.find().toArray()
});