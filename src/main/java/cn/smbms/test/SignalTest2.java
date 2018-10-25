package cn.smbms.test;

/**
 * 饿汉模式
 * */
public class SignalTest2 {

	private static SignalTest2 signalTest = new SignalTest2();

	private SignalTest2() {
		System.out.println("私有的构造方法");
	}

	public static SignalTest2 getInstance() {
		return signalTest;
	}
}
