package Interfaz;

import Modelo.Producto;
import Modelo.Reserva;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProductosView extends VBox {

	private FlowPane flowOfertado = new FlowPane();
	private FlowPane flowEnCurso = new FlowPane();

	public ProductosView() {

		setSpacing(20);
		setPadding(new Insets(20));

		flowOfertado.setHgap(20);
		flowOfertado.setVgap(20);

		flowEnCurso.setHgap(20);
		flowEnCurso.setVgap(20);

		VBox ofertadoBox = new VBox(10, new Label("OFERTADO"), flowOfertado);

		VBox alquiladoBox = new VBox(10, new Label("EN CURSO"), flowEnCurso);

		getChildren().addAll(ofertadoBox, alquiladoBox);
	}

	public void cargarArticulos(List<Producto> productos, List<Reserva> reservas) {

		flowOfertado.getChildren().clear();
		flowEnCurso.getChildren().clear();

		for (Producto p : productos) {
			ProductoCard card = new ProductoCard(p);
			if (p.getStock() > 0) {
				flowOfertado.getChildren().add(card);
			}
		}
		
		for (Reserva r : reservas) {
            if (r.getEstado().equalsIgnoreCase("EN_CURSO")) {
                ReservaCard card = new ReservaCard(r);
                flowEnCurso.getChildren().add(card);
            }
        }

	}
}