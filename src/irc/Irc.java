/***
 * Irc class : simple implementation of a chat using JAVANAISE 
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.*;
import java.awt.event.*;


import jvn.*;

import java.io.*;


public class Irc {
    public TextArea text;
    public TextField data;
    Frame frame;
    SentenceItf sentence;


    /**
     * main method
     * create a JVN object nammed IRC for representing the Chat application
     * @throws Exception 
     **/
    public static void main(String argv[]) throws Exception {
        try {
        	
//            // initialize JVN
//            JvnServerImpl js = JvnServerImpl.jvnGetServer();
//
//            // look up the IRC object in the JVN server
//            // if not found, create it, and register it in the JVN server
//            JvnObject jo = js.jvnLookupObject("IRC");
//
//            if (jo == null) {
//                jo = js.jvnCreateObject((Serializable) new Sentence());
//                // after creation, I have a write lock on the object
//                jo.jvnUnLock();
//                js.jvnRegisterObject("IRC", jo);
//            }
//            // create the graphical part of the Chat application
//            new Irc(jo);

            if(argv.length!=1){
                System.out.println("Problème d'argument");
            }
        	
        	SentenceItf s = (SentenceItf) JvnProxy.newInstance(Sentence.class,argv[0]);
          new Irc(s);     	
//        	s.write("rertet");

        } catch (Exception e) {
//	        System.out.println("IRC problem : " + e.getMessage());
        	throw e;
        }
    }

    /**
     * IRC Constructor
     *
     * @param jo the JVN object representing the Chat
     **/
    public Irc(SentenceItf s) {
        sentence = s;
        frame = new Frame();
        frame.setLayout(new GridLayout(1, 1));
        text = new TextArea(10, 60);
        text.setEditable(false);
        text.setForeground(Color.red);
        frame.add(text);
        data = new TextField(40);
        frame.add(data);
        Button read_button = new Button("read");
        read_button.addActionListener(new readListener(this));
        frame.add(read_button);
        Button write_button = new Button("write");
        write_button.addActionListener(new writeListener(this));
        frame.add(write_button);
        frame.setSize(545, 201);
        text.setBackground(Color.black);
        frame.setVisible(true);
    }
}


/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class readListener implements ActionListener {
    Irc irc;

    public readListener(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        try {
//            // lock the object in read mode
//            irc.sentence.jvnLockRead();
//
//            // invoke the method
//            String s = ((Sentence) (irc.sentence.jvnGetSharedObject())).read();
//
//            // unlock the object
//            irc.sentence.jvnUnLock();
        	
        	String s = irc.sentence.read();

            // display the read value
            irc.data.setText(s);
            irc.text.append(s + "\n");
        } catch (Exception je) {
            System.out.println("IRC problem : " + je.getMessage());
        }
    }
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class writeListener implements ActionListener {
    Irc irc;

    public writeListener(Irc i) {
        irc = i;
    }

    /**
     * Management of user events
     **/
    public void actionPerformed(ActionEvent e) {
        try {
            // get the value to be written from the buffer
            String s = irc.data.getText();
//
//            // lock the object in write mode
//            irc.sentence.jvnLockWrite();
//
//            // invoke the method
//            ((Sentence) (irc.sentence.jvnGetSharedObject())).write(s);
//
//            // unlock the object
//            irc.sentence.jvnUnLock();
        	
        	irc.sentence.write(s);
        } catch (Exception je) {
            System.out.println("IRC problem  : " + je.getMessage());
        }
    }
}



