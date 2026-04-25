package comMatRoig.Level1_1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MarioBros1_1 extends ApplicationAdapter {
    // El "pintor" que dibuja todo
    private SpriteBatch batch;

    // La cámara: define qué parte del mundo vemos
    private OrthographicCamera camera;

    // El mapa cargado desde tu archivo .tmx
    private TiledMap map;

    // El "renderizador" que sabe dibujar el mapa
    private OrthogonalTiledMapRenderer mapRenderer;

    // ========== MÉTODOS DEL CICLO DE VIDA ==========

    @Override
    public void create() {
        // 1. Inicializar el pintor
        batch = new SpriteBatch();

        // 2. Crear la cámara
        // Parámetros: ancho_visible, alto_visible
        // 256x240 = resolución original del NES
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 640, 480);

        // Posicionar la cámara para ver el inicio del nivel
        // El suelo está en la parte inferior, subimos la cámara un poco
        camera.position.set(320, 240, 0);  // Centro de la ventana
        camera.update();

        // 3. Cargar el mapa desde assets/
        // Gdx.files.internal busca en la carpeta assets
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("nivel1-1.tmx");

        // 4. Crear el renderizador del mapa
        // 1/16f = escala (1 tile = 16 unidades del mundo)
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
    }

    @Override
    public void render() {
        // 1. Limpiar pantalla (color cielo azul del Mario original)
        // RGB: 92, 148, 252 (azul cielo característico)
        ScreenUtils.clear(92/255f, 148/255f, 252/255f, 1);

        // 2. Actualizar la cámara
        camera.update();

        // 3. Dibujar el mapa
        // Le decimos al renderizador qué cámara usar
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    @Override
    public void dispose() {
        // Liberar memoria (buena práctica)
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
    }
}
