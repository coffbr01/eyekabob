/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.util;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class NiceNodeList {
	private NodeList nodeList;
	private Map<String, Node> map;

	public NiceNodeList(NodeList nodeList) {
		setNodeList(nodeList);
	}

	public void setNodeList(NodeList nodeList) {
		this.nodeList = nodeList;
	}

	public Map<String, Node> get(String... keys) {
		if (map == null) {
			map = new HashMap<String, Node>();
		}

		if (nodeList == null) {
			Log.e(this.getClass().getName(), "NodeList was null. You're probably initializing NiceNodeList incorrectly.");
			return map;
		}

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node aNode = nodeList.item(i);
			String aNodeName = aNode.getNodeName();
			for (int j = 0; j < keys.length; j++) {
				if (keys[j].equals(aNodeName)) {
					map.put(aNodeName, aNode);
				}
			}
		}

		return map;
	}
}
