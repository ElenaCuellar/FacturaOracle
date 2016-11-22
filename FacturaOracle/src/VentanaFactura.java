import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaFactura extends JFrame {

	private JPanel contentPane;
	private JTextField tfNum;
	private JTextField tfTotal;
	private JTextField tfFecha;
	private JTextField tfCliente;
	private JButton bTotal;
	private JRadioButton rSi, rNo;
	private ConexionBD bd;
	private BDFactura bdFactura;
	private int codigo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaFactura frame = new VentanaFactura();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaFactura() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				bdFactura.cerrarRecursos();
				System.exit(0);
			}
		});
		setTitle("Consulta de facturas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 328);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNumDeFactura = new JLabel("Num. de factura:");
		lblNumDeFactura.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNumDeFactura.setBounds(35, 33, 115, 14);
		contentPane.add(lblNumDeFactura);
		
		tfNum = new JTextField();
		tfNum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				consultarFactura();
			}
		});
		tfNum.setBounds(160, 31, 97, 20);
		contentPane.add(tfNum);
		tfNum.setColumns(10);
		
		JButton bConsultar = new JButton("Consultar");
		bConsultar.setHorizontalAlignment(SwingConstants.LEADING);
		bConsultar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				consultarFactura();
			}
		});
		bConsultar.setFont(new Font("Tahoma", Font.BOLD, 11));
		bConsultar.setBounds(278, 33, 134, 36);
		bConsultar.setIcon(new ImageIcon("buscar.gif"));
		contentPane.add(bConsultar);
		
		bTotal = new JButton("Total a pagar");
		bTotal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				consultarTotal();
			}
		});
		bTotal.setEnabled(false);
		bTotal.setFont(new Font("Tahoma", Font.BOLD, 11));
		bTotal.setBounds(35, 209, 115, 23);
		contentPane.add(bTotal);
		
		tfTotal = new JTextField();
		tfTotal.setEditable(false);
		tfTotal.setBounds(171, 210, 241, 20);
		contentPane.add(tfTotal);
		tfTotal.setColumns(10);
		
		JLabel lblFechaDeFactura = new JLabel("Fecha de factura:");
		lblFechaDeFactura.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblFechaDeFactura.setBounds(35, 93, 115, 14);
		contentPane.add(lblFechaDeFactura);
		
		tfFecha = new JTextField();
		tfFecha.setEditable(false);
		tfFecha.setColumns(10);
		tfFecha.setBounds(160, 91, 97, 20);
		contentPane.add(tfFecha);
		
		JLabel lblCliente = new JLabel("Cliente:");
		lblCliente.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblCliente.setBounds(35, 156, 62, 14);
		contentPane.add(lblCliente);
		
		tfCliente = new JTextField();
		tfCliente.setEditable(false);
		tfCliente.setColumns(10);
		tfCliente.setBounds(93, 154, 319, 20);
		contentPane.add(tfCliente);
		
		JLabel lblPagada = new JLabel("Pagada:");
		lblPagada.setHorizontalAlignment(SwingConstants.CENTER);
		lblPagada.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPagada.setBounds(278, 94, 115, 14);
		contentPane.add(lblPagada);
		
		ButtonGroup grupo = new ButtonGroup();
		
		rSi = new JRadioButton("SI");
		rSi.setEnabled(false);
		rSi.setBounds(284, 115, 56, 23);
		grupo.add(rSi);
		contentPane.add(rSi);
		
		rNo = new JRadioButton("NO");
		rNo.setEnabled(false);
		rNo.setSelected(true);
		rNo.setBounds(356, 115, 56, 23);
		grupo.add(rNo);
		contentPane.add(rNo);
		
		JButton bSalir = new JButton("SALIR");
		bSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				bdFactura.cerrarRecursos();
				System.exit(0);
			}
		});
		bSalir.setBounds(284, 241, 128, 23);
		contentPane.add(bSalir);
		
		codigo = -1;
		
		//Iniciar la conexion
		bd = new ConexionBD("localhost","1521","xe","accesoDatos","accesoDatos");
		if(!bd.correcta){
			int opcion=JOptionPane.showConfirmDialog(this,"Error de conexion con la base de datos.\n¿Quiere cerrar la aplicacion?","Error",JOptionPane.YES_NO_OPTION);
			if(opcion==JOptionPane.YES_OPTION)
				System.exit(0);
			else //no cierra el programa pero sin conexion no funciona nada....
				bdFactura = new BDFactura(); 
		}
		else{
			//Iniciar el objeto que ejecutará sentencias en la bd
			bdFactura = new BDFactura(bd);
		}
	}
	
	public void consultarFactura(){
		//Deshabilitar el boton del total al comienzo
		bTotal.setEnabled(false);
		tfTotal.setText("");
		//Controlar que el codigo introducido es un numero
		try{
			codigo=Integer.parseInt(tfNum.getText());
			
			//Controlar que el codigo introducido exista
			if(bdFactura.existeFactura("Facturas", "nFactura",codigo)){
				ArrayList<String> contenido = bdFactura.verFactura("Facturas","nFactura",codigo);
				//Mostrar los datos obtenidos
				tfFecha.setText(contenido.get(0));
				tfCliente.setText(contenido.get(1));
				if(contenido.get(2).equals("s"))
					rSi.setSelected(true);
				else
					rNo.setSelected(true);
				//Habilitar boton del total
				bTotal.setEnabled(true);
			}
			else{
				codigo=-1;
				JOptionPane.showMessageDialog(this,"No existe la factura","Aviso",JOptionPane.OK_OPTION);
			}
		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(this,"Introduzca un numero en el campo de texto.","Error",JOptionPane.OK_OPTION);
			codigo=-1;
			tfNum.setText("");
			tfNum.requestFocus();
		}
	}
	
	public void consultarTotal(){
		if(codigo>0)
			tfTotal.setText(bdFactura.calcularTotal(codigo));
		
		else
			JOptionPane.showMessageDialog(this,"El codigo no es valido","Error",JOptionPane.OK_OPTION);
	}
}
