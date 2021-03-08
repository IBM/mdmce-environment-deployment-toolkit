/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Marshals information from the Scripts.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the Document class.
 *
 * @see Script
 */
public class DocumentHandler extends BasicEntityHandler {

	private String inputDirectory;

	public DocumentHandler(String sInputFilePath, String sDocumentationPath, String sVersion, TemplateParameters tp, String sEncoding) {

		super(Script.getInstance(),
				"Scripts.csv",
				"DOCSTORE" + File.separator + "DOCSTORE.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);

		inputDirectory = sInputFilePath.substring(0, sInputFilePath.lastIndexOf(File.separator));
		
		try {
			if (!sDocumentationPath.equals("")) {
				List<Script> alDocumentation = new ArrayList<>();
				getDocumentsForAllFilesInDir(new File(sDocumentationPath), alDocumentation);
				for (Script doc : alDocumentation) {
					addToCache(doc.getPathRemote(), doc);
				}
			}
			// Create a default empty parameter set if it doesn't already exist...
			EnvironmentHandler.logger.info(" . . . Attempting to create default parameters file: " + inputDirectory + File.separator + "params" + File.separator + "None");
			File fileParamsPath = new File(inputDirectory + File.separator + "params");
			fileParamsPath.mkdirs();
			File fileDefaultParams = new File(inputDirectory + File.separator + "params" + File.separator + "None");
			EnvironmentHandler.logger.info(" . . .   --> Successful? " + fileDefaultParams.createNewFile());
		} catch (FileNotFoundException errNoFile) {
			EnvironmentHandler.logger.severe("Error: File not found! " + errNoFile.getMessage());
		} catch (IOException errIO) {
			EnvironmentHandler.logger.severe("Error: IO problem! " + errIO.getMessage());
		}
		
	}

	/**
	 * Recursively traverse through the provided directory (and its subdirectories) to find all documentation, and add
	 * each document to the provided list.
	 * @param directory the directory to traverse for documentation
	 * @param alDocs the list of documents to which to append
	 */
	private void getDocumentsForAllFilesInDir(File directory, List<Script> alDocs) {
		File[] filesAndDirs = directory.listFiles();
		if (filesAndDirs == null) {
			EnvironmentHandler.logger.warning("WARNING: Specified documentation directory not found: " + directory.getPath());
		} else {
			for (File file : filesAndDirs) {
				if (!file.getName().equals(".git")) {
					if (file.isDirectory()) {
						getDocumentsForAllFilesInDir(file, alDocs);
					} else {
						String sDocPath = file.getPath();
						sDocPath = sDocPath.substring(sDocPath.indexOf("documentation")).replace(File.separatorChar, '/');
						Script doc = new Script(sDocPath);
						alDocs.add(doc);
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
		
		Script doc = (Script) oEntity;
		if (!doc.getFileDestSpec().equals(""))
			bValid = validateExists(doc.getFileDestSpec(), Spec.class.getName(), doc.getName()) && bValid;
		if (!doc.getInputSpec().equals(""))
			bValid = validateExists(doc.getInputSpec(), Spec.class.getName(), doc.getName()) && bValid;
		
		if (!doc.getContainerName().equals("$ALL")) {
			if (!doc.getContainerType().equals(""))
				bValid = validateExists(doc.getContainerName(), (doc.getContainerType().equals("CATALOG") ? Catalog.class.getName() : Hierarchy.class.getName()), doc.getName() ) && bValid;
			else if (doc.getType().equals("CTG"))
				bValid = validateExists(doc.getContainerName(), Catalog.class.getName(), doc.getName()) && bValid;
			else if (doc.getType().equals("CTR"))
				bValid = validateExists(doc.getContainerName(), Hierarchy.class.getName(), doc.getName()) && bValid;
		}
		
		File fileDoc = new File(inputDirectory + doc.getPathLocal());
		if (!fileDoc.exists()) {
			EnvironmentHandler.logger.warning("WARNING (" + doc.getName() + "): Could not find specified document - " + inputDirectory + doc.getPathLocal());
			bValid = false;
		}
		
		return bValid;
		
	}

	/**
	 * Retrieve the input directory used for the documents.
	 * @return String
	 */
	public String getInputDirectory() {
		return inputDirectory;
	}

}
