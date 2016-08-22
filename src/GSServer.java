import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class GSServer {
	ServerSocket server=null;
	final static int PORT=1415;
	final int maxNumClients=20;
	final int maxNumCards=13;
	HashMap<Integer,GoofspielGame>games=new HashMap<Integer,GoofspielGame>();
	ArrayList<ClientThread>clients=new ArrayList<ClientThread>();
	ArrayList<Integer>avaClientIndex=new ArrayList<Integer>();
	public int joinGame(ClientThread client,int id){
		if(id<0||id>=games.size())return -1;
		GoofspielGame game=games.get(id);
		if(!game.isAvaliable())return -1;
		client.joinGame(game);
		return game.addPlayer(new GoofPlayer(maxNumCards));
	}
	public String getGameName(int id){
		return this.games.get(id).getName();
	}
	public int getGameId(String name) throws NumberFormatException, IOException{
		Iterator<Entry<Integer, GoofspielGame>> itr=games.entrySet().iterator();
		while(itr.hasNext()){
			Entry<Integer, GoofspielGame> ent=itr.next();
			if(ent.getValue().getName().equals(name)){
				return ent.getKey();
			}
		}
		return -1;
	}
	
	public int newGame(String gameName){
		Iterator<Entry<Integer, GoofspielGame>> itr=games.entrySet().iterator();
		while(itr.hasNext()){
			Entry<Integer, GoofspielGame> ent=itr.next();
			if(ent.getValue().getName().equals(gameName)){
				return -1;
			}
		}
		//
		Set<Integer>keys=games.keySet();
		int i=0;
		while(true){
			if(!keys.contains(i))break;
			i++;
		}
		games.put(i,new GoofspielGame(13,2,gameName));
		return i;
	}
	public ArrayList<Integer> getAvaGames() {
		ArrayList<Integer> avaGames=new ArrayList<Integer> ();

		Iterator<Entry<Integer, GoofspielGame>> itr=games.entrySet().iterator();
		while(itr.hasNext()){
			Entry<Integer, GoofspielGame> ent=itr.next();
			if(ent.getValue().isAvaliable()){
				avaGames.add(ent.getKey());
			}
		}
		return avaGames;
	}
	public void autoRemovePlayer(){
		for(int i=0;i<clients.size();i++){
			if(clients.get(i).isExit()){
				avaClientIndex.add(clients.get(i).getIndex());
				clients.remove(i);
			}
		}
		Iterator<Entry<Integer, GoofspielGame>> itr=games.entrySet().iterator();
		while(itr.hasNext()){
			Entry<Integer, GoofspielGame> ent=itr.next();
			if(ent.getValue().isEnd()){
				games.remove(ent.getKey());
			}
		}
		System.out.println(games.toString());
	}
	boolean isQuit=false;
	public GSServer(){
		for(int i=0;i<this.maxNumClients;i++){
			avaClientIndex.add(i);
		}
			try{
				server=new ServerSocket(PORT);
			}catch(Exception e) {
					System.out.println("can not listen to:"+e);
			}
			System.out.println("*********Server Starts!*******");
				while(!isQuit){
					Socket socket=null;
					try{
						while(clients.size()>=maxNumClients){Thread.sleep(100);autoRemovePlayer();}
						socket=server.accept();
						ClientThread client=new ClientThread(avaClientIndex.remove(0),socket,this);
						clients.add(client);autoRemovePlayer();
						client.start();
					}catch(Exception e) {
						System.out.println("Error."+e);
					}
				}
				System.out.println("*********Server is Exit!*******");
	}
	public static void main(String[] args) {
		new GSServer();

	}
}
