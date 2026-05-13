package Interfaz;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

import Controller.ApiClient;
import Controller.ApiResult;
import javafx.application.Platform;
import javafx.scene.image.Image;

public final class ImageLoader {

	private static final Logger log = Logger.getLogger(ImageLoader.class.getName());
	private static final Map<Integer, Image> imageCache = new ConcurrentHashMap<>();
	private static final Map<Integer, String> firstUrlCache = new ConcurrentHashMap<>();

	private ImageLoader() {
	}

	public static void loadProductImage(Integer productoId, Consumer<Image> onLoaded) {
		if (productoId == null || onLoaded == null) {
			return;
		}

		Image cachedImage = imageCache.get(productoId);
		if (cachedImage != null) {
			Platform.runLater(() -> onLoaded.accept(cachedImage));
			return;
		}

		AsyncExecutor.io().submit(() -> {
			String primeraUrl = firstUrlCache.get(productoId);
			if (primeraUrl == null) {
				ApiResult<List<String>> urlsResult = ApiClient.obtenerUrlsImagenesPorProducto(productoId);
				List<String> urls = urlsResult.isOk() && urlsResult.getData() != null ? urlsResult.getData() : List.of();
				if (!urlsResult.isOk()) {
					log.warning("No se pudieron obtener imagenes del producto " + productoId + ": "
							+ urlsResult.getTechnicalMessage());
					return;
				}
				if (urls.isEmpty()) {
					return;
				}
				primeraUrl = urls.get(0);
				firstUrlCache.put(productoId, primeraUrl);
			}

			try {
				Image image = new Image(primeraUrl, true);
				image.exceptionProperty().addListener((obs, old, ex) -> {
					if (ex != null) {
						log.warning("Error cargando imagen del producto " + productoId + ": " + ex.getMessage());
					}
				});
				imageCache.put(productoId, image);
				Platform.runLater(() -> onLoaded.accept(image));
			} catch (Exception e) {
				log.warning("Error inesperado cargando imagen del producto " + productoId + ": " + e.getMessage());
			}
		});
	}
}
