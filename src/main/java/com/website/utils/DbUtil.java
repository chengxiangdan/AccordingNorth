package com.website.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.*;
import java.util.*;

//import com.ffManage.daomian.Addressleton;

/**
 * 数据库工具类
 *
 * @author sjh
 *
 */
public class DbUtil {

    private int recordcount; // 总记录条数
    private int currpage = 1;// 当前页
    private int pagesize = 10;// 每页记录条数
    private int pagecount = 1;// 总页数

    private static DruidDataSource dbPoll;

    static {
        try {
            dbPoll = new DruidDataSource();
            dbPoll.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dbPoll.setUsername(PropertiesUtil.readByKey("username"));
            dbPoll.setPassword(PropertiesUtil.readByKey("password"));

            String adress="";
            String url="";
            url =  PropertiesUtil.readByKey("url");
            dbPoll.setUrl(url);
            dbPoll.setInitialSize(1);
            dbPoll.setMinIdle(1);
            dbPoll.setMaxActive(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 加载数据库链接，从数据库连接池里
     *
     * @return
     * @throws Exception
     */
    public static DruidPooledConnection getConn() throws Exception {
        return dbPoll.getConnection();
    }

    private static void freeConn(DruidPooledConnection conn, PreparedStatement ptmt) throws Exception {
        try{
            if(ptmt != null){
                ptmt.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new Exception(e);
        }
    }
    /**
     * 功能：实现数据插入操作
     *
     * @param sql
     *            数据库预处理语句，实现插入数据功能
     * @param objs
     *            根据预处理语句填写对应内容，有多少个问号就填几个
     * @return
     * @throws Exception 
     */
    public static int save(String sql, Object... objs) throws Exception {
        int result = 0;
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ptmt.setObject(i + 1, objs[i]);
            }
            result = ptmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }

    /**
     * 功能：实现数据插入操作
     *
     * @param sql
     *            数据库预处理语句，实现插入数据功能
     * @param values
     *            根据预处理语句填写对应内容，有多少个问号数组中就填几个值
     * @return
     * @throws Exception 
     */
    public static int add(String sql, Object[] values) throws Exception {
        int result = 0;
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            int indes = 1;
            for (Object obj : values) {
                ptmt.setObject(indes++, obj);
            }
            result = ptmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }

    /**
     * @param tablename
     *            表名
     * @param values
     *            map集合，key为表的列名，values为添加的值。
     * @return
     * @throws Exception 
     */
    public static int insert(String tablename, Map<String, Object> values) throws Exception {
        int result = 0;
        StringBuilder f = new StringBuilder(values.size());
        StringBuilder v = new StringBuilder(values.size());
        Set<String> kset = values.keySet();
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;

        for (String key : kset) {
            f.append(key + ",");
            v.append("?,");
        }
        String sql = String.format("insert into %s(%s) values(%s)", tablename, f.substring(0, f.length() - 1),
                v.substring(0, v.length() - 1));
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);

            int index = 1;
            for (String key : kset) {
                ptmt.setObject(index++, values.get(key));
            }
            result = ptmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }

    /**
     * 功能：返回一个表的主键名
     *
     * @param dbname
     * @param tablename
     * @return
     * @throws Exception 
     */
    public String getPk(String dbname, String tablename) throws Exception {
        String pk = null;
        DruidPooledConnection conn = null;
        DatabaseMetaData dbmd = null;
        try {
            conn = getConn();
            dbmd = conn.getMetaData();
            ResultSet rs = dbmd.getPrimaryKeys(dbname, null, tablename);
            if (rs.next()) {
                pk = rs.getString(4);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return pk;
    }

    /**
     * 功能：根据主键删除一条记录
     *
     * @param tablename
     *            表名
     * @param primarykey
     *            要删除记录的主键名
     * @return
     * @throws Exception 
     */
    public int delete(String dbname, String tablename, Object primarykey) throws Exception {
        int result = 0;
        String sql = String.format("delete from "+tablename+" where "+dbname+" = ?");
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            ptmt.setObject(1, primarykey);
            result = ptmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }

        return result;
    }
    /**
     * 功能：插入多条数据
     *
     * @param tablename
     *            表名
     * @param columns
     *            列名称数组
     * @param   values    需要被删除的主键值的集合
     * @return
     * @throws Exception 
     */
    public static int[] addMuch(String tablename, String[] columns,List<Map<String, Object>> values) throws Exception {
        int[] result = null;
        int size = 0;
        if((size = columns.length) > 0) {
        	 StringBuilder sqlBuilder=new StringBuilder();
        	 StringBuilder fieldsBuilder=new StringBuilder();
        	 StringBuilder valuesBuilder = new StringBuilder();
        	 
        	 sqlBuilder.append("insert into ").append(tablename).append(" ");
        	 fieldsBuilder.append(" (");
        	 valuesBuilder.append(" values (");
        	 Iterator<String> it = Arrays.asList(columns).iterator();
        	 for(int i=0;i<columns.length;i++) {
        		  fieldsBuilder.append("`").append(columns[i]).append("`");
                  valuesBuilder.append("?");
                  it.next();
                  if(!it.hasNext())
                	  break;
                  valuesBuilder.append(",");
                  fieldsBuilder.append(",");
        	 }
        	 fieldsBuilder.append(")");
             valuesBuilder.append(")");
             sqlBuilder.append(fieldsBuilder).append(valuesBuilder);
             DruidPooledConnection conn = null;
             PreparedStatement ptmt = null;
             try {
                 conn = getConn();
                 ptmt = conn.prepareStatement(sqlBuilder.toString());
                 for(Map<String, Object> map : values) {
                	 for(int i= 0;i<size;i++) {
                		 ptmt.setObject(i+1, map.get(columns[i]));
                	 }
                	 ptmt.addBatch();
                 }
                 result = ptmt.executeBatch();
             } catch (SQLException e) {
                 e.printStackTrace();
                 throw new Exception(e);
             } finally {
                 freeConn(conn, ptmt);
             }
        }
        return result;
    }
    /**
     * 功能：根据多个主键删除多条记录
     *
     * @param tablename
     *            表名
     * @param primarykey
     *            表中字段主键字段名
     * @param   values    需要被删除的主键值的集合
     * @return
     * @throws Exception 
     */
    public static int deleteIds(String tablename, Object primarykey,List<String> values) throws Exception {
        int result = 0;
        int size = 0;
        if((size = values.size()) > 0) {
        	 StringBuilder sb = new StringBuilder();
        	 sb.append("delete from ").append(tablename).append(" where ").append(primarykey).append(" in (");
        	 Iterator<String> it = values.iterator();
        	 for(;;) {
                  sb.append("?");
                  it.next();
                  if(!it.hasNext())
                	  break;
                  sb.append(",");
        	 }
             sb.append(")");
             DruidPooledConnection conn = null;
             PreparedStatement ptmt = null;
             try {
                 conn = getConn();
                 ptmt = conn.prepareStatement(sb.toString());
                 for (int i = 0; i < size; i++) {
                	 ptmt.setObject(i+1, values.get(i));
				}
                 result = ptmt.executeUpdate();
             } catch (SQLException e) {
                 e.printStackTrace();
                 throw new Exception(e);
             } finally {
                 freeConn(conn, ptmt);
             }
        }
        return result;
    }
    /**
     * @param tablename
     * @param where
     *            表示删除条件 一个条件一个问号
     * @param objs
     *            根据预处理语句填写对应内容，有多少个问号就填几个
     * @return
     * @throws Exception 
     */
    public static int delete(String tablename, String where, Object... objs) throws Exception {
        int result = 0;
        String sql = String.format("delete from %s %s", tablename, where);
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ptmt.setObject(i + 1, objs[i]);
            }
            result = ptmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }

    /**
     * @param sql
     *            update tablename set name = ? ... where id = ?,
     * @param objs
     *            "andy",...,100 根据update语句填写共有几个值
     * @return
     * @throws Exception 
     */
    public static int update(String sql, Object... objs) throws Exception {
        int result = 0;
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ptmt.setObject(i + 1, objs[i]);
            }
            result = ptmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }

    /**
     * @param tablename
     *            表名
     * @param m
     *            map集合存放要修改的列名及值
     * @param where
     *            修改的条件
     * @return
     * @throws Exception 
     */
    public static int update(String tablename, Map<String, Object> m, String where) throws Exception {
        int result = 0;
        StringBuilder s = new StringBuilder();
        for (String k : m.keySet()) {
            s.append(k + "=?,");
        }
        String sql = String.format("update %s set %s %s", tablename, s.toString().substring(0, s.length() - 1), where);
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            int i = 0;
            for (Object o : m.values()) {
                ptmt.setObject(++i, o);
            }
            result = ptmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }
    
    /**
     * 批量更新
     * @param tableName
     * @param setColumns
     * @param setValues
     * @param whereColumns
     * @return
     * @throws Exception
     */
    public static int updateBatch(String tableName,String[] setColumns, List<Map<String, Object>> setValues,String[] whereColumns, List<Map<String,Object>> whereValues) throws Exception {
        int result = 0;
        StringBuilder setBuilder = new StringBuilder();
       
        StringBuilder sqlBuilder=new StringBuilder();
        
        for (String k : setColumns) {
            setBuilder.append(k + "=?,");
        }
      
        sqlBuilder.append("UPDATE ").append(tableName).append(" SET ").append(setBuilder.toString().substring(0, setBuilder.length() - 1));
        if(whereColumns != null){
        	StringBuilder whereBuilder=new StringBuilder();
        	whereBuilder.append(" WHERE 1=1 ");
        	for(String whereCol:whereColumns){
              	whereBuilder.append(" AND ").append(whereCol).append("=? ");
            }
        	sqlBuilder.append(" ").append(whereBuilder);
        }
        String sql = sqlBuilder.toString();
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            for (int i=0;i<setValues.size();i++) {
            	Map<String, Object> setValueMap = setValues.get(i);
            	int j=0;
            	for(;j<setColumns.length;j++){
            		ptmt.setObject(j+1, setValueMap.get(setColumns[j]));
            	}
            	if(whereValues != null){
            		Map<String, Object> whereValueMap = whereValues.get(i);
                	for(int k=0;k<whereColumns.length;k++){
                		ptmt.setObject(j+1, whereValueMap.get(whereColumns[k]));
                	}
            	}
                ptmt.addBatch();
            }
            ptmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }

    /**
     * 功能实现修改数据
     *
     * @param tablename
     *            表名
     * @param map
     *            map集合存放要修改的列名及值，和表的主键名和主键值
     * @return
     * @throws Exception 
     */
    public int update(String dbname, String tablename, Map<String, Object> map) throws Exception {
        int result = 0;
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
        	String pk = getPk(dbname, tablename);
            if (map.containsKey(pk)) {
                StringBuilder f = new StringBuilder(map.size());
                Object pkname = map.get(pk);
                map.remove(pk);
                Set<String> kset = map.keySet();
                for (String key : kset) {
                    f.append(key + "=?,");
                }
                String sql = String.format("update %s set %s where %s=?", tablename, f.substring(0, f.length() - 1), pk);
                conn = getConn();
                ptmt = conn.prepareStatement(sql);
                int index = 1;
                for (String key : kset) {
                    ptmt.setObject(index++, map.get(key));
                }
                ptmt.setObject(index, pkname);
                result = ptmt.executeUpdate();
            }
		}catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return result;
    }

