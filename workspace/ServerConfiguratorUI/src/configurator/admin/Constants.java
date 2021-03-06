package configurator.admin;

public interface Constants {
	String ASK_REQUEST = "ASK_REQUEST";
	String VERSION ="VERSION";
	String TYPE = "TYPE";
	String THEME ="THEME";
	String QUESTION = "QUESTION";
	String SENDER_EMAIL ="SENDER_EMAIL" ;
    String PASSWORD = "PASSWORD";
    String EMAIL = "EMAIL";
     
    String admin_user = "admin";
    String admin_password ="admin";
    
    String HOST = "localhost";
    
    int ASK_REQ = 1;
     int STOP_REQ =2;
     int START_REQ = 3;
    int PAUSE_REQ = 4;
    int ITEMS_REQUEST =5;
    
    String invalid_request = "Invalid request";
    int port = 12345;
    int EMAILS_COUNT = 2;
    
   int SERV_REQ_OK = 101;
   int SERV_REQ_DENY = 201;
   int SERV_REQ_ERROR = 301;
}
