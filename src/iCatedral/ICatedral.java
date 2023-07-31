package iCatedral;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import panamahitek.Arduino.PanamaHitek_Arduino;

public class ICatedral {
    
    private static String[] originalNotes = new String[12];
    
    static {
        originalNotes[0] = "C";
        originalNotes[1] = "^C";
        originalNotes[2] = "D";
        originalNotes[3] = "^D";
        originalNotes[4] = "E";
        originalNotes[5] = "F";
        originalNotes[6] = "^F";
        originalNotes[7] = "G";
        originalNotes[8] = "^G";
        originalNotes[9] = "A";
        originalNotes[10] = "^A";
        originalNotes[11] = "B";
    }
    
    private static String[] bellActivation = new String[48];
    
    private static int[][] bellMatrix = {
    /* C  */   {99, 13, 14, 15, 16},
    /* C# */   {99, 17, 18, 19, 20},
    /* D  */   {99, 21, 22, 23, 24},
    /* D# */   {99, 25, 26, 27, 28},
    /* E  */   {99, 29, 30, 31, 32},
    /* F  */   {99, 33, 34, 35, 36},
    /* F# */   {99, 37, 38, 39, 40},
    /* G  */   {99, 41, 42, 43, 44},
    /* G# */   {99, 45, 46, 47, 5},
    /* A  */   {0, 1, 2, 3, 4},
    /* A# */   {99, 6, 7, 8, 99},
    /* B  */   {9, 10, 11, 12, 99},
     

};
    
    private String name;
    private String pathFileLocation;
    private String NtaPathFileLocation;
    private String AcfPathFileLocation;
    // characteristics order = T, C, L, Q, M, W
    private String[] characteristics = new String[6]; 
    public String[] armadura = new String[1];
    public String[] songNumber = new String[1];
    private double tempoUnity;
    private static String arduinoComPort = "COM4";
    private static int arduinoBaudRate = 9600;
    private static String message = "";
    
    public ICatedral()
    {   	
    	String folder = chooseFolder();
    	
    	String FileToConvert[] = chooseFile("abc");
    	
    	this.name = FileToConvert[1].substring(0, FileToConvert[1].length() - 4);
    
        this.pathFileLocation = (folder + "\\" + name + ".bkp");
        NtaPathFileLocation = (folder + "\\" + name + ".nta");
        AcfPathFileLocation = (folder + "\\" + name + ".acf");
        Arrays.fill(bellActivation, "0");
        
        File testFile = new File(this.AcfPathFileLocation);
        
        if (!testFile.exists()) {  
            try {
                FileWriter creator = new FileWriter(this.pathFileLocation);
                creator.close();
                message = "Se ha creado el  archivo " + name;
                System.out.println(message);
            } catch (IOException e) {
            	message = "Ha ocurrido un error al crear el archivo.";
            	System.out.println(message);
                e.printStackTrace();
            }
            
            copy(FileToConvert[0]);
            
            setCharacteristics();
            setArmadura();
            setSongNumber();
            
            tempoUnity = 60 / Double.parseDouble(characteristics[3]);
            
            createNta();
            
            fillNta();
            
            createACF();
            
            fillAcf();
            
        }
        
        else {
            
        	message = "Ya existe un archivo con ese nombre";
            System.out.println(message);}
        
    }
    
    public ICatedral(String nulo){
    	
    	nulo = null;
    	
    	String infoAcf[] = chooseFile("acf");
        
    	this.name = infoAcf[1].substring(0, infoAcf[1].length() - 4);
        AcfPathFileLocation = infoAcf[0];
        NtaPathFileLocation = infoAcf[0].substring(0, infoAcf[0].length() - 3) + "nta";
        pathFileLocation = NtaPathFileLocation;
        Arrays.fill(bellActivation, "0");
        
        File testFileNta = new File(this.NtaPathFileLocation);
        
        File testFileAcf = new File(this.AcfPathFileLocation);
        
        if (testFileNta.exists() && testFileAcf.exists()) {
        	message = "Archivo añadido";
            System.out.println(message); 
            
            setArmadura();
            
            }
        
        else {
            
        	message = "Erro al añadir archivo";
            System.out.println(message);}
        
    }
    
