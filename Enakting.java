/*
 * @ Tope Omitola 19 Oct 2009
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;



public class Enakting {

	public ArrayList<Hashtable<String, ArrayList<String>>> get(Workbook workbook, int sheetNum,int colsStart,
															   int colsEnd,int rowDataStart, int rowEnd, int rowValStart ) {

		Sheet sheet = workbook.getSheet(sheetNum);
		ArrayList<Hashtable<String, ArrayList<String>>> tableContents = new ArrayList<>();
		String cellContent;
		Hashtable<String, ArrayList<String>> eachColContents = null;
		for(int x = colsStart; x <= colsEnd; x++) { // COLUMNS WITH DATA
			Cell[] colCells = sheet.getColumn(x);
			eachColContents = new Hashtable<>();
			String colKey = colCells[rowDataStart].getContents(); // real data starts at column cell 2
			ArrayList<String> colValues = new ArrayList<String>();
			int colLengthOfInterest = rowEnd;
			for (int i = rowValStart; i < colLengthOfInterest; i++) { // column value data starts at column cell 5
				cellContent = colCells[i].getContents();
				if(cellContent.length() > 0) {
					colValues.add(cellContent);
				}
			}
			eachColContents.put(colKey, colValues);
			tableContents.add(eachColContents);
		}
		return tableContents;
	}

	public void printSchemaFile() {

		PrintWriter outputSchemaFile =  null;
		// output into  file
		try {

			outputSchemaFile =  new PrintWriter(new BufferedWriter(new FileWriter("BusinessImpactsOfCovid19Schema.ttl")));

			// output the schema first and some initial data
			// Martin Szomzor advised to correct these 15:00 27 Nov 2009 Friday
			//outputSchemaFile.println("@prefix : <http://enakting.ecs.soton.ac.uk/statistics/data/> .");
			outputSchemaFile.println("@prefix : <http://example.org/schema/business_impacts_covid19/> .");
			outputSchemaFile.println("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .");
			outputSchemaFile.println("@prefix dc: <http://purl.org/dc/elements/1.1/> .");
			outputSchemaFile.println("@prefix owl: <http://www.w3.org/2002/07/owl#> .");
			outputSchemaFile.println("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .");
			outputSchemaFile.println("@prefix sdmx-dimension: <http://purl.org/linked-data/sdmx/2009/dimension#> .");
			outputSchemaFile.println("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
			//outputSchemaFile.println("@prefix scovo: <http://purl.org/NET/scovo#> .");
			outputSchemaFile.println("@prefix qb: <http://purl.org/linked-data/cube#> .");
			//outputSchemaFile.println("@prefix statistics: <http://enakting.ecs.soton.ac.uk/statistics/> .");
			//outputSchemaFile.println("@base <http://enakting.ecs.soton.ac.uk/statistics/data/> . \n");

			outputSchemaFile.println("#Dimension - table \n");
			outputSchemaFile.println(":TimePeriod rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty . \n");

			outputSchemaFile.println("#Dimension - row");
			outputSchemaFile.println(":Industry rdfs:subClassOf qb:DimensionProperty;");
			outputSchemaFile.println(" \t dc:title \"Industry\". \n");

			outputSchemaFile.println("#Dimensions - columns");
			outputSchemaFile.println(":NumberOfSurveysSent rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty . \n");

			outputSchemaFile.println("#Dimensions - columns");
			outputSchemaFile.println(":NumberOfResponses rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty .\n");

			outputSchemaFile.println("#Dimensions - columns");
			outputSchemaFile.println(":ProportionOfResponses rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty .\n");

			outputSchemaFile.println("#Dimensions - columns");
			outputSchemaFile.println(":TradingStatus rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty .\n");

			outputSchemaFile.println("#Dimension - row");
			outputSchemaFile.println(":WorkforceSize rdfs:subClassOf qb:DimensionProperty;");
			outputSchemaFile.println(" \t dc:title \"WorkforceSize\". \n");

			outputSchemaFile.println("#Dimension - row");
			outputSchemaFile.println(":Country rdfs:subClassOf qb:DimensionProperty;");
			outputSchemaFile.println(" \t dc:title \"Country\". \n");

			outputSchemaFile.println("#Dimensions - columns");
			outputSchemaFile.println(":AppliedInitiatives rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty .\n");

			outputSchemaFile.println("#Dimensions - columns");
			outputSchemaFile.println(":ReceivedInitiatives rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty .\n");

			outputSchemaFile.println("#Dimensions - columns");
			outputSchemaFile.println(":IntendedToApplyInitiatives rdf:type owl:Class ;");
			outputSchemaFile.println("\t rdfs:subClassOf qb:DimensionProperty .\n");

		} catch(IOException ioex) {
			System.out.println(ioex);
		}

		outputSchemaFile.close();


	}

	public static void main(String[] args) {

		Workbook workbook = null;

		try {
			workbook = Workbook.getWorkbook(new File("BusinessImpactsOfCovid19Data.xls"));
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Enakting enakting = new Enakting();
		ArrayList<Hashtable<String, ArrayList<String>>> wkBookSheetContents = enakting.get(workbook,2,0,3,1,19,2);
		System.out.println(wkBookSheetContents);

		// set up the 18 DataSet objects
		DataSet[] allIndustries = new DataSet[18];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		Hashtable<String, ArrayList<String>> industries = wkBookSheetContents.get(0);
		Set<Map.Entry<String, ArrayList<String>>> industriesEntrySet = industries.entrySet();
		Iterator<Entry<String, ArrayList<String>>> iter = industriesEntrySet.iterator();
		while(iter.hasNext()) {
			 Map.Entry<String, ArrayList<String>> elem = iter.next();
			 ArrayList<String> values = elem.getValue();
			 for(int i = 0; i < values.size(); i++) {
				 allIndustries[i].setIndustryName(values.get(i));
			 }
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			 while(iter2.hasNext()) {
				 Map.Entry<String, ArrayList<String>> elem = iter2.next();
				 String itemName = elem.getKey();
				 ArrayList<String> values = elem.getValue();
				 for(int x = 0; x < values.size(); x++) {
					 String itemValue = values.get(x);
					 int cellNumber = x + 1;
					 DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					 allIndustries[x].addDataItem(dataItem);
				 }
			 }
		}

		enakting.printSchemaFile();
		PrintWriter outputDataFile = null;;
		try {

			outputDataFile = new PrintWriter(new BufferedWriter(new FileWriter("BusinessImpactsOfCovid19Data.ttl")));

			outputDataFile.println("@prefix : <http://business_impacts_covid19.example.org/data/> .");
			outputDataFile.println("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .");
			outputDataFile.println("@prefix dc: <http://purl.org/dc/elements/1.1/> .");
			outputDataFile.println("@prefix owl: <http://www.w3.org/2002/07/owl#> .");
			outputDataFile.println("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .");
			outputDataFile.println("@prefix sdmx-dimension: <http://purl.org/linked-data/sdmx/2009/dimension#> .");
			outputDataFile.println("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
			//outputDataFile.println("@prefix scovo: <http://purl.org/NET/scovo#> .");
			outputDataFile.println("@prefix qb: <http://purl.org/linked-data/cube#> .");
			outputDataFile.println("@prefix impact: <http://example.org/schema/business_impacts_covid19/> .");


			outputDataFile.println(":TP2020_04 rdf:type impact:TimePeriod;");
			outputDataFile.println("\t dc:title \"2020/04\" ;");
			outputDataFile.println("\t sdmx-dimension:refTime \"2020/04\"^^xsd:date .\n");

			// now print the rows
			for (int z = 0; z < allIndustries.length - 1; z++) {
				outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
				outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
			}

			// start preparing to print the columns
			// now print the columns
			for (int a = 0; a < allIndustries.length; a++) {
				ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
				for (int b = 0; b < dataItems.size(); b++) {
					DataItem dI = dataItems.get(b);
					outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :NumberOfSurveysSent;");
					outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
				}
			}

			outputDataFile.println("#Dataset");
			outputDataFile.println(":ds1 rdf:type qb:Dataset;");
			outputDataFile.println("\t dc:title \"Sample for the BICS survey broken down by industry, UK, 6 April to 19 April 2020\";");
			for (int d = 0; d < allIndustries.length; d++) {
				ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
				int e = 0;
				DataItem dI = null;
				for (; e < dataItems.size(); e++) {
					dI = dataItems.get(e);
					if ((d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false) {
						outputDataFile.println("\t qb:datasetOf :ds1_" + dI.getCellNumber() + ";");
					} else {
						// print if this is the last one in the series
						outputDataFile.println("\t qb:datasetOf :ds1_" + dI.getCellNumber());
					}
				}
			}
			outputDataFile.println("\t . \n");

			// the lines here output the DataSets and in them the DataItems
			for (int y = 0; y < allIndustries.length; y++) {
				outputDataFile.println(allIndustries[y].toString(1));
			}
		}catch(IOException ioex) {
			System.out.println(ioex);
		}
			//Table2------------------------------------------------------------------------------------------------------------
			wkBookSheetContents = enakting.get(workbook,2,0,4,22,24,23);
			System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
			allIndustries = new DataSet[1];
			for(int i = 0; i < allIndustries.length; i++) {
				allIndustries[i] = new DataSet();
			}
			// the lines here fill in data into the 18 DataSet objects
			industries = wkBookSheetContents.get(0);
			industriesEntrySet = industries.entrySet();
			iter = industriesEntrySet.iterator();

			while(iter.hasNext()) {

				Map.Entry<String, ArrayList<String>> elem = iter.next();
				ArrayList<String> values = elem.getValue();

				for(int i = 0; i < values.size(); i++) {
					allIndustries[i].setIndustryName(values.get(i));
				}
			}

			// the lines here set up the DataItems, which are subsets of DataSets
			for(int i = 1; i < wkBookSheetContents.size(); i++) {
				int rowNumber = i;
				Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
				Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
				Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
				while(iter2.hasNext()) {
					Map.Entry<String, ArrayList<String>> elem = iter2.next();
					String itemName = elem.getKey();
					ArrayList<String> values = elem.getValue();
					for(int x = 0; x < values.size(); x++) {
						String itemValue = values.get(x);
						int cellNumber = x + 1;
						DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
						allIndustries[x].addDataItem(dataItem);
					}
				}
			}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :NumberOfSurveysSent;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds2 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Sample for the BICS survey broken down by workforce size, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds2_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds2_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(2));
		}
		//Table3------------------------------------------------------------------------------------------------------------
			wkBookSheetContents = enakting.get(workbook,3,0,3,1,16,2);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[14];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :NumberOfResponses;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds3 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Number of all responding businesses, broken down by industry, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds3_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds3_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(3));
		}
		//Table4------------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,3,0,4,20,22,21);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[1];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :NumberOfResponses;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds4 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Number of all responding businesses, broken down by workforce size, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds4_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds4_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(4));
		}
		//Table5------------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,3,6,9,1,16, 2);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[14];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :ProportionOfResponses;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds5 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Proportion of all responding businesses, broken down by industry, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds5_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds5_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(5));
		}

		//Table6------------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,3,6,10,20,22, 21);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[1];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :ProportionOfResponses;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds6 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Proportion of all responding businesses, broken down by workforce size, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds6_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds6_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(6));
		}
		//Table7------------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,4,0,3,1,16,2);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[14];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :TradingStatus;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds7 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Trading Status of all responding businesses, broken down by industry, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds7_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds7_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(7));
		}
		//Table8------------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,4,0,3,19,23,20);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[3];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:WorkforceSize;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :TradingStatus;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds8 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Trading Status of all responding businesses, broken down by workforce size, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds8_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds8_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(8));
		}
		//Table9------------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,4,0,3,26,32,27);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[5];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Country;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :TradingStatus;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds9 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Trading Status of all responding businesses, broken down by country, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds9_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds9_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(9));
		}

		//Table10------------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,5,0,7,1,16,2);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[14];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :AppliedInitiatives;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds10 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Applied Initiatives of all responding businesses, broken down by industry, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds10_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds10_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(10));
		}

		//Table11-----------------------------------------------------------------------------------------------------------

		wkBookSheetContents = enakting.get(workbook,5,0,7,19,23,20);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[3];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:WorkforceSize;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :AppliedInitiatives;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds11 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Applied Initiatives of all responding businesses, broken down by workforce size, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds11_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds11_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(11));
		}

		//Table12-----------------------------------------------------------------------------------------------------------

		wkBookSheetContents = enakting.get(workbook,5,0,7,26,32,27);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[5];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Country;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :AppliedInitiatives;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds12 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Applied Initiatives of all responding businesses, broken down by country, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds12_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds12_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(12));
		}

		//Table13-----------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,6,0,7,1,16,2);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[14];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :ReceivedInitiatives;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds13 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Received Initiatives of all responding businesses, broken down by industry, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds13_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds13_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(13));
		}


		//Table14-----------------------------------------------------------------------------------------------------------

		wkBookSheetContents = enakting.get(workbook,6,0,7,19,23,20);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[3];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:WorkforceSize;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :ReceivedInitiatives;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds14 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Received Initiatives of all responding businesses, broken down by workforce size, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds14_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds14_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(14));
		}
		//Table15-----------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,7,0,7,1,16,2);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[14];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:Industry;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :IntendedToApplyInitiatives;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds15 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Intended to apply Initiatives of all responding businesses, broken down by industry, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds15_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds15_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(15));
		}

		//Table16-----------------------------------------------------------------------------------------------------------
		wkBookSheetContents = enakting.get(workbook,7,0,7,19,23,20);
		System.out.println(wkBookSheetContents);


		// set up the 2 DataSet objects
		allIndustries = new DataSet[3];
		for(int i = 0; i < allIndustries.length; i++) {
			allIndustries[i] = new DataSet();
		}
		// the lines here fill in data into the 18 DataSet objects
		industries = wkBookSheetContents.get(0);
		industriesEntrySet = industries.entrySet();
		iter = industriesEntrySet.iterator();

		while(iter.hasNext()) {

			Map.Entry<String, ArrayList<String>> elem = iter.next();
			ArrayList<String> values = elem.getValue();

			for(int i = 0; i < values.size(); i++) {
				allIndustries[i].setIndustryName(values.get(i));
			}
		}

		// the lines here set up the DataItems, which are subsets of DataSets
		for(int i = 1; i < wkBookSheetContents.size(); i++) {
			int rowNumber = i;
			Hashtable<String, ArrayList<String>> tempColContent = wkBookSheetContents.get(i);
			Set<Map.Entry<String, ArrayList<String>>> tempColContentSet = tempColContent.entrySet();
			Iterator<Entry<String, ArrayList<String>>> iter2 = tempColContentSet.iterator();
			while(iter2.hasNext()) {
				Map.Entry<String, ArrayList<String>> elem = iter2.next();
				String itemName = elem.getKey();
				ArrayList<String> values = elem.getValue();
				for(int x = 0; x < values.size(); x++) {
					String itemValue = values.get(x);
					int cellNumber = x + 1;
					DataItem dataItem = new DataItem(itemName, itemValue,  (cellNumber +  "_" + rowNumber)); // this cell number is a quick fix. Come back to fix properly. At present, I invert to get the right cell number.
					allIndustries[x].addDataItem(dataItem);
				}
			}
		}

		// now print the rows
		for(int z = 0; z < allIndustries.length; z++) {
			outputDataFile.println(":" + allIndustries[z].getIndustryNameLabel() + " rdf:type impact:WorkforceSize;");
			outputDataFile.println("\t dc:title \"" + allIndustries[z].getIndustryName() + "\"." + "\n");
		}

		// now print the columns
		for(int a = 0; a < allIndustries.length; a++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[a].getDataItems();
			for(int b = 0; b < dataItems.size(); b++) {
				DataItem dI = dataItems.get(b);
				outputDataFile.println(":" + dI.getItemLabel() + " rdf:type :IntendedToApplyInitiatives;");
				outputDataFile.println("\t dc:title " + "\"" + dI.getItemTitle() + "\"." + "\n");
			}
		}

		outputDataFile.println("#Dataset");
		outputDataFile.println(":ds16 rdf:type qb:Dataset;");
		outputDataFile.println("\t dc:title \"Intended to apply Initiatives of all responding businesses, broken down by workforce size, UK, 6 April to 19 April 2020\";");
		for(int d = 0; d < allIndustries.length; d++) {
			ArrayList<DataItem> dataItems = (ArrayList<DataItem>) allIndustries[d].getDataItems();
			int e = 0;
			DataItem dI = null;
			for( ; e < dataItems.size(); e++) {
				dI = dataItems.get(e);
				if( (d == (allIndustries.length - 1) && e == (dataItems.size() - 1)) == false ) {
					outputDataFile.println("\t qb:datasetOf :ds16_" + dI.getCellNumber() + ";");
				} else {
					// print if this is the last one in the series
					outputDataFile.println("\t qb:datasetOf :ds16_" + dI.getCellNumber());
				}
			}
		}
		outputDataFile.println("\t . \n");

		// the lines here output the DataSets and in them the DataItems
		for(int y = 0; y < allIndustries.length; y++) {
			outputDataFile.println(allIndustries[y].toString(16));
		}
		outputDataFile.close();
	}
}


