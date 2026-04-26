package comMatRoig.Level1_1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

    // ========== ATRIBUTOS DE ANIMACIÓN ==========
    /**
     * La imagen de Mario cargada en memoria.
     * CONCEPTO: Texture
     * Es una imagen (PNG, JPG) cargada en la memoria de video (GPU).
     * Dibujar desde la GPU es MUCHO más rápido que desde el disco duro.
     */
    // El sprite sheet completo (imagen de 64x16 con 4 frames)
    private Texture spriteSheet;

    // Array que guarda cada frame como un recorte (TextureRegion)
    private TextureRegion[] frames;

    // Índice del frame actual que se está mostrando (0, 1, 2 o 3)
    private int currentFrame = 0;

    // Temporizador para saber cuándo cambiar de frame
    private float animationTimer = 0;

    // Duración de cada frame en segundos (0.15 = 150 milisegundos)
    // A 60 FPS, cada frame dura ~9 frames de juego
    private static final float FRAME_DURATION = 0.15f;

    // ========== CONSTANTES ==========
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    private static final float GRAVITY = -500f;
    private static final float JUMP_FORCE = 250f;

    // ========== ESTADOS DE ANIMACIÓN ==========
    // Enum interno para saber qué animación está activa
    private enum AnimationState {
        IDLE,       // Quieto (usaremos frame 0)
        WALKING,    // Caminando → frames 1, 2, 3 ciclando
        SKIDDING,   // Frenando (frame 2)
        JUMPING,    // Saltando (frame 3)
        DEAD        // Muriendo (frame 3 o especial)
    }
    private AnimationState currentState = AnimationState.IDLE;

    // Dirección: 1 = derecha, -1 = izquierda
    private int facingDirection = 1;

    // ========== CONSTRUCTOR ==========

    public Mario(float startX, float startY) {
        this.x = startX;
        this.y = startY;

        // Cargar el sprite sheet
        spriteSheet = new Texture("mario_anim.png");

        // Crear los 4 frames recortando la imagen
        // new TextureRegion(textura, x, y, ancho, alto)
        // x se mueve de 0 en 0: 0, 16, 32, 48
        frames = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            frames[i] = new TextureRegion(spriteSheet, i * 16, 0, 16, 16);
        }
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
            facingDirection = -1;  // Mira a la izquierda
        }
        if (right) {
            velocityX = speed;   // Positivo = derecha
            facingDirection = 1;   // Mira a la derecha
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
            currentState = AnimationState.JUMPING;
        }

        // ===== APLICAR MOVIMIENTO VERTICAL =====
        y += velocityY * deltaTime;

        // ===== COLISIÓN CON EL SUELO (actualizado) =====
        // Primero usamos un suelo fijo en y=80
        // actualizamos con lo siguiente

        float groundY = 272;  // Suelo más arriba, sobre los ladrillos

        if (y <= groundY) {
            y = groundY;       // Colocar exactamente en el suelo
            velocityY = 0;      // Detener caída

            // Si acaba de aterrizar, cambiar estado
            if (!onGround) {
                onGround = true;
                currentState = AnimationState.IDLE;
            }
        }

        // ===== ACTUALIZAR ESTADO DE ANIMACIÓN =====
        updateAnimationState(velocityX);

        // ===== ACTUALIZAR FRAME DE ANIMACIÓN =====
        updateAnimation(deltaTime);

    }
    /**
     * Decide qué animación mostrar según el movimiento.
     */
    private void updateAnimationState(float velocityX) {
        if (!onGround) {
            // Está en el aire → saltando
            currentState = AnimationState.JUMPING;
        } else if (velocityX != 0) {
            // Detectar si está cambiando de dirección (frenando)
            // Si se mueve a la derecha pero mira a la izquierda, o viceversa
            if ((velocityX > 0 && facingDirection == -1) ||
                (velocityX < 0 && facingDirection == 1)) {
                currentState = AnimationState.SKIDDING;
            } else {
                // Se está moviendo → caminando
                currentState = AnimationState.WALKING;
            }
        } else {
            // No se mueve → quieto
            currentState = AnimationState.IDLE;
        }
    }

    /**
     * Cambia el frame actual según el estado y el tiempo transcurrido.
     */
    private void updateAnimation(float deltaTime) {
        animationTimer += deltaTime;

        // Solo cambiar de frame si pasó el tiempo suficiente
        if (animationTimer >= FRAME_DURATION) {
            animationTimer = 0;  // Reiniciar temporizador

            switch (currentState) {
                case IDLE:
                    currentFrame = 0;  // Siempre frame 0 (quieto)
                    break;

                case WALKING:
                    // Ciclar entre frames 1, 2, 3
                    // Si está en 1 → 2, si está en 2 → 3, si está en 3 → 1
                    if (currentFrame < 1 || currentFrame > 3) {
                        currentFrame = 1;  // Empezar desde WALK1
                    } else {
                        currentFrame++;
                        if (currentFrame > 3) {
                            currentFrame = 1;  // Volver a WALK1
                        }
                    }
                    break;

                case SKIDDING:
                    currentFrame = 4;  // Frame de frenado
                    break;

                case JUMPING:
                    currentFrame = 5;  // Frame de salto
                    break;

                case DEAD:
                    currentFrame = 6;  // Por ahora mismo que salto
                    break;
            }
        }
    }

    // ========== DRAW ==========
    /**
     * draw() dibuja a Mario en pantalla.
     *
     * @param batch El "pintor" de LibGDX que dibuja imágenes 2D
     */

    public void draw(SpriteBatch batch) {
        // CONCEPTO: Dibujar volteado horizontalmente
        // Si facingDirection es -1 (izquierda), volteamos el sprite
        // Parámetros: region, x, y, origenX, origenY, ancho, alto, escalaX, escalaY, rotación

        TextureRegion frame = frames[currentFrame];

        if (facingDirection == 1) {
            // Mira a la derecha: dibujar normal
            batch.draw(frame, x, y, WIDTH, HEIGHT);
        } else {
            // Mira a la izquierda: voltear horizontalmente
            // El truco: dibujamos con ancho negativo (-WIDTH)
            // y movemos x para compensar
            batch.draw(frame, x + WIDTH, y, -WIDTH, HEIGHT);
        }
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

    // ========== DISPOSE ==========
    /**
     * Libera la memoria de la textura cuando ya no la necesitamos.
     * Es buena práctica para no dejar "basura" en la GPU.
     */
    public void dispose() {
        spriteSheet.dispose();
    }
}