    private void createNta(){
        
        try {
                FileWriter creator = new FileWriter(NtaPathFileLocation);
                creator.close();
                message = "Se ha creado el  archivo " + name + ".nta";
                System.out.println(message);
            } catch (IOException e) {
            	message = "Ha ocurrido un error al crear el archivo .nta .";
                System.out.println(message);
                e.printStackTrace();
            }
        
    }
    
    private void createACF(){
        
        try {
                FileWriter creator = new FileWriter(AcfPathFileLocation);
                creator.close();
                message = "Se ha creado el  archivo " + name + ".acf";
                System.out.println(message);
            } catch (IOException e) {
            	message = "Ha ocurrido un error al crear el archivo .acf .";
                System.out.println(message);
                e.printStackTrace();
            }
        
    }
    
    private void fillNta(){

        int currentPosition = 1;
        double realTime = 0;
        String result[];
        boolean keep = true;
        int kcounter = 0;
        
        for(int songCounter = 1; songCounter <= getNumberOfSongs(); songCounter++){
            
            try {
            
            File readFile = new File(pathFileLocation);
            File writeFile = new File(NtaPathFileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
            
            writer.write("Cancion " + songCounter);
            writer.newLine();
            
            String line = reader.readLine();
            
            while (line != null) {
                
                if (line.contains("K")) {
                    
                    if(kcounter <= (armadura.length -1)){
                        kcounter++;
                        writer.write(line);
                        writer.newLine();}
                    
                    line = reader.readLine();
                    
                    while(line.charAt(0) == ' '){
                
                        while(keep){

                            char analizedChar = line.charAt(currentPosition);

                            if(Character.isLetter(analizedChar)) {

                                result = getNoteTimeAndPostion(line,currentPosition);
                                
                                realTime = setRealTime(result[1],tempoUnity);
                                
                                writer.write(result[0] + " " + "T" + realTime);
                                writer.newLine();

                            }

                            else{

                                switch(analizedChar) {
                                    
                                  case '[':
                                      
                                    result = doCorchete(line,currentPosition);
                                    currentPosition = Integer.parseInt(result[result.length - 1]);
                                    
                                    for (int i = 0; i < result.length - 2; i++) {
                                        writer.write(result[i] + " ");
                                    }

                                    realTime = setRealTime(result[result.length - 2],tempoUnity);
                                    
                                    writer.write("" + "T" +realTime);
                                    
                                    writer.newLine();
                                    
                                    break;
                                    
                                  case '(':
                                      
                                    result = serchTresillo(line,currentPosition);
                                    currentPosition = Integer.parseInt(result[result.length - 1]);
                                    for (int i = 0; i < result.length - 1; i++) {
                                        writer.write(result[i] + " ");
                                        realTime = (setRealTime(String.valueOf(result[1].charAt(result[1].length() - 1)),tempoUnity)) * (2) / (result.length-1);
                                        
                                        writer.write(""+"T" +realTime);
                                        
                                        writer.newLine();
                                    }

                                    break;
                                    
                                  case '\\':
                                      
                                    keep = false;
                                    
                                    break;
                                }                
                            }

                            currentPosition++;

                        }
                
                        line = reader.readLine();
                        keep = true;
                        currentPosition = 0;
                        
                        if(line == null) {break;}
                
                    }
                    
                }
                
                else{
                
                    line = reader.readLine();
                    
                }
            }
            
            reader.close();
            writer.close();
            
            } 
            
            catch (IOException e) {
            	message = "Erro al convertir el archivo a nta. ";
                System.out.println(message + e.getMessage());
            }
            
        }
        
    }
    
    private void fillAcf(){
        
        String fistLetter = "";
        String octava = "5";
        String binario;
        int kcounter = 0;
        int alteration = 0;
        char letter;
        int charPosition;
        boolean becuadro = false;
        double realTime = 0;
            
            try {
            
                File readFile = new File(NtaPathFileLocation);
                File writeFile = new File(AcfPathFileLocation);
                BufferedReader reader = new BufferedReader(new FileReader(readFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));

                String line = reader.readLine();

                while(line != null){

                    if (line.contains("Cancion")){

                        writer.write("11001000 ");
                        
                        writer.newLine();
                        
                        binario = toBinario("" + line.charAt(8));
                        
                        writer.write(binario + " ");
                        
                        writer.newLine();
                        
                        line = reader.readLine();
                        line = reader.readLine();
                    }
                    
                    if (line.contains("K")){
                        kcounter++;
                        line = reader.readLine();}

                    charPosition = 0;
                    
                    if(line == null){break;}

                    while(charPosition < line.length()) {
                        
                        letter = line.charAt(charPosition);
                        
                        if(Character.isLetter(letter) && letter != 'T' && letter != 'Z') {
                            
                            fistLetter = Character.toString(letter);
                            
                            if (charPosition > 0){

                                letter = line.charAt(charPosition - 1);
                                if(letter == '0') {
                                        becuadro = true;
                                }
                                if(letter == '1') {
                                        alteration = 1;
                                }
                                if(letter == '2') {
                                        alteration = -1;
                                }
 
                            }
                            
                            charPosition++;
                            
                            letter = line.charAt(charPosition);
                            octava = Character.toString(letter);
                            
                            if(becuadro){bellActivation[bellMatrixCheckBecuadro(fistLetter,octava)] = "1";}
                            
                            else{bellActivation[bellMatrixCheck(fistLetter,octava,alteration,kcounter)] = "1";}
                            
                            becuadro = false;
                            
                            alteration = 0;
                            
                        }
                        
                        if(letter == 'T'){
                            realTime = Double.parseDouble(line.substring(charPosition+1));
                            break;
                        }
                        
                        charPosition++;
                        
                    }
                    
                    writer.write(bellActivationFusion() + " ");
                    
                    writer.newLine();
                    
                    writer.write(timeToBinary(realTime).substring(0,8) + " ");
                    
                    writer.newLine();
                    
                    writer.write(timeToBinary(realTime).substring(8,16) + " ");
                    
                    writer.newLine();
                    
                    Arrays.fill(bellActivation, "0");

                    line = reader.readLine();
   
                }
                
                writer.write("11001000 ");
                writer.newLine();
                writer.write("11001000 ");
                
                reader.close();
                writer.close();
                    
            }
            
            catch (IOException e) {
            	message = "Erro al convertir el archivo a acf. ";
                System.out.println(message + e.getMessage());            
            }
        
    }

    private void copy(String readFilePath){
        
        try {
            
            File readFile = new File(readFilePath);
            File writeFile = new File(pathFileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
            
            String line = reader.readLine();
            
            while (line != null) {
                writer.write(line);
                writer.newLine();
                line = reader.readLine();
            } 
            
            reader.close();
            writer.close();
            
            message = "Archivo respaldado";
            System.out.println(message);
            
        } 
        catch (IOException e) {
        	message = "Error al respaldar el archivo." ;
            System.out.println(message + e.getMessage());
        }
        
    }

    public String getName(){return name;}
    
    public String getPath(){return pathFileLocation;}
    
    public String getNtaPathFileLocation(){return NtaPathFileLocation;}
    
    public String getcharacteristic(int characteristicPosition ){
        
        return characteristics[characteristicPosition];
    
    }
    
    public String getArmadura(int armaduraPosition ){
        
        return armadura[armaduraPosition];
    
    }
    
    public String[] getAllArmadura(){
        
        return armadura;
    
    }
    
    public int getNumberOfArmaduras(){
        
        return armadura.length;
    
    }
    
    public String getSongNumer(int songPosition ){
        
        return songNumber[songPosition];
    
    }
    
    public int getNumberOfSongs(){
        
        return songNumber.length;
    
    }
    
    public double getTempoUnity(){return tempoUnity; }
    
    private String getArduinoComPort() {return arduinoComPort;}
    
    private void setArduinoComPort(String newArduinoComPort) {arduinoComPort = newArduinoComPort;}
    
    private int getarduinoBaudRate() {return arduinoBaudRate;}
    
    private void setarduinoBaudRate(int newArduinoBaudRate) {arduinoBaudRate = newArduinoBaudRate;}
    
    private String serchParameter(String parameter){
        
        String result = "";
        
        try {
            File readFile = new File(pathFileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains(parameter)) {
                    int index = line.indexOf(parameter);
                    result = line.substring(index + parameter.length());
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
        	message = "Erro al buscar parametro. ";
            System.out.println(message + e.getMessage());
        }
        
        if(result.isEmpty()){result = "Sin dato";}
        
        return result;
    }
    
    private void setCharacteristics(){
        
        characteristics[0] = serchParameter("T:");
        characteristics[1] = serchParameter("C:");
        characteristics[2] = serchParameter("L:");
        characteristics[3] = serchParameter("Q:");
        characteristics[4] = serchParameter("M:");
        characteristics[5] = serchParameter("W:");
        
    }
    
    private void setArmadura(){
        
        int currentPosition = 0;

        armadura[currentPosition] = "";
        
        try {
            File readFile = new File(pathFileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("K:")) {
                    armadura = Arrays.copyOf(armadura, armadura.length + 1);
                    int index = line.indexOf("K:");
                    armadura[currentPosition] = line.substring(index + "K:".length());
                    currentPosition++;
                }
                
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
        	message = "Error al configurar armadura. ";
            System.out.println(message + e.getMessage());
        }
        
        armadura = Arrays.copyOf(armadura, armadura.length -1);
                
    }
    
    private void setSongNumber(){
        
        int currentPosition = 0;

        songNumber[currentPosition] = "";
        
        try {
            File readFile = new File(pathFileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("X:")) {
                    songNumber = Arrays.copyOf(songNumber, songNumber.length + 1);
                    int index = line.indexOf("X:");
                    songNumber[currentPosition] = line.substring(index + "X:".length());
                    currentPosition++;
                }
                
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
        	message = "Error al establecer el numero de la cancion. ";
            System.out.println(message + e.getMessage());
        }
        
        songNumber = Arrays.copyOf(songNumber, songNumber.length -1);
                
    }
    
    private int armaduraPatch(int actualArmadura,String note){

        switch (armadura[actualArmadura]) {
            
        case "C":
        break;
        
        case "G":
            
            if(note.equals("F"))
                {return 1;};
        
        break;
        
        case "D":
            
            if(note.equals("F") || 
               note.equals("C"))
               {return 1;}
        
        break;
        
        case "A":
            
            if(note.equals("F") || 
               note.equals("C") ||
               note.equals("G"))
               {return 1;}
        
        break;
        
        case "E":
        
            if(note.equals("F") || 
               note.equals("C") ||
               note.equals("G") ||
               note.equals("D"))
                {return 1;}
            
        break;
        
        case "B":
            
            if(note.equals("F") || 
               note.equals("C") ||
               note.equals("G") ||
               note.equals("D") ||
               note.equals("A"))
                {return 1;}
            
        break;
        
        case "F#":
            
            if(note.equals("F") || 
               note.equals("C") ||
               note.equals("G") ||
               note.equals("D") ||
               note.equals("A") ||
               note.equals("E"))
                {return 1;}
            
        break;
        
        case "C#":
            
            if(note.equals("F") || 
               note.equals("C") ||
               note.equals("G") ||
               note.equals("D") ||
               note.equals("A") ||
               note.equals("E") ||
               note.equals("B"))
                {return 1;}
        
        break;
        
        case "F":
            
            if(note.equals("B"))
                {return -1;}
        break;
        
        case "Bb":
            
            if(note.equals("E") ||
               note.equals("B"))
                {return -1;}
        
        break;
        
        case "Eb":
            
            if(note.equals("A") ||
               note.equals("E") ||
               note.equals("B"))
                {return -1;}
        
        break;
        
        case "Ab":
        
            if(note.equals("D") ||
               note.equals("A") ||
               note.equals("E") ||
               note.equals("B"))
                {return -1;}
        
        break;
        
        case "Db":
            
            if(note.equals("G") ||
               note.equals("D") ||
               note.equals("A") ||
               note.equals("E") ||
               note.equals("B"))
                {return -1;}
        
        break;
        
        case "Gb":
            if(note.equals("C") ||
               note.equals("G") ||
               note.equals("D") ||
               note.equals("A") ||
               note.equals("E") ||
               note.equals("B"))
                {return -1;}
        
        break;
        
        case "Cb":
            if(note.equals("F") || 
               note.equals("C") ||
               note.equals("G") ||
               note.equals("D") ||
               note.equals("A") ||
               note.equals("E") ||
               note.equals("B"))
                {return -1;}
        
        break;
        
        default:
            System.out.println("No se encuentro ninguna armadura");
        break;
        
        }
        
         return 0;  
    }
    
    public static String[] getNoteTime(String analizedAcorde){    
        
        String result[] = new String [3];
        String octava = "5";
        String alteration = "";
        String noteTime;
        char fistLetter = ' ';
        char letter;
        int commasAfterFirstLetter = 0;
        int charPosition;

        for(charPosition = 0; charPosition < analizedAcorde.length(); charPosition++) {
            letter = analizedAcorde.charAt(charPosition);
            if(Character.isLetter(letter)) {
                fistLetter = letter;
                break;
            }
        }
        
        if (charPosition > 0){
        
            letter = analizedAcorde.charAt(charPosition - 1);
            if(letter == '=') {
                    alteration = "0";
            }
            if(letter == '^') {
                    alteration = "1";
            }
            if(letter == '_') {
                    alteration = "2";
            }
        }
        
        for(int i = (charPosition + 1); i < charPosition + 4; i++) {
            letter = analizedAcorde.charAt(i);
            if(letter == ',') {
            commasAfterFirstLetter++;
            }
            else{break;}
        }
        
        letter = analizedAcorde.charAt(charPosition + commasAfterFirstLetter + 1);
        noteTime = Character.toString(letter);
        
        if(Character.isLetter(letter)) {
                noteTime = "1";
        }
        if(letter == '/') {
                noteTime = "1/2";
        }
        if(letter == '3') {
            letter = analizedAcorde.charAt(charPosition + commasAfterFirstLetter + 2);
            if(letter == '/') {
                noteTime = "3/2";
            }
            
        }        

        if(commasAfterFirstLetter < 1){

            if(Character.isUpperCase(fistLetter)) {
                //System.out.println("La primera letra es mayúscula.");
                octava = "3";
            } else if(Character.isLowerCase(fistLetter)) {
                //System.out.println("La primera letra es minúscula.");
                octava = "4";
            } else {
                //System.out.println("No se encontró una letra en el string.");
            }
        
        }
        
        else{
            
            if(commasAfterFirstLetter == 1){octava = "2";}
            if(commasAfterFirstLetter == 2){octava = "1";}
            if(commasAfterFirstLetter == 3){octava = "0";}
        }
        
        result[0] = (alteration + fistLetter + octava);
        
        result[0] = result[0].toUpperCase();
        
        result[1] = noteTime;
  
        return result ;
    }
    
    public static String[] getNoteTimeAndPostion(String analizedAcorde, int currentCharPosition){    
        
        String result[] = new String [3];
        String octava = "5";
        String alteration = "";
        String noteTime;
        char fistLetter = ' ';
        char letter;
        int commasAfterFirstLetter = 0;
        int charPosition;

        for(charPosition = currentCharPosition; charPosition < analizedAcorde.length(); charPosition++) {
            letter = analizedAcorde.charAt(charPosition);
            if(Character.isLetter(letter)) {
                fistLetter = letter;
                break;
            }
        }
        
        if (charPosition > 0){
        
            letter = analizedAcorde.charAt(charPosition - 1);
            if(letter == '=') {
                    alteration = "0";
            }
            if(letter == '^') {
                    alteration = "1";
            }
            if(letter == '_') {
                    alteration = "2";
            }
        }
        
        for(int i = (charPosition + 1); i < charPosition + 4; i++) {
            letter = analizedAcorde.charAt(i);
            if(letter == ',') {
            commasAfterFirstLetter++;
            }
            else{break;}
        }
        
        letter = analizedAcorde.charAt(charPosition + commasAfterFirstLetter + 1);
        noteTime = Character.toString(letter);
        
        switch (letter) {
            case '/':
                noteTime = "1/2";
                break;
            case '3':
                letter = analizedAcorde.charAt(charPosition + commasAfterFirstLetter + 2);
                if(letter == '/') {
                    noteTime = "3/2";
                }
                else noteTime = "3";
                break;
            default:
                if (Character.isDigit(letter) && letter!='3') {
                    noteTime = String.valueOf(letter);
                } else {
                    noteTime = "1";
                }
                
        }       

        if(commasAfterFirstLetter < 1){

            if(Character.isUpperCase(fistLetter)) {
                //System.out.println("La primera letra es mayúscula.");
                octava = "3";
            } else if(Character.isLowerCase(fistLetter)) {
                //System.out.println("La primera letra es minúscula.");
                octava = "4";
            } else {
                //System.out.println("No se encontró una letra en el string.");
            }
        
        }
        
        else{
            
            if(commasAfterFirstLetter == 1){octava = "2";}
            if(commasAfterFirstLetter == 2){octava = "1";}
            if(commasAfterFirstLetter == 3){octava = "0";}
        }
        
        result[0] = (alteration + fistLetter + octava);
        
        result[0] = result[0].toUpperCase();
        
        result[1] = noteTime;
        
        result[2] = Integer.toString(charPosition + commasAfterFirstLetter + 1);
        
        return result ;
    }

    public static String[] doCorchete(String analizedAcorde,int currentPosition){
        
        char letter = ' ';
        int noteCounter = 0;
        int currentCharPosition = currentPosition;
        String acordeTime = "";
        boolean keep = true;
        
        while(keep){
            
            letter = analizedAcorde.charAt(currentCharPosition);
            
            if(Character.isLetter(letter)) {
                noteCounter++;
            }
            
            if(letter == ']' || letter == '\\'){keep = false;}
            
            currentCharPosition++;
            
        }
        
        currentCharPosition = currentPosition;
        
        String acordeNotes[] = new String [noteCounter + 2];
        
        for(int i = 0; i < noteCounter; i++ ){
            
            String[] result = new String[3];
            
            result = getNoteTimeAndPostion(analizedAcorde,currentCharPosition);
            
            //acordeNotes[i] = result[0] + " " + result[1] + " " + result[2];
            
            acordeNotes[i] = result[0];
            
            currentCharPosition = Integer.parseInt(result[2]); 
            
            acordeTime = result[1];
        }
        
        acordeNotes [noteCounter] = acordeTime;
        
        acordeNotes [noteCounter + 1] = Integer.toString(currentCharPosition);
        
        return acordeNotes;
    }
    
    public static String[] serchTresillo(String analizedAcorde,int currentPosition){
        
        int charPosition;
        char letter;
        String[] result;
        String[] tresillo;
        int noteCounter;
        
        tresillo = new String[1];

        for(charPosition = currentPosition; charPosition < analizedAcorde.length(); charPosition++) {
            letter = analizedAcorde.charAt(charPosition);
            if(letter == '(') {
                
                noteCounter = Character.getNumericValue(analizedAcorde.charAt(charPosition + 1));
                
                tresillo = new String[noteCounter + 1];
                
                result = new String[3];
                
                charPosition = currentPosition + 1;
                
                    for(int i = 0; i < noteCounter; i++ ){

                    result = getNoteTimeAndPostion(analizedAcorde,charPosition);

                    tresillo[i] = result[0] + " " + result[1];

                    charPosition = Integer.parseInt(result[2]); 

                    }
                    
                tresillo [noteCounter] = Integer.toString(charPosition);
                    
                break;
            }
        }
        
        return tresillo;
    }
    
    public static double setRealTime(String musicalTime,double tempoUnity){
        
        double realTime;
        
        switch(musicalTime) {
            case "1/2":
              realTime = 0.5 * tempoUnity;
              break;
            case "3/2":
              realTime = 1.5 * tempoUnity;
              break;
            default:
              realTime = Double.parseDouble(musicalTime) * tempoUnity;
          }

        return realTime;
    }
    
    public static String toBinario(String numberString){
        
        int miByte = Integer.parseInt(numberString);
        String binarioTrue = Integer.toBinaryString(miByte & 0xFF);
        String resultado = String.format("%8s", binarioTrue).replace(' ', '0');
        
        return resultado;
    }
    
    public static int bellMatrixCheckBecuadro(String note,String octava){
        
        int posicionNote = 0;
        int resultado;
            
        for (int i = 0; i < 12; i++) {
                
            if(note.equals(originalNotes[i])){
                posicionNote = i;
                break;
            }
        }
        
        resultado = bellMatrix[posicionNote][Integer.parseInt(octava)];
           
        return resultado;
        
    }
    
    public int bellMatrixCheck(String note,String octava,int alteracion,int actualArmadura){
        
        int posicionNote = 0;
        int resultado;
        int octavaInt = Integer.parseInt(octava);
            
        for (int i = 0; i < 12; i++) {
                
            if(note.equals(originalNotes[i])){
                posicionNote = i;
                break;
            }
        }
        
        switch (alteracion) {
            
        case 0:
            posicionNote = posicionNote + armaduraPatch(actualArmadura,note);
        break;
            
        case 1:
            posicionNote++;
        break;
            
        case -1:
            posicionNote--;
        break;
        
        }
        
        if(posicionNote == -1){posicionNote = 11;octavaInt--;}
        if(posicionNote == 12){posicionNote = 0; octavaInt++;}
        
        resultado = bellMatrix[posicionNote][octavaInt];
           
        return resultado;
        
    }

    public static String bellActivationFusion(){
        
        StringBuilder binaryString = new StringBuilder();
        for (int i = 0; i < bellActivation.length; i++) {
            binaryString.append(bellActivation[i]);
        }

        String binaryNumber = binaryString.toString();
        return binaryNumber;

    }
    
    public static String timeToBinary(double time){
        
        int integerPart = (int) time;
        int decimalPart = (int) ((time - integerPart) * 100); // Multiplica por 100 para obtener 2 decimales

        String binaryInteger = String.format("%8s", Integer.toBinaryString(integerPart)).replace(' ', '0');
        String binaryDecimal = String.format("%8s", Integer.toBinaryString(decimalPart)).replace(' ', '0');

        return binaryInteger + binaryDecimal;
        
    }
    
    public void sendSong() {
    	
    	PanamaHitek_Arduino arduinocatedral = new PanamaHitek_Arduino();
    	
    	try {
			arduinocatedral.arduinoTX(arduinoComPort, arduinoBaudRate);
			
			File readFile = new File(AcfPathFileLocation);
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            
            String line = reader.readLine();
            
            while (line != null) {
                arduinocatedral.sendData(line);
                line = reader.readLine();
            } 
            
            reader.close();
            
            System.out.println("Envio de datos a arduino exitoso");		
			
		} catch (Exception e) {
			message = "Error de conexion con Arduino. ";
			System.out.println(message);
			e.printStackTrace();
		}
    
    }
    
    public static void sendCurrentListOfSongs(ArrayList<ICatedral> CurrentListOfSongs) {
    	
    	PanamaHitek_Arduino arduinocatedral = new PanamaHitek_Arduino();
    	
    	for (ICatedral song : CurrentListOfSongs) {
    	
	    	try {
				arduinocatedral.arduinoTX(arduinoComPort, arduinoBaudRate);
				
				File readFile = new File(song.AcfPathFileLocation);
	            BufferedReader reader = new BufferedReader(new FileReader(readFile));
	            
	            String line = reader.readLine();
	            
	            while (line != null) {
	                arduinocatedral.sendData(line);
	                line = reader.readLine();
	            } 
	            
	            reader.close();
	            
	            System.out.println("Envio de datos a arduino exitoso");		
				
			} catch (Exception e) {
				message = "Error de conexion con Arduino. ";
				System.out.println(message);
				e.printStackTrace();
			}
	    
    	}
    
    }
    
    public void sendPlayList(ICatedralPlayList playList) {
    	
    	PanamaHitek_Arduino arduinocatedral = new PanamaHitek_Arduino();
    	
    	try {
			arduinocatedral.arduinoTX(arduinoComPort, arduinoBaudRate);
			
			File readFile = new File(playList.getAcflPathFileLocation());
            BufferedReader reader = new BufferedReader(new FileReader(readFile));
            
            String line = reader.readLine();
            
            while (line != null) {
                arduinocatedral.sendData(line);
                line = reader.readLine();
            } 
            
            reader.close();
            
            System.out.println("Envio de datos a arduino exitoso");		
			
		} catch (Exception e) {
			message = "Error de conexion con Arduino. ";
			System.out.println(message);
			e.printStackTrace();
		}
    
    }

    
    @Override
    public String toString() {
    	return 	"Nombre del archivo: " + name + 
    			"\nTitulo: " + getcharacteristic(0) +
    			"\nCompositor: " + getcharacteristic(1) +
    			"\nLongitud de la nota : " + getcharacteristic(2) +
    			"\nCampo del tiempo : " + getcharacteristic(3) +
    			"\nTipo de compás: " + getcharacteristic(4) +
    			"\nSoftware utilizado: " + getcharacteristic(5);
    }
    
    private String[] chooseFile(String extencion) {
    	
    	String[] info = new String[2];
    	
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        fileChooser.setDialogTitle("Seleccionar archivo ." + extencion + " a agregar.");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos ."+ extencion, extencion);        
        fileChooser.setFileFilter(filter);
        
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {

            java.io.File selectedFile = fileChooser.getSelectedFile();
            info[0] = selectedFile.getAbsolutePath();
            info[1] = selectedFile.getName();
            
        } else {
        	message = "El usuario canceló la selección de la carpeta.";
            System.out.println(message);
        }
        
        return info;
    }

    private String chooseFolder() {
    	
    	String rutaCarpeta = "";
    	
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        fileChooser.setDialogTitle("Seleccionar carpeta en donde crear el archivo");
        
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {

            java.io.File selectedFile = fileChooser.getSelectedFile();
            rutaCarpeta = selectedFile.getAbsolutePath();
            
        } else {
        	message = "El usuario canceló la selección de la carpeta.";
            System.out.println(message);
        }
        
        return rutaCarpeta;
    }



}