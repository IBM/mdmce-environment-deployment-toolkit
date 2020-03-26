/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.BasicEntity;
import com.ibm.mdmce.envtoolkit.deployment.model.ContainerContent;
import com.ibm.mdmce.envtoolkit.deployment.model.TemplateParameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Marshals information from the CatalogContents.csv and HierarchyContents.csv and handles transformation to relevant XML and CSV file(s).
 * The expected file format is defined within the ContainerContent class.
 *
 * @see ContainerContent
 */
public abstract class ContainerContentHandler extends BasicEntityHandler {

	private String relativePath;
	private String outputPath;

	/**
	 * Construct a new handler for the provided parameters.
	 * @param entity the entity for which to construct a handler
	 * @param csvFilePath the path to the CSV file that this handler processes
	 * @param xmlFilePath the path to the XML file that this handler processes
	 * @param sInputFilePath the location of the CSV file to translate
	 * @param version the version of the software for which to translate
	 * @param tp the template parameters to apply to the contents of the CSV file
	 * @param sOutputPath the directory into which to transfer the content files
	 * @param sEncoding the encoding of the CSV file
	 * @param <T> the type of entity for which to construct a handler
	 */
	public <T extends BasicEntity> ContainerContentHandler(T entity,
														   String csvFilePath,
														   String xmlFilePath,
														   String sInputFilePath,
														   String version,
														   TemplateParameters tp,
														   String sOutputPath,
														   String sEncoding) {
		super(entity, csvFilePath, xmlFilePath, sInputFilePath, version, tp, sEncoding);
		relativePath = sInputFilePath;
		outputPath = sOutputPath;
		initialize(sInputFilePath, sEncoding, tp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected <T extends BasicEntity> void addLineToCache(List<List<String>> alReplacedTokens) {
		try {
			for (List<String> aReplacedTokens : alReplacedTokens) {
				ContainerContent instance = entity.createInstance(aReplacedTokens);
				File fDataFile = new File(relativePath + File.separator + instance.getDataFilePath().replace("/", File.separator));
				if (fDataFile.isFile()) {
					FileChannel srcChannel = new FileInputStream(fDataFile.getAbsolutePath()).getChannel();
					String sOutputFileName = instance.getDataFilePath().substring(instance.getDataFilePath().lastIndexOf("/") + 1);
					instance.setFilename(sOutputFileName);
					String sOutputFilePath = outputPath + File.separator + sOutputFileName;
					File fDirs = new File(sOutputFilePath.substring(0, sOutputFilePath.lastIndexOf(File.separator)) + File.separator);
					fDirs.mkdirs();
					File fDst = new File(sOutputFilePath);
					fDst.createNewFile();
					FileChannel dstChannel = new FileOutputStream(fDst).getChannel();
					dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
					srcChannel.close();
					dstChannel.close();
				} else {
					err.println(". . . WARNING: Data file - " + relativePath + File.separator + instance.getDataFilePath() + " - not found.");
				}
				addToCacheWithType(instance.getUniqueId(), ContainerContent.class.getName(), instance);
			}
		} catch (FileNotFoundException errNoFile) {
			err.println("Error: File not found! " + errNoFile.getMessage());
		} catch (IOException errIO) {
			err.println("Error: IO problem! " + errIO.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		ContainerContent containerData = (ContainerContent) oEntity;
		bValid = validateExists(containerData.getContainerName(), ContainerContent.class.getName()) && bValid;
		return bValid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validateEntities(String sClassName) {
		return super.validateEntities(ContainerContent.class.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void outputEntities(String sCompanyCode, String sClassName, Writer outFile, String sOutputFilePath, String sOutputType) throws IOException {
		super.outputEntities(sCompanyCode, ContainerContent.class.getName(), outFile, sOutputFilePath, sOutputType);
	}

}
