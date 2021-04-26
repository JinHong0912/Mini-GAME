/*
 *  작 성 일  : 2020년 07월 23 일 
 *  작 성 자  : 류 진 홍
 *  작업 내용  : 자바 언어로 Snake Game 만들기
 *  참고 영상 : 유튜브 : 코딩 강사
 */
package Snake;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;


public class SnakeGame {

	static class MyFrame extends JFrame{
		
		static class XY{ // 전역변수 : X와 Y의 위치를 가지고 있는 커스텀 위치
		  int x;
		  int y;
		  
		  public XY(int x, int y) {
			  this.x = x;
			  this.y = y;
			  
		  }
			
		}
		// 전체적인 panel 선언하기
		static JPanel panelNorth;// 메시지 영역에 들어가는 panel
		static JPanel panelCenter;// 센터에 들어가는  panel : 게임 판 
		static JLabel labelTitle;// 전체 적인 타이틀 부분
		static JLabel labelMessage; // 게임 메시지 부분
		static JPanel[][] panels = new JPanel[20][20]; // 바둑판 모양 (UI)  같은 뱀이 지나다니는 부분 -> 2차 배열
		static int[][] map = new int[20][20]; // 실제로 가지고 있는 값 /fruit 9 위치 , Bomb 8 위치 , 0 Blank 아무것도 없는 0으로 초기화 
		static LinkedList<XY> snake = new LinkedList<XY>();// 뱀의 몸통 : 어레이 리스트 가능 타입이 XY 타입 = new XY 클래스 타입
		static int dir = 3; // move direction 0 : up , 1 : down , 2 :left , 3 : right
		static int score = 0; // 게임 점수
		static int time = 0; // game time : 게임 시간 -> unit 1 second 1초 단위
		static int timeTickCount = 0; //per 200ms : 뱀은 더 빨리 움직여야 해서
		static Timer timer = null;
		
	
		
		public MyFrame(String title) { // 맨 처음 윈도우 창에 보이는 화면 / 초기화 코드 부분 
			super( title ); //  JFrame에 타이틀을 넘겨주고 초기화 된다.
			this.setSize(400,500);// 넓이 높이를 의미 한다 
			this.setVisible(true);// Visible 을 해야지 윈도우 창이 보인다.
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 이 부분이 있어야 닫기가 된다.
			
			initUI(); // init UI 전체적인 초기화 해주는 부분 -> 1번
			makeSnakeList();// make Snake Body 만들기 -> 2번
			startTimer(); // start Timer : 시작 시간 설정 
			setKeyListener(); // Listen key event : 키보드 입력
			makeFruit(); // make fruit : 과일을 먹고 꼬리가 자라는 부분
			
			
		}
		//makeFruit : 열매 만들기	
		public void makeFruit() {
			Random rand = new Random();
			//0 ~ 19 X : 0 ~19 Y : X Y 측에 랜덤하게 과일을 위치 시킨다.
			int randX = rand.nextInt(19);
			int randY = rand.nextInt(19);
			
			map[randX][randY] = 9; // 9 is fruit : 맵에다가 데이터 셋팅 완료
				
		}
		
		
		
		
		//setKeyListener : 키보드 입력으로 방향 바꾸기
		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {// move direction 0 : up , 1 : down , 2 :left , 3 : right
				@Override
				public void keyPressed(KeyEvent e) {
					
					if(e.getKeyCode() == KeyEvent.VK_UP) {
					   if(dir != 1) {
						   dir = 0; 					   
					   }
					}else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
						if(dir != 0) {
							dir = 1;							
						}
					}else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
						if(dir != 3) {
							dir = 2;
							
						}
					}else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if(dir != 2) {
							dir = 3;							
						}
					}
					
