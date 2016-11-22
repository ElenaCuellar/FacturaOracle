import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class BDFactura {
	ConexionBD bd;
	ResultSet res;
	PreparedStatement sentenciaPrep;
	CallableStatement procedimiento;
	String instruccion;
	ArrayList<String> lista;
	
	public BDFactura(ConexionBD bd){
		this.bd=bd;
		res = null;
		sentenciaPrep=null;
		procedimiento=null;
		instruccion="";
		lista = new ArrayList<String>();
		for(int i=1;i<4;i++)
			lista.add("");
	}
	public BDFactura(){ 
		/*Por si falla la conexion, que cree un objeto bdFactura vacio
		 *  y no dé otros fallos, aunque no sirva de nada*/
		bd=null;
		res = null;
		sentenciaPrep=null;
		procedimiento=null;
		instruccion="";
		lista = new ArrayList<String>();
		for(int i=1;i<4;i++)
			lista.add("");
	}
	
	public boolean existeFactura(String tabla, String criterio, int codigo){
		instruccion = "SELECT COUNT(*) AS contador FROM "+tabla+" WHERE "+criterio+" = ?";
		try {
			sentenciaPrep = bd.conexion.prepareStatement(instruccion);
			sentenciaPrep.setInt(1, codigo);
			res = sentenciaPrep.executeQuery();
			
			if(res.next() && res.getInt("contador")>0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"Error al comprobar factura","Error",JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}
	
	public ArrayList<String> verFactura(String tabla, String criterio, int codigo){
		instruccion = "SELECT * FROM "+tabla+" WHERE "+criterio+" = ?";
		try {
			sentenciaPrep = bd.conexion.prepareStatement(instruccion);
			sentenciaPrep.setInt(1, codigo);
			res = sentenciaPrep.executeQuery();
			
			//Tratamos los datos obtenidos para pasarlos a String...
			lista.clear();
			if(res.next()){
				SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
				Date fec;
				String fecha;
				//Si existe una fecha...
				if((fec=res.getDate("fecfactura"))!=null)
					fecha = formatoFecha.format(fec);
				else //Si no...
					fecha = "Sin fecha";
				lista.add(fecha);
				lista.add(verCliente(res.getInt("codcliente")));
				//Si existe informacion del pago..
				String pagada;
				if((pagada=res.getString("pagada"))!=null)
					lista.add(pagada);
				else //Si no...
					lista.add("n");
				return lista;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"Error al mostrar factura","Error",JOptionPane.ERROR_MESSAGE);
		}
		return lista;
	}
	
	public String verCliente(int codCliente){
		instruccion = "SELECT nombre FROM Clientes WHERE codigo= "+codCliente;
		try {
			Statement sentencia = bd.conexion.createStatement();
			ResultSet resultado = sentencia.executeQuery(instruccion);
			if(resultado.next())
				return resultado.getString("nombre");
			sentencia.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"Error al mostrar cliente","Error",JOptionPane.ERROR_MESSAGE);
		}
		return "Sin cliente";
	}
	
	public String calcularTotal(int codigo){
		float xtotal=0;
		instruccion = "{call sp_total_factura(?,?)}";
		try {
			procedimiento = bd.conexion.prepareCall(instruccion);
			procedimiento.setInt(1,codigo);
			procedimiento.registerOutParameter(2, Type.FLOAT);
			
			int procedimientoCorrecto=procedimiento.executeUpdate();
			//...va a devolver siempre 1, porque tiene 1 valor de salida
			if(procedimientoCorrecto>=0 && (xtotal=procedimiento.getFloat(2))!=-1) 
				return Float.toString(xtotal);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,"Error al calcular el total","Error",JOptionPane.ERROR_MESSAGE);
		}
		return "Total incorrecto";
	}
	
	public void cerrarRecursos(){
		try{
			if(sentenciaPrep!=null)
				sentenciaPrep.close();
			if(procedimiento!=null)
				procedimiento.close();
			if(bd!=null)
				bd.conexion.close();
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null,"Error al cerrar recursos","Error",JOptionPane.ERROR_MESSAGE);
		}
	}
}
