package vkinterface.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonMap {

	String title;
	HashMap<String, String> map;
	
	enum InputState{
		PRE_KEY, KEY, POST_KEY, VALUE_TYPE, ARRAY, OBJECT, STRING, PRIMITIVE;
	}
	
	//The method also converts "\\" into "\" and "\n" into real newlinecharacter.
	//Maybe TODO add full escape sequences support?
	public JsonMap(String text) throws Exception{
		map = new HashMap<String, String>();
		
		InputState state = InputState.KEY;
		
		if(text.charAt(0)=='{' && text.charAt(text.length()-1)=='}'){
			text = text.substring(1, text.length()-1);
		}
		
		char inText[] = text.toCharArray();
		
		StringBuilder temp = new StringBuilder();
		boolean isQuotes=false;
		int countBrackets = 0;
		
		String currentKey = null;
		
		boolean exitFlag = false;

		int i=-1;//TRUST ME, I AM ENGINEER!!
		
		mainLoop: while(!exitFlag){
			i++;//DO NOT ASK, PLEASE!!
			
			if(i>=inText.length){
				exitFlag=true;
			}
			
			switch(state){
			case PRE_KEY:
				if(exitFlag) continue mainLoop;
				
				switch(inText[i]){
				case ' ':
					continue mainLoop;
				case ',':
					currentKey="";
					isQuotes=false;
					temp = new StringBuilder();
					
					state= InputState.KEY;
					break;
				default: 
					System.err.println(text);//TODO not best way to handle errors
					throw new Exception("JSON FORMAT ERROR while finding key --"
							+ " unexpected shit after value");
				}
				break;
			case KEY:
				if(exitFlag) throw new Exception("JSON FORMAT ERROR while reading key -- unexpected EOS");
				
				if(!isQuotes){
					switch(inText[i]){
					case ' ':
						continue mainLoop;
					case '"':
						isQuotes=true;
						break;
					default:
						throw new Exception("JSON FORMAT ERROR while finding key --"
							+ " unexpected shit before key");
					}
				}else{
					switch(inText[i]){
					case '"':
						if(temp.length()<1) throw new Exception("JSON FORMAT ERROR while reading key -- unexpected sequence end");
						
						currentKey = temp.toString();							
						state= InputState.POST_KEY;
					default:
						temp.append(inText[i]);
					}
				}
				break;
			case POST_KEY:
				if(exitFlag) throw new Exception("JSON FORMAT ERROR while finding value -- unexpected EOS");
				
				switch(inText[i]){
				case ' ':
					continue mainLoop;
				case ':':
					temp = new StringBuilder();
					state= InputState.VALUE_TYPE;
					break;
				default: 
					throw new Exception("JSON FORMAT ERROR while finding key --"
							+ " unexpected shit after key");
				}
				break;		
			case VALUE_TYPE:
				if(exitFlag) throw new Exception("JSON FORMAT ERROR while recognizing value type -- unexpected EOS");
				
				switch(inText[i]){
				case ' ':
					continue mainLoop;
				case '"':
					state= InputState.STRING;
					break;
				case '{':
					state= InputState.OBJECT;
					countBrackets = 1;
					break;
				case '[':
					state= InputState.ARRAY;
					countBrackets = 1;
					break;
				default:
					//TODO need check -- PRIMITIVE can only be boolean or number
					state = InputState.PRIMITIVE;
					temp.append(inText[i]);
					//TODO Awful, think how to rewrite
					if(i+1>=inText.length || inText[i+1]==','){
				
						map.put(currentKey, temp.toString());					
						
						state = InputState.PRE_KEY;
					}
				}
				break;
			case STRING:
				if(exitFlag) throw new Exception("JSON FORMAT ERROR while building string -- unexpected EOS");
				
				switch(inText[i]){
				case '"':
					//TODO Kostil dikiy
					if((inText[i-1]!='\\')){
						//Note: no need in that; str can be blank
						/*if(temp.length()<1){
							System.err.println(text);
							throw new Exception("JSON FORMAT ERROR while building string -- unexpected sequence end");
						}*/

						map.put(currentKey, temp.toString());
						
						state = InputState.PRE_KEY;

					}
					
					temp.append(inText[i]);
					
					break;
				default:
					//XXX Kostil dikiy
					if(! (inText[i-2]!='\\' && inText[i-1]=='\\') ){
						temp.append(inText[i]);
					}else if(inText[i]=='n'){
						temp.append("\n");
					}
					break;
				}
				break;
			case ARRAY:
				if(exitFlag) throw new Exception("JSON FORMAT ERROR while reading array -- unexpected EOS");
							
				switch(inText[i]){
				case ']':
					countBrackets--;
					if(countBrackets==0){				
						map.put(currentKey, temp.toString());	
						state = InputState.PRE_KEY;
					}else{
						temp.append(inText[i]);	
					}
					break;
				case '[':
					countBrackets++;
					//fall through	
				default:
					temp.append(inText[i]);
					break;
				}
				break;
			case OBJECT:
				if(exitFlag) throw new Exception("JSON FORMAT ERROR while reading object -- unexpected EOS");
				
				switch(inText[i]){
				case '}':
					countBrackets--;
					if(countBrackets==0){
						if(temp.length()<1){
							throw new Exception("JSON FORMAT ERROR while reading object -- unexpected sequence end");
						}
				
						map.put(currentKey, temp.toString());					
						
						state = InputState.PRE_KEY;
					}else{
						temp.append(inText[i]);
					}
					break;
				case '{':
					countBrackets++;
					//fall through
				default:
					temp.append(inText[i]);
					break;
				}
				break;
			case PRIMITIVE:
				if(exitFlag) throw new Exception("JSON FORMAT ERROR while reading primitive -- unexpected EOS");
			
				//TODO need check -- PRIMITIVE can only be boolean or number
				
				switch(inText[i]){
				case ' ':			
					map.put(currentKey, temp.toString());					
					
					state = InputState.PRE_KEY;
					break;
				default:
					temp.append(inText[i]);
					
					
					//TODO Awful, think how to rewrite
					if(i+1>=inText.length || inText[i+1]==','){
						if(temp.length()<1){
							throw new Exception("JSON FORMAT ERROR while reading primitive -- unexpected sequence end");
						}
				
						map.put(currentKey, temp.toString());					
						
						state = InputState.PRE_KEY;
					}
					
					break;
				}
				break;
			default: throw new Exception("JSON FORMAT ERROR (invalid state)");
			}
		}		
	}	
	
	public String getByKey(String key){
		return map.get(key);
	}

	public boolean contains(String key) {
		return map.containsKey(key);
	}

	public static JsonMap[] parseObjectsArray(String text) throws Exception {
		
		/*if(text.charAt(0)=='[' && text.charAt(text.length()-1)==']'){
			text = text.substring(1, text.length()-1);
		}*/
		
		if (text.length()==0){
			return new JsonMap[0];
		}
		
		char[] inText = text.toCharArray();
		
		int bracketsCount = 0;
		
		ArrayList<JsonMap> ret = new ArrayList<JsonMap>();
		
		boolean exitFlag = false;
		
		StringBuilder temp = null;
		
		int i=0;
		
		while(!exitFlag){
			if(bracketsCount==0){
				if(inText[i]=='{'){
					bracketsCount++;
					temp = new StringBuilder();
					temp.append('{');
				}
				//TODO care about correctness
				//(now it can read nicely "{abc:bcd} SHIT,SHIT {abc:bcd}", but it has not to)
			}else{
				temp.append(inText[i]);
				
				if(inText[i]=='{'){
					bracketsCount++;
				}else if(inText[i]=='}'){
					bracketsCount--;
				}
				
				if(bracketsCount==0){
					ret.add(new JsonMap(temp.toString()));
				}
			}
		
			i++;
			if(i>=inText.length) exitFlag=true; //TODO care about unexpected end
		}
		
		return ret.toArray(new JsonMap[ret.size()]);
	}
	
}
