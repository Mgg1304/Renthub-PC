package Interfaz;

import Modelo.Producto;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProductosView extends VBox {

    private FlowPane flowOfertado = new FlowPane();
    private FlowPane flowAlquilado = new FlowPane();

    public ProductosView() {

        setSpacing(20);
        setPadding(new Insets(20));

        flowOfertado.setHgap(20);
        flowOfertado.setVgap(20);

        flowAlquilado.setHgap(20);
        flowAlquilado.setVgap(20);

        VBox ofertadoBox = new VBox(10,
                new Label("OFERTADO"),
                flowOfertado
        );

        VBox alquiladoBox = new VBox(10,
                new Label("ALQUILADO"),
                flowAlquilado
        );

        getChildren().addAll(ofertadoBox, alquiladoBox);
    }

    public void cargarProductos(List<Producto> productos) {

        flowOfertado.getChildren().clear();
        flowAlquilado.getChildren().clear();

        for (Producto p : productos) {

            ProductoCard card = new ProductoCard(p);

            // lógica simple (puedes cambiarla luego)
            if (p.getStock() > 0) {
                flowOfertado.getChildren().add(card);
            } else {
                flowAlquilado.getChildren().add(card);
            }
        }
    }
    
    
}