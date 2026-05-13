package Interfaz;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.Producto;
import Modelo.Reserva;
import Modelo.Usuario;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;
import java.util.logging.Logger;

public class ReservaCard extends VBox {

    Logger log = Logger.getLogger(ReservaCard.class.getName());

    public ReservaCard(Reserva reserva) {

        setSpacing(8);
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(180);

        setStyle("""
                -fx-background-color: white;
                -fx-padding: 12;
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-border-color: #ddd;
                """);

        // Imagen
        ImageView imagen = new ImageView();
        imagen.setFitWidth(150);
        imagen.setFitHeight(100);
        imagen.setPreserveRatio(true);

        Image carga = new Image("file:src/resources/img/carga.png");
        imagen.setImage(carga);

        Producto producto = reserva != null ? reserva.getProducto() : null;
        Integer productoId = producto != null ? producto.getId() : null;

        if (productoId != null) {
            log.info("Cargando imagen para producto ID: " + productoId);
        }

        if (productoId != null) {
            new Thread(() -> {

                ApiResult<List<String>> urlsResult = ApiClient.obtenerUrlsImagenesPorProducto(productoId);
                List<String> urls = urlsResult.isOk() && urlsResult.getData() != null ? urlsResult.getData() : List.of();
                if (!urlsResult.isOk()) {
                    log.warning("No se pudieron obtener imagenes del producto " + productoId + ": "
                            + urlsResult.getTechnicalMessage());
                }

                if (urls != null && !urls.isEmpty()) {

                    String primeraUrl = urls.get(0);

                    Platform.runLater(() -> {

                        try {

                            Image img = new Image(primeraUrl, true);

                            img.exceptionProperty().addListener((obs, old, ex) -> {
                                if (ex != null) {
                                    log.severe("Error cargando imagen: " + ex.getMessage());
                                }
                            });

                            imagen.setImage(img);

                        } catch (Exception e) {
                            log.severe("Error cargando imagen: " + e.getMessage());
                        }
                    });
                }

            }).start();
        }

        // Nombre producto
        Label nombreProducto = new Label(producto != null && producto.getNombre() != null
                ? producto.getNombre()
                : "Sin producto");
        nombreProducto.setWrapText(true);
        nombreProducto.setStyle("""
                -fx-font-size: 14;
                -fx-font-weight: bold;
                """);

        // Estado reserva
        Label estado = new Label("Estado: " + (reserva != null && reserva.getEstado() != null
                ? reserva.getEstado()
                : "Sin estado"));

        estado.setStyle("""
                -fx-background-color: #e8e8e8;
                -fx-padding: 4 8;
                -fx-background-radius: 6;
                -fx-font-size: 11;
                """);

        // Fechas

        Label fechaInicio = new Label(
                "Inicio: " + (reserva != null && reserva.getFechaInicio() != null
                        ? reserva.getFechaInicio()
                        : "Sin fecha"));

        Label fechaFin = new Label(
                "Fin: " + (reserva != null && reserva.getFechaFin() != null
                        ? reserva.getFechaFin()
                        : "Sin fecha"));

        // Precio
        String precioTexto = producto != null ? producto.getPrecioPorDia() + "€/día" : "Precio no disponible";
        Label precio = new Label(
				precioTexto);

        precio.setStyle("""
                -fx-font-weight: bold;
                -fx-font-size: 13;
                """);

        // Usuario
        Usuario usuarioReserva = reserva != null ? reserva.getUsuario() : null;
        Label usuario = new Label(
				"Usuario: " + (usuarioReserva != null && usuarioReserva.getNombre() != null
						? usuarioReserva.getNombre()
						: "Sin usuario"));

        VBox infoBox = new VBox(
                5,
                estado,
                usuario,
                fechaInicio,
                fechaFin,
                precio
        );

        infoBox.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(
                imagen,
                nombreProducto,
                infoBox
        );

        this.setOnMouseClicked(e -> {
            if (reserva != null) {
                SceneManager.mostrarDetalleReserva(reserva);
            }
        });
    }
}
