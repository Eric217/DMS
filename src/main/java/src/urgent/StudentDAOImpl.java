package src.urgent;

import src.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StudentDAOImpl {

    private Connection conn;
    private PreparedStatement pstmt;

    public StudentDAOImpl(Connection conn) {
        this.conn = conn;
    }


    public boolean doCreate(Student vo) throws SQLException {
        String sql = "INSERT INTO emp(empno,ename,job,hiredate,sal,comm) VALUES (?,?,?,?,?,?)" ;
        this.pstmt = this.conn.prepareStatement(sql);
//        this.pstmt.setInt(1, vo.getEmpno());
//        this.pstmt.setString(2, vo.getEname());
//        this.pstmt.setString(3, vo.getJob());
//        this.pstmt.setDate(4, new java.sql.Date(vo.getHiredate().getTime()));
//        this.pstmt.setDouble(5, vo.getSal());
        return this.pstmt.executeUpdate() > 0 ;
    }


    public boolean doUpdate(Student vo) throws SQLException {
        String sql = "UPDATE emp SET ename=?,job=?,hiredate=?,sal=?,comm=? WHERE empno=?" ;
        this.pstmt = this.conn.prepareStatement(sql);
//        this.pstmt.setString(1, vo.getEname());
//        this.pstmt.setString(2, vo.getJob());
//        this.pstmt.setDate(3, new java.sql.Date(vo.getHiredate().getTime()));
//        this.pstmt.setDouble(4, vo.getSal());
//        this.pstmt.setDouble(5, vo.getComm());
//        this.pstmt.setInt(6, vo.getEmpno());
        return this.pstmt.executeUpdate() > 0 ;

    }


    public boolean doRemoveBatch(Set<String> ids) throws SQLException {
        if(ids == null || ids.size() == 0){	//没有要删除的数据
            return false ;
        }
        //字符串的拼凑使用StringBuffer
        StringBuffer sql = new StringBuffer() ;
        sql.append("DELETE FROM emp WHERE empno IN(") ;
        Iterator<String> iter = ids.iterator();
        while(iter.hasNext()){
            //删除语句的格式是delete from emp where empno in ("","","",);
            sql.append(iter.next()).append(",") ;
        }
        //删除最后一个逗号，并且添加)
        sql.delete(sql.length()-1, sql.length()).append(")");
        /* conn.prepareStatement()只接受String，所以让
         * StringBuffer转为String，需要用toString(),
         * 这是重点强调了的知识点
         * */
        this.pstmt = this.conn.prepareStatement(sql.toString());
        // pstmt.executeUpdate()返回结果为int
        return this.pstmt.executeUpdate() == ids.size();
    }


    public Student findById(String id) throws SQLException {
        Student vo = null ;
        String sql = "SELECT name, password, email, phone, grade, college, major, " +
                "introduce, pu" +
                "nish_end FROM " +
                "student " +
                "WHERE id=?" ;
        this.pstmt = this.conn.prepareStatement(sql) ;
        pstmt.setString(1, id);
        ResultSet rs = this.pstmt.executeQuery() ;
        if(rs.next()) {
            vo = new Student() ;
            vo.setId(id);
            vo.setName(rs.getString(1));
            vo.setPassword(rs.getString(2));
            vo.setEmail(rs.getString(3));
            vo.setPhone(rs.getString(4));
            vo.setGrade(rs.getInt(5));
            vo.setCollege(rs.getString(6));
            vo.setMajor(rs.getString(7));
            vo.setIntroduce(rs.getString(8));
            vo.setPunish_end(rs.getTimestamp(9));

        }
        return vo ;
    }


    public List<Student> findAll() throws SQLException {
        List<Student> all = new ArrayList<Student>() ;
        String sql = "SELETE empno,ename,job,hiredate,sal,comm FROM emp" ;
        this.pstmt = this.conn.prepareStatement(sql) ;
        ResultSet rs = this.pstmt.executeQuery() ;
        while(rs.next()) {
            Student vo = new Student() ;
//            vo.setEmpno(rs.getInt(1));
//            vo.setEname(rs.getString(2));
//            vo.setJob(rs.getString(3));
//            vo.setHiredate(rs.getDate(4));
//            vo.setSal(rs.getDouble(5));
//            vo.setComm(rs.getDouble(6));
            all.add(vo) ;
        }
        return all;
    }


    public List<Student> findAllSplit(Integer currentPage, Integer lineSize, String column, String keyWord) throws SQLException {
        List<Student> all = new ArrayList<Student>() ;
        String sql = "SELECT * FROM "
                + " (SELECT empno,ename,job,hiredate,sal,comm,ROWNUM rn"
                + " FROM emp"
                + " WHERE " + column + " LIKE ? AND ROWNUM<=?) temp "
                + " WHERE temp.rn>? " ;
        this.pstmt = this.conn.prepareStatement(sql) ;
        this.pstmt.setString(1, "%" + keyWord + "%");
        this.pstmt.setInt(2, currentPage * lineSize);
        this.pstmt.setInt(3, (currentPage - 1) * lineSize);
        ResultSet rs = this.pstmt.executeQuery() ;
        while(rs.next()) {
            Student vo = new Student() ;
//            vo.setEmpno(rs.getInt(1));
//            vo.setEname(rs.getString(2));
//            vo.setJob(rs.getString(3));
//            vo.setHiredate(rs.getDate(4));
//            vo.setSal(rs.getDouble(5));
//            vo.setComm(rs.getDouble(6));
            all.add(vo) ;
        }
        return all;
    }


    public Integer getAllCount(String column, String keyWord) throws SQLException {
        String sql = "SELECT COUNT(empno) FROM emp WHERE " + column + " LIKE ?" ;
        this.pstmt = this.conn.prepareStatement(sql) ;
        this.pstmt.setString(1, "%" + keyWord + "%");
        ResultSet rs = this.pstmt.executeQuery() ;
        if(rs.next()) {
            return rs.getInt(1) ;
        }
        return null;
    }


}
