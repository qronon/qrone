<?php 

public class qrone{
	public var $_extends;
	public var $_requires;
	
	public var $_name;
	public var $_attributes;
	public function __construct($name, $attributes){
		$this->_name = $name;
		$this->_attributes = $attributes;
	}
	
	
	
	public function render(){
		echo $this->getHTMLFull();
	}
	
	
	public function set_html($append){
		
	}
	
	public function append($append){
		
	}
}

?>