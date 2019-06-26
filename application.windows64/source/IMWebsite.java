import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class IMWebsite extends PApplet {

boolean isClient;
boolean isServer;



// Server variables
sMessage smessage;
Server server;
String[] ssavedMessage = new String[5];
String stempSave;

// Client variables
Client cclient;
String cIP;
boolean cloggedIn;
cMessage cmessage;
String[] csavedMessage = new String[5];

public void setup() {
  
  isClient = false;
  isServer = false;
}

public void serverSetup() { // This is the setup if the user chooses to be a server
  
  server = new Server(this, 4444);
  smessage = new sMessage();
  for(int i = 0; i < ssavedMessage.length; i++) {
    ssavedMessage[i] = " ";
  }
}

public void serverDraw() { // This is the draw if the user chooses to be a server
  background(255);
  stroke(0);
  line(0, height*3/4, width, height*3/4);
  smessage.getMessage();
  smessage.show();
  smessage.type();
  smessage.oldMessages();
}

public void clientSetup() {
  cloggedIn = false;
  cmessage = new cMessage();
  for(int i = 0; i < csavedMessage.length; i++) {
    csavedMessage[i] = " ";
  }
}

public void csignIn() {
  text("What IP would you like to connect to?", width/2, height/2);
  cmessage.show();
  cmessage.type();
  if(key == ENTER || key == RETURN) {
    cIP = cmessage.messStr;
    cmessage.mess = subset(cmessage.mess, 0, 0);
    cclient = new Client(this, cIP, 4444);
    cloggedIn = true;
  }
  
}

public void clientDraw() {
  background(255);
  stroke(0);
  if(!cloggedIn) {
    csignIn();
  }
  else {
    line(0, height*3/4, width, height*3/4);
    cmessage.show();
    cmessage.type();
    cmessage.setMessage();
    cmessage.oldMessages();
  }
}

class sMessage {
  char[] mess = new char[0];
  String messStr;
  
  public void show() {
    defaults();
    messStr = new String(mess);
    text(messStr, width/2, height*7/8);
  }
  
  public void send() {
    server.write(ssavedMessage[0]);
  }
  
  public void type() {
    if(keyPressed) {
      mess = append(mess, key);
      keyPressed = false;
      
      if((key == DELETE || key == BACKSPACE) && mess.length - 1 > 0) {
        mess = subset(mess, 0, mess.length-2);
      }
      else if((key == ENTER || key == RETURN) && mess.length - 1 > 0) {
        logMessages();
        send();
        mess = subset(mess, 0, 0);
        key = '_';
      }
    }
  }
  
  public void logMessages() {
    ssavedMessage[0] = messStr;
  }
  
  public void getMessage() {
    Client sclient = server.available();
    if(sclient != null) {
      stempSave = sclient.readString();
      if(ssavedMessage[0] != stempSave) {
        ssavedMessage[0] = stempSave;
        send();
      }
    }
  }
  
  public void oldMessages() {
    defaults();
    text(ssavedMessage[0], width/2, height*27/40);
  }
}

class cMessage {
  char[] mess = new char[0];
  String messStr;
  
  public void show() {
    defaults();
    messStr = new String(mess);
    text(messStr, width/2, height*7/8);
  }
  
  public void type() {
    if(keyPressed) {
      mess = append(mess, key);
      keyPressed = false;
      
      if((key == DELETE || key == BACKSPACE) && mess.length - 1 > 0) {
        mess = subset(mess, 0, mess.length-2);
      }
      else if((key == ENTER || key == DELETE) && mess.length - 1 > 0) {
        if(cloggedIn) {
          sendToServer();
          mess = subset(mess, 0, 0);
          key = '_';
        }
      }
    }
  }
  
  public void sendToServer() {
    cclient.write(messStr);
  }
  
  public void setMessage() {
    if(cclient.available() != 0) {
      csavedMessage[0] = cclient.readString();
    }
  }
  
  public void oldMessages() {
    defaults();
    text(csavedMessage[0], width/2, height*27/40);
  }
}

public void defaults() {
  stroke(0);
  fill(0);
  textAlign(CENTER);
  textSize(30);
}

public void draw() {
  if(isServer) {
    serverDraw();
  }
  else if(isClient) {
    clientDraw();
  }
  else {
    background(255);
    defaults();
    text("Press 's' to run program as server, or 'c' to run program as client", width/2, height/2);
    if(keyPressed) { // Checks if c or s has been pressed
      if(key == 's') {
        isServer = true;
        serverSetup();
      }
      else if(key == 'c') {
        isClient = true;
        clientSetup();
      }
      keyPressed = false;
    }
  }
}
  public void settings() {  size(1000, 500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "IMWebsite" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
