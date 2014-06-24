package net.wilmens.spoppin.objects;

public enum ServerResponseEnum {
	Unknown(0),
	NotConnected(1),
	OK(200),
	InvalidAPIKey(401),
	VoteIntervalError(420),
	NotNearVenue(421),
	RequestFailed(500);
	
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
