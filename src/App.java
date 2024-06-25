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

@SuppressWarnings("unused")
public class App {
    JFrame frame;
    JTextField textField;
    JLabel scoreText;
    
    Map<Word, JLabel> wordToLabel;
    Map<Integer, Integer> laneToCoordMap;
    Map<Integer, Integer> wordsOnLane;
    
    Font myFont = new Font("Arial",Font.BOLD,30);
    Font wordFont = new Font("Arial", Font.PLAIN, 20);

    static ArrayList<Word> wordsCurrentlyUsed;
    ArrayList<String> wordStringsCurrentlyUsed;
    ArrayList<String> wordList;
    
    static int score;

    int maxWordsPerLane;
    int minAbstand;

    App(){
    	minAbstand = 200;
        maxWordsPerLane = 3;

        score = 0;
        
        wordsCurrentlyUsed = new ArrayList<>();
        wordStringsCurrentlyUsed = new ArrayList<>();

        wordList = new ArrayList<>();
        wordToLabel = new HashMap<>();
        wordsOnLane = new HashMap<>();
        laneToCoordMap = new HashMap<>();

        laneToCoordMap.put(1, 90);
        laneToCoordMap.put(2, 160);
        laneToCoordMap.put(3, 230);
        laneToCoordMap.put(4, 300);
        laneToCoordMap.put(5, 370);
        
        wordsOnLane.put(1, 0);
        wordsOnLane.put(2, 0);
        wordsOnLane.put(3, 0);
        wordsOnLane.put(4, 0);
        wordsOnLane.put(5, 0);

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
        wordsOnLane.put(lane, wordsOnLane.get(lane) + 1);
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

    public void startWords(){
        ArrayList<Integer> usedLanes = new ArrayList<>();
        while (true){
            Random rand1 = new Random();
            Collections.shuffle(wordList, rand1);
            String choosenWord = wordList.get(0);
            Random rand2 = new Random();
            int lane = (int)Math.ceil(5 * rand2.nextDouble());
            if (!usedLanes.contains(lane)){
                usedLanes.add(lane);
                createWord(choosenWord, lane);
                System.out.println("<start word creation> "+choosenWord + " got created in Lane " + lane);
                break;
            }
        }
    }

    public void addWords(int num){
        System.out.println("<Creator> creating " +num + " word(s)..");

        for (int numIndex = 0; numIndex < num; numIndex++){
            // 1. valides Wort finden
            ArrayList<String> validWords = new ArrayList<>();
            for (String word : wordList){
                if (!wordStringsCurrentlyUsed.contains(word)){
                    validWords.add(word);
                }
            }

            // 2. valide Lane finden
            ArrayList<Integer> validLanes = new ArrayList<>();
            for (int lane = 1; lane <= 5; lane++){
                boolean validAbstand = true;
                if (wordsOnLane.get(lane) <= maxWordsPerLane){
                    for (Word word : wordsCurrentlyUsed){
                        if (word.lane == lane){
                            if (word.xPos < minAbstand){
                                validAbstand = false;
                            }
                        }
                    }
                }
                if (validAbstand){
                    validLanes.add(lane);
                }
            }

            System.out.println("<Creator> Found " + validLanes.size() + " valid lanes.");
            System.out.println("<Creator> Found " + validWords.size() + " valid words.");

            if (validWords.size() != 0 && validLanes.size() != 0){
                Random rand1 = new Random();
                Collections.shuffle(validWords, rand1);
                String choosenWord = validWords.get(0);
                
                Random rand2 = new Random();
                Collections.shuffle(validLanes, rand2);
                Integer choosenLane = validLanes.get(0);
    
                createWord(choosenWord, choosenLane);
            }
            else{
                System.out.println("<ERROR> No valid Word / Lane combination found!!!");
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
        wordsOnLane.put(word.lane, wordsOnLane.get(word.lane) + -1);

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
            word.xPos = finalX;
        } 
    }

    public void markCurrentWord(String input){
        try{
            for (Word word : wordsCurrentlyUsed){
                int alrdyTypeLetter = input.length();
                String matchingWordLetter = word.content.substring(0,alrdyTypeLetter);
                if (input.equals(matchingWordLetter)){
                    JLabel label = wordToLabel.get(word);
                }
            }
        }
        catch (Exception e){
            System.err.println("<ERROR> Cannot find matching letters..");
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("test of python update funktioniert!!");
        System.out.println("--------- <DedeType> ---------");
        System.out.println("<Start> Starting window..");

        long lastTime = System.nanoTime();
        double deltaTime;

        App dedeType = new App();
        System.out.println("<Init> Initializing app..");
        dedeType.generadeWords();
        dedeType.startWords();

        double speed = 50.0;
        
        double counter = 0;
        double difficulty = 7;
 
        while (true){
            String input = dedeType.textField.getText();

            if (input != null){
                input = input.replaceAll(" ", "");
                dedeType.markCurrentWord(input);
                for (Word word : wordsCurrentlyUsed) {
                    if (input.equals(word.content)){
                        System.out.println("<Remove> removed " + input +"..");
                        dedeType.removeWord(word);
                        dedeType.textField.setText("");
                        score += 1;
                        break;
                    }
                }
            }

            long currentTime = System.nanoTime();
            deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            
            lastTime = currentTime;

            dedeType.update(deltaTime, speed);
            counter += difficulty * deltaTime;
            
            if (counter >= 10){
                speed += .75;
                difficulty += .05;
                dedeType.addWords(1);
                counter = 0;
                System.out.println("<Data> Speed: " + speed + " | Difficulty: " + difficulty);
            }
        }
    }
}