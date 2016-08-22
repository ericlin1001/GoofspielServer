import java.util.ArrayList;

class GoofPlayer{
	int curPlay;
	int numCards;
	ArrayList<Integer>cards;
	ArrayList<Integer>playCards;
	int point=0;
	int index=-1;
	public GoofPlayer(int pnumCards){
		curPlay=1;
		init(pnumCards);
	}
	void setIndex(int i){
		index=i;
	}
	public void init(int pnumCards){
		numCards=pnumCards;
		cards=new ArrayList<Integer>();
		for(int i=0;i<numCards;i++){
			cards.add(i+1);
		}
		playCards=new ArrayList<Integer>(); 
		points=new ArrayList<Integer>(); 
		point=0;
		curPlay=1;
	}
	public void end(){
		
	}
	boolean isReady=false;
	public void showCard(int c){
		cards.remove((Integer)c);
		playCards.add(c);
		isReady=true;
	}
	public void playOne(){
		curPlay++;
		isReady=false;
	}
	public boolean isReady(int p){
		return isReady&&curPlay==p;
	}
	public int getCard(int p){
		p-=1;
		if(0<=p&& p<playCards.size())return playCards.get(p);
		return -1;
	}
	ArrayList<Integer>getPlayCards(){
		return playCards;
	}
	public String getName(){
		return "GoofPlayer("+index+")";
	}
	//
	public int getNumRemainCards() {
		return cards.size();
	}
	public ArrayList<Integer> getRemainCards() {
		return cards;
	}
	//
	ArrayList<Integer>points;
	public void winPoint(int p){
		point+=p;
		points.add(p);
	}
	public int getPoint(){
		return point;
	}
	public ArrayList<Integer>getPoints(){
		return points;
	}
	public String toString(){
		return this.getName();
	}
};
class ChancePlayer extends GoofPlayer{
	public ChancePlayer(int i){super(i);}
	public boolean isReady(int p){
		return true;
	}	
	public void playOne(){
		//
		if(cards.size()>0){
			int ri=(int)(Math.random()*cards.size());
			int c=cards.get(ri);
			this.showCard(c);
			//
			curPlay++;
		}
	}
	public int getCard(int p){
		return playCards.get(playCards.size()-1);
	}
	public String getName(){
		return "ChancePlayer("+")";
	}

};
public class GoofspielGame {
	int play=0;
	boolean isEnd=false;
	int numCards;
	ArrayList<GoofPlayer>players=new ArrayList<GoofPlayer>();
	ArrayList<Boolean>isExits=new ArrayList<Boolean>();
	int NumPlayers=2;
	String name="";
	//
	public GoofspielGame(int pNumCards,int pNumPlayers,String name){
		this.numCards =pNumCards;
		this.name=name;
		this.NumPlayers=pNumPlayers;
		addPlayer(new ChancePlayer(numCards));
		this.getChancePlayer().setIndex(0);
		start();
	}
	public boolean getReadyForNextPlay(int p){
		return p==play&&((NumPlayers+1)==this.getNumPlayers());
	}
	public int addPlayer(GoofPlayer p){//return id.
		//from 1->>....
		players.add(p);
		isExits.add(false);
		p.setIndex(players.size()-1);
		return players.size()-1; 
	}
	public void setPlayerExit(int id){
		isExits.set(id, true);
	}
	public void start(){
		play=1;
		getChancePlayer().playOne();
	}
	public void showCard(int id, int card) {
		// TODO Auto-generated method stub
		players.get(id).showCard(card);
		tryForNextPlay();
		if(play>this.numCards)isEnd=true;
	}
	

	private GoofPlayer getChancePlayer(){
		return players.get(0);
	}
	private boolean tryForNextPlay(){
		for(int i=0;i<players.size();i++){
			if(!players.get(i).isReady(play))return false;
		}
		//all is ready for next play,dealing with current play.
		int maxi=0;
		int maxCard=0;
		boolean isEq=false;
		for(int i=1;i<players.size();i++){
			int iplayCard=players.get(i).getCard(play);
			if(iplayCard>maxCard){
				maxi=i;
				maxCard=iplayCard;
				isEq=false;
			}else if(iplayCard==maxCard){
				isEq=true;
			}
		}
		
		if(!isEq){
			//normal
			players.get(maxi).winPoint(this.getChancePlayer().getCard(play));
		}else{
			//some player bid the same card.
			this.getChancePlayer().winPoint(this.getChancePlayer().getCard(play));
		}
		//wait for next play
		for(int i=0;i<players.size();i++){
			players.get(i).playOne();
		}
		play++;
		return true;
	}
	
//***************************************
	public int getNumRemainCards(int id) {
		// TODO Auto-generated method stub
		return players.get(id).getNumRemainCards();
	}

	public int getNumPlayers() {
		// TODO Auto-generated method stub
		return players.size();
	}
	public String getName(){
		return this.name;
	}
	public ArrayList<Integer> getNextPlayAndRes() {
		// TODO Auto-generated method stub
		ArrayList<Integer>cs=new ArrayList<Integer>();
		for(int i=0;i<players.size();i++){
			cs.add(players.get(i).getCard(play-1));
		}
		return cs;
	}
	public ArrayList<Integer> getRemainCards(int id) {
		// TODO Auto-generated method stub
		return players.get(id).getRemainCards();
		
	}
	public ArrayList<Integer> getPoints(int id) {
		// TODO Auto-generated method stub
		return players.get(id).getPoints();
	}
	public boolean isAvaliable() {
		// TODO Auto-generated method stub
		return this.getNumPlayers()<(this.NumPlayers+1);
	}
	public String toString(){
		String str="GoofspielGame(";
		for(int i=0;i<players.size();i++){
			str+=players.get(i).toString()+",";
		}
		str+=")";
		return str;
	}
	public boolean isEnd(){
		for(int i=1;i<this.players.size();i++){
			if(!isExits.get(i))return false;
		}
		return isEnd;
	}

}
