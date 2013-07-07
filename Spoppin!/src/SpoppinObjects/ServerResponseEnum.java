package com.example.spoppin;

public enum ServerResponseEnum {
	Unknown(0),
	OK(200),
	InvalidAPIKey(201);
	
	private final int _value;

	ServerResponseEnum(int value) {
        _value = value;
    }

    public int Value() {
        return _value;
    }
    
    static ServerResponseEnum fromValue(int value) {  
        for (ServerResponseEnum my: ServerResponseEnum.values()) {  
            if (my.Value() == value) {  
                return my;  
            }  
        }    
        return null;  
    }  
		
}
