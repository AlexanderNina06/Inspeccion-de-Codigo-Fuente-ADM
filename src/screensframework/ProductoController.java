package screensframework;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import screensframework.DBConnect.DBConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Label;
import java.sql.PreparedStatement;

public class ProductoController implements Initializable, ControlledScreen {
    
    ScreensController controlador;
    private ControlesBasicos controlesBasicos = new ControlesBasicos();
    @FXML private Button btAddProducto;
    @FXML private Button btModificarProducto;
    @FXML private Button btEliminarProducto;
    @FXML private Button btNuevoProducto;
    
    @FXML private TextField tfNombreProducto;
    @FXML private TextField tfPrecioProducto;
    @FXML private TextField tfBuscarProducto;
    @SuppressWarnings("rawtypes")
    @FXML private ComboBox cbCategoriaProducto;
    @SuppressWarnings("rawtypes")
    @FXML private ComboBox cbMarcaProducto;
    @FXML private Label lbCodigoProducto;
    
    @SuppressWarnings("rawtypes")
    @FXML private TableView tablaProducto;
    @SuppressWarnings("rawtypes")
    @FXML private TableColumn col;
    private Connection conexion;
    
    @SuppressWarnings("rawtypes")
    ObservableList<ObservableList> producto;
    
    
    @SuppressWarnings({ "unused", "unchecked" })
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.cargarDatosTabla();
        
        btEliminarProducto.setDisable(true);
        btModificarProducto.setDisable(true);
        btEliminarProducto.setStyle("-fx-background-color:grey");
        btModificarProducto.setStyle("-fx-background-color:grey");
        
        ObservableList<Object> categoriaID = FXCollections.observableArrayList();
        ObservableList<Object> categoriaNomnre = FXCollections.observableArrayList();
        ObservableList<Object> subCategoria = FXCollections.observableArrayList();
        ObservableList<Object> marcas = FXCollections.observableArrayList();
        
        try {
            conexion = DBConnection.connect();
    
            // COMBOBOX DE CATEGORIA
            String slqCategoria = "SELECT idcategoria, nombre_categoria FROM categoria";
            ResultSet resultadoCategoria = conexion.createStatement().executeQuery(slqCategoria);
            while (resultadoCategoria.next()) {
                int idCategoria = resultadoCategoria.getInt("idcategoria");
                String nombreCategoria = resultadoCategoria.getString("nombre_categoria");
                cbCategoriaProducto.getItems().add(new Categoria(idCategoria, nombreCategoria));
            }
    
            // COMBOBOX DE MARCAS
            String slqMarcas = "SELECT idmarca, nombre_marca FROM marca";
            ResultSet resultadoMarcas = conexion.createStatement().executeQuery(slqMarcas);
            while (resultadoMarcas.next()) {
                int idMarca = resultadoMarcas.getInt("idmarca");
                String nombreMarca = resultadoMarcas.getString("nombre_marca");
                cbMarcaProducto.getItems().add(new Marca(idMarca, nombreMarca));
            }
    
            resultadoCategoria.close();
            resultadoMarcas.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }
    
    @Override
    public void setScreenParent(ScreensController pantallaPadre) {
        controlador = pantallaPadre;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
public void cargarDatosTabla() {
    producto = FXCollections.observableArrayList();

    try {
        conexion = DBConnection.connect();
        // SQL FOR SELECTING ALL OF CUSTOMER
        String sql = "SELECT p.idproducto, "
                + " p.nombre_producto, "
                + " p.precio, "
                + " c.nombre_categoria AS nom_categoria, "
                + " m.nombre_marca AS nom_marca "
                + " FROM producto AS p, "
                + " categoria AS c, "
                + " marca AS m "
                + " WHERE p.idcategoria = c.idcategoria AND "
                + " p.idmarca = m.idmarca "
                + " ORDER BY p.idproducto DESC";

        // ResultSet
        ResultSet rs = conexion.createStatement().executeQuery(sql);

        // Títulos de las columnas
        String[] titulos = {
                "Codigo",
                "Nombre",
                "Precio",
                "Categoria",
                "Marca"
        };

        /**********************************
         * TABLE COLUMN ADDED DYNAMICALLY *
         **********************************/

        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            final int j = i;
            col = new TableColumn(titulos[i]);
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> parametro) {
                    return new SimpleStringProperty((String) parametro.getValue().get(j));
                }
            });

            tablaProducto.getColumns().addAll(col);
            col.setMinWidth(100);
            System.out.println("Column [" + i + "] ");

