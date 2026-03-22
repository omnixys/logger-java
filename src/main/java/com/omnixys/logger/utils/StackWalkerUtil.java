package com.omnixys.logger.utils;

public final class StackWalkerUtil {

    private static final StackWalker WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private StackWalkerUtil() {}

    public static Caller resolve() {
        return WALKER.walk(stream ->
                stream.skip(3)
                        .findFirst()
                        .map(f -> new Caller(f.getClassName(), f.getMethodName()))
                        .orElse(new Caller("unknown", "unknown"))
        );
    }

    public record Caller(String clazz, String method) {}
}