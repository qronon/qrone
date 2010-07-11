if(!window.qrone){
	if(!window.require) window.require = function(){}
	window.qrone = function(className, qid){
		if(!qrone[className]){
			qrone[className] = function(){
				return "<div style=\"margin:1px;padding:6px;border:solid 1px gray;background-color:#eee;\">" 
				+ className + " : class not found</div>";
			}
		}
		if(!window.jQuery) return qrone[className];
		if(!qrone._map[qid]){
			if(!qid){
				qid = "qrone" + qrone._last;
				qrone._last++;
			}
			var e = null;
			if(qid == ""){
				e = jQuery("body");
			}
			
			qrone._map[qid] = jQuery(qrone[className](qid));
			jQuery.extend(qrone._map[qid],qrone[className],{
				qid: function(id){
					if(arguments.length < 1) return qid;
					return jQuery(document.getElementById(qid+id));
				}});
		}
		return qrone._map[qid];
	}
	window.qrone.qid = function(id){
		if(window.jQuery) return jQuery(document.getElementById(id));
		return document.getElementById(id);
	}
	if(window.jQuery){
		jQuery.extend(qrone,{
			_last:0,
			_map:{}
		});
		jQuery.fn.extend({
			qrone:function(className){
				this.each(function(a,b){
					$(this).html(qrone(className));
				})
				return this;
			}
		});
	}
}
