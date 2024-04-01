package me.udnek.itemscoreu.nms;

import me.udnek.itemscoreu.nms.versions.v1_20_R1;

public class NMSHelper {
    private static NMSHandler nmsHandler;

    public static NMSHandler getNMS(){
        return nmsHandler;
    }

    public static void updateNMS(){
        nmsHandler = new v1_20_R1();
    }

}
