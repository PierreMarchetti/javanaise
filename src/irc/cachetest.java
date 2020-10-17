package irc;

import java.util.Scanner;

import jvn.JvnException;

public class cachetest {

	public static void main(String[] args) throws IllegalArgumentException, JvnException {
    	SentenceItf s1 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC1");
    	SentenceItf s2 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC2");
    	SentenceItf s3 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC3");

    	
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("lancez le cachetest2 avant de continuer");
    	scanner.nextLine();
    	
    	s2.read();
    	s3.write("1");

    	SentenceItf s4 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC4");
    	s4.write("1");
    	SentenceItf s5 = (SentenceItf) JvnProxy.newInstance(Sentence.class,"IRC5");


    	
	}

}
