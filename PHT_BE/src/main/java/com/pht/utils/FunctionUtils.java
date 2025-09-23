package com.pht.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FunctionUtils {

	public static String convertString(String stringInput) {
		String out = ReplaceChar.replaceUnicode(stringInput);
		out = ReplaceChar.ReplaceSpecialChar(out);
		return out;
	}

	public static String getStackTrace(Exception e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		e.printStackTrace(pWriter);
		return sWriter.toString();
	}
}
