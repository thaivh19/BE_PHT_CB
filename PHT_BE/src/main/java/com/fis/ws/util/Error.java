package com.fis.ws.util;

public enum Error {

	DISCREPANCIES_FOUND("10006", "Discrepancies found"),
	TAX_REC_NOT_MATCH("10008", "Tax record is incorrect (cannot be matched)"),
	TAX_REC_NOT_FOUND("10009", "Tax record not found"),
	NO_VALID_TAX_REC("10012", "All of tax records returned from Customs Department have outstanding amount equals to 0"), 
	CUSTOMS_RECEIPT_NUMBER_NOT_FOUND("10015", "Customs Receipt Number not found"),
	IN_CANCEL_AWAITING("10016", "Tax record is in cancel awaiting status"),
	ALREADY_CANCELLED("10017", "Tax record was cancelled"), 
	ALREADY_APPROVED("10018", "Tax record was confirmed"),
	RECON_REQUEST_NOT_FOUND("10019", "Recon request not found"),
	MESSAGE_NOT_SUPPORT("10020", "Message %s is not supported"),
	TAX_CANCELLATION_RECEIPT_NUMBER_NOT_FOUND("10021", "Tax cancellation receipt number %s not found"),
	TAX_MESSAGE_NOT_SENT("10022", "Tax message cannot be processed due to system overload"),
	DUPLICATE_SPI("10023","Transaction send spi duplicated"),
	CUSTOMER_NOT_FOUND("10090", "customer record not found"),
	TAX_NOT_FOUND("10000", "Without voucher success for reconciliation"),
	SYS_ERR("00004", "System error!"),
	GDT_RESEND_ERROR("00001", "error from GDT"),
	GDC_RESEND_ERROR("00002", "error from GDC"),
	GDT_RESEND_TIMEOUT("00006", "Timeout from Tax dept"),
	GDC_RESEND_TIMEOUT("00005", "Timeout from customs dept");		
	
    private final String code;
    private final String msg;

    Error(String code, String msg) {
    	this.code = code;
    	this.msg = msg;
    }

    /**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	public static Error fromValue(String v) {
        for (Error c: Error.values()) {
            if (c.code.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
