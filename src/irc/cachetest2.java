package irc;

import jvn.JvnException;

public class cachetest2 {

	public static void main(String[] args) throws IllegalArgumentException, JvnException {
//    	SentenceItf s3 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC3");
    	SentenceItf s4 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC4");
//    	SentenceItf s5 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC5");
    	
    	
    	SentenceItf s1 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC1");
    	SentenceItf s2 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC2");
    	
    	s1.write("1");    	
    	s2.write("1");
    	

	}

}
