package irc;


public interface SentenceItf {
	
	@SentenceMethodLockType ( name = "write" )  
    public void write(String text);
	@SentenceMethodLockType ( name = "read" )  
    public String read();
}
