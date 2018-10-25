package cn.smbms.test;

/**
 * 懒汉模式
 * */
public class SignalTest {

	private static SignalTest signalTest;

	private SignalTest() {
		System.out.println("私有的构造方法");
	}

	public static SignalTest getInstance() {
		signalTest = new SignalTest();
		return signalTest;
	}
}
