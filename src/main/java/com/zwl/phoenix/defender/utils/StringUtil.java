package com.zwl.phoenix.defender.utils;

import java.util.List;

public class StringUtil {
	
	public static boolean isEmpty(String str) {
		if(str==null || "".equals(str)) {
			return true;
		}
		
		return false;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(List object) {
		return !isNotEmpty(object);
	}

	public static boolean isNotEmpty(List obj) {
		try {
			if (obj == null || obj.size() <= 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return true;
		}
	}
}