					// 게임 재시작 버튼
					if(e.getKeyCode() == KeyEvent.VK_SPACE) {
						
					}
				}
			});
		}
		
		
		//makeSnakeList :  뱀 만들기
		
		public void makeSnakeList() {
			snake.add(new XY(10,10)); // Head of Snake
			snake.add(new XY(9,10));  // Body of Snake : 뱀의 몸은 머리의 바로 뒤에 있으므로 가로가 9 가 된다.
			snake.add(new XY(8,10));  // Tail of Snake : 뱁의 꼬리 부분은 맨 마지막에 위치 { ex : [T][B][H] }
			
		}
		
		// Timer 생성하기 UI
		public void startTimer() {
			timer = new Timer(100, new ActionListener() { //ActionListener : 인터페이스 함수 익명 객체로 선언한다.
			  										   // 50ms per 1 second
				@Override
				public void actionPerformed(ActionEvent e) {
					timeTickCount += 1;
			 
				 	if(timeTickCount % 10 == 0) { // 20개가 되면 1초가 올라간다.
				 		time++; // 1 sec add
					  
				 	}
				 	moveSnake();// 뱀 움직임
				 	updateUI(); // 전체 데이터의 UI를 갱신한다. :  표현 한다.
				 	
				
				}
			});
			timer.start(); //Start Timer!!;
		}
		
			// 뱀 움직이는 방 : 좌우 앞으로 움직이는 뱀 , 뒤로는 가지 못한다 : 키의 움직에 따라 움직이는 뱀을 설정한다.
			public void moveSnake() {
				XY headXY = snake.get(0); // get head
				int headX = headXY.x;
				int headY = headXY.y;
				
				
				
				// 진행 방향 움직임 조정
				// 다음으로 이동할 때 폭탄 인가 아니면 벽인가 ?
				if(dir == 0) { //move direction 0 : up , 1 : down , 2 :left , 3 : right
					// 충돌 
					boolean isColl = checkCollision(headX , headY-1);
					if( isColl == true) {//Game Over
						labelMessage.setText("Game Over!!");
						timer.stop();
						return;	
					}
					
					
					snake.add(0, new XY(headX , headY -  1)); // 처음의 머리 위치 : 진행 방향
					snake.remove(snake.size() - 1);// 꼬리 하나 자르기  : remove tail
					
					
				}else if(dir == 1) {//move direction 0 : up , 1 : down , 2 :left , 3 : right
					// 충돌 
					boolean isColl = checkCollision(headX , headY + 1);
					if( isColl == true) {//Game Over
						labelMessage.setText("Game Over!!");
						timer.stop();
						return;	
					}
					snake.add(0, new XY(headX , headY +  1)); // 처음의 머리 위치 : 진행 방향
					snake.remove(snake.size() - 1);// 꼬리 하나 자르기 : remove tall
					
				}else if(dir == 2) {//move direction 0 : up , 1 : down , 2 :left , 3 : right
					// 충돌 
					boolean isColl = checkCollision(headX - 1 , headY);
					if( isColl == true) {//Game Over
						labelMessage.setText("Game Over!!");
						timer.stop();
						return;	
					}
					snake.add(0, new XY(headX - 1 , headY)); // 처음의 머리 위치 : 진행 방향
					snake.remove(snake.size() - 1);// 꼬리 하나 자르기 : remove tall
					
				}else if(dir == 3) {//move direction 0 : up  , 1 : down , 2 :left , 3 : right
					// 충돌 
					boolean isColl = checkCollision(headX + 1 , headY);
					if( isColl == true) {//Game Over
						labelMessage.setText("Game Over!!");
						timer.stop();
						return;	
					}
					snake.add(0, new XY(headX + 1 , headY)); // 처음의 머리 위치 : 진행 방향
					snake.remove(snake.size() - 1);// 꼬리 하나 자르기 : remove tail
				}
				
			}
			// checkCollision
			//  반환 값은 boolean 이고 가지고 오는 값은 int headX, int headY 
			public boolean checkCollision(int headX , int headY) {
				if( headX < 0 || headX > 19 || headY < 0 || headY > 19) { // 벽에 충돌 했을때
				
					return true;
				}
				
				// Collision to Snake Body : 자신 몸에 부딪히면 게임오버!!
				for(XY xy : snake) {
					if( headX == xy.x && headY  == xy.y ) {
						return true;
						 
					}
					
				}
				
				// 과일을 먹으면 과일이 사라짐  : 배열에서는 XY 값이 다르다 반대
				 if(map[headY][headX] == 9) {// Colision on Fruit : 열매에 충돌 한 경우
					 map[headY][headX] = 0;
					 addTail();
					 
					 makeFruit();// 과일 다시 만듬
					 score += 100; // 과일을 먹으면 과일 점수
					 
				 }
			
				
				
				
				return false;	
			}
			// 뱀의 꼬리가 추가됨  : 진행 방향에 따라 꼬리 추가 해주기
			public void addTail() {
				int tailX = snake.get(snake.size() - 1).x;
				int tailY = snake.get(snake.size() - 1).y;
				int tailX2 = snake.get(snake.size() - 2).x;
				int tailY2 = snake.get(snake.size() - 2).y;
				 
				
				// 판별 
				if(tailX < tailX2) { // to Right : attach to Left
 					snake.add( new XY(tailX - 1 , tailY));
					
				}else if(tailX > tailX2) { // to Left : attach to Right
					snake.add( new XY(tailX + 1 , tailY));
					
				}else if(tailY < tailY2) { // to UP : attach to Down
					snake.add( new XY(tailX , tailY - 1));
					
				}else if(tailY > tailY2) { // to Down : attach to UP
					snake.add( new XY(tailX , tailY + 1));
					
				}
			}
			
			// 모든 데이터의 움직임 업데이트 하는 부분 
			public void updateUI() {
				labelTitle.setText("Score :  " + score  + " || " + "Time :  " + time + " Sec");//
				
				// 뱀의 위치 표현  : clear tile (panel) : 전체 UI 를 지운다
				// 2차 배열 2중 for문 연습하기
				for(int i = 0; i < 20 ; i++) {
					for(int j = 0; j < 20 ; j++) {
						 if( map[i][j] == 0) { //blank : 열매, 폭탄 없음
							 panels[i][j].setBackground(Color.GRAY);			 
						 
						 }else if(map[i][j] == 9) { // Fruit
							 panels[i][j].setBackground(Color.RED);
						 }
					}
				}
			
				// draw Snake : 향상된 for 문 사용
				int index = 0;
				for( XY  xy : snake) { // 뱀의 길이 만큼 순환 
							
					//  뱀 UI
					if(index == 0) { //head : 스크린에서는 모서리 부터 (0.0) 좌표 시작이라서 바꾸어 줘야 한다.
						panels[xy.y][xy.x].setBackground(Color.YELLOW);  // 뱀의 머리 부분 색
						
					}else { // body(s) : 하나 이거나 여러개
						panels[xy.y][xy.x].setBackground(Color.BLACK);  // 뱀의 몸 부분
					}
					
					index++;
					
				}
				
			
			}
			
			
		//initUI 가지고 오기
		
		public void initUI() {
			this.setLayout(new BorderLayout());
			
			panelNorth = new JPanel();
			panelNorth.setPreferredSize(new Dimension(400,100));//위쪽 메시지 영역
			panelNorth.setBackground(Color.BLACK);// 배경색 
			panelNorth.setLayout(new FlowLayout());
			
			
			// LabelTitle 타이틀 UI
			labelTitle = new JLabel("Score :  0 || Time :  0 Sec");
			labelTitle.setPreferredSize(new Dimension(400,50)); // 크기 , 높이 : 높이의 절반 정도 차지 : 사이즈 지정;
			//labelTitle.setFont(new Font("TimesRoman", Font.BOLD, 20)); //기본 폰트
			labelTitle.setFont(new Font("NanumBarunGothic", Font.BOLD, 20)); // 글자 폰트
			labelTitle.setForeground(Color.WHITE);//글자색 
			labelTitle.setHorizontalAlignment(JLabel.CENTER); // 글자 가운데 정렬
			panelNorth.add(labelTitle);
			
			// labelMessage UI
			labelMessage = new JLabel("Eat Fruit!!");
			labelMessage.setPreferredSize(new Dimension(400,20)); // 크기 , 높이 : 사이즈 지정;
			//labelMessage.setFont(new Font("TimesRoman", Font.BOLD, 20)); //기본 폰트
			labelMessage.setFont(new Font("NanumBarunGothic", Font.BOLD, 20)); // 글자 폰트
			labelMessage.setForeground(Color.YELLOW);//글자색 
			labelMessage.setHorizontalAlignment(JLabel.CENTER); // 글자 가운데 정렬
			panelNorth.add(labelMessage);
			
			// pack 을 해야 한다.
			this.add("North" , panelNorth); // 프레임에 panelNorth 를 추가 한다.
											// 부분 경계 	North 부분  타이틀 있는 부분
			
			// panelCenter  만들기
			 
			panelCenter = new JPanel();
			panelCenter.setLayout(new GridLayout(20,20));// 게임 판 레이 아웃 바둑판 레이 아웃 (가로,세로) 크기
			
			// panel에 대한 2중 for 문 만들기
			// 2 차 배열문
			 for(int i = 0 ; i < 20 ; i++) { // i loop : 열 ROW
				 for(int j = 0 ; j < 20 ; j++) { // j loop : 행	 Column
					 map[i][j] = 0; // init 0 : Blank : 아무 것도 없는 공간이다.
					 
					 // panelUI  만들기 2차 배열
					 panels[i][j] = new JPanel();
					 panels[i][j].setPreferredSize(new Dimension(20,20));// 화면의 크기
					 panels[i][j].setBackground(Color.BLACK);// 배경색
					 panelCenter.add(panels[i][j]);
					 
				 }
			 }
			this.add("Center", panelCenter); // 부분 경계 Center 부분
			this.pack();// Remove Empty Space : 빈 공간을 맞춰 주면서 UI 가  보인다.
			
			
		}
		
	}
	
	
	
	public static void main(String[] args) {
		//  처음 시작 부분 타이틀
		
		new MyFrame("Snake Game");
		
		
	}

}
