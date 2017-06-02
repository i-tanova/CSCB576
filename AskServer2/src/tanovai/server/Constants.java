package tanovai.server;

public interface Constants {

	String EMAIL_HOST = "smtp.gmail.com";
	String EMAIL_ACCOUNT = "dfdsf@gmail.com";
	String EMAIL_PASSWORD = "pass";
	

	String ASK_REQUEST = "ASK_REQUEST";
	String ASK_REQUEST_END = "</ASK_REQUEST>";
	String VERSION = "VERSION";
	String TYPE = "TYPE";
	String THEME = "THEME";
	String QUESTION = "QUESTION";
	String SENDER_EMAIL = "SENDER_EMAIL";
	String PASSWORD = "PASSWORD";
	String EMAIL = "EMAIL";

	String user = "admin";
	String admin_password = "admin";

	int ASK_REQ = 1;
	int STOP_REQ = 2;
	int START_REQ = 3;
	int PAUSE_REQ = 4;
	int ITEMS_REQUEST = 5;
	int LOGIN_REQ = 6;

	String invalid_request = "Invalid request";
	String configDir = "Resources";
	String configFile = "config.txt";
	int rmiPort = 12347;
	int EMAILS_COUNT = 2;

	int SERV_REQ_OK = 101;
	int SERV_REQ_DENY = 201;
	int SERV_REQ_ERROR = 301;

	String LOG_FILE = "Log\\log.txt";

	int MSG_STAT_INFO = 111;
	int MSG_STAT_ERR = 222;
}
