package co.edu.unal.tictactoe

class TicTacToeGame {

    companion object {
        const val EMPTY = 0
        const val CROSS = 1
        const val NOUGHT = 2
    }

    // --------------------------------------------------
    // NIVELES DE DIFICULTAD
    // --------------------------------------------------

    enum class DifficultyLevel {
        EASY,
        HARDER,
        EXPERT
    }

    // Por defecto, el computador inicia en Expert.
    private var difficultyLevel = DifficultyLevel.EXPERT

    fun getDifficultyLevel(): DifficultyLevel {
        return difficultyLevel
    }

    fun setDifficultyLevel(level: DifficultyLevel) {
        difficultyLevel = level
    }

    // --------------------------------------------------
    // TABLERO
    // --------------------------------------------------

    private val board = IntArray(9) { EMPTY }

    private var currentPlayer = CROSS

    fun getCurrentPlayer(): Int {
        return currentPlayer
    }

    fun makeMove(position: Int): Boolean {

        if (position !in 0..8) {
            return false
        }

        if (board[position] != EMPTY) {
            return false
        }

        board[position] = currentPlayer

        currentPlayer =
            if (currentPlayer == CROSS) NOUGHT
            else CROSS

        return true
    }

    fun getCell(position: Int): Int {
        return board[position]
    }

    // --------------------------------------------------
    // REINICIAR PARTIDA
    // --------------------------------------------------

    fun resetGame() {

        for (i in board.indices) {
            board[i] = EMPTY
        }

        currentPlayer = CROSS
    }

    // --------------------------------------------------
    // COMPROBAR GANADOR
    // --------------------------------------------------

    // Devuelve:
    // 0 = partida continúa
    // 1 = ganó X
    // 2 = ganó O
    // 3 = empate

    fun checkWinner(): Int {

        val winningLines = arrayOf(

            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),

            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),

            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        for (line in winningLines) {

            val a = line[0]
            val b = line[1]
            val c = line[2]

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

        if (board.any { it == EMPTY }) {
            return 0
        }

        return 3
    }

    // --------------------------------------------------
    // MOVIMIENTO DEL COMPUTADOR
    // --------------------------------------------------

    fun getComputerMove(): Int {

        return when (difficultyLevel) {

            // EASY:
            // El computador siempre juega al azar.
            DifficultyLevel.EASY -> {
                getRandomMove()
            }

            // HARDER:
            // Intenta ganar.
            // Si no puede, juega al azar.
            DifficultyLevel.HARDER -> {

                val winningMove = getWinningMove()

                if (winningMove != -1) {
                    winningMove
                } else {
                    getRandomMove()
                }
            }

            // EXPERT:
            // Intenta ganar.
            // Si no puede, bloquea al humano.
            // Si tampoco necesita bloquear, juega al azar.
            DifficultyLevel.EXPERT -> {

                val winningMove = getWinningMove()

                if (winningMove != -1) {
                    winningMove
                } else {

                    val blockingMove = getBlockingMove()

                    if (blockingMove != -1) {
                        blockingMove
                    } else {
                        getRandomMove()
                    }
                }
            }
        }
    }

    // --------------------------------------------------
    // INTENTAR GANAR
    // --------------------------------------------------

    private fun getWinningMove(): Int {

        for (i in board.indices) {

            if (board[i] == EMPTY) {

                // Simulamos una jugada de O.
                board[i] = NOUGHT

                if (checkWinner() == 2) {

                    // Restauramos el tablero.
                    board[i] = EMPTY

                    return i
                }

                board[i] = EMPTY
            }
        }

        return -1
    }

    // --------------------------------------------------
    // BLOQUEAR AL HUMANO
    // --------------------------------------------------

    private fun getBlockingMove(): Int {

        for (i in board.indices) {

            if (board[i] == EMPTY) {

                // Simulamos una jugada de X.
                board[i] = CROSS

                if (checkWinner() == 1) {

                    // Restauramos el tablero.
                    board[i] = EMPTY

                    return i
                }

                board[i] = EMPTY
            }
        }

        return -1
    }

    // --------------------------------------------------
    // MOVIMIENTO ALEATORIO
    // --------------------------------------------------

    private fun getRandomMove(): Int {

        val availableMoves = board.indices.filter {
            board[it] == EMPTY
        }

        return if (availableMoves.isNotEmpty()) {
            availableMoves.random()
        } else {
            -1
        }
    }
}