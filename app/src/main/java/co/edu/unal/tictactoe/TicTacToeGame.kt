package co.edu.unal.tictactoe

// Esta clase contiene la lógica del juego Tic-Tac-Toe.
// Aquí controlaremos el tablero, los jugadores y las jugadas.
class TicTacToeGame {

    // Constantes que representan el contenido de cada casilla.
    // EMPTY significa que la casilla todavía está libre.
    companion object {
        const val EMPTY = 0
        const val CROSS = 1
        const val NOUGHT = 2
    }

    // El tablero tiene 9 posiciones:
    //
    //  0 | 1 | 2
    // ---+---+---
    //  3 | 4 | 5
    // ---+---+---
    //  6 | 7 | 8
    //
    // Al comenzar todas las posiciones están vacías.
    private val board = IntArray(9) { EMPTY }

    // El primer jugador será X.
    private var currentPlayer = CROSS

    // Devuelve qué jugador tiene el turno actualmente.
    fun getCurrentPlayer(): Int {
        return currentPlayer
    }

    // Permite realizar una jugada en una posición del tablero.
    fun makeMove(position: Int): Boolean {

        // Comprobamos que la posición exista.
        if (position !in 0..8) {
            return false
        }

        // Si la casilla ya está ocupada, no permitimos la jugada.
        if (board[position] != EMPTY) {
            return false
        }

        // Guardamos la jugada del jugador actual.
        board[position] = currentPlayer

        // Cambiamos el turno.
        currentPlayer =
            if (currentPlayer == CROSS) NOUGHT
            else CROSS

        return true
    }

    // Permite consultar qué hay en una posición del tablero.
    fun getCell(position: Int): Int {
        return board[position]
    }

    // Reinicia completamente el tablero.
// Todas las posiciones vuelven a quedar vacías
// y X vuelve a ser el primer jugador.
    fun resetGame() {

        for (i in board.indices) {
            board[i] = EMPTY
        }

        currentPlayer = CROSS
    }


    // Comprueba si existe un ganador o un empate.
//
// Devuelve:
// 0 = la partida continúa
// 1 = ganó X
// 2 = ganó O
// 3 = empate
    fun checkWinner(): Int {

        // Todas las combinaciones posibles para ganar.
        val winningLines = arrayOf(

            // Filas
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),

            // Columnas
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),

            // Diagonales
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        // Recorremos cada combinación ganadora.
        for (line in winningLines) {

            val a = line[0]
            val b = line[1]
            val c = line[2]

            // Si las tres posiciones tienen el mismo símbolo
            // y no están vacías, tenemos un ganador.
            if (
                board[a] != EMPTY &&
                board[a] == board[b] &&
                board[b] == board[c]
            ) {

                return if (board[a] == CROSS) {
                    1
                } else {
                    2
                }
            }
        }

        // Si todavía queda alguna casilla vacía,
        // la partida continúa.
        if (board.any { it == EMPTY }) {
            return 0
        }

        // Si no hay ganador ni espacios vacíos,
        // significa que hubo empate.
        return 3
    }

    // Devuelve la mejor posición para que juegue el computador.
//
// Estrategia:
// 1. Si el computador puede ganar, juega allí.
// 2. Si el jugador humano puede ganar en el siguiente turno,
//    bloquea esa posición.
// 3. Si ninguna de las anteriores aplica,
//    elige una casilla libre al azar.
    fun getComputerMove(): Int {

        // --------------------------------------------------
        // 1. INTENTAR GANAR
        // --------------------------------------------------

        for (i in board.indices) {

            // Solo probamos posiciones vacías.
            if (board[i] == EMPTY) {

                // Simulamos que el computador juega aquí.
                board[i] = NOUGHT

                // Revisamos si esa jugada produce una victoria.
                if (checkWinner() == 2) {

                    // Restauramos la casilla antes de devolver la posición.
                    board[i] = EMPTY

                    return i
                }

                // Si no ganó, devolvemos la casilla a vacío.
                board[i] = EMPTY
            }
        }


        // --------------------------------------------------
        // 2. BLOQUEAR AL HUMANO
        // --------------------------------------------------

        for (i in board.indices) {

            if (board[i] == EMPTY) {

                // Simulamos que el humano juega aquí.
                board[i] = CROSS

                // Si X ganaría en esa posición,
                // el computador debe bloquearla.
                if (checkWinner() == 1) {

                    board[i] = EMPTY

                    return i
                }

                board[i] = EMPTY
            }
        }


        // --------------------------------------------------
        // 3. MOVIMIENTO ALEATORIO
        // --------------------------------------------------

        // Creamos una lista con todas las posiciones libres.
        val availableMoves = board.indices.filter {
            board[it] == EMPTY
        }

        // Si todavía existen posiciones libres,
        // elegimos una al azar.
        return if (availableMoves.isNotEmpty()) {

            availableMoves.random()

        } else {

            // Si no hay posiciones libres,
            // devolvemos -1.
            -1
        }
    }
}