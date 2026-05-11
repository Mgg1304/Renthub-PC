package Interfaz;

import Controller.ApiClient;
import Modelo.Producto;
import Modelo.Reserva;
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

        log.info("Cargando imagen para producto ID: " + reserva.getProducto().getId());

        new Thread(() -> {

            List<String> urls = ApiClient.obtenerUrlsImagenesPorProducto(reserva.getProducto().getId());

            if (!urls.isEmpty()) {

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

        // Nombre producto
        Label nombreProducto = new Label(reserva.getProducto().getNombre());
        nombreProducto.setWrapText(true);
        nombreProducto.setStyle("""
                -fx-font-size: 14;
                -fx-font-weight: bold;
                """);

        // Estado reserva
        Label estado = new Label("Estado: " + reserva.getEstado());

        estado.setStyle("""
                -fx-background-color: #e8e8e8;
                -fx-padding: 4 8;
                -fx-background-radius: 6;
                -fx-font-size: 11;
                """);

        // Fechas

        Label fechaInicio = new Label(
                "Inicio: " + reserva.getFechaInicio());

        Label fechaFin = new Label(
                "Fin: " + reserva.getFechaFin());

        // Precio
        Label precio = new Label(
        		reserva.getProducto().getPrecioPorDia() + "€/día");

        precio.setStyle("""
                -fx-font-weight: bold;
                -fx-font-size: 13;
                """);

        // Usuario
        Label usuario = new Label(
                "Usuario: " + reserva.getUsuario().getNombre());

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
            SceneManager.mostrarDetalleReserva(reserva);
        });
    }
}
