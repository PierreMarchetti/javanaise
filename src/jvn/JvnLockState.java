package jvn;

public enum JvnLockState {
	NL("no local lock"),

    RLC("read lock cached"),

    WLC("write lock cached"),

    RLT("read lock taken"),

    WLT("write lock taken"),         

    RLT_WLC("read lock taken – write lock cached");
    
    private String description ;  
    
    private JvnLockState(String description) {  
        this.description = description;  
   }  
     
    public String getDescription() {  
        return  this.description ;  
   } 
}
