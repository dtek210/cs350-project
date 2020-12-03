package cs350f20project.controller.cli.parser;

import java.util.ArrayList;
import java.util.List;

import cs350f20project.Startup;
import cs350f20project.controller.ActionProcessor;
import cs350f20project.controller.command.A_Command;
import cs350f20project.controller.command.PointLocator;
import cs350f20project.controller.command.behavioral.A_CommandBehavioral;
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
import cs350f20project.controller.command.meta.CommandMetaDoExit;
import cs350f20project.datatype.Angle;
import cs350f20project.datatype.CoordinatesDelta;
import cs350f20project.datatype.CoordinatesWorld;
import cs350f20project.datatype.Latitude;
import cs350f20project.datatype.Longitude;

public class CommandParser {
	//THIS IS PROBLEM STAYS NULL
	private static A_ParserHelper parserHelper ;
	private static String commandText = "DO BRAKE train1;";
	
	CommandParser(A_ParserHelper parserHelper,String commandText){
		CommandParser.parserHelper = parserHelper;
		CommandParser.commandText = commandText;
	}
	
	public static void main(String[] args) {
		//MyParserHelper parserHelper = new MyParserHelper(null);	
		Startup myStartUp = new Startup();
		
		CommandParser parser = new CommandParser(parserHelper, commandText);
		parser.parse();
		myStartUp.go();
		System.out.println("compiled maybe");
	}


