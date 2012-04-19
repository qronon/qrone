$("#test1").html("<h1>Hello World!</h1>");


function addrow(key, value){
	if( key && value ){
		var row = $(".row").clone();
		$(".key", row).html(key);
		$(".value", row).html(value);
		$("#table").append(row);
	}
}

addrow( "request", request );
addrow( "response", response );

addrow( "document", document.toString() );
addrow( "document.cookie", document.cookie.toString() );

addrow( "location", location );
addrow( "location.href", location.href );
addrow( "location.protocol", location.protocol );
addrow( "location.hostname", location.hostname );
addrow( "location.port", location.port );
addrow( "location.pathname", location.pathname );
addrow( "location.search", location.search );
addrow( "location.hash", location.hash );

addrow( "navigator", navigator );
addrow( "navigator.userAgent", navigator.userAgent );

addrow( "query", query );

addrow( "user", user );
addrow( "user.id", user.id  );
addrow( "user.name", user.name  );
addrow( "user.deviceId", user.deviceId );
/*
*/