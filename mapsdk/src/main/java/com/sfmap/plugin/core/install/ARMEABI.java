package com.sfmap.plugin.core.install;

import android.os.Build;

/**
 *
 * @date: 2014/10/31
 */
/*package*/ enum ARMEABI {
	ARM("armeabi", "lib/armeabi"),
	ARM_V7A("armeabi-v7a", "lib/armeabi-v7a"),
	X86("x86", "lib/x86"),
	MIPS("mips", "lib/mips");
	private final String value;
	private final String soDir;

	ARMEABI(String value, String soDir) {
		this.value = value;
		this.soDir = soDir;
	}

	public static ARMEABI getSystemArch() {
		return fastValueOfABI(Build.CPU_ABI);
	}

	public static ARMEABI fastValueOfSoDir(String soDir) {
		// 字符串集较小,且固定, 使用length简单判断.
		int valueHash = soDir == null ? 0 : soDir.length();
		switch (valueHash) {
			case 11:
				return ARM;
			case 15:
				return ARM_V7A;
			case 7:
				return X86;
			case 8:
				return MIPS;
			default:
				return ARM;
		}
	}

	public static ARMEABI fastValueOfABI(String abi) {
		// 字符串集较小,且固定, 使用length简单判断.
		int valueHash = abi == null ? 0 : abi.length();
		switch (valueHash) {
			case 7:
				return ARM;
			case 11:
				return ARM_V7A;
			case 3:
				return X86;
			case 4:
				return MIPS;
			default:
				return ARM;
		}
	}

	public String getSoDir() {
		return soDir;
	}

	@Override
	public String toString() {
		return value;
	}
}
