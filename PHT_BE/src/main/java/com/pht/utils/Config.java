package com.pht.utils;

public class Config {
	public static final String FILE_NAME = "TCSProperty";	
    //Bank gateway config
    public static String BGW_HQ_TTDT;
    public static String BGW_HQ_DCDT;
    public static String BGW_HQ_TTDT247;
    public static String BGW_HQ_DCDT247;
    public static String BGW_TCT;
    public static String FLAG;
    public static String URL_CORE;
    public static String TIME_UPDATE_FT_CITAD;
    
    public static String FILE_PATH_SIGN_HSM_PFX;
    public static String FILE_PATH_SIGN_HSM_CER;
    public static String GIPAUTO_PRIVATE_KEY_FILE_PATH_UNIX;
    
    public static String PATH_XML;
    public static String PASSWORD_OF_PFX_FILE;
    public static String FILE_JKS;
    public static String PASSWORD_OF_JKS;
    public static String SENDER_CODE = "79321001"; 
    public static String SENDER_NAME = "Ngân hàng TMCP Phát triển TP.HCM"; 
    public static String SERIAL_CUSTOM_VERIFY;
    public static String FLAG_CITAD;
    public static String FOLDER_XML;
    public static String FOLDER_XML_UNIX;
    public static String MA_TK_TREO;
    public static String COT_CURRENT;
    public static String CURRENT_NOSTRO_ACC;
    public static String COT_CITAD;
    public static String DEFAULT_GIPAUTO_FEE;
    public static String EXCEPTION_CHARACTER;
    public static int MAX_TIME_PROCESSING;
    public static int ALLOW_AUTO_ACCOUNTING;
    public static String TIME_RECONCILE_CUSTOMS1 = "07:00";
    public static String TIME_RECONCILE_CUSTOMS2 = "19:00";
    public static String LDAP_USER_CONTAINER = "";
    public static String LDAP_URI = "";
    public static String LDAP_DOMAIN = "";    
    public static String MIN_TIME_CORE = "15:30";
    public static String ENCRYPTKEY = "";
    public static String ALLOW_SEL_BOOK_LIST = "";
    public static String OFSERROR_TIMEOUT = "OFSERROR_TIMEOUT";
    public static String URL_CORE_IIB= "";
    public static String TTSP_RECEIVE_BANK= "";
    
    public static String HOST_EMAIL_CENTER= "";
    public static String PORT_EMAIL_CENTER = "";
    public static String ACC_NAME_EMAIL_CENTER= "";
    public static String PASS_EMAIL_CENTER= "";
    public static String EMAIL_CENTER= "";
    public static String ALERT_LOG_EMAIL_TO ="";
    public static String ACC_EMAIL_TO_DOICHIEU="";
    public static String ACC_EMAIL_CC_DOICHIEU="";
    public static String USER_SECURITY_PRINCIPAL_LDAP="";
    public static String PASSWORD_SECURITY_PRINCIPAL_LDAP="";
    public static String GIP_FILE_PATH_PFX;
    public static String GIP_FILE_PASSWORD_PFX;
    public static String CTULO_MAX_FILE_UPLOAD="";
    public static String GIP_WSPort_address ;
    public static String GIP_PATH_XML ;
	public Config() {
		
	}
}
