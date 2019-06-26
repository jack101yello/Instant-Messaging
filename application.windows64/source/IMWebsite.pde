boolean isClient;
boolean isServer;

import processing.net.*;

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

void setup() {
  size(1000, 500);
  isClient = false;
  isServer = false;
}

void serverSetup() { // This is the setup if the user chooses to be a server
  
  server = new Server(this, 4444);
  smessage = new sMessage();
  for(int i = 0; i < ssavedMessage.length; i++) {
    ssavedMessage[i] = " ";
  }
}

void serverDraw() { // This is the draw if the user chooses to be a server
  background(255);
  stroke(0);
  line(0, height*3/4, width, height*3/4);
  smessage.getMessage();
  smessage.show();
  smessage.type();
  smessage.oldMessages();
}

void clientSetup() {
  cloggedIn = false;
  cmessage = new cMessage();
  for(int i = 0; i < csavedMessage.length; i++) {
    csavedMessage[i] = " ";
  }
}

void csignIn() {
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

void clientDraw() {
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
  
  void show() {
    defaults();
    messStr = new String(mess);
    text(messStr, width/2, height*7/8);
  }
  
  void send() {
    server.write(ssavedMessage[0]);
  }
  
  void type() {
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
  
  void logMessages() {
    ssavedMessage[0] = messStr;
  }
  
  void getMessage() {
    Client sclient = server.available();
    if(sclient != null) {
      stempSave = sclient.readString();
      if(ssavedMessage[0] != stempSave) {
        ssavedMessage[0] = stempSave;
        send();
      }
    }
  }
  
  void oldMessages() {
    defaults();
    text(ssavedMessage[0], width/2, height*27/40);
  }
}

class cMessage {
  char[] mess = new char[0];
  String messStr;
  
  void show() {
    defaults();
    messStr = new String(mess);
    text(messStr, width/2, height*7/8);
  }
  
  void type() {
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
  
  void sendToServer() {
    cclient.write(messStr);
  }
  
  void setMessage() {
    if(cclient.available() != 0) {
      csavedMessage[0] = cclient.readString();
    }
  }
  
  void oldMessages() {
    defaults();
    text(csavedMessage[0], width/2, height*27/40);
  }
}

void defaults() {
  stroke(0);
  fill(0);
  textAlign(CENTER);
  textSize(30);
}

void draw() {
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