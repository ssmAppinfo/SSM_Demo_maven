package cn.smbms.test;

/**
 * 饿汉模式
 * */
public class SignalTest3 {

	private static class Sigleton{
		private static SignalTest3 signalTest = new SignalTest3();
		private Sigleton(){
			
		}
		public static SignalTest3 getSigleton(){
			return signalTest;
		}
		
	}
	

	private SignalTest3() {
		System.out.println("私有的构造方法");
	}

	public static SignalTest3 getInstance() {
		SignalTest3 signalTest3 = Sigleton.getSigleton();
		return signalTest3;
	}
}
