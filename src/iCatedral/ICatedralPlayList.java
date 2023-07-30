package iCatedral;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ICatedralPlayList {

	public static ArrayList<ICatedral> currentPlayList = new ArrayList<>();
	
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
    private String NtalPathFileLocation;
    private String AcflPathFileLocation;
    // characteristics order = T, C, L, Q, M, W
    public String[] armadura;
    public String[] songNumber = new String[1];
    private static String message = "";
	
    public ICatedralPlayList(String name, String pathFileLocation){
        
        this.name = name;
        this.pathFileLocation = (pathFileLocation + "\\" + name + ".bkpl");
        NtalPathFileLocation = (pathFileLocation + "\\" + name + ".ntal");
        AcflPathFileLocation = (pathFileLocation + "\\" + name + ".acfl");
        Arrays.fill(bellActivation, "0");
        
        File testFile = new File(this.pathFileLocation);
        
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
            
            setArmadura(currentPlayList);
                        
            createNtal();
            
            fillNtal(currentPlayList);
            
            createACFL();
            
            fillAcfL();
        }
        
        else {
            
        	message = "Ya existe un archivo con ese nombre";
            System.out.println(message);}
        
    }
    
    public ICatedralPlayList(String AcfPathFileLocation){
        
        this.AcflPathFileLocation = AcfPathFileLocation;
        Arrays.fill(bellActivation, "0");
        
        File testFile = new File(this.pathFileLocation);
        
        if (testFile.exists()) {  
        	message = "Se ha añadido el  archivo " + name;
            System.out.println(message);
        }
        
        else {
            
        	message = "No existe ningun archivo con ese nombre";
            System.out.println(message);}
        
    }
    
    private void createNtal(){
        
        try {
                FileWriter creator = new FileWriter(NtalPathFileLocation);
                creator.close();
                message = "Se ha creado el  archivo " + name + ".ntal";
                System.out.println(message);
            } catch (IOException e) {
            	message = "Ha ocurrido un error al crear el archivo .ntal .";
                System.out.println(message);
                e.printStackTrace();
            }
        
    }
    
    private void createACFL(){
        
        try {
                FileWriter creator = new FileWriter(AcflPathFileLocation);
                creator.close();
                message = "Se ha creado el  archivo " + name + ".acfl";
                System.out.println(message);
            } catch (IOException e) {
            	message = "Ha ocurrido un error al crear el archivo .acfl .";
                System.out.println(message);
                e.printStackTrace();
            }
        
    }
    
    private void fillNtal(ArrayList<ICatedral> playList){
    	
    	int songNumer = 1;
    	            
    	try {
    		
	        File writeFile = new File(NtalPathFileLocation);
	        BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));
    		
    		for (ICatedral song : playList) {
                		
	            File readFile = new File(song.getNtaPathFileLocation());
	            BufferedReader reader = new BufferedReader(new FileReader(readFile));
	            
	            String line = reader.readLine();
	            
	            while (line != null) {
	            	
	            	if (line.contains("Cancion")) {
	                    line = "Cancion " + songNumer;
	                    songNumer++;
	                }

	                writer.write(line);
	                writer.newLine();
	                line = reader.readLine();
	            } 
	            
	            reader.close();
	            
	            message = "Archivo " + song.getName() + " agregado.";
	            System.out.println(message);
	            
    		}
    		
    		writer.close();
        } 
        catch (IOException e) {
        	message = "Error al crear el archivo .ntal " ;
            System.out.println(message + e.getMessage());
        }
    	
    }
    
    private void fillAcfL(){
        
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
            
                File readFile = new File(NtalPathFileLocation);
                File writeFile = new File(AcflPathFileLocation);
                BufferedReader reader = new BufferedReader(new FileReader(readFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile));

                String line = reader.readLine();

                while(line != null){

                    if (line.contains("Cancion")){

                        writer.write("11001000 ");
                        
                        writer.newLine();
                        
                        binario = toBinario(line.substring(8));
                        
                        writer.write(binario + " ");
                        
                        writer.newLine();
                        
                        line = reader.readLine();
//                        line = reader.readLine();
                    }
                    
                    else if (line.contains("K")){
                        kcounter++;
                        line = reader.readLine();}
                    
                    else {
                    
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
   
                }
                
                writer.write("11001000 ");
                writer.newLine();
                writer.write("11001000 ");
                
                reader.close();
                writer.close();
                    
            }
            
            catch (IOException e) {
            	message = "Erro al convertir el archivo a acfl. ";
                System.out.println(message + e.getMessage());            
            }
        
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
    
    private void setArmadura(ArrayList<ICatedral> playList){
    	
    	ArrayList<String> armaduraSetter = new ArrayList<>();
    	
    	for (ICatedral song : playList) {
    		
    		for (int i = 0; i < song.armadura.length; i++) {
    			armaduraSetter.add(song.armadura[i]);
			}
    		
    	}
    	    	
    	armadura = armaduraSetter.toArray(new String[armaduraSetter.size()]);
    	
    }
    
    public String getAcflPathFileLocation(){return AcflPathFileLocation;}
}
