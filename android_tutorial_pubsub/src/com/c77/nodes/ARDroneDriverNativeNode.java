package com.c77.nodes;

import org.ros.android.android_tutorial_pubsub.NativeNodeMain;

/**
 * Created by ayi on 30/06/14.
 */
public class ARDroneDriverNativeNode extends NativeNodeMain {
    public ARDroneDriverNativeNode() {
        super("ardrone_driver_jni");
    }

    @Override
    protected native void execute(String rosMasterUri, String namespace, String rosHostname, String nodeName, String[] remappingArguments);

    @Override
    protected native void shutdown();

}