            // Centrar los datos de la tabla
            col.setCellFactory(new Callback<TableColumn<String, String>, TableCell<String, String>>() {
                @Override
                public TableCell<String, String> call(TableColumn<String, String> p) {
                    TableCell cell = new TableCell() {
                        @Override
                        protected void updateItem(Object t, boolean bln) {
                            if (t != null) {
                                super.updateItem(t, bln);
                                System.out.println(t);
                                setText(t.toString());
                                setAlignment(Pos.CENTER); // Setting the Alignment
                            }
                        }
                    };
                    return cell;
                }
            });
        }

        /********************************
         * Cargamos de la base de datos *
         ********************************/

        while (rs.next()) {
            // Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                // Iterate Column
                row.add(rs.getString(i));
            }
            System.out.println("Row [1] added " + row);
            producto.addAll(row);
        }

        // FINALLY ADDED TO TableView
        tablaProducto.setItems(producto);
        rs.close();
    } catch (SQLException e) {
        System.out.println("Error " + e);
    }
}
    
@SuppressWarnings("unchecked")
public void cargarProductosText(String valor) {
    try {
        conexion = DBConnection.connect();
        String sql = "SELECT p.*, c.*, m.* FROM producto AS p, categoria AS c, marca AS m WHERE idproducto = " + valor + " AND p.idcategoria = c.idcategoria AND p.idmarca = m.idmarca";
        ResultSet rs = conexion.createStatement().executeQuery(sql);

        while (rs.next()) {
            lbCodigoProducto.setText(rs.getString("idproducto"));
            tfNombreProducto.setText(rs.getString("nombre_producto"));
            tfPrecioProducto.setText(rs.getString("precio"));

            // Establecer la categoría seleccionada
            Categoria categoria = new Categoria(rs.getInt("c.idcategoria"), rs.getString("c.nombre_categoria"));
            cbCategoriaProducto.setValue(categoria);

            // Establecer la marca seleccionada
            Marca marca = new Marca(rs.getInt("m.idmarca"), rs.getString("m.nombre_marca"));
            cbMarcaProducto.setValue(marca);
        }
        rs.close();
    } catch (SQLException ex) {
        System.out.println("Error " + ex);
    }
}
    
