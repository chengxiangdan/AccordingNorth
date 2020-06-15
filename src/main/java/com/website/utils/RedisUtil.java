package com.website.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisUtil {
    private static JedisPool jedisPool=null;
    static {
        String ip="120.27.62.139";
        Integer port=6379;
        String password="";
        try {
            ip=PropertiesUtil.readByKey("jedis.ip");
            port=Integer.parseInt(PropertiesUtil.readByKey("jedis.port"));
            password=PropertiesUtil.readByKey("jedis.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxIdle(256);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setMinEvictableIdleTimeMillis(60000L);
        config.setTimeBetweenEvictionRunsMillis(3000L);
        config.setNumTestsPerEvictionRun(-1);
        if(password==null||password.equals("")) {
            jedisPool=new JedisPool(config,ip,port,60000);
        }else {
            jedisPool=new JedisPool(config,ip,port,60000,password);
        }
    }

    public static void main(String[] args) {
        /*Jedis jedis = new Jedis("120.27.62.139",6379);
        System.out.println("连接成功");*/

        //存储数据到列表中
       String name="name";
       String value="程";
      /* jedis.set(name,value);
        String s = jedis.get(name);
        System.out.println(s);*/

        RedisUtil.set(name.getBytes(),value.getBytes());
        byte[] bytes = RedisUtil.get(name.getBytes());
        String val=new String(bytes);
        System.out.println(val);

    }

    /**
     * 获取队列数据
     * @param  key 键名
     * @return
     */
    public static byte[] rpop(byte[] key) {
        byte[] bytes = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            bytes = jedis.rpop(key);
        } finally {
            jedis.close();
        }
        return bytes;
    }
    /**
     * 存储REDIS队列 顺序存储
     * @param  key reids键名
     * @param  value 键值
     */
    public static void lpush(byte[] key, byte[] value) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.lpush(key, value);
        } finally {
            jedis.close();
        }
    }

    /**
     * @ 获取数据
     * @param key
     * @return
     */
    public static String get(String key){
        String value=null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            value=jedis.get(key);
        }finally {
            jedis.close();
        }
        return value;
    }
    /**
     * 	获取数据
     *	@param key
     *	@return
     */
    public static byte[] get(byte[] key){
        byte[] value = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            value = jedis.get(key);
        } finally {
            jedis.close();
        }
        return value;
    }
    public static void set(byte[] key, byte[] value) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.set(key, value);
        } finally {
            jedis.close();
        }
    }

    public static void set(byte[] key, byte[] value, int time) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.set(key, value);
            jedis.expire(key, time);
        } finally {
            jedis.close();
        }
    }

    public static void hset(byte[] key, byte[] field, byte[] value) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.hset(key, field, value);
        } finally {
            jedis.close();
        }
    }

    public static void hset(String key, String field, String value) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.hset(key, field, value);
        } finally {
            jedis.close();
        }
    }

    /**
     * 	获取数据
     *
     * @param key
     * @return
     */
    public static String hget(String key, String field) {
        String value = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            value = jedis.hget(key, field);
        } finally {
            jedis.close();
        }
        return value;
    }
    /**
     * 	获取数据
     * @param key
     * @return
     */
    public static byte[] hget(byte[] key, byte[] field) {
        byte[] value = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis = jedisPool.getResource();
            value = jedis.hget(key, field);
        } finally {
            jedis.close();
        }
        return value;
    }
    public static void hdel(byte[] key, byte[] field) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.hdel(key, field);
        } finally {
            jedis.close();
        }
    }
    /**
     * 	存储REDIS队列 反向存储
     * @param  key reids键名
     * @param  value 键值
     */
    public static void rpush(byte[] key, byte[] value) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.rpush(key, value);
        } finally {
            jedis.close();
        }
    }

    /**
     * 	将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端
     * @param  key reids键名
     * @param  destination 键值
     */
    public static void rpoplpush(byte[] key, byte[] destination) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.rpoplpush(key, destination);
        } finally {
            jedis.close();
        }
    }

    /**
     * 	获取队列数据
     * @param  key 键名
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List lpopList(byte[] key) {
        List list = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            list = jedis.lrange(key, 0, -1);
        } finally {
            jedis.close();
        }
        return list;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void hmset(Object key, Map hash) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.hmset(key.toString(), hash);
        } finally {
            jedis.close();
        }
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void hmset(Object key, Map hash, int time) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.hmset(key.toString(), hash);
            jedis.expire(key.toString(), time);
        } finally {
            jedis.close();
        }
    }
    @SuppressWarnings("rawtypes")
    public static List hmget(Object key, String... fields) {
        List result = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            result = jedis.hmget(key.toString(), fields);
        } finally {
            jedis.close();
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public static Set hkeys(String key) {
        Set result = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            result = jedis.hkeys(key);
        }finally {
            jedis.close();
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public static List lrange(byte[] key, int from, int to) {
        List result = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            result = jedis.lrange(key, from, to);
        }finally {
            jedis.close();
        }
        return result;
    }
    @SuppressWarnings("rawtypes")
    public static Map hgetAll(byte[] key) {
        Map result = null;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            result = jedis.hgetAll(key);
        }finally {
            jedis.close();
        }
        return result;
    }

    public static void del(byte[] key) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            jedis.del(key);
        }finally {
            jedis.close();
        }
    }

    public static long llen(byte[] key) {
        long len = 0;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            len= jedis.llen(key);
        } finally {
            jedis.close();
        }
        return len;
    }
    /**
     * 	设置有效期
     *	@param key
     *	@param seconds
     *	@return
     */
    public static long expire(byte[] key,int seconds) {
        long len = 0;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            len=jedis.expire(key, seconds);
        } finally {
            jedis.close();
        }
        return len;
    }
    /**
     *  查看有效期
     *	@param key
     *	@return
     */
    public static long pttl(byte[] key) {
        long len = 0;
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            len=jedis.pttl(key);
        } finally {
            jedis.close();
        }
        return len;
    }

}
