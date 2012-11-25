if(!window.qrone || !window.qrone.initialized){
	window.qrone = function(className, qid){
		
		if(qrone._map[qid]){
			return qrone._map[qid];
		}
			
		if(!qrone[className]){
			qrone[className] = function(id){
				return "<div style=\"margin:1px;padding:6px;border:solid 1px gray;background-color:#eee;\">" 
				+ className + " : class not found</div>";
			}
		}

		if(!qid){
			qid = "qid" + qrone._last;
			qrone._last++;
		}
		
		if(window.jQuery){
			var ins = jQuery( qrone[className](qid) );
			jQuery.each(qrone[className], function(arg, val) {
				ins[arg] = val;
			});
			ins.id = qid;
			qrone._map[qid] = ins;
			return ins;
		}else{
			if(!qrone._class[className]){
				qrone._class[className] = function(){}
				for(var arg in qrone[className]){
					qrone._class[className].prototype[arg] = qrone[className][arg];
					qrone._class[className].prototype.html = qrone[className];
				}
			}
			var cls = qrone._class[className];
			var ins = new cls();
			ins.id = qid;
			qrone._map[qid] = ins;
			return ins;
		}
	}
	window.qrone.initialized = true;
	window.qrone._last = 0;
	window.qrone._class = {};
	window.qrone._map = {};
}