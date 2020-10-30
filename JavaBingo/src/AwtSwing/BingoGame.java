/*
 *  작 성 일  : 2020년 07월 23 일 
 *  작 성 자  : 류 진 홍
 *  작업 내용  : 자바 언어로 빙고 게임 만들기
 *  참고 영상 : 유튜브 : 코딩 강사
 */


//빙고 게임이다
package AwtSwing;
// 2번 :  스윙을 사용하기 때문에 모든 것을 import 한다.
// *  : 모든 것을 불러 온다. 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;
import java.util.stream.Stream;

import javax.management.openmbean.OpenDataException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.sound.sampled.*;


public class BingoGame {
	// 1번 : MyFrame class 호출 하기
	
	// 4 번 : 화면 UI 만들기
	// 패널 만들기
	static JPanel panelNorth; // Top View : 가장 기본적인 정보 표현
	static JPanel panelCenter; //Game View : 게임 화면이 실제로 표현
	static JLabel labelMessage; // text : 화면에 표시 되는 메시지 표현
	// 4 x 4 버튼 만들기
	static JButton[] buttons = new JButton[16]; // 게임의 4x4 바이트 짜리 버튼을 만든다.
	//이미지 표현 - 이미지를 가지고 있는 배열이 있어야 한다.
	static String[] images = { // 과일이 같은 것을 만드는 빙고 게임임으로 과일을 2개씩 만든다.
			"fruit01.png" ,"fruit02.png" ,"fruit03.png" ,"fruit04.png" , // all undercase
			"fruit05.png" ,"fruit06.png" ,"fruit07.png" ,"fruit08.png" ,
			"fruit01.png" ,"fruit02.png" ,"fruit03.png" ,"fruit04.png" ,
			"fruit05.png" ,"fruit06.png" ,"fruit07.png" ,"fruit08.png"
	};// 16개 짜리 이미지 배열을 가질 수 있게 만든다.
	
	// 게임 로직 
	static int openCount = 0; // Opened Card Count : 0, 1 , 2 -> 과일 카드를 누르는 부분
	static int buttonIndexSave1 = 0; // First Opened Card Index : 0~15 -> 처음 열리는 카드
	static int buttonIndexSave2 = 0; // Second Opened Card Index : 0~15 -> 두번 열리는 카드
	static Timer timer;
	static int tryCount = 0;// Try Count : 몇번 시도 했는지	
	static int successCount = 0; // success Count = Bing Count : 0~8 ->  성공한 수
	
	
	
	static  class MyFrame extends JFrame implements ActionListener{// 2번  : implements ActionListener 인터페이스 추가하기
		// 3번 : myFrame 생성자 만들기 
		public MyFrame(String title) {// 생성자 시작
			super(title);//jframe 상단에 타이틀 입력
			 this.setLayout(new BorderLayout());// awt의 기본 프레임이 BorderLayout 이다.
			 									// BorderLayout은 상하 좌우로 센터로 나누어진 레이 아웃이다.
			 this.setSize(400,500);//전체 크기 400px , 500px 상단의 100px 는 타이틀 이다. 400 X 400 은 게임 화면이다
			 this.setVisible(true);// true를 해야 실제로 보인다.
			 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 실제로 X 버튼을 누르면 실제로 닫힌다.
			   
			 
			  initUI( this );//Screen UI set. : 화면 초기화 하는 set 만들기
			 
			  // 카드를 섞는 작업 부분
			  mixCard(); // Mix Fruit Card
			  
			  
			  this.pack(); // Pack Empty Space. : 여백이나 가장자리를 비어 있는 공간을 사용 한다.
			  			   // 정리 된 UI 가 된다.
		}
		// success , fail Sound 넣기
		static void playSound(String filename) {
			File file = new File("./wav/" + filename);
			if(file.exists()) {
				try {
					AudioInputStream  stream = AudioSystem.getAudioInputStream(file);
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
					
				}catch(Exception e ){
					e.printStackTrace();
				}
			}else {
				System.out.println("File Not Found!!");// file 	확인 하기
			}
		}
	
		
		@Override// implements ActionListener 를 구현하는 페이지

		public void actionPerformed(ActionEvent e) { // 버튼 이벤트가 눌리게 되면  여기서 콜백이  호출 되게 된다.
			// System.out.println("Button Clicked!");// 이미지 클릭하면 텍스트 출력하는 곳 
			
			//버튼을 누를 때 액션을 하는 부분 : 버튼을 누를 때마다 openCount 가 하나씩 올라간다.
			if(openCount == 2) {// 카드가 2개 열리면 return 한다, 더 이상 안열리게 하기 위해서
				return;
			}
			
			
			
			//인덱스 가지고 오기 // 버튼 이벤트 구현하기
			JButton btn = (JButton)e.getSource();
			int index = getButtonIndex( btn );
			//System.out.println("Button Index : " + index);//클릭시 이미지 출력 되는 부분
			btn.setIcon(changeImage(images[index]));// 어레이에 저장 되어 있는 이미지를 가지고 온다. 위에 이미지 순서에 따라서 이미지를  가지고 온다.
	
			openCount++; // openCount 추가 하기
			if(openCount == 1) { // First Card?
				buttonIndexSave1 = index;// 첫번째 카드이면 저장 한다. 판정 로직 
			
			}else if (openCount == 2 ) {// Second Card?
				buttonIndexSave2 = index; // 첫번쨰 두번쨰 카드를 저장한다.
				tryCount++; // 시도 횟수를 하나 올려 준다.
				
				//labelMessage.setText("Find Same Card!!" + "Try" + tryCount);
				labelMessage.setText("Find Same Fruit! " + "  Try " + tryCount);
				// 두번쨰 카드를 누르면 tryCount 가 하나씩 추가 된다.
			
			
				// 판정 로직  : judge Logic
				boolean isBingo = checkCard(buttonIndexSave1,buttonIndexSave2);
				if(isBingo == true ) {
					 playSound("Bingo.wav"); // 성공 Sound
					
					 openCount = 0;
					 successCount++;
					 if(successCount == 8) { // 과일 찾기를 성공 했을 떄
						 labelMessage.setText(" Game Over " + " Try " + tryCount);
					 }
						 
				}else {// 과일 찾기를 실패 할때 카드를 뒤집는다.
					
					backToQuetion();
					 
				}
			}
		
		}
		
