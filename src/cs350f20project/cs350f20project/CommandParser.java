package cs350f20project.controller.cli.parser;

import java.util.*;

import java.io.*;

import cs350f20project.Startup;
import cs350f20project.controller.cli.TrackLocator;
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
import cs350f20project.controller.command.creational.CommandCreatePowerPole;
import cs350f20project.controller.command.creational.CommandCreatePowerStation;
import cs350f20project.controller.command.creational.CommandCreatePowerSubstation;
import cs350f20project.controller.command.creational.CommandCreateStockCarBox;
import cs350f20project.controller.command.creational.CommandCreateStockCarFlatbed;
import cs350f20project.controller.command.creational.CommandCreateStockCarPassenger;
import cs350f20project.controller.command.creational.CommandCreateStockCarTank;
import cs350f20project.controller.command.creational.CommandCreateStockCarTender;
import cs350f20project.controller.command.creational.CommandCreateStockEngineDiesel;
import cs350f20project.controller.command.creational.CommandCreateTrackBridgeDraw;
import cs350f20project.controller.command.creational.CommandCreateTrackBridgeFixed;
import cs350f20project.controller.command.creational.CommandCreateTrackCrossing;
import cs350f20project.controller.command.creational.CommandCreateTrackCrossover;
import cs350f20project.controller.command.creational.CommandCreateTrackCurve;
import cs350f20project.controller.command.creational.CommandCreateTrackEnd;
import cs350f20project.controller.command.creational.CommandCreateTrackLayout;
import cs350f20project.controller.command.creational.CommandCreateTrackRoundhouse;
import cs350f20project.controller.command.creational.CommandCreateTrackStraight;
import cs350f20project.controller.command.creational.CommandCreateTrackSwitchTurnout;
import cs350f20project.controller.command.creational.CommandCreateTrackSwitchWye;
import cs350f20project.controller.command.meta.CommandMetaDoExit;
import cs350f20project.controller.command.meta.CommandMetaDoRun;
import cs350f20project.controller.command.meta.CommandMetaViewDestroy;
import cs350f20project.controller.command.meta.CommandMetaViewGenerate;
import cs350f20project.controller.command.structural.CommandStructuralCommit;
import cs350f20project.controller.command.structural.CommandStructuralCouple;
import cs350f20project.controller.command.structural.CommandStructuralLocate;
import cs350f20project.controller.command.structural.CommandStructuralUncouple;
import cs350f20project.datatype.*;

public class CommandParser {
	

	public String commandText;
	public MyParserHelper parserHelper;
	private ArrayList<String> commandTextArray;
	 private ArrayList<String> multipleCommands;
	
