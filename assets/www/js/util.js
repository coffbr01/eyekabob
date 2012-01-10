com = {};
com.eyekabob = {};
com.eyekabob.util = {};
/**
 * Converts xml DOM to json.
 * Note: no attributes are converted.
 * Result will contain only nodeNames and node.data where applicable.
 * Also skips all whitespace.
 */
com.eyekabob.util.xmlToJson = function(xml, json) {
    if (json === undefined) {
        // Should only happen on the first call.
        json = {};
    }

    if (typeof(xml) === "string") {
        // Typically you want to pass xml that has already been parsed.
        // From an xmlhttpresponse, use xmlhttpresponse.responseXML instead
        // of responseText to skip this branch.
        xml = (new DOMParser()).parseFromString(xml, "text/xml");
    }

    var i = 0;
    for (; i < xml.childNodes.length; i++) {
        var childNode = xml.childNodes[i];
        if (childNode.data !== undefined) {
            if (childNode.data.trim() !== "") {
                json.data = childNode.data;
            }
            continue;
        }

        var dataStructure = {};
        if (json[childNode.nodeName] === undefined) {
            // Node has not been seen before. Pass empty object as json.
            json[childNode.nodeName] = dataStructure;
        }
        else if (json[childNode.nodeName].length === undefined) {
            // Node has been seen once before. Convert to array.
            var old = json[childNode.nodeName];
            json[childNode.nodeName] = [];
            json[childNode.nodeName].push(old);
            json[childNode.nodeName].push(dataStructure);
        }
        else {
            // Node has been seen at least twice now. Just push new object to array.
            json[childNode.nodeName].push(dataStructure);
        }

        // Mutate json recursively.
        com.eyekabob.util.xmlToJson(childNode, dataStructure);
    }

    // All mutations finished, return result.
    return json;
};

