#include <TimerThree.h>

#define Alarm_pin 40
#define Solar_pin A15
#define Battery_pin A14
#define Load_pin A10
#define SIM900 Serial2
#define Radar Serial3

// Function prototypes
void SIM900_Power();
void SIM900_GPRS_Request();
void SIM900_Feedback();
void SIM900_SendSMS();
void SIM900_Receive_SMS();
void Parameter_Measure();
void Speed_Read();

int tick = 0;
boolean GPRS_lock = false;
boolean Find_2 = false;
boolean Miss_GPRS = false;
String Alarm_Status = "good";
String Vehicle_Speed_Buf = "";
float V_solar = 0.0;
float V_battery = 0.0;
float I_Load = 0.0;
int previous_num = 0;
int current_num = 0;
int vehicle_cnt = 0;

// Software equivalent of pressing the GSM shield "power" button
void SIM900_Power()
{
    // Initialise the power of SIM900
    digitalWrite(9, HIGH);
    delay(1000);
    digitalWrite(9, LOW);
    delay(5000);
}

void SIM900_Feedback()
{
    while(SIM900.available() != 0)
    Serial.write(SIM900.read());
}

void SIM900_SendSMS()
{
    SIM900.println("AT + CMGS = \"+64211879860\"");//send sms message, be careful need to add a country code before the cellphone number
    delay(100);
    SIM900_Feedback();
    SIM900.println("Warning ! look at the app!");//the content of the message
    delay(100);
    SIM900_Feedback();
    SIM900.println((char)26);//the ASCII code of the ctrl+z is 26
    delay(100);
    SIM900_Feedback();
    SIM900.println();    
}

void SIM900_Receive_SMS()
{   
    char incoming_char;
    
    if(SIM900.available() > 0)
    {
        //A
        incoming_char=SIM900.read(); //Get the character from the cellular serial port.
        if(incoming_char == 65)
        {         
            Serial.println(incoming_char);       
        }
    }
}

void SIM900_Init()
{
    // Because we want to send the SMS in text mode
    SIM900.print("AT+CMGF=1\r"); 
    delay(100);
    SIM900_Feedback();

    // Blurt out contents of new SMS upon receipt to the GSM shield's serial out
    SIM900.print("AT+CNMI=2,2,0,0,0\r"); 
    delay(100);
    SIM900_Feedback();
  
    // Signal quality report
    SIM900.println("AT+CSQ");
    delay(1000);
    SIM900_Feedback();

    // Check if the MS is connected to the GPRS network
    SIM900.println("AT+CGATT?");
    delay(100);
    SIM900_Feedback();

    // Bearer settings for applications based on IP, SAPBR connection type is using GPRS
    SIM900.println("AT+SAPBR=3,1,\"CONTYPE\",\"GPRS\"");
    delay(1000);
    SIM900_Feedback();

    // Setting the APN
    SIM900.println("AT+SAPBR=3,1,\"APN\",\"vodafone\"");
    delay(4000);
    SIM900_Feedback();

    // Setting the SAPBR
    SIM900.println("AT+SAPBR=1,1");
    delay(5000);
    SIM900_Feedback();
            
    SIM900.println("");
    delay(100);
}
void SIM900_GPRS_Request()
{
    char c;
 
    // Check if the MS is connected to the GPRS network
    SIM900.println("AT+CGATT?");
    delay(100);
    SIM900_Feedback();
       
    // Get local IP adress
    SIM900.println("AT+CIFSR");
    delay(2000); 
    SIM900_Feedback();

    // Set Prompt of '>' when module sends data
    SIM900.println("AT+CIPSPRT=0");
    delay(3000);
    SIM900_Feedback();

    // Start up the connection
    SIM900.println("AT+CIPSTART=\"tcp\",\"arduinolink1.azurewebsites.net\",\"80\"");
    delay(2000);
    SIM900_Feedback();

    // Begin send data to remote server
    SIM900.println("AT+CIPSEND");
    delay(4000);
    SIM900_Feedback();

    // The parameters user want to send
    SIM900.print( "GET http://arduinolink1.azurewebsites.net");
    SIM900.print( "/api/?battery_voltage="+(String)V_battery+"&load_current="+(String)I_Load+"&security_switch="+Alarm_Status+"&solar_voltage="+(String)V_solar+"");
    SIM900.println(" HTTP/1.0");
    SIM900.println();
    delay(500);
    SIM900_Feedback();
    
    // Sending
    SIM900.println((char)26);
    delay(5000);

    while(SIM900.available() != 0)
    {
        c = SIM900.read();
        if(c == 50)
        {
            Find_2 = true;
        }
    }

    if(Find_2 == true)
    {
        Miss_GPRS = false;
    }
    else
    {
         Miss_GPRS = true;
    }
    Serial.print("Miss_GPRS is ");
    Serial.println(Miss_GPRS);
}

