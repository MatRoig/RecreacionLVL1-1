package comMatRoig.Level1_1;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class World {

    private TiledMapTileLayer collisionLayer;
    private int tileWidth;
    private int tileHeight;
    private int mapHeightTiles;
    private int mapHeightPixels;

    public World(TiledMap map) {
        collisionLayer = (TiledMapTileLayer) map.getLayers().get(0);
        tileWidth = (int) collisionLayer.getTileWidth();
        tileHeight = (int) collisionLayer.getTileHeight();
        mapHeightTiles = collisionLayer.getHeight();
        mapHeightPixels = mapHeightTiles * tileHeight;
    }

    /**
     * Convierte Y de LibGDX a fila de Tiled
     */
    private int worldYToTileY(float worldY) {
        int tileY = (int) ((mapHeightPixels - worldY) / tileHeight);
        if (tileY < 0) tileY = 0;
        if (tileY >= mapHeightTiles) tileY = mapHeightTiles - 1;
        return tileY;
    }

    /**
     * Verifica si hay tile sólido en (worldX, worldY)
     */
    public boolean isSolid(float worldX, float worldY) {
        int tileX = (int) (worldX / tileWidth);
        int tileY = worldYToTileY(worldY);

        if (tileX < 0 || tileX >= collisionLayer.getWidth()) {
            return false;
        }

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        return cell != null && cell.getTile() != null;
    }

    /**
     * Devuelve la altura Y de la parte superior del tile en la posición x,y
     * Si no hay tile, devuelve -1
     */
    public float getTileTopY(float worldX, float worldY) {
        int tileX = (int) (worldX / tileWidth);
        int tileY = worldYToTileY(worldY);

        if (tileX < 0 || tileX >= collisionLayer.getWidth()) {
            return -1;
        }

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        if (cell != null && cell.getTile() != null) {
            // Convertir de vuelta a LibGDX: parte superior del tile
            return (mapHeightTiles - 1 - tileY) * tileHeight + tileHeight;
        }

        return -1;
    }
}
