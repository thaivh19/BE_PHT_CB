package com.pht.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ConvertNumberToString {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private static DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.FRENCH);
	private static DecimalFormat ff = new DecimalFormat("#.##########");
	public static final String KHONG = "không";
	public static final String MOT = "một";
	public static final String HAI = "hai";
	public static final String BA = "ba";
	public static final String BON = "bốn";
	public static final String NAM = "năm";
	public static final String SAU = "sáu";
	public static final String BAY = "bảy";
	public static final String TAM = "tám";
	public static final String CHIN = "chín";
	public static final String LAM = "lăm";
	public static final String LE = "lẻ";
	public static final String MUOI = "mươi";
	public static final String MUOIF = "mười";
	public static final String MOTS = "mốt";
	public static final String TRAM = "trăm";
	public static final String NGHIN = "nghìn";
	public static final String TRIEU = "triệu";
	public static final String TY = "tỷ";
	public static final String[] number = { KHONG, MOT, HAI, BA, BON, NAM, SAU, BAY, TAM, CHIN };
	private static String kieuDonvi[] = { "\u0111\u1ED3ng", "ngh\u00ECn", "tri\u1EC7u", "t\u1EF7" };

	private static String[] p = { "kh\u00F4ng", "m\u1ED9t", "hai", "ba", "b\u1ED1n", "n\u0103m",
			"s\u00E1u", "b\u1EA3y", "t\u00E1m", "ch\u00EDn", "m\u01B0\u1EDDi" };

	private static String[] p2 = { "linh", "m\u1ED1t", "ph\u1EA9y", "tr\u0103m", "m\u01B0\u01A1i" };

	private static String thongbao = "Chỉ đọc được số có 21 chữ số bên trái dấu phây!";

	public static void initStartArray() {
		String[] initKieuDonvi = { "\u0111\u1ED3ng", "ngh\u00ECn", "tri\u1EC7u", "t\u1EF7" };
		
		String[] initp = { "kh\u00F4ng", "m\u1ED9t", "hai", "ba", "b\u1ED1n", "n\u0103m",
				"s\u00E1u", "b\u1EA3y", "t\u00E1m", "ch\u00EDn", "m\u01B0\u1EDDi" };
		
		//mang chua cac thong so khac dung de doc day so
		String[] initp2 = { "linh", "m\u1ED1t", "ph\u1EA9y", "tr\u0103m", "m\u01B0\u01A1i" };

		kieuDonvi = initKieuDonvi;
		p = initp;
		p2 = initp2;
	}

	public static boolean CheckNumber(String str) {
		try {
			new Double(str);
			return true;
		} catch (Exception k) {
			return false;
		}
	}

	/*
	 * neu test==true thi 0 tra ve la "khong" va nguoc lai tra ve la "linh" neu
	 * testOne==true thi 1 tra ve la mot(co dau ') va nguoc lai la mot(co dau .)
	 */
	public static String passOneNumberToString(int str, boolean test,
			boolean testOne) {
		switch (str) {
		case 0:
			if (test) {
				return p[0];//"khong";
			} else {
				return p2[0];//"linh";
			}
		case 1:
			if (testOne) {
				return p2[1];//"Mot" dau ' ;
			} else {
				return p[1];//"mot" dau . ;
			}
		case 2:
			return p[2];//"hai";
		case 3:
			return p[3];//"ba";
		case 4:
			return p[4];//"bon";
		case 5:
			return p[5];//"nam";
		case 6:
			return p[6];//"sau";
		case 7:
			return p[7];//"bay";
		case 8:
			return p[8];//"tam";
		case 9:
			return p[9];//"chin";
		default:
			return "";
		}
	}

	//Doc so co 2 chu so
	public static String readTwoNumber(String str, String donviDoc) {
		String s = "";
		char ch0 = str.toCharArray()[0];
		char ch2 = str.toCharArray()[1];
		int in = Integer.parseInt(Character.toString(ch0));
		int in2 = Integer.parseInt(Character.toString(ch2));

		if (in == 1 && in2 == 0) {
			//s = "muoi ` ";
			s = p[10];
		} else if (in == 1 && in2 > 0) {
			//s = "muoi ` " + passOneNumberToString(in2, true, false);
			s = p[10] + " " + passOneNumberToString(in2, true, false);
		} else if (in > 1 && in2 > 0) {
			//s = passOneNumberToString(in, true, false)+ " muoi ";
			s = passOneNumberToString(in, true, false) + " " + p2[4] + " ";
			if (in2 == 1) {
				s = s + passOneNumberToString(in2, true, true);
			} else {
				s = s + passOneNumberToString(in2, true, false);
			}
		} else if (in > 0 && in2 == 0) {
			//s = passOneNumberToString(in, true,false)+" "+"muoi";
			s = passOneNumberToString(in, true, false) + " " + p2[4];
		}

		if (donviDoc.equals("")) {
			return s;
		} else {
			return s + " " + donviDoc;
		}
	}

	public static String readOneNumber(String str, String donviDoc) {
		String s = passOneNumberToString(Integer.parseInt(str), true, false);
		return s + " " + donviDoc;
	}

	public static String readThreeNumber(String str, String donviDoc) {
		if (str.compareTo("000") == 0) {
			if (donviDoc.compareTo("đồng") == 0) {
				return " " + donviDoc;
			} else
				return "";
		} else {
			String s = "";
			char ch0 = str.toCharArray()[0];
			char ch2 = str.toCharArray()[1];
			char ch3 = str.toCharArray()[2];

			int in = Integer.parseInt(Character.toString(ch0));
			int in2 = Integer.parseInt(Character.toString(ch2));
			int in3 = Integer.parseInt(Character.toString(ch3));
			//Doc chu so dau tien
			//String strBegin = passOneNumberToString(in,true,false) +" tram ";
			String strBegin = passOneNumberToString(in, true, false) + " "
					+ p2[3] + " ";
			if (in2 == 0 && in3 == 0) {
				s = strBegin.trim();
			} else if (in2 == 0) {
				s = strBegin + passOneNumberToString(in2, false, false);
				s = s + " " + passOneNumberToString(in3, true, false);
			} else {
				//Doc 2 so sau
				String sub2Number = readTwoNumber(str
						.substring(1, str.length()), "");
				s = strBegin + sub2Number.trim();
			}
			return s + " " + donviDoc;
		}
	}

	/*
	 * Ham chia day so str ra thanh cac phan tu trong mang, moi phan tu trong
	 * mang chua 3 chu so hoac 2 hoac 1. Tuy vao so chu so ma chung ta truyen
	 * cho str. Nhung lon nhat trong moi phan tu chi duoc 3 chu so. 3 phan tu
	 * cuoi cua chuoi so duoc dat vao phan tu dau tien cua mang va cu tiep tuc
	 * nhu the ...
	 */
	public static String[] passStrToElements(String str) {
		int dodaiStr = str.length();
		String result[] = null;
		if (dodaiStr % 3 == 0 && dodaiStr >= 3) {
			result = new String[dodaiStr / 3];
		} else {
			result = new String[dodaiStr / 3 + 1];
		}
		int i = 0;
		while (str.length() > 0) {
			if (str.length() < 3) {
				result[i] = str;
				break;
			}
			result[i] = str.substring(str.length() - 3, str.length());
			str = str.substring(0, str.length() - 3);
			i++;
		}
		return result;
	}

	/*
	 * Chuyen tu mot mang chua cac phan tu (moi phan tu co 3 chu so hoac la 2
	 * hoac 1) sang mot mang tuong ung la nhung chuoi doc cac so do theo don vi
	 * doc tuong ung nhu: dong, nghin, trieu, ty, nghin ty, trieu ty, ty ty.
	 */
	public static String[] passArrayNumberToArrayString(String arg[],
			String value[]) {
		String result[] = new String[arg.length];

		int j = 0; //Bien chay de lay gia tri cua kieuDonvi[]
		for (int i = 0; i < result.length; i++) {
			if (i == 4) { //chuyen sang don vi tinh la "ty" vi co 10 chu so
				// thay vi la "dong" cho 9 chu so
				j = 1;
			}
			if (arg[i].length() == 3) {
				result[i] = readThreeNumber(arg[i].toString().trim(), value[j]);
			} else if (arg[i].length() == 2) {
				result[i] = readTwoNumber(arg[i].toString().trim(), value[j]);
			} else if (arg[i].length() == 1) {
				result[i] = readOneNumber(arg[i].toString().trim(), value[j]);
			}
			j++;
		}
		return result;
	}

	public static String converNumToString(String str) {
		initStartArray();
		String s = "";
		String subRight = "";
		String subLeft = "";

		if (str == null || !CheckNumber(str.replaceAll(" ", ""))) {
			return s;
		}
		str = str.replaceAll(" ", "");
		if (str.indexOf("E") != -1) {
			str = NumberFormat.getInstance().format(new Float(str)).replaceAll(
					",", "");
		}

		if (str.indexOf(".") != -1) {
			subLeft = str.substring(0, str.indexOf("."));
			if (subLeft.length() > 21) {
				//return "Chi doc duoc so co 21 chu so ben trai dau phay! Xin
				// loi ban!:"+subLeft.length();
				return thongbao;
			}
			subRight = str.substring(str.indexOf(".") + 1, str.length());
			//s = convertLeftToString(subLeft, true).trim()+" phay
			// "+convertRightToString(subRight).trim()+" dong";
			s = convertLeftToString(subLeft, true).trim() + " " + p2[2] + " "
					+ convertRightToString(subRight).trim() + " "
					+ kieuDonvi[0];
		} else {
			if (str.length() > 21) {
				//return "Chi doc duoc so co 21 chu so ben trai dau phay! Xin
				// loi ban!:"+subLeft.length();
				return thongbao;
			}
			s = convertLeftToString(str, false);
		}
		if(s.trim().length()>1)
			s = s.trim().substring(0, 1).toUpperCase() + s.trim().substring(1);
		return s;
	}

	protected static String convertRightToString(String str) {
		String s = "";
		char ch[] = str.toCharArray();
		char c = ch[0];
		int in = Integer.parseInt(Character.toString(c));
		if (str.length() > 3) {
			str = str.substring(0, 2);
		}
		if (str.length() == 1) {
			s = passOneNumberToString(in, true, false);
		} else if (str.length() == 2) {
			s = readTwoNumber(str, "");
		}
		//Phuc hoi lai donvi[], neu nhu co dau phay thi sau ham
		// convertLeftToString() kieuDonvi[0] = "";
		//Do vay ta phai phuc hoi lai no sau ham convertRightToString()
		initStartArray();
		return s;
	}

	/*
	 * Chuyen day so nguyen ben trai sang chu, neu test == true thi bo chu don
	 * vi "Viet Nam dong" cuoi cung
	 */
	protected static String convertLeftToString(String str, boolean test) {
		String s = "";
		String arrayNumber[] = passStrToElements(str);
		if (test) {
			kieuDonvi[0] = "";
		}
		String arrayValue[] = passArrayNumberToArrayString(arrayNumber,
				kieuDonvi);
		for (int j = arrayValue.length - 1; j >= 0; j--) {
			s = s.trim() + " " + arrayValue[j].trim();
		}
		return s;
	}
	public static String customCurrencyToVietNamese(String so) {
		ArrayList<String> kq = readNum(so);
		String result = "";
		for (int i = 0; i < kq.size(); i++) {
			result += kq.get(i) + " ";
		}
		return Character.toUpperCase(result.charAt(0)) + result.substring(1, result.length()) + "đồng.";
	}

	private static ArrayList<String> readNum(String a) {
		ArrayList<String> kq = new ArrayList<String>();

		ArrayList<String> List_Num = Split(a, 3);

		while (List_Num.size() != 0) {
			switch (List_Num.size() % 3) {
			case 1:
				kq.addAll(read_3num(List_Num.get(0)));
				break;
			case 2:
				ArrayList<String> nghin = read_3num(List_Num.get(0));
				if (!nghin.isEmpty()) {
					kq.addAll(nghin);
					kq.add(NGHIN);
				}
				break;
			case 0:
				ArrayList<String> trieu = read_3num(List_Num.get(0));
				if (!trieu.isEmpty()) {
					kq.addAll(trieu);
					kq.add(TRIEU);
				}
				break;
			}

			if (List_Num.size() == (List_Num.size() / 3) * 3 + 1 && List_Num.size() != 1)
				kq.add(TY);

			List_Num.remove(0);
		}

		return kq;
	}

	private static ArrayList<String> read_3num(String a) {
		ArrayList<String> kq = new ArrayList<String>();
		int num = -1;
		try {
			num = Integer.parseInt(a);
		} catch (Exception ex) {
		}
		if (num == 0)
			return kq;

		int hang_tram = -1;
		try {
			hang_tram = Integer.parseInt(a.substring(0, 1));
		} catch (Exception ex) {
		}
		int hang_chuc = -1;
		try {
			hang_chuc = Integer.parseInt(a.substring(1, 2));
		} catch (Exception ex) {
		}
		int hang_dv = -1;
		try {
			hang_dv = Integer.parseInt(a.substring(2, 3));
		} catch (Exception ex) {
		}

		if (hang_tram != -1) {
			kq.add(number[hang_tram]);
			kq.add(TRAM);
		}

		switch (hang_chuc) {
		case -1:
			break;
		case 1:
			kq.add(MUOIF);
			break;
		case 0:
			if (hang_dv != 0)
				kq.add(LE);
			break;
		default:
			kq.add(number[hang_chuc]);
			kq.add(MUOI);
			break;
		}

		switch (hang_dv) {
		case -1:
			break;
		case 1:
			if ((hang_chuc != 0) && (hang_chuc != 1) && (hang_chuc != -1))
				kq.add(MOTS);
			else
				kq.add(number[hang_dv]);
			break;
		case 5:
			if ((hang_chuc != 0) && (hang_chuc != -1))
				kq.add(LAM);
			else
				kq.add(number[hang_dv]);
			break;
		case 0:
			if (kq.isEmpty())
				kq.add(number[hang_dv]);
			break;
		default:
			kq.add(number[hang_dv]);
			break;
		}
		return kq;
	}
	private static ArrayList<String> Split(String str, int chunkSize) {
		int du = str.length() % chunkSize;
		if (du != 0)
			for (int i = 0; i < (chunkSize - du); i++)
				str = "#" + str;
		return splitStringEvery(str, chunkSize);
	}

	private static ArrayList<String> splitStringEvery(String s, int interval) {
		ArrayList<String> arrList = new ArrayList<String>();
		int arrayLength = (int) Math.ceil(((s.length() / (double) interval)));
		String[] result = new String[arrayLength];
		int j = 0;
		int lastIndex = result.length - 1;
		for (int i = 0; i < lastIndex; i++) {
			result[i] = s.substring(j, j + interval);
			j += interval;
		}
		result[lastIndex] = s.substring(j);

		arrList.addAll(Arrays.asList(result));
		return arrList;
	}
}