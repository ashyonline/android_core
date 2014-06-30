package org.ros.android.android_tutorial_pubsub;

import android.util.Log;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.yaml.snakeyaml.Yaml;

/**
 * @author jcerruti@creativa77.com (Julian Cerruti)
 */
public abstract class NativeNodeMain extends AbstractNodeMain {

    private final String libraryName;
    private final String[] remappings;

    public NativeNodeMain(String libraryName) {
        this(libraryName, null);
    }

    public NativeNodeMain(String libraryName, InputStream remappingYmlInputStream) {
        this.libraryName = libraryName;
        if(remappingYmlInputStream != null) {
            Map<String, String> remappingMap = (Map<String,String>)(new Yaml()).load(remappingYmlInputStream);
            List<String> remappingsList = new ArrayList<String>();
            for(Map.Entry<String, String> e: remappingMap.entrySet()) {
                remappingsList.add(e.getKey()+":="+e.getValue());
            }
            remappings = remappingsList.toArray(new String[remappingMap.size()]);
        } else {
            remappings = new String[0];
        }
        System.loadLibrary(this.libraryName);
    }

    @Override
    public GraphName getDefaultNodeName() {
        Random rn = new Random();
        int rand = 10000000 + (int)(Math.random() * ((99999999 - 10000000) + 1));
        String randString = Integer.toString(rand);
        return GraphName.of("jni_library_loader_" + randString);
    }

    protected abstract void execute(String rosMasterUri, String namespace, String rosHostname, String nodeName, String[] remappingArguments);
    protected abstract void shutdown();

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Log.d(libraryName, "onStart (enter)");

        // Get actual info from node
        final String masterUri = connectedNode.getMasterUri().toString();
        final String hostname = connectedNode.getUri().getHost();
        // NOTE: Remove trailing /
        final String nodeName = this.libraryName; //connectedNode.getName().toString().substring(1);
        // TODO: Also pass along namespace (?)

        new Thread() {
            @Override
            public void run() {
                Log.d(libraryName, "about to execute");
                execute(masterUri, "", hostname, nodeName, remappings);
                Log.d(libraryName, "after execute");
            }
        }.start();
        Log.d(libraryName, "onStart (exit)");

        // TODO: Perform proper cleanup (node stays registered in master after this)
    }

    @Override
    public void onShutdown(Node node) {
        shutdown();
    }
}
