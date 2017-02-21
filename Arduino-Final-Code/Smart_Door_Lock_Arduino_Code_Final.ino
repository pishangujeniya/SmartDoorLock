String inputString = "";
String command = "";
String value = "";
String gotstatus = "";
String password = "pishang"; // this is the password for opening and closing your door
                            // you can set any pasword you like using digit and symbols
boolean stringComplete = false; 



int motorPin1 = 10; // pin 2 on L293D IC
int motorPin2 = 11; // pin 7 on L293D IC
int motorEnablePin = 9; // pin 1 on L293D IC
//int checkPin8 = 8;  // Checking for Satus
//int checkPin7 = 7; // Checking for Status
int Speed = 10;

void setup(){
  //start serial connection
  Serial.begin(9600);  // baud rate is 9600 must match with bluetooth 
  //The String reserve() function allows you to allocate a buffer in memory for manipulating strings.
  inputString.reserve(50);  // reserve 50 bytes in memory to save for string manipulation 
  command.reserve(50);
  value.reserve(50);
  
  boolean stringOK = false;
  
  pinMode(motorPin1, OUTPUT);
  pinMode(motorPin2, OUTPUT);
  pinMode(motorEnablePin, OUTPUT);
  pinMode(2, INPUT_PULLUP);
 // pinMode(checkPin8, INPUT);
//  digitalWrite(checkPin7, HIGH);
  
}

void loop(){
  // if arduino receive a string termination character like \n stringComplete will set to true
  if (stringComplete) {
    //Serial.println(inputString);
   // int sensorgggg = digitalRead(4);
    // Serial.println(sensorgggg);
    int sensorVal = digitalRead(2);
    delay(100);
    // identified the posiion of '=' in string and set its index to pos variable
    
   // checkval = digitalRead(checkPin8);
    //Serial.println(digitalRead(checkPin8));

    if(inputString == "GETSTATUS\n")
    {
      if(sensorVal == HIGH)
      {
        gotstatus = "UNLOCKED";
       Serial.println("UNLOCKED");
      }
      if(sensorVal == LOW)
      {
        gotstatus = "LOCKED";
      Serial.println("LOCKED");
      }
    }
    
    int pos = inputString.indexOf('=');
    // value of pos variable > or = 0 means '=' present in received string.
    if (pos > -1) {
      // substring(start, stop) function cut a specific portion of string from start to stop
      // here command will be the portion of received string till '='
      // let received string is open=test123
      // then command is 'open' 
        command = inputString.substring(0, pos);
      // value will be from after = to newline command
      // for the above example value is test123
      // we just ignoreing the '=' taking first parameter of substring as 'pos+1'
      // we are using '=' as a separator between command and vale
      // without '=' any other character can be used
      // we are using = menas our command or password must not contains any '=', otherwise it will cause error 
        value = inputString.substring(pos+1, inputString.length()-1);  // extract command up to \n exluded
     //   Serial.println(command);
      //  Serial.println(value);
       
       // password.compareTo(value) compare between password tring and value string, if match return 0 
    if(!password.compareTo(value) && (command == "OPEN") && digitalRead(2) == LOW){
          // if password matched and command is 'OPEN' than door should open
           openDoor(); // call openDoor() function
           
           Serial.println("OPENING"); // sent open feedback to phone
           delay(100);
           }
    else if(!password.compareTo(value) && (command == "CLOSE") && digitalRead(2) == HIGH){
          // if password matched and command is 'CLOSE' than door should close
           closeDoor();
           Serial.println("CLOSING"); // sent " CLOSE" string to the phone 
           delay(100);
           }
    else if(password.compareTo(value)){
          // if password not matched than sent wrong feedback to phone
           Serial.println("WRONG");
           delay(100);
           } 
        } 
       // clear the string for next iteration
       inputString = "";
       stringComplete = false;
    }
   
}


void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    char inChar = (char)Serial.read(); 
    //Serial.write(inChar);
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline or a carriage return, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n' || inChar == '\r') {
      stringComplete = true;
    } 
  }
}

void openDoor(){
  
   digitalWrite(motorPin1, HIGH);
   digitalWrite(motorPin2, LOW); 
   digitalWrite(motorEnablePin, HIGH);
   // use analogWrite() function if you want to control speed and use millis() function, not delay()
   //analogWrite(motorEnablePin, Speed);
   delay(375); 
   // now off motor
   digitalWrite(motorPin1, LOW);
   digitalWrite(motorPin2, LOW);
     
}

void closeDoor(){

   digitalWrite(motorPin1, LOW);
   digitalWrite(motorPin2, HIGH); 
   digitalWrite(motorEnablePin, HIGH);
   //analogWrite(motorEnablePin, Speed);
   delay(375); 
   digitalWrite(motorPin1, LOW);
   digitalWrite(motorPin2, LOW);
  }

