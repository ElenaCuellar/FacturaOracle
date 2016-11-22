import java.sql.*;

public class ConexionBD {
	Connection conexion;
	boolean correcta;
	
	public ConexionBD(String host, String puerto, String sid, String user, String pass){
		correcta = true;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			correcta = false;
		}
		try {
			conexion=DriverManager.getConnection("jdbc:oracle:thin:@"+host+":"+puerto+":"+sid,user,pass);
		} catch (SQLException e) {
			correcta = false;
		}
	}
}
