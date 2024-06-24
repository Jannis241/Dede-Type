import javax.swing.*;
import java.awt.*;
import java.util.*;


class Word{
	String content;
	int lane;
	double xPos;
	double desiredX;
	Word(String c, int l){
		content = c;
		lane = l;
		xPos = 0;
		desiredX = 0;
		
	}
}

public class App {
    JFrame frame;
    JTextField textField;
    JLabel scoreText;
    
    Map<Word, JLabel> wordToLabel;
    Map<Integer, Integer> laneToCoordMap;
    Map<Double, Integer> coordToLaneMap;
    Map<Word, Integer> wordsOnLane;
    
    Font myFont = new Font("Arial",Font.BOLD,30);
    Font wordFont = new Font("Arial", Font.PLAIN, 20);

    static ArrayList<Word> wordsCurrentlyUsed;
    ArrayList<String> wordStringsCurrentlyUsed;
    ArrayList<String> wordList;
    int minAbstand;
    
    static int xCoordLabel;
    
    static int score;
    
    int maxWordsPerLane;
    App(){
    	minAbstand = 100;
        maxWordsPerLane = 3;
        score = 0;
        xCoordLabel = 0;
        wordsCurrentlyUsed = new ArrayList<>();
        wordStringsCurrentlyUsed = new ArrayList<>();

        
        wordList = new ArrayList<>();
        wordToLabel = new HashMap<>();
        wordsOnLane = new HashMap<>();
        
        laneToCoordMap = new HashMap<>();
        coordToLaneMap = new HashMap<>();

        laneToCoordMap.put(1, 90);
        laneToCoordMap.put(2, 160);
        laneToCoordMap.put(3, 230);
        laneToCoordMap.put(4, 300);
        laneToCoordMap.put(5, 370);
        
        
        coordToLaneMap.put(90.0, 1);
        coordToLaneMap.put(160.0, 2);
        coordToLaneMap.put(230.0, 3);
        coordToLaneMap.put(300.0, 4);
        coordToLaneMap.put(370.0, 5);

        frame = new JFrame("Fufu Type");
        frame.setSize(1000,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        textField = new JTextField();
        textField.setBounds(375, 450, 250,50);
        textField.setFont(myFont);
        textField.setEditable(true);
        

        scoreText = new JLabel();
        scoreText.setFont(myFont);
        scoreText.setHorizontalAlignment(SwingConstants.CENTER);
        scoreText.setVerticalAlignment(SwingConstants.CENTER);
        scoreText.setBounds(0, 20, 1000, 25);
        scoreText.setText("Score: " + score);
    


        frame.add(scoreText);
        frame.add(textField);
        frame.setLayout(null);
        frame.setVisible(true);
    }
    
    public void createWord(String text, int lane){
        int yCoord = laneToCoordMap.get(lane);
        Word word = new Word(text, lane);

        JLabel testLabel = new JLabel();
        testLabel.setFont(wordFont);
        testLabel.setText(text);
        testLabel.setBounds(0, yCoord, 1000, 50);
        frame.add(testLabel);
        wordToLabel.put(word, testLabel);
        
        wordsCurrentlyUsed.add(word);
        wordStringsCurrentlyUsed.add(text);
    }


    public void generadeWords(){
        wordList.add("Adresse");
        wordList.add("Gewitter");
        wordList.add("Stift");
        wordList.add("Brot");
        wordList.add("Baum");
        wordList.add("Tisch");
        wordList.add("Schrank");
        wordList.add("Bildschirm");
        wordList.add("Natur");
        wordList.add("Schule");
        wordList.add("Tastatur");
        wordList.add("Bett");
        wordList.add("Glas");
        wordList.add("Wasser");
        wordList.add("Fahrrad");
        wordList.add("Haus");
        wordList.add("Hose");
    }
    public void addWords(int num){
    	
    	
    	
        ArrayList<Integer> lanesBeingUsed = new ArrayList<>();
        for (int i = 0; i < num; i++){
            boolean found = false;

            while(!found){
                Random rand1 = new Random();
                Collections.shuffle(wordList, rand1);
                String choosenWord = wordList.get(0);
                Random rand2 = new Random();
                int lane = (int)Math.ceil(5 * rand2.nextDouble());
                
            	if (wordStringsCurrentlyUsed.size() == 0) {
                    System.out.println("<creation> "+choosenWord + " got created in Lane " + lane);
                    found = true;
                    createWord(choosenWord, lane);
            	}
                
                // check ob das word schon genutzt wird und ob noch platz in der Lane ist
            	else if (!wordStringsCurrentlyUsed.contains(choosenWord) && wordsOnLane.get(lane) <= maxWordsPerLane){
                	
                	// gucken ob der mindestabstand ist
                	
                	// ->> bei start muss noch gefixt werden weil da ja der mindestabstand niht ist + index error falls liste leer ist (es noch keine wörter gibt)

                     	for (Word word : wordsCurrentlyUsed) {
                    		if (word.lane == lane) { // falls das wort auf der selben lane ist
                    			if (word.xPos >= minAbstand) {
                                    System.out.println("<creation> "+choosenWord + " got created in Lane " + lane);
                                    found = true;
                                    createWord(choosenWord, lane);
                    			}
                    		}
                    	}

     
                	

                   
                    
                }
    
                
            }
        }
    }


    public void removeWord(Word word){
        wordsCurrentlyUsed.remove(word);
        wordStringsCurrentlyUsed.remove(word.content);
        JLabel labelToRemove = wordToLabel.get(word);
        if (labelToRemove != null) {
            frame.remove(labelToRemove);
            frame.revalidate(); 
            frame.repaint(); 
        }

        wordToLabel.remove(word);

    }

    public void update(double dt, double speed){

        scoreText.setText("Score: " + score);
        double distanceDiff = speed * dt; // die distanz die verändert werden muss
        
        
        for (Word word : wordsCurrentlyUsed){
            double desiredX = word.desiredX + distanceDiff; // was das finale x sein müsste ohne runden
            
            word.desiredX = desiredX;
            
            
            int finalX = (int)Math.round(desiredX); // das finale x wird mit runden ausgerechnet
            if (finalX >= 1000){
                frame.dispose();
            }
            wordToLabel.get(word).setLocation(finalX, wordToLabel.get(word).getY());
        }
        
        
        
        // updaten wie viele wörter auf einer lane sind        
        wordsOnLane.clear();
        
        for (Word word : wordsCurrentlyUsed) {
        	int lane = (int) coordToLaneMap.get(word.xPos);
        	wordsOnLane.put(lane, wordsOnLane.get(lane) + 1);
        	
        }
        
    }


    public static void main(String[] args) throws Exception {

        
    
        System.out.println("github test");
        System.out.println("github test");
        System.out.println("github test");
        System.out.println("github test");
        System.out.println("github test"); 
        System.out.println("--------- <Fufu Type> ---------");
        System.out.println("<Start> Starting window..");

        long lastTime = System.nanoTime();
        double deltaTime;

        App fufuType = new App();
        System.out.println("<Init> Initializing app..");
        fufuType.generadeWords();
        fufuType.addWords(4);
        

        int counter = 0;

        while (true){
            String input = fufuType.textField.getText();
            if (input != null){
                for (Word word : wordsCurrentlyUsed) {
                    if (input.equals(word.content +" ")){
                        System.out.println("");
                        System.out.println("<remove> "+input + "got correctly typed.. -> removing it..");
                        String actualWord = input.substring(0, input.length() - 1); // das leerzeichen am ende entfernen damit man das richtige Wort löschen kann
                        fufuType.removeWord(word);
                        fufuType.textField.setText("");
                        fufuType.addWords(1);
                        score += 1;
                        break;
                    }
                }
            }

            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            
            lastTime = currentTime;

            fufuType.update(deltaTime, 75.0);
            counter += 1;
            
            if (counter >= 1e15 * deltaTime){
                System.out.println("added new word..");
                fufuType.addWords(1);
                counter = 0;
            }
             
            
        }
    }
}