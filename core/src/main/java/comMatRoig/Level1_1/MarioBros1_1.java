package comMatRoig.Level1_1;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class MarioBros1_1 extends ApplicationAdapter {

    // Pintor que dibuja todo en pantalla
    private SpriteBatch batch;

    // Cámara que sigue a Mario
    private OrthographicCamera camera;

    // El mapa visual desde Tiled
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // NUESTRO PERSONAJE
    // Declaramos una variable de tipo Mario, pero aún no creamos el objeto
    private Mario mario;

    // El mundo (tilemap para colisiones)
    // private World world; (Agregar despues)

    @Override
    public void create() {
        // 1. Inicializar pintor
        batch = new SpriteBatch();

        // 2. Crear cámara
        // 256x240 = resolución original del NES
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 256, 240);

        // 3. Crear el mundo (nuestro tilemap lógico)
       //  world = new World(); (Agregar despues)

        // 4. Cargar mapa visual desde Tiled
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("nivel1-1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // 5. CREAR A MARIO
        // new Mario(50, 80) llama al constructor
        // Posición inicial: x=50 (un poco a la derecha del borde)
        //                  y=80 (sobre el suelo)
        //CORRECCION:
        // Después (más arriba, donde está el suelo real):
        mario = new Mario(50, 272);
    }

    @Override
    public void render() {
        // CONCEPTO: deltaTime
        // Gdx.graphics.getDeltaTime() devuelve el tiempo desde el último frame
        // Ejemplo a 60 FPS: ~0.0167 segundos (1/60)
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Actualizar lógica de Mario (movimiento, gravedad, colisiones)
        mario.update(deltaTime);

        // ===== ACTUALIZAR CÁMARA PARA SEGUIR A MARIO =====

        // La cámara se posiciona en la X de Mario
        float camX = mario.getX();

        // PERO con un límite: no puede mostrar "nada" a la izquierda
        // La mitad del ancho de la cámara es el mínimo (128 píxeles)
        float minCamX = 256 / 2f;  // 128

        if (camX < minCamX) {
            camX = minCamX;  // Fijar en el borde izquierdo inicial
        }

        // Después de calcular camX, limitamos también la Y de la cámara
        float camY = 360;  // Altura ideal para ver suelo + cielo

        // Límite inferior: la cámara no puede bajar de cierto punto
        // para no mostrar la zona subterránea
        float minCamY = 80;  // Mínimo para que no se vea lo de abajo

        if (camY < minCamY) {
            camY = minCamY;
        }

        camera.position.set(camX, camY, 0);

        // Aplicar posición a la cámara
        // Y queda fija en el centro vertical (120 = 240/2)
        // CORRECCIOON:
        // La altura del mapa en Tiled es de 15 tiles × 16px = 240px
        // Pero el suelo está en la parte superior del tilemap en Tiled
        // Necesitamos que la cámara mire hacia arriba donde está el contenido
        camera.position.set(camX, 360, 0);
        camera.update();

        // Limpiar pantalla con color azul cielo del Mario original
        ScreenUtils.clear(92 / 255f, 148 / 255f, 252 / 255f, 1);

        // ===== DIBUJAR =====

        // 1. Dibujar el mapa (fondo, suelo, tuberías)
        // setView le dice al renderizador "usa esta cámara"
        mapRenderer.setView(camera);
        mapRenderer.render();

        // 2. Dibujar a Mario
        // CONCEPTO: batch.begin() y batch.end()
        // begin() = "empieza a pintar"
        // end() = "termina y muestra todo en pantalla"
        // Todo entre begin() y end() se dibuja en ese orden

        // setProjectionMatrix: le dice al pintor "usa la misma cámara"
        // para que Mario aparezca en la posición correcta del mundo
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mario.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        // Liberar memoria (buena práctica)
        batch.dispose();
        map.dispose();
        mapRenderer.dispose();
        mario.dispose();
    }
}