void Parameter_Measure()
{
    float sum = 0.0;
        
    // Safety switch detection
    if(digitalRead(Alarm_pin) == 0)
    {
        Alarm_Status = "bad";
    }
    else
    {
        Alarm_Status = "good";         
    }

    // Measure Solar panel voltage
    for(int i=0;i<10;i++)
    {
        V_solar = analogRead(Solar_pin);
        V_solar = (V_solar/1024.0)*5.0;
        V_solar = (V_solar/(1200.0/(1200.0+6800.0)));
        sum += V_solar;
    }
    V_solar = sum/10.0;
    sum = 0.0;
        
    // Measure Battery voltage
    for(int i=0;i<10;i++)
    {    
        V_battery = analogRead(Battery_pin);
        V_battery = (V_battery/1024.0)*5.0;
        V_battery = (V_battery/(1800.0/(1800.0+3900.0)));
        sum += V_battery;
    }
    V_battery = sum/10.0;
    sum = 0.0;

    // Measure Load current
    for(int i=0;i<10;i++)
    {   
        I_Load = analogRead(Load_pin);
        I_Load = (I_Load - 509) * 0.125;
        sum += I_Load;
    }
    I_Load = sum/10.0;
    sum = 0.0;
    return;
}

void Speed_Read()
{
    char c;
    int cnt = 0;
    
    // Vehicle speed measurement
    while(Radar.available() != 0)
    {
        c = (char)Radar.read();
        if((c >= 48) && ( c <= 57))
        {
            if(cnt == 0)
            {
                current_num = current_num + (((int)c - 48) * 100);      
            }
            else if(cnt == 1)
            {
                current_num = current_num + (((int)c - 48) * 10);                 
            }
            else if(cnt == 2)
            {              
                current_num = current_num + ((int)c - 48);                      
            }
            cnt++;
        }
        else if(c == 13)
        {
            if(abs(current_num - previous_num) > 10)
            {
                if(current_num != 0)
                {
                    vehicle_cnt ++;
                    Vehicle_Speed_Buf = Vehicle_Speed_Buf + (String)current_num + ",";
    
                    Serial.print("Vehicle count is ");
                    Serial.print(vehicle_cnt);
                    Serial.print("\n");      
                    
                    Serial.print("Vehicle speed is ");
                    Serial.print(Vehicle_Speed_Buf);
                    Serial.print("\n");                         
                }      
            }
            previous_num = current_num;
            current_num = 0;
            cnt = 0;
            Radar.flush();
            break;
        } 
    }    
}

// 1sec Interrupt handler 
void timerIsr()
{
    tick++;
    Parameter_Measure();
    // If reach 10 min
    if(tick >= 600)
    {
        Timer3.stop();
        GPRS_lock = true;
        tick = 0;
    }
}

void setup()
{
    // Initialisation 
    Serial.begin(19200);
    Radar.begin(19200);
    SIM900.begin(19200);
    pinMode(Alarm_pin, INPUT);
    Serial.println("Start SIM900 initialisation");
    SIM900_Power();
    delay(5000);
    SIM900_Init();
   
    Serial.println("Start Timer");
    Timer3.initialize(1000000);
    Timer3.attachInterrupt(timerIsr);
}

void loop()
{
    while(GPRS_lock == true || Miss_GPRS == true)
    {
        // Initisation of SIM900
        SIM900_GPRS_Request();
        if(Miss_GPRS == true)
        {
            continue;     
        }
        GPRS_lock = false;
        Miss_GPRS = false;
        Find_2 = false;
        Vehicle_Speed_Buf = "";
        vehicle_cnt = 0;
        Timer3.start();     
    }
    SIM900_Receive_SMS();
    Speed_Read();
}
