package DAOs;

import db.MyConnection;
import model.data;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class data_DAO {
    public static List<data> getAllFiles(String email) throws SQLException{
        Connection connection = MyConnection.getConnection( );
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM data WHERE email =?");
        ps.setString(1,email);
        ResultSet rs =ps.executeQuery();

        List<data> files = new ArrayList<>();

        while(rs.next()){
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new data(id, name, path));

        }

        return files;
    }

    public static int hideFile(data file)throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("INSERT INTO data (name,path,email,bin_data) values(?,?,?,?)");
        ps.setString(1,file.getFileName());
        ps.setString(2, file.getPath());
        ps.setString(3, file.getEmail());


        File f = new File(file.getPath()); //  file f main daali
        FileReader fr = new FileReader(f); // filereader se f file read kari
        ps.setCharacterStream(4,fr,f.length());


        int ans = ps.executeUpdate();
        fr.close();   // close the file reader
        f.delete(); // delete file from that path


        return ans;
    }
    public  static void unhide(int id )throws SQLException,IOException{
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("select path, bin_data from data where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        String path = rs.getString("path");
        Clob c = rs.getClob("bin_data");

        Reader r = c.getCharacterStream();
        FileWriter fw = new FileWriter(path);
        int i;
        while ((i = r.read()) != -1) {
            fw.write((char) i);
        }
        fw.close();
        ps = connection.prepareStatement("delete from data where id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
       // System.out.println("Successfully Unhidden");
    }

}
