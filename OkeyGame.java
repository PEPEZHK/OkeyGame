import java.util.Arrays;
import java.util.Random;

public class OkeyGame {

    Player[] players;
    Tile[] tiles;

    Tile lastDiscardedTile;

    int currentPlayerIndex = 0;

    public OkeyGame() {
        players = new Player[4];
    }

    public void createTiles() {
        tiles = new Tile[104];
        int currentTile = 0;

        // two copies of each color-value combination, no jokers
        for (int i = 1; i <= 13; i++) {
            for (int j = 0; j < 2; j++) {
                tiles[currentTile++] = new Tile(i,'Y');
                tiles[currentTile++] = new Tile(i,'B');
                tiles[currentTile++] = new Tile(i,'R');
                tiles[currentTile++] = new Tile(i,'K');
            }
        }
    }

    /*
     * TODO: distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles
     * this method assumes the tiles are already sorted
     */
    public void distributeTilesToPlayers() {
        shuffleTiles();
        int j=0;
        for( int i=0 ; i<players.length; i++){
            int count=0;
            if(i==0){
                while(count!=15){
                players[i].addTile(tiles[j]);
                count++;
                j++;
                }
            }
            else{
                while(count!=14){
                players[i].addTile(tiles[j]);
                count++;
                j++;
                }
            }
        }
        for (int i = 0; i < 48; i++) {
            tiles[i] = tiles[i + 56];
        }
        tiles = Arrays.copyOf(tiles, 48);
    }

    /*
     * TODO: get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() {
         return lastDiscardedTile +"";
    }

    /*
     * TODO: get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() {
        currentPlayerIndex = getCurrentPlayerIndex();
        players[currentPlayerIndex].addTile(tiles[tiles.length-1]);
        Tile pickedTile = tiles[tiles.length-1];
        Tile[] newTiles = new Tile[tiles.length-1];
        System.arraycopy(tiles, 0, newTiles, 0, tiles.length-1);
        tiles = newTiles;
        return pickedTile + "";
    }

    /*
     * TODO: should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {
        Random rand = new Random();
		for (int i = 0; i < tiles.length; i++) 
        {
			int r = rand.nextInt(tiles.length);
			Tile temp = tiles[r];
			tiles[r] = tiles[i];
			tiles[i] = temp;
		}
    }

    /*
     * TODO: check if game still continues, should return true if current player
     * finished the game. Use calculateLongestChainPerTile method to get the
     * longest chains per tile.
     * To win, you need one of the following cases to be true:
     * - 8 tiles have length >= 4 and remaining six tiles have length >= 3 the last one can be of any length
     * - 5 tiles have length >= 5 and remaining nine tiles have length >= 3 the last one can be of any length
     * These are assuming we check for the win condition before discarding a tile
     * The given cases do not cover all the winning hands based on the original
     * game and for some rare cases it may be erroneous but it will be enough
     * for this simplified version
     */
    public boolean didGameFinish() {
	int counter3 = 0;
        int counter4 = 0;
        int counter5 = 0;
        int[] longestChains = new int[players[currentPlayerIndex].playerTiles.length];
        for (int i = 0; i < longestChains.length; i++)
        {
            if (longestChains[i] == 3)
            {
                counter3++;
            }
            else if (longestChains[i] == 4)
            {
                counter4++;
            }
            else if (longestChains[i] == 5)
            {
                counter5++;
            }
        }
        if(counter4 == 2 && counter3 == 2 || counter5 == 1 && counter3 == 3)
        {
            return true;
        }
        return false;
    }

    /*
     * TODO: Pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * You may choose randomly or consider if the discarded tile is useful for
     * the current status. Print whether computer picks from tiles or discarded ones.
     */
    public void pickTileForComputer() {
        Random rand = new Random();
        int r = rand.nextInt(2);
        switch (r) 
        {
            case 0: String topTile = getTopTile(); 
            int newFromTopValue = Integer.parseInt(topTile.substring(0, topTile.length() - 1));
            char newFromTopColor = topTile.charAt(topTile.length() - 1);
            Tile newTopTile = new Tile(newFromTopValue, newFromTopColor); 
            players[currentPlayerIndex].addTile(newTopTile);
            break;
            
            case 1: String discardedTile = getLastDiscardedTile(); 
            int newFromDiscardedValue = Integer.parseInt(discardedTile.substring(0, discardedTile.length() - 1));
            char newFromDiscardedColor = discardedTile.charAt(discardedTile.length() - 1);
            Tile newDiscardedTile = new Tile(newFromDiscardedValue, newFromDiscardedColor);
            players[currentPlayerIndex].addTile(newDiscardedTile);
        }
    }

    /*
     * TODO: Current computer player will discard the least useful tile.
     * For this use the findLongestChainOf method in Player class to calculate
     * the longest chain length per tile of this player,
     * then choose the tile with the lowest chain length and discard it
     * this method should print what tile is discarded since it should be
     * known by other players
     */
    public void discardTileForComputer() {
        int minChainVal = 1000;
        int indexOfUselessTile = 0;
        Player currentPlayer = players[currentPlayerIndex];


        for(int k=0; k< players[currentPlayerIndex].playerTiles.numberOfTiles; k++){
            if(currentPlayer.findLongestChainOf(currentPlayer.playerTiles[k]) < minChainVal){
                minChainVal = currentPlayer.findLongestChainOf(currentPlayer.playerTiles[k]);
                indexOfUselessTile = k;
            }
        }   
        currentPlayer.getAndRemoveTile(indexOfUselessTile);
        System.out.println("The discarded tile is :" + currentPlayer.playerTiles[indexOfUselessTile]);
    }

    /*
     * TODO: discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {
        lastDiscardedTile = players[currentPlayerIndex].getAndRemoveTile(tileIndex);
    }

    public void currentPlayerSortTilesColorFirst() {
        players[currentPlayerIndex].sortTilesColorFirst();
    }

    public void currentPlayerSortTilesValueFirst() {
        players[currentPlayerIndex].sortTilesValueFirst();
    }

    public void displayDiscardInformation() {
        if(lastDiscardedTile != null) {
            System.out.println("Last Discarded: " + lastDiscardedTile.toString());
        }
    }

    public void displayCurrentPlayersTiles() {
        players[currentPlayerIndex].displayTiles();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

      public String getCurrentPlayerName() {
        return players[currentPlayerIndex].getName();
    }

    public void passTurnToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    public void setPlayerName(int index, String name) {
        if(index >= 0 && index <= 3) {
            players[index] = new Player(name);
        }
    }

}