@FXML
private void getProductoSeleccionado(MouseEvent event) {
    tablaProducto.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            if (tablaProducto != null && tablaProducto.getSelectionModel().getSelectedItem() != null) {

                btAddProducto.setDisable(true);
                btEliminarProducto.setDisable(false);
                btModificarProducto.setDisable(false);
                btAddProducto.setStyle("-fx-background-color:grey");
                btEliminarProducto.setStyle("-fx-background-color:#66CCCC");
                btModificarProducto.setStyle("-fx-background-color:#66CCCC");

                Object selectedItem = tablaProducto.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof ObservableList) {
                    ObservableList<String> selectedRow = (ObservableList<String>) selectedItem;
                    String productoId = selectedRow.get(0); // Obtener el ID de la primera columna
                    cargarProductosText(productoId);
                } else {
                    System.err.println("Error: El elemento seleccionado no es un ObservableList<String>");
                }
            }
        }
    });
}
    
    @FXML
    private void addProducto(ActionEvent event) {
        
        int indiceCategoria = cbCategoriaProducto.getSelectionModel().getSelectedIndex() + 1;
        int indiceMarca = cbMarcaProducto.getSelectionModel().getSelectedIndex() + 1;
        
        try {
            conexion = DBConnection.connect();
            String sql = "INSERT INTO producto "
                    + " (nombre_producto, precio, idcategoria, idmarca) "
                    + " VALUES (?, ?, ?, ?)";
            PreparedStatement estado = conexion.prepareStatement(sql);
            estado.setString(1, tfNombreProducto.getText());
            estado.setInt(2, Integer.parseInt(tfPrecioProducto.getText()));
            estado.setInt(3, indiceCategoria);
            estado.setInt(4, indiceMarca);
            
            int n  = estado.executeUpdate();
            
            if (n > 0) {
                
                tablaProducto.getColumns().clear();
                tablaProducto.getItems().clear();
                cargarDatosTabla();
            }
            estado.close();
            
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
        
    }
    
    @FXML
    private void modificarProducto(ActionEvent event) {
        Categoria categoriaSeleccionada = (Categoria) cbCategoriaProducto.getSelectionModel().getSelectedItem();
        Marca marcaSeleccionada = (Marca) cbMarcaProducto.getSelectionModel().getSelectedItem();

        if (categoriaSeleccionada == null || marcaSeleccionada == null) {
            System.out.println("Por favor, seleccione una categoría y marca.");
            return;
        }

        try {
            conexion = DBConnection.connect();

            String sql = "UPDATE producto SET nombre_producto = ?, precio = ?, idcategoria = ?, idmarca = ? WHERE idproducto = " + lbCodigoProducto.getText() + "";

            PreparedStatement estado = conexion.prepareStatement(sql);

            estado.setString(1, tfNombreProducto.getText());
            estado.setInt(2, Integer.parseInt(tfPrecioProducto.getText()));
            estado.setInt(3, categoriaSeleccionada.getIdCategoria());
            estado.setInt(4, marcaSeleccionada.getIdMarca());

            int n = estado.executeUpdate();

            if (n > 0) {
                tablaProducto.getColumns().clear();
                tablaProducto.getItems().clear();
                cargarDatosTabla();
            }

            estado.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }
    
    @FXML
    private void eliminarProducto(ActionEvent event) {
        
        int confirmarEliminar = JOptionPane.showConfirmDialog(null, "Realmente desea eliminar este producto??");
        
        if (confirmarEliminar == 0) {
            try 
            {
                conexion = DBConnection.connect();

                String sql = "DELETE FROM producto WHERE idproducto = "+lbCodigoProducto.getText()+"";

                PreparedStatement estado = conexion.prepareStatement(sql);

                int n = estado.executeUpdate();

                if (n > 0) {
                    tablaProducto.getColumns().clear();
                    tablaProducto.getItems().clear();
                    cargarDatosTabla();
                }

                estado.close();

            } catch (SQLException e) {
                System.out.println("Error " + e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @FXML
    private void buscarProducto(ActionEvent event) {
        
        tablaProducto.getItems().clear();
        try {
            conexion = DBConnection.connect();
            String sql = "SELECT p.idproducto, "
                    + " p.nombre_producto, "
                    + " p.precio, "
                    + " c.nombre_categoria AS nom_categoria, "
                    + " m.nombre_marca AS nom_marca "
                    + " FROM producto AS p, categoria AS c, marca AS m "
                    + " WHERE CONCAT "
                    + " (p.idproducto, '', "
                    + " p.nombre_producto, '', "
                    + " p.precio, '', "
                    + " c.nombre_categoria, '',"
                    + " m.nombre_marca) LIKE '%"+tfBuscarProducto.getText()+"%' AND "
                    + " p.idcategoria = c.idcategoria AND "
                    + " p.idmarca = m.idmarca ORDER BY p.idproducto DESC";
            
            ResultSet rs = conexion.createStatement().executeQuery(sql);
            
            while(rs.next()){
                
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                producto.addAll(row);
            }
            tablaProducto.setItems(producto);
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
        
    }
    
    @FXML
    private void nuevoProducto(ActionEvent event) {
        
        btAddProducto.setDisable(false);
        btEliminarProducto.setDisable(true);
        btModificarProducto.setDisable(true);
        btAddProducto.setStyle("-fx-background-color:#66CCCC");
        btEliminarProducto.setStyle("-fx-background-color:grey");
        btModificarProducto.setStyle("-fx-background-color:grey");
    }
    
    @FXML
    private void irInicioContenido(ActionEvent event) {
        controlador.setScreen(ScreensFramework.contenidoID);
    }
    
    @FXML
    private void salir(ActionEvent event) {
        this.controlesBasicos.salirSistema();
    }
    
    @FXML
    private void cerrarSesion(ActionEvent event) {
        
        controlador.setScreen(ScreensFramework.loginID);
    }

    public class Categoria {
        private int idCategoria;
        private String nombreCategoria;
    
        public Categoria(int idCategoria, String nombreCategoria) {
            this.idCategoria = idCategoria;
            this.nombreCategoria = nombreCategoria;
        }
    
        public int getIdCategoria() {
            return idCategoria;
        }
    
        public String getNombreCategoria() {
            return nombreCategoria;
        }
    
        @Override
        public String toString() {
            return nombreCategoria; // Muestra solo el nombre en el ComboBox
        }
    }
    
    public class Marca {
        private int idMarca;
        private String nombreMarca;
    
        public Marca(int idMarca, String nombreMarca) {
            this.idMarca = idMarca;
            this.nombreMarca = nombreMarca;
        }
    
        public int getIdMarca() {
            return idMarca;
        }
    
        public String getNombreMarca() {
            return nombreMarca;
        }
    
        @Override
        public String toString() {
            return nombreMarca; // Muestra solo el nombre en el ComboBox
        }
    }
}

