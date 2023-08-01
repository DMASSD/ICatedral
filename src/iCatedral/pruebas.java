package iCatedral;

public class pruebas {

	public static void main(String[] args) {  
        
	    ICatedral guadalupe = new ICatedral("");

	    ICatedral himno = new ICatedral("");

//	    ICatedral chiapaneca = new ICatedral("");

	    ICatedral navidad = new ICatedral("");
	       
	    ICatedralPlayList.currentPlayList.add(guadalupe);
	    
	    ICatedralPlayList.currentPlayList.add(himno);
	    
//	    ICatedralPlayList.currentPlayList.add(chiapaneca);
	    
	    ICatedralPlayList.currentPlayList.add(navidad);
	    
	    for(ICatedral song : ICatedralPlayList.currentPlayList) {
	    	for(String armadura : song.getAllArmadura()) {
	    		System.out.println(armadura);
	    	}
	    }
	       	    
	    ICatedralPlayList pruebaList = new ICatedralPlayList();
	    
	    System.out.println(pruebaList.toString());
	    
	    
	}
}
