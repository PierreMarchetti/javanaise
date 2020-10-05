package jvn;

public enum JvnCoordLockState {
	NL("no lock"),

    R("read"),

    W("write");
    
    private String description ;  
    
    private JvnCoordLockState(String description) {  
        this.description = description;  
   }  
     
    public String getDescription() {  
        return  this.description ;  
   } 
}
