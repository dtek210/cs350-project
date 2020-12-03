package cs350f20project;

import java.util.*;

import cs350f20project.controller.cli.TrackLocator;
import cs350f20project.controller.cli.parser.MyParserHelper;
import cs350f20project.controller.command.A_Command;
import cs350f20project.controller.command.PointLocator;
import cs350f20project.controller.command.creational.CommandCreatePowerPole;
import cs350f20project.controller.command.creational.CommandCreatePowerStation;
import cs350f20project.controller.command.creational.CommandCreateStockCarBox;
import cs350f20project.controller.command.creational.CommandCreateStockCarCaboose;
import cs350f20project.controller.command.creational.CommandCreateStockCarFlatbed;
import cs350f20project.controller.command.creational.CommandCreateStockCarPassenger;
import cs350f20project.controller.command.creational.CommandCreateStockCarTank;
import cs350f20project.controller.command.creational.CommandCreateStockCarTender;
import cs350f20project.controller.command.creational.CommandCreateStockEngineDiesel;
import cs350f20project.controller.command.creational.CommandCreateTrackCrossover;
import cs350f20project.controller.command.creational.CommandCreateTrackCurve;
import cs350f20project.controller.command.creational.CommandCreateTrackEnd;
import cs350f20project.controller.command.creational.CommandCreateTrackRoundhouse;
import cs350f20project.controller.command.meta.CommandMetaDoExit;
import cs350f20project.controller.command.meta.CommandMetaDoRun;
import cs350f20project.datatype.Angle;
import cs350f20project.datatype.CoordinatesDelta;
import cs350f20project.datatype.CoordinatesWorld;
import cs350f20project.datatype.Latitude;
import cs350f20project.datatype.Longitude;

import java.io.*;


public class CommandParser {
	MyParserHelper parserHelper;
	String commandText;
	private ArrayList<String> commandTextArray;
	public CommandParser(MyParserHelper parserHelper, String commandText) {
		this.parserHelper = parserHelper;
		this.commandText = commandText;
		commandTextArray = new ArrayList<String>();
		
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
	
	public void parse() {
		if(this.commandText.equalsIgnoreCase("@exit")) {
			A_Command command = new CommandMetaDoExit();
			this.parserHelper.getActionProcessor().schedule(command);
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
		}
		
		//Command 24 medium
		if(this.commandTextArray.get(1).equalsIgnoreCase("Power") && this.commandTextArray.get(2).equalsIgnoreCase("Station")) {
			java.util.List<java.lang.String> substations = new ArrayList<java.lang.String>();
			String stationID = this.commandTextArray.get(3);
			String coords = this.commandTextArray.get(5);
			String delta = this.commandTextArray.get(7);
			CoordinatesWorld worldCoord;
			
			if(coords.charAt(0) == '$') {
				worldCoord = parserHelper.getReference(coords.substring(1));
			} else {
				worldCoord = parseWorldCoordinates(coords);
			}
			CoordinatesDelta deltaCoord = parseDeltaCoordinates(delta);
			
			for(int i = 9; i < this.commandTextArray.size()-1; i++) {
				substations.add(this.commandTextArray.get(i));
			}
			
			CommandCreatePowerStation powerStation = new CommandCreatePowerStation(stationID, worldCoord, deltaCoord, substations);
			this.parserHelper.getActionProcessor().schedule(powerStation);
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
		}
		
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
                else {
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
        }
		
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
        }
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
        }
        
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
		
		//command #51
        else if (this.commandTextArray.get(0).equalsIgnoreCase("@Exit")) {
            A_Command exit = new CommandMetaDoExit();
            this.parserHelper.getActionProcessor().schedule(exit);
        }

        //command #52 
        else if (this.commandTextArray.get(0).equalsIgnoreCase("@run")) {
            String runString = this.commandTextArray.get(1);
            CommandMetaDoRun run = new CommandMetaDoRun(runString);
            this.parserHelper.getActionProcessor().schedule(run);
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
