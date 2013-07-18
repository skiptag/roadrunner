package de.skiptag.roadrunner.persistence.inmemory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Sets;

import de.skiptag.roadrunner.persistence.Path;

public class Node extends JSONObject {
    public Object getObjectForPath(Path path) {
	try {
	    Object node;
	    if (has(path.getFirstElement())) {
		Object object = get(path.getFirstElement());
		node = object;
	    } else {
		node = new Node();
		put(path.getFirstElement(), node);
	    }

	    if (path.isSimple()) {
		return node;
	    } else {
		if (node instanceof Node) {
		    return ((Node) node).getObjectForPath(path.getSubpath(1));
		}
	    }
	} catch (JSONException exp) {

	}
	return null;
    }

    public Node getNodeForPath(Path path) throws JSONException {
	Node node;
	if (has(path.getFirstElement())) {
	    Object object = get(path.getFirstElement());
	    node = (Node) object;
	} else {
	    node = new Node();
	    put(path.getFirstElement(), node);
	}

	if (path.isSimple()) {
	    return node;
	} else {
	    return node.getNodeForPath(path.getSubpath(1));
	}
    }

    public void populate(JSONObject payload) throws JSONException {
	Iterator<?> itr = payload.sortedKeys();
	while (itr.hasNext()) {
	    Object key = (Object) itr.next();
	    put((String) key, payload.get((String) key));
	}
    }

    public void populate(String obj) throws JSONException {
	populate(new JSONObject(obj));
    }

    public Collection<Node> getChildren() throws JSONException {
	Set<Node> nodes = Sets.newHashSet();
	Iterator<?> itr = keys();
	while (itr.hasNext()) {
	    Object key = itr.next();
	    if (get((String) key) instanceof Node) {
		nodes.add((Node) get((String) key));
	    }
	}
	return nodes;
    }

    public boolean hasChildren() throws JSONException {
	return !getChildren().isEmpty();
    }

    public boolean pathExists(Path path) throws JSONException {
	if (has(path.getFirstElement())) {
	    Object object = get(path.getFirstElement());
	    if (object instanceof Node) {
		Node node = (Node) object;
		return node.pathExists(path.getSubpath(1));
	    } else if (path.isSimple()) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }
}
