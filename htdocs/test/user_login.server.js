
user.login("d789037d-379f-4c5b-990f-eaa9091ba4c9");

document.write({
	"status" : "OK",
	"user" : {
		"name" : user.name,
		"id" : user.id,
		"browserId" : user.browserId,
		"store" : user.store
	}
});