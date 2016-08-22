import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {
	Socket socket;
	GSServer server;
	BufferedReader si;
	PrintWriter so;
	GoofspielGame game=null;
	int id=-1;
	int clientid=-1;
	int play=1;
public ClientThread(int i,Socket socket,GSServer s) throws IOException{
	this.socket=socket;
	si=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	so=new PrintWriter(socket.getOutputStream());
	server=s;
	clientid=i;
	//BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
	//System.out.println("Client:"+si.readLine());
}
int getPlay(){
	return play;
}
void waitForNextPlay() throws InterruptedException{
	while(!game.getReadyForNextPlay(play))Thread.sleep(10);
	send(game.getNextPlayAndRes().toString());
}
void showCard(int card) throws InterruptedException{
	play++;//into new play.
	game.showCard(id, card);
}


private void send(String str){
	so.println(str);
	so.flush();
}
boolean isExit=false;
public boolean isExit(){
	return isExit;
}
private void doExit(){
	//send("bye()");
	if(game!=null)this.game.setPlayerExit(id);
	isExit=true;
	this.server.autoRemovePlayer();
}
@Override
public void run() {
	// TODO Auto-generated method stub
	try {
		while(true){
			String str=si.readLine();
			if(str==null){
				System.out.println("client("+clientid+"): exiting...");
				doExit();
				return;
			}
			if(!(str.startsWith("getGameId")||str.startsWith("joinGame"))){
			System.out.println("client("+clientid+"): "+str);
			}
		if(str.equals("start()")){
			send(""+id);
			send("start()");
		}else if(str.equals("getNumPlayers()")){
			send(""+game.getNumPlayers());
		}else if(str.equals("bye()")){
			this.doExit();
		}else if(str.equals("getGames()")){
			send(server.getAvaGames().toString());
		}else if(str.equals("waitForNextPlay()")){
			waitForNextPlay();
		}else {
			int from=str.indexOf('(');
			int to=str.indexOf(')');
			int param=-1;
			String paramStr="";
			if((from+1)<to)paramStr=str.substring(from+1,to);
			if(from!=-1&&to!=-1&&(from+1)<to){
				try{
					param=Integer.parseInt(paramStr);
				}catch(NumberFormatException e){
					//e.printStackTrace();
				}
			}
			if(str.startsWith("showCard")){
				int card=param;
				showCard(card);
			}else if(str.startsWith("getNumRemainCards")){
				send(""+game.getNumRemainCards(id));
			}else if(str.startsWith("getRemainCards")){
				send(""+game.getRemainCards(id));
			}else if(str.startsWith("getPoints")){
				send(""+game.getPoints(param));
			}else if(str.startsWith("newGame")){
				int gameid=server.newGame(paramStr);
				if(gameid!=-1){
					id=server.joinGame(this, gameid);
				}
				send(""+gameid);
			}else if(str.startsWith("getGameId")){
				int gameid=server.getGameId(paramStr);
				send(""+gameid);
			}else if(str.startsWith("getGameName")){
				String gameName=server.getGameName(param);
				send(gameName);
			}else if(str.startsWith("joinGame")){
				id=server.joinGame(this, param);
				send(""+id);
			}
		}
	}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	super.run();
}
public void joinGame(GoofspielGame game) {
	// TODO Auto-generated method stub
	this.game=game;
}
public Integer getIndex() {
	// TODO Auto-generated method stub
	return this.clientid;
}

}