	public CommandParser(MyParserHelper parser_Helper,String command_Text){
		parserHelper = parser_Helper;
		commandText = command_Text;
		commandTextArray = new ArrayList<String>();
        multipleCommands = new ArrayList<String>();
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
	
	private void parseStructuralCommand(String commandTextItem) {
		String[] items = commandTextItem.split("\\s");
		
		///////////////rule 60
		//COMMIT
		//CommandStructuralCommit
		//Commits creational and structural configurations and prevents any further changes.
		if(items[0].equalsIgnoreCase("commit")) {
			A_Command command = new CommandStructuralCommit();
			this.parserHelper.getActionProcessor().schedule(command); 
		}//end rule 60
		
		
		
		///////////////rule 61
		//COUPLE STOCK id1 AND id2
		//CommandStructuralCouple
		//Couples stock id1 to id2.
		if((items[0] + " " + items[1]).equalsIgnoreCase("couple stock") && items[3].equalsIgnoreCase("and")) {
	   
			String id1 = items[2];
			String id2 = items[4];
	   
			A_Command command = new CommandStructuralCouple(id1, id2);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 61
		
		
		///////////////rule 62
		//LOCATE STOCK id1 ON TRACK id2 DISTANCE number FROM ( START | END )
		//CommandStructuralLocate
		//Locates stock id1 on track id2 at number meters from the start or end of the track.
		if((items[0] + " " + items[1]).equalsIgnoreCase("locate stock") && (items[3] + " " + items[4]).equalsIgnoreCase("on track")
				&& items[6].equalsIgnoreCase("distance") && items[8].equalsIgnoreCase("from")) {
	   
			//items2
			String id1 = items[2];
	   
			//items5
			String id2 = items[5];
	   
			//items7
			Double distance = Double.parseDouble(items[7]);

			//items9
			boolean startOrEnd;
			if(items[9].equalsIgnoreCase("start")) {
				startOrEnd = true;
			}
			else {
				startOrEnd = false;
			}
	   
			TrackLocator locator = new TrackLocator(id2, distance, startOrEnd);
			A_Command command = new CommandStructuralLocate(id1, locator);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 62
		
		
		///////////////rule 65
		//UNCOUPLE STOCK id1 AND id2
		//CommandStructuralUncouple
		//Uncouples stock id1 from id2.
		if((items[0] + " " + items[1]).equalsIgnoreCase("uncouple stock") && items[3].equalsIgnoreCase("and")) {
			String id1 = items[2];
			String id2 = items[4];
			A_Command command = new CommandStructuralUncouple(id1, id2);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 65
		
	}

	private void parseMetaCommand(String commandTextItem) {
		String[] items = commandTextItem.split("\\s");
		//command #51
        if (this.commandTextArray.get(0).equalsIgnoreCase("@Exit")) {
            A_Command exit = new CommandMetaDoExit();
            this.parserHelper.getActionProcessor().schedule(exit);
        }
        
      //command #52 
        else if (this.commandTextArray.get(0).equalsIgnoreCase("@run")) {
            String runString = this.commandTextArray.get(1);
            CommandMetaDoRun run = new CommandMetaDoRun(runString);
            this.parserHelper.getActionProcessor().schedule(run);
        }
        
		///////////////rule 55
		//CLOSE VIEW id
		//CommandMetaViewDestroy
		//Closes view id.
		if((items[0] + " " + items[1]).equalsIgnoreCase("close view")){
	   
			String id = items[2];
			A_Command command = new CommandMetaViewDestroy(id);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 55
		
		
		///////////////rule 56
		//OPEN VIEW id1 ORIGIN (coordinates_world|('$'id2)) WORLD WIDTH integer1 SCREEN WIDTH integer2 HEIGHT integer3
		//CommandMetaViewGenerate
		//Creates view id1 at origin coordinates_world or id2 where the world is integer1 meters wide and the screen is integer2 pixels wide and integer3 high.
		if((items[0] + " " + items[1]).equalsIgnoreCase("open view") && items[3].equalsIgnoreCase("origin") &&
				(items[5] + " " + items[6]).equalsIgnoreCase("world width") && (items[8] + " " + items[9]).equalsIgnoreCase("screen width") &&
				items[11].equalsIgnoreCase("height")) {
	   
			CoordinatesWorld origin;
	   
			//item2
			String id1 = items[2];
	   
			//item4
			if(items[4].contains("$")) {  
				origin = parserHelper.getReference(items[4].substring(1));
			}
	   
			else {
				String[] coor = items[4].split("/");
				String lat = coor[0];
				String lon = coor[1];
		   
		   
				int findLatDegree = lat.indexOf("*");
				int findLatMinutes = lat.indexOf("'");//need help with this
				int findLatSeconds = lat.indexOf('"');//need help with this
				int latDegrees = Integer.parseInt(lat.substring(0, findLatDegree));
				int latMinutes = Integer.parseInt(lat.substring(findLatDegree + 1, findLatMinutes));
				double latSeconds = Double.parseDouble(lat.substring(findLatMinutes + 1, findLatSeconds));
				Latitude worldLat = new Latitude(latDegrees, latMinutes, latSeconds);
		   
		   
				int findLonDegree = lon.indexOf("*");
				int findLonMinutes = lon.indexOf("'");//need help with this
				int findLonSeconds = lon.indexOf('"');//need help with this
				int lonDegrees = Integer.parseInt(lon.substring(0, findLonDegree));;
				int lonMinutes = Integer.parseInt(lon.substring(findLonDegree + 1, findLonMinutes));
				double lonSeconds = Double.parseDouble(lon.substring(findLonMinutes + 1, findLonSeconds));
				Longitude worldLon = new Longitude(lonDegrees, lonMinutes, lonSeconds);
		   
				origin = new CoordinatesWorld(worldLat, worldLon);   
			}
	   
			//item7
			int worldWidth = Integer.parseInt(items[7]);
	   
			//item10
			int screenWidth = Integer.parseInt(items[10]);
	   
			//item12
			int height = Integer.parseInt(items[12]);
	   
			//CoordinatesScreen screenSize
			CoordinatesScreen screenSize = new CoordinatesScreen(screenWidth, height);
	   
			A_Command command = new CommandMetaViewGenerate(id1, origin, worldWidth, screenSize);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end 56
		
	}

	private void parseCreationalCommand(String commandTextItem) {
		String[] items = commandTextItem.split("\\s");
		List<String> idPoles = new ArrayList<String>();

		// Rule 22 CommandCreatePowerCatenary
		if ((items[1] + " " + items[2]).equals("POWER CATENARY")) {
			// pole IDs start after 4th element
			int count = 4;

			while ((items.length - 4) > count) {
				idPoles.add(items[count]);
				count++;
			}
			A_Command command = new CommandCreatePowerCatenary(items[3], idPoles);
			parserHelper.getActionProcessor().schedule(command);
		}
		
		//Command 23 medium
		if(this.commandTextArray.get(1).equalsIgnoreCase("Power") && this.commandTextArray.get(2).equalsIgnoreCase("Pole")) {
			String poleId = this.commandTextArray.get(3);
			String trackId = this.commandTextArray.get(6);
			double distance = Double.parseDouble(commandTextArray.get(8));
			TrackLocator locator;
			if(this.commandTextArray.get(10).equalsIgnoreCase("START"))
				locator = new TrackLocator(trackId, distance, true);
			else
				locator = new TrackLocator(trackId, distance, false);
				A_Command pole = new CommandCreatePowerPole(poleId, locator);
				this.parserHelper.getActionProcessor().schedule(pole);
		}//END Command 23 medium
		
		//Command 24 medium
		if(this.commandTextArray.get(1).equalsIgnoreCase("Power") && this.commandTextArray.get(2).equalsIgnoreCase("Station")) {
			java.util.List<java.lang.String> substations = new ArrayList<java.lang.String>();
			String stationID = this.commandTextArray.get(3);
			String coords = this.commandTextArray.get(5);
			String delta = this.commandTextArray.get(7);
			CoordinatesWorld worldCoord;
					
			if(coords.charAt(0) == '$') {
				worldCoord = parserHelper.getReference(coords.substring(1));
			}else{
				worldCoord = parseWorldCoordinates(coords);
			}
				CoordinatesDelta deltaCoord = parseDeltaCoordinates(delta);
					
			for(int i = 9; i < this.commandTextArray.size()-1; i++) {
				substations.add(this.commandTextArray.get(i));
			}
				CommandCreatePowerStation powerStation = new CommandCreatePowerStation(stationID, worldCoord, deltaCoord, substations);
				this.parserHelper.getActionProcessor().schedule(powerStation);
			}
		
		///////////////rule 25
		//CREATE POWER SUBSTATION id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA coordinates_delta WITH CATENARIES idn+
		//CommandCreatePowerSubstation
		//Creates power substation id1 at coordinates_delta meters from coordinates_world or id2 with catenaries idn.

		if((items[0] + " " + items[1]  + " " + items[2]).equalsIgnoreCase("create power station") && (items[4]).equalsIgnoreCase("reference") && 
		   (items[6]).equalsIgnoreCase("delta") && (items[8] + " " + items[9]).equalsIgnoreCase("with catenaries")){
     
	    String id1;
	    CoordinatesWorld reference;
	    CoordinatesDelta delta;
	    List<String> idCatenaries = new ArrayList<>();
	   
	    //items3 id1   	   
	    id1 = items[3];
	   
	    //items5 ( coordinates_world | ( '$' id2 ) )
	    if(items[5].contains("$")) {  
		    reference =  parserHelper.getReference(items[5].substring(1));//are we including the $ in the string, and is the $ attached?
	    }
	   
	    else {
		   String[] coor = items[5].split("/");
		   String lat = coor[0];
		   String lon = coor[1];
		   
		   
		   int findLatDegree = lat.indexOf("*");
		   int findLatMinutes = lat.indexOf("'");//need help with this
		   int findLatSeconds = lat.indexOf('"');//need help with this
		   int latDegrees = Integer.parseInt(lat.substring(0, findLatDegree));
		   int latMinutes = Integer.parseInt(lat.substring(findLatDegree + 1, findLatMinutes));
		   double latSeconds = Double.parseDouble(lat.substring(findLatMinutes + 1, findLatSeconds));
		   Latitude worldLat = new Latitude(latDegrees, latMinutes, latSeconds);
		   
		   
		   int findLonDegree = lon.indexOf("*");
		   int findLonMinutes = lon.indexOf("'");//need help with this
		   int findLonSeconds = lon.indexOf('"');//need help with this
		   int lonDegrees = Integer.parseInt(lon.substring(0, findLonDegree));
		   int lonMinutes = Integer.parseInt(lon.substring(findLonDegree + 1, findLonMinutes));
		   double lonSeconds = Double.parseDouble(lon.substring(findLonMinutes + 1, findLonSeconds));
		   Longitude worldLon = new Longitude(lonDegrees, lonMinutes, lonSeconds);
		   
		   reference = new CoordinatesWorld(worldLat, worldLon);
		   
	    }
	   
	    //items7 coordinates_delta
	       String[] coorDelta = items[7].split(":");
	       Double left = Double.parseDouble(coorDelta[0]);
	       Double right = Double.parseDouble(coorDelta[1]);
	       
	       delta = new CoordinatesDelta(left, right);
	       
	    //items10+ idn+
	    for(int i = 10; i < items.length; i++) {
		   String temp = items[i];
		   idCatenaries.add(temp);
	    }
	   
	    A_Command command = new CommandCreatePowerSubstation(id1, reference, delta, idCatenaries);
	    this.parserHelper.getActionProcessor().schedule(command);
 		}///////end rule25
		

		// Rule 28 CommandCreateStockCarBox
		if ((items[1] + " " + items[2] + " " + items[4] + " " + items[5]).equals("STOCK CAR AS BOX")) {
			A_Command command = new CommandCreateStockCarBox((String) items[3]);
			parserHelper.getActionProcessor().schedule(command);
		}
		
		//Command 29 easy
		if(this.commandTextArray.get(1).equalsIgnoreCase("Stock") && this.commandTextArray.get(5).equalsIgnoreCase("Box")) {
			String id = this.commandTextArray.get(3);
			CommandCreateStockCarBox box = new CommandCreateStockCarBox(id);
			this.parserHelper.getActionProcessor().schedule(box);
		}
		
		//Command 30 easy
		if(this.commandTextArray.get(1).equalsIgnoreCase("Stock") && this.commandTextArray.get(5).equalsIgnoreCase("Flatbed")) {
			String id = this.commandTextArray.get(3);
			CommandCreateStockCarFlatbed flatbed = new CommandCreateStockCarFlatbed(id);
			this.parserHelper.getActionProcessor().schedule(flatbed);
		}
		
		//Command 31 easy
		if(this.commandTextArray.get(1).equalsIgnoreCase("Stock") && this.commandTextArray.get(5).equalsIgnoreCase("Passenger")) {
			String id = this.commandTextArray.get(3);
			CommandCreateStockCarPassenger passenger = new CommandCreateStockCarPassenger(id);
			this.parserHelper.getActionProcessor().schedule(passenger);
		}
		
		//Command 32 easy
		if(this.commandTextArray.get(1).equalsIgnoreCase("Stock") && this.commandTextArray.get(5).equalsIgnoreCase("Tank")) {
			String id = this.commandTextArray.get(3);
			CommandCreateStockCarTank tank = new CommandCreateStockCarTank(id);
			this.parserHelper.getActionProcessor().schedule(tank);
		}
		
		//Command 33 easy
		if(this.commandTextArray.get(1).equalsIgnoreCase("Stock") && this.commandTextArray.get(5).equalsIgnoreCase("Tender")) {
			String id = this.commandTextArray.get(3);
			CommandCreateStockCarTender tender = new CommandCreateStockCarTender(id);
			this.parserHelper.getActionProcessor().schedule(tender);
		}
		
		//Command 34 medium
		if(this.commandTextArray.get(1).equalsIgnoreCase("Stock") && this.commandTextArray.get(2).equalsIgnoreCase("Engine") 
				&& this.commandTextArray.get(6).equalsIgnoreCase("On")) {
			boolean startOrEnd;
			String id = this.commandTextArray.get(3);
			
			if(this.commandTextArray.get(14).equalsIgnoreCase("START")) {
				startOrEnd = true;
			} else {
				startOrEnd = false;
			}
			
			String trackID = this.commandTextArray.get(8);
			double distance = Double.parseDouble(this.commandTextArray.get(10));
			TrackLocator locator;
			
			if(this.commandTextArray.get(10).equalsIgnoreCase("START")) {
				locator = new TrackLocator(trackID, distance, true);
			} else {
				locator = new TrackLocator(trackID, distance, false);
			}
			CommandCreateStockEngineDiesel engine = new CommandCreateStockEngineDiesel(id, locator, startOrEnd);
			this.parserHelper.getActionProcessor().schedule(engine);
		}// End Rule 34
		
		
		
		///////////////rule 39
		// CREATE TRACK BRIDGE DRAW id1 REFERENCE (coordinates_world | ('$'id2)) DELTA START coordinates_delta1 END coordinates_delta2 ANGLE angle
		//CommandCreateTrackBridgeDraw
		//Creates drawbridge track id1 starting at coordinates_delta1 meters and ending at coordinates_delta2 meters from coordinates_world or id2 with maximum elevation
		//angle.
		if((items[0] + " " + items[1]  + " " + items[2] + " " + items[3]).equalsIgnoreCase("create track bridge draw") &&
				items[5].equalsIgnoreCase("reference") && (items[7] + " " + items[8]).equalsIgnoreCase("delta start") && 
				items[10].equalsIgnoreCase("end") && items[12].equalsIgnoreCase("angle")) {
	   

			CoordinatesWorld reference1;
	   
			//items4 id1
			String id1 = items[4];
	   
			//items6 (coordinates_world | ('$'id2))
			if(items[6].contains("$")) {  
				reference1 = parserHelper.getReference(items[6].substring(1));
			}
	   
			else {
				String[] coor = items[6].split("/");
				String lat = coor[0];
				String lon = coor[1];
		   
		   
				int findLatDegree = lat.indexOf("*");
				int findLatMinutes = lat.indexOf("'");//need help with this
				int findLatSeconds = lat.indexOf('"');//need help with this
				int latDegrees = Integer.parseInt(lat.substring(0, findLatDegree));
				int latMinutes = Integer.parseInt(lat.substring(findLatDegree + 1, findLatMinutes));
				double latSeconds = Double.parseDouble(lat.substring(findLatMinutes + 1, findLatSeconds));
				Latitude worldLat = new Latitude(latDegrees, latMinutes, latSeconds);
		   
		   
				int findLonDegree = lon.indexOf("*");
				int findLonMinutes = lon.indexOf("'");//need help with this
				int findLonSeconds = lon.indexOf('"');//need help with this
				int lonDegrees = Integer.parseInt(lon.substring(0, findLonDegree));
				int lonMinutes = Integer.parseInt(lon.substring(findLonDegree + 1, findLonMinutes));
				double lonSeconds = Double.parseDouble(lon.substring(findLonMinutes + 1, findLonSeconds));
				Longitude worldLon = new Longitude(lonDegrees, lonMinutes, lonSeconds);
		   
				reference1 = new CoordinatesWorld(worldLat, worldLon);
			}   
			//items9 coordinates_delta1
	   
				String[] coorDelta1 = items[9].split(":");
				Double left1 = Double.parseDouble(coorDelta1[0]);
				Double right1 = Double.parseDouble(coorDelta1[1]);
	       
				CoordinatesDelta delta1 = new CoordinatesDelta(left1, right1);
	       
				//items11 coordinates_delta2
				String[] coorDelta2 = items[9].split(":");
				Double left2 = Double.parseDouble(coorDelta2[0]);
				Double right2 = Double.parseDouble(coorDelta2[1]);
	       
				CoordinatesDelta delta2 = new CoordinatesDelta(left2, right2);
	       
			//PointLocator locator
				PointLocator locator = new PointLocator(reference1, delta1, delta2);
	       
			//items13 angle
				double ang = Double.parseDouble(items[13]);
				Angle angle = new Angle(ang);
	   
				A_Command command = new CommandCreateTrackBridgeDraw(id1, locator, angle);
				this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 39
		
		
		
		///////////////rule 40
		//CREATE TRACK BRIDGE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
		//CommandCreateTrackBridgeFixed
		//Creates fixed bridge track id1 starting at coordinates_delta1 meters and ending at coordinates_delta2 meters from coordinates_world or id2.
		if((items[0] + " " + items[1]  + " " + items[2]).equalsIgnoreCase("create track bridge") && items[4].equalsIgnoreCase("reference")
				&& (items[6] + " " + items[7]).equalsIgnoreCase("delta start") && items[9].equalsIgnoreCase("end")) {
 		
			CoordinatesWorld reference1;
	   
			//item3 id1
			String id1 = items[3];
 		
			//items5 reference
			if(items[5].contains("$")) {  
				reference1 = parserHelper.getReference(items[5].substring(1));
			}
	   
			else {
				String[] coor = items[5].split("/");
				String lat = coor[0];
				String lon = coor[1];
		   
		   
				int findLatDegree = lat.indexOf("*");
				int findLatMinutes = lat.indexOf("'");//need help with this
				int findLatSeconds = lat.indexOf('"');//need help with this
				int latDegrees = Integer.parseInt(lat.substring(0, findLatDegree));
				int latMinutes = Integer.parseInt(lat.substring(findLatDegree + 1, findLatMinutes));
				double latSeconds = Double.parseDouble(lat.substring(findLatMinutes + 1, findLatSeconds));
				Latitude worldLat = new Latitude(latDegrees, latMinutes, latSeconds);
		   
		   
				int findLonDegree = lon.indexOf("*");
				int findLonMinutes = lon.indexOf("'");//need help with this
				int findLonSeconds = lon.indexOf('"');//need help with this
				int lonDegrees = Integer.parseInt(lon.substring(0, findLonDegree));
				int lonMinutes = Integer.parseInt(lon.substring(findLonDegree + 1, findLonMinutes));
				double lonSeconds = Double.parseDouble(lon.substring(findLonMinutes + 1, findLonSeconds));
				Longitude worldLon = new Longitude(lonDegrees, lonMinutes, lonSeconds);
		   
				reference1 = new CoordinatesWorld(worldLat, worldLon);
			}	
 		
			//items8 delta1
			String[] coorDelta1 = items[8].split(":");
			Double left1 = Double.parseDouble(coorDelta1[0]);
			Double right1 = Double.parseDouble(coorDelta1[1]);
      
			CoordinatesDelta delta1 = new CoordinatesDelta(left1, right1);	
 		
			//items 10 delta2
			String[] coorDelta2 = items[10].split(":");
			Double left2 = Double.parseDouble(coorDelta2[0]);
			Double right2 = Double.parseDouble(coorDelta2[1]);
     
			CoordinatesDelta delta2 = new CoordinatesDelta(left2, right2);	
 		
			//PointLocator locator
			PointLocator locator = new PointLocator(reference1, delta1, delta2);
 			
			A_Command command = new CommandCreateTrackBridgeFixed(id1, locator);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 40
		
		///////////////rule 41
		//CREATE TRACK CROSSING id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) DELTA START coordinates_delta1 END coordinates_delta2
		//CommandCreateTrackCrossing
		//Creates grade-crossing track id1 starting at coordinates_delta1 meters and ending at coordinates_delta2 meters from coordinates_world or id2.
		if((items[0] + " " + items[1]  + " " + items[2]).equalsIgnoreCase("create track crossing") && items[4].equalsIgnoreCase("reference") &&
				(items[6] + " " + items[7]).equalsIgnoreCase("delta start") && items[9].equalsIgnoreCase("end")) {
 
			CoordinatesWorld reference1;
			//item3 id1
			String id1 = items[3];
	   
			//item5 reference
			if(items[5].contains("$")) {  
				reference1 = parserHelper.getReference(items[5].substring(1));
			}
	   
			else {
				String[] coor = items[5].split("/");
				String lat = coor[0];
				String lon = coor[1];
		   
		   
				int findLatDegree = lat.indexOf("*");
				int findLatMinutes = lat.indexOf("'");//need help with this
				int findLatSeconds = lat.indexOf('"');//need help with this
				int latDegrees = Integer.parseInt(lat.substring(0, findLatDegree));
				int latMinutes = Integer.parseInt(lat.substring(findLatDegree + 1, findLatMinutes));
				double latSeconds = Double.parseDouble(lat.substring(findLatMinutes + 1, findLatSeconds));
				Latitude worldLat = new Latitude(latDegrees, latMinutes, latSeconds);
		   
		   
				int findLonDegree = lon.indexOf("*");
				int findLonMinutes = lon.indexOf("'");//need help with this
				int findLonSeconds = lon.indexOf('"');//need help with this
				int lonDegrees = Integer.parseInt(lon.substring(0, findLonDegree));
				int lonMinutes = Integer.parseInt(lon.substring(findLonDegree + 1, findLonMinutes));
				double lonSeconds = Double.parseDouble(lon.substring(findLonMinutes + 1, findLonSeconds));
				Longitude worldLon = new Longitude(lonDegrees, lonMinutes, lonSeconds);
		   
				reference1 = new CoordinatesWorld(worldLat, worldLon);
			}
			//item8 delta1
			String[] coorDelta1 = items[8].split(":");
			Double left1 = Double.parseDouble(coorDelta1[0]);
			Double right1 = Double.parseDouble(coorDelta1[1]);
     
			CoordinatesDelta delta1 = new CoordinatesDelta(left1, right1);
     
			//item10 delta2
			String[] coorDelta2 = items[10].split(":");
			Double left2 = Double.parseDouble(coorDelta2[0]);
			Double right2 = Double.parseDouble(coorDelta2[1]);
     
			CoordinatesDelta delta2 = new CoordinatesDelta(left2, right2);
     
			//PointLocator locator
			PointLocator locator = new PointLocator(reference1, delta1, delta2);
	   
	   
			A_Command command = new CommandCreateTrackCrossing(id1, locator);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 41
		
		//Command 42 hard
		if (this.commandTextArray.get(1).equalsIgnoreCase("Track") && this.commandTextArray.get(2).equalsIgnoreCase("Crossover")) {
			String id1 = this.commandTextArray.get(3);
		    CoordinatesWorld id2 = null;
            CoordinatesDelta coordinates_delta1 = null;
	        CoordinatesDelta coordinates_delta2 = null;
		    CoordinatesDelta coordinates_delta3 = null;
		    CoordinatesDelta coordinates_delta4 = null;

		    if(commandTextArray.get(5).contains("$")) {
		    	if((parserHelper.getReference(commandTextArray.get(5).substring(1)) != null)) {
		    		id2 = parserHelper.getReference(commandTextArray.get(5).substring(1));
		         }
		         else{
		        	 throw new NullPointerException("id does not exist");
		         }
		    }
		    else {
		    	id2 = parseWorldCoordinates(commandTextArray.get(5));
		    }
		    	coordinates_delta1 = parseDeltaCoordinates(commandTextArray.get(8));
		        coordinates_delta2 = parseDeltaCoordinates(commandTextArray.get(10));
		        coordinates_delta3 = parseDeltaCoordinates(commandTextArray.get(12));
		        coordinates_delta4 = parseDeltaCoordinates(commandTextArray.get(14));
		            
		        CommandCreateTrackCrossover trackCrossover = new CommandCreateTrackCrossover(id1, id2, coordinates_delta1, coordinates_delta2, coordinates_delta3, coordinates_delta4);
		        this.parserHelper.getActionProcessor().schedule(trackCrossover);
		}//end rule 42
		
		//command #43 hard
        if (this.commandTextArray.get(1).equalsIgnoreCase("TRACK") && this.commandTextArray.get(2).equalsIgnoreCase("CURVE")) {
            String id = this.commandTextArray.get(3);
            String coordText = this.commandTextArray.get(5);
            CoordinatesWorld coords;
            if (coordText.charAt(0) == '$')
                coords = parserHelper.getReference(coordText.substring(1));
            else
                coords = parseWorldCoordinates(coordText);
            CoordinatesDelta delta1 = parseDeltaCoordinates(this.commandTextArray.get(8));
            CoordinatesDelta delta2 = parseDeltaCoordinates(this.commandTextArray.get(10));
            A_Command createCurve;
            if (this.commandTextArray.size() == 13) {
                double ortho = Double.parseDouble(this.commandTextArray.get(13));
                createCurve = new CommandCreateTrackCurve(id, coords, delta1, delta2, ortho);
            }
            else {
                CoordinatesDelta ortho = parseDeltaCoordinates(this.commandTextArray.get(12));
                createCurve = new CommandCreateTrackCurve(id, coords, delta1, delta2, ortho);
            }
            this.parserHelper.getActionProcessor().schedule(createCurve);
        }//end rule 43

        //command 44
        if(this.commandTextArray.get(1).equalsIgnoreCase("Track") && 
                this.commandTextArray.get(2).equalsIgnoreCase("End")) {

            String trackID = this.commandTextArray.get(3);
            String coords = this.commandTextArray.get(5);
            String delta1 = this.commandTextArray.get(8);
            String delta2 = this.commandTextArray.get(10);
            CoordinatesWorld worldCoord;

            if (coords.charAt(0) == '$') {
                    worldCoord = parserHelper.getReference(coords.substring(1));
            }
            else {
                    worldCoord = parseWorldCoordinates(coords);
            }

            CoordinatesDelta deltaStart = parseDeltaCoordinates(delta1);
            CoordinatesDelta deltaEnd = parseDeltaCoordinates(delta2);
            PointLocator locator = new PointLocator(worldCoord, deltaStart, deltaEnd);

            CommandCreateTrackEnd end = new CommandCreateTrackEnd(trackID, locator);

            this.parserHelper.getActionProcessor().schedule(end);
        }//end rule 44
		
				
		///////////////rule 45
		//CREATE TRACK LAYOUT id1 WITH TRACKS idn+
		//CommandCreateTrackLayout
		//Creates track layout id1 with tracks idn.
		if((items[0] + " " + items[1] + " " + items[2]).equalsIgnoreCase("create track layout") && (items[4] + " " + items[5]).equalsIgnoreCase("with tracks")) {
	   
			List<String> trackIds = new ArrayList<>();
	   
			//item3 id1
			String id1 = items[3];
	   	
			//item6+ trackIds
			for(int i = 6; i < items.length; i++) {
				String temp = items[i];
				trackIds.add(temp);
			}
	   
			A_Command command = new CommandCreateTrackLayout(id1, trackIds);
			this.parserHelper.getActionProcessor().schedule(command);
		}//end rule 45
		
		  //command #46 hard
        if (this.commandTextArray.get(1).equalsIgnoreCase("TRACK") && this.commandTextArray.get(2).equalsIgnoreCase("ROUNDHOUSE")) {
            String id = this.commandTextArray.get(3);
            String coordText = this.commandTextArray.get(5);
            CoordinatesWorld coords;
            if (coordText.charAt(0) == '$')
                coords = parserHelper.getReference(coordText.substring(1));
            else
                coords = parseWorldCoordinates(coordText);
            CoordinatesDelta delta1 = parseDeltaCoordinates(this.commandTextArray.get(8));
            Angle angle1 = new Angle(Double.parseDouble(this.commandTextArray.get(11)));
            Angle angle2 = new Angle(Double.parseDouble(this.commandTextArray.get(13)));
            Angle angle3 = new Angle(Double.parseDouble(this.commandTextArray.get(15)));
            int integer = Integer.parseInt(this.commandTextArray.get(17));
            double num1 = Double.parseDouble(this.commandTextArray.get(20));
            double num2 = Double.parseDouble(this.commandTextArray.get(23));
            A_Command createRound = new CommandCreateTrackRoundhouse(id, coords, delta1, angle1, angle2, angle3, integer, num1, num2);
            this.parserHelper.getActionProcessor().schedule(createRound);
        }

		// Rule 47 CommandCreateTrackStraight
		if((items[1]+ " "+ items[2]+ " "+ items[4]).equalsIgnoreCase("TRACK STRAIGHT REFERENCE")){
			
            CoordinatesWorld world = null;
			
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[5].substring(1))) {
					world = parserHelper.getReference(items[5].substring(1));
					
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

		// rule 48 CommandCreateTrackSwitchTurnout
		if ((items[1] + " " + items[2] + " " + items[3]).equalsIgnoreCase("TRACK SWITCH TURNOUT")) {
			CoordinatesWorld world = null;		
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[6].substring(1))) {
					world = parserHelper.getReference(items[6].substring(1));
				}
			}
			else{
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
				CoordinatesDelta start1 = new CoordinatesDelta(Double.parseDouble(deltaStart[0]),Double.parseDouble(deltaStart[1]));
				CoordinatesDelta end1 = new CoordinatesDelta(Double.parseDouble(deltaEnd[0]),Double.parseDouble(deltaEnd[1]));
				CoordinatesDelta start2 = new CoordinatesDelta(Double.parseDouble(curveDeltaStart[0]),Double.parseDouble(curveDeltaStart[1]));
				CoordinatesDelta end2 = new CoordinatesDelta(Double.parseDouble(curveDeltaEnd[0]),Double.parseDouble(curveDeltaEnd[1]));
				CoordinatesDelta origin = new CoordinatesDelta(Double.parseDouble(deltaOrigin[0]),Double.parseDouble(deltaOrigin[1]));
				A_Command command = new CommandCreateTrackSwitchTurnout(items[4],world,start1,end1,start2,end2,origin);
				parserHelper.getActionProcessor().schedule(command);
		}
		
		
		// rule 49 CommandCreateTrackSwitchTurnout
		//CREATE TRACK SWITCH WYE id1 REFERENCE ( coordinates_world | ( '$' id2 ) ) [6]
		//DELTA START coordinates_delta1 END coordinates_delta2
		//DISTANCE ORIGIN number1 DELTA START coordinates_delta3 END coordinates_delta4 DISTANCE ORIGIN number2
		if ((items[1] + " " + items[2] + " " + items[3]).equals("TRACK SWITCH WYE")) {
			CoordinatesWorld world = null;		
			if(commandText.contains("$")) {
				if( parserHelper.hasReference(items[6].substring(1))) {
					world = parserHelper.getReference(items[6].substring(1));
				}
			}
			else{
				String[] coor = items[6].split("/");
	            String [] lat = coor[0].split("\\*|'");
	            String [] lon = coor[1].split("\\*|'|\"");
	            Latitude worldLat = new Latitude(Integer.parseInt(lat[0]),Integer.parseInt(lat[1]),Double.parseDouble(lat[2]));
				Longitude worldLon = new Longitude(Integer.parseInt(lon[0]),Integer.parseInt(lon[1]),Double.parseDouble(lon[2]));
				world = new CoordinatesWorld(worldLat,worldLon);
			} 
			String [] delta1Start = items[9].split(":");
			String [] delta1End = items[11].split(":");
			String [] deltaOrigin1Distance = items[14].split(":");
			String [] delta2Start = items[17].split(":");
			String [] delta2End = items[19].split(":");
			String [] deltaOrigin2Distance  = items[22].split(":");
			CoordinatesDelta start1 = new CoordinatesDelta(Double.parseDouble(delta1Start[0]),Double.parseDouble(delta1Start[1]));
			CoordinatesDelta end1 = new CoordinatesDelta(Double.parseDouble(delta1End[0]),Double.parseDouble(delta1End[1]));
			CoordinatesDelta origin1 = new CoordinatesDelta(Double.parseDouble(deltaOrigin1Distance[0]),Double.parseDouble(deltaOrigin1Distance[1]));
			CoordinatesDelta start2 = new CoordinatesDelta(Double.parseDouble(delta2Start[0]),Double.parseDouble(delta2Start[1]));
			CoordinatesDelta end2 = new CoordinatesDelta(Double.parseDouble(delta2End[0]),Double.parseDouble(delta2End[1]));
			CoordinatesDelta origin2 = new CoordinatesDelta(Double.parseDouble(deltaOrigin2Distance[0]),Double.parseDouble(deltaOrigin2Distance[1]));
			A_Command command = new CommandCreateTrackSwitchWye(items[4],world,start1,end1,origin1,start2,end2,origin2);
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
			
			A_Command command = new CommandBehavioralSetDirection((String)items[2],Boolean.parseBoolean(items[4]));
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

	private static ArrayList<String> commandToArray(String command, ArrayList<String> commandTextArray) {
		if(command.indexOf(" ") == -1) {
			commandTextArray.add(command);
			return commandTextArray;
		} else {
			int index = command.indexOf(" ");
			commandTextArray.add(command.substring(0, index));
			return commandToArray(command.substring(index + 1), commandTextArray);
		}
	}
	
	private CoordinatesDelta parseDeltaCoordinates(String coordinates) {
		ArrayList<Character> deltaCoordinates = new ArrayList<Character>();


        for(int i = 0; i < coordinates.length(); i++) {
            deltaCoordinates.add(i, coordinates.charAt(i));
        }
        ArrayList<Character> xCoord = new ArrayList<Character>();
        ArrayList<Character> yCoord = new ArrayList<Character>();
        int count = 0;
        while(!deltaCoordinates.get(count).equals(':')) {
            xCoord.add(count, deltaCoordinates.get(count));
            count++;
        }
        if(deltaCoordinates.get(count).equals(':')) {
            count++;
        }
        int tempCount = 0;
        for(int i = count; i < deltaCoordinates.size(); i++) {
            yCoord.add(tempCount, deltaCoordinates.get(i));
            tempCount++;
        }

        String x,y;

        x = removeUnwantedChars(xCoord);
        y = removeUnwantedChars(yCoord);

        double deltaX = Double.parseDouble(x);
        double deltaY = Double.parseDouble(y);

        return new CoordinatesDelta(deltaX,deltaY);
	}

	private CoordinatesWorld parseWorldCoordinates(String coordinates) {
		 ArrayList<Character> worldCoordinates = new ArrayList<Character>();
	        for(int i = 0; i < coordinates.length(); i++) {
	            worldCoordinates.add(i, coordinates.charAt(i));
	        }
	        ArrayList<Character> latitude = new ArrayList<Character>();
	        ArrayList<Character> longitude = new ArrayList<Character>();
	        int count = 0;
	        while(!worldCoordinates.get(count).equals('/')) {
	            latitude.add(worldCoordinates.get(count));
	            count++;
	        }
	        while(count < worldCoordinates.size()) {
	            if(worldCoordinates.get(count).equals('/')) {
	                count++;
	            }
	            longitude.add(worldCoordinates.get(count));
	            count++;
	        }

	        String latDegString, latMinString, latSecString, longDegString, longMinString, longSecString;

	        ArrayList<Character> latDegList = new ArrayList<Character>();
	        ArrayList<Character> latMinList = new ArrayList<Character>();
	        ArrayList<Character> latSecList = new ArrayList<Character>();
	        ArrayList<Character> longDegList = new ArrayList<Character>();
	        ArrayList<Character> longMinList = new ArrayList<Character>();
	        ArrayList<Character> longSecList = new ArrayList<Character>();
	        int counter = 0;
	        while(!latitude.get(counter).equals('*')) {
	            latDegList.add(counter, latitude.get(counter));
	            counter++;
	        }
	        if(latitude.get(counter).equals('*')) {
	            counter++;
	        }
	        int tempCount = 0;
	        while(!latitude.get(counter).equals('\'')) {
	            latMinList.add(tempCount, latitude.get(counter));
	            counter++;
	            tempCount++;
	        }
	        if(latitude.get(counter).equals('"')) {
	            counter++;
	        }
	        tempCount = 0;
	        while(!latitude.get(counter).equals(' ')) {
	            latSecList.add(tempCount, latitude.get(counter));
	            counter++;
	            tempCount++;
	        }

	        latDegString = removeUnwantedChars(latDegList);

	        latMinString = removeUnwantedChars(latMinList);

	        latSecString = removeUnwantedChars(latSecList);

	        int latDeg = Integer.parseInt(latDegString);
	        int latMin = Integer.parseInt(latMinString);
	        double latSec = Double.parseDouble(latSecString);

	        counter = 0;
	        while(!longitude.get(counter).equals('*')) {
	            longDegList.add(counter, longitude.get(counter));
	            counter++;
	        }
	        if(longitude.get(counter).equals('*')) {
	            counter++;
	        }
	        tempCount = 0;
	        while(!longitude.get(counter).equals('\'')) {
	            longMinList.add(tempCount, longitude.get(counter));
	            counter++;
	            tempCount++;
	        }
	        if(longitude.get(counter).equals('\'')) {
	            counter++;
	        }
	        tempCount = 0;
	        while(!longitude.get(counter).equals('"')) {
	            longSecList.add(tempCount, longitude.get(counter));
	            counter++;
	            tempCount++;
	        }
	        longDegString = removeUnwantedChars(longDegList);
	        longMinString = removeUnwantedChars(longMinList);
	        longSecString = removeUnwantedChars(longSecList);
	        int longDeg = Integer.parseInt(longDegString);
	        int longMin = Integer.parseInt(longMinString);
	        double longSec = Double.parseDouble(longSecString);

	        return new CoordinatesWorld(new Latitude(latDeg,latMin,latSec), new Longitude(longDeg,longMin,longSec));

	    }
	
	private String removeUnwantedChars(ArrayList<Character> list) {
        @SuppressWarnings("unused")
        String finalString;

        return finalString = list.toString().replace("[", "").replace("]", "").replace(",", "").replace(" ", "");

    }

	
}
