package com.demonwav.wat.bind;

import java.util.logging.Logger;

public final class NativeLogger {

    static {
        NativeLoader.init();
    }

    private NativeLogger() {}

    public static native void initLoggerInstance(final Logger logger);

    public static native void closeLoggerInstance();
}
