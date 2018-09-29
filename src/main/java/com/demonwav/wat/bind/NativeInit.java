package com.demonwav.wat.bind;

public final class NativeInit {

    static {
        NativeLoader.init();
    }

    private NativeInit() {}

    public static native void init(String[] plugins);

    public static native void teardown();
}
