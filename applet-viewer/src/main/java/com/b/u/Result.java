package com.b.u;

public class Result {
	
	 Object  result;

	 public Object getResult() {
		return result;
	}
	 
	 public String getString(){
		 return (String) (result);
	 }
	 
	 public Boolean getBoolean(){
		 return (Boolean) (result);
	 }

	 public void setResult(Object result) {
		this.result = result;
	}
	
}
