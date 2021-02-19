/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Marshals information from files in the 'LookupTableContent' directory and handles transformation to relevant XML and CSV file(s).
 * The expected file format is defined within the LookupTableData class.
 *
 * @see LookupTableContent
 */
public class LookupTableDataHandler extends BasicEntityHandler {

	public LookupTableDataHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sOutputPath, String sEncoding) {

		super(LookupTableContent.getInstance());

		this.csvFilePath = "LookupTableContent" + File.separator;
		this.xmlFilePath = "LOOKUP_TABLE_CONTENT" + File.separator + "LOOKUP_TABLE_CONTENT_DATA.xml";
		this.version = sVersion;

		sInputFilePath = sInputFilePath + File.separator + csvFilePath;

		EnvironmentHandler.logger.info("Reading input from: " + sInputFilePath);

		File dirLkpData = new File(sInputFilePath);
		File[] aLkpDataFiles = dirLkpData.listFiles();
		
		if (aLkpDataFiles != null) {
			for (File fInputFile : aLkpDataFiles) {
				if (fInputFile.isFile()) {
					EnvironmentHandler.logger.info(" - File from: " + fInputFile.getName());
					try {
						// Read in the entities first...
						CSVParser readerCSV = new CSVParser(fInputFile, sEncoding);
						List<String> aTokens = readerCSV.splitLine();

						// Build the entity (attribute paths, table name, etc) from the first line only...
						LookupTableContent lkpData = entity.createInstance(aTokens);
						lkpData.setFileName(fInputFile.getName());
						addToCache(lkpData.getUniqueId(), lkpData);

						// Then start transforming all the lines into another file
						Writer outTransformedFile = BasicEntityHandler.getNewWriter(sOutputPath + lkpData.getTransformedFileName());
						aTokens = readerCSV.splitLine();

						// Issue doesn't work for multiple lookup
						if (aTokens != null && !aTokens.isEmpty())
							federated = true;
						while (aTokens != null && !aTokens.isEmpty()) {
							transformDataLine(lkpData, outTransformedFile, aTokens);
							aTokens = readerCSV.splitLine();
						}
						outTransformedFile.flush();
						outTransformedFile.close();

					} catch (FileNotFoundException errNoFile) {
						EnvironmentHandler.logger.severe("Error: File not found! " + errNoFile.getMessage());
						federated = false;
					} catch (IOException errIO) {
						EnvironmentHandler.logger.severe("Error: IO problem! " + errIO.getMessage());
					}
				}
			}
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		LookupTableContent lkpData = (LookupTableContent) oEntity;
		bValid = validateExists(lkpData.getTableName(), Lookup.class.getName()) && bValid;
		return bValid;
	}

	private void transformDataLine(LookupTableContent lkpData, Writer outFile, List<String> aDataValues) throws IOException {
		List<String> alOrderedAttributes = lkpData.getAttributePaths();
		for (int iOAs = 1; iOAs < alOrderedAttributes.size() + 1; iOAs++) {
			// We start at 1 and then reduce by 1 because the first column is the lookup table's name, not data
			String sAttrPath = alOrderedAttributes.get(iOAs - 1);
			String sDataValue = aDataValues.get(iOAs);
			if (!sDataValue.equals(""))
				outFile.write("," + BasicEntity.escapeForCSV(sAttrPath + "|" + sDataValue));
		}
		outFile.write("\n");
	}

}
