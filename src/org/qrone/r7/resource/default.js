if(!window.$){
	window.$ = function(s){
		return document.select(s);
	}
}
if(!window.XMLHttpRequest){
	window.XMLHttpRequest = function(){
		
	}
	window.XMLHttpRequest.prototype = {
			abort : function(){
				
			},
			getAllResponseHeaders : function(){
				return this.responseHeader;
			},
			getResponseHeader : function(name){
				if(!this.responseHeader) return null;
				return this.responseHeader[name]
			},
			open : function(method, url, async, username, passwd){
				this._method = method;
				this._url = url;
			},
			send : function(content){
				var response = http.request(this._url, this._method, content, 
						this._requestHeader,
						true);
				this.responseHeader = response.headers;
				this.readyState = 4;
				this.responseBody = response.body;
				this.responseText = response.content;
				this.responseXML = new XML(this.responseText);
				this.status = response.responseCode;
				if(this.onreadystatechange) this.onreadystatechange();
			},
			setRequestHeader : function(name, value){
				if(!this._requestHeader) this._requestHeader = {};
				this._requestHeader[name] = value;
			}
	}
}
