if(window.jQuery && !jQuery.fn.__CLASS__){
	jQuery.fn.__CLASS__ = function(){
		this.each(function(){
			$(this).qrone("__CLASS__");
		});
		return this;
	}
}