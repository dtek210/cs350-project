package cs350f20project.controller.cli.parser;

import java.util.ArrayList;
import java.util.List;

import cs350f20project.Startup;
import cs350f20project.controller.command.A_Command;
import cs350f20project.controller.command.PointLocator;
import cs350f20project.controller.command.behavioral.CommandBehavioralBrake;
import cs350f20project.controller.command.behavioral.CommandBehavioralSelectBridge;
import cs350f20project.controller.command.behavioral.CommandBehavioralSelectRoundhouse;
import cs350f20project.controller.command.behavioral.CommandBehavioralSelectSwitch;
import cs350f20project.controller.command.behavioral.CommandBehavioralSetDirection;
import cs350f20project.controller.command.behavioral.CommandBehavioralSetReference;
import cs350f20project.controller.command.behavioral.CommandBehavioralSetSpeed;
import cs350f20project.controller.command.creational.CommandCreatePowerCatenary;
import cs350f20project.controller.command.creational.CommandCreateStockCarBox;
import cs350f20project.controller.command.creational.CommandCreateTrackStraight;
import cs350f20project.controller.command.creational.CommandCreateTrackSwitchTurnout;
import cs350f20project.datatype.*;

public class CommandParser {
	

	public String commandText;

	public MyParserHelper parserHelper;
	
	public CommandParser(MyParserHelper parser_Helper,String command_Text){
		parserHelper = parser_Helper;
		commandText = command_Text;
	}
	
	public static void main(String[] args) {
		Startup myStartUp = new Startup();
		myStartUp.go();
		System.out.println("test");
	}


	public void parse(){
		String [] commandList = commandText.split(";");	
		String [] MetaCalls = {"OPEN", "CLOSE" , "@"}; 
		String [] StructuralCalls = {"UNCOUPLE","LOCATE","COUPLE","COMMIT"};
		
		for(String command : commandList ) {
			if(command.contains("DO") || command.contains("do")){
				parseBehavioralCommand(command);
			}
			if(command.contains("CREATE") || command.contains("create")){
				parseCreationalCommand(command);
			}
			
			for (String call : MetaCalls) {
				if(command.contains(call)){
					parseMetaCommand(command);
				}
			}
			
			for (String call : StructuralCalls) {
				if(command.contains(call)){
					parseStructuralCommand(command);
				}
			}
			
		}
		
	}
	
	private void parseStructuralCommand(String command) {
	
		
	}

	private void parseMetaCommand(String command) {
		
		
	}

