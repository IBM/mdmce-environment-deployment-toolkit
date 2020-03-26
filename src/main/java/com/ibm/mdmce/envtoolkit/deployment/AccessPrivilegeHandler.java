/*
Copyright IBM Corp. 2007-2020 All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
*/
package com.ibm.mdmce.envtoolkit.deployment;

import com.ibm.mdmce.envtoolkit.deployment.model.*;

import java.io.*;
import java.util.*;

/**
 * 
 * Marshals information from the AccessPrivs.csv and handles transformation to relevant XML(s).
 * The expected file format is defined within the AccessPrivilege class.
 *
 * @see AccessPriv
 */
public class AccessPrivilegeHandler extends BasicEntityHandler {
	
	public AccessPrivilegeHandler(String sInputFilePath, String sVersion, TemplateParameters tp, String sCmpCode, String sEncoding) {
		super(AccessPriv.getInstance(),
				"AccessPrivs.csv",
				"CONTAINER_ACCESSPRV" + File.separator + "CONTAINER_ACCESSPRV.xml",
				sInputFilePath,
				sVersion,
				tp,
				sEncoding);
		initialize(sInputFilePath, sEncoding, tp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected <T extends BasicEntity> void addLineToCache(List<List<String>> alReplacedTokens) {
		for (List<String> aReplacedTokens : alReplacedTokens) {
			AccessPriv instance = entity.createInstance(aReplacedTokens);
			for (String sRoleName : instance.getRoles()) {
				addToCache(instance.getUniqueIdFromRole(sRoleName), instance);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean validate(BasicEntity oEntity) {
		boolean bValid = true;
		AccessPriv ap = (AccessPriv) oEntity;
		String sContainerType = ap.getContainerType();
		if (sContainerType.equals("CATALOG")) {
			bValid = validateExists(ap.getContainerName(), Catalog.class.getName()) && bValid;
		} else {
			bValid = validateExists(ap.getContainerName(), Hierarchy.class.getName()) && bValid;
		}
		for (String sAttrCollectionName : ap.getAttributeCollectionToReadOnly().keySet()) {
			bValid = validateExists(sAttrCollectionName, AttrCollection.class.getName()) && bValid;
		}
		for (String role : ap.getRoles()) {
			bValid = (AccessPriv.ALL_ROLES.equals(role) || validateExists(role, Role.class.getName())) && bValid;
		}
		return bValid;
	}
	
}
