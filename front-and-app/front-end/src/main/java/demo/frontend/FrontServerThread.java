package demo.frontend;


/**   
* @Title: FrontServerThread.java 
* @Package demo.frontend 
* @Description: TODO
* @author thb
* @date 2017年11月9日 上午10:49:54 
* @version V1.0   
*/
public class FrontServerThread extends Thread{
	
	private int port;
	

	/** 
	*
	* @Description: TODO
	* @param   
	* @return void   
	* @throws 
	*/
	public void init(int port) {
		this.port=port;
		Thread frontServerThread=new Thread(this,"FrontServerThread"+port);
	//	frontServerThread.setDaemon(true);
		frontServerThread.start();
	}
	
	@Override
	public void run() {
		FrontServer server = new FrontServer();
	    server.serve(port);
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
}