	private void parseCreationalCommand(String commandTextItem) {
		String [] items = commandTextItem.split("\\s");
		List<String> idPoles = new ArrayList<String>();

		// Rule 22 CommandCreatePowerCatenary      
		if((items[1]+ " "+ items[2]).equals("POWER CATENARY")){
			//pole IDs start after 4th element
			int count = 4; 		
				    
			while ((items.length - 4) > count) {
				idPoles.add(items[count]);
				count++;
			}	
			A_Command command = new CommandCreatePowerCatenary(items[3],idPoles);
			parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 28 CommandCreateStockCarBox
		if((items[1]+ " "+ items[2]+ " "+ items[4]+ " "+ items[5]).equals("STOCK CAR AS BOX")){
			A_Command command = new CommandCreateStockCarBox((String)items[3]);
			parserHelper.getActionProcessor().schedule(command);
		}
		
		
		// Rule 47 CommandCreateTrackStraight
		if((items[1]+ " "+ items[2]+ " "+ items[4]).equals("TRACK STRAIGHT REFERENCE")){
			
            CoordinatesWorld world = null;
			
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[6].substring(1))) {
					world = parserHelper.getReference(items[6].substring(1));
					
				}
			}else {
				//sets up point locator 
				String[] coor = items[5].split("/");
	            String [] lat = coor[0].split("\\*|'");
	            String [] lon = coor[1].split("\\*|'|\"");
	            Latitude worldLat = new Latitude(Integer.parseInt(lat[0]),Integer.parseInt(lat[1]),Double.parseDouble(lat[2]));
				Longitude worldLon = new Longitude(Integer.parseInt(lon[0]),Integer.parseInt(lon[1]),Double.parseDouble(lon[2]));
				world = new CoordinatesWorld(worldLat,worldLon);
			}
			String[] deltaStart = items[8].split(":");
			String[] deltaEnd = items[10].split(":");
			CoordinatesDelta start = new CoordinatesDelta(Double.parseDouble(deltaStart[0]),Double.parseDouble(deltaStart[1]));
			CoordinatesDelta end = new CoordinatesDelta(Double.parseDouble(deltaEnd[0]),Double.parseDouble(deltaEnd[1]));
			PointLocator pl = new PointLocator(world,start,end);
			A_Command command = new CommandCreateTrackStraight((String)items[3], pl);
			parserHelper.getActionProcessor().schedule(command);
		}
		
		//rule 48 CommandCreateTrackSwitchTurnout
		if((items[1]+ " "+ items[2]+ " "+ items[3]).equals("TRACK SWITCH TURNOUT")){
			CoordinatesDelta start1 = null;
			CoordinatesDelta end1 = null;
			CoordinatesDelta start2 = null;
			CoordinatesDelta end2 = null;
			CoordinatesDelta origin = null;
			CoordinatesWorld world = null;
			
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[7].substring(1))) {
					world = parserHelper.getReference(items[7].substring(1));
				}
					
			}else{
				String[] coor = items[6].split("/");
	            String [] lat = coor[0].split("\\*|'");
	            String [] lon = coor[1].split("\\*|'|\"");
	            Latitude worldLat = new Latitude(Integer.parseInt(lat[0]),Integer.parseInt(lat[1]),Double.parseDouble(lat[2]));
				Longitude worldLon = new Longitude(Integer.parseInt(lon[0]),Integer.parseInt(lon[1]),Double.parseDouble(lon[2]));
				world = new CoordinatesWorld(worldLat,worldLon);
			} 
				String [] deltaStart = items[10].split(":");
				String [] deltaEnd = items[12].split(":");
				String [] curveDeltaStart = items[16].split(":");
				String [] curveDeltaEnd = items[18].split(":");
				String [] deltaOrigin = items[21].split(":");
				start1 = new CoordinatesDelta(Double.parseDouble(deltaStart[0]),Double.parseDouble(deltaStart[1]));
				end1 = new CoordinatesDelta(Double.parseDouble(deltaEnd[0]),Double.parseDouble(deltaEnd[1]));
				start2 = new CoordinatesDelta(Double.parseDouble(curveDeltaStart[0]),Double.parseDouble(curveDeltaStart[1]));
				end2 = new CoordinatesDelta(Double.parseDouble(curveDeltaEnd[0]),Double.parseDouble(curveDeltaEnd[1]));
				origin = new CoordinatesDelta(Double.parseDouble(deltaOrigin[0]),Double.parseDouble(deltaOrigin[1]));
				A_Command command = new CommandCreateTrackSwitchTurnout(items[4],world,start1,end1,start2,end2,origin);
				parserHelper.getActionProcessor().schedule(command);
		}
		
	}

	private void parseBehavioralCommand(String commandTextItem) {
		// 
		Angle angle;
		String [] items = commandTextItem.split("\\s");
		 
		// Rule 2 CommandBehavioralBrake parse
		if((items[1]).equals("BRAKE")){
			A_Command command = new CommandBehavioralBrake(items[2]);
			parserHelper.getActionProcessor().schedule(command);		
		}
		
	
		// Rule 6 CommandBehavioralSelectBridge parse
		if((items[1] + " " + items[2]).equals("SELECT DRAWBRIDGE")){
			A_Command command = new CommandBehavioralSelectBridge(items[3],Boolean.parseBoolean(items[4]));
			parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 7 CommandBehavioralSelectRoundhouse parse
		if((items[1] + " " + items[2]).equals("SELECT ROUNDHOUSE")){
			angle = new Angle(Double.parseDouble( items[4]));
			A_Command command = new CommandBehavioralSelectRoundhouse(items[3],angle,Boolean.parseBoolean(items[5]));
			parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 8  CommandBehavioralSelectSwitch parse
		if((items[1]+ " "+ items[2]+ " "+ items[4]).equals("SELECT SWITCH PATH")){
			A_Command command = new CommandBehavioralSelectSwitch(items[3],Boolean.parseBoolean(items[5]));
			parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 11 CommandBehavioralSetDirection
		if((items[1]+ " "+ items[3]).equals("SET DIRECTION")){
			boolean bool = isBoolean(items[4]);
			A_Command command = new CommandBehavioralSetDirection((String)items[2],bool);
			parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 12 CommandBehavioralSetReference
		if((items[1]+ " "+ items[2]+ " "+ items[3]).equals("SET REFERENCE ENGINE")){
			A_Command command = new CommandBehavioralSetReference((String)items[4]);
			parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 15 CommandBehavioralSetSpeed
		if((items[1]+ " "+ items[3]).equals("SET SPEED")){
			A_Command command = new CommandBehavioralSetSpeed((String)items[2],Double.parseDouble(items[4]));
			parserHelper.getActionProcessor().schedule(command);
		}
		
	
		
	
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	