	public void parse(){
		String [] commandList = commandText.split(";");	
		
		for(String command : commandList ) {
			if(command.contains("DO") || command.contains("do")){
				parseBehavioralCommand(command);
			}
			if(command.contains("CREATE") || command.contains("create")){
				parseCreationalCommand(command);
			}
			if(command.contains("@")){
				parseMetaCommand(command);
			}
		}
		
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
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 28 CommandCreateStockCarBox
		if((items[1]+ " "+ items[2]+ " "+ items[4]+ " "+ items[5]).equals("STOCK CAR AS BOX")){
			A_Command command = new CommandCreateStockCarBox((String)items[3]);
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 47 CommandCreateTrackStraight
		if((items[1]+ " "+ items[2]+ " "+ items[4]).equals("TRACK STRAIGHT REFERENCE")){
			
			CoordinatesDelta start;
			CoordinatesDelta end;
			PointLocator pl = null;
			
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[7])) {
					CoordinatesWorld world = parserHelper.getReference(items[7]);
					start = new CoordinatesDelta(Double.parseDouble(items[9]),Double.parseDouble(items[11]));
					end = new CoordinatesDelta(Double.parseDouble(items[12]),Double.parseDouble(items[13]));
					pl = new PointLocator(world,start,end);
				}
			}else {
			//sets up point locator
			start = new CoordinatesDelta(Double.parseDouble(items[10]),Double.parseDouble(items[12]));
			end = new CoordinatesDelta(Double.parseDouble(items[14]),Double.parseDouble(items[16]));
			Latitude lat = new Latitude(Double.parseDouble(items[5]));
			Longitude lng = new Longitude(Double.parseDouble(items[7]));
			CoordinatesWorld world = new CoordinatesWorld(lat,lng);
			pl = new PointLocator(world,start,end);
			}
			A_Command command = new CommandCreateTrackStraight((String)items[3], pl);
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		
		if((items[1]+ " "+ items[2]+ " "+ items[3]).equals("TRACK SWITCH TURNOUT")){
			CoordinatesDelta start1 = null;
			CoordinatesDelta end1 = null;
			CoordinatesDelta start2 = null;
			CoordinatesDelta end2 = null;
			CoordinatesDelta origin = null;
			CoordinatesWorld world = null;
			
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[7])) {
					world = parserHelper.getReference(items[7]);
					start1 = new CoordinatesDelta(Double.parseDouble(items[11]),Double.parseDouble(items[13]));
					end1 = new CoordinatesDelta(Double.parseDouble(items[15]),Double.parseDouble(items[17]));
					start2 = new CoordinatesDelta(Double.parseDouble(items[21]),Double.parseDouble(items[23]));
					end2 = new CoordinatesDelta(Double.parseDouble(items[25]),Double.parseDouble(items[27]));
					origin = new CoordinatesDelta(Double.parseDouble(items[30]),Double.parseDouble(items[31]));
				}
					
			}else{
				Latitude lat = new Latitude(Double.parseDouble(items[6]));
				Longitude lng = new Longitude(Double.parseDouble(items[8]));
				world = new CoordinatesWorld(lat,lng);
				start1 = new CoordinatesDelta(Double.parseDouble(items[12]),Double.parseDouble(items[14]));
				end1 = new CoordinatesDelta(Double.parseDouble(items[16]),Double.parseDouble(items[18]));
				start2 = new CoordinatesDelta(Double.parseDouble(items[22]),Double.parseDouble(items[24]));
				end2 = new CoordinatesDelta(Double.parseDouble(items[26]),Double.parseDouble(items[28]));
				origin = new CoordinatesDelta(Double.parseDouble(items[31]),Double.parseDouble(items[33]));
					
			}
				A_Command command = new CommandCreateTrackSwitchTurnout(items[4],world,start1,end1,start2,end2,origin);
				CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		
	}

	private void parseBehavioralCommand(String commandTextItem) {
		// 
		Angle angle;
		String [] items = commandTextItem.split("\\s");
		 
		// Rule 2 CommandBehavioralBrake parse
		if((items[1]).equals("BRAKE")){
			A_Command command = new CommandBehavioralBrake(items[2]);
			CommandParser.parserHelper.getActionProcessor().schedule(command);		
		}
		
		// Rule 6 CommandBehavioralSelectBridge parse
		if((items[1] + " " + items[2]).equals("SELECT DRAWBRIDGE")){
			A_Command command = new CommandBehavioralSelectBridge(items[3],Boolean.parseBoolean(items[4]));
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 7 CommandBehavioralSelectRoundhouse parse
		if((items[1] + " " + items[2]).equals("SELECT ROUNDHOUSE")){
			angle = new Angle(Double.parseDouble( items[4]));
			A_Command command = new CommandBehavioralSelectRoundhouse(items[3],angle,Boolean.parseBoolean(items[5]));
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 8  CommandBehavioralSelectSwitch parse
		if((items[1]+ " "+ items[2]+ " "+ items[4]).equals("SELECT SWITCH PATH")){
			A_Command command = new CommandBehavioralSelectSwitch(items[3],Boolean.parseBoolean(items[5]));
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 11 CommandBehavioralSetDirection
		if((items[1]+ " "+ items[3]).equals("SET DIRECTION")){
			boolean bool = isBoolean(items[4]);
			A_Command command = new CommandBehavioralSetDirection((String)items[2],bool);
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 12 CommandBehavioralSetReference
		if((items[1]+ " "+ items[2]+ " "+ items[3]).equals("SET REFERENCE ENGINE")){
			A_Command command = new CommandBehavioralSetReference((String)items[4]);
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
		// Rule 15 CommandBehavioralSetSpeed
		if((items[1]+ " "+ items[3]).equals("SET SPEED")){
			A_Command command = new CommandBehavioralSetSpeed((String)items[2],Double.parseDouble(items[4]));
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
		
	
		
	
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*
	// Rule 2 CommandBehavioralBrake parse
	// (string id)
	public void parseRule2(){
		String [] items = commandText.split("\\s");
		 
		if((items[0] + " " + items[1]).equals("DO BRAKE")){
			A_Command command = new CommandBehavioralBrake(items[2]);
			CommandParser.parserHelper.getActionProcessor().schedule(command);		
		}
	}
	
	// Rule 6 CommandBehavioralSelectBridge parse
	// (string id, boolean toDraw)
	public void parseRule6(){
		String [] items = commandText.split("\\s");
		
		if((items[0] + " " + items[1]+ " "+ items[2]).equals("DO SELECT DRAWBRIDGE")){
				A_Command command = new CommandBehavioralSelectBridge(items[3],Boolean.parseBoolean(items[4]));
				CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}

	// Rule 7 CommandBehavioralSelectRoundhouse parse
	// (string id, Angle angle, boolean isClockwise)
	public void parseRule7(){
		String [] items = commandText.split("\\s");
		Angle angle = new Angle(Double.parseDouble( items[4]));
		if((items[0] + " " + items[1]+ " "+ items[2]).equals("DO SELECT ROUNDHOUSE")){
			
				A_Command command = new CommandBehavioralSelectRoundhouse(items[3],angle,Boolean.parseBoolean(items[5]));
				CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}

	// Rule 8  CommandBehavioralSelectSwitch parse
	// (String id, boolean isPrimaryElseSecondary)
	public void parseRule8(){
		String [] items = commandText.split("\\s");
		
		if((items[0] + " " + items[1]+ " "+ items[2]+ " "+ items[4]).equals("DO SELECT SWITCH PATH")){
				A_Command command = new CommandBehavioralSelectSwitch(items[3],Boolean.parseBoolean(items[5]));
				CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}
	
	// Rule 11 CommandBehavioralSetDirection
	// (String id, boolean isForwardElseBackward)
	public void parseRule11(){
		String [] items = commandText.split("\\s");
		
		if((items[0] + " " + items[1]+ " "+ items[3]).equals("DO SET DIRECTION")){
				boolean bool = isBoolean(items[4]);
				A_Command command = new CommandBehavioralSetDirection((String)items[2],bool);
				CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}
	
	// Rule 12 CommandBehavioralSetReference
	// (string id)
	public void parseRule12(){
		String [] items = commandText.split("\\s");
		
		
		if((items[0] + " " + items[1]+ " "+ items[2]+ " "+ items[3]).equals("DO SET REFERENCE ENGINE")){
				A_Command command = new CommandBehavioralSetReference((String)items[4]);
				CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}
	
	// Rule 15 CommandBehavioralSetSpeed
	// (java.lang.String id, double speed)
	public void parseRule15(){
		String [] items = commandText.split("\\s");
			
		if((items[0] + " " + items[1]+ " "+ items[3]).equals("DO SET SPEED")){
			A_Command command = new CommandBehavioralSetSpeed((String)items[2],Double.parseDouble(items[4]));
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}
	
	
	
	
	
	
	
	// Rule 22 CommandCreatePowerCatenary
		// java.lang.String id, java.util.List<java.lang.String> idPoles
	public void parseRule22(){
		String [] items = commandText.split("\\s");
		List<String> idPoles = new ArrayList<String>();
		//pole IDs start after 4th element
		int count = 4; 		
		    
		while (idPoles.size() > count) {
			idPoles.add(items[count]);
		    count++;
		}
		      
		if((items[0] + " " + items[1]+ " "+ items[2]).equals("CREATE POWER CATENARY")){
			A_Command command = new CommandCreatePowerCatenary(items[3],idPoles);
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}
	
	// Rule 28 CommandCreateStockCarBox
	// (string id)
	public void parseRule28(){
		String [] items = commandText.split("\\s");
		
		if((items[0] + " " + items[1]+ " "+ items[2]+ " "+ items[4]+ " "+ items[5]).equals("CREATE STOCK CAR AS BOX")){
				A_Command command = new CommandCreateStockCarBox((String)items[3]);
				CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}
	
	// Rule 47 CommandCreateTrackStraight
	// ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
	// CREATE TRACK STRAIGHT id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
	public void parseRule47(){
		String [] items = commandText.split("\\s");
			
		if((items[0] + " " + items[1]+ " "+ items[2]+ " "+ items[4]).equals("CREATE TRACK STRAIGHT REFERENCE")){
			
			CoordinatesDelta start;
			CoordinatesDelta end;
			PointLocator pl = null;
			
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[7])) {
					CoordinatesWorld world = parserHelper.getReference(items[7]);
					start = new CoordinatesDelta(Double.parseDouble(items[9]),Double.parseDouble(items[11]));
					end = new CoordinatesDelta(Double.parseDouble(items[12]),Double.parseDouble(items[13]));
					pl = new PointLocator(world,start,end);
				}
			}else {
			//sets up point locator
			start = new CoordinatesDelta(Double.parseDouble(items[10]),Double.parseDouble(items[12]));
			end = new CoordinatesDelta(Double.parseDouble(items[14]),Double.parseDouble(items[16]));
			Latitude lat = new Latitude(Double.parseDouble(items[5]));
			Longitude lng = new Longitude(Double.parseDouble(items[7]));
			CoordinatesWorld world = new CoordinatesWorld(lat,lng);
			pl = new PointLocator(world,start,end);
			}
			A_Command command = new CommandCreateTrackStraight((String)items[3], pl);
			CommandParser.parserHelper.getActionProcessor().schedule(command);
		}
	}

		
	// Rule 48 CommandCreateTrackSwitchTurnout
		// CREATE TRACK SWITCH TURNOUT id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) STRAIGHT DELTA START coordinates_delta1 END 
		// coordinates_delta2 CURVE DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number
		// 
	public void parseRule48()
	{
		String [] items = commandText.split("\\s");
		CoordinatesDelta start1 = null;
		CoordinatesDelta end1 = null;
		CoordinatesDelta start2 = null;
		CoordinatesDelta end2 = null;
		CoordinatesDelta origin = null;
		CoordinatesWorld world = null;
		
		if((items[0] + " " + items[1]+ " "+ items[2]+ " "+ items[3]).equals("CREATE TRACK SWITCH TURNOUT"))
		{
				
			if(commandText.contains("$")) 
			{
				if( parserHelper.hasReference(items[7])) 
				{
					world = parserHelper.getReference(items[7]);
					start1 = new CoordinatesDelta(Double.parseDouble(items[11]),Double.parseDouble(items[13]));
					end1 = new CoordinatesDelta(Double.parseDouble(items[15]),Double.parseDouble(items[17]));
					start2 = new CoordinatesDelta(Double.parseDouble(items[21]),Double.parseDouble(items[23]));
					end2 = new CoordinatesDelta(Double.parseDouble(items[25]),Double.parseDouble(items[27]));
					origin = new CoordinatesDelta(Double.parseDouble(items[30]),Double.parseDouble(items[31]));
				}
					
			}else 
			{
				Latitude lat = new Latitude(Double.parseDouble(items[6]));
				Longitude lng = new Longitude(Double.parseDouble(items[8]));
				world = new CoordinatesWorld(lat,lng);
				start1 = new CoordinatesDelta(Double.parseDouble(items[12]),Double.parseDouble(items[14]));
				end1 = new CoordinatesDelta(Double.parseDouble(items[16]),Double.parseDouble(items[18]));
				start2 = new CoordinatesDelta(Double.parseDouble(items[22]),Double.parseDouble(items[24]));
				end2 = new CoordinatesDelta(Double.parseDouble(items[26]),Double.parseDouble(items[28]));
				origin = new CoordinatesDelta(Double.parseDouble(items[31]),Double.parseDouble(items[33]));
					
			}
				A_Command command = new CommandCreateTrackSwitchTurnout(items[4],world,start1,end1,start2,end2,origin);
				CommandParser.parserHelper.getActionProcessor().schedule(command);
			}
		}
*/
	private boolean isBoolean(String a) {
		if(a.equalsIgnoreCase("true")) {
			return true;
		}
		return false;
		
	}
	
}
