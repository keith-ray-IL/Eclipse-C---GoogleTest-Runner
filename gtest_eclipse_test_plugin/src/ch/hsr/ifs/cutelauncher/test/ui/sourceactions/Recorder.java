package ch.hsr.ifs.cutelauncher.test.ui.sourceactions;

import java.util.HashSet;

@SuppressWarnings("unchecked")
public class Recorder {
	static HashSet<String> hs;
	
	public static void store(HashSet hh){
		if(hs == null){
			hs=new HashSet();
		}
		
		hs.addAll(hh);

	}
	
	public static void printUniqueCall(){
		if(hs!=null){
			System.out.println("###########Start");
			for(String i:hs){
				System.out.println(i);
			}System.out.println("###########End");
		}else System.out.println("nothing");
			
	}
}
