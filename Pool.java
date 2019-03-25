package com.springboot.Pool;

import com.mysql.jdbc.Driver;

import java.lang.InterruptedException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Pool implements PoolInterface {

    /**
     * 初始化连接池大小
     * */
    private static final int count = 20;

    //采用线程安全的链表队列
    private BlockingQueue<Connection> blockingQueue = new LinkedBlockingDeque<>();

    private static Pool pool = new Pool();

    private static boolean falg = true;
    private Pool(){
        try {
            //加载驱动
            Class.forName(PoolInterface.driver);
            for (int i = 0; i < count; i++)
                this.blockingQueue.offer(DriverManager.getConnection(PoolInterface.url,PoolInterface.username,PoolInterface.password));//入队
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Pool getPool(){
        if (falg){
            return pool;
        }
        throw new RuntimeException("被单例攻击");
    }

    @Override
    public Connection getConnection(){
        final Connection connection = blockingQueue.poll();//拿去一个元素,同时队列删除该元素
        if (connection==null){
            throw new RuntimeException("连接超时");
        }
        ClassLoader loader = connection.getClass().getClassLoader();
        //创建一个动态代理
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object object = null;
                //判断是否是close方法,是的话归还队列
                if (method.getName().equals("close")) {
                    object = method.invoke(connection,args);
                    blockingQueue.offer(connection);
                }else{
                    object = method.invoke(connection,args);
                }
                return object;
            }
        };
        //创建一个代理对象,对代理类方法进行结束后增强
        return (Connection) Proxy.newProxyInstance(loader,connection.getClass().getInterfaces(),handler);
    }

}
class test{
    public static void main(String[] args){
        Pool pool = Pool.getPool();
        List<Connection> list = new ArrayList<>();
        for (int i = 0; i < 21; i++){
            list.add(pool.getConnection());
        }
    }
}
