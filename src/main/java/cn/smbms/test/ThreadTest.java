package cn.smbms.test;

public class ThreadTest implements Runnable {

	@Override
	public void run() {
		SignalTest3 test = SignalTest3.getInstance();
	}

	public static void main(String[] args) {
		ThreadTest threadTest = new ThreadTest();
		Thread thread = new Thread(threadTest);
		thread.start();
		Thread thread1 = new Thread(threadTest);
		thread1.start();
		Thread thread2 = new Thread(threadTest);
		thread2.start();
		Thread thread3 = new Thread(threadTest);
		thread3.start();
		Thread thread4 = new Thread(threadTest);
		thread4.start();
	}
}
