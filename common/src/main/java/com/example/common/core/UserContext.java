package com.example.common.core;

// 线程级当前用户上下文
public class UserContext {
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    public static void set(Long userId){
        USER_ID_HOLDER.set(userId);
    }

    public static Long get(){
        return USER_ID_HOLDER.get();
    }

    public static void clear(){
        USER_ID_HOLDER.remove();
    }
}
