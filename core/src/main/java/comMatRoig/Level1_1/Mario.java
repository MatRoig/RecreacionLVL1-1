package comMatRoig.Level1_1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Clase que representa al personaje Mario.
 *
 * CONCEPTO: Encapsulamiento
 * Los atributos son "private" (privados). Esto significa que solo esta clase
 * puede modificarlos directamente. Otras clases deben usar métodos "getter"
 * para leerlos. Es como tener una caja fuerte: tú tienes la llave, otros
 * deben pedirte permiso para ver lo que hay dentro.
 */
public class Mario {

    // ========== ATRIBUTOS (campos/variables de instancia) ==========
    // Son las "características" de Mario. Cada objeto Mario tiene sus propios valores.

    /**
     * Posición horizontal en el mundo del juego (en píxeles).
     * Usamos float (número con decimales) porque el movimiento puede ser suave:
     * Mario puede estar en x=50.5, x=100.3, etc.
     *
     * CONCEPTO: float vs int
     * int = números enteros (1, 2, 3, -5, 100)
     * float = números con decimales (1.5, 3.14, -0.5)
     * Usamos float para movimiento fluido entre píxeles.
     */
    private float x;
    private float y;

    /**
     * Velocidad de movimiento horizontal (píxeles por segundo).
     * Si speed = 100, y el juego corre a 60 FPS:
     * En cada frame avanza 100 * 0.016 = 1.6 píxeles aprox.
     */
    private float speed = 100f;

    /**
     * Velocidad vertical (para saltos y caídas).
     * Positiva = sube, negativa = cae, 0 = quieto verticalmente.
     */
    private float velocityY = 0;

    /**
     * ¿Está Mario tocando el suelo?
     * true = puede saltar
     * false = está en el aire (caída o salto)
     */
    private boolean onGround = false;

    /**
     * La imagen de Mario cargada en memoria.
     * CONCEPTO: Texture
     * Es una imagen (PNG, JPG) cargada en la memoria de video (GPU).
     * Dibujar desde la GPU es MUCHO más rápido que desde el disco duro.
     */
    private Texture texture;

    // Tamaño del sprite de Mario (16x16 píxeles, tamaño original NES)
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    /**
     * Gravedad: aceleración constante hacia abajo.
     * Valor negativo porque en pantalla Y aumenta hacia abajo.
     * -500 significa que cada segundo, la velocidad vertical disminuye 500 unidades.
     */
    private static final float GRAVITY = -500f;

    /**
     * Fuerza del salto: impulso inicial hacia arriba.
     * Valor positivo porque subir es contra la gravedad.
     */
    private static final float JUMP_FORCE = 250f;

    // ========== CONSTRUCTOR ==========

    /**
     * El constructor se ejecuta cuando creamos un objeto con "new".
     * Ejemplo: new Mario(50, 80) llama a este método.
     *
     * @param startX Posición inicial horizontal
     * @param startY Posición inicial vertical
     */
    public Mario(float startX, float startY) {
        // "this.x" se refiere al atributo de la clase
        // "startX" es el parámetro recibido
        // Los distinguimos porque tienen nombres diferentes
        this.x = startX;
        this.y = startY;

        // Cargar la imagen desde la carpeta assets/
        // Gdx.files.internal busca automáticamente en assets/
        texture = new Texture("mario.png");
    }

    // ========== MÉTODOS (acciones que Mario puede hacer) ==========

    /**
     * update() se ejecuta 60 veces por segundo (cada frame).
     * Aquí está toda la lógica: movimiento, gravedad, colisiones.
     *
     * @param deltaTime Tiempo transcurrido desde el último frame (en segundos)
     * @param world El mundo del juego (para colisiones con el tilemap)
     */
    public void update(float deltaTime) {

        // ===== MOVIMIENTO HORIZONTAL =====

        // Leer teclado: ¿está presionada la flecha izquierda?
        // CONCEPTO: Gdx.input
        // Gdx es la clase principal de LibGDX. "input" maneja teclado, ratón, etc.
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);

        // ¿Está presionada la flecha derecha?
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        // Calcular velocidad horizontal
        float velocityX = 0;

        if (left) {
            velocityX = -speed;  // Negativo = izquierda
        }
        if (right) {
            velocityX = speed;   // Positivo = derecha
        }

        // Aplicar movimiento horizontal
        // Fórmula física: posición = posición + (velocidad × tiempo)
        // Ejemplo: si x=50, velocityX=100, deltaTime=0.016:
        // x = 50 + (100 × 0.016) = 50 + 1.6 = 51.6
        x += velocityX * deltaTime;

        // LÍMITE: No puede ir más atrás del borde izquierdo
        // En el juego original, Mario nunca puede retroceder más allá del borde de la pantalla
        if (x < 0) {
            x = 0;
        }

        // ===== GRAVEDAD =====

        // Aplicar gravedad: la velocidad vertical disminuye constantemente
        // Ejemplo: si velocityY=0, GRAVITY=-500, deltaTime=0.016:
        // velocityY = 0 + (-500 × 0.016) = -8
        // En el siguiente frame será más negativo, entonces cae más rápido
        velocityY += GRAVITY * deltaTime;

        // ===== SALTO =====

        // CONCEPTO: isKeyJustPressed vs isKeyPressed
        // isKeyPressed: true TODO el tiempo que mantengas la tecla
        // isKeyJustPressed: true UNA SOLA VEZ al pulsar la tecla
        //
        // ¿Por qué usar JustPressed para saltar?
        // Si usáramos isKeyPressed, Mario "volaría" mientras mantengas espacio presionado.
        // Con JustPressed, salta una vez por cada pulsación.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            velocityY = JUMP_FORCE;  // Impulso hacia arriba
            onGround = false;        // Ya no está en el suelo
        }

        // Aplicar movimiento vertical
        y += velocityY * deltaTime;

        // ===== COLISIÓN CON EL SUELO (versión simple) =====
        // Por ahora usamos un suelo fijo en y=80
        // Más adelante lo reemplazaremos con colisiones reales del tilemap

        float groundY = 272;  // Suelo más arriba, sobre los ladrillos

        if (y <= groundY) {
            y = groundY;       // Colocar exactamente en el suelo
            velocityY = 0;      // Detener caída
            onGround = true;    // Ahora puede volver a saltar
        }
    }

    /**
     * draw() dibuja a Mario en pantalla.
     *
     * @param batch El "pintor" de LibGDX que dibuja imágenes 2D
     */
    public void draw(SpriteBatch batch) {
        // Dibujar la textura en la posición (x, y)
        // Parámetros: imagen, posiciónX, posiciónY, ancho, alto
        batch.draw(texture, x, y, WIDTH, HEIGHT);
    }

    // ========== GETTERS (métodos para leer atributos privados) ==========

    /**
     * Devuelve la posición X de Mario.
     * La cámara usará esto para seguirlo.
     */
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    /**
     * Libera la memoria de la textura cuando ya no la necesitamos.
     * Es buena práctica para no dejar "basura" en la GPU.
     */
    public void dispose() {
        texture.dispose();
    }
}