    /**
     * 功能：实现表的查询工作 只能查询一条结果
     *
     * @param sql
     *            sql查询语句// select score name from stu where id = ?,1
     * @param params
     *            条件
     * @return
     * @throws Exception 
     */
    public static Map<String, Object> queryone(String sql, Object... params) throws Exception {
        Map<String, Object> map = null;
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ptmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = ptmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            String[] keys = new String[rsmd.getColumnCount()];
            for (int c = 1; c <= rsmd.getColumnCount(); c++) {
                keys[c - 1] = rsmd.getColumnLabel(c);
            }
            map = new HashMap<String, Object>();
            if(rs.next()) {
                for (int n = 0; n < keys.length; n++) {
                    map.put(keys[n], rs.getObject(keys[n]));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return map;
    }

    /**
     * 功能：实现表的查询工作 能查询多条结果,返回List<Map<String,Object>>集合。一条结果就是一个map。
     *
     * @param sql
     *            sql查询语句// select score name from stu where id = ?,1
     * @param objs
     *            条件
     * @return
     * @throws Exception 
     */
    public static List<Map<String, Object>> query(String sql, Object... objs) throws Exception {
        List<Map<String, Object>> list = null;
        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(sql);
            for (int i = 0; i < objs.length; i++) {
                ptmt.setObject(i + 1, objs[i]);
            }
            ResultSet rs = ptmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            String[] keys = new String[rsmd.getColumnCount()];
            for (int m = 1; m <= rsmd.getColumnCount(); m++) {
                keys[m - 1] = rsmd.getColumnLabel(m);
            }
            list = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int n = 0; n < keys.length; n++) {
                    map.put(keys[n], rs.getObject(keys[n]));
                }
                list.add(map);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return list;
    }

    /**
     * 实现分页查询与多条查询类似
     *
     * @param sql
     *            select * from stu where id>?,1
     * @param objs
     *            条件
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("resource")
    public List<Map<String, Object>> page(String sql, Object... objs) throws Exception {
        List<Map<String, Object>> list = null;
        String ccc = "select count(*)" + sql.substring(sql.indexOf("from"));

        DruidPooledConnection conn = null;
        PreparedStatement ptmt = null;
        try {
            conn = getConn();
            ptmt = conn.prepareStatement(ccc);
            int index = 1;
            for (Object o : objs) {
                ptmt.setObject(index++, o);
            }
            ResultSet rs = ptmt.executeQuery();
            rs.next();
            this.recordcount = rs.getInt(1);// 总记录条数
            this.pagecount = this.recordcount % this.pagesize == 0 ? this.recordcount / this.pagesize
                    : this.recordcount / this.pagesize + 1;
            if (this.currpage < 1)
                this.currpage = 1;
            if (this.currpage > getPagecount())
                this.currpage = this.pagecount;

            String psql = sql + " limit ?,?";
            ptmt = conn.prepareStatement(psql);
            index = 1;
            for (Object o : objs) {
                ptmt.setObject(index++, o);
            }
            ptmt.setInt(index++, this.currpage * this.pagesize - this.pagesize);
            ptmt.setInt(index, this.pagesize);
            rs = ptmt.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            list = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> m = new HashMap<String, Object>();
                int cc = rsmd.getColumnCount();
                for (int i = 1; i <= cc; i++) {
                    String name = rsmd.getColumnLabel(i);
                    m.put(name, rs.getObject(name));
                }
                list.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            freeConn(conn, ptmt);
        }
        return list;
    }

    /**
     * 实现分页查询功能
     *
     * @param currpage
     *            当前页（查询的第几页）
     * @param pagesize
     *            每页的记录条数
     * @param tablename
     *            表名
     * @param fields
     *            查询的对象（列名）
     * @param where
     *            查询的条件
     * @return
     * @throws Exception 
     */
    public List<Map<String, Object>> page(int currpage, int pagesize, String tablename, String fields, String where) throws Exception {
        this.currpage = currpage;
        this.pagesize = pagesize;
        String sql = String.format("select %s from %s %s", fields, tablename, where);
        return page(sql);
    }

    public int getRecordcount() {
        return recordcount;
    }

    public void setRecordcount(int recordcount) {
        this.recordcount = recordcount;
    }

    public int getCurrpage() {
        return currpage;
    }

    public void setCurrpage(int currpage) {
        this.currpage = currpage;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

}