		// 실패시 카드를 뒤집는다.
		public void backToQuetion() {
			
			// 1초 딜레이를 줘야 한다. 1초 후에 실행 :Timer 1 Second
			timer = new Timer(1000, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Timer .");
					playSound("fail.wav");// 실패시 Sound
					
					openCount = 0;
					buttons[buttonIndexSave1].setIcon(changeImage("question.png"));
					buttons[buttonIndexSave2].setIcon(changeImage("question.png"));
					timer.stop();
				}
			});
			timer.start();
			
		}
		
		// 체크 메소드 ( 판정 로직 )
		public boolean checkCard(int index1, int index2) {
			if(index1 == index2) {
				return false;
			}
			
			if(images[index1].equals(images[index2])) {
				return true;
			
			}else {
				return false;
			}
		}
		
		public int getButtonIndex(JButton btn) {
			int index = 0;
			for(int i = 0 ; i < 16 ; i++) {
				if(buttons[i] == btn) { // Same Instance? : 실제로 인스턴스가 같은지 객체가 같은지 확인?
					index = i; // 같으면 
				}
			}
			return index; 
		}
	}
	static void mixCard() {
		// Random 함수 사용하기
		Random rand = new Random();// import 해주기
		// 섞는 기능 만들기
		for(int i = 0; i < 1000 ; i++) {
			int random = rand.nextInt(15) + 1;// 0 ~ 15 인데 + 1을 해서 1 ~ 15까지 카드 값을 가지고 온다.
			
			// swap 로직을 가지고 온다.
			String temp = images[0];
			images[0] = images[random];
			images[random] = temp;
			
			  	
			
		}
		
		
	}
	
	static void initUI( MyFrame myFrame ) {// 메소드 만들기 ;
		// 게임 처음 제목 부분 100px 부분 UI
		
		panelNorth = new JPanel();
		panelNorth.setPreferredSize(new Dimension(400,100));
		panelNorth.setBackground(Color.BLUE);
		labelMessage = new JLabel("Find Same Fruit! " + "  Try 0"); // 안내글 과 시도한 횟수 표시
		labelMessage.setPreferredSize(new Dimension(400, 100));
		labelMessage.setForeground(Color.WHITE);//글씨 색깔을 말한다.
		labelMessage.setFont(new Font("NanumBarunGothic", Font.BOLD, 20));
		labelMessage.setHorizontalAlignment(JLabel.CENTER);// 여기를 왼쪽 오른쪽 바꾸어 주면 글자 위치 변경
		panelNorth.add(labelMessage);// 만들어진 레이블을 넣어 준다.
		myFrame.add("North" , panelNorth); // North를 사용해야 한다.
		
		
		// 게임 화면 UI
		panelCenter = new JPanel();
		panelCenter.setLayout(new GridLayout(4,4));
		panelCenter.setPreferredSize(new Dimension(400,400));
		for(int i = 0; i < 16 ; i++) {// 0~15 까지 16 번 도는 버튼 만들기
			buttons[i] = new JButton();
			buttons[i].setPreferredSize(new Dimension(100,100));
			buttons[i].setIcon(	changeImage("question.png"));// 이미지를 넣는 부분
			// 누른 버튼에 있어서 과일카드를 표시 하는 부분을 만들어 주는 이벤트 로직 구현하기
			buttons[i].addActionListener(myFrame);//1번 :  이렇게 만들고 구현이 가능하도록 인터페이스 만들기 

			
			panelCenter.add(buttons[i]);// i 루프에 들어가 있는 버튼을 넣는 부분.
			
		}
		myFrame.add("Center", panelCenter);
		
	}
		// 게임의 이미지를 가지고 오는 부분 : 이미지 사이즈를 100으로 재조정 해서 이미지를 가지고 온다.
	static ImageIcon changeImage(String filename) {// 현재 이미지를 가지고 있는 폴더에서 이미지를 가지고 오는 부분
		ImageIcon icon = new ImageIcon("./img/" + filename);
		Image originImage = icon.getImage();
		Image changedImage = originImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);// 이미지의 사이즈가 128px 인데 우리는 100px으로 가지고 온다.
		ImageIcon icon_new = new ImageIcon(changedImage); // 실제로 아이콘을 가지고 온다.
		return icon_new;
		 
	}
	
	
	public static void main(String[] args) {
		// Bingo Game 만들기
		// 첫번째 아이콘 가지고 오기 (flaticon.com) 사이트 에서 가지고 오기
		
		new MyFrame("Bingo Game");
	}

}
